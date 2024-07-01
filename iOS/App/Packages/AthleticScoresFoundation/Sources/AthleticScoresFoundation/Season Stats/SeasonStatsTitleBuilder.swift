//
//  SeasonStatsTitleBuilder.swift
//
//
//  Created by Mark Corbyn on 11/7/2023.
//

import AthleticApolloTypes
import Foundation

public struct SeasonStatsTitleBuilder {

    public static func makeSeasonSummaryTitle(
        season: GQL.SeasonStatsSeason,
        seasonType: GQL.SeasonStatsSeasonType?,
        alwaysIncludeLeagueName: Bool = false
    ) -> String? {
        let leagueName: String?
        if alwaysIncludeLeagueName || season.league.sport == .soccer {
            /// Soccer leagues should always have the league name, while other leagues are depending on what the caller wants.
            leagueName = season.league.displayName
        } else {
            leagueName = nil
        }

        let seasonName: String? = season.active == false ? season.name : nil
        let seasonPhase: String? = season.league.hasMultipleSeasonPhases ? seasonType?.name : nil

        let nameParts = [seasonName, leagueName, seasonPhase].compactMap { $0 }
        guard !nameParts.isEmpty else {
            return nil
        }

        return nameParts.joined(separator: " ")
    }
}

extension GQL.SeasonStatsSeason.League {
    fileprivate var hasMultipleSeasonPhases: Bool {
        if sport == .soccer {
            /// Supported soccer leagues only have one phase except for MLS which has regular and post season
            return id == .mls
        } else {
            /// Assume other sports have multiple season phases
            return true
        }
    }
}
