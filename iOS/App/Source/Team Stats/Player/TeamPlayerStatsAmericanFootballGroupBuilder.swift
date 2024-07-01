//
//  TeamPlayerStatsAmericanFootballGroupBuilder.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 25/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation
import OrderedCollections

struct TeamPlayerStatsAmericanFootballGroupBuilder {
    typealias Group = PlayerStatsGroup<GameStat>

    static func makeGroups(from players: [GQL.SeasonStatsPlayer]) -> [Group] {
        struct CategoryGroup {
            let category: GQL.GameStatCategory
            let players: [Group.Player]
        }

        let players = players
            .lazy
            .filter {
                guard let seasonStats = $0.playerSeasonStats?.seasonStats else { return false }

                return !seasonStats.isEmpty
            }
            .map(Group.Player.init)
        var categoryGroups: OrderedDictionary<GQL.GameStatCategory, CategoryGroup> = [:]

        players.forEach { player in
            let categories = OrderedSet(player.stats.values.compactMap { $0.category })

            categories.forEach { category in
                if let group = categoryGroups[category] {
                    categoryGroups[category] = CategoryGroup(
                        category: category,
                        players: group.players + [player]
                    )
                } else {
                    categoryGroups[category] = CategoryGroup(
                        category: category,
                        players: [player]
                    )
                }
            }
        }

        return categoryGroups.values
            .sorted { $0.category.sortOrder < $1.category.sortOrder }
            .compactMap { group in
                Group(
                    id: group.category.rawValue,
                    title: group.category.title,
                    players: group.players,
                    category: group.category
                )
            }
    }
}

extension GQL.GameStatCategory {
    fileprivate var sortOrder: Int {
        let categories = [
            GQL.GameStatCategory.passing,
            .rushing,
            .receiving,
            .defense,
            .kicking,
            .punts,
            .kickReturns,
            .puntReturns,
        ]
        return categories.firstIndex(of: self) ?? .max
    }
}
