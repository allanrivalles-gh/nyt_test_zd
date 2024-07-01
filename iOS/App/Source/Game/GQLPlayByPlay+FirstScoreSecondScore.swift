//
//  GQLPlayByPlay+FirstScoreSecondScore.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 16/6/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

extension GQL.BaseballTeamPlay {
    var firstScore: String {
        awayScore.string
    }

    var secondScore: String {
        homeScore.string
    }
}

extension GQL.AmericanFootballDrive {
    var firstScore: String {
        awayScore.string
    }

    var secondScore: String {
        homeScore.string
    }
}
