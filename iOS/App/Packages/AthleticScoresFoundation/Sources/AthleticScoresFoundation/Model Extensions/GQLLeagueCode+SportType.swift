//
//  GQLLeagueCode+SportType.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 18/10/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import Foundation

extension GQL.LeagueCode {
    public var sportType: SportType {
        switch self {
        case .epl, .euc, .ucl, .fri, .uwc, .uel, .mls, .pre, .nws, .fac, .cha, .lec, .leo, .let,
            .woc, .prd, .cdr, .wwc:
            return .soccer

        case .nhl:
            return .hockey

        case .nfl, .ncaafb:
            return .americanFootball

        case .nba, .ncaamb, .wnba, .ncaawb:
            return .basketball

        case .mlb:
            return .baseball

        case .__unknown:
            return .unknown
        }
    }
}
