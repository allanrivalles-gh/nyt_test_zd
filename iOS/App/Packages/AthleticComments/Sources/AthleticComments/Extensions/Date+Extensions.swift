//
//  Date+Extensions.swift
//
//
//  Created by Jason Leyrer on 8/1/22.
//

import AthleticFoundation
import Foundation

extension Date {
    public func commentTimeString(timeSettings: TimeSettings = SystemTimeSettings()) -> String {
        if isNewerThan(24.hours, timeSettings: timeSettings) {
            return commentTimeShort(timeSettings: timeSettings)
        } else if isSame(
            timeSettings.now().add(days: -1),
            granularity: .day,
            timeSettings: timeSettings
        ) {
            return Strings.yesterday.localized.capitalized
        } else if isSame(timeSettings.now(), granularity: .year, timeSettings: timeSettings) {
            return Date.shortMonthDayFormatter.string(from: self)
        } else {
            return Date.shortMonthDayYearFormatter.string(from: self)
        }
    }

    public var discussionDateString: String {
        Date.shortWeekdayShortMonthDayFormatter.string(from: self)
    }

    public var numberOfDaysFromNow: Int {
        let fromDate = Calendar.current.startOfDay(for: Date())
        let toDate = Calendar.current.startOfDay(for: self)

        return Calendar.current.dateComponents([.day], from: fromDate, to: toDate).day!
    }

    private func commentTimeShort(timeSettings: TimeSettings = SystemTimeSettings()) -> String {
        guard !isDistantPast else {
            return ""
        }

        guard isOlderThan(1.minute, timeSettings: timeSettings) else {
            return Strings.justNow.localized
        }

        return Date.abbreviatedComponentsFormatter.string(
            from: -timeIntervalSince(timeSettings.now())
        ) ?? ""
    }
}
