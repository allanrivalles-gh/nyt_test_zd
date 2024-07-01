//
//  UserDefaults+Extensions.swift
//
//
//  Created by Jason Leyrer on 9/7/22.
//

import AthleticApolloTypes
import Foundation
import SwiftUI

extension UserDefaults {

    @AppStorage("contentCommentsSortOrder", store: nil)
    public static var contentCommentsSortOrder: GQL.CommentSortBy = .likes

    @AppStorage("discussionCommentsSortOrder", store: nil)
    public static var discussionCommentsSortOrder: GQL.CommentSortBy = .trending

    @AppStorage("qandaCommentsSortOrder", store: nil)
    public static var qandaCommentsSortOrder: GQL.CommentSortBy = .trending

    @AppStorage("gameThreadsCommentsSortOrder", store: nil)
    public static var gameThreadsCommentsSortOrder: GQL.CommentSortBy = .recent
}

extension GQL.ContentType {
    public var commentsSortOrder: GQL.CommentSortBy {
        switch self {
        case .discussion:
            return UserDefaults.discussionCommentsSortOrder
        case .qanda:
            return UserDefaults.qandaCommentsSortOrder
        case .gameV2:
            return UserDefaults.gameThreadsCommentsSortOrder
        default:
            return UserDefaults.contentCommentsSortOrder
        }
    }

    public func setCommentsSortOrder(_ sortOrder: GQL.CommentSortBy) {
        switch self {
        case .discussion:
            UserDefaults.discussionCommentsSortOrder = sortOrder
        case .qanda:
            UserDefaults.qandaCommentsSortOrder = sortOrder
        case .gameV2:
            UserDefaults.gameThreadsCommentsSortOrder = sortOrder
        default:
            UserDefaults.contentCommentsSortOrder = sortOrder
        }
    }
}
