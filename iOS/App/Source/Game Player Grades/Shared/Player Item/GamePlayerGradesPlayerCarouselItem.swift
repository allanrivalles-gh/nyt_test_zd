//
//  GamePlayerGradesPlayerCarouselItem.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 6/1/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import Foundation
import SwiftUI

struct GamePlayerGradesPlayerCarouselItem: View {
    @ObservedObject var viewModel: GamePlayerGradesPlayerViewModel

    let teamLogos: [ATHImageResource]
    let iconColor: Color?
    let isGradingLocked: Bool

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    init(
        viewModel: GamePlayerGradesPlayerViewModel,
        teamLogos: [ATHImageResource],
        iconColor: Color?,
        isGradingLocked: Bool
    ) {
        self.viewModel = viewModel
        self.teamLogos = teamLogos
        self.iconColor = iconColor
        self.isGradingLocked = isGradingLocked
    }

    var body: some View {
        VStack(spacing: 0) {
            SizedLazyImage(
                size: CGSize(width: 320, height: 160),
                resources: viewModel.headshots,
                alignment: .bottom,
                loading: {
                    HStack {
                        Circle()
                            .fill(Color.chalk.dark200)
                            .opacity(0.1)
                            .frame(width: 96, height: 96)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                },
                failed: {
                    HStack {
                        TeamLogoLazyImage(
                            size: 60,
                            resources: teamLogos
                        )
                        .frame(width: 96, height: 96)
                        .background(Color.chalk.dark100.opacity(0.2))
                        .clipShape(Circle())
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                },
                modifyImage: { $0.aspectRatio(contentMode: .fit) }
            )
            .clipped()
            .background(iconColor)
            .overlay(alignment: .topTrailing) {
                if viewModel.gradeInfo?.userGrade != nil {
                    Image(systemName: "checkmark.circle.fill")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .foregroundColor(
                            Color.highContrastAppearance(
                                of: .chalk.dark800,
                                forBackgroundColor: iconColor ?? .chalk.dark200
                            )
                        )
                        .frame(width: 18, height: 18)
                        .padding([.top, .trailing], 15)

                }
            }

            VStack(alignment: .leading, spacing: 0) {

                HStack(spacing: 0) {
                    HStack(spacing: 4) {
                        Text(viewModel.displayName)
                            .fontStyle(.slab.s.bold)
                            .lineLimit(1)
                            .foregroundColor(.chalk.dark700)

                        if let position = viewModel.positionAbbreviation {
                            Text(position)
                                .fontStyle(.calibreUtility.s.regular)
                                .foregroundColor(.chalk.dark500)
                        }
                    }

                    Spacer(minLength: 0)

                    VStack(alignment: .trailing, spacing: 0) {
                        HStack(spacing: 4.5) {
                            if let averageGradeTitle = viewModel.averageGradeTitle {
                                GradeStar(
                                    color: .chalk.yellow,
                                    isFilled: true,
                                    size: 11
                                )
                                .padding(.leading, 3)

                                Text(averageGradeTitle)
                                    .fontStyle(.calibreHeadline.l.semibold)
                                    .foregroundColor(
                                        viewModel.isAverageGradeDimmed
                                            ? .chalk.dark500
                                            : .chalk.dark800
                                    )
                                    .padding(.vertical, -3)
                            }
                        }
                    }
                }

                HStack(spacing: 0) {
                    if viewModel.gradeInfo?.userGrade != nil {
                        Text(viewModel.yourGradeTitle)
                            .fontStyle(.calibreUtility.s.regular)
                            .foregroundColor(.chalk.dark500)
                            .multilineTextAlignment(.leading)
                            .padding(.trailing, 6)

                        GradeStarsBar(
                            filledColor: .chalk.dark800,
                            unfilledColor: .chalk.dark800,
                            numberFilled: viewModel.gradeInfo?.userGrade ?? 0,
                            size: 12,
                            spacing: 3
                        )

                    } else if !isGradingLocked {
                        Text(viewModel.notGradedTitle + ":")
                            .fontStyle(.calibreUtility.s.regular)
                            .foregroundColor(.chalk.dark500)
                            .padding(.trailing, 6)

                        GradeStarsBar(
                            filledColor: .chalk.dark800,
                            unfilledColor: .chalk.dark800,
                            numberFilled: 0,
                            size: 12,
                            spacing: 3
                        )
                    }

                    Spacer(minLength: 0)

                    if let totalGradesTitle = viewModel.totalGradesTitle {
                        Text(totalGradesTitle)
                            .fontStyle(.calibreUtility.s.regular)
                            .foregroundColor(.chalk.dark500)
                    }
                }
                .padding(.bottom, 4)

                Spacer()

                DividerView(color: .chalk.dark400)

                HStack(spacing: 4) {
                    let _ = DuplicateIDLogger.logDuplicates(in: viewModel.keyStats)
                    ForEach(viewModel.keyStats) { stat in
                        VStack(spacing: 2) {
                            Text(stat.value)
                                .fontStyle(.calibreHeadline.s.medium)
                                .foregroundColor(.chalk.dark700)
                            Text(stat.title)
                                .fontStyle(.calibreUtility.xs.regular)
                                .foregroundColor(.chalk.dark500)
                        }
                        .frame(maxWidth: .infinity)
                    }
                }
                .padding(.top, 8)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 4)
        }
        .padding(.bottom, 16)
        .frame(width: 320, height: height)
        .background(Color.chalk.dark300)
        .cornerRadius(4)
    }

    private var height: CGFloat {
        let base: CGFloat = 290
        let adjustment: CGFloat

        switch dynamicTypeSize {
        case .xSmall:
            adjustment = -12
        case .small:
            adjustment = -8
        case .medium:
            adjustment = -4
        case .large:
            adjustment = 0
        case .xLarge:
            adjustment = 8
        case .xxLarge:
            adjustment = 16
        case .xxxLarge:
            adjustment = 24
        case .accessibility1:
            adjustment = 32
        case .accessibility2:
            adjustment = 40
        case .accessibility3:
            adjustment = 48
        case .accessibility4:
            adjustment = 56
        case .accessibility5:
            adjustment = 64
        @unknown default:
            adjustment = 0
        }

        return base + adjustment
    }
}

struct GamePlayerGradesPlayerCarouselItem_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            Preview()
                .background(Color.chalk.dark100)
                .lightScheme()
            Preview()
                .background(Color.chalk.dark100)
                .darkScheme()
        }
        .loadCustomFonts()

    }

    private struct Preview: View {
        var body: some View {
            ScrollView(.horizontal) {
                HStack(spacing: 10) {
                    GamePlayerGradesPlayerCarouselItem(
                        viewModel: GamePlayerGradesPlayerViewModel(
                            player: GQL.GamePlayerGradesTeam.LineUp.Player(
                                id: "1",
                                displayName: "T. Pollard Super Long Name Very Long",
                                position: .quarterback,
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
                                    ),
                                    Self.makeKeyStat(
                                        id: "1",
                                        header: "STAT",
                                        label: "QB Rating",
                                        value: "999"
                                    ),
                                    Self.makeKeyStat(
                                        id: "2",
                                        header: "C/ATT",
                                        label: "Rushing Yards",
                                        value: "5/19"
                                    ),
                                    Self.makeKeyStat(
                                        id: "3",
                                        header: "TD",
                                        label: "Rushing Touchdowns",
                                        value: "2"
                                    ),
                                ]
                            ),
                            teamId: "team-1",
                            gameId: "12345abc",
                            leagueCode: .nfl,
                            container: .gameTab,
                            analytics: analytics,
                            gradesStore: gradesStore
                        ),
                        teamLogos: [],
                        iconColor: Color(hex: "#326cde"),
                        isGradingLocked: false
                    )
                    GamePlayerGradesPlayerCarouselItem(
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
                            container: .gameTab,
                            analytics: analytics,
                            gradesStore: gradesStore
                        ),
                        teamLogos: [],
                        iconColor: Color(hex: "#326cde"),
                        isGradingLocked: false
                    )
                }
            }
        }

        private let analytics = PlayerGradesAnalyticsTracker(gameId: "abc123", leagueCode: .nba)

        static func makeKeyStat(
            id: String,
            header: String,
            label: String,
            value: String
        ) -> GQL.GamePlayerGradesTeam.LineUp.Player.Stat {
            .makeStringGameStat(
                id: id,
                statHeaderLabel: header,
                statLabel: label,
                statType: "",
                stringValue: value,
                statGroups: [.gradesDefault, .gradesExtra]
            )
        }
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
