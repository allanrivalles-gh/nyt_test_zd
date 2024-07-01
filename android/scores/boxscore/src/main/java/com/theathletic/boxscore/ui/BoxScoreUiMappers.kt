package com.theathletic.boxscore.ui

import com.theathletic.boxscore.data.local.Article
import com.theathletic.boxscore.data.local.BasicHeader
import com.theathletic.boxscore.data.local.BoxScore
import com.theathletic.boxscore.data.local.BoxScoreModules
import com.theathletic.boxscore.data.local.Items
import com.theathletic.boxscore.data.local.LatestNewsModule
import com.theathletic.boxscore.data.local.ModuleHeader
import com.theathletic.boxscore.data.local.PodcastEpisode
import com.theathletic.boxscore.data.local.Section
import com.theathletic.datetime.asGMTString
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.feed.compose.ui.components.podcast.FeedPodcastEpisodeUiModel
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.utility.datetime.DateUtilityImpl
import java.util.Date

fun BoxScore.toBoxScoreUi(): BoxScoreUiModel {
    return BoxScoreUiModel(
        sections = sections.map { it.toSectionsUi() }
    )
}

private fun Section.toSectionsUi(): BoxScoreUiModel.SectionsUiModel {
    return BoxScoreUiModel.SectionsUiModel(
        id = id,
        type = type,
        modules = modules.mapNotNull { it.toModulesUi() }
    )
}

private fun BoxScoreModules.toModulesUi(): BoxScoreModuleUiModel? {
    if (this is LatestNewsModule) {
        return BoxScoreUiModel.LatestNewsUiModel(
            id = id,
            header = header.toHeaderUi(),
            blocks = blocks.mapNotNull { it.toUi() }
        )
    }
    return null
}

private fun Items.toUi(): LayoutUiModel.Item? {
    return when (this) {
        is Article -> ArticleUiModel(
            id = articleId,
            title = title,
            excerpt = description.orEmpty(),
            imageUrl = imageUri.orEmpty(),
            byline = authors.orEmpty(),
            commentCount = if (commentCount < 1) "" else commentCount.toString(),
            isBookmarked = isBookmarked,
            isRead = isRead,
            postType = PostType.ARTICLE,
            permalink = permalink ?: "",
            analyticsData = AnalyticsData("", 0, "")
        )
        is PodcastEpisode -> {
            val remainingTime = duration?.minus(timeElapsed ?: 0)
            FeedPodcastEpisodeUiModel(
                podcastId = podcastId,
                id = episodeId,
                date = DateUtilityImpl.formatPodcastDate(Date(publishedAt.timeMillis).asGMTString()),
                title = title,
                description = description.orEmpty(),
                duration = DateUtilityImpl.formatPodcastDuration(remainingTime?.times(1000L) ?: 0L),
                progress = timeElapsed?.toFloat()?.div(duration?.toFloat() ?: 1f) ?: 0f,
                playbackState = playbackState,
                downloadState = downloadState,
                imageUrl = imageUrl.orEmpty(),
                permalink = permalink,
                analyticsData = null
            )
        }
        else -> null
    }
}

private fun ModuleHeader?.toHeaderUi(): BoxScoreUiModel.BasicHeaderUiModel? {
    if (this is BasicHeader) {
        return BoxScoreUiModel.BasicHeaderUiModel(
            id = id,
            title = title
        )
    }
    return null
}