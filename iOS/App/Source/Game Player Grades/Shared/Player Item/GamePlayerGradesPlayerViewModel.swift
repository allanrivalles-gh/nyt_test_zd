//
//  GamePlayerGradesPlayer.swift
//  theathletic-ios
//
//  Created by Duncan Lau on 9/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import Combine
import Foundation

final class GamePlayerGradesPlayerViewModel: ObservableObject, Identifiable {

    enum GradingState: Equatable {
        case notGraded
        case grading(grade: Int)
        case optimisticallyGraded
        case graded
    }

    struct Stat: Identifiable {
        let id: AnyHashable
        let title: String
        let value: String
    }

    @Published private(set) var state: GradingState = .notGraded
    @Published private(set) var gradeInfo: PlayerGrade?

    var id: AnyHashable {
        gamePlayerId
    }

    let teamId: String
    let gamePlayerId: String
    let teamMemberId: String
    let gameId: String
    let leagueCode: GQL.LeagueCode
    let displayName: String
    let positionAbbreviation: String?
    let statsSummary: String
    let headshots: [ATHImageResource]
    let keyStats: [Stat]

    private let container: PlayerGradesEntryPoint
    private let gradesStore: PlayerGradesDataStore
    private let analytics: PlayerGradesAnalyticsTracker
    private let network: GamePlayerGradesNetworking
    private var playerGradeCancellable: AnyCancellable?
    private lazy var logger = ATHLogger(category: .gamePlayerGrades)

    var yourGradeTitle: String {
        String(
            format: Strings.gradesYourGradeFormat.localized,
            gradeInfo?.userGrade ?? 0
        )
    }

    var notGradedTitle: String {
        Strings.gradesNotGraded.localized
    }

    var averageGradeTitle: String? {
        gradeInfo?.averageString
    }

    var isAverageGradeDimmed: Bool {
        gradeInfo?.total ?? 0 <= 0
    }

    var totalGradesTitle: String? {
        guard let totalGrades = gradeInfo?.total else {
            return nil
        }

        return String(
            format: totalGrades == 1
                ? Strings.gradesTotalGradesSingularFormat.localized
                : Strings.gradesTotalGradesPluralFormat.localized,
            totalGrades
        )
    }

    init(
        player: GQL.GamePlayerGradesTeam.LineUp.Player,
        teamId: String,
        gameId: String,
        leagueCode: GQL.LeagueCode,
        container: PlayerGradesEntryPoint,
        analytics: PlayerGradesAnalyticsTracker,
        gradesStore: PlayerGradesDataStore,
        network: GamePlayerGradesNetworking = AppEnvironment.shared.network
    ) {
        self.network = network
        self.container = container
        self.analytics = analytics
        self.gradesStore = gradesStore

        gamePlayerId = player.id
        teamMemberId = player.player.id
        self.teamId = teamId
        self.gameId = gameId
        self.leagueCode = leagueCode
        displayName = player.displayName ?? ""
        positionAbbreviation = player.position?.abbreviation

        let stats = player.stats
            .compactMap { $0.fragments.gameStat }
            .gameStats.first(where: { $0.type == "summary" })
        statsSummary = stats?.value.displayValue ?? ""

        keyStats = player.stats
            .filter {
                $0.statGroups.contains(.gradesDefault)
            }.compactMap {
                GameStat($0.fragments.gameStat)
            }.map { model in
                Stat(
                    id: model.id,
                    title: model.longLabel ?? model.shortLabel ?? model.label,
                    value: model.value.displayValue
                )
            }

        headshots = player.player.headshots.map {
            ATHImageResource(entity: $0.fragments.playerHeadshot)
        }

        let gradeInfo = gradesStore.grade(for: player.id)
        self.gradeInfo = gradeInfo

        state = gradeInfo?.userGrade != nil ? .graded : .notGraded

        startObservingGradeStore()
    }

    @MainActor
    func gradePlayer(grade: Int) async {
        guard let originalGradeInfo = gradeInfo else {
            return
        }

        /// Don't observe changes to the grades store while we're in the middle of submitting a grade because we may get a fresh value from the backend
        /// which doesn't yet contain the optimistically graded player value.
        stopObservngGradeStore()
        defer {
            startObservingGradeStore()
        }

        trackGradeSubmission(grade: grade)

        state = .grading(grade: grade)

        gradeInfo = optimisticallyMakeNewGrade(from: originalGradeInfo, withNewGrade: grade)
        state = .optimisticallyGraded

        do {
            let entity = try await network.gradePlayer(
                gameId: gameId,
                teamMemberId: teamMemberId,
                grade: grade
            )

            gradesStore.storeGrade(gamePlayerId: gamePlayerId, entity: entity)
            gradeInfo = gradesStore.grade(for: gamePlayerId)

            state = .graded
        } catch {
            gradeInfo = originalGradeInfo

            state = .notGraded

            logger.error(
                "Failed to submit grades to GQL with error: \(error)",
                .network
            )
        }
    }

    func trackClick(sourceView: AnalyticsEvent.View) {
        Task {
            switch container {
            case .gameTab:
                await analytics.clickExpandGradePlayerFlowOnGameTab(
                    teamMemberId: teamMemberId,
                    teamId: teamId
                )
            case .gradesTab:
                await analytics.clickExpandGradePlayerFlowOnGradesTab(
                    teamMemberId: teamMemberId,
                    teamId: teamId
                )
            }
        }
    }

    private func trackGradeSubmission(grade: Int) {
        switch container {
        case .gameTab:
            /// Unsupported
            break
        case .gradesTab:
            Task {
                await analytics.clickGradePlayerInGradesTabListFlow(
                    grade: grade,
                    teamMemberId: teamMemberId,
                    teamId: teamId
                )
            }
        }
    }

    private func optimisticallyMakeNewGrade(
        from originalGradeInfo: PlayerGrade,
        withNewGrade newGrade: Int
    ) -> PlayerGrade {
        PlayerGrade.make(from: originalGradeInfo, withNewUserGrade: newGrade)
    }

    private func startObservingGradeStore() {
        playerGradeCancellable = gradesStore.publisherForGamePlayerId(id: gamePlayerId)
            .dropFirst()
            .sink { [weak self] grade in
                self?.gradeInfo = grade
                self?.state = grade?.userGrade != nil ? .graded : .notGraded
            }
    }

    private func stopObservngGradeStore() {
        playerGradeCancellable = nil
    }

}
