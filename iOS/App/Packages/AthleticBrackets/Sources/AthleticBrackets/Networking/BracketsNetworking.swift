//
//  BracketsNetworking.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 8/27/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Apollo
import AthleticApolloNetworking
import AthleticApolloTypes
import AthleticFoundation
import AthleticRestNetwork
import Combine
import Foundation

public protocol BracketsNetworking {

    /// Queries
    func fetchTournament(leagueCode: GQL.LeagueCode, seasonId: String?) async throws
        -> GQL.GetTournamentQuery.Data

    /// Subscriptions

    func subscribeToTournamentGameUpdates(
        forIds ids: Set<String>
    ) -> AnyPublisher<GQL.TournamentGameUpdatesSubscription.Data.LiveScoreUpdate, Never>
}

extension NetworkModel: BracketsNetworking {

    // MARK: - Queries

    public func fetchTournament(
        leagueCode: GQL.LeagueCode,
        seasonId: String?
    ) async throws -> GQL.GetTournamentQuery.Data {
        let query = GQL.GetTournamentQuery(league_code: leagueCode, season_id: seasonId)
        return try await graphFetch(query: query, cachePolicy: .fetchIgnoringCacheData)
    }

    // MARK: - Subscriptions

    public func subscribeToTournamentGameUpdates(
        forIds ids: Set<String>
    ) -> AnyPublisher<GQL.TournamentGameUpdatesSubscription.Data.LiveScoreUpdate, Never> {
        subscriptionsManager.tournamentGames
            .updates(forIds: ids)
            .eraseToAnyPublisher()
    }
}
