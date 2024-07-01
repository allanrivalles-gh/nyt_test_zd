//
//  PlayerGrade.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 19/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation

struct PlayerGrade {

    let average: Double
    let averageString: String
    let total: Int
    let userGrade: Int?
    let updatedAt: Date

    init(average: Double, averageString: String, total: Int, userGrade: Int?, updatedAt: Date) {
        self.average = average
        self.averageString = total > 0 ? averageString : Strings.notApplicableAbbreviation.localized
        self.total = total
        self.userGrade = userGrade
        self.updatedAt = updatedAt
    }

}

extension PlayerGrade: Equatable {}

extension PlayerGrade {

    /// Creates a new player grade from the given instance by updating the user grade and adjusting the average & totals.
    /// - Parameters:
    ///   - source: Base grade
    ///   - userGrade: The new user grade to apply
    /// - Returns: Updated instance
    static func make(
        from source: PlayerGrade,
        withNewUserGrade userGrade: Int?,
        timeSettings: TimeSettings = SystemTimeSettings()
    ) -> PlayerGrade {
        let sumExcludingOldGrade: Double
        let totalExcludingOldGrade: Int

        if let oldGrade = source.userGrade {
            sumExcludingOldGrade = (source.average * Double(source.total)) - Double(oldGrade)
            totalExcludingOldGrade = max(0, source.total - 1)

        } else {
            sumExcludingOldGrade = source.average * Double(source.total)
            totalExcludingOldGrade = source.total
        }

        let gradeToAdd: Double = userGrade.map { Double($0) } ?? 0
        let totalToAdd = userGrade != nil ? 1 : 0

        let newSum = sumExcludingOldGrade + gradeToAdd
        let newTotal = totalExcludingOldGrade + totalToAdd
        let newAverage =
            newTotal > 0
            ? (newSum / Double(newTotal)).rounded(toPlaces: 1)
            : 0

        return PlayerGrade(
            average: newAverage,
            averageString: String(format: "%.1f", newAverage),
            total: newTotal,
            userGrade: userGrade,
            updatedAt: timeSettings.now()
        )
    }

}
