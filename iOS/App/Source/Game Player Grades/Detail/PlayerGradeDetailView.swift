//
//  PlayerGradeDetailView.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 9/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import SwiftUI

struct PlayerGradeDetailView: View {

    @StateObject var viewModel: PlayerGradeDetailViewModel
    var showGradesTab: VoidClosure
    @Binding var pageStatsExpansions: [PlayerGradeDetailPagingViewModel.Page: Bool]
    @Binding var shouldShowUngradedToast: Bool

    @State private var isStatsExpanded: Bool = false
    @State private var gradeSpacerHeight: CGFloat = 0

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        GeometryReader { detailGeometry in
            VStack(spacing: 0) {
                VStack(spacing: 0) {
                    Header(
                        title: viewModel.title,
                        subtitle: viewModel.subtitle,
                        headshots: viewModel.headshots,
                        teamLogos: viewModel.teamLogos,
                        isStatsExpanded: isStatsExpanded
                    )

                    VStack(spacing: 0) {
                        GameInfo()
                            .background(Color.chalk.dark200)
                            .zIndex(1)

                        DividerView()
                            .zIndex(1)

                        Stats(
                            keyStats: viewModel.keyStats,
                            allStats: viewModel.allStats,
                            isStatsExpanded: $isStatsExpanded
                        )
                        .zIndex(0)
                    }
                    .padding(.horizontal, 16)
                    .background(Color.chalk.dark200)
                    .clipped()
                }

                Spacer(minLength: gradeSpacerHeight)
                    .id("\(viewModel.playerId)-spacer")
                    .getSize { size in
                        gradeSpacerHeight = size.height
                    }

                GradeSubmission(viewModel: viewModel, showGradesTab: showGradesTab)
                    .padding(.horizontal, 16)

                Spacer(minLength: gradeSpacerHeight)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .padding(.bottom, 30)
            .background(Color.chalk.dark200)
            .onChange(of: dynamicTypeSize) { _ in
                gradeSpacerHeight = 0
            }
            .onChange(of: detailGeometry.size.height) { _ in
                gradeSpacerHeight = 0
            }
        }
        .onChange(of: isStatsExpanded) { newValue in
            pageStatsExpansions[.playerGradeDetail(viewModel)] = newValue
        }
        .onReceive(viewModel.showUngradedToastSubject) { showUngradedToast in
            shouldShowUngradedToast = showUngradedToast
        }
    }
}

private struct Header: View {

    let title: String
    let subtitle: String?
    let headshots: [ATHImageResource]
    let teamLogos: [ATHImageResource]
    let isStatsExpanded: Bool

    @Environment(\.teamColor) private var teamColor
    @Environment(\.teamForegroundColor) private var teamForegroundColor

    var body: some View {
        VStack(spacing: 0) {
            VStack(spacing: 0) {
                Text(title)
                    .fontName(.regularSlabBold, size: 24)
                    .foregroundColor(teamForegroundColor)

                if let subtitle = subtitle {
                    Text(subtitle)
                        .fontStyle(.calibreUtility.xl.regular)
                        .foregroundColor(teamForegroundColor.opacity(0.7))
                }
            }
            .padding(.top, 16)
            .padding(.bottom, 11)

            if !isStatsExpanded {
                let preferredHeadshotHeight: CGFloat = 213

                VStack {
                    GeometryReader { geometry in
                        let imageHeight = min(preferredHeadshotHeight, geometry.size.height)
                        let loadingCircleHeight = min(164, geometry.size.height)
                        let failedCircleHeight = min(156, geometry.size.height * 0.9)
                        let failedLogoHeight = failedCircleHeight * 0.75

                        SizedLazyImage(
                            size: CGSize(width: geometry.size.width, height: imageHeight),
                            resources: headshots,
                            alignment: .bottom,
                            loading: {
                                HStack {
                                    Circle()
                                        .fill(Color.chalk.dark200)
                                        .opacity(0.1)
                                        .frame(width: loadingCircleHeight)
                                }
                                .frame(maxWidth: .infinity, maxHeight: .infinity)
                            },
                            failed: {
                                HStack {
                                    TeamLogoLazyImage(
                                        size: failedLogoHeight,
                                        resources: teamLogos
                                    )
                                    .frame(width: failedCircleHeight, height: failedCircleHeight)
                                    .background(Color.chalk.dark100.opacity(0.2))
                                    .clipShape(Circle())
                                }
                                .frame(maxWidth: .infinity, maxHeight: .infinity)
                            },
                            modifyImage: { $0.aspectRatio(contentMode: .fit) }
                        )
                        .clipped()
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: preferredHeadshotHeight)
            }
        }
        .frame(maxWidth: .infinity)
        .background(teamColor)
    }
}

private struct GameInfo: View {

    @EnvironmentObject private var gameInfo: GradesDetailGameViewModel

    var body: some View {
        SideBySideColumns(
            leading: {
                HStack(spacing: 4) {
                    TeamLogoLazyImage(size: 24, resources: gameInfo.firstTeamLogos)
                    Text(gameInfo.firstScore)
                        .fontName(.calibreRegular, size: 32)
                        .foregroundColor(.chalk.dark800)
                }
            },
            trailing: {
                HStack(spacing: 4) {
                    Text(gameInfo.secondScore)
                        .fontName(.calibreRegular, size: 32)
                        .foregroundColor(.chalk.dark800)
                    TeamLogoLazyImage(size: 24, resources: gameInfo.secondTeamLogos)
                }
            },
            divider: {
                VStack(spacing: 0) {
                    Text(gameInfo.progressMain ?? .gameStatPlaceholder)
                        .fontStyle(.calibreUtility.s.medium)
                        .foregroundColor(color(for: gameInfo.progressMainStyle))

                    if let extra = gameInfo.progressExtra {
                        Text(extra)
                            .fontStyle(.calibreUtility.s.medium)
                            .foregroundColor(color(for: gameInfo.progressExtraStyle))
                    }
                }
                .padding(.horizontal, 16)
            },
            alignment: .center
        )
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }

    private func color(for style: GradesDetailGameViewModel.GameProgressStyle) -> Color {
        switch style {
        case .live: return .chalk.red
        case .highlighted: return .chalk.dark700
        case .normal: return .chalk.dark500
        }
    }

}

private struct Stats: View {
    private static let scrollViewNamespace = "stats-scrollview"

    let keyStats: [PlayerGradeDetailViewModel.Stat]
    let allStats: [PlayerGradeDetailViewModel.Stat]

    @Binding var isStatsExpanded: Bool

    @State private var scrollOffset: CGFloat = 0
    @State private var scrollViewHeight: CGFloat = 100
    @State private var scrollContentHeight: CGFloat = 200

    var body: some View {
        VStack(spacing: 0) {
            if !isStatsExpanded && !keyStats.isEmpty {
                HStack(alignment: .lastTextBaseline, spacing: 4) {
                    let _ = DuplicateIDLogger.logDuplicates(in: keyStats)
                    ForEach(keyStats) { stat in
                        Text(stat.value)
                            .fontName(.calibreMedium, size: 32)
                            .foregroundColor(.chalk.dark700)
                            .multilineTextAlignment(.center)
                            .frame(maxWidth: .infinity)
                    }
                }
                .padding(.top, 16)

                HStack(alignment: .firstTextBaseline, spacing: 4) {
                    let _ = DuplicateIDLogger.logDuplicates(in: keyStats)
                    ForEach(keyStats) { stat in
                        Text(stat.title)
                            .fontStyle(.calibreUtility.s.regular)
                            .foregroundColor(.chalk.dark500)
                            .multilineTextAlignment(.center)
                            .frame(maxWidth: .infinity)
                    }
                }
                .padding(.top, 2)
            }

            if !allStats.isEmpty {
                if isStatsExpanded {
                    ScrollView {
                        VStack(spacing: 0) {
                            let _ = DuplicateIDLogger.logDuplicates(in: allStats)
                            ForEach(allStats) { stat in
                                HStack(spacing: 0) {
                                    Text(stat.title)
                                        .fontStyle(.calibreUtility.l.regular)
                                        .foregroundColor(.chalk.dark700)
                                    Spacer()
                                    Text(stat.value)
                                        .fontStyle(.calibreUtility.l.medium)
                                        .foregroundColor(.chalk.dark700)
                                }
                                .padding(.vertical, 8)
                                .padding(.trailing, 28)

                                DividerView()
                            }
                        }
                        .getSize { size in
                            scrollContentHeight = size.height
                        }
                        .getVerticalOffset(in: .named(Self.scrollViewNamespace)) { offset in
                            withAnimation(.linear(duration: 0.1)) {
                                scrollOffset = offset
                            }
                        }
                    }
                    .getSize { size in
                        scrollViewHeight = size.height
                    }
                    .coordinateSpace(name: Self.scrollViewNamespace)
                    .overlay(
                        Group {
                            if isTopGradientShowing {
                                LinearGradient(
                                    colors: [
                                        .chalk.dark200, .chalk.dark200.opacity(0),
                                    ],
                                    startPoint: .top,
                                    endPoint: .bottom
                                )
                                .frame(height: 64)
                            }
                        },
                        alignment: .top
                    )
                    .overlay(
                        Group {
                            if isBottomGradientShowing {
                                LinearGradient(
                                    colors: [
                                        .chalk.dark200, .chalk.dark200.opacity(0),
                                    ],
                                    startPoint: .bottom,
                                    endPoint: .top
                                )
                                .frame(height: 64)
                            }
                        },
                        alignment: .bottom
                    )
                }

                Button(
                    action: {
                        withAnimation {
                            isStatsExpanded.toggle()
                        }
                    }
                ) {
                    HStack(spacing: 8) {
                        Text(
                            isStatsExpanded
                                ? Strings.showLessStats.localized
                                : Strings.showMoreStats.localized
                        )
                        .fontStyle(.calibreUtility.l.regular)

                        Chevron(width: 10, height: 10, direction: isStatsExpanded ? .down : .up)
                    }
                    .foregroundColor(.chalk.dark500)
                    .padding(.top, 24)
                    .padding(.bottom, 16)
                }

                DividerView()
            }
        }
    }

    private var isTopGradientShowing: Bool {
        scrollOffset < -20
    }

    private var isBottomGradientShowing: Bool {
        scrollOffset > -(scrollContentHeight - scrollViewHeight)
    }
}

private struct GradeSubmission: View {
    @ObservedObject var viewModel: PlayerGradeDetailViewModel

    var showGradesTab: VoidClosure

    var body: some View {
        if viewModel.isLocked {
            ZStack(alignment: .center) {
                LockedGradeSubmission(
                    averageGrade: viewModel.averageGrade,
                    averageGradeSubtitle: viewModel.averageGradeSubtitle,
                    isAverageGradeDimmed: viewModel.isAverageGradeDimmed,
                    userGrade: viewModel.userGrade,
                    userGradeSubtitle: viewModel.yourGradeText
                )

                lockedGradeSectionSizingGuide
            }
        } else {
            ZStack(alignment: .top) {
                UnlockedGradeSection(
                    title: viewModel.gradeSectionTitle,
                    showCheckmark: viewModel.showGradeCheckmark,
                    userGrade: viewModel.userGrade,
                    averageGrade: viewModel.averageGrade,
                    averageGradeSubtitle: viewModel.averageGradeSubtitle,
                    isAverageGradeDimmed: viewModel.isAverageGradeDimmed,
                    isGradingInProgress: viewModel.isGradingInProgress,
                    onTapStar: viewModel.isLocked || viewModel.isGradingInProgress
                        ? nil
                        : { grade in
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            viewModel.gradeTapped(grade: grade)
                        },
                    onTapShowAllGrades: showGradesTab
                )

                unlockedGradeSectionSizingGuide
            }
        }
    }

    /// An invisible view to size the grade container in the locked state so that the screen layout remains consistent when paging between players..
    private var lockedGradeSectionSizingGuide: some View {
        LockedGradeSubmission(
            averageGrade: "3.0",
            averageGradeSubtitle: Strings.averageOfGradesPluralFormat.localized,
            isAverageGradeDimmed: false,
            userGrade: 3,
            userGradeSubtitle: Strings.gradesYourGradeFormat.localized
        )
        .opacity(0)
    }

    /// An invisible view to size the grade container so that the stars remain in a consistent position between graded and ungraded states.
    /// The graded state with an average grade state requires the most amount of space so we use the layout of this state to dictate the minimum container height.
    private var unlockedGradeSectionSizingGuide: some View {
        UnlockedGradeSection(
            title: Strings.gradeSubmitted.localized,
            showCheckmark: true,
            userGrade: 3,
            averageGrade: "3.0",
            averageGradeSubtitle: Strings.averageOfGradesPluralFormat.localized,
            isAverageGradeDimmed: false,
            isGradingInProgress: false,
            onTapStar: nil,
            onTapShowAllGrades: {}
        )
        .opacity(0)
    }
}

private struct LockedGradeSubmission: View {

    let averageGrade: String?
    let averageGradeSubtitle: String?
    let isAverageGradeDimmed: Bool

    let userGrade: Int?
    let userGradeSubtitle: String?

    var body: some View {
        VStack(spacing: 18) {
            if let average = averageGrade, let subtitle = averageGradeSubtitle {
                VStack(spacing: 0) {
                    HStack(spacing: 2) {
                        GradeStar(color: .chalk.yellow, isFilled: true, size: 16)
                        Text(average)
                            .fontName(.calibreMedium, size: 64)
                            .foregroundColor(
                                isAverageGradeDimmed ? .chalk.dark500 : .chalk.dark800
                            )
                            .padding(.top, -16)
                            .padding(.bottom, -8)
                    }

                    Text(subtitle)
                        .fontStyle(.calibreUtility.l.regular)
                        .foregroundColor(.chalk.dark500)
                }
            }

            if let userGrade = userGrade, let subtitle = userGradeSubtitle {
                VStack(spacing: 8) {
                    GradeStarsBar(
                        filledColor: .chalk.dark800,
                        unfilledColor: .chalk.dark800,
                        numberFilled: userGrade,
                        size: 12,
                        spacing: 3,
                        onTapStar: nil
                    )
                    Text(subtitle)
                        .fontStyle(.calibreUtility.l.regular)
                        .foregroundColor(.chalk.dark500)
                }
            }
        }
        .padding(.vertical, 8)
    }
}

private struct UnlockedGradeSection: View {

    let title: String?
    let showCheckmark: Bool
    let userGrade: Int?
    let averageGrade: String?
    let averageGradeSubtitle: String?
    let isAverageGradeDimmed: Bool
    let isGradingInProgress: Bool
    let onTapStar: ((Int) -> Void)?
    var onTapShowAllGrades: VoidClosure

    var body: some View {
        VStack(spacing: 0) {
            if let title {
                HStack(spacing: 6) {
                    Text(title)
                        .fontStyle(.calibreHeadline.s.medium)
                        .foregroundColor(.chalk.dark700)

                    Image(systemName: "checkmark.circle.fill")
                        .renderingMode(.template)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .foregroundColor(.chalk.dark800)
                        .frame(width: 18, height: 18)
                        .opacity(showCheckmark ? 1 : 0)
                }
                .padding(.bottom, 14)
            }

            AnimatedGradeStarsBar(
                filledColor: .chalk.dark800,
                unfilledColor: .chalk.dark800,
                numberFilled: userGrade ?? 0,
                size: 40,
                spacing: 8,
                onTapStar: onTapStar
            )

            if let average = averageGrade, let subtitle = averageGradeSubtitle {
                VStack(spacing: -2) {
                    HStack(spacing: 2) {
                        GradeStar(color: .chalk.yellow, isFilled: true, size: 10)

                        Text(average)
                            .fontStyle(.calibreHeadline.m.semibold)
                            .foregroundColor(
                                isAverageGradeDimmed
                                    ? .chalk.dark500
                                    : .chalk.dark800
                            )
                    }

                    Text(subtitle)
                        .fontStyle(.calibreUtility.s.regular)
                        .foregroundColor(.chalk.dark500)
                }
                .padding(.top, 16)
            }

            if userGrade == nil {
                Button(action: onTapShowAllGrades) {
                    HStack(spacing: 10) {
                        Text(Strings.seeAllPlayerGrades.localized)
                            .fontStyle(.calibreUtility.l.regular)
                            .foregroundColor(.chalk.dark800.opacity(0.7))

                        Chevron(
                            foregroundColor: .chalk.dark800,
                            width: 10,
                            height: 10
                        )
                    }
                }
                .padding(.top, 32)
            }
        }
        .padding(.vertical, 8)
    }
}

struct PlayerGradeDetailView_Previews: PreviewProvider {

    static func mockViewModel(
        playerId: String = "abc",
        isLocked: Bool = false,
        userGrade: Int? = 2
    ) -> PlayerGradeDetailViewModel {
        let gradesStore = PlayerGradesDataStore()
        gradesStore.storeGrade(
            gamePlayerId: playerId,
            entity: .init(
                id: "",
                average: 3.7,
                averageString: "3.7",
                gameId: "abc123",
                grade: userGrade,
                playerId: playerId,
                total: 65,
                updatedAt: Date(timeIntervalSince1970: 0)
            )
        )
        return PlayerGradeDetailViewModel(
            player: .init(
                id: playerId,
                player: .init(
                    id: "543",
                    headshots: []
                ),
                stats: [
                    Self.makeKeyStat(id: "1", header: "STAT", label: "QB Rating", value: "999"),
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
                    Self.makeExtraStat(id: "4", label: "Fumbles Lost", value: "9999"),
                    Self.makeExtraStat(id: "5", label: "On Target Throws (%)", value: "5/19"),
                    Self.makeExtraStat(id: "6", label: "Poor Throws (%)", value: "2"),
                    Self.makeExtraStat(id: "7", label: "Longest Pass (Yards)", value: "9999"),
                    Self.makeExtraStat(id: "8", label: "Sacks", value: "9999"),
                    Self.makeExtraStat(id: "9", label: "Knockdowns", value: "9999"),
                    Self.makeExtraStat(id: "10", label: "Throw Aways", value: "9999"),
                    Self.makeExtraStat(id: "11", label: "Passing Hurries", value: "9999"),
                    Self.makeExtraStat(id: "12", label: "Dropped Passes", value: "9999"),
                    Self.makeExtraStat(id: "13", label: "Passing Batted Passes", value: "9999"),
                ]
            ),
            teamAlias: "BST",
            teamLogos: [],
            gameId: "abc123",
            teamId: "team-1",
            leagueCode: .nfl,
            isGradingLocked: isLocked,
            entryPoint: .gradesTab,
            analytics: PlayerGradesAnalyticsTracker(gameId: "abc123", leagueCode: .nba),
            gradesStore: gradesStore
        )
    }

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

    static func makeExtraStat(
        id: String,
        label: String,
        value: String
    ) -> GQL.GamePlayerGradesTeam.LineUp.Player.Stat {
        .makeStringGameStat(
            id: id,
            statHeaderLabel: "",
            statLabel: label,
            statType: "",
            stringValue: value,
            statGroups: [.gradesExtra]
        )
    }

    static var previews: some View {
        Group {
            NavigationStack {
                PlayerGradeDetailView(
                    viewModel: mockViewModel(isLocked: false, userGrade: 2),
                    showGradesTab: {},
                    pageStatsExpansions: .constant([:]),
                    shouldShowUngradedToast: .constant(false)
                )
            }
            .previewDisplayName("Unlocked, 2 Grade")

            NavigationStack {
                PlayerGradeDetailView(
                    viewModel: mockViewModel(isLocked: true, userGrade: 2),
                    showGradesTab: {},
                    pageStatsExpansions: .constant([:]),
                    shouldShowUngradedToast: .constant(false)
                )
            }
            .previewDisplayName("Locked, 2 Grade")

            NavigationStack {
                PlayerGradeDetailView(
                    viewModel: mockViewModel(isLocked: false, userGrade: nil),
                    showGradesTab: {},
                    pageStatsExpansions: .constant([:]),
                    shouldShowUngradedToast: .constant(false)
                )
            }
            .previewDisplayName("Unlocked, No Grade")

            NavigationStack {
                PlayerGradeDetailView(
                    viewModel: mockViewModel(isLocked: true, userGrade: nil),
                    showGradesTab: {},
                    pageStatsExpansions: .constant([:]),
                    shouldShowUngradedToast: .constant(false)
                )
            }
            .previewDisplayName("Locked, No Grade")
        }
        .environmentObject(GradesDetailGameViewModel(gameId: "abc123"))
        .preferredColorScheme(.dark)
        .loadCustomFonts()
    }
}
