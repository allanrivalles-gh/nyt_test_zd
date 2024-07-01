//
//  TeamThreadPostDestination.swift
//
//
//  Created by kevin fremgen on 6/20/23.
//

import Foundation
import SwiftUI

public struct TeamThreadPostDestination: Codable, Hashable {
    public let id: String
    public let authorName: String
    public let authorImageUrl: URL
    public let teamColor: Color
    public let createdAt: Date
    public let title: String
    public let content: String
    public let likesCount: Int
    public let commentsCount: Int
    public let interaction: String?

    public init(
        id: String,
        authorName: String,
        authorImageUrl: URL,
        teamColor: Color,
        createdAt: Date,
        title: String,
        content: String,
        likesCount: Int,
        commentsCount: Int,
        interaction: String?
    ) {
        self.id = id
        self.authorName = authorName
        self.authorImageUrl = authorImageUrl
        self.teamColor = teamColor
        self.createdAt = createdAt
        self.title = title
        self.content = content
        self.likesCount = likesCount
        self.commentsCount = commentsCount
        self.interaction = interaction
    }
}
