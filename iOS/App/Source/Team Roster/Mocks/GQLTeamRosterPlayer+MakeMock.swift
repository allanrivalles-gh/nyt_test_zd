//
//  GQLTeamRosterPlayer+MakeMock.swift
//  theathletic-iosTests
//
//  Created by Tim Korotky on 31/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

extension GQL.TeamRosterPlayer {
    static func makeMock(
        id: String,
        birthDate: String? = nil,
        displayName: String? = nil,
        height: Int? = nil,
        jerseyNumber: Int? = nil,
        position: GQL.Position? = nil,
        weight: Int? = nil
    ) -> Self {
        GQL.TeamRosterPlayer(
            id: id,
            birthDate: birthDate,
            displayName: displayName,
            headshots: GQL.PlayerHeadshot
                .makeMockLogos(withPlayerId: id)
                .map { try! .init($0) },
            height: height,
            jerseyNumber: jerseyNumber,
            position: position,
            weight: weight
        )
    }
}
