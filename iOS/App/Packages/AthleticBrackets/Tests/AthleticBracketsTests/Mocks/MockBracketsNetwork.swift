//
//  MockBracketsNetwork.swift
//
//
//  Created by Mark Corbyn on 13/7/2023.
//

import AthleticApolloTypes
import AthleticBrackets
import Combine
import Foundation

final class MockBracketsNetwork: BracketsNetworking {

    func fetchTournament(
        leagueCode: AthleticApolloTypes.GQL.LeagueCode,
        seasonId: String?
    ) async throws -> GQL.GetTournamentQuery.Data {
        return .init(
            getTournament: .init(
                id: "mock-tournament",
                extraStages: [],
                stages: []
            )
        )
    }

    func subscribeToTournamentGameUpdates(
        forIds ids: Set<String>
    ) -> AnyPublisher<GQL.TournamentGameUpdatesSubscription.Data.LiveScoreUpdate, Never> {
        Just(
            GQL.TournamentGameUpdatesSubscription.Data.LiveScoreUpdate(unsafeResultMap: [:])
        ).eraseToAnyPublisher()
    }

}
