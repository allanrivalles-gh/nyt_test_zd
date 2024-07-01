//
//  BaseballPitchZone.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 22/6/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

/// Maps a backend defined zone to a pitch position. The backend delivers us zones that sit on this grid:
///     `[131, 101, 102, 103, 111]`
///     `[132,  1,   2,   3,  112]`
///     `[133,  4,   5,   6,  113]`
///     `[134,  7,   8,   9,  114]`
///     `[135, 121, 122, 123, 115]`
///
/// Which maps to the grid defined in Figma:
///     `[1,  2,  3,  4,  5 ]`
///     `[6,  7,  8,  9,  10]`
///     `[11, 12, 13, 14, 15]`
///     `[16, 17, 18, 19, 20]`
///     `[21, 22, 23, 24, 25]`
///
/// This object maps directly from the backend zones to the positions that result from them.
struct BaseballPitchZone {
    let zone: Int

    var position: CGPoint {
        switch zone {
        /// First row
        case 131:
            return CGPoint(x: 0, y: 0)
        case 101:
            return CGPoint(x: 5, y: 0)
        case 102:
            return CGPoint(x: 8, y: 0)
        case 103:
            return CGPoint(x: 11, y: 0)
        case 111:
            return CGPoint(x: 16, y: 0)

        /// Second row
        case 132:
            return CGPoint(x: 0, y: 5)
        case 1:
            return CGPoint(x: 5, y: 5)
        case 2:
            return CGPoint(x: 8, y: 5)
        case 3:
            return CGPoint(x: 11, y: 5)
        case 112:
            return CGPoint(x: 16, y: 5)

        /// Third row
        case 133:
            return CGPoint(x: 0, y: 8)
        case 4:
            return CGPoint(x: 5, y: 8)
        case 5:
            return CGPoint(x: 8, y: 8)
        case 6:
            return CGPoint(x: 11, y: 8)
        case 113:
            return CGPoint(x: 16, y: 8)

        /// Fourth row
        case 134:
            return CGPoint(x: 0, y: 11)
        case 7:
            return CGPoint(x: 5, y: 11)
        case 8:
            return CGPoint(x: 8, y: 11)
        case 9:
            return CGPoint(x: 11, y: 11)
        case 114:
            return CGPoint(x: 16, y: 11)

        /// Fifth row
        case 135:
            return CGPoint(x: 0, y: 16)
        case 121:
            return CGPoint(x: 5, y: 16)
        case 122:
            return CGPoint(x: 8, y: 16)
        case 123:
            return CGPoint(x: 11, y: 16)
        case 115:
            return CGPoint(x: 16, y: 16)

        default:
            assertionFailure("Encountered unhandled zone \(zone)")
            return CGPoint(x: 0, y: 0)
        }
    }
}
