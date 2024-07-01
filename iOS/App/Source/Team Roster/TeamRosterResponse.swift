//
//  TeamRosterResponse.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 22/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

struct TeamRosterResponse {
    let team: GQL.PlayerStatsTeam?
    let players: [GQL.TeamRosterPlayer]?
}
