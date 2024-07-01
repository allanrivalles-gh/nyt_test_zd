//
//  ScoresScheduleGameRow.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 24/2/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticNavigation
import AthleticScoresFoundation
import AthleticUI
import SwiftUI

public struct ScheduleGameRow: View {
    private enum Constants {
        static let infoColumnWidth: CGFloat = 144
    }

    @Environment(\.redactionReasons) var reasons
    @ObservedObject var viewModel: ScheduleGameViewModel
    let containerProxy: GeometryProxy

    @ScaledMetric var transitionHorizontalOffset: CGFloat = 10

    @EnvironmentObject private var navigation: NavigationModel

    public init(
        viewModel: ScheduleGameViewModel,
        containerProxy: GeometryProxy
    ) {
        self.viewModel = viewModel
        self.containerProxy = containerProxy
    }

    public var body: some View {
        VStack(spacing: 8) {
            if let header = viewModel.header {
                HStack(spacing: 0) {
                    Text(header)
                        .fontStyle(.calibreUtility.xs.regular)
                        .foregroundColor(.chalk.dark500)
                        .multilineTextAlignment(.leading)

                    Spacer(minLength: 16)
                }
            }

            HStack(spacing: 0) {
                VStack(spacing: 6) {
                    TeamRow(
                        team: viewModel.topTeam,
                        otherTeamRanking: viewModel.bottomTeam.ranking
                    )
                    .animation(.easeInOut(duration: 0.6), value: viewModel.topTeam)

                    TeamRow(
                        team: viewModel.bottomTeam,
                        otherTeamRanking: viewModel.topTeam.ranking
                    )
                    .animation(.easeInOut(duration: 0.6), value: viewModel.bottomTeam)
                }

                HStack(spacing: 0) {
                    VStack(alignment: .leading, spacing: 0) {
                        let _ = DuplicateIDLogger.logDuplicates(
                            in: viewModel.gameInfo.text,
                            id: \.string
                        )
                        ForEach(viewModel.gameInfo.text, id: \.string) { text in
                            let fontStyle = text.style.fontStyle
                            let textWidth = text.string.width(
                                with: .preferredFont(for: fontStyle)
                            )

                            Text(text.string)
                                .foregroundColor(text.style.color)
                                .fontStyle(fontStyle)
                                .multilineTextAlignment(.leading)
                                .transition(
                                    .slide(
                                        insertionEdge: .trailing,
                                        insertionOffsetX: -textWidth + transitionHorizontalOffset,
                                        removalEdge: .leading,
                                        removalOffsetX: textWidth - transitionHorizontalOffset
                                    )
                                    .combined(with: .opacity)
                                )
                        }
                    }
                    .padding(.leading, 15)

                    Spacer(minLength: 2)

                    switch viewModel.gameInfo.widget {
                    case .baseballBases(let highlighted):
                        BaseballBasesDiamond(
                            highlighting: highlighted,
                            baseSize: 12,
                            animationProperties: (isAnimated: true, duration: 0.6)
                        )
                    case .none:
                        EmptyView()
                    }

                    /// This acts as trailing padding on the row, while allowing the widget to be centered in the space available
                    Spacer(minLength: 16)
                }
                .frame(width: Constants.infoColumnWidth)
                .animation(.easeInOut(duration: 0.6), value: viewModel.gameInfo)
                .if(reasons.contains(.placeholder)) {
                    $0.shimmering()
                }
            }
            .overlay(alignment: .trailing) {
                DividerView(axis: .vertical)
                    .offset(x: -Constants.infoColumnWidth)
            }

            if let footer = viewModel.footer {
                HStack(spacing: 0) {
                    Text(footer)
                        .fontStyle(.calibreUtility.xs.regular)
                        .foregroundColor(.chalk.dark500)
                        .multilineTextAlignment(.leading)

                    Spacer(minLength: 16)
                }
            }

            if let discussionLinkText = viewModel.discussionLinkText {
                NavigationLink(
                    screen: .scores(
                        .boxScore(
                            BoxScoreDestination(
                                gameId: viewModel.gameId,
                                initialSelectionOverride: .comments(nil)
                            )
                        )
                    )
                ) {
                    DiscussionCtaView(discussionLinkText)
                }
                .padding(.trailing, 16)
                .padding(.top, 2)
                .onSimultaneousTapGesture {
                    viewModel.trackClickDiscussionCta()
                }
            }
        }
        .padding(.leading, 16)
        .padding(.vertical, 12)
        .background(Color.chalk.dark200)
        .contextMenu {
            viewModel.contextMenuActions.menuButtons
        }
        .if(!reasons.contains(.placeholder)) {
            $0.trackImpressions(
                with: viewModel.impressionManager,
                record: viewModel.analyticData.impress,
                containerProxy: containerProxy
            )
        }
    }
}

private struct TeamRow: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.redactionReasons) var reasons

    private struct Constants {
        static let rankingStandardWidth: CGFloat = 13
    }

    let team: ScheduleGameViewModel.Team
    let otherTeamRanking: String?

    @ScaledMetric var transitionVerticalOffset: CGFloat = 12

    private var isDarkMode: Bool {
        colorScheme == .dark
    }

    private var logoOpacity: Double {
        if team.isLogoDimmed {
            return isDarkMode ? 0.4 : 0.3
        } else {
            return 1
        }
    }

    var body: some View {
        HStack(spacing: 0) {
            TeamLogoLazyImage(size: 24, resources: team.logos)
                .opacity(logoOpacity)
                .if(reasons.contains(.placeholder)) {
                    $0.shimmering()
                }

            HStack(alignment: .center, spacing: 0) {
                HStack(alignment: .center, spacing: 0) {
                    if hasRankingColumn {
                        ZStack(alignment: .trailing) {
                            if let thisRanking = team.ranking {
                                RankingText(ranking: thisRanking)
                            }

                            if let otherRanking = otherTeamRanking {
                                /// A sizing guide so the same amount of space is allocated to both teams
                                RankingText(ranking: otherRanking)
                                    .opacity(0)
                            }
                        }
                        .frame(minWidth: 12, alignment: .trailing)
                        .padding(.leading, 12)
                    }

                    Text(team.name)
                        .fontStyle(.calibreUtility.xl.medium)
                        .foregroundColor(teamTextColor)
                        .lineLimit(1)
                        .padding(.leading, hasRankingColumn ? 4 : 16)
                }

                let _ = DuplicateIDLogger.logDuplicates(
                    in: Array(team.icons.indices),
                    id: \.self
                )
                ForEach(team.icons.indices, id: \.self) { index in
                    Group {
                        switch team.icons[index] {
                        case .americanFootballPossession:
                            Image("icn_american_football_possession")
                                .foregroundColor(.chalk.dark800)
                        case .redCard:
                            Image("icn_soccer_red_card")
                        }
                    }
                    .padding(.leading, 8)
                }
            }
            .if(reasons.contains(.placeholder)) {
                $0.shimmering()
            }

            Spacer(minLength: 8)

            Group {
                if let trailingInfo = team.trailingInfo {
                    switch trailingInfo {
                    case .score(let scoreInfo):
                        HStack(alignment: .center, spacing: 6) {
                            if let score = scoreInfo.score {
                                let fontStyle: AthleticFont.Style = .calibreHeadline.s.medium
                                let textHeight = score.height(
                                    with: .preferredFont(for: fontStyle)
                                )

                                Text(score)
                                    .fontStyle(fontStyle)
                                    .fixedSize()
                                    .id(score)
                                    .transition(
                                        .slide(
                                            insertionEdge: .top,
                                            insertionOffsetY: textHeight - transitionVerticalOffset,
                                            removalEdge: .bottom,
                                            removalOffsetY: -textHeight + transitionVerticalOffset
                                        )
                                        .combined(with: .opacity)
                                    )
                            }

                            if let penaltyScore = scoreInfo.penaltyScore {
                                let fontStyle: AthleticFont.Style = .calibreUtility.l.regular
                                let textHeight = penaltyScore.height(
                                    with: .preferredFont(for: fontStyle)
                                )

                                Text(penaltyScore)
                                    .fontStyle(fontStyle)
                                    .fixedSize()
                                    .id(penaltyScore)
                                    .transition(
                                        .slide(
                                            insertionEdge: .top,
                                            insertionOffsetY: textHeight - transitionVerticalOffset,
                                            removalEdge: .bottom,
                                            removalOffsetY: -textHeight + transitionVerticalOffset
                                        )
                                        .combined(with: .opacity)
                                    )
                            }
                        }
                        .foregroundColor(teamTextColor)

                        Image("victor_pointer_white")
                            .renderingMode(.template)
                            .foregroundColor(.chalk.dark800)
                            .padding(.leading, 8)
                            .opacity(scoreInfo.isPointerShowing ? 1 : 0)

                    case .plainText(let text):
                        Text(text)
                            .fontStyle(.calibreUtility.xs.regular)
                            .foregroundColor(.chalk.dark500)
                            .multilineTextAlignment(.trailing)
                            .padding(.trailing, 16)
                    }
                }
            }
            .if(reasons.contains(.placeholder)) {
                $0.shimmering()
            }
        }
    }

    private var hasRankingColumn: Bool {
        team.ranking != nil || otherTeamRanking != nil
    }

    private var teamTextColor: Color {
        switch team.textVisibility {
        case .full:
            return .chalk.dark800

        case .lightlyDimmed:
            return .chalk.dark600

        case .veryDimmed:
            return .chalk.dark500
        }
    }
}

private struct RankingText: View {
    let ranking: String

    var body: some View {
        Text(ranking)
            .fontStyle(.calibreUtility.xs.regular)
            .foregroundColor(.chalk.dark500)
    }
}

private struct DiscussionCtaView: View {
    let text: String

    init(_ text: String) {
        self.text = text
    }

    var body: some View {
        HStack(alignment: .center, spacing: 0) {
            RedCircleBadge(
                size: 5,
                topPadding: 1
            )

            Text(text)
                .fontStyle(.calibreUtility.s.medium)
                .foregroundColor(.chalk.dark700)
                .padding(.leading, 3)

            Chevron(
                foregroundColor: .chalk.dark800,
                width: 5,
                height: 9,
                direction: .right,
                topPadding: 2
            )
            .padding(.leading, 7)
        }
    }
}

extension ScheduleGameViewModel.GameInfo.Text.Style {
    var color: Color {
        switch self {
        case .default:
            return .chalk.dark500
        case .whiteBold, .whiteNormal:
            return .chalk.dark800
        case .live:
            return .chalk.red
        }
    }

    var fontStyle: AthleticFont.Style {
        switch self {
        case .whiteNormal, .default:
            return .calibreUtility.s.regular
        case .whiteBold, .live:
            return .calibreUtility.s.medium
        }
    }
}

struct ScoresLandingGameRow_Previews: PreviewProvider {

    private static var content: some View {
        GeometryReader { containerProxy in
            ScrollView {
                VStack(alignment: .leading) {
                    Section("Pre-Game") {
                        Text("NBA")
                        ScheduleGameRow(
                            viewModel: .init(
                                model: SchedulePreviewMocks.preGameNBA,
                                impressionManager: AnalyticsManagers.scoresImpressions,
                                impressionRecord: makePreviewImpressionRecord(),
                                clickAnalyticsIndexV: nil
                            ),
                            containerProxy: containerProxy
                        )

                        Text("Premier League (Following)")
                        ScheduleGameRow(
                            viewModel: .init(
                                model: SchedulePreviewMocks.soccerPreGame,
                                impressionManager: AnalyticsManagers.scoresImpressions,
                                impressionRecord: makePreviewImpressionRecord(),
                                clickAnalyticsIndexV: nil
                            ),
                            containerProxy: containerProxy
                        )

                        Text("Knockout TBD Teams")
                        ScheduleGameRow(
                            viewModel: .init(
                                model: SchedulePreviewMocks.soccerKnockoutPreGame,
                                impressionManager: AnalyticsManagers.scoresImpressions,
                                impressionRecord: makePreviewImpressionRecord(),
                                clickAnalyticsIndexV: nil
                            ),
                            containerProxy: containerProxy
                        )
                    }
                    Section("In-Game") {
                        Text("NCAA")
                        ScheduleGameRow(
                            viewModel: .init(
                                model: SchedulePreviewMocks.ncaaInGame,
                                impressionManager: AnalyticsManagers.scoresImpressions,
                                impressionRecord: makePreviewImpressionRecord(),
                                clickAnalyticsIndexV: nil
                            ),
                            containerProxy: containerProxy
                        )

                        Text("Baseball")
                        ScheduleGameRow(
                            viewModel: .init(
                                model: SchedulePreviewMocks.baseballInGame,
                                impressionManager: AnalyticsManagers.scoresImpressions,
                                impressionRecord: makePreviewImpressionRecord(),
                                clickAnalyticsIndexV: nil
                            ),
                            containerProxy: containerProxy
                        )
                    }
                    Section("Post-Game") {
                        Text("Soccer Shootout")
                        ScheduleGameRow(
                            viewModel: .init(
                                model: SchedulePreviewMocks.soccerShootout,
                                impressionManager: AnalyticsManagers.scoresImpressions,
                                impressionRecord: makePreviewImpressionRecord(),
                                clickAnalyticsIndexV: nil
                            ),
                            containerProxy: containerProxy
                        )
                    }

                    Spacer()
                }
            }
            .padding(.top, 50)
        }
    }

    private static func makePreviewImpressionRecord() -> AnalyticsImpressionRecord {
        AnalyticsImpressionRecord(
            verb: .impress,
            view: .scores,
            element: .home,
            objectType: .gameId,
            objectIdentifier: "-1",
            requiredValues: ScoresEnvironment.shared.makeAnalyticsDefaults()
        )
    }

    static var previews: some View {
        content
            .padding(1)
            .preferredColorScheme(.dark)
            .previewDisplayName("Dark Mode")
            .previewLayout(.fixed(width: 375, height: 2000))
        content
            .preferredColorScheme(.light)
            .previewDisplayName("Light Mode")
            .previewLayout(.fixed(width: 375, height: 2000))
    }
}
