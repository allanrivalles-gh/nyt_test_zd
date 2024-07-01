//
//  GameGradesPlayerRow.swift
//  theathletic-ios
//
//  Created by Duncan Lau on 5/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticUI
import Foundation
import SwiftUI

struct GamePlayerGradesPlayerRow: View {
    @ObservedObject var viewModel: GamePlayerGradesPlayerViewModel
    @Binding var presentedPlayer: GamePlayerID?
    @State private var gradingState: GamePlayerGradesPlayerViewModel.GradingState
    @State private var allowsGrading: Bool
    @State private var gradingGrade: Int?

    @Namespace private var rowNamespace
    private let starsGeometryId = "stars"

    let teamLogos: [ATHImageResource]
    let iconColor: Color?
    let isGradingLocked: Bool
    let analyticsSourceView: AnalyticsEvent.View

    var shouldShowPlayerGrade: Bool {
        isGradingLocked
            || gradingState == .optimisticallyGraded
            || gradingState == .graded
    }

    init(
        viewModel: GamePlayerGradesPlayerViewModel,
        presentedPlayer: Binding<GamePlayerID?>,
        teamLogos: [ATHImageResource],
        iconColor: Color?,
        isGradingLocked: Bool,
        analyticsSourceView: AnalyticsEvent.View
    ) {
        self.viewModel = viewModel
        _presentedPlayer = presentedPlayer
        self.teamLogos = teamLogos
        self.iconColor = iconColor
        self.isGradingLocked = isGradingLocked
        self.analyticsSourceView = analyticsSourceView
        let isGraded = viewModel.gradeInfo?.userGrade != nil
        _gradingState =
            isGraded
            ? .init(initialValue: .graded) : .init(initialValue: .notGraded)
        allowsGrading = !isGraded
    }

    var body: some View {
        HStack(alignment: .center, spacing: 0) {
            Button(action: {
                viewModel.trackClick(sourceView: .gradePlayersGameTab)
                presentedPlayer = viewModel.gamePlayerId
            }) {
                HStack(alignment: .center, spacing: 0) {
                    PlayerLazyImage(
                        headshots: viewModel.headshots,
                        teamLogos: teamLogos,
                        backgroundColor: iconColor,
                        size: 48
                    )

                    ZStack {
                        maximumHeightSizingGuide.opacity(0)

                        VStack(alignment: .leading, spacing: 0) {
                            HStack(alignment: .center, spacing: 0) {
                                playerInfo()

                                Spacer(minLength: 0)

                                if shouldShowPlayerGrade {
                                    AverageGrade(
                                        averageGradeTitle: viewModel.averageGradeTitle,
                                        isDimmed: viewModel.isAverageGradeDimmed
                                    )
                                }
                            }

                            if shouldShowPlayerGrade {
                                GradeBottomRowInfo(
                                    yourGradeTitle: viewModel.yourGradeTitle,
                                    userGrade: viewModel.gradeInfo?.userGrade,
                                    notGradedTitle: viewModel.notGradedTitle,
                                    totalGradesTitle: viewModel.totalGradesTitle,
                                    matchStarsBarGeometry: (
                                        id: starsGeometryId, namespace: rowNamespace
                                    )
                                )
                            }
                        }
                    }
                    .padding(.leading, 16)
                }
            }

            if !shouldShowPlayerGrade {
                AnimatedGradeStarsBar(
                    filledColor: .chalk.dark800,
                    unfilledColor: .chalk.dark800,
                    numberFilled: gradingGrade ?? 0,
                    size: 28,
                    spacing: 3,
                    onTapStar: allowsGrading
                        ? { grade in
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            allowsGrading = false
                            Task {
                                await viewModel.gradePlayer(grade: grade)
                            }
                        } : nil
                )
                .matchedGeometryEffect(
                    id: starsGeometryId,
                    in: rowNamespace,
                    anchor: .leading
                )
                .padding(.leading, 3)
            }
        }
        .padding(.vertical, 16)
        .frame(maxWidth: .infinity)
        .onReceive(viewModel.$state) { state in
            switch state {
            case .optimisticallyGraded:
                withAnimation(.easeOut(duration: 0.5).delay(1.0)) {
                    self.gradingState = state
                }
            case let .grading(grade):
                gradingGrade = grade
                self.gradingState = state
            case .graded:
                self.gradingState = state
            case .notGraded:
                gradingGrade = nil
                allowsGrading = true
                withAnimation(.easeIn(duration: 0.5).delay(1.0)) {
                    self.gradingState = state
                }
            }
        }
    }

    @ViewBuilder
    private func playerInfo() -> some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(viewModel.displayName)
                .fontStyle(.calibreUtility.l.medium, hugSingleLineHeight: true)
                .lineLimit(1)
                .foregroundColor(.chalk.dark700)
            if !viewModel.statsSummary.isEmpty {
                Text(viewModel.statsSummary)
                    .fontStyle(.calibreUtility.s.regular)
                    .multilineTextAlignment(.leading)
                    .foregroundColor(.chalk.dark500)
            }
        }
    }

    @ViewBuilder
    private var maximumHeightSizingGuide: some View {
        VStack(spacing: 0) {
            ZStack {
                playerInfo()
                AverageGrade(averageGradeTitle: "T/A", isDimmed: false)
            }

            GradeBottomRowInfo(
                yourGradeTitle: viewModel.yourGradeTitle,
                userGrade: 0,
                notGradedTitle: viewModel.notGradedTitle,
                totalGradesTitle: "Placeholder",
                matchStarsBarGeometry: nil
            )
        }
    }
}

private struct AverageGrade: View {

    @Environment(\.dynamicTypeSize) private var systemDynamicTypeSize

    let averageGradeTitle: String?
    let isDimmed: Bool

    var body: some View {
        VStack(alignment: .trailing, spacing: 0) {
            HStack(spacing: 4.5) {
                if let averageGradeTitle {
                    GradeStar(
                        color: .chalk.yellow,
                        isFilled: true,
                        size: 11
                    )
                    .padding(.leading, 3)

                    Text(averageGradeTitle)
                        .fontStyle(.calibreHeadline.l.semibold, hugSingleLineHeight: true)
                        .foregroundColor(
                            isDimmed
                                ? .chalk.dark500
                                : .chalk.dark800
                        )
                        .padding(.top, -2)
                }
            }
        }
    }
}

private struct GradeBottomRowInfo: View {
    let yourGradeTitle: String
    let userGrade: Int?
    let notGradedTitle: String
    let totalGradesTitle: String?
    let matchStarsBarGeometry: (id: AnyHashable, namespace: Namespace.ID)?

    var body: some View {
        HStack(spacing: 0) {
            if let userGrade {
                Text(yourGradeTitle)
                    .fontStyle(.calibreUtility.s.regular, hugSingleLineHeight: true)
                    .foregroundColor(.chalk.dark500)
                    .padding(.trailing, 6)

                let starsBar = GradeStarsBar(
                    filledColor: .chalk.dark800,
                    unfilledColor: .chalk.dark800,
                    numberFilled: userGrade,
                    size: 12,
                    spacing: 3
                )

                if let matchStarsBarGeometry {
                    starsBar.matchedGeometryEffect(
                        id: matchStarsBarGeometry.id,
                        in: matchStarsBarGeometry.namespace,
                        anchor: .leading
                    )
                } else {
                    starsBar
                }
            }

            Spacer(minLength: 0)

            if let totalGradesTitle {
                Text(totalGradesTitle)
                    .fontStyle(.calibreUtility.s.regular, hugSingleLineHeight: true)
                    .foregroundColor(.chalk.dark500)
            }
        }
    }
}

struct GameGradesPlayerRow_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            Preview()
                .padding(.horizontal, 16)
                .background(Color.chalk.dark100)
            Preview()
                .padding(.horizontal, 16)
                .background(Color.chalk.dark100)
                .darkScheme()
        }
        .loadCustomFonts()

    }

    private struct Preview: View {
        var body: some View {
            VStack(spacing: 0) {
                GamePlayerGradesPlayerRow(
                    viewModel: GamePlayerGradesPlayerViewModel(
                        player: GQL.GamePlayerGradesTeam.LineUp.Player(
                            id: "1",
                            displayName: "T. Pollard Super Long Name",
                            grade: GQL.GamePlayerGradesTeam.LineUp.Player.Grade(
                                id: "grades-1",
                                average: 3.4,
                                averageString: "3.4",
                                gameId: "game-1",
                                grade: 2,
                                order: 1,
                                playerId: "555",
                                total: 55,
                                updatedAt: Date(timeIntervalSince1970: 0)
                            ),
                            player: GQL.GamePlayerGradesTeam.LineUp.Player.Player(
                                id: "player-id",
                                headshots: []
                            ),
                            stats: [
                                .makeStringGameStat(
                                    id: "123",
                                    statLabel: "",
                                    statType: "summary",
                                    stringValue: "QB, SUMMARY, S",
                                    statGroups: [.gradesSummary]
                                )
                            ]
                        ),
                        teamId: "team-1",
                        gameId: "12345abc",
                        leagueCode: .nfl,
                        container: .gradesTab,
                        analytics: analytics,
                        gradesStore: gradesStore
                    ),
                    presentedPlayer: .constant(nil),
                    teamLogos: [],
                    iconColor: Color(hex: "#326cde"),
                    isGradingLocked: false,
                    analyticsSourceView: .gradePlayersGradesTab
                )
                GamePlayerGradesPlayerRow(
                    viewModel: GamePlayerGradesPlayerViewModel(
                        player: GQL.GamePlayerGradesTeam.LineUp.Player(
                            id: "2",
                            displayName: "T. Pollard Super Long Name",
                            grade: GQL.GamePlayerGradesTeam.LineUp.Player.Grade(
                                id: "grades-2",
                                average: 1.4,
                                averageString: "1.4",
                                gameId: "game-1",
                                grade: nil,
                                order: 1,
                                playerId: "666",
                                total: 1,
                                updatedAt: Date(timeIntervalSince1970: 0)
                            ),
                            player: GQL.GamePlayerGradesTeam.LineUp.Player.Player(
                                id: "player-id",
                                headshots: []
                            ),
                            stats: []
                        ),
                        teamId: "team-2",
                        gameId: "12345abc",
                        leagueCode: .nfl,
                        container: .gradesTab,
                        analytics: analytics,
                        gradesStore: gradesStore
                    ),
                    presentedPlayer: .constant(nil),
                    teamLogos: [],
                    iconColor: Color(hex: "#326cde"),
                    isGradingLocked: false,
                    analyticsSourceView: .gradePlayersGradesTab
                )
            }
        }

        private let analytics = PlayerGradesAnalyticsTracker(gameId: "abc123", leagueCode: .nba)
    }

    private static let gradesStore = {
        let store = PlayerGradesDataStore()
        store.storeGrade(
            gamePlayerId: "1",
            entity: GQL.GamePlayerGrade(
                id: "grades-1",
                average: 3.4,
                averageString: "3.4",
                gameId: "game-1",
                grade: 2,
                order: 1,
                playerId: "555",
                total: 55,
                updatedAt: Date(timeIntervalSince1970: 0)
            )
        )
        return store
    }()

}
