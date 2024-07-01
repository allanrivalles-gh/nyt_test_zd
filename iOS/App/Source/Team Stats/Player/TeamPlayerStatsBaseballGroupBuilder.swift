//
//  TeamPlayerStatsBaseballGroupBuilder.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 23/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

struct TeamPlayerStatsBaseballGroupBuilder {
    typealias Group = PlayerStatsGroup<GameStat>

    static func makeGroups(from players: [GQL.SeasonStatsPlayer]) -> [Group] {
        let players = players.map(Group.Player.init)
        var battingPlayers: [Group.Player] = []
        var pitchingPlayers: [Group.Player] = []

        players.forEach { player in
            let statCategories = Set(player.stats.values.map { $0.category })
            if statCategories.contains(.batting) {
                battingPlayers.append(player)
            }
            if statCategories.contains(.pitching) {
                pitchingPlayers.append(player)
            }
        }

        var groups: [Group] = []

        if !battingPlayers.isEmpty,
            let group = Group(
                id: GQL.GameStatCategory.batting.rawValue,
                title: GQL.GameStatCategory.batting.title,
                players: battingPlayers,
                category: .batting
            )
        {
            groups.append(group)
        }

        if !pitchingPlayers.isEmpty,
            let group = Group(
                id: GQL.GameStatCategory.pitching.rawValue,
                title: GQL.GameStatCategory.pitching.title,
                players: pitchingPlayers,
                category: .pitching
            )
        {
            groups.append(group)
        }

        return groups
    }
}
