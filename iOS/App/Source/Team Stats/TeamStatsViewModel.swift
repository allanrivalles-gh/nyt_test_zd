//
//  TeamStatsViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 19/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import Foundation

final class TeamStatsViewModel: ObservableObject {
    enum Section: Identifiable {
        case leaders(TeamSeasonStatsLeadersViewModel)
        case seasonStats(TeamSeasonStatsViewModel)
        case playerStats(TeamPlayerStatsViewModel)

        var id: String {
            switch self {
            case let .leaders(viewModel):
                return viewModel.id
            case let .seasonStats(viewModel):
                return viewModel.id
            case let .playerStats(viewModel):
                return viewModel.id
            }
        }
    }

    enum PageOption {
        case team, player
    }

    fileprivate struct Pages {
        let team: TeamPage?
        let player: PlayerPage?
    }

    fileprivate struct TeamPage {
        let leaders: TeamSeasonStatsLeadersViewModel?
        let seasonStats: TeamSeasonStatsViewModel?
    }

    fileprivate struct PlayerPage {
        let playerStats: TeamPlayerStatsViewModel
    }

    @MainActor @Published private(set) var state: LoadingState = .initial
    @MainActor @Published private(set) var picker: SegmentedPickerViewModel<PageOption>?

    @MainActor
    var sections: [Section]? {
        guard let pages = pages else { return nil }

        switch selectedPage {
        case .team:
            guard let page = pages.team else { return [] }
            return [
                page.leaders.map { .leaders($0) },
                page.seasonStats.map { .seasonStats($0) },
            ].compactMap { $0 }
        case .player:
            guard let page = pages.player else { return [] }
            return [.playerStats(page.playerStats)]
        }
    }

    @MainActor @Published private(set) var selectedPage: PageOption = .team
    @MainActor @Published private var pages: Pages?

    private let teamId: String
    private let followingModel: FollowingModel
    private let builder = TeamStatsViewModelBuilder()

    init(
        teamId: String,
        followingModel: FollowingModel = AppEnvironment.shared.following
    ) {
        self.teamId = teamId
        self.followingModel = followingModel
    }

    @MainActor
    func fetchDataIfNecessary(network: HubNetworking) async {
        let isEmpty = sections == nil || sections?.isEmpty == true

        guard !state.isLoading, isEmpty else {
            return
        }

        state = .loading()

        await loadData(network: network)
    }

    private func loadData(network: HubNetworking) async {
        do {
            let response = try await network.fetchTeamStats(teamId: teamId)
            let existingPages = await pages
            let pages = await builder.makePages(
                response: response,
                teamId: teamId,
                sport: response.team.map({ SportType(gqlValue: $0.sport) }),
                existingPages: existingPages
            )
            let picker = await builder.makePicker(
                pages: pages,
                selectedPage: selectedPage
            ) { [weak self] selectedId in
                guard let self = self else { return }
                Task {
                    await self.mainActorSet(\.selectedPage, selectedId)
                }
            }

            await MainActor.run {
                self.picker = picker
                self.pages = pages
                self.state = .loaded
            }
        } catch {
            ATHLogger(category: .hub).debug("Failed to load team stats with error \(error)")
            await mainActorSet(\.state, .failed)
        }
    }

    func trackPageView() {
        Task {
            let page = await selectedPage
            let element: AnalyticsEvent.Element = {
                switch page {
                case .team: return .teamStats
                case .player: return .playerStats
                }
            }()

            await Analytics.track(
                event: AnalyticsEventRecord(
                    verb: .view,
                    view: .stats,
                    element: element,
                    objectType: .teamId,
                    objectIdentifier: teamId,
                    metaBlob: .init(
                        leagueId: followingModel.followableEntities.team(
                            withGqlId: teamId
                        )?.associatedLeagueLegacyId
                    )
                )
            )
        }
    }
}

private actor TeamStatsViewModelBuilder {
    func makePages(
        response: TeamStatsResponse,
        teamId: String,
        sport: SportType?,
        existingPages: TeamStatsViewModel.Pages?
    ) async -> TeamStatsViewModel.Pages {
        let leaders = response.leadersTeam.flatMap {
            TeamSeasonStatsLeadersViewModel(
                id: "stat-leaders",
                teamId: teamId,
                entity: $0
            )
        }
        let seasonStats = response.stats.flatMap { stats in
            TeamSeasonStatsViewModel(
                id: "season-stats",
                models: stats.map { GameStat($0) }
            )
        }
        let playerStats: TeamPlayerStatsViewModel?
        let teamPage: TeamStatsViewModel.TeamPage?
        let playerPage: TeamStatsViewModel.PlayerPage?

        if let players = response.players {
            if let existingViewModel = existingPages?.player?.playerStats {
                playerStats = await TeamPlayerStatsViewModel(
                    viewModel: existingViewModel,
                    sport: sport,
                    entities: players
                )
            } else {
                playerStats = await TeamPlayerStatsViewModel(
                    id: "player-stats",
                    sport: sport,
                    entities: players,
                    teamId: teamId,
                    teamColor: response.team?.colorPrimary
                )
            }
        } else {
            playerStats = nil
        }

        if leaders != nil || seasonStats != nil {
            teamPage = TeamStatsViewModel.TeamPage(
                leaders: leaders,
                seasonStats: seasonStats
            )
        } else {
            teamPage = nil
        }

        if let playerStats = playerStats {
            playerPage = TeamStatsViewModel.PlayerPage(
                playerStats: playerStats
            )
        } else {
            playerPage = nil
        }

        return TeamStatsViewModel.Pages(
            team: teamPage,
            player: playerPage
        )
    }

    func makePicker(
        pages: TeamStatsViewModel.Pages,
        selectedPage: TeamStatsViewModel.PageOption,
        onSelectId: @escaping (TeamStatsViewModel.PageOption) -> Void
    ) async -> SegmentedPickerViewModel<TeamStatsViewModel.PageOption>? {
        guard
            pages.team != nil,
            pages.player != nil
        else {
            return nil
        }

        return SegmentedPickerViewModel(
            options: [
                pages.team.map { _ in
                    SegmentedPickerViewModel.Option(
                        id: .team,
                        title: Strings.teamStatsTeamTitle.localized.capitalized
                    )
                },
                pages.player.map { _ in
                    SegmentedPickerViewModel.Option(
                        id: .player,
                        title: Strings.teamStatsPlayerTitle.localized.capitalized
                    )
                },
            ].compactMap { $0 },
            selectedId: selectedPage,
            onSelectId: onSelectId
        )
    }
}
