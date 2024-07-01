//
//  PlayerStatsGroup+TeamRosterStat.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 30/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import Foundation

extension PlayerStatsGroup {
    init?(
        id: String,
        title: String?,
        players: [PlayerStatsGroup.Player]
    ) where Stat == TeamRosterStat {
        var existingTypes: Set<TeamPlayerStatsStatIdentifier> = []
        let uniqueStats: [Stat] =
            players
            .map { $0.stats.values }
            .reduce(into: []) { uniqueStats, playerStats in
                playerStats.forEach {
                    let statIdentifier = $0.statIdentifier

                    guard !existingTypes.contains(statIdentifier) else { return }

                    uniqueStats.append($0)
                    existingTypes.insert(statIdentifier)
                }
            }

        guard !uniqueStats.isEmpty else { return nil }

        let headings = uniqueStats.map {
            PlayerStatsGroup.StatHeading(
                id: $0.statIdentifier,
                label: $0.shortLabel
            )
        }
        self.id = id
        self.title = title
        self.players = players
        self.stats = headings
    }
}

extension PlayerStatsGroup.Player {
    init(
        entity: GQL.TeamRosterPlayer,
        timeSettings: TimeSettings = SystemTimeSettings()
    ) where Stat == TeamRosterStat {
        var stats: [TeamRosterStat] = []

        let weightUnit: WeightUnit
        let heightUnit: HeightUnit

        /// Opta (soccer) is metric, Sportsrader (everything else) is imperial. This would be better improved with a backend
        /// property in the future, but for now we make lemonade.
        switch entity.sport {
        case .soccer:
            weightUnit = .kilograms
            heightUnit = .centimeters

        default:
            weightUnit = .pounds
            heightUnit = .inches
        }

        if let position = entity.position?.abbreviation {
            stats.append(
                TeamRosterStat(
                    id: "\(entity.id)-position-stat-id",
                    type: "position",
                    shortLabel: Strings.teamRosterPositionAbbreviation.localized,
                    value: .position(position)
                )
            )
        }

        if let height = entity.height {
            stats.append(
                TeamRosterStat(
                    id: "\(entity.id)-height-stat-id",
                    type: "height",
                    shortLabel: Strings.teamRosterHeightAbbreviation.localized,
                    value: .height(height, heightUnit)
                )
            )
        }

        if let weight = entity.weight {
            stats.append(
                TeamRosterStat(
                    id: "\(entity.id)-weight-stat-id",
                    type: "weight",
                    shortLabel: Strings.teamRosterWeightAbbreviation.localized,
                    value: .weight(weight, weightUnit)
                )
            )
        }

        if let birthDate = entity.birthDate.flatMap(Date.userEndDateShortFormatter.date) {
            let components = timeSettings.calendar
                .dateComponents([.year, .day], from: birthDate, to: timeSettings.now())

            stats.append(
                contentsOf: [
                    TeamRosterStat(
                        id: "\(entity.id)-date-of-birth-stat-id",
                        type: "date-of-birth",
                        shortLabel: Strings.teamRosterDateOfBirthAbbreviation.localized,
                        value: .birthDate(birthDate)
                    ),
                    components.year.map {
                        TeamRosterStat(
                            id: "\(entity.id)-age-stat-id",
                            type: "age",
                            shortLabel: Strings.teamRosterAgeAbbreviation.localized,
                            value: .age($0, components.day ?? 0)
                        )
                    },
                ].compactMap { $0 }
            )
        }

        self.id = entity.id
        self.name = entity.displayName
        self.jerseyNumber = entity.jerseyNumber
        self.headshots = entity.headshots.map { $0.fragments.playerHeadshot }
        self.stats = stats.orderedDictionary(indexedBy: \.statIdentifier)
        self.position = entity.position
    }
}

extension TeamRosterStat {
    fileprivate var statIdentifier: TeamPlayerStatsStatIdentifier {
        TeamPlayerStatsStatIdentifier(category: nil, statType: type)
    }
}
