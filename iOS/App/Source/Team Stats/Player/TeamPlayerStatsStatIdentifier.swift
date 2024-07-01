//
//  TeamPlayerStatsStatIdentifier.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 25/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation

/// An identifier to uniquely identify game stat objects. Players can have multiple stats with the same stat type belonging to different
/// categories. An example of this is yards in American football.
struct TeamPlayerStatsStatIdentifier: Hashable {
    typealias CategoryType = String
    typealias StatType = String

    let category: CategoryType?
    let statType: StatType
}
