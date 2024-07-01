package com.theathletic.comments.analytics

import com.theathletic.comments.v2.data.local.CommentsSourceType
val CommentsSourceType.isHeadline: Boolean get() = this == CommentsSourceType.HEADLINE
val CommentsSourceType.isArticle: Boolean get() = this == CommentsSourceType.ARTICLE
val CommentsSourceType.isPodcast: Boolean get() = this == CommentsSourceType.PODCAST_EPISODE
val CommentsSourceType.sourceIdType: String
    get() = when (this) {
        CommentsSourceType.ARTICLE -> "article_id"
        CommentsSourceType.PODCAST_EPISODE -> "podcast_episode_id"
        CommentsSourceType.HEADLINE -> "headline_id"
        CommentsSourceType.DISCUSSION -> "article_id"
        CommentsSourceType.QANDA -> "article_id"
        CommentsSourceType.GAME -> "game_id"
        CommentsSourceType.TEAM_SPECIFIC_THREAD -> "game_id"
    }