//
//  GradesDetailGameViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 19/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticScoresFoundation
import AthleticUI
import Combine
import Foundation

final class GradesDetailGameViewModel: ObservableObject {
    enum GameProgressStyle {
        case live
        case highlighted
        case normal
    }

    typealias Network = GamePlayerGradesNetworking & LiveUpdatesNetworking

    @Published private(set) var firstScore: String = .gameStatPlaceholder
    @Published private(set) var secondScore: String = .gameStatPlaceholder
    @Published private(set) var firstTeamLogos: [ATHImageResource] = []
    @Published private(set) var secondTeamLogos: [ATHImageResource] = []
    @Published private(set) var progressMain: String?
    @Published private(set) var progressExtra: String?
    @Published private(set) var progressMainStyle: GameProgressStyle = .normal
    @Published private(set) var progressExtraStyle: GameProgressStyle = .normal

    private let gameId: String
    private let network: Network
    private var subscriptionCancellable: AnyCancellable?

    init(gameId: String, network: Network = AppEnvironment.shared.network) {
        self.gameId = gameId
        self.network = network

        Task { @MainActor in
            let gameSummary = try await network.fetchGamePlayerGradesGameSummary(gameId: gameId)
            handleGameUpdate(model: gameSummary)
        }
    }

    private func handleGameUpdate(model: GQL.GamePlayerGradesGameSummary) {
        firstScore = model.firstTeam?.score?.string ?? .gameStatPlaceholder
        secondScore = model.secondTeam?.score?.string ?? .gameStatPlaceholder

        firstTeamLogos =
            model.firstTeam?.team?.logos.map {
                ATHImageResource(entity: $0.fragments.teamLogo)
            } ?? []
        secondTeamLogos =
            model.secondTeam?.team?.logos.map {
                ATHImageResource(entity: $0.fragments.teamLogo)
            } ?? []

        let gameStatusDisplay = model.gameStatus?.fragments.gameStatusDisplay
        let phase = GamePhase(statusCode: model.status, startedAt: model.startedAt)
        if let phase, phase == .postGame {
            progressExtra = gameStatusDisplay?.detail
            progressMain = model.scheduledAt?.dateShort()
            progressMainStyle = .normal
            progressExtraStyle = .highlighted

        } else {
            progressMain = gameStatusDisplay?.main
            progressExtra = gameStatusDisplay?.extra
            if model.sport == .soccer {
                progressMainStyle = .live
                progressExtraStyle = .normal
            } else {
                progressMainStyle = .highlighted
                progressExtraStyle = .live
            }
        }

        if let phase, ScheduledGame.needsUpdates(for: phase, scheduledAt: model.scheduledAt) {
            startLiveUpdates()
        } else {
            subscriptionCancellable = nil
        }
    }

    private func startLiveUpdates() {
        subscriptionCancellable =
            network
            .startLiveUpdates(
                subscription: GQL.GradesGameSummaryUpdatesSubscription(gameId: gameId)
            )
            .receive(on: RunLoop.main)
            .sink { [weak self] data in
                guard
                    let self = self,
                    let data = data.liveScoreUpdates
                else {
                    return
                }

                self.handleGameUpdate(model: data.fragments.gamePlayerGradesGameSummary)
            }
    }
}
