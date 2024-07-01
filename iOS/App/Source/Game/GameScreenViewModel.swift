//
//  GameScreenViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 26/8/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import AthleticAnalytics
import AthleticApolloTypes
import AthleticComments
import AthleticFoundation
import AthleticGameSchedules
import AthleticNavigation
import AthleticScoresFoundation
import AthleticUI
import Combine
import Foundation
import Nuke

final class GameScreenViewModel: ObservableObject {

    @Published private(set) var header: GameLargeScoreHeaderViewModel?
    @Published private(set) var state: LoadingState = .initial
    @Published private(set) var pagingViewModel: GamePagingViewModel?
    @Published private(set) var hasLiveActivityStarted = false

    var leagueString: String? {
        leagueCode?.rawValue
    }

    private(set) var shareUrl: URL?
    private var initialTabSelectionOverride: BoxScoreDestination.Tab?

    private var leagueCode: GQL.LeagueCode?
    private let gameId: String
    private let compass: Compass
    private let entitlement: Entitlement
    private let network: BoxScoreNetworking & CommentsNetworking
    private var scheduledGame: ScheduledGame?
    private var boxScoreSections: [GQL.BoxScoreContent.Section] = []
    private var liveUpdatesSubscription: AnyCancellable?
    private var cancellables = Cancellables()
    private lazy var logger = ATHLogger(category: .scores)
    private var teamId: String? = nil
    private var liveBlog: GQL.GameContainer.LiveBlog?

    private var isLiveActivityEnabled: Bool {
        compass.config.flags.isLiveActivitiesEnabled
    }

    init(
        leagueCode: GQL.LeagueCode?,
        gameId: String,
        scheduledGame: ScheduledGame?,
        initialTabSelectionOverride: BoxScoreDestination.Tab? = nil,
        compass: Compass = AppEnvironment.shared.compass,
        entitlement: Entitlement = AppEnvironment.shared.entitlement,
        network: BoxScoreNetworking & CommentsNetworking = AppEnvironment.shared.network
    ) {
        self.leagueCode = leagueCode
        self.gameId = gameId
        self.scheduledGame = scheduledGame
        self.initialTabSelectionOverride = initialTabSelectionOverride
        self.compass = compass
        self.entitlement = entitlement
        self.network = network

        if let scheduledGame = scheduledGame {
            let viewModel = GameLargeScoreHeaderViewModel()
            viewModel.delegate = self

            header = viewModel
            header?.update(with: scheduledGame)
        }

        Task {
            await updateMenuItems()
            observeSubscriberStatusChanges()

            if #available(iOS 16.2, *), isLiveActivityEnabled {
                await LiveScoreActivitiesManager.shared.publisherForHasLiveActivityStarted(
                    for: gameId
                )
                .sink { [weak self] hasLiveActivityStarted in
                    self?.hasLiveActivityStarted = hasLiveActivityStarted
                }
                .store(in: &cancellables)
            }
        }
    }

    @MainActor
    private func handle(
        scheduledGame: ScheduledGame,
        entity: GQL.GameContainer,
        boxScoreSections: [GQL.BoxScoreContent.Section]
    ) async {
        leagueCode = entity.fragments.gameV2Lite.league.id
        self.scheduledGame = scheduledGame
        liveBlog = entity.liveBlog
        self.boxScoreSections = boxScoreSections

        await updateMenuItems()

        let header: GameLargeScoreHeaderViewModel

        if let existing = self.header {
            header = existing
        } else {
            let viewModel = GameLargeScoreHeaderViewModel()
            viewModel.delegate = self
            header = viewModel
        }
        header.update(with: entity)
        self.header = header

        shareUrl = entity.permalink.flatMap { URL(string: $0) }
    }

    @MainActor
    private func handleUpdated(entity: GQL.GameContainer) async {
        scheduledGame = ScheduledGame(entity: entity.fragments.gameV2Lite)
        liveBlog = entity.liveBlog
        await updateMenuItems()
        header?.update(with: entity)
    }

    func appWillForeground() {
        loadData()
    }

    func appDidBackground() {
        /// Subscription values can be received while app is going into suspension.
        /// When app goes to foreground after sometime, these stale values will attempt to
        /// update UI, and may interrupt with newly loaded data in appWillForeground().
        /// Hence it is neccessary to stop live updates subscription going into background.
        liveUpdatesSubscription = nil
    }
}

extension GameScreenViewModel {
    func loadData() {
        Task {
            await loadGame(forId: gameId)
        }
    }

    func trackShareTapped() {
        Analytics.track(
            event: .init(
                verb: .click,
                view: .gameFeed,
                element: .share,
                objectType: .gameId,
                objectIdentifier: gameId
            )
        )
    }

    func trackSelectEvent(for selectedItem: GameScreenMenuItem) {
        guard
            let phase = scheduledGame?.phase,
            let currentItem = pagingViewModel?.selectedTab.item
        else {
            return
        }

        /// The view is the view we're on, and the object is the object we have selected.
        let view: AnalyticsEvent.View
        let objectType: AnalyticsEvent.ObjectType
        var blogId: String? = nil

        switch currentItem {
        case .game:
            view = .boxScoreView(for: phase)
        case .liveBlog:
            view = AnalyticsEvent.View.boxScoreLiveBlogView(for: phase)
            blogId = liveBlog?.id
        case .playerGrades:
            view = .gradePlayersGradesTab
        case .stats:
            guard let matchStatsView = AnalyticsEvent.View.matchStatsView(for: phase) else {
                return
            }
            view = matchStatsView
        case .playByPlay:
            guard let playByPlayView = AnalyticsEvent.View.playByPlayView(for: phase) else {
                return
            }
            view = playByPlayView
        case .timeline:
            guard let timelineView = AnalyticsEvent.View.timelineView(for: phase) else {
                return
            }
            view = timelineView
        case .comments:
            view = .discussView(for: phase)
        }

        switch selectedItem {
        case .game:
            objectType = .gameTab
        case .liveBlog:
            objectType = .liveBlogTab
            blogId = liveBlog?.id
        case .playerGrades:
            objectType = .gradesTab
        case .stats:
            objectType = .statsTab
        case .playByPlay:
            objectType = .playsTab
        case .timeline:
            objectType = .timelineTab
        case .comments:
            objectType = .discussTab
        }

        Analytics.track(
            event: .init(
                verb: .click,
                view: view,
                element: .boxScoreNav,
                objectType: objectType,
                metaBlob: .init(
                    leagueId: leagueCode?.rawValue,
                    gameId: gameId,
                    teamId: teamId,
                    blogId: blogId
                )
            )
        )
    }
}

extension BoxScoreDestination.Tab {

    var focusedCommentId: String? {
        switch self {
        case .comments(let id):
            return id
        default:
            return nil
        }
    }

    var focusedBoxScoreSection: BoxScoreFocusableSection? {
        switch self {
        case .boxScore(let section):
            return section
        default:
            return nil
        }
    }

    var isCommentsInitialOverride: Bool {
        switch self {
        case .comments(_):
            return true
        default:
            return false
        }
    }
}

// MARK: - Networking

// MARK: Game

extension GameScreenViewModel {

    @MainActor
    private func loadGame(forId id: String) async {
        guard !state.isLoading else {
            return
        }

        state = .loading()
        do {
            async let loadedGameContainer = network.fetchGameContainer(id: id)

            /// We will eventually replace fetchGameContainer with the BFF query fetchBoxScoreSections.
            async let loadedBoxScore = fetchBoxScoreSections()

            await handle(
                gameContainer: try loadedGameContainer,
                boxScoreSections: loadedBoxScore
            )
        } catch {
            logger.error(
                "Failed to fetch game container for game id: (\(gameId)) with error: \(error)",
                .network
            )
            state = .failed
        }
    }

    private func fetchBoxScoreSections() async -> [GQL.BoxScoreContent.Section] {
        do {
            return try await network.fetchBoxScoreSections(
                gameId: gameId
            )
        } catch {
            logger.error(
                "Failed to fetch box score for game id: (\(gameId)) with error: \(error)",
                .network
            )
            return []
        }
    }

    @MainActor
    private func handle(
        gameContainer entity: GQL.GameContainer,
        boxScoreSections: [GQL.BoxScoreContent.Section]
    ) async {
        /// Bail out if this game is for an unsupported league code. This can happen when incoming from a Push Notification.
        guard GQL.LeagueCode.supportedCodes.contains(entity.fragments.gameV2Lite.league.id) else {
            state = .failed
            return
        }

        let game = ScheduledGame(entity: entity.fragments.gameV2Lite)
        await handle(scheduledGame: game, entity: entity, boxScoreSections: boxScoreSections)

        if game.needsUpdates {
            startLiveUpdates()
        }

        state = .loaded
    }

    private func startLiveUpdates() {
        liveUpdatesSubscription = AppEnvironment.shared.network
            .subscribeToBoxScoreUpdates(forIds: [gameId])
            .sink { [weak self] in
                guard let self else {
                    return
                }
                let container = $0.fragments.gameContainer
                Task {
                    await self.handleUpdated(entity: container)

                    if container.fragments.gameV2Lite.status == .final {
                        self.liveUpdatesSubscription = nil
                    }
                }
            }
    }
}

// MARK: - Menu Items Construction

extension GameScreenViewModel {

    private func observeSubscriberStatusChanges() {
        entitlement.$hasAccessToContent
            .dropFirst()
            .sink { [weak self] _ in
                guard let self else {
                    return
                }
                Task {
                    await self.updateMenuItems()
                }
            }
            .store(in: &cancellables)
    }

    @MainActor
    private func updateMenuItems() async {
        guard
            let leagueCode = leagueCode,
            let game = scheduledGame,
            let phase = scheduledGame?.phase
        else {
            return
        }

        var newItems: [GameScreenMenuItem] = []
        var initialItemSelection: GameScreenMenuItem? = nil

        let boxScoreSectionViewModel = BoxScoreSectionViewModel(
            boxScoreSections: boxScoreSections,
            gameId: gameId,
            gamePhase: phase,
            isSlideStoriesEnabled: compass.config.flags.isGameSlideStoriesEnabled
        )

        do {
            /// Game Tab

            let sectionViewModels = boxScoreSectionViewModel.sectionViewModels(for: .game)

            let viewModel: BoxScoreGameViewModel
            if let existingViewModel = pagingViewModel?.boxScoreViewModel {
                viewModel = existingViewModel
                await viewModel.handleUpdated(sectionViewModels: sectionViewModels)
            } else {
                viewModel = try BoxScoreGameViewModel(
                    leagueCode: leagueCode,
                    gameId: gameId,
                    selectedTeamId: subscribedTeamIds().first ?? "",
                    gamePhase: phase,
                    areCommentsDiscoverable: game.areCommentsDiscoverable,
                    sectionViewModels: sectionViewModels,
                    network: network,
                    focusedSection: initialTabSelectionOverride?.focusedBoxScoreSection
                )
            }

            newItems.append(.game(viewModel))
        } catch {
            logger.warning("Box score was not added: \(error.localizedDescription)")
        }

        /// Live blog
        if let liveBlog = liveBlog {
            let item = GameScreenMenuItem.liveBlog(
                permalink: liveBlog.permalink,
                permalinkForEmbed: liveBlog.permalinkForEmbed,
                isBadged: liveBlog.liveStatus == .live
            )

            newItems.append(item)

            if initialTabSelectionOverride == .liveBlog {
                initialItemSelection = item
                consumeInitialTabSelection()
            }
        }

        /// Discuss
        if game.showDiscussTab(entitlement: entitlement) {
            let commentsViewModel =
                pagingViewModel?.commentsViewModel
                ?? CommentListViewModel(
                    id: gameId,
                    title: game.title,
                    commentsType: .gameV2,
                    comments: [],
                    isCommentLikedProvider: UserDynamicData.comment.isLiked(for:),
                    additionalLikeTapAction: UserDynamicData.comment.likeTapAction,
                    isCommentFlaggedProvider: UserDynamicData.comment.isFlagged(for:),
                    additionalFlagAction: UserDynamicData.comment.flagAction,
                    areTeamSpecificThreadsEnabled: game.areTeamSpecificCommentsEnabled,
                    additionalTeamThreadSwitchAction: { [weak self] teamId in
                        self?.updateTeamId(teamId: teamId)
                    },
                    shouldHideTitleForComments: true,
                    focusedCommentId: initialTabSelectionOverride?.focusedCommentId,
                    network: AppEnvironment.shared.network,
                    userId: AppEnvironment.shared.user.current?.id ?? "",
                    analyticsDefaults: AnalyticDefaults()
                )

            let item = GameScreenMenuItem.comments(
                model: commentsViewModel,
                isBadged: game.areCommentsDiscoverable
            )

            newItems.append(item)

            if case .comments = initialTabSelectionOverride {
                initialItemSelection = item
                consumeInitialTabSelection()
            }
        }

        /// Player grades
        if game.showPlayerGradesTab() {
            let viewModel: GamePlayerGradesViewModel

            if let existing = pagingViewModel?.playerGradesViewModel {
                viewModel = existing
            } else {
                viewModel = GamePlayerGradesViewModel(
                    gameId: gameId,
                    leagueCode: leagueCode,
                    gamePhase: phase,
                    selectedTeamId: subscribedTeamIds().first ?? ""
                )
            }

            let item = GameScreenMenuItem.playerGrades(viewModel)

            newItems.append(item)

            if initialTabSelectionOverride == .playerGrades {
                initialItemSelection = item
                consumeInitialTabSelection()
            }
        }

        /// Match stats
        if game.showStatsTab {
            let viewModel: GameStatsViewModel

            if let existing = pagingViewModel?.gameStatsViewModel {
                viewModel = existing
            } else {
                viewModel = GameStatsViewModel(
                    gameId: gameId,
                    leagueCode: leagueCode,
                    gamePhase: phase,
                    selectedTeamId: subscribedTeamIds().first ?? ""
                )
            }

            let item = GameScreenMenuItem.stats(viewModel)

            newItems.append(item)

            if initialTabSelectionOverride == .stats {
                initialItemSelection = item
                consumeInitialTabSelection()
            }
        }

        /// Play by play
        if game.showPlaysTab {
            let viewModel: PlayByPlayViewModel

            if let existing = pagingViewModel?.gamePlayByPlayViewModel {
                viewModel = existing
            } else {
                viewModel = PlayByPlayViewModel(
                    leagueCode: leagueCode,
                    gameId: gameId,
                    gamePhase: phase
                )
            }

            if leagueCode.sportType == .soccer {
                let item = GameScreenMenuItem.timeline(viewModel)
                newItems.append(item)

                if initialTabSelectionOverride == .plays {
                    initialItemSelection = item
                    consumeInitialTabSelection()
                }
            } else {
                let item = GameScreenMenuItem.playByPlay(viewModel)
                newItems.append(item)

                if initialTabSelectionOverride == .plays {
                    initialItemSelection = item
                    consumeInitialTabSelection()
                }
            }
        }

        if let viewModel = pagingViewModel {
            viewModel.update(with: newItems, selection: initialItemSelection)
        } else {
            pagingViewModel = GamePagingViewModel(
                game: .init(
                    id: game.gameId,
                    phase: phase,
                    leagueCode: leagueCode
                ),
                items: newItems,
                initialItemSelection: initialItemSelection
            )
        }
    }

    private func subscribedTeamIds() -> [String] {
        scheduledGame?.subscribedTeamIds() ?? []
    }

    private func consumeInitialTabSelection() {
        initialTabSelectionOverride = nil
    }

    private func updateTeamId(teamId: String) {
        self.teamId = teamId
    }
}

extension GameScreenViewModel: GameLargeScoreHeaderViewModelDelegate {
    func viewModel(
        _ viewModel: GameLargeScoreHeaderViewModel,
        needsToTrackSelectedEntity entity: FollowingEntity
    ) {
        guard let game = scheduledGame, let phase = game.phase else {
            return
        }

        let view: AnalyticsEvent.View

        switch pagingViewModel?.selectedTab.item {
        case .game:
            view = .boxScoreView(for: phase)

        case .liveBlog:
            // TODO: Implement analytics.
            return

        case .playerGrades:
            // TODO: Implement analytics.
            return

        case .stats:
            guard let matchStatsView = AnalyticsEvent.View.matchStatsView(for: phase) else {
                return
            }
            view = matchStatsView

        case .playByPlay:
            guard let playByPlayView = AnalyticsEvent.View.playByPlayView(for: phase) else {
                return
            }
            view = playByPlayView

        case .timeline:
            guard let timelineView = AnalyticsEvent.View.timelineView(for: phase) else {
                return
            }
            view = timelineView

        case .comments, nil:
            return
        }

        Analytics.track(
            event: .init(
                verb: .click,
                view: view,
                element: .teamScoresAndSchedules,
                objectType: entity.type.analyticsObjectType,
                objectIdentifier: entity.legacyId
            )
        )
    }
}

// MARK: - Helpers

extension GamePagingViewModel {

    var boxScoreViewModel: BoxScoreGameViewModel? {
        for tab in tabs {
            if case let .game(viewModel) = tab.item {
                return viewModel
            }
        }
        return nil
    }

    fileprivate var playerGradesViewModel: GamePlayerGradesViewModel? {
        for tab in tabs {
            if case let .playerGrades(viewModel) = tab.item {
                return viewModel
            }
        }
        return nil
    }

    fileprivate var gameStatsViewModel: GameStatsViewModel? {
        for tab in tabs {
            if case let .stats(viewModel) = tab.item {
                return viewModel
            }
        }
        return nil
    }

    fileprivate var gamePlayByPlayViewModel: PlayByPlayViewModel? {
        for tab in tabs {
            switch tab.item {
            case let .playByPlay(viewModel), let .timeline(viewModel):
                return viewModel
            default:
                break
            }
        }
        return nil
    }

    fileprivate var commentsViewModel: CommentListViewModel? {
        for tab in tabs {
            if case let .comments(model: viewModel, isBadged: _) = tab.item {
                return viewModel
            }
        }
        return nil
    }
}

extension SportType {
    fileprivate var isMatchStatsEnabled: Bool {
        [.basketball, .americanFootball, .hockey, .baseball]
            .contains(self)
    }

    fileprivate var isPlayByPlayEnabled: Bool {
        switch self {
        case .baseball, .basketball, .hockey, .americanFootball, .soccer:
            return true
        default:
            return false
        }
    }
}

// MARK: Live Activity

@available(iOS 16.2, *)
extension GameScreenViewModel {
    func startLiveActivity() async throws {
        guard isLiveActivityEnabled,
            let firstTeam = header?.firstTeam,
            let secondTeam = header?.secondTeam,
            let firstTeamTitle = firstTeam.title,
            let secondTeamTitle = secondTeam.title
        else {
            return
        }

        let firstTeamLogoFileName = [gameId, "first"].joined(separator: "_")
        let secondTeamLogoFileName = [gameId, "second"].joined(separator: "_")

        saveTeamLogo(
            teamLogoUrl: firstTeam.logoUrl,
            fileName: firstTeamLogoFileName
        )
        saveTeamLogo(
            teamLogoUrl: secondTeam.logoUrl,
            fileName: secondTeamLogoFileName
        )

        let attributes = LiveScoreAttributes(
            firstTeamInfo: LiveScoreAttributes.TeamInfo(
                teamName: firstTeamTitle,
                logoFileName: firstTeamLogoFileName
            ),
            secondTeamInfo: LiveScoreAttributes.TeamInfo(
                teamName: secondTeamTitle,
                logoFileName: secondTeamLogoFileName
            ),
            gameId: gameId
        )

        try await LiveScoreActivitiesManager.shared.startLiveActivity(
            attributes: attributes,
            contentState: .init(homeTeamScore: 0, awayTeamScore: 0)
        )
    }

    func endLiveActivity() async {
        guard isLiveActivityEnabled else { return }
        await LiveScoreActivitiesManager.shared.endLiveActivity(
            gameId: gameId,
            finalContentState: .init(homeTeamScore: 0, awayTeamScore: 0)
        )
    }

    private func saveTeamLogo(teamLogoUrl: URL?, fileName: String) {
        guard let teamLogoUrl else {
            return
        }
        Task {
            await LiveScoreActivitiesManager.saveTeamLogo(
                teamLogoURL: teamLogoUrl,
                fileName: fileName
            )
        }
    }
}

extension ScheduledGame {

    var showStatsTab: Bool {
        gameIdentifier.sportType.isMatchStatsEnabled && availableData.contains(.playerStats)
    }

    var showPlaysTab: Bool {
        gameIdentifier.sportType.isPlayByPlayEnabled && availableData.contains(.plays)
    }

    var showLiveBlogTab: Bool {
        availableData.contains(.liveBlog)
    }

    func showDiscussTab(entitlement: Entitlement) -> Bool {
        areCommentsEnabled && entitlement.hasAccessToContent
    }

    func showPlayerGradesTab() -> Bool {
        gradeStatus == .enabled || gradeStatus == .locked
    }
}
