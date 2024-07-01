//
//  CommentPreviewHelper.swift
//
//
//  Created by Jason Leyrer on 8/3/22.
//

import AthleticAnalytics
import AthleticApolloNetworking
import AthleticApolloTypes
import Foundation

struct CommentPreviewHelper {
    private static let comment = GQL.Comment(
        authorId: "1234",
        authorName: "A. Commenter",
        authorColor: "#969693",
        authorInitials: "AC",
        authorUserLevel: 0,
        comment: "A pretty short comment.",
        commentedAt: Date().add(hours: -5),
        id: "1",
        isPinned: false,
        likesCount: 21,
        parentId: "0",
        isDeletable: false,
        authorGameFlairs: [
            .init(id: "0", name: "PIT", iconContrastColor: "323232"),
            .init(id: "1", name: "CIN", iconContrastColor: "ff6600"),
        ],
        totalReplies: 0,
        tweet: nil
    )

    private static let tweetComment = GQL.Comment(
        authorId: "1234",
        authorName: "A. Commenter",
        authorColor: "#969693",
        authorInitials: "AC",
        authorUserLevel: 2,
        comment: "A pretty short comment.",
        commentedAt: Date().add(hours: -5),
        id: "1",
        isPinned: false,
        likesCount: 21,
        parentId: "0",
        isDeletable: true,
        authorGameFlairs: [
            .init(id: "0", name: "PIT", iconContrastColor: "323232"),
            .init(id: "1", name: "CIN", iconContrastColor: "ff6600"),
        ],
        totalReplies: 0,
        tweet: .init(id: "123", tweetUrl: "https://www.twitter.com")
    )

    private static let reply = GQL.Comment.Reply(
        authorId: "1234",
        authorName: "A. Commenter",
        authorColor: "#969693",
        authorInitials: "AC",
        authorUserLevel: 2,
        comment:
            "A Pretty long comment, that should extend the amount of text to multiple lines again and again. Steph Curry just nailed his NBA 3 point record. What an amazing time to be alive.",
        commentedAt: Date().add(hours: -1),
        id: "2",
        isPinned: false,
        isDeletable: false,
        likesCount: 0,
        parentId: "1",
        authorGameFlairs: [
            .init(id: "0", name: "PIT", iconContrastColor: "323232")
        ],
        totalReplies: 0
    )

    static let flaggedCommentModel = CommentViewModel(
        comment: CommentPreviewHelper.comment,
        id: "3",
        isOwner: false,
        isLiked: true,
        additionalLikeTapAction: nil,
        errorLikeTapAction: nil,
        isFlagged: true,
        analyticsDefaults: PreviewAnalyticDefaults(),
        network: CommentPreviewHelper.network,
        teamId: nil,
        legacyTeamId: nil
    )

    static let commentModel = CommentViewModel(
        comment: CommentPreviewHelper.comment,
        id: "1",
        isOwner: false,
        isLiked: true,
        additionalLikeTapAction: nil,
        errorLikeTapAction: nil,
        isFlagged: false,
        analyticsDefaults: PreviewAnalyticDefaults(),
        network: CommentPreviewHelper.network,
        teamId: nil,
        legacyTeamId: nil
    )

    static let replyModel = CommentViewModel(
        reply: CommentPreviewHelper.reply,
        parentComment: comment,
        id: "2",
        isOwner: false,
        isParentOwner: false,
        isLiked: true,
        additionalLikeTapAction: nil,
        errorLikeTapAction: nil,
        isFlagged: false,
        analyticsDefaults: PreviewAnalyticDefaults(),
        network: CommentPreviewHelper.network,
        teamId: nil,
        legacyTeamId: nil
    )

    static let network = NetworkModel(
        graphNetwork: .init(environment: .stage),
        restNetwork: .init(clientName: "ios", environment: .stage)
    )
}
