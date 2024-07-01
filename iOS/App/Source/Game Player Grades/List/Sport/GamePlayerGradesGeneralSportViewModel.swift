//
//  GamePlayerGradesGeneralSportViewModel.swift
//  theathletic-ios
//
//  Created by Duncan Lau on 30/11/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Apollo
import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticScoresFoundation
import AthleticUI
import Combine
import Foundation

final actor GamePlayerGradeGeneralSportViewModel: GamePlayerGradesSportViewModel {
    typealias Network = GamePlayerGradesNetworking & LiveUpdatesNetworking

    @Published var state: LoadingState = .initial
    @Published var items: [AnyIdentifiable] = []

    @Published private var selectedTeamId: String
    @Published private var entity: GQL.GamePlayerGrades?

    private let gameId: String
    private let leagueCode: GQL.LeagueCode
    private var gamePhase: GamePhase
    private let analytics: PlayerGradesAnalyticsTracker
    private let network: Network
    private lazy var logger = ATHLogger(category: .gamePlayerGrades)
    private var cancellables = Cancellables()
    private var liveUpdatesCancellable: AnyCancellable?
    private let gradesStore: PlayerGradesDataStore

    var stateUpdates: Published<LoadingState>.Publisher {
        $state
    }

    var itemsUpdates: Published<[AnyIdentifiable]>.Publisher {
        $items
    }

    init(
        gameId: String,
        leagueCode: GQL.LeagueCode,
        gamePhase: GamePhase,
        selectedTeamId: String,
        eventManager: AnalyticEventManager,
        gradesStore: PlayerGradesDataStore = .shared,
        network: Network = AppEnvironment.shared.network
    ) {
        self.gameId = gameId
        self.leagueCode = leagueCode
        self.gamePhase = gamePhase
        _selectedTeamId = Published(wrappedValue: selectedTeamId)
        self.gradesStore = gradesStore
        self.analytics = PlayerGradesAnalyticsTracker(
            gameId: gameId,
            leagueCode: leagueCode,
            eventManager: eventManager
        )
        self.network = network
    }

    func prepareBindings() {
        Publishers.CombineLatest($selectedTeamId, $entity.compactMap { $0 })
            .sink { [weak self] selectedTeamId, entity in
                guard let self = self else { return }
                Task {
                    await self.updateItems(with: entity, selectedTeamId: selectedTeamId)
                }
            }
            .store(in: &cancellables)
    }

    func loadData(isInitialLoad: Bool = false) async {
        state = .loading(showPlaceholders: isInitialLoad)

        do {
            let entity = try await network.fetchGamePlayerGrades(gameId: gameId)
            handle(entity: entity)
            state = .loaded
        } catch let error {
            logger.debug("Error fetching player grades: \(error)")
            state = .failed
        }
    }

    func select(teamId: String) {
        selectedTeamId = teamId

        Task {
            await analytics.clickSwitchPlayerGradeTeamsOnGradesTab(teamId: teamId)
        }
    }

    func trackView() {
        switch gamePhase {
        case .preGame, .nonStarter:
            /// Unsupported
            break
        case .inGame:
            Task {
                await analytics.viewGradesTabInGame()
            }
        case .postGame:
            Task {
                await analytics.viewGradesTabPostGame()
            }
        }
    }

    func stopLiveUpdates() {
        liveUpdatesCancellable = nil
    }
}

// MARK: - Private

extension GamePlayerGradeGeneralSportViewModel {
    private func startLiveUpdates() {
        liveUpdatesCancellable =
            network
            .startLiveUpdates(subscription: GQL.GamePlayerGradesUpdatesSubscription(gameId: gameId))
            .sink { [weak self] data in
                guard
                    let self = self,
                    let data = data.liveScoreUpdates
                else {
                    return
                }

                Task {
                    await self.handle(entity: data.fragments.gamePlayerGrades)
                }
            }
    }

    private func handle(entity: GQL.GamePlayerGrades) {
        gradesStore.storeGrades(from: entity)
        self.entity = entity

        let phase = GamePhase(statusCode: entity.status, startedAt: entity.startedAt)
        if let phase, phase != gamePhase {
            gamePhase = phase
        }
        var scheduledGameNeedsUpdate = false

        if let phase = GamePhase(statusCode: entity.status, startedAt: entity.startedAt) {
            scheduledGameNeedsUpdate = ScheduledGame.needsUpdates(
                for: phase,
                scheduledAt: entity.scheduledAt
            )
        }

        if scheduledGameNeedsUpdate || entity.gradeStatus == .enabled {
            if liveUpdatesCancellable == nil {
                startLiveUpdates()
            }
        } else {
            liveUpdatesCancellable = nil
        }
    }
}

// MARK: View Model Construction

extension GamePlayerGradeGeneralSportViewModel {
    private func updateItems(
        with entity: GQL.GamePlayerGrades,
        selectedTeamId: String
    ) {
        guard
            let firstTeam = entity.firstTeam,
            let secondTeam = entity.secondTeam
        else {
            return
        }

        let teams = [firstTeam, secondTeam]
        let firstTeamInfo = PlayerGradesBasicTeamInfo(
            id: firstTeam.team?.id,
            displayName: firstTeam.team?.displayName
        )
        let secondTeamInfo = PlayerGradesBasicTeamInfo(
            id: secondTeam.team?.id,
            displayName: secondTeam.team?.displayName
        )
        let selectedTeamIndex = secondTeamInfo.id == selectedTeamId ? 1 : 0
        let team = teams[selectedTeamIndex]

        guard
            let playerGradesTeamViewModel =
                GamePlayerGradeGeneralSportViewModel.makePlayerGradesTeamViewModel(
                    from: team,
                    gameId: entity.id,
                    leagueCode: entity.league.id,
                    analytics: analytics,
                    gradeStatus: entity.gradeStatus,
                    gradesStore: gradesStore
                )
        else {
            self.items = []
            return
        }

        self.items = items(
            from: playerGradesTeamViewModel,
            firstTeamInfo: firstTeamInfo,
            secondTeamInfo: secondTeamInfo,
            selectedTeamId: selectedTeamId
        )
    }
}
