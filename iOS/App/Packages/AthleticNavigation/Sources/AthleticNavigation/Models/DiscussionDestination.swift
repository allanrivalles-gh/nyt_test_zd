//
//  DiscussionDestination.swift
//
//
//  Created by Jason Leyrer on 6/7/23.
//

import Foundation

public struct DiscussionDestination: Codable, Hashable {
    public let id: String
    public let title: String
    public let isLiveDiscussion: Bool
    public let objectIdentifier: String?
    public let focusCommentId: String?

    public init(
        id: String,
        title: String,
        isLiveDiscussion: Bool,
        objectIdentifier: String? = nil,
        focusCommentId: String? = nil
    ) {
        self.id = id
        self.title = title
        self.isLiveDiscussion = isLiveDiscussion
        self.objectIdentifier = objectIdentifier
        self.focusCommentId = focusCommentId
    }
}
