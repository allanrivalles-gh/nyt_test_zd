package com.theathletic.comments.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.theathletic.AddNewCommentMutation
import com.theathletic.CommentsForArticleQuery
import com.theathletic.CommentsForDiscussionQuery
import com.theathletic.CommentsForGameQuery
import com.theathletic.CommentsForHeadlineQuery
import com.theathletic.CommentsForPodcastEpisodeQuery
import com.theathletic.CommentsForQandaQuery
import com.theathletic.CreatedQaCommentSubscription
import com.theathletic.DeleteCommentMutation
import com.theathletic.EditCommentMutation
import com.theathletic.FlagCommentMutation
import com.theathletic.LikeCommentMutation
import com.theathletic.UnlikeCommentMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.FlagReason
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.extension.nullIfEmpty
import com.theathletic.network.apollo.notPersistedSubscription
import com.theathletic.type.CommentInput
import com.theathletic.type.CommentSortBy
import com.theathletic.type.ContentType
import com.theathletic.type.QueryCommentsInput
import com.theathletic.utility.coroutines.retryWithBackoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class CommentsApi @AutoKoin constructor(
    private val client: ApolloClient
) {

    suspend fun getCommentsForHeadline(
        id: String,
        sortBy: String
    ): ApolloResponse<CommentsForHeadlineQuery.Data> {
        val query = CommentsForHeadlineQuery(
            headlineId = id,
            input = QueryCommentsInput(
                content_id = id,
                content_type = ContentType.headline,
                sort_by = Optional.present(CommentSortBy.safeValueOf(sortBy))
            )
        )

        return client.query(query).execute()
    }

    suspend fun getCommentsForArticle(
        id: String,
        sortBy: String
    ): ApolloResponse<CommentsForArticleQuery.Data> {
        val query = CommentsForArticleQuery(
            articleId = id,
            input = QueryCommentsInput(
                content_id = id,
                content_type = ContentType.post,
                sort_by = Optional.present(CommentSortBy.safeValueOf(sortBy))
            )
        )
        return client.query(query).execute()
    }

    suspend fun getCommentsForPodcastEpisode(
        episodeId: String,
        sortBy: String
    ): ApolloResponse<CommentsForPodcastEpisodeQuery.Data> {
        val query = CommentsForPodcastEpisodeQuery(
            podcastEpisodeId = episodeId,
            input = QueryCommentsInput(
                content_id = episodeId,
                content_type = ContentType.podcast_episode,
                sort_by = Optional.present(CommentSortBy.safeValueOf(sortBy))
            )
        )
        return client.query(query).execute()
    }

    suspend fun getCommentsForDiscussion(
        id: String,
        sortBy: String
    ): ApolloResponse<CommentsForDiscussionQuery.Data> {
        val query = CommentsForDiscussionQuery(
            articleId = id,
            input = QueryCommentsInput(
                content_id = id,
                content_type = ContentType.discussion,
                sort_by = Optional.present(CommentSortBy.safeValueOf(sortBy))
            )
        )
        return client.query(query).execute()
    }

    suspend fun getCommentsForQanda(
        id: String,
        sortBy: String
    ): ApolloResponse<CommentsForQandaQuery.Data> {
        val query = CommentsForQandaQuery(
            articleId = id,
            input = QueryCommentsInput(
                content_id = id,
                content_type = ContentType.qanda,
                sort_by = Optional.present(CommentSortBy.safeValueOf(sortBy))
            )
        )
        return client.query(query).execute()
    }

    suspend fun getCommentsForGame(
        id: String,
        teamId: String = "",
        sortBy: String
    ): ApolloResponse<CommentsForGameQuery.Data> {
        val query = CommentsForGameQuery(
            input = QueryCommentsInput(
                content_id = id,
                content_type = ContentType.game_v2,
                sort_by = Optional.present(CommentSortBy.safeValueOf(sortBy)),
                team_id = Optional.present(teamId.nullIfEmpty()),
            ),
            teamId = Optional.present(teamId.nullIfEmpty()),
        )
        return client.query(query).execute()
    }

    fun subscribeCreatedQAComment(id: String): Flow<CreatedQaCommentSubscription.Data> {
        return client.notPersistedSubscription(CreatedQaCommentSubscription(id))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun addComment(
        comment: String,
        contentId: String,
        commentsSourceType: CommentsSourceType,
        parentId: String = "",
        teamId: String = ""
    ): AddNewCommentMutation.AddNewComment {
        val input = CommentInput(
            content_id = contentId,
            comment = comment,
            content_type = Optional.present(commentsSourceType.contentType),
            parent_id = Optional.present(parentId.nullIfEmpty()),
            team_id = Optional.presentIfNotNull(teamId.nullIfEmpty()),
            platform = "android"
        )

        val mutation = AddNewCommentMutation(input, teamId = Optional.presentIfNotNull(teamId))
        val response = client.mutation(mutation).execute()
        val newComment = response.data?.addNewComment
        if (response.hasErrors() || newComment == null) {
            throw Exception("Unable to add comment :$contentId ${response.errors?.joinToString(" - ")}")
        }

        return newComment
    }

    private val CommentsSourceType.contentType: ContentType
        get() = when (this) {
            CommentsSourceType.PODCAST_EPISODE -> ContentType.podcast_episode
            CommentsSourceType.ARTICLE -> ContentType.post
            CommentsSourceType.DISCUSSION -> ContentType.discussion
            CommentsSourceType.QANDA -> ContentType.qanda
            CommentsSourceType.HEADLINE -> ContentType.headline
            CommentsSourceType.GAME -> ContentType.game_v2
            CommentsSourceType.TEAM_SPECIFIC_THREAD -> ContentType.game_v2
        }

    suspend fun deleteCommentAsync(commentId: String): ApolloResponse<DeleteCommentMutation.Data> {
        val mutation = DeleteCommentMutation(commentId = commentId)

        return client.mutation(mutation).execute()
    }

    suspend fun editCommentAsync(
        commentId: String,
        comment: String
    ): ApolloResponse<EditCommentMutation.Data> {
        val mutation = EditCommentMutation(
            commentId = commentId,
            comment = comment
        )

        return client.mutation(mutation).execute()
    }

    suspend fun flagCommentAsync(
        commentId: String,
        reason: FlagReason
    ): ApolloResponse<FlagCommentMutation.Data> {
        val mutation = FlagCommentMutation(
            commentId = commentId,
            reason = mapFlagReason(reason)
        )

        return client.mutation(mutation).execute()
    }

    suspend fun likeCommentAsync(commentId: String): ApolloResponse<LikeCommentMutation.Data> {
        val mutation = LikeCommentMutation(commentId = commentId)

        return client.mutation(mutation).execute()
    }

    suspend fun likeComment(commentId: String): LikeCommentMutation.Data {
        val result = client.mutation(LikeCommentMutation(commentId = commentId)).execute()
        val data = result.data
        if (data == null || data.likeComment.not() || result.hasErrors()) {
            throw Exception(Exception("Unable to like comment ${result.errors?.joinToString(" - ")}"))
        }
        return data
    }

    suspend fun unlikeCommentAsync(commentId: String): ApolloResponse<UnlikeCommentMutation.Data> {
        val mutation = UnlikeCommentMutation(commentId = commentId)

        return client.mutation(mutation).execute()
    }

    suspend fun unlikeComment(commentId: String): UnlikeCommentMutation.Data {
        val result = client.mutation(UnlikeCommentMutation(commentId = commentId)).execute()
        val data = result.data
        if (data == null || data.unlikeComment.not() || result.hasErrors()) {
            throw Exception(Exception("Unable to unlike comment ${result.errors?.joinToString(" - ")}"))
        }
        return data
    }

    private fun mapFlagReason(reason: FlagReason): com.theathletic.type.FlagReason {
        return when (reason) {
            FlagReason.SPAM -> com.theathletic.type.FlagReason.spam
            FlagReason.ABUSIVE_OR_HARMFUL -> com.theathletic.type.FlagReason.abusive_or_harmful
            FlagReason.TROLLING_OR_BAITING -> com.theathletic.type.FlagReason.trolling_or_baiting
            FlagReason.USER -> com.theathletic.type.FlagReason.user
            else -> com.theathletic.type.FlagReason.spam
        }
    }
}