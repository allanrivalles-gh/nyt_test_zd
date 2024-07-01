//
//  NetworkModel+Comments.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 10/6/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import AthleticApolloNetworking
import AthleticApolloTypes
import AthleticFoundation
import AthleticRestNetwork
import Combine
import Foundation

public protocol CommentsNetworking {

    /// Queries

    func fetchComments(
        itemId: String,
        contentType: GQL.ContentType,
        teamId: String?,
        limit: Int?,
        sortBy: GQL.CommentSortBy,
        useServerCachedQuery: Bool
    ) async throws -> GQL.CommentsResponse

    func fetchDiscussionDetails(id: String, usingCache: Bool) async throws -> GQL.ArticleContentLite

    func fetchQandADateInfo(id: String, usingCache: Bool) async throws -> (
        startDate: Date, endDate: Date
    )

    func fetchTeamThreads(gameId id: String) async throws -> GQL.TeamSpecificResponse

    /// Operations

    func likeComment(id: String) -> NetworkPublisher<Void>

    func unlikeComment(id: String) -> NetworkPublisher<Void>

    func addComment(
        text: String,
        contentId: String,
        parentId: String?,
        teamId: String?,
        contentType: GQL.ContentType,
        queue: DispatchQueue
    ) async throws -> GQL.Comment

    func addPlayComment(
        text: String,
        contentId: String,
        contentType: GQL.ContentType,
        teamId: String?,
        occurredAtString: String,
        queue: DispatchQueue
    ) async throws -> GQL.PlayCommentResponse

    @discardableResult
    func editComment(
        text: String,
        commentId: String
    ) async throws -> GQL.EditCommentMutation.Data

    @discardableResult
    func deleteComment(commentId: String) async throws -> GQL.DeleteCommentMutation.Data

    @discardableResult
    func flagComment(
        commentId: String,
        reason: GQL.FlagReason
    ) async throws -> GQL.FlagCommentMutation.Data

    @discardableResult
    func updateCurrentSpecificThread(
        gameId: String,
        teamId: String
    ) async throws -> GQL.UpdateCurrentSpecificThreadMutation.Data

}

extension NetworkModel: CommentsNetworking {

    // MARK: - Queries

    public func fetchTeamThreads(gameId: String) async throws -> GQL.TeamSpecificResponse {
        let query = GQL.TeamSpecificThreadsQuery(gameId: gameId)

        guard
            let teamSpecificThreads = try await graphFetch(
                query: query,
                cachePolicy: .fetchIgnoringCacheData
            ).teamSpecificThreads
        else {
            throw AthError.failedQueryError
        }

        return teamSpecificThreads.fragments.teamSpecificResponse
    }

    public func fetchComments(
        itemId: String,
        contentType: GQL.ContentType,
        teamId: String?,
        limit: Int?,
        sortBy: GQL.CommentSortBy,
        useServerCachedQuery: Bool = true
    ) async throws -> GQL.CommentsResponse {
        let input = GQL.QueryCommentsInput(
            contentId: itemId,
            contentType: contentType,
            sortBy: sortBy,
            limit: limit,
            teamId: teamId
        )

        if useServerCachedQuery {
            return try await graphFetch(
                query: GQL.GetCommentsCache1Query(input: input, teamId: teamId),
                cachePolicy: .fetchIgnoringCacheData
            ).getComments.fragments.commentsResponse
        }
        return try await graphFetch(
            query: GQL.GetCommentsQuery(input: input, teamId: teamId),
            cachePolicy: .fetchIgnoringCacheData
        ).getComments.fragments.commentsResponse
    }

    public func fetchDiscussionDetails(id: String, usingCache: Bool) async throws
        -> GQL.ArticleContentLite
    {
        let cachePolicy: CachePolicy =
            usingCache ? .returnCacheDataDontFetch : .fetchIgnoringCacheData

        let response = try await graphFetch(
            query: GQL.DiscussionDetailsQuery(id: id),
            cachePolicy: cachePolicy
        )

        guard let details = response.articleById?.fragments.articleContentLite else {
            throw AthError.failedQueryError
        }

        return details
    }

    public func fetchQandADateInfo(id: String, usingCache: Bool) async throws -> (
        startDate: Date, endDate: Date
    ) {
        let cachePolicy: CachePolicy =
            usingCache ? .returnCacheDataDontFetch : .fetchIgnoringCacheData

        let response = try await graphFetch(
            query: GQL.QandADateInfoQuery(id: id),
            cachePolicy: cachePolicy
        )

        guard let startDate = response.qandaById?.fragments.qandADateInfo.startedAt,
            let endDate = response.qandaById?.fragments.qandADateInfo.endedAt
        else {
            throw AthError.failedQueryError
        }

        return (startDate: startDate, endDate: endDate)
    }

    // MARK: - Operations

    public func likeComment(id: String) -> NetworkPublisher<Void> {
        Future { [weak self] promise in
            _ = self?.apolloClient.perform(
                mutation: GQL.LikeCommentMutation(commentId: id),
                publishResultToStore: true,
                queue: .main
            ) { result in
                switch result {
                case .success:
                    promise(.success(()))
                case .failure(let error):
                    promise(.failure(error))
                }
            }
        }
        .convertToResult()
    }

    public func unlikeComment(id: String) -> NetworkPublisher<Void> {
        Future { [weak self] promise in
            _ = self?.apolloClient.perform(
                mutation: GQL.UnlikeCommentMutation(commentId: id),
                publishResultToStore: true,
                queue: .main
            ) { result in
                switch result {
                case .success:
                    promise(.success(()))
                case .failure(let error):
                    promise(.failure(error))
                }
            }
        }
        .convertToResult()
    }

    public func addComment(
        text: String,
        contentId: String,
        parentId: String? = nil,
        teamId: String? = nil,
        contentType: GQL.ContentType,
        queue: DispatchQueue
    ) async throws -> GQL.Comment {
        let input = GQL.CommentInput(
            comment: text,
            contentId: contentId,
            contentType: contentType,
            parentId: parentId,
            platform: String.platform,
            teamId: teamId
        )
        let mutation = GQL.AddNewCommentMutation(commentInput: input, teamId: teamId)
        return try await graphPerform(mutation: mutation, queue: queue).addNewComment.fragments
            .comment
    }

    public func addPlayComment(
        text: String,
        contentId: String,
        contentType: GQL.ContentType,
        teamId: String?,
        occurredAtString: String,
        queue: DispatchQueue
    ) async throws -> GQL.PlayCommentResponse {
        let input = GQL.PlayCommentInput(
            comment: text,
            contentId: contentId,
            contentType: contentType,
            occurredAtStr: occurredAtString,
            platform: String.platform,
            teamId: teamId
        )
        let mutation = GQL.AddPlayCommentMutation(input: input, teamId: teamId)
        return try await graphPerform(mutation: mutation, queue: queue).addPlayComment.fragments
            .playCommentResponse
    }

    @discardableResult
    public func editComment(text: String, commentId: String) async throws
        -> GQL.EditCommentMutation.Data
    {
        let mutation = GQL.EditCommentMutation(commentId: commentId, comment: text)
        return try await graphPerform(mutation: mutation, queue: .main)
    }

    @discardableResult
    public func deleteComment(commentId: String) async throws -> GQL.DeleteCommentMutation.Data {
        let mutation = GQL.DeleteCommentMutation(commentId: commentId)
        return try await graphPerform(mutation: mutation, queue: .main)
    }

    @discardableResult
    public func flagComment(
        commentId: String,
        reason: GQL.FlagReason
    ) async throws -> GQL.FlagCommentMutation.Data {
        let mutation = GQL.FlagCommentMutation(commentId: commentId, reason: reason)
        return try await graphPerform(mutation: mutation, queue: .main)
    }

    @discardableResult
    public func updateCurrentSpecificThread(
        gameId: String,
        teamId: String
    ) async throws -> GQL.UpdateCurrentSpecificThreadMutation.Data {
        let mutation = GQL.UpdateCurrentSpecificThreadMutation(gameId: gameId, teamId: teamId)
        return try await graphPerform(mutation: mutation, queue: .main)
    }
}
