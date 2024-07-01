//
//  ScheduledGame+DisplayTitle.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 5/11/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

extension ScheduledGame {

    public enum DateFormat {
        case date
        case timeWithMeridiem
        case time
    }

    public func preGameStatusDisplayTitle(
        dateFormat: DateFormat,
        capitalized: Bool = true
    ) -> String? {
        guard !ScheduledGame.Status.preGameExceptionCases.contains(status) else {
            return capitalized ? status.title().uppercased() : status.title()
        }

        guard let gameDate = scheduledAt else {
            return nil
        }

        return gameDate.gameDateString(
            withFormat: dateFormat,
            isTimeTbd: isScheduledTimeTbd,
            capitalized: capitalized
        )
    }

    public func preGameStatusDisplaySubtitle(
        dateFormat: DateFormat,
        capitalized: Bool = true
    ) -> String? {
        guard let gameDate = scheduledAt else {
            return nil
        }

        guard !ScheduledGame.Status.preGameExceptionCases.contains(status) else {
            return gameDate.dayOfWeekDayMonthFormatted(capitalized: capitalized)
        }

        return gameDate.gameDateString(
            withFormat: dateFormat,
            isTimeTbd: isScheduledTimeTbd,
            capitalized: capitalized
        )
    }
}

extension Date {

    fileprivate func gameDateString(
        withFormat format: ScheduledGame.DateFormat,
        isTimeTbd: Bool?,
        capitalized: Bool = true
    ) -> String {
        switch format {
        case .date:
            return dayOfWeekDayMonthFormatted(capitalized: capitalized)

        case .timeWithMeridiem where isTimeTbd == true:
            return Strings.tbd.localized

        case .time where isTimeTbd == true:
            return Strings.tbd.localized

        case .timeWithMeridiem:
            return Date.timeWithSpaceFormatter.string(from: self)

        case .time:
            return Date.hourDateFormatter.string(from: self)
        }
    }

    fileprivate func dayOfWeekDayMonthFormatted(capitalized: Bool = true) -> String {
        let dayOfWeekString = Date.shortDayFormatter.string(from: self)
        let dayOfWeekDisplay = capitalized ? dayOfWeekString.uppercased() : dayOfWeekString
        let monthDayString = Date.localizedDayMonthFormatter.string(from: self)

        return dayOfWeekDisplay + ", " + monthDayString
    }
}

extension ScheduledGame.Team {
    public var displayScore: String? {
        guard let score = score else {
            return nil
        }

        if let penaltyScore = penaltyScore {
            return "\(score) (\(penaltyScore))"
        } else {
            return score
        }
    }
}
