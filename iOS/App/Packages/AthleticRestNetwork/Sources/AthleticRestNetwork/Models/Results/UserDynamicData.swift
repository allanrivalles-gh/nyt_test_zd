//
//  UserDynamicData.swift
//
//
//  Created by Kyle Browning on 1/21/20.
//

import Foundation

/// - Tag: DynamicData
public struct DynamicData: Codable {
    public let articlesRead, articlesRated, articlesSaved, commentsLiked, commentsFlagged: [Int]
}
