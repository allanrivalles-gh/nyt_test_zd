//
//  TeamRosterGroupBuilder.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 31/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

protocol TeamRosterGroupBuilder {
    typealias Group = PlayerStatsGroup<TeamRosterStat>

    func makeGroups(players: [GQL.TeamRosterPlayer]) -> [Group]
}
