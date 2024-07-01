//
//  GameScoreActionsProvider.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 14/2/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import SwiftUI

public struct GameScoreContextMenuActionsProvider {

    let gameId: String
    let status: GamePhase?

    public init(gameId: String, status: GamePhase?) {
        self.gameId = gameId
        self.status = status
    }

    @ViewBuilder
    public var menuButtons: some View {
        if ScoresEnvironment.shared.currentUser()?.isAdmin == true {
            Text("ID: \(gameId)")

            Button(
                "Copy ID",
                action: {
                    UIPasteboard.general.string = gameId
                }
            )

            Button(
                "Copy deeplink",
                action: {
                    UIPasteboard.general.string = "\(Global.General.deeplinkUrl)boxscore/\(gameId)"
                }
            )

            #if !PRODUCTION
                if status == .postGame {
                    Button(
                        "Trigger replay",
                        action: {
                            triggerReplay(forGameId: gameId)
                        }
                    )
                }
            #endif
        }
    }

    private func triggerReplay(forGameId gameId: String) {
        Task {
            try await ScoresEnvironment.shared.network.startGameReplay(id: gameId)
        }
    }
}
