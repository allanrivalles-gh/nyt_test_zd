//
//  HubTab.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 28/7/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticNavigation
import AthleticUI
import Foundation

final class HubTab: PagingTab, Hashable {

    let tabType: HubTabType
    let title: String

    init(tabType: HubTabType, sport: SportType?) {
        self.tabType = tabType
        self.title = Self.title(tabType: tabType, sport: sport)
    }

    // MARK: - PagingTab

    private static func title(tabType: HubTabType, sport: SportType?) -> String {
        switch tabType {
        case .feed:
            return Strings.hubFeedTabTitle.localized
        case .threads:
            return Strings.hubThreadsTabTitle.localized
        case .schedule:
            return Strings.hubScheduleTabTitle.localized
        case .standings where sport == .soccer:
            return Strings.hubStandingsTabSoccerTitle.localized
        case .standings:
            return Strings.hubStandingsTabDefaultTitle.localized
        case .roster where sport == .soccer:
            return Strings.hubSquadTabTitle.localized
        case .roster:
            return Strings.hubRosterTabTitle.localized
        case .stats:
            return Strings.hubStatsTabTitle.localized
        case .bracket:
            return Locale.current.isNorthAmerica
                ? Strings.hubBracketTabTitleNorthAmerica.localized
                : Strings.hubBracketTabTitleWorld.localized
        case .podcasts:
            return Strings.podcasts.localized
        }
    }

    // MARK: - Hashable

    static func == (lhs: HubTab, rhs: HubTab) -> Bool {
        lhs.tabType == rhs.tabType
    }

    func hash(into hasher: inout Hasher) {
        hasher.combine(tabType)
    }
}
