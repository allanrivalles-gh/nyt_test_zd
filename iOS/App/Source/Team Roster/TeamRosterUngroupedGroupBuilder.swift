//
//  TeamRosterUngroupedGroupBuilder.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 30/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

struct TeamRosterUngroupedGroupBuilder: TeamRosterGroupBuilder {
    func makeGroups(players: [GQL.TeamRosterPlayer]) -> [Group] {
        let group = Group(
            id: TeamRosterSection.allPlayers.rawValue,
            title: nil,
            players: players.map { Group.Player(entity: $0) }
        )
        return [group].compactMap { $0 }
    }
}
