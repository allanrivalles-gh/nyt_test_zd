//
//  GameIdentifier.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 24/5/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import Foundation

public struct GameIdentifier: Hashable {
    public let leagueCode: GQL.LeagueCode
    public let gameId: String

    public init(leagueCode: GQL.LeagueCode, gameId: String) {
        self.leagueCode = leagueCode
        self.gameId = gameId
    }
}

extension GameIdentifier {

    public var sportType: SportType {
        leagueCode.sportType
    }

    public var isSoccer: Bool {
        sportType == .soccer
    }
}
