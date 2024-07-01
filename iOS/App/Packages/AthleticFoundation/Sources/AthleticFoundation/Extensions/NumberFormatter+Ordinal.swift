//
//  NumberFormatter+Ordinal.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 8/10/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

extension NumberFormatter {
    public static let ordinal: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.numberStyle = .ordinal
        return formatter
    }()
}
