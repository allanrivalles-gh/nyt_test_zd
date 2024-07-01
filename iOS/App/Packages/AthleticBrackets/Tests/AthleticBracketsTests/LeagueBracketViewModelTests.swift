//
//  LeagueBracketViewModelTests.swift
//
//
//  Created by Mark Corbyn on 13/7/2023.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticScoresFoundation
import AthleticTestUtils
import XCTest

@testable import AthleticBrackets

final class LeagueBracketViewModelTests: XCTestCase {
    fileprivate let gameTickets = GQL.GameTickets(
        uri: "https://mock",
        provider: "mock-provider",
        minPrice: [.init(amount: 123, currency: .usd)],
        logosLightMode: [],
        logosDarkMode: []
    )

    @MainActor func testBuildRoundTile_seriesTitle_text_showsTheText() throws {
        let series = GQL.TournamentSeries.makeMock(
            seriesTitle: try! GQL.TournamentSeries.SeriesTitle(
                jsonObject: GQL.TournamentSeries.SeriesTitle.AsTournamentSeriesTextTitle(
                    text: "Backend text title"
                ).jsonObject
            )
        )
        let tile = makeViewModel().buildRoundTile(for: series)
        XCTAssertEqual(tile.tile.title, "Backend text title")
    }

    @MainActor func testBuildRoundTile_seriesTitle_date_formatsWeekdayMonthDay() throws {
        let date = Calendar.current.date(
            from: DateComponents(year: 2026, month: 4, day: 20)
        )!
        let series = GQL.TournamentSeries.makeMock(
            seriesTitle: try! GQL.TournamentSeries.SeriesTitle(
                jsonObject: GQL.TournamentSeries.SeriesTitle.AsTournamentSeriesDateTitle(
                    date: date
                ).jsonObject
            )
        )
        let tile = makeViewModel().buildRoundTile(for: series)
        XCTAssertEqual(tile.tile.title, "Mon, Apr 20")
    }

    @MainActor func testBuildRoundTile_seriesTitle_null_isNil() throws {
        let series = GQL.TournamentSeries.makeMock(seriesTitle: nil)
        let tile = makeViewModel().buildRoundTile(for: series)
        XCTAssertNil(tile.tile.title)
    }

    @MainActor func testBuildRoundTile_preGame_hasGamesTickets() throws {
        let futureGame = GQL.TournamentGame.makeMockSoccerGame(
            scheduledAt: .distantFuture,
            status: .scheduled,
            tickets: gameTickets
        )
        let series = GQL.TournamentSeries.makeMock(games: [futureGame])
        let tile = makeViewModel().buildRoundTile(for: series)
        if case let .series(series) = tile.tile.data {
            XCTAssertEqual(series.games[0].ticketViewModel?.title, "Tickets from $123")
        }
    }

    @MainActor func testBuildRoundTile_preGame_noGamesTicketsFromBackend() throws {
        let futureGame = GQL.TournamentGame.makeMockSoccerGame(
            scheduledAt: .distantFuture,
            status: .scheduled,
            tickets: nil
        )
        let series = GQL.TournamentSeries.makeMock(games: [futureGame])
        let tile = makeViewModel().buildRoundTile(for: series)
        if case let .series(series) = tile.tile.data {
            XCTAssertNil(series.games[0].ticketViewModel)
        }
    }

    @MainActor func testBuildRoundTile_inGame_doNotShowGamesTickets() throws {
        let currentGame = GQL.TournamentGame.makeMockSoccerGame(
            scheduledAt: Date.now,
            status: .inProgress,
            tickets: gameTickets
        )
        let series = GQL.TournamentSeries.makeMock(games: [currentGame])
        let tile = makeViewModel().buildRoundTile(for: series)
        if case let .series(series) = tile.tile.data {
            XCTAssertNil(series.games[0].ticketViewModel)
        }
    }

    @MainActor func testBuildRoundTile_postGame_doNotShowGamesTickets() throws {
        let pastGame = GQL.TournamentGame.makeMockSoccerGame(
            scheduledAt: .distantPast,
            status: .final,
            tickets: gameTickets
        )
        let series = GQL.TournamentSeries.makeMock(games: [pastGame])
        let tile = makeViewModel().buildRoundTile(for: series)
        if case let .series(series) = tile.tile.data {
            XCTAssertNil(series.games[0].ticketViewModel)
        }
    }

    // MARK: - Team Highlighting

    @MainActor func testBuildRoundTile_teamHubTeamIsCompeting_isHighlighted() throws {
        let hubTeamId = "team-777"
        let game = GQL.TournamentGame.makeMockSoccerGame(
            scheduledAt: .distantPast,
            status: .final,
            tickets: gameTickets
        )
        let series = GQL.TournamentSeries.makeMock(homeTeamId: hubTeamId, games: [game])
        let tile = makeViewModel(teamId: hubTeamId).buildRoundTile(for: series)
        XCTAssertTrue(tile.tile.isHighlighted)
    }

    @MainActor func testBuildRoundTile_teamHubTeamIsNotCompeting_isNotHighlighted() throws {
        let hubTeamId = "team-777"
        let game = GQL.TournamentGame.makeMockSoccerGame(
            scheduledAt: .distantPast,
            status: .final,
            tickets: gameTickets
        )
        let series = GQL.TournamentSeries.makeMock(homeTeamId: "not-hub-team-id", games: [game])
        let tile = makeViewModel(teamId: hubTeamId).buildRoundTile(for: series)
        XCTAssertFalse(tile.tile.isHighlighted)
    }
}

extension LeagueBracketViewModelTests {
    @MainActor
    fileprivate func makeViewModel(teamId: String? = nil) -> LeagueBracketViewModel {
        LeagueBracketViewModel(
            network: MockBracketsNetwork(),
            leagueId: "nba",
            leagueCode: .nba,
            seasonId: nil,
            teamId: teamId,
            analyticsDefaults: MockAnalyticDefaults(),
            tbdString: "Test",
            getGamePhase: { game in
                GamePhase(
                    statusCode: game.status,
                    startedAt: game.startedAt
                )?.asTournamentGamePhase
            }
        )
    }
}

extension GamePhase {
    fileprivate var asTournamentGamePhase: TournamentTile.Phase {
        switch self {
        case .nonStarter, .preGame:
            return .preGame
        case .inGame:
            return .inGame
        case .postGame:
            return .postGame
        }
    }
}
