//
//  GQLTournamentGame+MakeMock.swift
//
//
//  Created by Duncan Lau on 28/9/2023.
//

import AthleticApolloTypes
import Foundation

extension GQL.TournamentGame {
    static func makeMockSoccerGame(
        scheduledAt: Date,
        status: GQL.GameStatusCode,
        tickets: GQL.GameTickets?
    ) -> Self {
        GQL.TournamentGame.makeSoccerGame(
            id: "0",
            scheduledAt: scheduledAt,
            status: status,
            sport: .soccer,
            venue: .init(name: "Stadium Name"),
            tickets: tickets != nil ? try! .init(jsonObject: tickets!.jsonObject) : nil,
            keyEvents: []
        )
    }
}
