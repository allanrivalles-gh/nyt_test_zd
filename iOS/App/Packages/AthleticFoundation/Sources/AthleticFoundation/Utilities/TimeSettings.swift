//
//  TimeSettings.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 30/4/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

// An object that can provide the user's time settings
public protocol TimeSettings {

    /// Timestamp for the current time
    func now() -> Date

    /// Current TimeZone
    var timeZone: TimeZone { get }

    /// User calendar with associated timezone / locale
    var calendar: Calendar { get }

}

/// The current system time settings
/// This is expected to be the default value for any variables requiring a `TimeSettings` object.
public struct SystemTimeSettings: TimeSettings {
    public init() {}

    public func now() -> Date {
        return Date()
    }

    public var timeZone: TimeZone {
        return .current
    }

    public var calendar: Calendar {
        return .current
    }
}

public struct MockTimeSettings: TimeSettings {
    private let _now: Date
    public let timeZone: TimeZone
    public let calendar: Calendar

    public init(
        now: Date = Date(timeIntervalSince1970: 0),
        timeZone: TimeZone? = nil,
        calendar: Calendar? = nil
    ) {
        let timeZone = timeZone ?? TimeZone(secondsFromGMT: 0)!
        let calendar = calendar ?? Calendar.create(identifier: .gregorian, timeZone: timeZone)
        self._now = now
        self.timeZone = timeZone
        self.calendar = calendar
    }

    public func now() -> Date {
        _now
    }
}

extension Calendar {
    fileprivate static func create(identifier: Identifier, timeZone: TimeZone) -> Calendar {
        var calendar = Calendar(identifier: identifier)
        calendar.timeZone = timeZone
        return calendar
    }
}
