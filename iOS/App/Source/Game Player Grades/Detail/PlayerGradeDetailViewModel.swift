//
//  PlayerGradeDetailViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 9/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import Combine
import Foundation
import SwiftUI

final class PlayerGradeDetailViewModel: ObservableObject {

    struct TeamInfo {
        let logos: [ATHImageResource]
        let score: String
    }

    struct GameInfo {
        let firstTeam: TeamInfo
        let secondTeam: TeamInfo

        let progressMain: String?
        let progressExtra: String?
    }

    struct Stat: Identifiable {
        let id: AnyHashable
        let title: String
        let value: String
    }

    // MARK: Header

    let playerId: String
    let gameId: String
    let teamMemberId: String
    let title: String
    let subtitle: String?
    let headshots: [ATHImageResource]
    let teamLogos: [ATHImageResource]

    // MARK: - Stats

    let keyStats: [Stat]
    let allStats: [Stat]

    // MARK: Grade

    let isLocked: Bool
    let showUngradedToastSubject = PassthroughSubject<Bool, Never>()

    @Published private(set) var userGrade: Int?
    @Published private(set) var averageGrade: String?
    @Published private(set) var averageGradeSubtitle: String?
    @Published private(set) var isAverageGradeDimmed: Bool = false
    @Published private(set) var isGradingInProgress: Bool = false

    var gradeSectionTitle: String? {
        guard !isLocked else {
            return nil
        }

        if userGrade == nil {
            return Strings.gradeThisPerformance.localized
        } else {
            return Strings.gradeSubmitted.localized
        }
    }

    var showGradeCheckmark: Bool {
        userGrade != nil
    }

    var yourGradeText: String? {
        guard let grade = userGrade else {
            return nil
        }

        return String(format: Strings.gradesYourGradeFormat.localized, grade)
    }

    private let teamId: String
    private let leagueCode: GQL.LeagueCode
    private let entryPoint: PlayerGradesEntryPoint
    private let analytics: PlayerGradesAnalyticsTracker
    private let gradesStore: PlayerGradesDataStore
    private let network: GamePlayerGradesNetworking

    private var gradeCancellable: AnyCancellable?
    private lazy var logger = ATHLogger(category: .gamePlayerGrades)

    init(
        player: GQL.GamePlayerGradesTeam.LineUp.Player,
        teamAlias: String?,
        teamLogos: [GQL.TeamLogo],
        gameId: String,
        teamId: String,
        leagueCode: GQL.LeagueCode,
        isGradingLocked: Bool,
        entryPoint: PlayerGradesEntryPoint,
        analytics: PlayerGradesAnalyticsTracker,
        gradesStore: PlayerGradesDataStore,
        network: GamePlayerGradesNetworking = AppEnvironment.shared.network
    ) {
        self.teamId = teamId
        self.leagueCode = leagueCode
        self.entryPoint = entryPoint
        self.analytics = analytics
        self.gradesStore = gradesStore
        self.network = network

        self.playerId = player.id
        self.teamMemberId = player.player.id
        self.gameId = gameId
        self.title = player.displayName ?? ""

        let teamAndNumber = [teamAlias, player.jerseyNumber.flatMap { "#\($0)" }]
            .compactMap { $0 }
            .joined(separator: " ")

        self.subtitle = [player.position?.abbreviation, teamAndNumber]
            .compactMap { $0 }
            .joined(separator: ", ")

        self.headshots = player.player.headshots.map {
            ATHImageResource(entity: $0.fragments.playerHeadshot)
        }
        self.teamLogos = teamLogos.map { ATHImageResource(entity: $0) }

        self.keyStats = player.stats
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

        self.allStats = player.stats.filter {
            $0.statGroups.contains(.gradesDefault) || $0.statGroups.contains(.gradesExtra)
        }.compactMap {
            GameStat($0.fragments.gameStat)
        }.map {
            Stat(id: $0.id, title: $0.label, value: $0.value.displayValue)
        }

        self.isLocked = isGradingLocked

        let playerGrade = gradesStore.grade(for: player.id)

        applyGrade(playerGrade)

        gradeCancellable = gradesStore.publisherForGamePlayerId(
            id: playerId
        )
        .dropFirst()
        .sink { [weak self] gradeInfo in
            self?.applyGrade(gradeInfo)
        }
    }

    func gradeTapped(grade: Int) {
        guard let originalGradeInfo = gradesStore.grade(for: playerId) else {
            return
        }

        Task { @MainActor in
            isGradingInProgress = true
            defer {
                isGradingInProgress = false
            }

            let newUserGrade: Int?

            if let existingGrade = userGrade, existingGrade == grade {
                trackUngradeSubmission(oldGrade: grade)
                newUserGrade = nil
            } else {
                trackGradeSubmission(grade: grade)
                newUserGrade = grade
            }

            /// Optimistically update the grade locally and then revert if the request fails
            let updatedGrade = optimisticallyMakeNewGrade(
                from: originalGradeInfo,
                withNewGrade: newUserGrade
            )

            /// Optmistically show/hide grade deleted toast based on user's action,
            /// then reverting (hide toast) if request failed
            showUngradedToastSubject.send(newUserGrade == nil)

            gradesStore.storeGrade(
                gamePlayerId: playerId,
                newGrade: updatedGrade,
                ignoreOutdated: false
            )

            do {
                let responseEntity: GQL.GamePlayerGrade

                if let newUserGrade {
                    responseEntity = try await network.gradePlayer(
                        gameId: gameId,
                        teamMemberId: teamMemberId,
                        grade: newUserGrade
                    )
                } else {
                    responseEntity = try await network.ungradePlayer(
                        gameId: gameId,
                        teamMemberId: teamMemberId
                    )
                }

                if responseEntity.grade != newUserGrade {
                    logger.warning(
                        "The backend responded with an unexpected grade value \(responseEntity.grade?.string ?? "nil")"
                    )
                }

                gradesStore.storeGrade(gamePlayerId: playerId, entity: responseEntity)
            } catch {
                gradesStore.storeGrade(
                    gamePlayerId: playerId,
                    newGrade: originalGradeInfo,
                    ignoreOutdated: false
                )

                showUngradedToastSubject.send(false)

                logger.error(
                    "API call failed to \(newUserGrade == nil ? "ungrade" : "grade") team member \(teamMemberId) value \(newUserGrade?.string ?? "nil") in game \(gameId) with error: \(error)",
                    .network
                )
            }
        }
    }

    private func optimisticallyMakeNewGrade(
        from originalGradeInfo: PlayerGrade,
        withNewGrade newGrade: Int?
    ) -> PlayerGrade {
        PlayerGrade.make(from: originalGradeInfo, withNewUserGrade: newGrade)
    }

    func trackView() {
        Task {
            switch entryPoint {
            case .gameTab:
                await analytics.viewGradePlayerFlowOnGameTab(
                    teamMemberId: teamMemberId,
                    teamId: teamId
                )
            case .gradesTab:
                await analytics.viewGradePlayerFlowOnGradesTab(
                    teamMemberId: teamMemberId,
                    teamId: teamId
                )
            }
        }
    }

    private func trackGradeSubmission(grade: Int) {
        Task {
            switch entryPoint {
            case .gameTab:
                await analytics.clickGradePlayerInGameTabFlow(
                    grade: grade,
                    teamMemberId: teamMemberId,
                    teamId: teamId
                )
            case .gradesTab:
                await analytics.clickGradePlayerInGradesTabModalFlow(
                    grade: grade,
                    teamMemberId: teamMemberId,
                    teamId: teamId
                )
            }
        }
    }

    private func trackUngradeSubmission(oldGrade: Int) {
        Task {
            switch entryPoint {
            case .gameTab:
                await analytics.clickUngradePlayerInGameTabFlow(
                    oldGrade: oldGrade,
                    teamMemberId: teamMemberId,
                    teamId: teamId
                )
            case .gradesTab:
                await analytics.clickUngradePlayerInGradesTabModalFlow(
                    oldGrade: oldGrade,
                    teamMemberId: teamMemberId,
                    teamId: teamId
                )
            }
        }
    }
}

extension PlayerGradeDetailViewModel: Identifiable, Hashable {

    var id: AnyHashable {
        playerId
    }

    static func == (lhs: PlayerGradeDetailViewModel, rhs: PlayerGradeDetailViewModel) -> Bool {
        lhs.playerId == rhs.playerId
    }

    func hash(into hasher: inout Hasher) {
        hasher.combine(playerId)
    }
}

extension PlayerGradeDetailViewModel {

    private func applyGrade(_ gradeInfo: PlayerGrade?) {
        self.userGrade = gradeInfo?.userGrade

        /// If the user has submitted a grade or grading is locked then show the average
        if userGrade != nil || isLocked {
            self.averageGrade = gradeInfo?.averageString
            self.isAverageGradeDimmed = gradeInfo?.total ?? 0 <= 0

            self.averageGradeSubtitle = gradeInfo.flatMap({
                let totalGrades = $0.total
                let numberFormatter = NumberFormatter()
                numberFormatter.numberStyle = .decimal
                guard
                    let formattedNumber = numberFormatter.string(
                        from: NSNumber(value: totalGrades)
                    )
                else {
                    return nil
                }

                return String(
                    format: totalGrades == 1
                        ? Strings.averageOfGradesSingularFormat.localized
                        : Strings.averageOfGradesPluralFormat.localized,
                    formattedNumber
                )
            })
        } else {
            self.averageGrade = nil
            self.averageGradeSubtitle = nil
            self.isAverageGradeDimmed = false
        }
    }

    private func optimisticallyMakeNewGrade(
        from originalGradeInfo: PlayerGrade,
        withNewGrade newGrade: Int
    ) -> PlayerGrade {
        PlayerGrade.make(from: originalGradeInfo, withNewUserGrade: newGrade)
    }
}
