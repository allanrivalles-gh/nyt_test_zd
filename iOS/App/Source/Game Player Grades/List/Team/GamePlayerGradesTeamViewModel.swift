//
//  GamePlayerGradeDetailsCellViewModel.swift
//  theathletic-ios
//
//  Created by Duncan Lau on 30/11/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticScoresFoundation
import AthleticUI
import Foundation
import SwiftUI

typealias GamePlayerID = String

struct GamePlayerGradesTeamViewModel: Identifiable {

    let id: String
    let gameId: String
    let teamLogos: [ATHImageResource]
    let iconColor: Color?
    let players: [GamePlayerGradesPlayerViewModel]

    private let gradeStatus: GradeStatus?
    private let gameTeam: GQL.GamePlayerGradesTeam
    private let team: GQL.GamePlayerGradesTeam.Team
    private let leagueCode: GQL.LeagueCode
    private let analytics: PlayerGradesAnalyticsTracker

    var isGradingLocked: Bool {
        gradeStatus == .locked
    }

    func makePagingDetailViewModel(
        selectedGamePlayerId: String,
        gameId: String,
        gradesStore: PlayerGradesDataStore = .shared
    ) -> PlayerGradeDetailPagingViewModel {

        let gradablePlayers =
            gameTeam.lineUp?.sortedGradablePlayers ?? []
        let detailViewModels = gradablePlayers.map {
            PlayerGradeDetailViewModel(
                player: $0,
                teamAlias: team.alias,
                teamLogos: team.logos.map { $0.fragments.teamLogo },
                gameId: gameId,
                teamId: team.id,
                leagueCode: leagueCode,
                isGradingLocked: isGradingLocked,
                entryPoint: .gradesTab,
                analytics: analytics,
                gradesStore: gradesStore
            )
        }
        let firstPage =
            detailViewModels.first {
                $0.playerId == selectedGamePlayerId
            } ?? detailViewModels[0]

        return PlayerGradeDetailPagingViewModel(
            playerPages: detailViewModels,
            selectedPage: firstPage,
            gameId: gameId,
            leagueCode: leagueCode,
            teamColor: iconColor,
            isGradingLocked: isGradingLocked,
            entryPoint: .gradesTab,
            analytics: analytics,
            gradesStore: gradesStore
        )
    }
}

extension GamePlayerGradesTeamViewModel {
    init?(
        gameTeam: GQL.GamePlayerGradesTeam,
        gameId: String,
        leagueCode: GQL.LeagueCode,
        analytics: PlayerGradesAnalyticsTracker,
        gradeStatus: GradeStatus?,
        gradesStore: PlayerGradesDataStore
    ) {
        guard let team = gameTeam.team else {
            return nil
        }

        self.gameTeam = gameTeam
        self.team = team
        self.gameId = gameId
        self.leagueCode = leagueCode
        self.analytics = analytics
        self.gradeStatus = gradeStatus

        id = gameTeam.id
        teamLogos = team.logos.compactMap { ATHImageResource(entity: $0.fragments.teamLogo) }
        iconColor = team.colorPrimary.map({ Color(hex: "#\($0)") })
        players =
            gameTeam.lineUp?.sortedGradablePlayers
            .map { player in
                GamePlayerGradesPlayerViewModel(
                    player: player,
                    teamId: team.id,
                    gameId: gameId,
                    leagueCode: leagueCode,
                    container: .gradesTab,
                    analytics: analytics,
                    gradesStore: gradesStore
                )
            } ?? []
    }
}
