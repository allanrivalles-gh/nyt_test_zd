//
//  GamePlayerGradesViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 25/11/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Apollo
import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticScoresFoundation
import Foundation

final class GamePlayerGradesViewModel: ObservableObject {
    @MainActor @Published var state: LoadingState = .initial
    @MainActor @Published var items: [AnyIdentifiable] = []

    let gameId: String
    let leagueCode: GQL.LeagueCode

    private let sportViewModel: GamePlayerGradesSportViewModel
    private var cancellables = Cancellables()

    init(
        gameId: String,
        leagueCode: GQL.LeagueCode,
        gamePhase: GamePhase,
        selectedTeamId: String,
        eventManager: AnalyticEventManager = AnalyticsManagers.events
    ) {
        self.gameId = gameId
        self.leagueCode = leagueCode
        let sportType = leagueCode.sportType

        switch sportType {
        default:
            sportViewModel = GamePlayerGradeGeneralSportViewModel(
                gameId: gameId,
                leagueCode: leagueCode,
                gamePhase: gamePhase,
                selectedTeamId: selectedTeamId,
                eventManager: eventManager
            )
        }

        Task {
            await sportViewModel.prepareBindings()
            await sportViewModel.stateUpdates
                .bindUIUpdates(to: self, at: \.state)
                .store(in: &cancellables)
            await sportViewModel.itemsUpdates
                .bindUIUpdates(to: self, at: \.items)
                .store(in: &cancellables)
        }
    }

    func loadData(isInitialLoad: Bool = false) async {
        guard await !state.isLoading else {
            return
        }

        await sportViewModel.loadData(isInitialLoad: isInitialLoad)
    }

    func trackView() async {
        await sportViewModel.trackView()
    }

    func appWillForeground() {
        Task {
            await loadData()
        }
    }

    func appDidBackground() {
        Task {
            /// Subscription values can be received while app is going into suspension.
            /// When app goes to foreground after sometime, these stale values will attempt to
            /// update UI, and may interrupt with newly loaded data in appWillForeground().
            /// Hence it is neccessary to stop live updates subscription going into background.
            await sportViewModel.stopLiveUpdates()
        }
    }
}

extension GamePlayerGradesViewModel {
    static var placeholder = GamePlayerGradesViewModel(
        gameId: "",
        leagueCode: .__unknown(""),
        gamePhase: .inGame,
        selectedTeamId: ""
    )
}
