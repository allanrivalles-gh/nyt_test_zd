//
//  GQLGamePlayerGradesTeamLineUp+Players.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 6/1/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

extension GQL.GamePlayerGradesTeam.LineUp {

    var sortedGradablePlayers: [GQL.GamePlayerGradesTeam.LineUp.Player] {
        players
            .filter { $0.grade != nil }
            .sorted(by: { lhs, rhs in
                let lhsOrder = (lhs.grade?.fragments.gamePlayerGrade.order ?? Int.max, lhs.id)
                let rhsOrder = (rhs.grade?.fragments.gamePlayerGrade.order ?? Int.max, rhs.id)
                return lhsOrder < rhsOrder
            })
    }

}
