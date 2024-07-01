//
//  TeamRosterSoccerGroupBuilder.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 31/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

struct TeamRosterSoccerGroupBuilder: TeamRosterGroupBuilder {
    func makeGroups(players: [GQL.TeamRosterPlayer]) -> [Group] {
        let players = players.map { Group.Player(entity: $0) }
        var goalkeeperPlayers: [Group.Player] = []
        var outfieldPlayers: [Group.Player] = []

        players.forEach { player in
            if player.position == .goalkeeper {
                goalkeeperPlayers.append(player)
            } else {
                outfieldPlayers.append(player)
            }
        }

        var groups: [Group] = []

        if !goalkeeperPlayers.isEmpty,
            let group = Group(
                id: "goalkeepers",
                title: Strings.teamRosterGoalkeepersTitle.localized,
                players: goalkeeperPlayers
            )
        {
            groups.append(group)
        }

        if !outfieldPlayers.isEmpty,
            let group = Group(
                id: "outfield-players",
                title: Strings.teamRosterOutfieldPlayersTitle.localized,
                players: outfieldPlayers
            )
        {
            groups.append(group)
        }

        return groups
    }
}
