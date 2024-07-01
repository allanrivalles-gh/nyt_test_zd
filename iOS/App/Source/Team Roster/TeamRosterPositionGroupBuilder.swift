//
//  TeamRosterPositionGroupBuilder.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 23/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import Foundation

struct TeamRosterPositionGroupBuilder: TeamRosterGroupBuilder {
    struct Filter {
        typealias ID = String

        let id: ID
        let title: String
        let positions: [GQL.Position]
    }

    private let filters: [Filter]

    init(filters: [Filter]) {
        self.filters = filters
    }

    func makeGroups(players: [GQL.TeamRosterPlayer]) -> [Group] {
        var positionsMap: [GQL.Position: Filter.ID] = [:]
        var filterBuckets: [Filter.ID: [Group.Player]] = [:]

        filters.forEach { filter in
            filterBuckets[filter.id] = []
            filter.positions.forEach {
                positionsMap[$0] = filter.id
            }
        }

        let players = players.map { Group.Player(entity: $0) }

        players.forEach { player in
            guard
                let position = player.position,
                let filterId = positionsMap[position]
            else {
                let message =
                    "Team roster encountered unhandled player type \(String(describing: player.position)) for player \(player.id)"
                ATHLogger(category: .hub).warning(message)
                assertionFailure(message)
                return
            }

            filterBuckets[filterId]?.append(player)
        }

        return filters.compactMap { filter in
            guard
                let players = filterBuckets[filter.id],
                !players.isEmpty
            else {
                return nil
            }

            return Group(
                id: filter.id,
                title: filter.title,
                players: players
            )
        }
    }
}
