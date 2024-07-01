//
//  FollowingEntity.swift
//
//
//  Created by Mark Corbyn on 19/5/2023.
//

import Foundation

public struct FollowingEntity: Codable, Equatable {
    public enum TeamType: Codable {
        case club
        case allStar
        case national
    }

    public let legacyId: String
    public let gqlId: String?
    public let sport: SportType?
    public let name: String
    public let shortName: String?
    public let shortDisplayName: String?
    public let longDisplayName: String?
    public let color: String?
    public let iconColor: String?
    public let type: FollowingEntityType
    public let searchText: String?
    public let imageUrl: URL?
    public let mediumImageUrl: URL?
    public let slug: String?
    public let associatedLeagueLegacyId: String?
    public let associatedLeagueGqlId: String?
    public var isFollowing: Bool
    public let hasGqlScores: Bool
    public let teamType: TeamType?
    public let isAssociatedWithPrimaryLeague: Bool
    public var isNotifyStories: Bool
    public var isNotifyGameResults: Bool
    public var isNotifyGameStart: Bool
    public var hasActiveBracket: Bool
    public var hasPodcastTab: Bool

    public init(
        legacyId: String,
        gqlId: String?,
        sport: SportType?,
        name: String,
        shortName: String?,
        shortDisplayName: String?,
        longDisplayName: String?,
        color: String?,
        iconColor: String?,
        type: FollowingEntityType,
        searchText: String?,
        imageUrl: URL?,
        mediumImageUrl: URL?,
        slug: String?,
        associatedLeagueLegacyId: String?,
        associatedLeagueGqlId: String?,
        isFollowing: Bool,
        hasGqlScores: Bool,
        teamType: TeamType?,
        isAssociatedWithPrimaryLeague: Bool,
        isNotifyStories: Bool,
        isNotifyGameResults: Bool,
        isNotifyGameStart: Bool,
        hasActiveBracket: Bool,
        hasPodcastTab: Bool
    ) {
        self.legacyId = legacyId
        self.gqlId = gqlId
        self.sport = sport
        self.name = name
        self.shortName = shortName
        self.shortDisplayName = shortDisplayName
        self.longDisplayName = longDisplayName
        self.color = color
        self.iconColor = iconColor
        self.type = type
        self.searchText = searchText
        self.imageUrl = imageUrl
        self.mediumImageUrl = mediumImageUrl
        self.slug = slug
        self.associatedLeagueLegacyId = associatedLeagueLegacyId
        self.associatedLeagueGqlId = associatedLeagueGqlId
        self.isFollowing = isFollowing
        self.hasGqlScores = hasGqlScores
        self.teamType = teamType
        self.isAssociatedWithPrimaryLeague = isAssociatedWithPrimaryLeague
        self.isNotifyStories = isNotifyStories
        self.isNotifyGameResults = isNotifyGameResults
        self.isNotifyGameStart = isNotifyGameStart
        self.hasActiveBracket = hasActiveBracket
        self.hasPodcastTab = hasPodcastTab
    }
}
