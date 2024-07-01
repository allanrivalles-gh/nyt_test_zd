//
//  CommentListViewModel.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/16/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import SwiftUI
import UIKit

final public class CommentListViewModel: ObservableObject {

    public enum ReplyCommentPrefix {
        static func forAuthor(_ author: String) -> String {
            return "@\(author) "
        }
    }

    typealias Networking = CommentsNetworking
    public typealias IsCommentLikedProvider = (Int) -> Bool
    public typealias IsCommentFlaggedProvider = (Int) -> Bool
    public typealias AdditionalFlagAction = (String, Bool) -> Void
    public typealias AdditionalTeamThreadSwitchAction = (String) -> Void

    public let id: String
    public var title: String
    public let toolbarTitle: String
    public let commentsType: GQL.ContentType
    public let analyticsDefaults: AnalyticsRequiredValues

    @Published public var state: LoadingState = .initial
    @Published public var comments: [CommentViewModel]
    @Published public private(set) var flairs: [CommentFlair] = []
    @Published public var focusedComment: CommentViewModel?
    @Published public var selectedReplyComment: CommentViewModel? {
        didSet {
            if selectedReplyComment != nil {
                commentingPlay = nil
            }

            focusedComment = selectedReplyComment
            objectWillChange.send()
        }
    }
    @Published public var editingComment: CommentViewModel? {
        didSet {
            focusedComment = editingComment
        }
    }
    @Published public var commentingPlay: CommentingPlay? {
        didSet {
            if commentingPlay != nil {
                selectedReplyComment = nil
            }
        }
    }

    @Published public var teamThreadList: TeamSpecificThreadListViewModel? {
        didSet {
            guard let teamId = teamThreadList?.currentThread.teamId else { return }
            additionalTeamThreadSwitchAction?(teamId)
        }
    }

    @Published public var dismissedComment: DismissedComment?
    @Published public var selectedCommentID: String?
    @Published public var isShowingFlagAlert: Bool = false
    @Published public var isShowingDeleteConfirmation: Bool = false
    @Published public var commentInteractionError: String?
    @Published public var sortBy: GQL.CommentSortBy {
        didSet {
            commentsType.setCommentsSortOrder(sortBy)
        }
    }
    @Published public var discussionHeaderViewModel: DiscussionHeaderViewModel?
    @Published public private(set) var isInitialLoad = false

    private let logger = ATHLogger(category: .comments)
    private let network: CommentsNetworking
    private let userId: String
    private let followingEntityProvider: CommentsFollowingEntityProvider?
    private var initiallyFocusedCommentId: String?
    private let isCommentLikedProvider: IsCommentLikedProvider?
    private let additionalLikeTapAction: CommentViewModel.AdditionalLikeTapAction?
    private let isCommentFlaggedProvider: IsCommentFlaggedProvider?
    private let additionalFlagAction: AdditionalFlagAction?
    private let areTeamSpecificThreadsEnabled: Bool
    private let additionalTeamThreadSwitchAction: AdditionalTeamThreadSwitchAction?
    private let shouldHideTitleForComments: Bool
    private var cancellables = Cancellables()

    /// Timer properties
    public private(set) var dwellTimer: DwellTimer?
    /// Hold unique id
    public private(set) var analyticsViewId = UUID().uuidString

    public var commentCountText: String {
        switch comments.count {
        case 0:
            return Strings.noComments.localized
        case 1:
            return "\(comments.count) \(Strings.comment.localized)"
        default:
            return "\(comments.count) \(Strings.comments.localized)"
        }
    }

    public var isDiscussion: Bool {
        commentsType == .discussion || commentsType == .qanda
    }

    var hidesTitle: Bool {
        shouldHideTitleForComments || title.isEmpty
    }

    var showsCommentDrawerFlairs: Bool {
        commentsType == .gameV2 && !flairs.isEmpty
    }

    private var gqlComments: [GQL.Comment] = [] {
        didSet {
            comments = Self.models(
                for: gqlComments,
                isCommentLikedProvider: isCommentLikedProvider,
                additionalLikeTapAction: additionalLikeTapAction,
                errorLikeTapAction: handleCommentLikeError,
                isCommentFlaggedProvider: isCommentFlaggedProvider,
                network: network,
                userId: userId,
                analyticsDefaults: analyticsDefaults,
                teamId: teamThreadList?.currentThread.teamId,
                legacyTeamId: teamThreadList?.currentThread.legacyId
            )

            state = .loaded

            if let commentId = initiallyFocusedCommentId,
                let model = comments.first(where: { $0.commentId == commentId })
            {
                focusedComment = model
                initiallyFocusedCommentId = nil
            }
        }
    }

    public init(
        id: String,
        title: String,
        commentsType: GQL.ContentType,
        comments: [CommentViewModel],
        isCommentLikedProvider: IsCommentLikedProvider?,
        additionalLikeTapAction: CommentViewModel.AdditionalLikeTapAction?,
        isCommentFlaggedProvider: IsCommentFlaggedProvider?,
        additionalFlagAction: AdditionalFlagAction?,
        areTeamSpecificThreadsEnabled: Bool = false,
        additionalTeamThreadSwitchAction: AdditionalTeamThreadSwitchAction? = nil,
        shouldHideTitleForComments: Bool = false,
        focusedCommentId: String? = nil,
        network: CommentsNetworking,
        userId: String,
        followingEntityProvider: CommentsFollowingEntityProvider? = nil,
        analyticsDefaults: AnalyticsRequiredValues
    ) {
        self.id = id
        self.title = title
        self.commentsType = commentsType
        self.comments = comments
        self.isCommentLikedProvider = isCommentLikedProvider
        self.additionalLikeTapAction = additionalLikeTapAction
        self.isCommentFlaggedProvider = isCommentFlaggedProvider
        self.additionalFlagAction = additionalFlagAction
        self.areTeamSpecificThreadsEnabled = areTeamSpecificThreadsEnabled
        self.additionalTeamThreadSwitchAction = additionalTeamThreadSwitchAction
        self.shouldHideTitleForComments = shouldHideTitleForComments
        self.initiallyFocusedCommentId = focusedCommentId
        self.network = network
        self.userId = userId
        self.followingEntityProvider = followingEntityProvider
        self.analyticsDefaults = analyticsDefaults
        switch commentsType {
        case .qanda:
            toolbarTitle = Strings.qandaTitle.localized
        case .discussion:
            toolbarTitle = Strings.discussionTitle.localized
        default:
            toolbarTitle = Strings.commentsTitle.localized
        }

        sortBy = commentsType.commentsSortOrder
    }

    @MainActor
    public func didPullToRefresh(surface: AnalyticsCommentSpecification.Surface) async {
        AnalyticsCommentSpecification.onRefreshComments(
            surface: surface,
            teamId: teamThreadList?.currentThread.teamId,
            requiredValues: analyticsDefaults
        )

        state = .loading()

        await fetchCommentData()
    }

    @MainActor
    public func fetchData(isInitialLoad: Bool = false) async {
        if isInitialLoad {
            self.isInitialLoad = true
        }

        state = .loading(showPlaceholders: isInitialLoad)

        await fetchCommentData()

        if self.isInitialLoad {
            self.isInitialLoad = false
        }
    }

    @MainActor
    public func focusCommentAfterNextFetch(id: String) {
        focusedComment = nil
        initiallyFocusedCommentId = id
    }

    @MainActor
    private func fetchCommentData() async {
        do {
            if let discussionDetails = await fetchDiscussionDetailsIfNecessary() {
                if commentsType == .qanda {
                    discussionHeaderViewModel = .init(
                        discussionDetails: discussionDetails,
                        type: .qanda(isLive: await fetchQandAIsLive()),
                        followingEntityProvider: followingEntityProvider
                    )
                } else {
                    discussionHeaderViewModel = .init(
                        discussionDetails: discussionDetails,
                        type: .discussion,
                        followingEntityProvider: followingEntityProvider
                    )
                }
            }

            if commentsType == .gameV2 && areTeamSpecificThreadsEnabled {
                let teamThreadsResponse = try await network.fetchTeamThreads(gameId: id)

                let currentThread = TeamSpecificThreadViewModel(
                    thread: teamThreadsResponse.currentThread.fragments.teamSpecificThread
                )
                let teamThreads = teamThreadsResponse.threads.compactMap {
                    TeamSpecificThreadViewModel(thread: $0.fragments.teamSpecificThread)
                }

                if let currentThread {
                    teamThreadList = TeamSpecificThreadListViewModel(
                        currentThread: currentThread,
                        teamThreads: teamThreads
                    )
                }
            }

            let commentsResponse = try await network.fetchComments(
                itemId: id,
                contentType: commentsType,
                teamId: teamThreadList?.currentThread.teamId,
                limit: nil,
                sortBy: sortBy,
                useServerCachedQuery: false
            )

            /// Everything but .gameV2 can be considered a post and is unique based on ID alone.
            /// We separate them in the cache to prevent possible collisions.
            UserDynamicData.commentCounts.updateCount(
                id: id,
                contentType: commentsType == .gameV2
                    ? commentsType.rawValue : GQL.ContentType.post.rawValue,
                value: commentsResponse.commentCount
            )

            flairs = commentsResponse.userFlairs.map { CommentFlair(flair: $0.fragments.flair) }
            gqlComments = commentsResponse.allComments.map { $0.fragments.comment }
            state = .loaded
        } catch {
            logger.warning(identifyError(error, message: "Failed to fetch comments"))
            state = .failed
        }
    }

    @MainActor
    public func publishComment(text: String) async {
        do {
            if editingComment != nil {
                state = .loading(showPlaceholders: true)
                try await editComment(text: text)
            } else {
                state = .loading(showPlaceholders: true)
                try await addComment(text: text)
            }

            state = .loaded
        } catch {
            logger.warning(identifyError(error, message: "Failed to publish comment"))
            state = .failed
        }
    }

    @MainActor
    public func publishPlayComment(text: String) async {
        do {
            try await addPlayComment(text: text)
            state = .loaded
        } catch {
            logger.warning(identifyError(error, message: "Failed to publish play comment"))
            state = .failed
        }
    }

    @MainActor
    public func flagComment(
        commentId: String,
        reason: CommentFlagReason,
        surface: AnalyticsCommentSpecification.Surface
    ) async {
        guard let index = comments.firstIndex(where: { $0.id == commentId }) else {
            logger.warning(
                "Index for comment with id \(commentId) not found when trying to track flagging event."
            )
            return
        }

        AnalyticsCommentSpecification.onFlagComment(
            commentId: commentId,
            vIndex: index,
            surface: surface,
            teamId: teamThreadList?.currentThread.teamId,
            requiredValues: analyticsDefaults
        )

        state = .loading(showPlaceholders: true)
        do {
            try await network.flagComment(commentId: commentId, reason: reason.flagReason)
            state = .loaded
            comments[index].isFlagged = true
            additionalFlagAction?(comments[index].id, comments[index].isFlagged)
        } catch {
            logger.warning(identifyError(error, message: "Failed to flag comment"))
            state = .failed
            comments[index].isFlagged = false
            additionalFlagAction?(comments[index].id, comments[index].isFlagged)
        }
    }

    @MainActor
    public func deleteComment(commentId: String) async {
        state = .loading(showPlaceholders: true)
        do {
            try await network.deleteComment(commentId: commentId)
            comments.removeAll { $0.id == commentId }

            state = .loaded
        } catch {
            logger.warning(identifyError(error, message: "Failed to delete comment"))
            state = .failed
        }
    }

    public static func models(
        for graphQLComments: [GQL.Comment],
        isCommentLikedProvider: IsCommentLikedProvider?,
        additionalLikeTapAction: CommentViewModel.AdditionalLikeTapAction?,
        errorLikeTapAction: CommentViewModel.ErrorLikeTapAction?,
        isCommentFlaggedProvider: IsCommentFlaggedProvider?,
        network: CommentsNetworking,
        userId: String,
        analyticsDefaults: AnalyticsRequiredValues,
        teamId: String?,
        legacyTeamId: Int?
    ) -> [CommentViewModel] {

        var comments: [CommentViewModel] = []
        for gqlComment in graphQLComments {
            // convert our GraphQL comment to an iOS Comment model
            let commentViewModel = CommentViewModel(
                comment: gqlComment,
                id: gqlComment.id,
                isOwner: userId == gqlComment.authorId,
                isLiked: isCommentLikedProvider?(gqlComment.id.intValue) ?? false,
                additionalLikeTapAction: additionalLikeTapAction,
                errorLikeTapAction: errorLikeTapAction,
                isFlagged: isCommentFlaggedProvider?(gqlComment.id.intValue) ?? false,
                analyticsDefaults: analyticsDefaults,
                network: network,
                teamId: teamId,
                legacyTeamId: legacyTeamId
            )

            comments.append(commentViewModel)
            // see if we have replies
            guard let replies = gqlComment.replies else {
                continue
            }

            // convert our network comment replies to an iOS Comment model
            comments.append(
                contentsOf: replies.compactMap { reply in
                    CommentViewModel(
                        reply: reply,
                        parentComment: gqlComment,
                        id: reply.id,
                        isOwner: userId == reply.authorId,
                        isParentOwner: userId == gqlComment.authorId,
                        isLiked: isCommentLikedProvider?(reply.id.intValue) ?? false,
                        additionalLikeTapAction: additionalLikeTapAction,
                        errorLikeTapAction: errorLikeTapAction,
                        isFlagged: isCommentFlaggedProvider?(gqlComment.id.intValue) ?? false,
                        analyticsDefaults: analyticsDefaults,
                        network: network,
                        teamId: teamId,
                        legacyTeamId: legacyTeamId
                    )
                }
            )
        }
        return comments
    }

    public static func models(
        for graphQLComments: [GQL.CommentWithoutReplies],
        isCommentLikedProvider: IsCommentLikedProvider?,
        additionalLikeTapAction: CommentViewModel.AdditionalLikeTapAction?,
        errorLikeTapAction: CommentViewModel.ErrorLikeTapAction?,
        isCommentFlaggedProvider: IsCommentFlaggedProvider?,
        network: CommentsNetworking,
        userId: String,
        analyticsDefaults: AnalyticsRequiredValues,
        teamId: String?,
        legacyTeamId: Int?
    ) -> [CommentViewModel] {

        var comments: [CommentViewModel] = []
        for gqlComment in graphQLComments {
            // convert our GraphQL comment to an iOS Comment model
            let commentViewModel = CommentViewModel(
                topComment: gqlComment,
                id: gqlComment.id,
                isOwner: userId == gqlComment.authorId,
                isLiked: isCommentLikedProvider?(gqlComment.id.intValue) ?? false,
                additionalLikeTapAction: additionalLikeTapAction,
                errorLikeTapAction: errorLikeTapAction,
                isFlagged: isCommentFlaggedProvider?(gqlComment.id.intValue) ?? false,
                analyticsDefaults: analyticsDefaults,
                network: network,
                teamId: teamId,
                legacyTeamId: legacyTeamId
            )

            comments.append(commentViewModel)
        }
        return comments
    }

    func trackSort(by sortBy: GQL.CommentSortBy, surface: AnalyticsCommentSpecification.Surface) {
        AnalyticsCommentSpecification.onSortComments(
            sortBy: sortBy,
            surface: surface,
            teamId: teamThreadList?.currentThread.teamId,
            requiredValues: analyticsDefaults
        )
    }

    func trackClickToTop(surface: AnalyticsCommentSpecification.Surface) {
        AnalyticsCommentSpecification.onClickToTopOfComments(
            surface: surface,
            teamId: teamThreadList?.currentThread.teamId,
            requiredValues: analyticsDefaults
        )
    }

    func trackTeamThreadSwitch(surface: AnalyticsCommentSpecification.Surface, teamId: String) {
        AnalyticsCommentSpecification.onTeamThreadSwitch(
            surface: surface,
            currentTeamId: teamThreadList?.currentThread.teamId,
            clickedTeamId: teamId,
            requiredValues: analyticsDefaults
        )
    }

    func trackViewEvents(
        manager: AnalyticEventManager = AnalyticsManagers.events,
        surface: AnalyticsCommentSpecification.Surface
    ) {
        /// Set the view id to group events
        analyticsViewId = UUID().uuidString
        /// Track view appear
        trackViewAppeared(surface: surface)
        /// Track seconds
        startTrackingSeconds(manager: manager, surface: surface)
    }

    func trackViewAppeared(surface: AnalyticsCommentSpecification.Surface) {
        AnalyticsCommentSpecification.onViewAllComments(
            surface: surface,
            commentsViewLinkId: analyticsViewId,
            teamId: teamThreadList?.currentThread.teamId,
            requiredValues: analyticsDefaults
        )
    }

    func startTrackingSeconds(
        manager: AnalyticEventManager = AnalyticsManagers.events,
        surface: AnalyticsCommentSpecification.Surface
    ) {

        /// Set the timer
        dwellTimer = DwellTimer(
            action: { [weak self] seconds in
                guard let self else { return }

                Task {
                    await self.handleScreenSeconds(
                        seconds: seconds,
                        manager: manager,
                        surface: surface
                    )
                }
            }
        )

        dwellTimer?.start()
    }

    func stopTrackingSeconds() {
        dwellTimer?.stop()
        dwellTimer = nil
    }

    @MainActor
    public func handleScreenSeconds(
        seconds: Int,
        manager: AnalyticEventManager,
        surface: AnalyticsCommentSpecification.Surface
    ) async {

        /// Make sure event isn't passed the 1 hour mark, 3600 seconds
        guard seconds <= 3600 else {
            /// Invalidate timer
            stopTrackingSeconds()
            return
        }

        /// Event is only suppose to fire at 3 and 5 seconds and then in 20 second intervals
        guard seconds == 3 || seconds == 5 || seconds % 20 == 0 else {
            return
        }

        await AnalyticsCommentSpecification.onSecondsPassed(
            manager: manager,
            surface: surface,
            commentViewLinkId: self.analyticsViewId,
            seconds: seconds,
            requiredValues: analyticsDefaults
        )
    }

    @MainActor
    private func addComment(text: String) async throws {
        let parentId: String?
        if let parentCommentViewModel = selectedReplyComment?.parentCommentViewModel {
            parentId = parentCommentViewModel.commentId
        } else {
            parentId = selectedReplyComment?.commentId
        }

        var commentResponse = try await network.addComment(
            text: text,
            contentId: id,
            parentId: parentId,
            teamId: teamThreadList?.currentThread.teamId,
            contentType: commentsType,
            queue: .main
        )

        /// if the backend fails to return flairs associated with this user/comment, add them in
        commentResponse = addAuthorFlairs(for: commentResponse)

        let commentViewModel = CommentViewModel(
            comment: commentResponse,
            id: id,
            isOwner: userId == commentResponse.authorId,
            isLiked: false,
            additionalLikeTapAction: additionalLikeTapAction,
            errorLikeTapAction: handleCommentLikeError,
            isFlagged: false,
            analyticsDefaults: analyticsDefaults,
            network: network,
            teamId: teamThreadList?.currentThread.teamId,
            legacyTeamId: teamThreadList?.currentThread.legacyId
        )

        guard let selectedReplyComment = selectedReplyComment else {
            if case .recent = sortBy {
                gqlComments.insert(commentResponse, at: 0)
            } else {
                gqlComments.append(commentResponse)
            }
            focusedComment = commentViewModel
            return
        }

        appendChildToParentComment(parentID: selectedReplyComment.id, childComment: commentResponse)

        focusedComment = commentViewModel
        self.selectedReplyComment = nil
    }

    @MainActor
    private func addPlayComment(text: String) async throws {
        guard let commentingPlay = commentingPlay else { return }

        let commentResponse = try await network.addPlayComment(
            text: text,
            contentId: id,
            contentType: commentsType,
            teamId: teamThreadList?.currentThread.teamId,
            occurredAtString: commentingPlay.occurredAtString,
            queue: .main
        )
        self.commentingPlay = nil

        let parentComment = commentResponse.parentComment.fragments.comment
        if commentResponse.isParentNew {
            if case .recent = sortBy {
                gqlComments.insert(parentComment, at: 0)
            } else {
                gqlComments.append(parentComment)
            }
        }

        var childComment = commentResponse.childComment.fragments.comment
        /// if the backend fails to return flairs associated with this user/comment, add them in
        childComment = addAuthorFlairs(for: childComment)

        let childCommentViewModel = CommentViewModel(
            comment: childComment,
            id: id,
            isOwner: userId == childComment.authorId,
            isLiked: false,
            additionalLikeTapAction: additionalLikeTapAction,
            errorLikeTapAction: handleCommentLikeError,
            isFlagged: false,
            analyticsDefaults: analyticsDefaults,
            network: network,
            teamId: teamThreadList?.currentThread.teamId,
            legacyTeamId: teamThreadList?.currentThread.legacyId
        )

        appendChildToParentComment(parentID: parentComment.id, childComment: childComment)
        focusedComment = childCommentViewModel
    }

    @MainActor
    private func addAuthorFlairs(for comment: GQL.Comment) -> GQL.Comment {
        guard !comment.authorGameFlairs.isEmpty && flairs.isEmpty else { return comment }

        var commentWithFlairs = comment
        commentWithFlairs.authorGameFlairs = flairs.map {
            GQL.Comment.AuthorGameFlair(
                id: $0.id,
                name: $0.name,
                iconContrastColor: $0.iconContrastColor.hexString
            )
        }
        return commentWithFlairs
    }

    @MainActor
    private func appendChildToParentComment(parentID: String, childComment: GQL.Comment) {
        /// This finds the parent comment, and then unpacks the reply to that it can insert
        /// the reply automatically into the view state.
        if let indexOfParentComment = gqlComments.firstIndex(where: { comment in
            comment.id == parentID
                || comment.replies?.contains(where: { reply in
                    return reply.id == parentID
                }) == true
        }),
            let reply = try? GQL.Comment.Reply(
                jsonObject: childComment.jsonObject
            )
        {
            gqlComments[indexOfParentComment].replies?.append(reply)
        }
    }

    @MainActor
    func updateThread(teamId: String?, surface: AnalyticsCommentSpecification.Surface) async {
        guard let teamId else { return }

        state = .loading(showPlaceholders: true)

        trackTeamThreadSwitch(surface: surface, teamId: teamId)

        do {
            let response = try await network.updateCurrentSpecificThread(
                gameId: id,
                teamId: teamId
            ).updateCurrentSpecificThread

            if response {
                await fetchCommentData()
                trackViewAppeared(surface: surface)
            } else {
                state = .failed
            }
        } catch {
            logger.warning("Mutating team thread failed with: \(error)")
            state = .failed
        }
    }

    @MainActor
    private func editComment(text: String) async throws {
        guard let editingComment = editingComment else {
            return
        }

        let commentResponse = try await network.editComment(
            text: text,
            commentId: editingComment.commentId
        )
        guard commentResponse.editComment else {
            throw AthError.commentsParsingFailed
        }

        if let index = comments.firstIndex(of: editingComment) {
            comments[index].comment = text
        }

        self.editingComment = nil
    }

    @MainActor
    private func setReplyComment(commentId: String) {
        guard let selectedReplyComment = comments.first(where: { $0.commentId == commentId }) else {
            return
        }
        selectedCommentID = commentId
        self.selectedReplyComment = selectedReplyComment
    }

    @MainActor
    public func dismissComment(with text: String) {
        dismissedComment = nil

        if let selectedReplyComment {
            let replyPrefix = ReplyCommentPrefix.forAuthor(selectedReplyComment.author)
            /// If text is only equal to author name we consider it empty
            if text != replyPrefix {
                dismissedComment = .reply(
                    replyComment: selectedReplyComment,
                    text: text
                )
            }
            self.selectedReplyComment = nil
        } else if let commentingPlay {
            if !text.isEmpty {
                dismissedComment = .play(commentingPlay: commentingPlay, text: text)
            }
            self.commentingPlay = nil
        } else if editingComment != nil {
            editingComment = nil
        } else {
            /// Make sure text is not empty
            if !text.isEmpty {
                dismissedComment = .topLevel(text: text)
            }
        }
    }

    public func dismissCommentAnalytics(
        manager: AnalyticEventManager = AnalyticsManagers.events,
        surface: AnalyticsCommentSpecification.Surface
    ) async {
        await AnalyticsCommentSpecification.onCommentDismissal(
            manager: manager,
            surface: surface,
            requiredValues: analyticsDefaults
        )
    }

    @MainActor
    public func undoDismissComment() -> String? {
        /// Make sure dismissed comment is not nil
        guard let dismissedComment else { return nil }

        switch dismissedComment {
        case .topLevel(let text):
            self.dismissedComment = nil
            return text
        case .reply(let replyComment, let text):
            setReplyComment(commentId: replyComment.commentId)
            self.dismissedComment = nil
            return text
        case .play(let commentingPlay, let text):
            self.commentingPlay = commentingPlay
            self.dismissedComment = nil
            return text
        }
    }

    public func undoDismissCommentAnalytics(
        manager: AnalyticEventManager = AnalyticsManagers.events,
        surface: AnalyticsCommentSpecification.Surface
    ) async {
        await AnalyticsCommentSpecification.onCommentDismissalUndo(
            manager: manager,
            surface: surface,
            requiredValues: analyticsDefaults
        )
    }

    public func handleCommentLikeError(for error: String) {
        withAnimation {
            commentInteractionError = error
        }
    }

    @MainActor
    private func fetchDiscussionDetailsIfNecessary() async -> GQL.ArticleContentLite? {
        guard [.discussion, .qanda].contains(commentsType) else { return nil }

        if let cachedDetails = try? await network.fetchDiscussionDetails(id: id, usingCache: true) {
            return cachedDetails
        } else {
            return try? await network.fetchDiscussionDetails(id: id, usingCache: false)
        }
    }

    @MainActor
    private func fetchQandAIsLive() async -> Bool {
        guard commentsType == .qanda else { return false }

        if let cachedDateInfo = try? await network.fetchQandADateInfo(id: id, usingCache: true) {
            return cachedDateInfo.startDate.isPast && cachedDateInfo.endDate.isFuture
        } else {
            if let dateInfo = try? await network.fetchQandADateInfo(id: id, usingCache: false) {
                return dateInfo.startDate.isPast && dateInfo.endDate.isFuture
            }

            return false
        }
    }

    private func identifyError(_ error: Error, message: String) -> String {
        let details = [
            "itemId: \(id)",
            "contentType: \(commentsType)",
            "error: \(error.localizedDescription)",
        ].joined(separator: ", ")
        return "\(message) with \(details)"
    }
}

extension CommentListViewModel: Equatable {
    public static func == (lhs: CommentListViewModel, rhs: CommentListViewModel) -> Bool {
        lhs.id == rhs.id
    }
}

extension GQL.CustomerDetail {
    var isStaff: Bool {
        return userLevel > 0
    }
}
