//
//  GradeStatus+Init.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 19/5/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

extension GQL.GameV2Lite {
    public var gradeStatus: GradeStatus? {
        if let gradeStatus = asAmericanFootballGame?.gradeStatus {
            return GradeStatus(gradeStatus: gradeStatus)

        } else if ScoresEnvironment.shared.config.isSoccerPlayerGradesEnabled(),
            let gradeStatus = asSoccerGame?.gradeStatus
        {
            return GradeStatus(gradeStatus: gradeStatus)

        } else if ScoresEnvironment.shared.config.isBasketballPlayerGradesEnabled(),
            let gradeStatus = asBasketballGame?.gradeStatus
        {
            return GradeStatus(gradeStatus: gradeStatus)

        } else if ScoresEnvironment.shared.config.isBaseballPlayerGradesEnabled(),
            let gradeStatus = asBaseballGame?.gradeStatus
        {
            return GradeStatus(gradeStatus: gradeStatus)

        } else if ScoresEnvironment.shared.config.isHockeyPlayerGradesEnabled(),
            let gradeStatus = asHockeyGame?.gradeStatus
        {
            return GradeStatus(gradeStatus: gradeStatus)

        } else {
            return nil
        }
    }
}

extension GQL.GamePlayerGrades {
    public var gradeStatus: GradeStatus? {
        if let gradeStatus = asAmericanFootballGame?.gradeStatus {
            return GradeStatus(gradeStatus: gradeStatus)

        } else if ScoresEnvironment.shared.config.isSoccerPlayerGradesEnabled(),
            let gradeStatus = asSoccerGame?.gradeStatus
        {
            return GradeStatus(gradeStatus: gradeStatus)

        } else if ScoresEnvironment.shared.config.isBasketballPlayerGradesEnabled(),
            let gradeStatus = asBasketballGame?.gradeStatus
        {
            return GradeStatus(gradeStatus: gradeStatus)

        } else if ScoresEnvironment.shared.config.isBaseballPlayerGradesEnabled(),
            let gradeStatus = asBaseballGame?.gradeStatus
        {
            return GradeStatus(gradeStatus: gradeStatus)

        } else if ScoresEnvironment.shared.config.isHockeyPlayerGradesEnabled(),
            let gradeStatus = asHockeyGame?.gradeStatus
        {
            return GradeStatus(gradeStatus: gradeStatus)

        } else {
            return nil
        }
    }
}

extension GradeStatus {
    fileprivate init(gradeStatus: GQL.GradeStatus) {
        switch gradeStatus {
        case .disabled:
            self = .disabled
        case .enabled:
            self = .enabled
        case .locked:
            self = .locked
        case let .__unknown(other):
            assertionFailure("Unsupported grade status \(other)")
            self = .other(other)
        }
    }
}
