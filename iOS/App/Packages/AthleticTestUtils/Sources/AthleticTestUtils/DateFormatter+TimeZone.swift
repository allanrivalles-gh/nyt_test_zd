//
//  DateFormatter+TimeZone.swift
//
//
//  Created by Mark Corbyn on 2/1/2023.
//

import Foundation

extension DateFormatter {

    public func configure(calendar: Calendar, timeZone: TimeZone) {
        self.calendar = calendar
        self.timeZone = timeZone
    }

    public func configureWithSystemSettings() {
        calendar = .current
        timeZone = .current
    }

}
