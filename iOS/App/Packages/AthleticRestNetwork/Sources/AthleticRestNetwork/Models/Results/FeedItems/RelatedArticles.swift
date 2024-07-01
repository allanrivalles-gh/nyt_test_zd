//
//  RelatedArticles.swift
//
//
//  Created by Eric Yang on 17/2/20.
//

import Foundation

// MARK: RelatedArticlesResponse
/// - tag: RelatedArticlesResponse
public struct RelatedArticlesResponse: Codable {
    public let featuredAuthorDetails: FeaturedAuthorDetail?
    public let relatedStories: [RelatedStory]?
}

// MARK: FeaturedAuthorDetail
/// - tag: FeaturedAuthorDetail
public struct FeaturedAuthorDetail: Codable {
    public let authorName: String?
    public let twitter: String?
    public let bio: String?
    public let featuredImgUrl: String?

    public let id: IntCodable?
    public let displayName: String?
    public let description: String?
    public let featuredPhoto: String?
}

public struct RelatedStory: Codable {
    public let timestampGmt: Date?
    public let title, excerpt: String?
    public let articleId: Int?
    public let imgUrl: String?
    public let entryType: String?
}

// MARK: AuthorDetailResponse
/// - tag: AuthorDetailResponse
public struct AuthorDetailResponse: Codable {
    public let author: FeaturedAuthorDetail?
}
