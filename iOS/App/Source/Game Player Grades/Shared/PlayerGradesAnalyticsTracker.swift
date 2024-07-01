//
//  PlayerGradesAnalyticsTracker.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 12/4/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import Foundation

actor PlayerGradesAnalyticsTracker {

    let gameId: String
    let leagueCode: GQL.LeagueCode
    let eventManager: AnalyticEventManager

    init(
        gameId: String,
        leagueCode: GQL.LeagueCode,
        eventManager: AnalyticEventManager = AnalyticsManagers.events
    ) {
        self.gameId = gameId
        self.leagueCode = leagueCode
        self.eventManager = eventManager
    }

    func clickGradePlayersButtonOnGameTab() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gameTab,
                element: .gradePlayers,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func clickSeeAllPlayerGradesOnGameTabInLockedState() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gameTab,
                element: .gradePlayersView,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func clickSeeAllPlayerGradesOnGradesTabFlowInUnlockedState() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradesTab,
                element: .gradePlayersView,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func clickSwitchPlayerGradeTeamsOnGameTab(teamId: String) async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGameTab,
                element: .team,
                objectType: .teamId,
                objectIdentifier: teamId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue,
                    gameId: gameId
                )
            ),
            manager: eventManager
        )
    }

    func clickExpandGradePlayerFlowOnGameTab(teamMemberId: String, teamId: String) async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGameTab,
                element: .teamPlayer,
                objectType: .teamMemberId,
                objectIdentifier: teamMemberId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue,
                    gameId: gameId,
                    teamId: teamId
                )
            ),
            manager: eventManager
        )
    }

    func viewGradePlayerFlowOnGameTab(teamMemberId: String, teamId: String) async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .view,
                view: .gradePlayersGameTab,
                element: .teamPlayer,
                objectType: .teamMemberId,
                objectIdentifier: teamMemberId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue,
                    gameId: gameId,
                    teamId: teamId
                )
            ),
            manager: eventManager
        )
    }

    func clickExpandGradePlayerFlowOnGradesTab(teamMemberId: String, teamId: String) async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGradesTab,
                element: .teamPlayer,
                objectType: .teamMemberId,
                objectIdentifier: teamMemberId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue,
                    gameId: gameId,
                    teamId: teamId
                )
            ),
            manager: eventManager
        )
    }

    func viewGradePlayerFlowOnGradesTab(teamMemberId: String, teamId: String) async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .view,
                view: .gradePlayersGradesTab,
                element: .teamPlayer,
                objectType: .teamMemberId,
                objectIdentifier: teamMemberId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue,
                    gameId: gameId,
                    teamId: teamId
                )
            ),
            manager: eventManager
        )
    }

    func clickGradePlayerInGameTabFlow(grade: Int, teamMemberId: String, teamId: String) async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gameTab,
                element: .grade,
                objectType: .grade,
                objectIdentifier: grade.string,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue,
                    gameId: gameId,
                    teamId: teamId,
                    teamMemberId: teamMemberId
                )
            ),
            manager: eventManager
        )
    }

    func clickUngradePlayerInGameTabFlow(
        oldGrade: Int,
        teamMemberId: String,
        teamId: String
    ) async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gameTab,
                element: .grade,
                objectType: .ungrade,
                objectIdentifier: oldGrade.string,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue,
                    gameId: gameId,
                    teamId: teamId,
                    teamMemberId: teamMemberId
                )
            ),
            manager: eventManager
        )
    }

    func clickSeeAllPlayerGradesButtonInGameTabFlow() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gameTab,
                element: .allGrades,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func clickPreviousOnGradePlayerGameTabFlow() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGameTab,
                element: .clickPrev,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func swipePreviousOnGradePlayerGameTabFlow() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGameTab,
                element: .swipePrev,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func clickNextOnGradePlayerGameTabFlow() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGameTab,
                element: .clickNext,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func swipeNextOnGradePlayerGameTabFlow() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGameTab,
                element: .swipeNext,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func viewGradesTabInGame() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .view,
                view: .boxScoreGradesInGame,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func viewGradesTabPostGame() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .view,
                view: .boxScoreGradesPostGame,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func clickSwitchPlayerGradeTeamsOnGradesTab(teamId: String) async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGradesTab,
                element: .team,
                objectType: .teamId,
                objectIdentifier: teamId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue,
                    gameId: gameId
                )
            ),
            manager: eventManager
        )
    }

    func clickGradePlayerInGradesTabModalFlow(
        grade: Int,
        teamMemberId: String,
        teamId: String
    ) async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradesTabModal,
                element: .grade,
                objectType: .grade,
                objectIdentifier: grade.string,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue,
                    gameId: gameId,
                    teamId: teamId,
                    teamMemberId: teamMemberId
                )
            ),
            manager: eventManager
        )
    }

    func clickUngradePlayerInGradesTabModalFlow(
        oldGrade: Int,
        teamMemberId: String,
        teamId: String
    ) async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradesTabModal,
                element: .grade,
                objectType: .ungrade,
                objectIdentifier: oldGrade.string,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue,
                    gameId: gameId,
                    teamId: teamId,
                    teamMemberId: teamMemberId
                )
            ),
            manager: eventManager
        )
    }

    func clickGradePlayerInGradesTabListFlow(
        grade: Int,
        teamMemberId: String,
        teamId: String
    ) async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradesTabList,
                element: .grade,
                objectType: .grade,
                objectIdentifier: grade.string,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue,
                    gameId: gameId,
                    teamId: teamId,
                    teamMemberId: teamMemberId
                )
            ),
            manager: eventManager
        )
    }

    func clickPreviousOnGradePlayerGradesTabFlow() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGradesTab,
                element: .clickPrev,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func swipePreviousOnGradePlayerGradesTabFlow() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGradesTab,
                element: .swipePrev,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func clickNextOnGradePlayerGradesTabFlow() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGradesTab,
                element: .clickNext,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }

    func swipeNextOnGradePlayerGradesTabFlow() async {
        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .gradePlayersGradesTab,
                element: .swipeNext,
                objectType: .gameId,
                objectIdentifier: gameId,
                metaBlob: .init(
                    leagueId: leagueCode.rawValue
                )
            ),
            manager: eventManager
        )
    }
}
