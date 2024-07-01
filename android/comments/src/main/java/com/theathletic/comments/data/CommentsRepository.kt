package com.theathletic.comments.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.FlagReason
import com.theathletic.comments.data.local.CommentsDataStore
import com.theathletic.comments.data.remote.CommentsApi
import com.theathletic.comments.data.remote.QandaCommentSubscriber
import com.theathletic.comments.data.remote.fetcher.ArticleCommentsFetcher
import com.theathletic.comments.data.remote.fetcher.ArticleDeleteCommentFetcher
import com.theathletic.comments.data.remote.fetcher.ArticleFlagCommentFetcher
import com.theathletic.comments.data.remote.fetcher.ArticleLikeCommentFetcher
import com.theathletic.comments.data.remote.fetcher.ArticleUnlikeCommentFetcher
import com.theathletic.comments.data.remote.fetcher.DiscussionCommentsFetcher
import com.theathletic.comments.data.remote.fetcher.GameCommentsFetcher
import com.theathletic.comments.data.remote.fetcher.PodcastEpisodeCommentsFetcher
import com.theathletic.comments.data.remote.fetcher.QandaCommentsFetcher
import com.theathletic.news.repository.mapApolloDeleteCommentResponseSuccess
import com.theathletic.news.repository.mapApolloEditCommentResponseSuccess
import com.theathletic.news.repository.mapApolloFlagCommentResponseSuccess
import com.theathletic.repository.CoroutineRepository
import com.theathletic.repository.safeApiRequest
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CommentsRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val commentsApi: CommentsApi,
    private val commentsDataSource: CommentsDataStore,
    private val articleLikeCommentFetcher: ArticleLikeCommentFetcher,
    private val articleUnlikeCommentFetcher: ArticleUnlikeCommentFetcher,
    private val articleDeleteCommentFetcher: ArticleDeleteCommentFetcher,
    private val articleFlagCommentFetcher: ArticleFlagCommentFetcher,
    private val articleCommentsFetcher: ArticleCommentsFetcher,
    private val podcastEpisodeCommentsFetcher: PodcastEpisodeCommentsFetcher,
    private val discussionCommentsFetcher: DiscussionCommentsFetcher,
    private val gameCommentsFetcher: GameCommentsFetcher,
    private val qandaCommentsFetcher: QandaCommentsFetcher,
    private val qandaCommentSubscriber: QandaCommentSubscriber
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    fun getCommentsFeed(key: String) = commentsDataSource.observeItem(key)

    suspend fun fetchArticleComments(
        articleId: String,
        key: String,
        sortBy: String
    ) = repositoryScope.async {
        articleCommentsFetcher.fetchRemote(
            ArticleCommentsFetcher.Params(
                key = key,
                articleId = articleId,
                sortBy = sortBy
            )
        )
    }.await()

    suspend fun fetchPodcastEpisodeComments(
        episodeId: String,
        key: String,
        sortBy: String
    ) = repositoryScope.async {
        podcastEpisodeCommentsFetcher.fetchRemote(
            PodcastEpisodeCommentsFetcher.Params(
                key = key,
                episodeId = episodeId,
                sortBy = sortBy
            )
        )
    }.await()

    suspend fun fetchDiscussionComments(
        discussionId: String,
        key: String,
        sortBy: String
    ) = repositoryScope.async {
        discussionCommentsFetcher.fetchRemote(
            DiscussionCommentsFetcher.Params(
                key = key,
                discussionId = discussionId,
                sortBy = sortBy
            )
        )
    }.await()

    suspend fun fetchQandaComments(
        qandaId: String,
        key: String,
        sortBy: String
    ) = repositoryScope.async {
        qandaCommentsFetcher.fetchRemote(QandaCommentsFetcher.Params(key = key, qandaId = qandaId, sortBy = sortBy))
    }.await()

    suspend fun fetchGameComments(
        gameId: String,
        teamId: String = "",
        key: String,
        sortBy: String
    ) = repositoryScope.async {
        gameCommentsFetcher.fetchRemote(
            GameCommentsFetcher.Params(
                key = key,
                gameId = gameId,
                teamId = teamId,
                sortBy = sortBy
            )
        )
    }.await()

    fun subscribeForNewQaComments(
        discussionId: String,
        key: String
    ) = repositoryScope.launch {
        qandaCommentSubscriber.subscribe(QandaCommentSubscriber.Params(key = key, qandaId = discussionId))
    }

    suspend fun addComment(
        commentInput: CommentInput
    ): Comment = commentsApi.addComment(
        comment = commentInput.content,
        contentId = commentInput.sourceDescriptor.id,
        commentsSourceType = commentInput.sourceType,
        parentId = commentInput.parentId,
        teamId = commentInput.teamId
    ).toDomain(isAuthor = true)

    suspend fun deleteComment(commentId: String) = safeApiRequest {
        mapApolloDeleteCommentResponseSuccess(
            commentsApi.deleteCommentAsync(commentId)
        )
    }

    suspend fun editComment(commentId: String, comment: String) = safeApiRequest {
        mapApolloEditCommentResponseSuccess(
            commentsApi.editCommentAsync(commentId, comment)
        )
    }

    suspend fun flagComment(commentId: String, reason: FlagReason) = safeApiRequest {
        mapApolloFlagCommentResponseSuccess(
            commentsApi.flagCommentAsync(commentId, reason)
        )
    }

    suspend fun likeComment(commentId: String) = commentsApi.likeComment(commentId).likeComment

    suspend fun unlikeComment(commentId: String) = commentsApi.unlikeComment(commentId).unlikeComment

    suspend fun likeArticleComment(articleId: Long, commentId: Long) {
        articleLikeCommentFetcher.fetchRemote(ArticleLikeCommentFetcher.Params(articleId, commentId))
    }

    suspend fun unlikeArticleComment(articleId: Long, commentId: Long) {
        articleUnlikeCommentFetcher.fetchRemote(ArticleUnlikeCommentFetcher.Params(articleId, commentId))
    }

    suspend fun deleteCommentArticle(articleId: Long, commentId: Long) {
        articleDeleteCommentFetcher.fetchRemote(ArticleDeleteCommentFetcher.Params(articleId, commentId))
    }

    suspend fun flagCommentArticle(articleId: Long, commentId: Long, flagReason: FlagReason) {
        articleFlagCommentFetcher.fetchRemote(
            ArticleFlagCommentFetcher.Params(articleId, commentId, flagReason)
        )
    }
}