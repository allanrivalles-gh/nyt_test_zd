//
//  IndicatorLineViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 21/2/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import Foundation

struct IndicatorLineViewModel {
    struct Item {
        let isHighlighted: Bool
    }

    let items: [Item]

    init(items: [Item]) {
        self.items = items
    }

    init?(gameId: String, team: GQL.AmericanFootballTimeoutsTeam) {
        self.init(
            gameId: gameId,
            remainingTimeouts: team.remainingTimeouts,
            usedTimeouts: team.usedTimeouts
        )
    }

    init?(gameId: String, team: GQL.BasketballTimeoutsTeam) {
        self.init(
            gameId: gameId,
            remainingTimeouts: team.remainingTimeouts,
            usedTimeouts: team.usedTimeouts
        )
    }

    init?(gameId: String, remainingTimeouts: Int?, usedTimeouts: Int?) {
        guard var remainingTimeouts = remainingTimeouts, var usedTimeouts = usedTimeouts else {
            return nil
        }

        if remainingTimeouts < 0 {
            ATHLogger(category: .boxScore).warning(
                "Encountered invalid remaining timouts of \(remainingTimeouts) for game \(gameId)"
            )
            remainingTimeouts = 0
        }

        if usedTimeouts < 0 {
            ATHLogger(category: .boxScore).warning(
                "Encountered invalid used timouts of \(usedTimeouts) for game \(gameId)"
            )
            usedTimeouts = 0
        }

        let highlighting: [Bool] =
            Array(repeating: true, count: remainingTimeouts)
            + Array(repeating: false, count: usedTimeouts)

        guard !highlighting.isEmpty else { return nil }

        items = highlighting.map { Item(isHighlighted: $0) }
    }
}
