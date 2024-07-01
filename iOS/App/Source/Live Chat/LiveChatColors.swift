//
//  LiveChatColors.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 8/3/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

enum LiveChatColors: String, CaseIterable {
    case userId0 = "#403C5C"
    case userId1 = "#1C3C64"
    case userId2 = "#497AB8"
    case userId3 = "#105E5E"
    case userId4 = "#3C5634"
    case userId5 = "#F89A1E"
    case userId6 = "#E95F33"
    case userId7 = "#CB3939"
    case userId8 = "#943848"
    case userId9 = "#969693"

    static var colors: [Color] {
        allCases.compactMap { Color(hex: $0.rawValue) }
    }

    static func color(forId id: String) -> Color {
        guard let lastIndex = id.last?.wholeNumberValue else {
            return .clear
        }

        return colors[lastIndex]
    }
}
