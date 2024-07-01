//
//  GamePlayRow.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 15/12/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticComments
import AthleticFoundation
import AthleticScoresFoundation
import AthleticUI
import Combine
import Foundation
import SwiftUI

struct GamePlayRow: View {
    private struct Constants {
        static let imageSize: CGFloat = 24
    }

    let viewModel: GamePlayViewModel

    private var shouldShowTitles: Bool {
        viewModel.titlePrefix != nil || viewModel.titleSuffix != nil
    }

    var body: some View {
        HStack {
            HStack(alignment: viewModel.contentAlignment, spacing: 12) {
                VStack(spacing: 4) {
                    switch viewModel.image {
                    case let .team(resources):
                        TeamLogoLazyImage(size: Constants.imageSize, resources: resources)

                    case let .player(resources, colorHex):
                        HeadshotLazyImage(
                            size: Constants.imageSize,
                            resources: resources,
                            contentMode: .fill,
                            backgroundColor: colorHex.map { Color(hex: $0) } ?? .clear
                        )

                    case nil:
                        EmptyView()
                    }

                    if let leadingText = viewModel.leadingText {
                        Text(leadingText)
                            .fontStyle(.calibreUtility.s.regular)
                            .foregroundColor(.chalk.dark500)
                            .fixedSize()
                    }
                }
                .frame(width: Constants.imageSize)

                VStack(spacing: 2) {
                    if shouldShowTitles {
                        HStack(spacing: 0) {
                            HStack(spacing: 6) {
                                if let text = viewModel.titlePrefix {
                                    Text(text)
                                        .fontStyle(.calibreUtility.l.medium)
                                        .foregroundColor(.chalk.dark700)
                                }
                                if let text = viewModel.titleSuffix {
                                    Text(text)
                                        .fontStyle(.calibreUtility.s.regular)
                                        .foregroundColor(.chalk.dark500)
                                }
                            }
                            Spacer(minLength: 0)
                        }
                    }
                    if let info = viewModel.info {
                        Text(info)
                            .fontStyle(.calibreUtility.s.regular)
                            .foregroundColor(.chalk.dark500)
                            .frame(
                                maxWidth: .infinity,
                                alignment: .leading
                            )
                            .fixedSize(horizontal: false, vertical: true)
                    }
                }
                if let trailingInfo = viewModel.trailingInfo {
                    switch trailingInfo {
                    case let .scores(viewModel):
                        GamePlayScoreDetail(viewModel: viewModel)

                    case let .result(result):
                        Text(result.title)
                            .fontStyle(.calibreUtility.xs.medium)
                            .foregroundColor(result.color)
                    case let .icon(imageName):
                        Image(imageName)
                            .frame(width: 24, height: 24)
                    }
                }
            }
            .padding(.vertical, 12)
            .padding(.horizontal, 16)
        }
        .frame(maxWidth: .infinity)
        .background(alignment: .topLeading) {
            if let curtainColor = viewModel.curtainColor.map({ Color(hex: $0) }) {
                TeamColorCurtain(
                    color: curtainColor,
                    sizeVariant: .small,
                    position: .leading
                )
                .frame(width: 60)
                .clipped()
            }
        }
        .background(Color.chalk.dark200)
        .clipped()
        .commentablePlay(CommentingPlay(viewModel: viewModel))
    }
}

struct BoxScoreDiscussiblePlayContextAnalytics {
    let gamePhase: GamePhase
    let gameId: String
    let leagueId: String
}

struct BoxScoreDiscussiblePlayContext {
    let analytics: BoxScoreDiscussiblePlayContextAnalytics
    let discuss: (CommentingPlay) -> Void
}

private struct BoxScoreDiscussPlayContextKey: EnvironmentKey {
    static var defaultValue: BoxScoreDiscussiblePlayContext? = nil
}

private struct BoxScoreDiscussPlayOriginTabKey: EnvironmentKey {
    static var defaultValue: GamePagingViewModel.Tab? = nil
}

extension EnvironmentValues {
    var boxScoreDiscussPlayContext: BoxScoreDiscussiblePlayContext? {
        get { self[BoxScoreDiscussPlayContextKey.self] }
        set { self[BoxScoreDiscussPlayContextKey.self] = newValue }
    }

    var boxScoreDiscussPlayOriginTab: GamePagingViewModel.Tab? {
        get { self[BoxScoreDiscussPlayOriginTabKey.self] }
        set { self[BoxScoreDiscussPlayOriginTabKey.self] = newValue }
    }
}

extension CommentingPlay {
    fileprivate init(viewModel: GamePlayViewModel) {
        let description = [viewModel.titleSuffix, viewModel.info]
            .compactMap { $0 }
            .joined(separator: "\n")
        self.init(
            id: viewModel.id,
            description: description,
            occurredAtString: viewModel.occurredAtString
        )
    }
}

// MARK: - Previews

struct BoxScoreScoringPlayRow_Previews: PreviewProvider {
    static var previews: some View {
        content
            .preferredColorScheme(.dark)
        content
            .preferredColorScheme(.light)
    }

    private static var content: some View {
        VStack(spacing: 0) {
            let _ = DuplicateIDLogger.logDuplicates(in: viewModels)
            ForEach(viewModels) {
                GamePlayRow(viewModel: $0)
                if $0.id != viewModels.last?.id {
                    DividerView()
                }
            }
            .padding(.horizontal, 16)
            .background(Color.chalk.dark200)
            Spacer()
        }
    }

    private static var viewModels: [GamePlayViewModel] {
        [
            GamePlayViewModel(
                id: "1",
                titlePrefix: "Television Timeout",
                titleSuffix: "2:57",
                occurredAtString: "1667954357000"
            ),
            GamePlayViewModel(
                id: "2",
                image: .team([]),
                titleSuffix: "2:57",
                info:
                    "Placerat at gravida tortor interdum. Amet fringilla adipiscing ipsum aenean",
                occurredAtString: "1667954357000"
            ),
            GamePlayViewModel(
                id: "3",
                titleSuffix: "2:57",
                info:
                    "Placerat at gravida tortor interdum. Amet fringilla adipiscing ipsum aenean",
                occurredAtString: "1667954357000",
                trailingInfo: .scores(
                    GamePlayScoreDetailViewModel(
                        firstScore: GamePlayScoreViewModel(title: "NYU", value: "1"),
                        secondScore: GamePlayScoreViewModel(title: "PIT", value: "0")
                    )
                )
            ),
            GamePlayViewModel(
                id: "4",
                info:
                    "Placerat at gravida tortor interdum. Amet fringilla adipiscing ipsum aenean",
                occurredAtString: "1667954357000",
                trailingInfo: .scores(
                    GamePlayScoreDetailViewModel(
                        firstScore: GamePlayScoreViewModel(title: "NYU", value: "1"),
                        secondScore: GamePlayScoreViewModel(title: "PIT", value: "0")
                    )
                )
            ),
            GamePlayViewModel(
                id: "5",
                image: .team([]),
                info:
                    "Placerat at gravida tortor interdum. Amet fringilla adipiscing ipsum aenean",
                occurredAtString: "1667954357000",
                trailingInfo: .scores(
                    GamePlayScoreDetailViewModel(
                        firstScore: GamePlayScoreViewModel(title: "NYU", value: "10"),
                        secondScore: GamePlayScoreViewModel(title: "PIT", value: "10")
                    )
                )
            ),
            GamePlayViewModel(
                id: "6",
                image: .team([]),
                info: "Placerat at gravida tortor",
                occurredAtString: "1667954357000",
                trailingInfo: .scores(
                    GamePlayScoreDetailViewModel(
                        firstScore: GamePlayScoreViewModel(title: "NYU", value: "10"),
                        secondScore: GamePlayScoreViewModel(title: "PIT", value: "10")
                    )
                )
            ),
            GamePlayViewModel(
                id: "7",
                image: .team([]),
                titlePrefix: "TD",
                titleSuffix: "2:57",
                info: "S. Diggs 3 Yd pass from J. Allen (T. Bass Kick)",
                occurredAtString: "1667954357000",
                trailingInfo: .scores(
                    GamePlayScoreDetailViewModel(
                        firstScore: GamePlayScoreViewModel(title: "NYU", value: "0"),
                        secondScore: GamePlayScoreViewModel(title: "PIT", value: "7")
                    )
                )
            ),
            GamePlayViewModel(
                id: "8",
                image: .player(images: [], colorHex: nil),
                titlePrefix: "S. Ago",
                titleSuffix: "NYI",
                info: "Sebastian Ago wrist shot",
                occurredAtString: "1667954357000",
                trailingInfo: .result(.goal)
            ),
            GamePlayViewModel(
                id: "9",
                image: .player(images: [], colorHex: "#123dec"),
                titlePrefix: "S. Ago",
                titleSuffix: "NYR",
                info: "Sebastian Ago wrist shot Sebastian Ago wrist shot Sebastian Ago wrist shot",
                occurredAtString: "1667954357000",
                trailingInfo: .result(.save)
            ),
        ]
    }
}

extension GamePlayViewModel.ScoringResult {
    fileprivate var title: String {
        switch self {
        case .goal:
            return Strings.shotGoalTitle.localized.uppercased()
        case .save:
            return Strings.shotSaveTitle.localized.uppercased()
        }
    }

    fileprivate var color: Color {
        switch self {
        case .goal:
            return .chalk.green
        case .save:
            return .chalk.dark800
        }
    }
}

extension GamePlayViewModel {
    fileprivate var contentAlignment: VerticalAlignment {
        switch image {
        case .player:
            return .center
        default:
            return .top
        }
    }
}
