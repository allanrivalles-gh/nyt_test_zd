//
//  ArticleReadStateRecord.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 5/18/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import Foundation

public struct ArticleReadStateRecord: StorageObject {
    public let articleId: String
    public var isRead: Bool
    public var percentRead: CGFloat?

    public var storageIdentifier: String {
        articleId
    }

    public init(articleId: String, isRead: Bool, percentRead: CGFloat? = nil) {
        self.articleId = articleId
        self.isRead = isRead
        self.percentRead = percentRead
    }
}
