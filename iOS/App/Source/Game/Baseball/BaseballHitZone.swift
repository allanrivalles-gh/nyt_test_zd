//
//  BaseballHitZone.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 20/6/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

struct BaseballHitZone {
    let zone: Int

    /// Returns a position in a 20x20 grid based on the specified zone, defined in Figma.
    ///
    /// For zones see https://developer.sportradar.com/files/MLBHitZones.png
    var position: CGPoint {
        switch zone {
        case 1:
            return CGPoint(x: -2, y: 5)
        case 2:
            return CGPoint(x: 2, y: 0)
        case 3:
            return CGPoint(x: 8, y: -2)
        case 4:
            return CGPoint(x: 14, y: 0)
        case 5:
            return CGPoint(x: 18, y: 5)

        case 6:
            return CGPoint(x: 0, y: 7)
        case 7:
            return CGPoint(x: 3, y: 2)
        case 8:
            return CGPoint(x: 8, y: 0)
        case 9:
            return CGPoint(x: 13, y: 2)
        case 10:
            return CGPoint(x: 16, y: 7)

        case 11:
            return CGPoint(x: 1, y: 8)
        case 12:
            return CGPoint(x: 4, y: 4)
        case 13:
            return CGPoint(x: 8, y: 2)
        case 14:
            return CGPoint(x: 12, y: 4)
        case 15:
            return CGPoint(x: 15, y: 8)

        case 16:
            return CGPoint(x: 2, y: 9)
        case 17:
            return CGPoint(x: 5, y: 6)
        case 18:
            return CGPoint(x: 8, y: 5)
        case 19:
            return CGPoint(x: 11, y: 6)
        case 20:
            return CGPoint(x: 14, y: 9)

        case 21:
            return CGPoint(x: 4, y: 11)
        case 22:
            return CGPoint(x: 6, y: 9)
        case 23:
            return CGPoint(x: 10, y: 9)
        case 24:
            return CGPoint(x: 12, y: 11)
        case 25:
            return CGPoint(x: 6, y: 12)

        case 26:
            return CGPoint(x: 10, y: 12)
        case 27:
            return CGPoint(x: 8, y: 13)
        case 28:
            return CGPoint(x: 6, y: 14)
        case 29:
            return CGPoint(x: 8, y: 15)
        case 30:
            return CGPoint(x: 10, y: 14)

        case 31:
            return CGPoint(x: 0, y: 13)
        case 32:
            return CGPoint(x: 3, y: 16)
        case 33:
            return CGPoint(x: 8, y: 18)
        case 34:
            return CGPoint(x: 13, y: 16)
        case 35:
            return CGPoint(x: 16, y: 13)

        default:
            assertionFailure("Encountered unhandled zone \(zone)")
            return CGPoint(x: 0, y: 0)
        }
    }
}
