//
//  TeamPlayerStatsViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 22/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import Foundation

final class TeamPlayerStatsViewModel: Identifiable {
    typealias Table = PlayerStatsTableViewModel<GameStat>

    let id: String
    let table: Table

    private let teamId: String
    private let teamColor: String?

    @MainActor
    convenience init(
        id: String,
        sport: SportType?,
        entities: [GQL.SeasonStatsPlayer],
        teamId: String,
        teamColor: String?
    ) {
        let gqlGroups = Self.makeGroups(entities: entities, sport: sport)
        let rowGroups = gqlGroups.makeRowGroups(teamId: teamId, teamColor: teamColor)
        let table = Table(
            gqlGroups: gqlGroups,
            rowGroups: rowGroups,
            activeSorting: sport?.defaultSorting ?? [:]
        )
        self.init(
            id: id,
            table: table,
            teamId: teamId,
            teamColor: teamColor
        )
    }

    @MainActor
    convenience init(
        viewModel: TeamPlayerStatsViewModel,
        sport: SportType?,
        entities: [GQL.SeasonStatsPlayer]
    ) {
        let gqlGroups = Self.makeGroups(entities: entities, sport: sport)
        let rowGroups = gqlGroups.makeRowGroups(
            teamId: viewModel.teamId,
            teamColor: viewModel.teamColor
        )
        let table = Table(
            gqlGroups: gqlGroups,
            rowGroups: rowGroups,
            oldTable: viewModel.table
        )
        self.init(
            id: viewModel.id,
            table: table,
            teamId: viewModel.teamId,
            teamColor: viewModel.teamColor
        )
    }

    private init(
        id: String,
        table: PlayerStatsTableViewModel<GameStat>,
        teamId: String,
        teamColor: String?
    ) {
        self.id = id
        self.table = table
        self.teamId = teamId
        self.teamColor = teamColor
    }

    private static func makeGroups(
        entities: [GQL.SeasonStatsPlayer],
        sport: SportType?
    ) -> [PlayerStatsGroup<GameStat>] {
        switch sport {
        case .americanFootball:
            return TeamPlayerStatsAmericanFootballGroupBuilder.makeGroups(from: entities)
        case .baseball:
            return TeamPlayerStatsBaseballGroupBuilder.makeGroups(from: entities)
        case .hockey:
            return TeamPlayerStatsHockeyGroupBuilder.makeGroups(from: entities)
        case .soccer:
            return TeamPlayerStatsSoccerGroupBuilder.makeGroups(from: entities)
        default:
            return TeamPlayerStatsUngroupedGroupBuilder.makeGroups(from: entities)
        }
    }
}

extension Array where Element == PlayerStatsGroup<GameStat> {
    func makeRowGroups(
        teamId: String,
        teamColor: String?
    ) -> PlayerStatsTableViewModel<GameStat>.RowGroups {
        reduce(into: [:]) { result, group in
            let rowMap = group.players.reduce(into: [:]) { result, player in
                result[player.id] = PlayerStatsTableViewModel.Row(
                    model: player,
                    teamId: teamId,
                    teamColor: teamColor,
                    clickAnalyticsView: .stats
                )
            }
            result[group.id] = rowMap
        }
    }
}

// MARK: - Default Sorting Mapping

extension SportType {
    fileprivate struct SortingMap<Category: Hashable, StatType> {
        let map: [Category: (category: Category?, statType: StatType)]
    }

    fileprivate var defaultSorting: TeamPlayerStatsViewModel.Table.ActiveSorting {
        switch self {
        case .americanFootball:
            return americanFootballSorting.activeSorting
        case .baseball:
            return baseballSorting.activeSorting
        case .basketball:
            return basketballSorting.activeSorting
        case .hockey:
            return hockeySorting.activeSorting
        case .soccer:
            return soccerSorting.activeSorting
        default:
            return [:]
        }
    }

    private var americanFootballSorting:
        SortingMap<GQL.GameStatCategory, AmericanFootballGameStatType>
    {
        .init(map: [
            .passing: (.passing, .yards),
            .rushing: (.rushing, .yards),
            .receiving: (.receiving, .yards),
            .defense: (.defense, .tackles),
            .kicking: (.kicking, .fieldGoalsMade),
            .punts: (.punts, .attempts),
            .kickReturns: (.kickReturns, .returns),
            .puntReturns: (.puntReturns, .returns),
        ])
    }

    private var baseballSorting: SortingMap<GQL.GameStatCategory, BaseballGameStatType> {
        .init(map: [
            .batting: (.batting, .atBat),
            .pitching: (.pitching, .gamesPlayed),
        ])
    }

    private var basketballSorting: SortingMap<TeamPlayerStatsSection, BasketballGameStatType> {
        .init(map: [.allPlayers: (nil, .totalGamesStarted)])
    }

    private var hockeySorting: SortingMap<TeamPlayerStatsSection, HockeyGameStatType> {
        .init(map: [
            .skatingPlayers: (nil, .totalPoints),
            .goaltendingPlayers: (nil, .totalGamesPlayed),
        ])
    }

    private var soccerSorting: SortingMap<TeamPlayerStatsSection, SoccerGameStatType> {
        .init(map: [
            .goalkeeperPlayers: (nil, .gamesPlayed),
            .outfieldPlayers: (nil, .gamesPlayed),
        ])
    }
}

extension SportType.SortingMap
where
    Category: RawRepresentable,
    Category.RawValue == String,
    StatType: RawRepresentable,
    StatType.RawValue == String
{
    var activeSorting: TeamPlayerStatsViewModel.Table.ActiveSorting {
        map.reduce(into: [:]) { result, entry in
            result[entry.key.rawValue] = TeamPlayerStatsViewModel.Table.SectionSorting(
                id: entry.key.rawValue,
                statId: TeamPlayerStatsStatIdentifier(
                    category: entry.value.category?.rawValue,
                    statType: entry.value.statType.rawValue
                ),
                order: .descending
            )
        }
    }
}
