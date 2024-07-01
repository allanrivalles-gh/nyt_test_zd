//
//  GameLargeScoreHeader.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 12/1/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticScoresFoundation
import AthleticUI
import Foundation
import SwiftUI

private let headerNamespace = "GameLargeScoreHeader"

struct GameLargeScoreHeader: View {
    fileprivate struct TeamInfoDimensions {
        let edgeOffset: CGFloat
        let width: CGFloat
    }

    @ObservedObject var viewModel: GameLargeScoreHeaderViewModel

    @State private var isCompact: Bool = true
    @State private var width: CGFloat = 0
    @State private var firstTeamLargeFrame: CGRect = .zero
    @State private var secondTeamLargeFrame: CGRect = .zero

    private var topPadding: CGFloat {
        if viewModel.title != nil {
            return 0
        } else if isCompact {
            return 5
        } else {
            return 8
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            if let title = viewModel.title {
                Text(title)
                    .fontStyle(.calibreUtility.s.regular)
                    .foregroundColor(.chalk.dark500)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 8)
                    .padding(.bottom, 16)
            }
            HStack(spacing: 0) {
                TeamContent(
                    alignment: .left,
                    viewModel: viewModel,
                    teamViewModel: viewModel.firstTeam,
                    teamInfoLargeFrame: $firstTeamLargeFrame,
                    isCompact: $isCompact
                )
                if let centerMode = viewModel.centerMode {
                    switch centerMode {
                    case let .normal(viewModel):
                        StackedTitles(viewModel: viewModel)
                    case let .live(statusDetails):
                        switch statusDetails {
                        case let .mlb(viewModel):
                            MLBLiveStatusContent(viewModel: viewModel)
                        case let .soccer(viewModel):
                            SoccerLiveStatusContent(viewModel: viewModel)
                        case let .default(viewModel):
                            GeneralSportLiveStatusContent(viewModel: viewModel)
                        }
                    }
                }
                TeamContent(
                    alignment: .right,
                    viewModel: viewModel,
                    teamViewModel: viewModel.secondTeam,
                    teamInfoLargeFrame: $secondTeamLargeFrame,
                    isCompact: $isCompact
                )
            }
            .padding(.bottom, 16)

            if let gameDescription = viewModel.gameDescription {
                VStack(spacing: 0) {
                    DividerView()
                    Text(gameDescription)
                        .foregroundColor(.chalk.dark500)
                        .fontStyle(.calibreUtility.s.regular)
                        .multilineTextAlignment(.center)
                        .padding(.vertical, 4)
                        .padding(.horizontal, 16)
                    DividerView()
                }
            }

            if viewModel.showsBottomRow {
                BottomRow(
                    firstTeamForm: viewModel.firstTeam.form,
                    secondTeamForm: viewModel.secondTeam.form,
                    expectedGoals: viewModel.expectedGoals,
                    firstTeamLargeDimensions: firstTeamLargeDimensions,
                    secondTeamLargeDimensions: secondTeamLargeDimensions,
                    isCompact: isCompact
                )
            }
        }
        .coordinateSpace(name: headerNamespace)
        .padding(.top, topPadding)
        .background(BoxScoreUIConstant.headerColor)
        .getSize { size in
            isCompact = size.width < 500
            width = size.width
        }
    }

    private var firstTeamLargeDimensions: TeamInfoDimensions {
        TeamInfoDimensions(
            edgeOffset: firstTeamLargeFrame.origin.x,
            width: firstTeamLargeFrame.width
        )
    }

    private var secondTeamLargeDimensions: TeamInfoDimensions {
        TeamInfoDimensions(
            edgeOffset: width - secondTeamLargeFrame.maxX,
            width: secondTeamLargeFrame.width
        )
    }
}

private struct TeamContent: View {
    enum Alignment {
        case left, right
    }

    let alignment: Alignment

    @ObservedObject var viewModel: GameLargeScoreHeaderViewModel
    @ObservedObject var teamViewModel: GameLargeScoreHeaderViewModel.TeamViewModel

    @Binding var teamInfoLargeFrame: CGRect
    @Binding var isCompact: Bool

    var body: some View {
        if isCompact {
            TeamContentVertical(
                alignment: alignment,
                layoutMode: viewModel.layoutMode,
                layoutInfo: viewModel.layoutInfo,
                teamViewModel: teamViewModel,
                onSelectEntity: {
                    viewModel.trackTapEvent(for: $0)
                }
            )
        } else {
            TeamContentHorizontal(
                alignment: alignment,
                layoutInfo: viewModel.layoutInfo,
                teamViewModel: teamViewModel,
                teamInfoFrame: $teamInfoLargeFrame,
                onSelectEntity: {
                    viewModel.trackTapEvent(for: $0)
                }
            )
        }
    }
}

private struct TeamContentVertical: View {
    let alignment: TeamContent.Alignment
    let layoutMode: GameLargeScoreHeaderViewModel.LayoutMode
    let layoutInfo: (hasIndicator: Bool, hasRanking: Bool)

    @ObservedObject var teamViewModel: GameLargeScoreHeaderViewModel.TeamViewModel

    let onSelectEntity: (FollowingEntity) -> Void

    private var teamPadding: CGFloat {
        switch layoutMode {
        case .largeIcon:
            return 26
        case .smallIcon:
            return 16
        }
    }

    var body: some View {
        VStack(alignment: alignment == .left ? .leading : .trailing, spacing: 16) {
            HStack(spacing: 0) {
                if alignment == .right {
                    if let text = teamViewModel.score {
                        ScoreText(
                            text: text,
                            color: teamViewModel.scoreColor,
                            footer: teamViewModel.scoreFooter
                        )
                        .frame(maxWidth: .infinity)
                        .animation(.easeInOut(duration: 1.0), value: teamViewModel.score)
                    } else {
                        Spacer()
                    }
                }
                VStack(spacing: 1) {
                    if let entity = teamViewModel.followingEntity {
                        NavigationLink(
                            screen: .hubDetails(
                                entity: entity,
                                preferredTab: .feed
                            )
                        ) {
                            VerticalTeamInfo(
                                alignment: alignment,
                                layoutMode: layoutMode,
                                layoutInfo: layoutInfo,
                                teamViewModel: teamViewModel
                            )
                        }
                        .onSimultaneousTapGesture {
                            onSelectEntity(entity)
                        }
                        .id(entity.id)
                    } else {
                        VerticalTeamInfo(
                            alignment: alignment,
                            layoutMode: layoutMode,
                            layoutInfo: layoutInfo,
                            teamViewModel: teamViewModel
                        )
                    }

                    if let image = teamViewModel.subtitleImage {
                        SubtitleImage(image: image)
                    }
                }
                .padding(alignment == .left ? .leading : .trailing, teamPadding)

                if alignment == .left {
                    if let text = teamViewModel.score {
                        ScoreText(
                            text: text,
                            color: teamViewModel.scoreColor,
                            footer: teamViewModel.scoreFooter
                        )
                        .frame(maxWidth: .infinity)
                        .animation(.easeInOut(duration: 1.0), value: teamViewModel.score)
                    } else {
                        Spacer()
                    }
                }
            }
        }
    }
}

private struct VerticalTeamInfo: View {
    let alignment: TeamContent.Alignment
    let layoutMode: GameLargeScoreHeaderViewModel.LayoutMode
    let layoutInfo: (hasIndicator: Bool, hasRanking: Bool)

    @ObservedObject var teamViewModel: GameLargeScoreHeaderViewModel.TeamViewModel

    private var imageSize: CGFloat {
        switch layoutMode {
        case .largeIcon:
            return 56
        case .smallIcon:
            return 40
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            TeamLogoLazyImage(
                size: imageSize,
                resources: [teamViewModel.logoUrl.map { ATHImageResource(url: $0) }]
                    .compactMap { $0 }
            )
            VStack(spacing: 3) {
                if let text = teamViewModel.title {
                    TeamTitle(
                        alignment: alignment,
                        layoutInfo: layoutInfo,
                        isIndicatorVisible: teamViewModel.isTitleIndicatorVisible,
                        text: text,
                        ranking: teamViewModel.ranking
                    )
                }
                if let text = teamViewModel.subtitle {
                    SubtitleText(text: text)
                }
            }
        }
    }
}

private struct TeamContentHorizontal: View {
    let alignment: TeamContent.Alignment
    let layoutInfo: (hasIndicator: Bool, hasRanking: Bool)

    @ObservedObject var teamViewModel: GameLargeScoreHeaderViewModel.TeamViewModel
    @Binding var teamInfoFrame: CGRect

    let onSelectEntity: (FollowingEntity) -> Void

    var body: some View {
        HStack(spacing: 0) {
            Spacer()
            if let text = teamViewModel.score, alignment == .right {
                ScoreText(
                    text: text,
                    color: teamViewModel.scoreColor,
                    footer: teamViewModel.scoreFooter
                )
                .animation(.easeInOut(duration: 1.0), value: teamViewModel.score)
                Spacer()
            }
            Group {
                if let entity = teamViewModel.followingEntity {
                    NavigationLink(
                        screen: .hubDetails(
                            entity: entity,
                            preferredTab: .feed
                        )
                    ) {
                        HorizontalTeamInfo(
                            alignment: alignment,
                            layoutInfo: layoutInfo,
                            teamViewModel: teamViewModel
                        )
                    }
                    .onSimultaneousTapGesture {
                        onSelectEntity(entity)
                    }
                    .id(entity.id)
                } else {
                    HorizontalTeamInfo(
                        alignment: alignment,
                        layoutInfo: layoutInfo,
                        teamViewModel: teamViewModel
                    )
                }
            }
            .getFrame(in: .named(headerNamespace)) { frame in
                teamInfoFrame = frame
            }
            if let text = teamViewModel.score, alignment == .left {
                Spacer()
                ScoreText(
                    text: text,
                    color: teamViewModel.scoreColor,
                    footer: teamViewModel.scoreFooter
                )
                .animation(.easeInOut(duration: 1.0), value: teamViewModel.score)
            }
            Spacer()
        }
    }
}

private struct HorizontalTeamInfo: View {
    let alignment: TeamContent.Alignment
    let layoutInfo: (hasIndicator: Bool, hasRanking: Bool)

    @ObservedObject var teamViewModel: GameLargeScoreHeaderViewModel.TeamViewModel

    var body: some View {
        HStack(spacing: 16) {
            if alignment == .right {
                teamLogo
            }
            VStack(spacing: 3) {
                if let text = teamViewModel.title {
                    TeamTitle(
                        alignment: alignment,
                        layoutInfo: layoutInfo,
                        isIndicatorVisible: teamViewModel.isTitleIndicatorVisible,
                        text: text,
                        ranking: teamViewModel.ranking
                    )
                }
                if let text = teamViewModel.subtitle {
                    SubtitleText(text: text)
                }
                if let image = teamViewModel.subtitleImage {
                    SubtitleImage(image: image)
                }
            }
            if alignment == .left {
                teamLogo
            }
        }
    }

    private var teamLogo: some View {
        TeamLogoLazyImage(
            size: 56,
            resources: [teamViewModel.logoUrl.map { ATHImageResource(url: $0) }]
                .compactMap { $0 }
        )
    }
}

private struct ScoreText: View {
    private struct Constants {
        static let fontStyle: AthleticFont.Style = .calibreHeadline.xl.regular
    }

    let text: String
    let color: Color
    let footer: GameLargeScoreHeaderViewModel.TeamViewModel.ScoreFooter?

    @ScaledMetric var transitionVerticalInsertionOffset: CGFloat = 40
    @ScaledMetric var transitionVerticalRemovalOffset: CGFloat = 20

    var body: some View {
        ZStack {
            maximumScoreWidthSizingGuide.opacity(0)

            VStack(spacing: 2) {
                let textHeight = text.height(with: .preferredFont(for: Constants.fontStyle))
                scoreInfo(text)
                    .id(text)
                    .transition(
                        .slide(
                            insertionEdge: .top,
                            insertionOffsetY: textHeight - transitionVerticalInsertionOffset,
                            removalEdge: .bottom,
                            removalOffsetY: -textHeight + transitionVerticalRemovalOffset
                        )
                        .combined(with: .intervalFade(start: 0.5))
                    )
                switch footer {
                case let .indicatorLine(viewModel):
                    IndicatorLine(viewModel: viewModel)
                case nil:
                    EmptyView()
                }
            }
        }
    }

    @ViewBuilder
    private var maximumScoreWidthSizingGuide: some View {
        scoreInfo("000")
    }

    @ViewBuilder
    private func scoreInfo(_ score: String) -> some View {
        Text(score)
            .fontStyle(Constants.fontStyle)
            .foregroundColor(color)
            .fixedSize()
            .padding(.horizontal, 4)
    }
}

private struct TeamTitle: View {
    let alignment: TeamContent.Alignment
    let layoutInfo: (hasIndicator: Bool, hasRanking: Bool)
    let isIndicatorVisible: Bool
    let text: String
    let ranking: String?

    var body: some View {
        ZStack {
            maximumWidthSizingGuide.opacity(0)

            HStack(spacing: 7) {
                if isIndicatorVisible && alignment == .left {
                    TeamTitleIndicator()
                }

                teamInfo(ranking: ranking, shortName: text)

                if isIndicatorVisible && alignment == .right {
                    TeamTitleIndicator()
                }
            }
        }
    }

    @ViewBuilder
    private var maximumWidthSizingGuide: some View {
        HStack(spacing: 7) {
            if layoutInfo.hasIndicator {
                TeamTitleIndicator()
            }
            teamInfo(ranking: layoutInfo.hasRanking ? "00" : nil, shortName: "XXX")
        }
    }

    @ViewBuilder
    private func teamInfo(ranking: String?, shortName: String) -> some View {
        HStack(alignment: .firstTextBaseline, spacing: 6) {
            if let ranking = ranking {
                Text(ranking)
                    .fontStyle(.calibreUtility.xs.regular)
                    .foregroundColor(.chalk.dark500)
                    .fixedSize()
            }
            Text(shortName)
                .fontStyle(.calibreUtility.xl.medium)
                .foregroundColor(.chalk.dark700)
                .fixedSize()
        }
    }
}

private struct SubtitleText: View {
    let text: String

    var body: some View {
        Text(text)
            .fontStyle(.calibreUtility.s.regular)
            .foregroundColor(.chalk.dark500)
            .fixedSize()
    }
}

private struct SubtitleImage: View {
    let image: UIImage

    var body: some View {
        Image(uiImage: image)
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(width: 56, height: 10)
    }
}

private struct TeamTitleIndicator: View {
    var body: some View {
        Circle()
            .fill(Color.chalk.green)
            .frame(width: 6, height: 6)
    }
}

private struct StackedTitles: View {
    let viewModel: GameStackedTitlesViewModel

    var body: some View {
        VStack(spacing: 2) {
            if let pretitle = viewModel.pretitle {
                Text(pretitle.text)
                    .fontStyle(pretitle.style ?? .calibreUtility.s.regular)
                    .foregroundColor(pretitle.color ?? .chalk.dark500)
            }
            if let title = viewModel.title {
                Text(title.text)
                    .fontStyle(title.style ?? .calibreUtility.l.medium)
                    .foregroundColor(title.color ?? .chalk.dark700)
            }
            if let subtitle1 = viewModel.subtitle1 {
                Text(subtitle1.text)
                    .fontStyle(subtitle1.style ?? .calibreUtility.s.regular)
                    .foregroundColor(subtitle1.color ?? .chalk.dark500)
            }
            if let subtitle2 = viewModel.subtitle2 {
                Text(subtitle2.text)
                    .fontStyle(subtitle2.style ?? .calibreUtility.s.regular)
                    .foregroundColor(subtitle2.color ?? .chalk.dark500)
            }
        }
    }
}

private struct GeneralSportLiveStatusContent: View {
    let viewModel: GameGeneralSportStatusViewModel

    var body: some View {
        ZStack {
            maximumClockWidthSizingGuide.opacity(0)

            VStack(spacing: 2) {
                if viewModel.isDelayed {
                    Text(Strings.delay.localized.uppercased())
                        .fontStyle(.calibreUtility.l.medium)
                        .foregroundColor(.chalk.red)
                }
                if let gameStatusDisplayMain = viewModel.gameStatusDisplayMain {
                    Text(gameStatusDisplayMain)
                        .fontStyle(.calibreUtility.l.medium)
                        .foregroundColor(.chalk.dark800)
                }
                if let text = viewModel.gameClockText {
                    /// Note: There is a bug in SwiftUI where the transition did not work. Wrapping it
                    ///  with VStack / HStack resolves the issue.
                    ///  Refer to https://www.objc.io/blog/2022/04/14/transitions/ for more info.
                    HStack {
                        gameClockText(text)
                            .id(text)
                            .transition(.intervalFade(start: 0.5))
                    }
                    .animation(.easeIn(duration: 1.0), value: viewModel.gameClockText)
                }
            }

        }
    }

    @ViewBuilder
    private var maximumClockWidthSizingGuide: some View {
        gameClockText("00:00")
    }

    @ViewBuilder
    private func gameClockText(_ text: String) -> some View {
        Text(text)
            .fontStyle(.calibreUtility.l.regular)
            .foregroundColor(.chalk.red)
    }
}

private struct SoccerLiveStatusContent: View {
    let viewModel: GameSoccerStatusViewModel

    var body: some View {
        ZStack {
            maximumClockWidthSizingGuide.opacity(0)

            VStack(spacing: 2) {
                if viewModel.isDelayed {
                    Text(Strings.delay.localized.uppercased())
                        .fontStyle(.calibreUtility.l.medium)
                        .foregroundColor(.chalk.red)
                }
                if let text = viewModel.gameClockText {
                    /// Note: There is a bug in SwiftUI where the transition did not work. Wrapping it
                    ///  with VStack / HStack resolves the issue.
                    ///  Refer to https://www.objc.io/blog/2022/04/14/transitions/ for more info.
                    HStack {
                        gameClockText(text)
                            .id(text)
                            .transition(.intervalFade(start: 0.5))
                    }
                    .animation(.default, value: viewModel.gameClockText)
                }
                if let text = viewModel.bottomText {
                    Text(text)
                        .fontStyle(.calibreUtility.s.regular)
                        .foregroundColor(.chalk.dark500)
                }
            }
        }
    }

    @ViewBuilder
    private var maximumClockWidthSizingGuide: some View {
        gameClockText("000'+00'")
    }

    @ViewBuilder
    private func gameClockText(_ text: String) -> some View {
        Text(text)
            .fontStyle(.calibreHeadline.s.medium)
            .foregroundColor(.chalk.red)
    }
}

private struct MLBLiveStatusContent: View {
    let viewModel: GameMLBStatusViewModel

    var body: some View {
        ZStack {
            maximumWidthSizingGuide.opacity(0)

            VStack(spacing: 4) {
                if viewModel.isDelayed {
                    Text(Strings.delay.localized.uppercased())
                        .fontStyle(.calibreUtility.l.medium)
                        .foregroundColor(.chalk.red)
                }

                /// Note: There is a bug in SwiftUI where the transition did not work. Wrapping it
                ///  with VStack / HStack resolves the issue.
                ///  Refer to https://www.objc.io/blog/2022/04/14/transitions/ for more info.
                VStack(spacing: 4) {
                    if let inningText = viewModel.gameInfo.inningText {
                        Text(inningText)
                            .fontStyle(.calibreUtility.xs.medium)
                            .foregroundColor(.chalk.red)
                            .id(inningText)
                            .transition(.intervalFade(start: 0.5))
                    }

                    BaseballBasesDiamond(
                        highlighting: viewModel.gameInfo.basesHighlighting,
                        baseSize: 9,
                        animationProperties: (isAnimated: true, duration: 1.0)
                    )

                    if let text = viewModel.gameInfo.bottomText {
                        Text(text)
                            .fontStyle(.calibreUtility.xs.medium)
                            .foregroundColor(.chalk.dark800)
                            .id(text)
                            .transition(.intervalFade(start: 0.5))
                    }
                }
                .animation(.easeInOut(duration: 1.0), value: viewModel.gameInfo)
            }
        }
    }

    @ViewBuilder
    private var maximumWidthSizingGuide: some View {
        Text("0-0, 0 OUT")
            .fontStyle(.calibreUtility.xs.medium)
    }
}

private struct BottomRow: View {

    let firstTeamForm: TeamFormChartViewModel?
    let secondTeamForm: TeamFormChartViewModel?
    let expectedGoals: GameLargeScoreHeaderViewModel.ExpectedGoals?

    let firstTeamLargeDimensions: GameLargeScoreHeader.TeamInfoDimensions
    let secondTeamLargeDimensions: GameLargeScoreHeader.TeamInfoDimensions
    let isCompact: Bool

    var body: some View {
        ZStack {
            HStack(spacing: 0) {
                if let form = firstTeamForm {
                    TeamFormChart(viewModel: form)
                        .padding(.vertical, 8)
                        .frame(minWidth: isCompact ? nil : firstTeamLargeDimensions.width)
                }

                Spacer(minLength: 8)

                if let form = secondTeamForm {
                    TeamFormChart(viewModel: form)
                        .padding(.vertical, 8)
                        .frame(minWidth: isCompact ? nil : secondTeamLargeDimensions.width)
                }
            }
            .padding(.leading, isCompact ? 16 : firstTeamLargeDimensions.edgeOffset)
            .padding(.trailing, isCompact ? 16 : secondTeamLargeDimensions.edgeOffset)

            if let expectedGoals = expectedGoals {
                SoccerExpectedGoalsComparison(
                    firstTeam: expectedGoals.firstTeam,
                    secondTeam: expectedGoals.secondTeam
                )
                .padding(.vertical, 4)
            }
        }
    }
}

// MARK: - Previews

struct GameLargeScoreHeader_Previews: PreviewProvider {
    @ViewBuilder
    static var content: some View {
        VStack {
            GameLargeScoreHeader(
                viewModel: {
                    let viewModel = GameLargeScoreHeaderViewModel(
                        navigationTitle: "GSW @ MIL",
                        layoutMode: .largeIcon,
                        centerMode: .normal(
                            GameStackedTitlesViewModel(
                                pretitle: .init(text: "Fri, 14 Jan"),
                                title: .init(text: "11:30 am", style: .calibreHeadline.s.medium),
                                subtitle1: .init(
                                    text: "Agg. 3-1",
                                    style: .calibreUtility.s.regular
                                ),
                                subtitle2: .init(text: "TNT", style: .calibreUtility.xs.regular)
                            )
                        )
                    )
                    viewModel.firstTeam.title = "GSW"
                    viewModel.firstTeam.ranking = "1"
                    viewModel.firstTeam.subtitle = "(30-10)"
                    viewModel.firstTeam.form = TeamFormChartViewModel(form: "")
                    viewModel.secondTeam.title = "MIL"
                    viewModel.secondTeam.ranking = "25"
                    viewModel.secondTeam.subtitle = "(26-17)"
                    viewModel.secondTeam.form = TeamFormChartViewModel(form: "WWD")
                    return viewModel
                }()
            )
            GameLargeScoreHeader(
                viewModel: {
                    let viewModel = GameLargeScoreHeaderViewModel(
                        navigationTitle: "CHA @ PHI",
                        layoutMode: .largeIcon,
                        centerMode: .normal(
                            GameStackedTitlesViewModel(
                                pretitle: nil,
                                title: .init(text: "FINAL", style: .calibreUtility.l.medium),
                                subtitle1: .init(
                                    text: "Thu, 13 Jan",
                                    style: .calibreUtility.s.regular
                                ),
                                subtitle2: nil
                            )
                        )
                    )
                    viewModel.firstTeam.title = "CHA"
                    viewModel.firstTeam.ranking = "1"
                    viewModel.firstTeam.score = "109"
                    viewModel.firstTeam.scoreColor = .chalk.dark800
                    viewModel.secondTeam.title = "UCONN"
                    viewModel.secondTeam.ranking = "25"
                    viewModel.secondTeam.score = "98"
                    viewModel.secondTeam.scoreColor = .chalk.dark500
                    return viewModel
                }()
            )
            GameLargeScoreHeader(
                viewModel: {
                    let viewModel = GameLargeScoreHeaderViewModel(
                        navigationTitle: "CHA @ PHI",
                        layoutMode: .largeIcon,
                        centerMode: .normal(
                            GameStackedTitlesViewModel(
                                pretitle: nil,
                                title: .init(text: "FINAL", style: .calibreUtility.l.medium),
                                subtitle1: .init(
                                    text: "Thu, 13 Jan",
                                    style: .calibreUtility.s.regular
                                ),
                                subtitle2: nil
                            )
                        )
                    )
                    viewModel.firstTeam.title = "CHA"
                    viewModel.firstTeam.ranking = "1"
                    viewModel.firstTeam.score = "109"
                    viewModel.firstTeam.scoreColor = .chalk.dark800
                    viewModel.firstTeam.scoreFooter = .indicatorLine(
                        IndicatorLineViewModel(
                            items: [
                                IndicatorLineViewModel.Item(isHighlighted: true),
                                IndicatorLineViewModel.Item(isHighlighted: true),
                                IndicatorLineViewModel.Item(isHighlighted: true),
                                IndicatorLineViewModel.Item(isHighlighted: false),
                            ]
                        )
                    )
                    viewModel.secondTeam.title = "PHI"
                    viewModel.secondTeam.ranking = "25"
                    viewModel.secondTeam.score = "98"
                    viewModel.secondTeam.scoreColor = .chalk.dark500
                    viewModel.secondTeam.scoreFooter = .indicatorLine(
                        IndicatorLineViewModel(
                            items: [
                                IndicatorLineViewModel.Item(isHighlighted: true),
                                IndicatorLineViewModel.Item(isHighlighted: false),
                                IndicatorLineViewModel.Item(isHighlighted: false),
                            ]
                        )
                    )
                    return viewModel
                }()
            )
            Spacer()
        }
    }

    static var previews: some View {
        content
            .preferredColorScheme(.dark)
        content
            .preferredColorScheme(.light)
    }
}
