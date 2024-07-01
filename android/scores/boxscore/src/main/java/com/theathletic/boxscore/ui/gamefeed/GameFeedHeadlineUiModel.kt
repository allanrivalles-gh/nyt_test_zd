package com.theathletic.boxscore.ui.gamefeed

import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString

data class GameFeedHeadlineUiModel(
    val id: String,
    val title: String,
    val excerpt: String,
    val authorText: String,
    val datetime: ParameterizedString,
    override val impressionPayload: ImpressionPayload,
    val analyticsPayload: GameFeedHeadlineAnalyticsPayload,
    val tag: String? = null,
    val readMoreId: String? = null,
    val readMoreText: String? = null,
    val imageUrl: String? = null
) : UiModel {

    override val stableId = "GameFeedHeadline:$id"

    interface Interactor {
        fun onGameFeedHeadlineClicked(id: String)
        fun onLiveBlogRelatedArticleClicked(
            articleId: String,
            analyticsPayload: GameFeedHeadlineAnalyticsPayload
        )
    }
}

data class GameFeedHeadlineAnalyticsPayload(
    val moduleIndex: Int,
    val blogPostId: String
) : AnalyticsPayload

data class GameFeedBriefUiModel(
    val id: String,
    val age: ParameterizedString,
    val authorName: String,
    val authorImageUrl: String,
    val authorDescription: String,
    val text: String,
    val imageUrl: String?,
    val isLiked: Boolean,
    val likeCount: String,
    val commentCount: String,
    override val impressionPayload: ImpressionPayload,
) : UiModel {

    override val stableId = "GameFeedBrief:$id"
}

data class GameFeedBlogHeaderUiModel(
    val id: String,
    val title: String,
    val description: String,
    val showLiveTag: Boolean,
    val updatedTime: ParameterizedString,
    val imageUrl: String?,
    val isHeaderCollapsed: Boolean,
    val showMoreLessButton: Boolean
) : UiModel {

    override val stableId = "GameFeedBlogHeaderUiModel:$id"

    interface Interactor {
        fun onMoreLessClicked()
    }
}