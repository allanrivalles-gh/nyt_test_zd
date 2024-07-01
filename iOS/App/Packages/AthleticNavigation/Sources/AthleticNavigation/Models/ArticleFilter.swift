//
//  ArticleFilter.swift
//
//
//  Created by Mark Corbyn on 5/6/2023.
//

import Foundation

public struct ArticleFilter: Codable, Hashable {
    public enum FilterType: String, Codable, Hashable {
        case team = "team"
        case league = "league"
        case author = "author"
        case user = "user"
        case custom = "custom"
        case leaguemostpopular = "leaguemostpopular"
        case citymostpopular = "citymostpopular"
        case category = "category"
        case community = "community"
        case liveDiscussion = "livediscussion"
        case leaguesWithTeams = "league_with_teams"
        case topic = "topic"
    }

    public let itemId: String
    public let name: String?
    public let type: FilterType
    public let selectedHubTab: HubTabType?

    public init(
        itemId: String,
        name: String? = nil,
        type: FilterType,
        selectedHubTab: HubTabType? = nil
    ) {
        self.itemId = itemId
        self.name = name
        self.type = type
        self.selectedHubTab = selectedHubTab
    }
}
