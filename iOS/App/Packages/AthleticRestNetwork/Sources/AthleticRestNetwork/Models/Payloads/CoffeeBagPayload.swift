//
//  CoffeeBagPayload.swift
//
//
//  Created by Eric Yang on 14/1/20.
//

import Foundation

// MARK: CoffeeGetArticleProtocol
/// - tag: CoffeeGetArticleProtocol
public protocol CoffeeGetArticleProtocol {
    var id: String { get }
    var useCached: Bool { get }
}

// MARK: CoffeeGetArticlePayload
/// - tag: CoffeeGetArticlePayload
public struct CoffeeGetArticlePayload: CoffeeGetArticleProtocol {
    public let id: String
    public let useCached: Bool

    public init(articleId: String, useCached: Bool) {
        self.id = articleId
        self.useCached = useCached
    }
}

// MARK: CoffeeGetArticlePayload
/// - tag: CoffeeGetTopicPayload
public struct CoffeeGetTopicPayload: CoffeeGetArticleProtocol {
    public let id: String
    public let useCached: Bool

    public init(topicId: String, useCached: Bool) {
        self.id = topicId
        self.useCached = useCached
    }
}

// MARK: CoffeeGetRecommendedTeamsPayload
/// - tag: CoffeeGetRecommendedTeamsPayload
public struct CoffeeGetRecommendedTeamsPayload: Encodable {
    public let lat: Double?
    public let lon: Double?

    public init(latitude: Double?, longtitude: Double?) {
        self.lat = latitude
        self.lon = longtitude
    }
}

// MARK: CoffeeGetRelatedArticlesPayload
/// - tag: CoffeeGetRelatedArticlesPayload
public struct CoffeeGetRelatedArticlesPayload: Encodable {
    public let articleId: String

    public init(itemId: String) {
        self.articleId = itemId
    }
}

// MARK: CoffeeGetAuthorDetailPayload
/// - tag: CoffeeGetAuthorDetailPayload
public struct CoffeeGetAuthorDetailPayload: Encodable {
    public let authorId: String

    public init(authorId: String) {
        self.authorId = authorId
    }
}
