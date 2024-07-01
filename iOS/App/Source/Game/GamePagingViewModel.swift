//
//  GamePagingViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 22/3/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticScoresFoundation
import AthleticUI
import Foundation
import SwiftUI

final class GamePagingViewModel: ObservableObject {
    final class Tab: PagingTab, Hashable {
        let id: String
        let title: String
        let item: GameScreenMenuItem

        var badge: some View {
            RedCircleBadge()
        }

        var shouldShowBadge: Bool {
            get {
                switch item {
                case .liveBlog(_, _, let isBadged):
                    return isBadged
                case .comments(_, let isBadged):
                    return isBadged
                default:
                    return false
                }
            }
            set {}
        }

        init(id: String, item: GameScreenMenuItem) {
            self.id = id
            self.title = item.title
            self.item = item
        }

        static func == (lhs: Tab, rhs: Tab) -> Bool {
            lhs.id == rhs.id
                && lhs.title == rhs.title
        }

        func hash(into hasher: inout Hasher) {
            hasher.combine(id)
            hasher.combine(title)
        }
    }

    struct Game {
        let id: String
        let phase: GamePhase
        let leagueCode: GQL.LeagueCode
    }

    @Published private(set) var tabs: [Tab]
    @Published var selectedTab: Tab
    let game: Game

    init?(
        game: Game,
        items: [GameScreenMenuItem],
        initialItemSelection: GameScreenMenuItem? = nil
    ) {
        self.game = game

        let tabs: [Tab] = Self.makeTabs(from: items)

        guard let firstTab = tabs.first else { return nil }

        self.tabs = tabs

        if let initialSelection = tabs.first(where: { $0.item == initialItemSelection }) {
            self.selectedTab = initialSelection
        } else {
            self.selectedTab = firstTab
        }
    }

    func update(with items: [GameScreenMenuItem], selection: GameScreenMenuItem? = nil) {
        let tabs = Self.makeTabs(from: items)

        guard !tabs.isEmpty else { return }

        onMain {
            self.tabs = tabs
            self.selectedTab =
                tabs.first(where: { $0.item == selection })
                ?? tabs.first(where: { $0 == self.selectedTab })
                ?? tabs[0]
        }
    }

    private static func makeTabs(from items: [GameScreenMenuItem]) -> [Tab] {
        items.map { item in
            let id: String

            switch item {
            case .game:
                id = "game"
            case .liveBlog:
                id = "live-blog"
            case .playerGrades:
                id = "player-grades"
            case .stats:
                id = "stats"
            case .playByPlay:
                id = "plays"
            case .timeline:
                id = "timeline"
            case .comments:
                id = "comments"
            }

            return Tab(id: id, item: item)
        }
    }
}
