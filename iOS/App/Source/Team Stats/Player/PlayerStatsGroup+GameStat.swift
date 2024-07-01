//
//  PlayerStatsGroup+GameStat.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 30/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

extension PlayerStatsGroup {
    init?(
        id: String,
        title: String?,
        players: [PlayerStatsGroup.Player],
        category: GQL.GameStatCategory? = nil
    ) where Stat == GameStat {
        let noCategoryFiltering = category == nil
        var existingTypes: Set<TeamPlayerStatsStatIdentifier> = []
        let uniqueStats: [Stat] =
            players
            .map { $0.stats.values }
            .reduce(into: []) { uniqueStats, playerStats in
                playerStats.forEach {
                    let statIdentifier = $0.statIdentifier

                    guard !existingTypes.contains(statIdentifier) else { return }

                    /// Some stats have no category and belong in all categories. An example of this is games played in
                    /// American football.
                    let isWildCard = $0.category == nil
                    let matchesCategory = $0.category == category

                    guard noCategoryFiltering || isWildCard || matchesCategory else { return }

                    uniqueStats.append($0)
                    existingTypes.insert(statIdentifier)
                }
            }

        guard !uniqueStats.isEmpty else { return nil }

        let headings = uniqueStats.map {
            PlayerStatsGroup.StatHeading(
                id: $0.statIdentifier,
                label: $0.shortLabel ?? $0.label
            )
        }
        self.id = id
        self.title = title
        self.players = players
        self.stats = headings
    }
}

extension PlayerStatsGroup.Player {
    /// An initializer that converts a GQL player entity into a model. As part of this a position stat will be prepended to the stats array in
    /// order for this to be fed into the table generation.
    init(entity: GQL.SeasonStatsPlayer) where Stat == GameStat {

        let playerSeasonStats = entity.playerSeasonStats?.seasonStats ?? []

        var gameStats =
            playerSeasonStats
            .map { $0.fragments.gameStat }
            .gameStats

        if let position = entity.position?.abbreviation {
            let positionStat = GameStat(
                id: "\(entity.id)-position-stat-id",
                type: "position",
                category: nil,
                longLabel: "Position",
                shortLabel: "POS",
                label: "",
                value: .string(position),
                isLessBest: false,
                parentIdentifier: nil
            )
            gameStats.insert(positionStat, at: 0)
        }

        self.id = entity.id
        self.name = entity.displayName
        self.jerseyNumber = entity.jerseyNumber
        self.headshots = entity.headshots.map { $0.fragments.playerHeadshot }
        self.stats = gameStats.orderedDictionary(
            indexedBy: {
                $0.statIdentifier
            }
        )
        self.position = entity.position
    }
}

extension GameStat {
    fileprivate var statIdentifier: TeamPlayerStatsStatIdentifier {
        TeamPlayerStatsStatIdentifier(category: category?.rawValue, statType: type)
    }
}
