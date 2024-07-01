//
//  ATHCommunityTopic.swift
//
//
//  Created by Eric Yang on 31/1/20.
//

import Foundation

// MARK: - ATHCommunityTopic
public struct ATHCommunityTopic: Codable {
    public let articleId: Int?
    public let articleTitle: String
    public let articleAuthorTitle: String?
    public let dayLocal: String?
    public let articlePublishDate: Date?
    public let postTypeId: Int?
    public let authorId: Int
    public let authorName: String
    public let authorImg: String
    public let articleHeaderImg: String
    public let gameId: Int
    public let isTeaser, disableComments, lockedComments, showRating: Bool
    public let startTimeGmt, endTimeGmt: Date
    public let commentsCount: Int
    public let versionNumber: Int
    public let permalink: String
    public let articleBody, articleExcerpt, teamHex: String
    public let teamIds: [Int]
    public let leagueIds: [Int]
    public let comments: [Comment]
    public let entryType: String
    public let entityTags: [EntityTag]
    public let featured: Bool

    init(withCodable object: ATHCommunityTopicCodable) {
        self.articleId = object.articleId.value ?? 0
        self.articleTitle = object.articleTitle ?? ""
        self.dayLocal = object.dayLocal ?? ""
        self.articlePublishDate = object.articlePublishDate ?? Date.distantPast
        self.postTypeId = object.postTypeId?.value ?? 0
        self.authorId = object.authorId?.value ?? 0
        self.authorName = object.authorName ?? ""
        self.articleAuthorTitle = object.authorTitle
        self.authorImg = object.authorImg ?? ""
        self.articleHeaderImg = object.articleHeaderImg ?? ""
        self.gameId = object.gameId?.value ?? 0
        self.isTeaser = object.isTeaser ?? false
        self.disableComments = object.disableComments ?? false
        self.lockedComments = object.lockedComments ?? false
        self.showRating = object.showRating ?? false
        self.startTimeGmt = object.startTimeGmt ?? Date.distantPast
        self.endTimeGmt = object.endTimeGmt ?? Date.distantPast
        self.commentsCount = object.commentsCount?.value ?? 0
        self.versionNumber = object.versionNumber ?? 0
        self.permalink = object.permalink ?? ""
        self.articleBody = object.articleBody ?? ""
        self.articleExcerpt = object.articleExcerpt ?? ""
        self.teamHex = object.teamHex ?? ""
        self.teamIds = object.teamIds ?? []
        self.leagueIds = object.leagueIds ?? []
        self.comments = object.comments ?? []
        self.entryType = object.entryType ?? ""
        self.entityTags = object.entityTags ?? []
        self.featured = object.featured ?? false
    }
}

// MARK: ATHCommunityTopicCodable
internal struct ATHCommunityTopicCodable: Codable {
    let articleId: IntCodable
    let articleTitle, dayLocal: String?
    let articlePublishDate: Date?
    let postTypeId, authorId: IntCodable?
    let authorName: String?
    let authorImg: String?
    let authorTitle: String?
    let articleHeaderImg: String?
    let gameId: IntCodable?
    let isTeaser, disableComments, lockedComments, showRating: Bool?
    let startTimeGmt, endTimeGmt: Date?
    let commentsCount: IntCodable?
    let versionNumber: Int?
    let permalink: String?
    let articleBody, articleExcerpt, teamHex: String?
    let teamIds: [Int]?
    let leagueIds: [Int]?
    let comments: [Comment]?
    let entryType: String?
    let entityTags: [EntityTag]?
    let featured: Bool?
}
