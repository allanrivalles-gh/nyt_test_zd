//
//  Timestamp.swift
//
//
//  Created by Kyle Browning on 5/26/22.
//

import Apollo
import Foundation

public typealias Timestamp = Date

extension Timestamp: JSONDecodable, JSONEncodable {

    public init(jsonValue value: JSONValue) throws {
        if let intValue = value as? Int {
            let timeInterval = TimeInterval(intValue / 1000)
            self = Date(timeIntervalSince1970: timeInterval)
            return
        }
        guard let isoString = value as? String else {
            throw JSONDecodingError.couldNotConvert(value: value, to: Date.self)
        }

        var convertedDate: Date? = nil
        switch isoString.count {
        case 10:
            convertedDate = Date.iso8601CalendarDateFormatter.date(from: isoString)
        case 17:
            convertedDate = Date.iso8601Formatter.date(from: isoString)
        case 24:
            convertedDate = Date.iso8601MillisecondsFormatter.date(from: isoString)
        default:
            convertedDate = Date.iso8601Formatter.date(from: isoString)
        }

        guard let date = convertedDate else {
            throw JSONDecodingError.couldNotConvert(value: value, to: Date.self)
        }
        self = date
    }

    public var jsonValue: JSONValue {
        return Date.iso8601Formatter.string(from: self)
    }
}

extension Date {
    static let iso8601MillisecondsFormat = "yyyy-MM-dd HH:mm:ss.SSS"
    static let iso8601CalendarDateFormat = "yyyy-MM-dd"

    internal static let iso8601Formatter = ISO8601DateFormatter()

    internal static let iso8601CalendarDateFormatter: DateFormatter = {
        dateFormatter(
            forFormat: Date.iso8601CalendarDateFormat
        ) as DateFormatter
    }()

    internal static let iso8601MillisecondsFormatter: DateFormatter = {
        dateFormatter(
            forFormat: Date.iso8601MillisecondsFormat
        ) as DateFormatter
    }()

    private static func dateFormatter(forFormat format: String) -> DateFormatter {
        let formatter = DateFormatter()
        formatter.calendar = Calendar(identifier: .iso8601)
        formatter.locale = Locale.current
        formatter.timeZone = NSTimeZone.default
        formatter.dateFormat = format

        return formatter
    }
}
