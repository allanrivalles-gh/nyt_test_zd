//
//  GQLTournamentSeries+MakeMock.swift
//
//
//  Created by Duncan Lau on 28/9/2023.
//

import AthleticApolloTypes
import Foundation

extension GQL.TournamentSeries {
    static func makeMock(
        seriesTitle: SeriesTitle? = nil,
        homeTeamId: String = "home-team-531",
        games: [GQL.TournamentGame] = []
    ) -> Self {
        .init(
            id: "mock",
            seriesTitle: seriesTitle,
            isLive: false,
            homeTeam: try! .init(
                jsonObject: GQL.TournamentTeam(id: homeTeamId, logos: []).jsonObject
            ),
            games: games.map {
                try! GQL.TournamentSeries.Game(jsonObject: $0.jsonObject)
            }
        )
    }
}
