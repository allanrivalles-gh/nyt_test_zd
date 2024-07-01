//
//  TournamentTile.swift
//
//
//  Created by Leonardo da Silva on 28/10/22.
//

import AthleticApolloTypes
import AthleticScoresFoundation
import AthleticUI
import Foundation
import SwiftUI

public struct TournamentTile: Identifiable {

    /// these are the game phases we care about so far for a tournament game
    public enum Phase {
        case preGame
        case inGame
        case postGame
    }

    public enum Team {
        case confirmed(TeamData)
        case placeholder(name: String)
    }

    public struct TeamScore {
        public let score: Int?
        public let penaltyScore: Int?
        public let redCardsCount: Int?

        init(score: Int? = nil, penaltyScore: Int? = nil, redCardsCount: Int? = nil) {
            self.score = score
            self.penaltyScore = penaltyScore
            self.redCardsCount = redCardsCount
        }
    }

    public struct TeamData {
        public let id: String
        public let legacyId: String?
        public let logos: [ATHImageResource]
        public let alias: String
        public let accentColor: Color?
        public let seed: Int?
        public let record: String?

        func lost(game: Game) -> Bool {
            guard let winnerTeamId = game.winnerTeamId else {
                return false
            }
            return id != winnerTeamId
        }

        func won(game: Game) -> Bool {
            guard let winnerTeamId = game.winnerTeamId else {
                return false
            }
            return id == winnerTeamId
        }

        func victoriesCount(series: Series) -> Int {
            series.games.map { $0.winnerTeamId == id ? 1 : 0 }.reduce(0) { $0 + $1 }
        }

        public init(
            id: String,
            legacyId: String?,
            logos: [ATHImageResource],
            alias: String,
            accentColor: Color?,
            seed: Int? = nil,
            record: String? = nil
        ) {
            self.id = id
            self.legacyId = legacyId
            self.logos = logos
            self.alias = alias
            self.accentColor = accentColor
            self.seed = seed
            self.record = record
        }
    }

    public struct GameTeam {
        public let details: Team?
        public let scores: TeamScore?

        init(details: Team?, scores: TeamScore? = nil) {
            self.details = details
            self.scores = scores
        }
    }

    public struct Game: Identifiable {
        public let id: String
        public let phase: Phase?
        public let status: GQL.GameStatusCode?
        public let sport: GQL.Sport?
        public let ticketViewModel: GameTicketPurchaseButtonViewModel?
        public let inningHalf: GQL.InningHalf?
        public let inning: Int?
        public let matchTimeDisplay: String?
        public let startedAt: Timestamp?
        public let scheduledAt: Timestamp?
        public let isStartTimeToBeDecided: Bool
        public let homeTeam: GameTeam?
        public let awayTeam: GameTeam?
        public let isPlaceholder: Bool

        init(
            id: String,
            phase: Phase?,
            status: GQL.GameStatusCode?,
            sport: GQL.Sport?,
            ticketViewModel: GameTicketPurchaseButtonViewModel?,
            inningHalf: GQL.InningHalf? = nil,
            inning: Int? = nil,
            matchTimeDisplay: String? = nil,
            startedAt: Timestamp? = nil,
            scheduledAt: Timestamp? = nil,
            isStartTimeToBeDecided: Bool = false,
            homeTeam: GameTeam?,
            awayTeam: GameTeam?,
            isPlaceholder: Bool = false
        ) {
            self.id = id
            self.phase = phase
            self.status = status
            self.sport = sport
            self.ticketViewModel = ticketViewModel
            self.inningHalf = inningHalf
            self.inning = inning
            self.matchTimeDisplay = matchTimeDisplay
            self.startedAt = startedAt
            self.scheduledAt = scheduledAt
            self.isStartTimeToBeDecided = isStartTimeToBeDecided
            self.homeTeam = homeTeam
            self.awayTeam = awayTeam
            self.isPlaceholder = isPlaceholder
        }

        public var winnerTeamId: String? {
            guard phase == .postGame else { return nil }

            let homeTeam = homeTeam?.scores
            let awayTeam = awayTeam?.scores

            /// The scores that decide who won/lost
            var homeDecidingScore: Int? = nil
            var awayDecidingScore: Int? = nil

            if let homePenaltyScore = homeTeam?.penaltyScore,
                let awayPenaltyScore = awayTeam?.penaltyScore
            {
                /// If there was a penalty shootout (soccer) then use the penalty score
                homeDecidingScore = homePenaltyScore
                awayDecidingScore = awayPenaltyScore
            } else if let homeScore = homeTeam?.score, let awayScore = awayTeam?.score {
                homeDecidingScore = homeScore
                awayDecidingScore = awayScore
            }

            if let homeDecidingScore, let awayDecidingScore {
                if homeDecidingScore > awayDecidingScore {
                    return self.homeTeam?.id
                } else if awayDecidingScore > homeDecidingScore {
                    return self.awayTeam?.id
                } else {
                    /// It was a tie (no winner)
                    return nil
                }
            }

            return nil
        }
    }

    public struct Series {
        public struct Result {
            var lost: Bool?
            var losses: [Bool?]
        }

        public let id: String
        public let bestOf: Int?
        public let isLive: Bool
        public let homeTeam: Team?
        public let awayTeam: Team?
        public internal(set) var games: [Game]

        public var bestOfCount: Int { bestOf ?? games.count }

        func getResult(team: TeamData?) -> Result {
            let bestOf = bestOfCount
            var losses = [Bool?](repeating: nil, count: bestOf)
            if let team {
                for (index, game) in games.enumerated() where game.phase == .postGame {
                    losses[index] = team.lost(game: game)
                }
            }
            let lossesCount = losses.compactMap { $0 == true ? $0 : nil }.count
            return Result(
                lost: team == nil ? nil : lossesCount > bestOf / 2,
                losses: losses
            )
        }
    }

    public enum Data {
        case series(Series)
        case game(Game)
    }

    public var id: String {
        switch data {
        case .game(let game): return game.id
        case .series(let series): return series.id
        }
    }

    public var homeTeam: Team? {
        switch data {
        case .game(let game): return game.homeTeam?.details
        case .series(let series): return series.homeTeam
        }
    }

    public var awayTeam: Team? {
        switch data {
        case .game(let game): return game.awayTeam?.details
        case .series(let series): return series.awayTeam
        }
    }

    public var title: String?
    public var data: Data
    public var isHighlighted: Bool

    public init(title: String?, data: Data, isHighlighted: Bool) {
        self.title = title
        self.data = data
        self.isHighlighted = isHighlighted
    }
}

extension TournamentTile.GameTeam {
    fileprivate var id: String? {
        switch details {
        case .confirmed(let data): return data.id
        case .placeholder(_), nil: return nil
        }
    }
}
