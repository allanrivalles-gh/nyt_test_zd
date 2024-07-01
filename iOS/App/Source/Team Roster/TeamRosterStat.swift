//
//  TeamRosterStat.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 30/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation

struct TeamRosterStat {
    enum Value {
        /// Age in years and days. Days is used in the comparison for correct granularity.
        case age(Int, Int)

        case birthDate(Date)

        case height(Int, HeightUnit)

        case position(String)

        case weight(Int, WeightUnit)
    }

    let id: String
    let type: String
    let shortLabel: String
    let value: Value
}

extension TeamRosterStat.Value: Comparable {
    static func < (lhs: Self, rhs: Self) -> Bool {
        switch (lhs, rhs) {
        case let (.age(lhsYears, lhsDays), .age(rhsYears, rhsDays)):
            return (lhsYears, lhsDays) < (rhsYears, rhsDays)

        case let (.birthDate(lhs), .birthDate(rhs)):
            return lhs < rhs

        case let (.height(lhs, lhsUnit), .height(rhs, rhsUnit)):
            let centimetersPerInch: Double = 2.54
            let lhsCentimeters: Double
            let rhsCentimeters: Double

            switch lhsUnit {
            case .centimeters:
                lhsCentimeters = Double(lhs)
            case .inches:
                lhsCentimeters = Double(lhs) * centimetersPerInch
            }

            switch rhsUnit {
            case .centimeters:
                rhsCentimeters = Double(rhs)
            case .inches:
                rhsCentimeters = Double(rhs) * centimetersPerInch
            }

            return lhsCentimeters < rhsCentimeters

        case let (.position(lhs), .position(rhs)):
            return lhs < rhs

        case let (.weight(lhs, lhsUnit), .weight(rhs, rhsUnit)):
            let poundsPerKilogram: Double = 2.20462
            let lhsPounds: Double
            let rhsPounds: Double

            switch lhsUnit {
            case .kilograms:
                lhsPounds = Double(lhs) * poundsPerKilogram
            case .pounds:
                lhsPounds = Double(lhs)
            }

            switch rhsUnit {
            case .kilograms:
                rhsPounds = Double(rhs) * poundsPerKilogram
            case .pounds:
                rhsPounds = Double(rhs)
            }

            return lhsPounds < rhsPounds

        default:
            return false
        }
    }
}

extension TeamRosterStat.Value {
    var displayValue: String {
        switch self {
        case let .age(value, _):
            return value.string

        case let .birthDate(value):
            return Date.iso8601ShortFormatter
                .string(from: value)
                .replacingOccurrences(of: "/", with: "-")

        case let .height(value, unit):
            switch unit {
            case .centimeters:
                let metres = Double(value) / 100
                return "\(metres) m"

            case .inches:
                let feet = floor(Double(value) / 12)
                let inches = Double(value) - Double(feet) * Double(12)
                return "\(Int(feet))-\(Int(inches))"
            }

        case let .position(value):
            return value

        case let .weight(value, unit):
            switch unit {
            case .kilograms:
                return "\(value) kg"

            case .pounds:
                return "\(value) lbs"
            }
        }
    }
}
