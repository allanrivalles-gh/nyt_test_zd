//
//  ScheduledGame+GameOutcome.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 11/11/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

extension ScheduledGame {
    public enum GameOutcome {
        case win, tie, loss
    }

    public struct TeamOutcomes {
        public let first: GameOutcome?
        public let second: GameOutcome?
    }

    public var teamOutcomes: TeamOutcomes {
        guard
            GamePhase(gameStatus: status, startedAt: startedAt) == .postGame,
            let firstMainScore = firstTeam?.score?.intValue,
            let secondMainScore = secondTeam?.score?.intValue
        else {
            return TeamOutcomes(first: nil, second: nil)
        }

        /// The scores that decide who won/lost
        let firstDecidingScore: Int
        let secondDecidingScore: Int

        if let firstPenaltyScore = firstTeam?.penaltyScore?.intValue,
            let secondPenaltyScore = secondTeam?.penaltyScore?.intValue
        {
            /// If there was a penalty shootout (soccer) then use the penalty score
            firstDecidingScore = firstPenaltyScore
            secondDecidingScore = secondPenaltyScore
        } else {
            firstDecidingScore = firstMainScore
            secondDecidingScore = secondMainScore
        }

        if firstDecidingScore < secondDecidingScore {
            return TeamOutcomes(first: .loss, second: .win)
        } else if firstDecidingScore > secondDecidingScore {
            return TeamOutcomes(first: .win, second: .loss)
        } else {
            return TeamOutcomes(first: .tie, second: .tie)
        }
    }
}
