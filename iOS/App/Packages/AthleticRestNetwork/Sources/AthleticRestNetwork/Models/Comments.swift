//
//  CommentsResponse.swift
//
//
//  Created by Eric Yang on 18/12/19.
//

import Foundation

// MARK: CommentResponse
/// - Tag: CommentsResponse
public struct CommentsResponse: Codable {
    public let totalComments: Int
    public let comments: [Comment]
}

// MARK: Comment
public struct Comment: Codable {
    public let commentId, authorId: IntCodable
    public let comment: String
    public let replies: [Comment]?
    public let articleItemId, authorName, avatarUrl: String?
    public let totalReplies, authorUserLevel, likesCount: Int?
    public let isLiked, isPinned, isAmbassador, isFlagged: Bool?
    public let date: Date?

    private enum CodingKeys: String, CodingKey {
        case commentId,
            authorId,
            comment,
            replies,
            articleItemId,
            authorName
        case
            avatarUrl = "authorProfilePicture"
        case
            totalReplies,
            authorUserLevel,
            likesCount,
            isLiked,
            isPinned,
            isAmbassador,
            isFlagged
        case
            date = "commentDateGmt"
    }
}

// MARK: Latest comment data
/// - Tag: LastestCommentData
public struct LatestCommentData: Codable {
    public var date: Date {
        guard let timestamp = latestTimestamp.value else { return Date.distantPast }
        return Date(timeIntervalSince1970: timestamp)
    }
    internal let latestTimestamp: TimestampCodable<Double>

    public var isAuthorComment: Bool {
        return authorName != nil
    }
    public let commentId: Int

    public let authorId: IntCodable?
    public let authorName: String?
    public let authorImageURL: String?

    internal let result: String?

    private enum CodingKeys: String, CodingKey {
        case latestTimestamp
        case
            commentId = "parentCommentId"
        case
            authorId = "commentUserId"
        case
            authorName
        case
            authorImageURL = "authorProfilePicture"
        case
            result

    }
}

// MARK: Get comments payload
/// - Tag: CommentsGetPayload
public struct CommentsGetPayload: Encodable {
    /**
     Initialization of the get comments payload.
     - Parameter articleId: the unique id of the article.
     - Parameter useCached: the flag indicates using cached or not.
     ### Usage Example: ###
     ````
     let payload = CommentsGetPayload(withArticleId: articleId, useCached: true)
     and then pass it into the service:
     CommentService.getComments(payload: payload, network: network)
    ````
    */
    public init(withArticleId articleId: Int, useCached: Bool) {
        self.articleId = articleId
        self.useCached = useCached
    }

    @StringEncoder
    public var articleId: Int
    public let useCached: Bool

    //Excluding the 'useCached' key when encode
    private enum CodingKeys: String, CodingKey {
        case articleId
    }
}

// MARK: Get podcast comments payload
/// - Tag: PodcastCommentsGetPayload
public struct PodcastCommentsGetPayload: Encodable {
    /**
     Initialization of the get  podcast comments payload.
     - Parameter episodeId: the unique id of the podcast episode.
     - Parameter useCached: the flag indicates using cached or not.
     ### Usage Example: ###
     ````
     let payload = PodcastCommentsGetPayload(withEpisodeId: episodeId, useCached: true)
     and then pass it into the service:
     CommentService.getComments(payload: payload, network: network)
    ````
    */
    public init(withEpisodeId episodeId: Int, useCached: Bool) {
        self.podcastEpisodeId = episodeId
        self.useCached = useCached
    }

    @StringEncoder
    public var podcastEpisodeId: Int
    public let useCached: Bool

    //Excluding the 'useCached' key when encode
    private enum CodingKeys: String, CodingKey {
        case podcastEpisodeId
    }
}

// MARK: Toggle like payload
/// - Tag: CommentsToggleLikePayload
public struct CommentsToggleLikePayload: Encodable {
    /**
     Initialization of the comment toggle like payload.
     - Parameter commentId: the unique id of the comment.
     - Parameter isLiked: the flag indicates like or unlike the comment.
     ### Usage Example: ###
     ````
     let payload = CommentsToggleLikePayload(withId: commentId, isLiked: isLiked)
     and then pass it into the service:
     CommentService.toggleLike(payload: payload, network: network)
    ````
    */
    public init(withId commentId: Int, isLiked: Bool) {
        self.commentId = commentId
        self.isLiked = isLiked
    }

    @StringEncoder
    public var commentId: Int
    public let isLiked: Bool

    //Excluding 'isLiked' key when encode
    private enum CodingKeys: String, CodingKey {
        case commentId
    }
}

// MARK: Post comment payload
public struct CommentsPostPayload: Encodable {
    public let postId: Int
    /// Platform is hardcoded as "iOS".
    public let platform: String = "iOS"
    /// Datetime GMT is current date by defaul.
    public let datetimeGmt: Date = Date()
    public let parentId: Int?
    public let comment: String

    /**
     Initialization of the post comment payload.
     - Parameter itemId: the unique id of the article.
     - Parameter parentId: the unique id of the comment's parent.
     - Parameter comment: the text of the comment.
     ### Usage Example: ###
     ````
     let payload = CommentsPostPayload(withId: itemId, comment: comment)
     and then pass it into the service:
     CommentService.postComment(payload: payload, network: network)
    ````
    */
    public init(
        withId postId: Int,
        parentId: Int? = nil,
        andText comment: String
    ) {
        self.postId = postId
        self.parentId = parentId
        self.comment = comment
    }
}

// MARK: Post  podcast comment payload
public struct PodcastCommentsPostPayload: Encodable {
    public let podcastEpisodeId: Int
    /// Platform is hardcoded as "iOS".
    public let platform: String = "iOS"
    /// Datetime GMT is current date by defaul.
    public let datetimeGmt: Date = Date()
    public let parentId: Int?
    public let comment: String

    /**
     Initialization of the post comment payload.
     - Parameter podcastEpisodeId: the unique id of the podcast episode
     - Parameter parentId: the unique id of the comment's parent.
     - Parameter comment: the text of the comment.
     ### Usage Example: ###
     ````
     let payload = PodcastCommentsPostPayload(withEpisodeId: episodeId, comment: comment)
     and then pass it into the service:
     CommentService.postComment(payload: payload, network: network)
    ````
    */
    public init(
        withEpisodeId episodeId: Int,
        parentId: Int = 0,
        andText comment: String
    ) {
        self.podcastEpisodeId = episodeId
        self.parentId = parentId
        self.comment = comment
    }
}

// MARK: Flag comment payload
/// - Tag: FlagCommentPayload
public struct FlagCommentPayload: Encodable {
    /**
     Initialization of the flag comment payload.
     - Parameter commentId: the unique id of the comment.
     - Parameter reason: the text of the reason.
     ### Usage Example: ###
     ````
     let payload = FlagCommentPayload(withId: commentId, reason: reason)
     and then pass it into the service:
     CommentService.flagComment(payload: payload, network: network)
    ````
    */
    public init(withId commentId: Int, reason: String) {
        self.commentId = commentId
        self.flagReason = reason
    }

    @StringEncoder
    public var commentId: Int
    public let flagReason: String
}

// MARK: Edit Comment payload
/// - Tag: EditCommentPayload
public struct EditCommentPayload: Encodable {
    /**
     Initialization of the edit comment payload.
     - Parameter commentId: the unique id of the comment.
     - Parameter comment: the text of the comment.
     ### Usage Example: ###
     ````
     let payload = EditCommentPayload(withId: commentId, comment: comment)
     and then pass it into the service:
     CommentService.editComment(payload: payload, network: network)
    ````
    */
    public init(withId commentId: Int, comment: String) {
        self.commentId = commentId
        self.comment = comment
    }

    @StringEncoder
    public var commentId: Int
    public let comment: String
}

// MARK: Delete comment payload
/// - Tag: DeleteCommentPayload
public struct DeleteCommentPayload: Encodable {
    /**
     Initialization of the delete comment payload.
     - Parameter commentId: the unique id of the comment.
     ### Usage Example: ###
     ````
     let payload = DeleteCommentPayload(withId: commentId)
     and then pass it into the service:
     CommentService.deleteComment(payload: payload, network: network)
    ````
    */
    public init(withId commentId: Int) {
        self.commentId = commentId
    }

    @StringEncoder
    public var commentId: Int
}

// MARK: Get last comment payload
/// - Tag: GetLastCommentDatePayload
public struct GetLastCommentDatePayload: Encodable {
    /**
     Initialization of the get last comment date payload.
     - Parameter discussionId: the unique id of the discussion.
     ### Usage Example: ###
     ````
     let payload = GetLastCommentDatePayload(withId: discussionId)
     and then pass it into the service:
     CommentService.getLastCommentDate(payload: payload, network: network)
    ````
    */
    public init(withId discussionId: String) {
        self.discussionId = discussionId
    }

    public let discussionId: String
}
