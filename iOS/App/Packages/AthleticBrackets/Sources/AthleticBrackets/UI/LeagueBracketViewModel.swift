//
//  LeagueBracketViewModel.swift
//  theathletic-ios
//
//  Created by Jason Xu on 10/27/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticScoresFoundation
import AthleticUI
import Combine
import Foundation
import SwiftUI

private typealias PlaceholderGameTeam = GQL.TournamentStage.PlaceholderGameTeam

public struct TournamentRoundTile {
    public let conferenceName: String?
    public fileprivate(set) var tile: TournamentTile
    fileprivate let placeholderTeams: PlaceholderGameTeam?
}

public struct TournamentRound {
    public let tab: BracketTab
    public fileprivate(set) var tiles: [TournamentRoundTile]
    public let connected: Bool
}

public struct TournamentGroup {
    public let id: String
    public let name: String
}

public struct TournamentGameInjectedDetails {
    public let phase: TournamentTile.Phase?
    public let title: String

    public init(phase: TournamentTile.Phase?, title: String) {
        self.phase = phase
        self.title = title
    }
}

public typealias GetTournamentGameDetails = (GQL.TournamentGame) -> TournamentGameInjectedDetails

@MainActor
final public class LeagueBracketViewModel: ObservableObject {
    enum LoadingState {
        case loaded(tabs: [BracketTab], rounds: [TournamentRound])
        case loading
        case failed
    }

    public var rounds: [TournamentRound]? {
        if case .loaded(_, let rounds) = loadingState {
            return rounds
        }
        return nil
    }

    public let leagueId: String
    public let leagueCode: GQL.LeagueCode
    public let seasonId: String?
    public let teamId: String?
    public let analyticsDefaults: AnalyticsRequiredValues
    public let tbdString: String

    private let network: BracketsNetworking
    private let getGamePhase: (GQL.TournamentGame) -> TournamentTile.Phase?
    private let logger = ATHLogger(category: .tournaments)
    private var gameUpdatesCancellable: AnyCancellable?

    @Published private(set) var loadingState: LoadingState?
    @Published var selectedTabOffset: Int = 0

    public init(
        network: BracketsNetworking,
        leagueId: String,
        leagueCode: GQL.LeagueCode,
        seasonId: String?,
        teamId: String?,
        analyticsDefaults: AnalyticsRequiredValues,
        tbdString: String,
        getGamePhase: @escaping (GQL.TournamentGame) -> TournamentTile.Phase?
    ) {
        self.network = network
        self.leagueId = leagueId
        self.leagueCode = leagueCode
        self.seasonId = seasonId
        self.teamId = teamId
        self.analyticsDefaults = analyticsDefaults
        self.tbdString = tbdString
        self.getGamePhase = getGamePhase
    }

    func load(ignoreIfLoaded: Bool = false, isRefresh: Bool = false) async {
        guard !ignoreIfLoaded || loadingState == nil else {
            return
        }

        if let rounds = await fetchTournamentData(setLoading: !isRefresh) {
            startListeningForGamesUpdates(rounds: rounds)
        }
    }

    func trackRoundTabClick(bracketRound: BracketTab.BracketRound) {

        var objectType: AnalyticsEvent.ObjectType {
            switch bracketRound {
            case .round1:
                return .round1Tab
            case .round2:
                return .round2Tab
            case .round3:
                return .round3Tab
            case .round4:
                return .round4Tab
            case .round5:
                return .round5Tab
            case .round6:
                return .round6Tab
            case .round7:
                return .round7Tab
            }
        }

        Analytics.track(
            event: .init(
                verb: .click,
                view: .brackets,
                element: .bracketsNav,
                objectType: objectType,
                metaBlob: .init(
                    leagueId: leagueId,
                    requiredValues: analyticsDefaults
                ),
                requiredValues: analyticsDefaults
            )
        )
    }

    private func startListeningForGamesUpdates(rounds: [TournamentRound]) {
        let gamesIds =
            rounds
            .flatMap { $0.tiles }
            .flatMap { roundTile in
                switch roundTile.tile.data {
                case .game(let game): return [game]
                case .series(let series): return series.games
                }
            }
            .filter { $0.needsUpdates }
            .map { $0.id }
        gameUpdatesCancellable = network.subscribeToTournamentGameUpdates(forIds: Set(gamesIds))
            .map { $0.fragments.tournamentGame }
            .receive(on: RunLoop.main)
            .sink { [weak self] game in
                self?.onReceiveUpdate(for: game)
            }
    }

    private func fetchTournamentData(setLoading: Bool) async -> [TournamentRound]? {
        if setLoading {
            loadingState = .loading
        }

        var loadedRounds: [TournamentRound]?
        do {
            let tournamentData = try await network.fetchTournament(
                leagueCode: leagueCode,
                seasonId: seasonId
            )
            let extraStages = tournamentData.getTournament.extraStages
                .map { $0.fragments.tournamentStage }
                .filter { !$0.notSupported }
            let stages = tournamentData.getTournament.stages
                .map { $0.fragments.tournamentStage }
            let extraTabs = buildTabs(stages: extraStages, offset: 0)
            let tabs = buildTabs(stages: stages, offset: extraTabs.count)
            let extraRounds = buildRounds(
                entries: Array(zip(extraStages, extraTabs)),
                connected: false
            )
            let rounds = buildRounds(
                entries: Array(zip(stages, tabs)),
                connected: true
            )
            let allTabs = extraTabs + tabs
            let allRounds = extraRounds + rounds
            loadingState = .loaded(tabs: allTabs, rounds: allRounds)
            loadedRounds = allRounds
        } catch let error as NSError {
            logger.error("Fetch Tournament Error \(error)")
            loadingState = .failed
        }

        return loadedRounds
    }

    private func buildRounds(
        entries: [(stage: GQL.TournamentStage, tab: BracketTab)],
        connected: Bool
    ) -> [TournamentRound] {
        return entries.map { (stage, tab) in
            let tiles: [TournamentRoundTile]
            if let series = stage.series {
                tiles = series.compactMap { series in
                    let series = series.fragments.tournamentSeries
                    return buildRoundTile(for: series)
                }
            } else {
                tiles = stage.bracketGames.enumerated().compactMap { index, game in
                    if let tournamentGame = game.fragments.tournamentGame {
                        /// the server returns a separate array with the placeholder teams,
                        /// these can be matched by index with the games in the stage to know
                        /// which placeholder belongs to which game
                        let placeholderTeams = stage.placeholderGameTeams
                            .flatMap { $0[safe: index] }
                            .flatMap { $0 }
                        return buildRoundTile(
                            for: tournamentGame,
                            placeholderTeams: placeholderTeams
                        )
                    } else if let game = game.fragments.tournamentPlaceholderGame {
                        return buildPlaceholderRoundGame(for: game)
                    } else {
                        return nil
                    }
                }
            }
            return TournamentRound(
                tab: tab,
                tiles: tiles,
                connected: connected
            )
        }
    }

    private func buildPlaceholderRoundGame(
        for tournamentGame: GQL.TournamentPlaceholderGame
    ) -> TournamentRoundTile {
        let homeTeamName = tournamentGame.homeTeam?.fragments.tournamentPlaceholderTeam?.name
        let awayTeamName = tournamentGame.awayTeam?.fragments.tournamentPlaceholderTeam?.name
        return TournamentRoundTile(
            conferenceName: tournamentGame.conference,
            tile: TournamentTile(
                title: Strings.upcomingGameTitle.localized,
                data: .game(buildPlaceholderGame(for: tournamentGame)),
                isHighlighted: false
            ),
            placeholderTeams: .init(
                homeTeamName: homeTeamName,
                awayTeamName: awayTeamName
            )
        )
    }

    private func buildPlaceholderGame(
        for tournamentGame: GQL.TournamentPlaceholderGame
    ) -> TournamentTile.Game {
        let homeTeam = buildTeam(
            tournamentTeam: tournamentGame.homeTeam?.fragments.tournamentTeam,
            placeholderTeam: tournamentGame.homeTeam?.fragments.tournamentPlaceholderTeam,
            placeholderName: nil,
            basketballTeam: nil
        )
        let awayTeam = buildTeam(
            tournamentTeam: tournamentGame.awayTeam?.fragments.tournamentTeam,
            placeholderTeam: tournamentGame.awayTeam?.fragments.tournamentPlaceholderTeam,
            placeholderName: nil,
            basketballTeam: nil
        )
        return TournamentTile.Game(
            id: tournamentGame.id,
            phase: .preGame,
            status: nil,
            sport: nil,
            ticketViewModel: nil,
            inningHalf: nil,
            inning: nil,
            matchTimeDisplay: nil,
            startedAt: nil,
            scheduledAt: nil,
            homeTeam: TournamentTile.GameTeam(details: homeTeam, scores: nil),
            awayTeam: TournamentTile.GameTeam(details: awayTeam, scores: nil),
            isPlaceholder: true
        )
    }

    private func buildGame(
        for tournamentGame: GQL.TournamentGame,
        placeholderTeams: PlaceholderGameTeam?,
        phase: TournamentTile.Phase?
    ) -> TournamentTile.Game {
        let homeTeamScore = tournamentGame.homeTeam?.fragments.tournamentGameTeam.asTeamScore(
            game: tournamentGame
        )
        let awayTeamScore = tournamentGame.awayTeam?.fragments.tournamentGameTeam.asTeamScore(
            game: tournamentGame
        )
        let homeTeam = tournamentGame.homeTeam?.fragments.tournamentGameTeam
        let awayTeam = tournamentGame.awayTeam?.fragments.tournamentGameTeam
        let homeTournamentTeam = buildTeam(
            tournamentTeam: homeTeam?.team?.fragments.tournamentTeam,
            placeholderTeam: nil,
            placeholderName: placeholderTeams?.homeTeamName,
            basketballTeam: homeTeam?.asBasketballGameTeam
        )
        let awayTournamentTeam = buildTeam(
            tournamentTeam: awayTeam?.team?.fragments.tournamentTeam,
            placeholderTeam: nil,
            placeholderName: placeholderTeams?.awayTeamName,
            basketballTeam: awayTeam?.asBasketballGameTeam
        )

        let ticketViewModel: GameTicketPurchaseButtonViewModel?
        if phase == .preGame,
            let tickets = tournamentGame.tickets?.fragments.gameTickets
        {
            ticketViewModel = GameTicketPurchaseButtonViewModel(
                tickets: tickets,
                analyticsDefaults: analyticsDefaults
            )
        } else {
            ticketViewModel = nil
        }

        return TournamentTile.Game(
            id: tournamentGame.id,
            phase: phase,
            status: tournamentGame.status,
            sport: tournamentGame.sport,
            ticketViewModel: ticketViewModel,
            inningHalf: tournamentGame.asBaseballGame?.inningHalf,
            inning: tournamentGame.asBaseballGame?.inning,
            matchTimeDisplay: tournamentGame.matchTimeDisplay,
            startedAt: tournamentGame.startedAt,
            scheduledAt: tournamentGame.scheduledAt,
            isStartTimeToBeDecided: tournamentGame.timeTbd ?? false,
            homeTeam: TournamentTile.GameTeam(
                details: homeTournamentTeam,
                scores: homeTeamScore
            ),
            awayTeam: TournamentTile.GameTeam(
                details: awayTournamentTeam,
                scores: awayTeamScore
            )
        )
    }

    public func buildRoundTile(
        for tournamentSeries: GQL.TournamentSeries
    ) -> TournamentRoundTile {
        let homeTeam = tournamentSeries.homeTeam?.fragments.tournamentTeam
        let awayTeam = tournamentSeries.awayTeam?.fragments.tournamentTeam
        let homeTournamentTeam = buildTeam(
            tournamentTeam: homeTeam,
            placeholderTeam: nil,
            placeholderName: nil,
            basketballTeam: GQL.TournamentGameTeam.AsBasketballGameTeam(
                seed: tournamentSeries.homeTeamRank,
                currentRecord: tournamentSeries.homeTeamRecord
            )
        )
        let awayTournamentTeam = buildTeam(
            tournamentTeam: awayTeam,
            placeholderTeam: nil,
            placeholderName: nil,
            basketballTeam: GQL.TournamentGameTeam.AsBasketballGameTeam(
                seed: tournamentSeries.awayTeamRank,
                currentRecord: tournamentSeries.awayTeamRecord
            )
        )

        return TournamentRoundTile(
            conferenceName: tournamentSeries.conference,
            tile: TournamentTile(
                title: tournamentSeries.seriesTitle?.formattedString,
                data: .series(
                    TournamentTile.Series(
                        id: tournamentSeries.id,
                        bestOf: tournamentSeries.bestOf,
                        isLive: tournamentSeries.isLive,
                        homeTeam: homeTournamentTeam,
                        awayTeam: awayTournamentTeam,
                        games: tournamentSeries.games.compactMap { game in
                            guard let game = game.fragments.tournamentGame else {
                                if let game = game.fragments.tournamentPlaceholderGame {
                                    return buildPlaceholderGame(for: game)
                                }
                                return nil
                            }
                            return buildGame(
                                for: game,
                                placeholderTeams: nil,
                                phase: getGamePhase(game)
                            )
                        }
                    )
                ),
                isHighlighted: isHighlighted(team1: homeTeam, team2: awayTeam)
            ),
            placeholderTeams: nil
        )
    }

    private func buildRoundTile(
        for tournamentGame: GQL.TournamentGame,
        placeholderTeams: PlaceholderGameTeam?
    ) -> TournamentRoundTile {
        let phase = getGamePhase(tournamentGame)
        return TournamentRoundTile(
            conferenceName: tournamentGame.conferenceName,
            tile: TournamentTile(
                title: phase.map { phase in
                    tournamentGame.title(for: phase, tbdString: tbdString)
                },
                data: .game(
                    buildGame(
                        for: tournamentGame,
                        placeholderTeams: placeholderTeams,
                        phase: phase
                    )
                ),
                isHighlighted: isHighlighted(
                    team1: tournamentGame.homeTeam?.fragments.tournamentGameTeam.team?.fragments
                        .tournamentTeam,
                    team2: tournamentGame.awayTeam?.fragments.tournamentGameTeam.team?.fragments
                        .tournamentTeam
                )
            ),
            placeholderTeams: placeholderTeams
        )
    }

    private func buildTeam(
        tournamentTeam: GQL.TournamentTeam?,
        placeholderTeam: GQL.TournamentPlaceholderTeam?,
        placeholderName: String?,
        basketballTeam: GQL.TournamentGameTeam.AsBasketballGameTeam?
    ) -> TournamentTile.Team? {
        guard let tournamentTeam, let alias = tournamentTeam.alias else {
            let placeholderName = placeholderTeam?.name ?? placeholderName
            if let placeholderName {
                return .placeholder(name: placeholderName)
            }

            return nil
        }

        let availableLogos = tournamentTeam.logos.map {
            ATHImageResource(entity: $0.fragments.teamLogo)
        }

        return .confirmed(
            .init(
                id: tournamentTeam.id,
                legacyId: tournamentTeam.legacyTeam?.id,
                logos: availableLogos,
                alias: alias,
                accentColor: tournamentTeam.colorAccent.map { Color(hex: $0) },
                seed: basketballTeam?.seed,
                /// the server response comes with paranthesis around, but figma wants without it
                record: basketballTeam?.currentRecord?.withoutParenthesisAround()
            )
        )
    }

    private func buildTabs(
        stages: [GQL.TournamentStage],
        offset: Int
    ) -> [BracketTab] {
        return stages.enumerated().map { (index, stage) in
            BracketTab(
                id: stage.id,
                title: stage.name,
                bracketRound: BracketTab.BracketRound(rawValue: offset + index + 1),
                isLive: stage.live
            )
        }
    }

    private func isHighlighted(team1: GQL.TournamentTeam?, team2: GQL.TournamentTeam?) -> Bool {
        guard let teamId else {
            return false
        }

        return teamId == team1?.id || teamId == team2?.id
    }

    private func onReceiveUpdate(for game: GQL.TournamentGame) {
        if case .loaded(let tabs, var rounds) = loadingState {
            if let index = rounds.indexOfGame(withId: game.id) {
                let tileIndex = index.asTileIndex
                let roundTile = rounds[tileIndex.round].tiles[tileIndex.tile]
                let newGame = buildGame(
                    for: game,
                    placeholderTeams: roundTile.placeholderTeams,
                    phase: getGamePhase(game)
                )
                switch index {
                case .singleGame:
                    /// the title can be affect by an update to the game
                    rounds[tileIndex].title = newGame.phase.map { phase in
                        game.title(for: phase, tbdString: tbdString)
                    }
                    rounds[tileIndex].data = .game(newGame)
                case .withinSeries(_, _, let gameIndex):
                    if case .series(var series) = rounds[tileIndex].data {
                        series.games[gameIndex] = newGame
                        rounds[tileIndex].data = .series(series)
                    }
                }
                loadingState = .loaded(tabs: tabs, rounds: rounds)

                /// this will make sure we unsubscribe from games that don't need update anymore
                if !newGame.needsUpdates {
                    startListeningForGamesUpdates(rounds: rounds)
                }
            }
        }
    }
}

extension GQL.TournamentSeries.SeriesTitle {

    fileprivate var formattedString: String? {
        if let title = asTournamentSeriesTextTitle {
            return title.text
        } else if let date = asTournamentSeriesDateTitle?.date {
            return Date.shortWeekdayShortMonthDayFormatter.string(from: date)
        } else {
            return nil
        }
    }

}

private enum GameIndex {
    case singleGame(round: Int, tile: Int)
    case withinSeries(round: Int, tile: Int, game: Int)

    var asTileIndex: TileIndex {
        switch self {
        case .singleGame(let round, let tile):
            return TileIndex(round: round, tile: tile)
        case .withinSeries(let round, let tile, _):
            return TileIndex(round: round, tile: tile)
        }
    }
}

private struct TileIndex {
    let round: Int
    let tile: Int
}

extension Array where Element == TournamentRound {
    fileprivate subscript(index: TileIndex) -> TournamentTile {
        get {
            return self[index.round].tiles[index.tile].tile
        }
        set {
            self[index.round].tiles[index.tile].tile = newValue
        }
    }

    fileprivate func indexOfGame(withId id: String) -> GameIndex? {
        for (roundIndex, round) in self.enumerated() {
            for (tileIndex, roundTile) in round.tiles.enumerated() {
                switch roundTile.tile.data {
                case .game(let game):
                    if game.id == id {
                        return .singleGame(round: roundIndex, tile: tileIndex)
                    }
                case .series(let series):
                    for (gameIndex, game) in series.games.enumerated() {
                        if game.id == id {
                            return .withinSeries(
                                round: roundIndex,
                                tile: tileIndex,
                                game: gameIndex
                            )
                        }
                    }
                }
            }
        }
        return nil
    }
}

extension String {
    fileprivate func withoutParenthesisAround() -> String {
        if hasPrefix("(") && hasSuffix(")") {
            let startIndex = index(self.startIndex, offsetBy: 1)
            let endIndex = index(self.startIndex, offsetBy: count - 1)
            return String(self[startIndex..<endIndex])
        }
        return self
    }
}

extension GQL.TournamentGame {
    fileprivate func redCardsCount(teamId: String) -> Int? {
        guard let keyEvents = asSoccerGame?.keyEvents else { return nil }
        return
            keyEvents
            .filter { event in
                guard
                    let cardEvent = event.asCardEvent,
                    cardEvent.team.id == teamId
                else {
                    return false
                }
                return cardEvent.cardType.isRedCard
            }
            .count
    }

    fileprivate var conferenceName: String? {
        return asBasketballGame?.bracket?.name
    }
}

extension GQL.TournamentStage {
    /// these stages we currently do not support, they shouldn't be displayed
    fileprivate var notSupported: Bool {
        return type == "third_final"
    }
}

extension GQL.TournamentGameTeam {
    func asTeamScore(game: GQL.TournamentGame) -> TournamentTile.TeamScore? {
        guard let teamId = team?.fragments.tournamentTeam.id else { return nil }
        return TournamentTile.TeamScore(
            score: score,
            penaltyScore: penaltyScore,
            redCardsCount: game.redCardsCount(teamId: teamId)
        )
    }
}

extension TournamentTile.Game {
    fileprivate var needsUpdates: Bool {
        return !isPlaceholder && phase != .postGame
    }
}
