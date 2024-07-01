//
//  GameScreenViewModelFactory.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 26/08/2021.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticNavigation
import AthleticScoresFoundation
import Foundation

struct GameScreenViewModelFactory {
    static func viewModel(
        from notificationData: BoxScoreDestination
    ) -> GameScreenViewModel {
        viewModel(
            fromId: notificationData.gameId,
            initialTabSelectionOverride: notificationData.initialSelectionOverride
        )
    }

    static func viewModel(
        fromId gameId: String,
        initialTabSelectionOverride: BoxScoreDestination.Tab? = nil
    ) -> GameScreenViewModel {
        GameScreenViewModel(
            leagueCode: nil,
            gameId: gameId,
            scheduledGame: nil,
            initialTabSelectionOverride: initialTabSelectionOverride
        )
    }

    static func viewModel(
        from scheduledGame: ScheduledGame,
        initialTabSelectionOverride: BoxScoreDestination.Tab? = nil
    ) -> GameScreenViewModel {
        GameScreenViewModel(
            leagueCode: scheduledGame.gameIdentifier.leagueCode,
            gameId: scheduledGame.gameIdentifier.gameId,
            scheduledGame: scheduledGame,
            initialTabSelectionOverride: initialTabSelectionOverride
        )
    }
}
