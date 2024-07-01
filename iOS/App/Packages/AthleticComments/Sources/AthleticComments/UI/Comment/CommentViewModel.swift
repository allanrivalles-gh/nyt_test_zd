//
//  CommentViewModel.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/16/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import SwiftUI

final public class CommentViewModel: ObservableObject, Identifiable, Equatable {
    public typealias AdditionalLikeTapAction = (String, Bool) -> Void
    public typealias ErrorLikeTapAction = (String) -> Void

    public let commentId: String
    public let id: String
    public let teamId: String?
    public let legacyTeamId: Int?
    public let author: String
    public let authorId: String
    public let authorInitial: String
    public let authorAvatarColor: String
    public let authorAvatarImageUrl: URL?
    public let authorGameFlairs: [CommentFlair]
    public let date: Date
    public var comment: String
    public let commentMetadata: String?
    public let permalink: URL?
    public let isReply: Bool
    public let isOwner: Bool
    public let isStaff: Bool
    public let isDeletable: Bool
    public let tweetUrl: URL?
    public let isPinned: Bool
    public var parentCommentViewModel: CommentViewModel? = nil
    public let additionalLikeTapAction: AdditionalLikeTapAction?
    public let errorLikeTapAction: ErrorLikeTapAction?
    public let analyticsDefaults: AnalyticsRequiredValues
    public let network: CommentsNetworking

    @Published var likesCount: Int = 0
    @Published var isLiked: Bool = false
    @Published var isFlagged: Bool = false
    @Published var isLoading: Bool = false

    private var cancellables = Cancellables()
    private let timeSettings: TimeSettings

    public var dateDisplayString: String {
        "• \(date.commentTimeString(timeSettings: timeSettings))"
    }

    public var isFromTheAthletic: Bool {
        author == "The Athletic" && isStaff
    }

    public init(
        comment: GQL.Comment,
        id: String,
        isOwner: Bool,
        isLiked: Bool,
        additionalLikeTapAction: AdditionalLikeTapAction?,
        errorLikeTapAction: ErrorLikeTapAction?,
        isFlagged: Bool,
        analyticsDefaults: AnalyticsRequiredValues,
        network: CommentsNetworking,
        timeSettings: TimeSettings = SystemTimeSettings(),
        teamId: String?,
        legacyTeamId: Int?
    ) {
        self.commentId = comment.id
        self.id = id
        self.author = comment.authorName
        self.authorId = comment.authorId
        self.authorInitial = String(comment.authorInitials.prefix(1))
        self.authorAvatarColor = comment.authorColor
        self.authorAvatarImageUrl = URL(string: comment.avatarUrl)
        self.date = comment.commentedAt
        self.comment = comment.comment
        self.commentMetadata = comment.commentMetadata
        self.permalink = URL(string: comment.commentPermalink)
        self.isReply = false
        self.isOwner = isOwner
        self.isStaff = comment.authorUserLevel > 0
        self.likesCount = comment.likesCount
        self.isLiked = isLiked
        self.isFlagged = isFlagged
        self.isDeletable = comment.isDeletable
        self.tweetUrl = URL(string: comment.tweet?.tweetUrl)
        self.isPinned = comment.isPinned
        self.analyticsDefaults = analyticsDefaults
        self.network = network
        self.timeSettings = timeSettings
        self.authorGameFlairs = comment.authorGameFlairs.compactMap {
            ($0?.fragments.flair).map(CommentFlair.init)
        }
        self.additionalLikeTapAction = additionalLikeTapAction
        self.errorLikeTapAction = errorLikeTapAction
        self.teamId = teamId
        self.legacyTeamId = legacyTeamId
    }

    public init(
        reply: GQL.Comment.Reply,
        parentComment: GQL.Comment,
        id: String,
        isOwner: Bool,
        isParentOwner: Bool,
        isLiked: Bool,
        additionalLikeTapAction: AdditionalLikeTapAction?,
        errorLikeTapAction: ErrorLikeTapAction?,
        isFlagged: Bool,
        analyticsDefaults: AnalyticsRequiredValues,
        network: CommentsNetworking,
        timeSettings: TimeSettings = SystemTimeSettings(),
        teamId: String?,
        legacyTeamId: Int?
    ) {
        self.commentId = reply.id
        self.id = id
        self.author = reply.authorName
        self.authorId = reply.authorId
        self.authorInitial = String(reply.authorInitials.prefix(1))
        self.authorAvatarColor = reply.authorColor
        self.authorAvatarImageUrl = URL(string: reply.avatarUrl)
        self.date = reply.commentedAt
        self.comment = reply.comment
        self.commentMetadata = reply.commentMetadata
        self.permalink = URL(string: reply.commentPermalink)
        self.isReply = true
        self.isOwner = isOwner
        self.isStaff = reply.authorUserLevel > 0
        self.likesCount = reply.likesCount
        self.isLiked = isLiked
        self.parentCommentViewModel = CommentViewModel(
            comment: parentComment,
            id: id,
            isOwner: isParentOwner,
            isLiked: isLiked,
            additionalLikeTapAction: additionalLikeTapAction,
            errorLikeTapAction: errorLikeTapAction,
            isFlagged: isFlagged,
            analyticsDefaults: analyticsDefaults,
            network: network,
            teamId: teamId,
            legacyTeamId: legacyTeamId
        )
        /// replies can't be created via Twitter
        self.tweetUrl = nil
        self.isDeletable = reply.isDeletable
        self.isPinned = reply.isPinned
        self.analyticsDefaults = analyticsDefaults
        self.network = network
        self.timeSettings = timeSettings
        self.authorGameFlairs = reply.authorGameFlairs.compactMap {
            ($0?.fragments.flair).map(CommentFlair.init)
        }
        self.additionalLikeTapAction = additionalLikeTapAction
        self.errorLikeTapAction = errorLikeTapAction
        self.teamId = teamId
        self.legacyTeamId = legacyTeamId
    }

    public init(
        topComment: GQL.CommentWithoutReplies,
        id: String,
        isOwner: Bool,
        isLiked: Bool,
        additionalLikeTapAction: AdditionalLikeTapAction?,
        errorLikeTapAction: ErrorLikeTapAction?,
        isFlagged: Bool,
        analyticsDefaults: AnalyticsRequiredValues,
        network: CommentsNetworking,
        timeSettings: TimeSettings = SystemTimeSettings(),
        teamId: String?,
        legacyTeamId: Int?
    ) {
        self.commentId = topComment.id
        self.id = id
        self.author = topComment.authorName
        self.authorId = topComment.authorId
        self.authorInitial = String(topComment.authorInitials.prefix(1))
        self.authorAvatarColor = topComment.authorColor
        self.authorAvatarImageUrl = URL(string: topComment.avatarUrl)
        self.date = topComment.commentedAt
        self.comment = topComment.comment
        self.commentMetadata = topComment.commentMetadata
        self.permalink = URL(string: topComment.commentPermalink)
        self.isReply = false
        self.isOwner = isOwner
        self.isStaff = topComment.authorUserLevel > 0
        self.likesCount = topComment.likesCount
        self.isLiked = isLiked
        self.isFlagged = isFlagged
        self.isDeletable = topComment.isDeletable
        self.tweetUrl = URL(string: topComment.tweet?.tweetUrl)
        self.isPinned = topComment.isPinned
        self.analyticsDefaults = analyticsDefaults
        self.network = network
        self.timeSettings = timeSettings
        self.authorGameFlairs = topComment.authorGameFlairs.compactMap {
            ($0?.fragments.flair).map(CommentFlair.init)
        }
        self.additionalLikeTapAction = additionalLikeTapAction
        self.errorLikeTapAction = errorLikeTapAction
        self.teamId = teamId
        self.legacyTeamId = legacyTeamId
    }

    public func handleLikeButtonAction(
        index: Int,
        surface: AnalyticsCommentSpecification.Surface
    ) {
        isLoading = true
        toggleLikeCount()
        isLiked.toggle()
        self.additionalLikeTapAction?(self.commentId, self.isLiked)
        if isLiked {
            AnalyticsCommentSpecification.onLikeComment(
                commentId: commentId,
                vIndex: index,
                surface: surface,
                teamId: teamId,
                requiredValues: analyticsDefaults
            )
            network.likeComment(id: commentId)
                .receive(on: RunLoop.main)
                .sink { [weak self] result in
                    guard let self = self else { return }
                    switch result {
                    case .success:
                        self.isLoading = false
                    case .failure(let error):
                        ATHLogger(category: .application).error(
                            "Mutating like state failed with: \(error)"
                        )
                        self.toggleLikeCount()
                        self.isLiked.toggle()
                        self.additionalLikeTapAction?(self.commentId, self.isLiked)
                        self.isLoading = false
                        self.errorLikeTapAction?(Strings.likedCommentError.localized)
                    }
                }
                .store(in: &cancellables)
        } else {
            AnalyticsCommentSpecification.onUnlikeComment(
                commentId: commentId,
                vIndex: index,
                surface: surface,
                teamId: teamId,
                requiredValues: analyticsDefaults
            )
            network.unlikeComment(id: commentId)
                .receive(on: RunLoop.main)
                .sink { [weak self] result in
                    guard let self = self else { return }
                    switch result {
                    case .success:
                        self.isLoading = false
                    case .failure(let error):
                        ATHLogger(category: .application).error(
                            "Mutating like state failed with: \(error)"
                        )
                        self.toggleLikeCount()
                        self.isLiked.toggle()
                        self.additionalLikeTapAction?(self.commentId, self.isLiked)
                        self.isLoading = false
                        self.errorLikeTapAction?(Strings.unlikedCommentError.localized)
                    }
                }
                .store(in: &cancellables)
        }
    }

    public func toggleLikeCount() {
        likesCount += isLiked ? -1 : 1
    }

    public func canDelete(userIsStaff: Bool) -> Bool {
        isOwner || (isDeletable && userIsStaff)
    }

    public static func == (lhs: CommentViewModel, rhs: CommentViewModel) -> Bool {
        lhs.commentId == rhs.commentId
    }
}

public struct CommentFlair: Identifiable {
    public let id: String
    public let name: String
    public let iconContrastColor: Color

    public init(id: String, name: String, iconContrastColor: Color) {
        self.id = id
        self.name = name
        self.iconContrastColor = iconContrastColor
    }

    public init(flair: GQL.Flair) {
        self.init(
            id: flair.id,
            name: flair.name,
            iconContrastColor: Color(hex: flair.iconContrastColor)
        )
    }
}
