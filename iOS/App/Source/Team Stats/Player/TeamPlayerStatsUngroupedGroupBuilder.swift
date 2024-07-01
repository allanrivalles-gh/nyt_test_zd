//
//  TeamPlayerStatsUngroupedGroupBuilder.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 23/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

struct TeamPlayerStatsUngroupedGroupBuilder {
    typealias Group = PlayerStatsGroup<GameStat>

    static func makeGroups(from players: [GQL.SeasonStatsPlayer]) -> [Group] {
        let group = Group(
            id: TeamPlayerStatsSection.allPlayers.rawValue,
            title: nil,
            players:
                players
                .lazy
                .filter {
                    guard let seasonStats = $0.playerSeasonStats?.seasonStats else { return false }

                    return !seasonStats.isEmpty
                }
                .map(Group.Player.init),
            category: nil
        )
        return [group].compactMap { $0 }
    }
}
