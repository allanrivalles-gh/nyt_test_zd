//
//  NumberFormatter+Thousands.swift
//
//  Created by Mark Corbyn on 3/7/2023.
//

import Foundation

extension NumberFormatter {
    public static let thousands: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        return formatter
    }()
}
