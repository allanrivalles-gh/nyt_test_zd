//
//  TeamPlayerStatsHockeyGroupBuilder.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 25/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

struct TeamPlayerStatsHockeyGroupBuilder {
    typealias Group = PlayerStatsGroup<GameStat>

    static func makeGroups(from players: [GQL.SeasonStatsPlayer]) -> [Group] {
        let players =
            players
            .lazy
            .filter {
                guard let seasonStats = $0.playerSeasonStats?.seasonStats else { return false }

                return !seasonStats.isEmpty
            }
            .map(Group.Player.init)
        var skatingPlayers: [Group.Player] = []
        var goaltendingPlayers: [Group.Player] = []

        players.forEach { player in
            if player.position == .goalie {
                goaltendingPlayers.append(player)
            } else {
                skatingPlayers.append(player)
            }
        }

        var groups: [Group] = []

        if !skatingPlayers.isEmpty,
            let group = Group(
                id: TeamPlayerStatsSection.skatingPlayers.rawValue,
                title: Strings.playerStatsSkatingPlayersTitle.localized,
                players: skatingPlayers
            )
        {
            groups.append(group)
        }

        if !goaltendingPlayers.isEmpty,
            let group = Group(
                id: TeamPlayerStatsSection.goaltendingPlayers.rawValue,
                title: Strings.playerStatsGoaltendingPlayersTitle.localized,
                players: goaltendingPlayers
            )
        {
            groups.append(group)
        }

        return groups
    }
}
