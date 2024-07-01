//
//  GQLTournamentGame+Title.swift
//
//
//  Created by Leonardo da Silva on 29/11/22.
//

import AthleticApolloTypes
import AthleticFoundation
import Foundation

extension GQL.TournamentGame {
    private struct Formatters {
        let date: DateFormatter
        let time: DateFormatter
        let dayOfWeek: DateFormatter

        static let shared: Formatters = .init()

        init(timeSettings: TimeSettings = SystemTimeSettings()) {
            date = DateFormatter(dateFormat: "MMM d", timeSettings: timeSettings)
            time = DateFormatter(dateFormat: "h:mma", timeSettings: timeSettings)
            dayOfWeek = DateFormatter(dateFormat: "EEE", timeSettings: timeSettings)
        }
    }

    func title(
        for phase: TournamentTile.Phase,
        tbdString: String,
        timeSettings: TimeSettings = SystemTimeSettings()
    ) -> String {
        let formatters: Formatters =
            timeSettings.timeZone == .current ? .shared : .init(timeSettings: timeSettings)

        var date: String? {
            guard let scheduledAt else {
                return nil
            }

            return formatters.date.string(from: scheduledAt)
        }

        var time: String? {
            if timeTbd ?? false {
                return tbdString
            }

            guard let scheduledAt else {
                return nil
            }

            return formatters.time.string(from: scheduledAt)
        }

        var dayOfWeek: String? {
            guard let scheduledAt else {
                return nil
            }

            return formatters.dayOfWeek.string(from: scheduledAt)
        }

        var isGameToday: Bool {
            guard let scheduledAt else {
                return false
            }

            return timeSettings.calendar.isDate(scheduledAt, inSameDayAs: timeSettings.now())
        }

        var isGameUpcoming: Bool {
            guard let scheduledAt else {
                return false
            }

            let today = timeSettings.calendar.startOfDay(for: timeSettings.now())
            return scheduledAt > today.add(days: 7)
        }

        var parts = [String]()

        switch phase {
        case .postGame:
            parts.append(postGamePrefix)
            if let date {
                parts.append(date)
            }
        case .inGame:
            break
        case .preGame:
            if !isGameToday {
                if let dayOfWeek {
                    parts.append(dayOfWeek)
                }
            }
            if isGameUpcoming {
                if let date {
                    parts.append(date)
                }
            } else {
                if let time {
                    parts.append(time)
                }
            }
        }

        if let venueName = venue?.name {
            parts.append(venueName)
        }

        return parts.joined(separator: ", ")
    }

    fileprivate var postGamePrefix: String {
        sport == .soccer ? Strings.postGamePrefixSoccer.localized : Strings.postGamePrefix.localized
    }
}

extension DateFormatter {
    fileprivate convenience init(dateFormat: String, timeSettings: TimeSettings) {
        self.init()
        self.dateFormat = dateFormat
        self.timeZone = timeSettings.timeZone
        self.calendar = timeSettings.calendar
    }
}
