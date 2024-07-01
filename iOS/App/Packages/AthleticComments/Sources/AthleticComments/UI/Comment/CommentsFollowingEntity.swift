//
//  CommentsFollowingEntity.swift
//
//
//  Created by Jason Leyrer on 8/30/22.
//

import Foundation

public protocol CommentsFollowingEntity {
    var color: String? { get }
    var imageUrl: URL? { get }
    var associatedLeagueLegacyId: String? { get }
}

public protocol CommentsFollowingEntityProvider {
    func entity(forLegacyId itemId: String, commentsEntityType: CommentsFollowingEntityType)
        -> CommentsFollowingEntity?
}

public enum CommentsFollowingEntityType: String {
    case team
    case league
}
