//
//  Int+Extensions.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 18/6/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

extension Int {

    public var string: String {
        "\(self)"
    }

    /// Compute the `n`th number in the Fibonacci sequence
    /// https://en.wikipedia.org/wiki/Fibonacci_number
    ///  0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55...
    ///
    /// - Parameter n: Position in the sequence
    /// - Returns: Number at that position
    public static func fibonacci(_ n: Int) -> Int {
        guard n > 1 else {
            return n
        }
        var fibs: [Int] = [0, 1]
        (2...n).forEach { i in
            fibs.append(fibs[i - 1] + fibs[i - 2])
        }
        return fibs.last!
    }

    public var milliseconds: TimeInterval { return TimeInterval(self) * 0.001 }
    public var millisecond: TimeInterval { return self.milliseconds }

    public var seconds: TimeInterval { return TimeInterval(self) * 1.milliseconds * 1000 }
    public var second: TimeInterval { return self.seconds }

    public var minutes: TimeInterval { return TimeInterval(self) * 60.seconds }
    public var minute: TimeInterval { return self.minutes }

    public var hours: TimeInterval { return TimeInterval(self) * 60.minutes }
    public var hour: TimeInterval { return self.hours }

    public var days: TimeInterval { return TimeInterval(self) * 24.hours }
    public var day: TimeInterval { return self.days }

    public var years: TimeInterval { return TimeInterval(self) * 365.days }
    public var year: TimeInterval { return self.years }

}
