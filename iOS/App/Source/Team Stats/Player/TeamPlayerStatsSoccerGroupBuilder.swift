//
//  TeamPlayerStatsSoccerGroupBuilder.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 25/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

struct TeamPlayerStatsSoccerGroupBuilder {
    typealias Group = PlayerStatsGroup<GameStat>

    static func makeGroups(from players: [GQL.SeasonStatsPlayer]) -> [Group] {
        let players = players
            .lazy
            .filter {
                guard let seasonStats = $0.playerSeasonStats?.seasonStats else { return false }

                return !seasonStats.isEmpty
            }
            .map(Group.Player.init)
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
                id: TeamPlayerStatsSection.goalkeeperPlayers.rawValue,
                title: Strings.playerStatsGoalkeeperPlayersTitle.localized,
                players: goalkeeperPlayers
            )
        {
            groups.append(group)
        }

        if !outfieldPlayers.isEmpty,
            let group = Group(
                id: TeamPlayerStatsSection.outfieldPlayers.rawValue,
                title: Strings.playerStatsOutfieldPlayersTitle.localized,
                players: outfieldPlayers
            )
        {
            groups.append(group)
        }

        return groups
    }
}
