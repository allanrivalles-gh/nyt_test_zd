//
//  GQL+FirstTeamSecondTeam.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 9/9/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

// MARK: - GQL Type Conformance

extension GQL.GameV2: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameTeam? { homeTeam?.fragments.gameTeam }
    public var awayFragment: GQL.GameTeam? { awayTeam?.fragments.gameTeam }
}

extension GQL.GameV2Lite: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameV2LiteTeam? { homeTeam?.fragments.gameV2LiteTeam }
    public var awayFragment: GQL.GameV2LiteTeam? { awayTeam?.fragments.gameV2LiteTeam }
}

extension GQL.ScoresBannerGame: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameV2LiteTeam? { homeTeam?.fragments.gameV2LiteTeam }
    public var awayFragment: GQL.GameV2LiteTeam? { awayTeam?.fragments.gameV2LiteTeam }
}

extension GQL.FeaturedGameV2: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.FeaturedGameTeam? { homeTeam?.fragments.featuredGameTeam }
    public var awayFragment: GQL.FeaturedGameTeam? { awayTeam?.fragments.featuredGameTeam }
}

extension GQL.FeaturedGameV2.AsSoccerGame: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.SoccerGameContainerGameTeam? {
        homeTeam?.fragments.soccerGameContainerGameTeam
    }

    public var awayFragment: GQL.SoccerGameContainerGameTeam? {
        awayTeam?.fragments.soccerGameContainerGameTeam
    }
}

extension GQL.GameV2LineUp: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameV2LineUpTeam? { homeTeam?.fragments.gameV2LineUpTeam }
    public var awayFragment: GQL.GameV2LineUpTeam? { awayTeam?.fragments.gameV2LineUpTeam }
}

extension GQL.BaseballGameV2PlayerStats: SportAwareFirstSecondTeamProviding {
    public var sport: GQL.Sport { .baseball }

    public var homeFragment: GQL.BaseballGameTeamPlayerStats? {
        homeTeam?.fragments.baseballGameTeamPlayerStats
    }
    public var awayFragment: GQL.BaseballGameTeamPlayerStats? {
        awayTeam?.fragments.baseballGameTeamPlayerStats
    }
}

extension GQL.GameV2LastGames: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameV2LastGamesTeam? { homeTeam?.fragments.gameV2LastGamesTeam }
    public var awayFragment: GQL.GameV2LastGamesTeam? { awayTeam?.fragments.gameV2LastGamesTeam }
}

extension GQL.GameV2TeamLeaders: SportAwareFirstSecondTeamProviding {
    public var homeFragment: [GQL.TeamLeader]? {
        seasonStats
            .fragments
            .seasonStatsTeamLeaderStats
            .homeStatLeaders.map {
                $0.fragments.teamLeader
            }
    }

    public var awayFragment: [GQL.TeamLeader]? {
        seasonStats
            .fragments
            .seasonStatsTeamLeaderStats
            .awayStatLeaders.map {
                $0.fragments.teamLeader
            }
    }

    public var firstTeamInfo: GQL.SeasonStatsTeamInfo? {
        sport == .soccer
            ? homeTeam?.fragments.seasonStatsTeamInfo
            : awayTeam?.fragments.seasonStatsTeamInfo
    }

    public var secondTeamInfo: GQL.SeasonStatsTeamInfo? {
        sport == .soccer
            ? awayTeam?.fragments.seasonStatsTeamInfo
            : homeTeam?.fragments.seasonStatsTeamInfo
    }
}

extension GQL.GameV2TopPerformers: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameV2TopPerformersTeam? {
        homeTeam?.fragments.gameV2TopPerformersTeam
    }
    public var awayFragment: GQL.GameV2TopPerformersTeam? {
        awayTeam?.fragments.gameV2TopPerformersTeam
    }
}

extension GQL.GameV2Injuries: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameV2InjuriesTeam? { homeTeam?.fragments.gameV2InjuriesTeam }
    public var awayFragment: GQL.GameV2InjuriesTeam? { awayTeam?.fragments.gameV2InjuriesTeam }
}

extension GQL.GameV2SeasonStats: SportAwareFirstSecondTeamProviding {
    public var homeFragment: [GQL.RankedStat]? {
        seasonStats.fragments.seasonStatsTeamStats.homeFragment
    }

    public var awayFragment: [GQL.RankedStat]? {
        seasonStats.fragments.seasonStatsTeamStats.awayFragment
    }
}

extension GQL.SeasonStatsTeamStats: FirstSecondTeamProviding {
    public var homeFragment: [GQL.RankedStat]? {
        homeTeamStats.map { $0.fragments.rankedStat }
    }
    public var awayFragment: [GQL.RankedStat]? {
        awayTeamStats.map { $0.fragments.rankedStat }
    }
}

extension GQL.GameV2ScoringGame {
    public var firstTeam: GQL.GameV2ScoringTeam? {
        sport == .soccer
            ? homeTeam?.fragments.gameV2ScoringTeam
            : awayTeam?.fragments.gameV2ScoringTeam
    }

    public var secondTeam: GQL.GameV2ScoringTeam? {
        sport == .soccer
            ? awayTeam?.fragments.gameV2ScoringTeam
            : homeTeam?.fragments.gameV2ScoringTeam
    }
}

extension GQL.AmericanFootballRecentPlays: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameV2RecentPlaysTeam? {
        homeTeam?.fragments.gameV2RecentPlaysTeam
    }
    public var awayFragment: GQL.GameV2RecentPlaysTeam? {
        awayTeam?.fragments.gameV2RecentPlaysTeam
    }
}

extension GQL.BasketballRecentPlays: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameV2RecentPlaysTeam? {
        homeTeam?.fragments.gameV2RecentPlaysTeam
    }
    public var awayFragment: GQL.GameV2RecentPlaysTeam? {
        awayTeam?.fragments.gameV2RecentPlaysTeam
    }
}

extension GQL.HockeyRecentPlays: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameV2RecentPlaysTeam? {
        homeTeam?.fragments.gameV2RecentPlaysTeam
    }
    public var awayFragment: GQL.GameV2RecentPlaysTeam? {
        awayTeam?.fragments.gameV2RecentPlaysTeam
    }
}

extension GQL.SoccerRecentPlays: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.SoccerPlayByPlayTeam? {
        homeTeam?.team?.fragments.soccerPlayByPlayTeam
    }
    public var awayFragment: GQL.SoccerPlayByPlayTeam? {
        awayTeam?.team?.fragments.soccerPlayByPlayTeam
    }
}

extension GQL.SoccerAllKeyPlays: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.SoccerPlayByPlayTeam? {
        homeTeam?.team?.fragments.soccerPlayByPlayTeam
    }
    public var awayFragment: GQL.SoccerPlayByPlayTeam? {
        awayTeam?.team?.fragments.soccerPlayByPlayTeam
    }
}

extension GQL.GamePlayByPlay: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GamePlayByPlayTeam? {
        homeTeam?.team?.fragments.gamePlayByPlayTeam
    }
    public var awayFragment: GQL.GamePlayByPlayTeam? {
        awayTeam?.team?.fragments.gamePlayByPlayTeam
    }
}

extension GQL.BaseballPlayByPlay: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.BaseballPlayByPlayTeam? {
        homeTeam?.fragments.baseballPlayByPlayTeam
    }
    public var awayFragment: GQL.BaseballPlayByPlayTeam? {
        awayTeam?.fragments.baseballPlayByPlayTeam
    }
}

extension GQL.AmericanFootballPlayByPlay: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.AmericanFootballPlayByPlayTeam? {
        homeTeam?.fragments.americanFootballPlayByPlayTeam
    }

    public var awayFragment: GQL.AmericanFootballPlayByPlayTeam? {
        awayTeam?.fragments.americanFootballPlayByPlayTeam
    }
}

extension GQL.SoccerPlayByPlay: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.SoccerPlayByPlayTeam? {
        self.homeTeam?.team?.fragments.soccerPlayByPlayTeam
    }

    public var awayFragment: GQL.SoccerPlayByPlayTeam? {
        awayTeam?.team?.fragments.soccerPlayByPlayTeam
    }
}

extension GQL.BaseballCurrentInning: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.BaseballPlayerTeam? { homeTeam?.fragments.baseballPlayerTeam }
    public var awayFragment: GQL.BaseballPlayerTeam? { awayTeam?.fragments.baseballPlayerTeam }
}

extension GQL.SoccerGameLineUps: SportAwareFirstSecondTeamProviding {
    public var sport: GQL.Sport { .soccer }
    public var homeFragment: GQL.SoccerTeamLineUp? { homeTeam?.fragments.soccerTeamLineUp }
    public var awayFragment: GQL.SoccerTeamLineUp? { awayTeam?.fragments.soccerTeamLineUp }
}

extension GQL.GameContainer.AsSoccerGame: SportAwareFirstSecondTeamProviding {
    public var sport: GQL.Sport { .soccer }
    public var homeFragment: GQL.SoccerGameContainerGameTeam? {
        homeTeam?.fragments.soccerGameContainerGameTeam
    }
    public var awayFragment: GQL.SoccerGameContainerGameTeam? {
        awayTeam?.fragments.soccerGameContainerGameTeam
    }
}

extension GQL.GameV2Odds: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameV2OddsTeam? { homeTeam?.team?.fragments.gameV2OddsTeam }
    public var awayFragment: GQL.GameV2OddsTeam? { awayTeam?.team?.fragments.gameV2OddsTeam }
}

extension GQL.GamePlayerGrades: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GamePlayerGradesTeam? {
        gradedHomeTeam?.fragments.gamePlayerGradesTeam
    }
    public var awayFragment: GQL.GamePlayerGradesTeam? {
        gradedAwayTeam?.fragments.gamePlayerGradesTeam
    }
}

extension GQL.GamePlayerGradesGameSummary: SportAwareFirstSecondTeamProviding {
    public var homeFragment: GQL.GameTeamSummaryInfo? { homeTeam?.fragments.gameTeamSummaryInfo }
    public var awayFragment: GQL.GameTeamSummaryInfo? { awayTeam?.fragments.gameTeamSummaryInfo }
}
