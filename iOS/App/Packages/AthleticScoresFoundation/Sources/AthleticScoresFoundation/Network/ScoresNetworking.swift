//
//  ScoresNetworking.swift
//
//  Created by Mark Corbyn on 19/5/2023.
//

import AthleticApolloTypes
import Foundation

public protocol ScoresNetworking {

    /// Queries

    func fetchScoresLandingFeed(timeZone: String) async throws
        -> GQL.ScoresLandingFeedQuery.Data

    func fetchScoresFeedGroups(
        timeZone: String,
        groupingId: String,
        filterIds: [String]
    ) async throws -> GQL.ScoresFeedGroupsQuery.Data

    func fetchTeamScheduleFeed(timeZone: String, teamId: String) async throws
        -> GQL.ScheduleFeedQuery.Data

    func fetchLeagueScheduleFeed(timeZone: String, leagueCode: GQL.LeagueCode) async throws
        -> GQL.ScheduleFeedQuery.Data

    func fetchTeamStandings(teamId: String) async throws
        -> GQL.TeamStandingsQuery.Data

    func fetchLeagueStandings(leagueCode: GQL.LeagueCode) async throws
        -> GQL.LeagueStandingsQuery.Data

    func fetchSeasonStandings(seasonId: String) async throws
        -> GQL.SeasonStandingsQuery.Data

    func fetchGame(id: String) async throws -> GQL.GameV2Lite

    /// Simulation

    func startGameReplay(id: String) async throws -> GQL.StartGameReplayMutation.Data

    func startGameSimulation(withDelay delay: Int) async throws -> String
}
