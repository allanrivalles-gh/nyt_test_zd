//
//  TeamStatsLeadersViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 22/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticUI
import Foundation

struct TeamSeasonStatsLeadersViewModel: Identifiable {
    struct Section: Identifiable {
        let id: String
        let title: String?
        let items: [Item]
        let hasHeadshots: Bool

        init(
            id: String,
            title: String?,
            items: [Item]
        ) {
            self.id = id
            self.title = title?.uppercased()
            self.items = items
            self.hasHeadshots = !items.lazy
                .flatMap { $0.iconResources }
                .isEmpty
        }
    }

    struct Item: Identifiable, Analytical {
        struct Value {
            let text: String
            let subtext: String?
        }

        let id: String
        let playerId: String
        let teamId: String
        let iconResources: [ATHImageResource]
        let iconHex: String?
        let title: Value
        let value: Value

        var analyticData: AnalyticData {
            AnalyticData(
                click: AnalyticsEventRecord(
                    verb: .click,
                    view: .stats,
                    element: .team,
                    objectType: .playerId,
                    objectIdentifier: playerId,
                    metaBlob: .init(
                        parentObjectType: .teamId,
                        parentObjectIdentifier: teamId
                    )
                )
            )
        }
    }

    let id: String
    let title: String
    let sections: [Section]
}

extension TeamSeasonStatsLeadersViewModel {
    init?(id: String, teamId: String, entity: GQL.StatLeadersTeam) {
        guard !entity.statLeaders.isEmpty else { return nil }

        self.id = id
        self.title = Strings.teamLeadersTitle.localized
        self.sections = entity.statLeaders.map { category in
            Section(
                id: category.id,
                title: category.statsCategory,
                items: category.leaders.compactMap { leader in
                    guard
                        let stat = leader.stats.first
                            .flatMap({ GameStat($0.fragments.gameStat) })
                    else {
                        return nil
                    }

                    let player = leader.player

                    return Item(
                        id: leader.id,
                        playerId: leader.player.id,
                        teamId: teamId,
                        iconResources: player.headshots
                            .map { ATHImageResource(entity: $0.fragments.playerHeadshot) },
                        iconHex: entity.colorPrimary,
                        title: Item.Value(
                            text: player.displayName ?? "-",
                            subtext: player.position?.abbreviation
                        ),
                        value: Item.Value(
                            text: stat.value.displayValue,
                            subtext:
                                leader.statsShortLabel
                                ?? stat.shortLabel
                                ?? stat.label
                        )
                    )
                }
            )
        }
    }
}
