package com.theathletic.comments.analytics

import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.entity.user.SortType

@Suppress("LongParameterList")
interface CommentsAnalyticsV2 {
    fun trackRefreshComments(
        sourceId: String,
        sourceType: CommentsSourceType,
        teamId: String?,
        analyticsPayload: CommentsAnalyticsPayload?,
    )

    fun trackSortComments(
        sourceId: String,
        sourceType: CommentsSourceType,
        sortBy: SortType,
        teamId: String?,
        analyticsPayload: CommentsAnalyticsPayload?,
    )

    fun trackAllComments(
        sourceId: String,
        sourceType: CommentsSourceType,
        analyticsPayload: CommentsAnalyticsPayload?,
        uid: String,
        teamId: String?,
    )

    fun trackCommentDwell(
        sourceId: String,
        sourceType: CommentsSourceType,
        analyticsPayload: CommentsAnalyticsPayload?,
        seconds: Int,
        uid: String,
    )

    fun trackArticleView(
        articleId: String,
        source: ClickSource
    )

    fun trackLikeComment(
        commentId: String,
        sourceId: String,
        sourceType: CommentsSourceType,
        filterType: SortType,
        index: Int,
        teamId: String?,
    )

    fun trackUnlikeComment(
        commentId: String,
        sourceId: String,
        sourceType: CommentsSourceType,
        filterType: SortType,
        index: Int,
        teamId: String?,
    )

    fun trackCommentSubmission(
        sourceId: String,
        source: CommentsSourceType
    )

    fun trackEditComment(commentId: String)

    fun trackDeleteComment(commentId: String)

    fun trackFlagComment(commentId: String, filter: SortType, index: Int, teamId: String?)

    fun trackThreadSwitch(gameStatus: String, gameId: String, currentTeamId: String?, clickedTeamId: String?)

    fun getImpressionPayload(commentId: String, gameId: String, commentIndex: Int): ImpressionPayload

    fun ImpressionPayload.impress(
        teamId: String?,
        startTime: Long,
        endTime: Long,
    )
}

@Exposes(CommentsAnalyticsV2::class)
internal
class CommentsAnalyticsV2Impl @AutoKoin constructor(
    val analytics: IAnalytics
) : CommentsAnalyticsV2 {
    override fun trackAllComments(
        sourceId: String,
        sourceType: CommentsSourceType,
        analyticsPayload: CommentsAnalyticsPayload?,
        uid: String,
        teamId: String?,
    ) {
        if (sourceType.isGame()) {
            analytics.track(
                Event.Discuss.View(
                    view = analyticsPayload?.gameStatusView.orEmpty(),
                    object_id = sourceId,
                    league_id = analyticsPayload?.leagueId.orEmpty(),
                    comment_view_link_id = uid,
                    team_id = teamId.orEmpty()
                )
            )
        } else {
            analytics.track(
                Event.Comments.AllCommentsView(
                    object_id = sourceId,
                    object_type = sourceType.sourceIdType,
                    comment_view_link_id = uid,
                )
            )
        }
    }

    override fun trackCommentDwell(
        sourceId: String,
        sourceType: CommentsSourceType,
        analyticsPayload: CommentsAnalyticsPayload?,
        seconds: Int,
        uid: String,
    ) {
        analytics.track(
            Event.Comments.Dwell(
                view = analyticsPayload?.gameStatusView ?: "comments",
                object_type = sourceType.sourceIdType,
                object_id = sourceId,
                comment_view_link_id = uid,
                seconds = seconds.toString(),
            )
        )
    }

    override fun trackArticleView(articleId: String, source: ClickSource) {
        analytics.track(
            Event.Article.View(
                article_id = articleId,
                source = source.value,
                percent_read = ""
            )
        )
    }

    override fun trackRefreshComments(
        sourceId: String,
        sourceType: CommentsSourceType,
        teamId: String?,
        analyticsPayload: CommentsAnalyticsPayload?
    ) {
        if (sourceType.isGame()) {
            trackGameCommentsRefresh(
                gameId = sourceId,
                teamId = teamId,
                analyticsPayload = analyticsPayload
            )
        }
    }

    override fun trackSortComments(
        sourceId: String,
        sourceType: CommentsSourceType,
        sortedBy: SortType,
        teamId: String?,
        analyticsPayload: CommentsAnalyticsPayload?
    ) {
        if (sourceType.isGame()) {
            trackGameCommentsSort(
                gameId = sourceId,
                teamId = teamId.orEmpty(),
                sortBy = sortedBy,
                analyticsPayload = analyticsPayload
            )
        } else {
            trackCommentsSort(
                id = sourceId,
                sourceType = sourceType,
                sortedBy = sortedBy,
            )
        }
    }

    override fun trackLikeComment(
        commentId: String,
        sourceId: String,
        sourceType: CommentsSourceType,
        filterType: SortType,
        index: Int,
        teamId: String?,
    ) {
        analytics.track(
            Event.Comments.Like(
                object_type = "comment_id",
                object_id = commentId,
                article_id = if (sourceType.isArticle) sourceId else "",
                podcast_episode_id = if (sourceType.isPodcast) sourceId else "",
                headline_id = if (sourceType.isHeadline) sourceId else "",
                filter_type = filterType.value,
                v_index = index.toString(),
                team_id = teamId.orEmpty()
            )
        )
    }

    override fun trackUnlikeComment(
        commentId: String,
        sourceId: String,
        sourceType: CommentsSourceType,
        filterType: SortType,
        index: Int,
        teamId: String?,
    ) {
        analytics.track(
            Event.Comments.Unlike(
                object_type = "comment_id",
                object_id = commentId,
                article_id = if (sourceType.isArticle) sourceId else "",
                podcast_episode_id = if (sourceType.isPodcast) sourceId else "",
                headline_id = if (sourceType.isHeadline) sourceId else "",
                filter_type = filterType.value,
                v_index = index.toString(),
                team_id = teamId.orEmpty()
            )
        )
    }

    override fun trackEditComment(commentId: String) {
        analytics.track(
            Event.Comments.Edit(object_id = commentId)
        )
    }

    override fun trackDeleteComment(commentId: String) {
        analytics.track(
            Event.Comments.Delete(object_id = commentId)
        )
    }

    override fun trackFlagComment(commentId: String, filter: SortType, index: Int, teamId: String?) {
        analytics.track(
            Event.Headline.Flag(
                element = "comment",
                object_type = "comment_id",
                object_id = commentId,
                filter_type = filter.value,
                v_index = index.toString(),
                team_id = teamId.orEmpty()
            )
        )
    }

    override fun trackCommentSubmission(
        sourceId: String,
        source: CommentsSourceType,
    ) {
        when (source) {
            CommentsSourceType.HEADLINE -> trackSubmitHeadlineComment(sourceId)
            CommentsSourceType.ARTICLE -> trackSubmitArticleComment("article", sourceId)
            CommentsSourceType.PODCAST_EPISODE -> trackSubmitPodcastComment(sourceId)
            CommentsSourceType.DISCUSSION -> trackSubmitArticleComment("discussion", sourceId)
            CommentsSourceType.QANDA -> trackSubmitArticleComment("q_and_a", sourceId)
            CommentsSourceType.GAME -> trackSubmitArticleComment("game", sourceId)
            CommentsSourceType.TEAM_SPECIFIC_THREAD -> trackSubmitArticleComment("game", sourceId)
        }
    }

    override fun trackThreadSwitch(
        gameStatus: String,
        gameId: String,
        currentTeamId: String?,
        clickedTeamId: String?,
    ) {
        analytics.track(
            Event.GameSpecificThreads.ChangeTeamSpace(
                view = gameStatus,
                current_team_id = currentTeamId.orEmpty(),
                clicked_team_id = clickedTeamId.orEmpty(),
                object_id = gameId,
            )
        )
    }

    private fun trackSubmitPodcastComment(podcastEpisodeId: String) {
        analytics.track(
            Event.Article.CommentAdded(
                element = "podcast_episode",
                object_type = "podcast_episode_id",
                object_id = podcastEpisodeId
            )
        )
    }

    private fun trackSubmitHeadlineComment(headlineId: String) {
        analytics.track(
            Event.Headline.Add(
                element = "comment",
                object_type = "headline_id",
                object_id = headlineId
            )
        )
    }

    private fun trackSubmitArticleComment(
        sourceElement: String,
        articleId: String
    ) {
        analytics.track(
            Event.Article.CommentAdded(
                element = sourceElement,
                object_type = "article_id",
                object_id = articleId
            )
        )
    }

    private fun trackGameCommentsRefresh(
        gameId: String,
        teamId: String?,
        analyticsPayload: CommentsAnalyticsPayload?
    ) {
        analytics.track(
            Event.Discuss.Click(
                view = analyticsPayload?.gameStatusView.orEmpty(),
                element = "refresh",
                object_id = gameId,
                league_id = analyticsPayload?.leagueId.orEmpty(),
                game_id = gameId,
                team_id = teamId.orEmpty()
            )
        )
    }

    private fun trackGameCommentsSort(
        gameId: String,
        teamId: String?,
        sortBy: SortType,
        analyticsPayload: CommentsAnalyticsPayload?
    ) {
        analytics.track(
            Event.Discuss.Click(
                view = analyticsPayload?.gameStatusView.orEmpty(),
                element = "sort",
                object_type = sortBy.value,
                game_id = gameId,
                league_id = analyticsPayload?.leagueId.orEmpty(),
                team_id = teamId.orEmpty()
            )
        )
    }

    private fun trackCommentsSort(
        id: String,
        sourceType: CommentsSourceType,
        sortedBy: SortType,
    ) {
        analytics.track(
            Event.Comments.Sort(
                object_type = sortedBy.value,
                article_id = if (sourceType.isArticle) id else "",
                headline_id = if (sourceType.isHeadline) id else "",
                podcast_episode_id = if (sourceType.isPodcast) id else "",
            )
        )
    }

    override fun ImpressionPayload.impress(
        teamId: String?,
        startTime: Long,
        endTime: Long,
    ) {
        analytics.track(
            Event.GameSpecificThreads.Impression(
                object_id = objectId,
                filter_id = teamId?.toLong(),
                impress_start_time = startTime,
                impress_end_time = endTime,
                v_index = vIndex,
                parent_object_id = parentObjectId
            )
        )
    }

    override fun getImpressionPayload(commentId: String, gameId: String, commentIndex: Int): ImpressionPayload {
        return ImpressionPayload(
            element = "comment",
            objectType = "comment_id",
            objectId = commentId,
            parentObjectType = "game_id",
            parentObjectId = gameId,
            vIndex = commentIndex.toLong(),
            pageOrder = -1
        )
    }
}