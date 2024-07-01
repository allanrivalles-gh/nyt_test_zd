//
//  GameScreenMenuItem.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 7/6/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticComments
import Foundation

/// An item representing a game screen tab.
///
/// Note that the `playByPlay` and `timeline` tabs are the same play by play screen and are mutually exclusive. Timeline is used
/// for soccer and differences include the title and analytics events. It's cleaner to model the difference here and have branching logic
/// rather than peppering the code with sport type ternaries.
enum GameScreenMenuItem {
    case game(BoxScoreGameViewModel)
    case liveBlog(permalink: String, permalinkForEmbed: String, isBadged: Bool)
    case playerGrades(GamePlayerGradesViewModel)
    case stats(GameStatsViewModel)
    case playByPlay(PlayByPlayViewModel)
    case timeline(PlayByPlayViewModel)
    case comments(model: CommentListViewModel, isBadged: Bool)

    var title: String {
        switch self {
        case let .game(viewModel):
            return viewModel.leagueCode.sportType == .soccer
                ? Strings.gameMatchTabTitle.localized
                : Strings.gameBoxScoreTabTitle.localized

        case .liveBlog:
            return Strings.gameLiveBlogTabTitle.localized

        case .playerGrades:
            return Strings.gameGradesTabTitle.localized

        case .stats:
            return Strings.gameStatsTabTitle.localized

        case .playByPlay:
            return Strings.gamePlayByPlayTabTitle.localized

        case .timeline:
            return Strings.gameTimelineTabTitle.localized

        case .comments:
            return Strings.gameDiscussTabTitle.localized
        }
    }

    var isComment: Bool {
        if case .comments = self {
            return true
        }
        return false
    }
}

extension GameScreenMenuItem: Equatable {
    static func == (lhs: GameScreenMenuItem, rhs: GameScreenMenuItem) -> Bool {
        switch (lhs, rhs) {
        case (.game, .game),
            (.liveBlog, .liveBlog),
            (.playerGrades, .playerGrades),
            (.stats, .stats),
            (.playByPlay, .playByPlay),
            (.timeline, .timeline),
            (.comments, .comments):
            return true

        default:
            return false
        }
    }
}
