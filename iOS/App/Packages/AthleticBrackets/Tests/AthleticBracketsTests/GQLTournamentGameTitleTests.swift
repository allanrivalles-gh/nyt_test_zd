//
//  GQLTournamentGameTitleTests.swift
//
//
//  Created by Leonardo da Silva on 29/11/22.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticTestUtils
import XCTest

@testable import AthleticBrackets

final class GQLTournamentGameTitleTests: XCTestCase {
    private let tbdString = "TBD"

    func testPostGameSoccer() {
        let november28th = Calendar.current.date(from: .init(month: 11, day: 28))
        let game = GQL.TournamentGame.makeSoccerGame(
            id: "0",
            scheduledAt: november28th,
            sport: .soccer,
            venue: .init(name: "Stadium Name"),
            keyEvents: []
        )
        let title = game.title(for: .postGame, tbdString: tbdString)
        XCTAssertEqual(title, "FT, Nov 28, Stadium Name")
    }

    func testPostGameNonSoccer() {
        let november28th = Calendar.current.date(from: .init(month: 11, day: 28))
        let game = GQL.TournamentGame.makeBasketballGame(
            id: "0",
            scheduledAt: november28th,
            sport: .basketball,
            venue: .init(name: "Stadium Name")
        )
        let title = game.title(for: .postGame, tbdString: tbdString)
        XCTAssertEqual(title, "Final, Nov 28, Stadium Name")
    }

    func testInGameInProgress() {
        let game = GQL.TournamentGame.makeBasketballGame(
            id: "0",
            status: .inProgress,
            sport: .basketball,
            venue: .init(name: "Stadium Name")
        )
        let title = game.title(for: .inGame, tbdString: tbdString)
        XCTAssertEqual(title, "Stadium Name")
    }

    func testPreGameToday() {
        let today = Date(timeIntervalSince1970: 1_670_245_140)
        let timeSettings = MockTimeSettings(now: today)
        let startOfToday = timeSettings.now().startOfDay(for: timeSettings.timeZone)
        let todayAt233AM = timeSettings.calendar.date(
            byAdding: .init(
                day: 0,
                hour: 2,
                minute: 33
            ),
            to: startOfToday
        )

        let game = GQL.TournamentGame.makeBasketballGame(
            id: "0",
            scheduledAt: todayAt233AM,
            sport: .basketball,
            venue: .init(name: "Stadium Name")
        )

        let title = game.title(
            for: .preGame,
            tbdString: tbdString,
            timeSettings: timeSettings
        )
        XCTAssertEqual(title, "2:33AM, Stadium Name")
    }

    func testPreGameWithinAWeekAndTimeTBD() {
        let monday5Dec2022 = Date(timeIntervalSince1970: 1_670_245_140)
        let timeSettings = MockTimeSettings(now: monday5Dec2022)
        let startOfMonday = timeSettings.calendar.startOfDay(for: timeSettings.now())
        let tuesday = timeSettings.calendar.date(
            byAdding: .init(day: 1),
            to: startOfMonday
        )
        let game = GQL.TournamentGame.makeBasketballGame(
            id: "0",
            scheduledAt: tuesday,
            timeTbd: true,
            sport: .basketball,
            venue: .init(name: "Stadium Name")
        )
        let title = game.title(
            for: .preGame,
            tbdString: tbdString,
            timeSettings: timeSettings
        )
        XCTAssertEqual(title, "Tue, TBD, Stadium Name")
    }

    func testPreGameWithinAWeek() {
        let monday5Dec2022 = Date(timeIntervalSince1970: 1_670_245_140)
        let timeSettings = MockTimeSettings(now: monday5Dec2022)
        let startOfMonday = timeSettings.calendar.startOfDay(for: timeSettings.now())
        let tuesdayAt141AM = timeSettings.calendar.date(
            byAdding: .init(
                day: 1,
                hour: 1,
                minute: 41
            ),
            to: startOfMonday
        )
        let game = GQL.TournamentGame.makeBasketballGame(
            id: "0",
            scheduledAt: tuesdayAt141AM,
            sport: .basketball,
            venue: .init(name: "Stadium Name")
        )
        let title = game.title(
            for: .preGame,
            tbdString: tbdString,
            timeSettings: timeSettings
        )
        XCTAssertEqual(title, "Tue, 1:41AM, Stadium Name")
    }

    func testPreGameAfterAWeek() {
        let wednesday5Oct2022 = Date(timeIntervalSince1970: 1_664_974_740)
        let timeSettings = MockTimeSettings(now: wednesday5Oct2022)
        let saturday5Nov2022 = timeSettings.calendar.date(
            byAdding: .init(month: 1),
            to: wednesday5Oct2022
        )

        let game = GQL.TournamentGame.makeBasketballGame(
            id: "0",
            scheduledAt: saturday5Nov2022,
            sport: .basketball,
            venue: .init(name: "Stadium Name")
        )

        let title = game.title(
            for: .preGame,
            tbdString: tbdString,
            timeSettings: timeSettings
        )
        XCTAssertEqual(title, "Sat, Nov 5, Stadium Name")
    }
}
