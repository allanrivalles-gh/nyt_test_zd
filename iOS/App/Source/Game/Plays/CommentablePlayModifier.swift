//
//  CommentablePlayModifier.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 02/12/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticComments
import AthleticFoundation
import AthleticScoresFoundation
import SwiftUI

extension View {
    func commentablePlay(_ play: CommentingPlay) -> some View {
        modifier(CommentablePlayModifier(play: play))
    }
}

private struct CommentablePlayModifier: ViewModifier {
    static let logger = ATHLogger(category: .comments)

    let play: CommentingPlay

    @Environment(\.boxScoreDiscussPlayContext) private var discussPlayContext
    @Environment(\.boxScoreDiscussPlayOriginTab) private var originTab

    func body(content: Content) -> some View {
        if let discussPlayContext {
            content
                .contextMenu {
                    Button {
                        trackDiscussButtonClick(context: discussPlayContext.analytics)

                        discussPlayContext.discuss(play)
                    } label: {
                        Label(Strings.discussPlayOption.localized, image: "icn_comments")
                    }
                }
        } else {
            content
        }
    }

    private func trackDiscussButtonClick(context: BoxScoreDiscussiblePlayContextAnalytics) {
        guard let originTab else {
            assert(
                false,
                "The modifier can't resolve analytics information in the current context."
            )
            let errorDetails = [
                originTab.map { "tab: \($0.title)" },
                "gameId: \(context.gameId)",
            ].compactMap { $0 }.joined(separator: ", ")
            Self.logger.warning(
                "Unable to resolve analytics information for commentablePlay modifier with \(errorDetails)"
            )
            return
        }

        guard let view = originTab.analyticsView(for: context.gamePhase) else {
            assert(false, "Analytics view not implemented for current context.")
            let errorDetails = [
                "tab: \(originTab.title)",
                "gamePhase: \(context.gamePhase)",
            ].joined(separator: ", ")
            Self.logger.warning(
                "Analytics view not implemented on commentablePlay modifier with \(errorDetails)"
            )
            return
        }

        Analytics.track(
            event: .init(
                verb: .click,
                view: view,
                element: .discuss,
                objectType: .playId,
                objectIdentifier: play.analyticsId(gameId: context.gameId),
                metaBlob: .init(
                    leagueId: context.leagueId,
                    gameId: context.gameId
                )
            )
        )
    }
}

extension CommentingPlay {
    fileprivate func analyticsId(gameId: String) -> String {
        "\(gameId)-\(occurredAtString)"
    }
}

extension GamePagingViewModel.Tab {
    fileprivate func analyticsView(for gamePhase: GamePhase) -> AnalyticsEvent.View? {
        switch item {
        case .game:
            switch gamePhase {
            case .inGame: return .boxScoreInGame
            case .postGame: return .boxScorePostGame
            default: break
            }
        case .playByPlay:
            switch gamePhase {
            case .inGame: return .boxScorePlaysInGame
            case .postGame: return .boxScorePlaysPostGame
            default: break
            }
        case .timeline:
            switch gamePhase {
            case .inGame: return .boxScoreTimelineInGame
            case .postGame: return .boxScoreTimelinePostGame
            default: break
            }
        default: break
        }
        return nil
    }
}
