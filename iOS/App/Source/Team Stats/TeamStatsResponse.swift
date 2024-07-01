//
//  TeamStatsResponse.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 22/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

struct TeamStatsResponse {
    let team: GQL.PlayerStatsTeam?
    let stats: [GQL.RankedStat]?
    let leadersTeam: GQL.StatLeadersTeam?
    let players: [GQL.SeasonStatsPlayer]?
}
