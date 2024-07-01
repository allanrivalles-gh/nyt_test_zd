package com.theathletic.comments.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.data.remote.handler.ArticleCommentsDataHandler
import com.theathletic.comments.data.remote.handler.BaseCommentsDataHandler
import com.theathletic.comments.data.remote.handler.DiscussionCommentsDataHandler
import com.theathletic.comments.data.remote.handler.GameCommentsDataHandler
import com.theathletic.comments.data.remote.handler.PodcastEpisodeCommentsDataHandler
import com.theathletic.comments.data.remote.handler.QandaCommentsDataHandler
import com.theathletic.comments.v2.data.local.CommentsSourceType

class CommentsDataHandlerFactory @AutoKoin constructor(
    private val commentsRepository: CommentsRepository
) {
    fun instantiate(sourceType: CommentsSourceType): BaseCommentsDataHandler {
        return when (sourceType) {
            CommentsSourceType.ARTICLE, CommentsSourceType.HEADLINE -> ArticleCommentsDataHandler(
                commentsRepository
            )
            CommentsSourceType.PODCAST_EPISODE -> PodcastEpisodeCommentsDataHandler(
                commentsRepository
            )
            CommentsSourceType.DISCUSSION -> DiscussionCommentsDataHandler(
                commentsRepository
            )
            CommentsSourceType.QANDA -> QandaCommentsDataHandler(
                commentsRepository
            )
            CommentsSourceType.GAME -> GameCommentsDataHandler(commentsRepository)
            CommentsSourceType.TEAM_SPECIFIC_THREAD -> GameCommentsDataHandler(
                commentsRepository
            )
        }
    }
}