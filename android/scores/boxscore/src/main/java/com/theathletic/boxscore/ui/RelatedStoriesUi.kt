package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.boxscore.ui.BoxScorePreviewData.relatedStoriesInteractorMock
import com.theathletic.boxscore.ui.BoxScorePreviewData.relatedStoriesMock
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.UiModel
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon

data class RelatedStoriesUiModel(
    val id: String,
    val articles: List<RelatedStoriesUi.Article>
) : UiModel {
    override val stableId = "RelatedStories:$id"
}

sealed class RelatedStoriesUi {

    data class RelatedStoriesAnalyticsPayload(
        val articleId: String,
        val gameId: String,
        val leagueId: String,
        val pageOrder: Int,
        val articlePosition: Int
    ) : AnalyticsPayload

    data class Article(
        val id: String,
        val gameId: String,
        val title: String,
        val imageUrl: String,
        val authors: ResourceString,
        val commentCount: String,
        val showCommentCount: Boolean,
        val analyticsPayload: RelatedStoriesAnalyticsPayload,
        val impressionPayload: ImpressionPayload
    )

    interface Interactor {
        fun onArticleClick(
            analyticsPayload: RelatedStoriesAnalyticsPayload
        )
    }

    interface Interaction {
        data class OnArticleClick(
            val analyticsPayload: RelatedStoriesAnalyticsPayload
        ) : FeedInteraction
    }
}

@Composable
fun RelatedStories(
    articles: List<RelatedStoriesUi.Article>,
    includeDivider: Boolean = false, // todo (Adil): Remove this when cleaning up RelatedStoriesUiModel
    includeTopDivider: Boolean = false, // todo (Adil): Remove this when cleaning up RelatedStoriesUiModel
    onArticleClick: (analyticsPayload: RelatedStoriesUi.RelatedStoriesAnalyticsPayload) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {

        if (includeTopDivider) {
            BoxScoreHeaderDivider()
        }
        BoxScoreHeaderTitle(R.string.box_score_related_stories_title)

        articles.forEachIndexed { index, article ->
            ArticleRow(
                title = article.title,
                authors = article.authors,
                commentCount = article.commentCount,
                imageUrl = article.imageUrl,
                showCommentCount = article.showCommentCount,
                analyticsPayload = article.analyticsPayload,
                onArticleClick = onArticleClick
            )
            if (index < articles.lastIndex)
                Divider(modifier = Modifier.padding(horizontal = 16.dp), color = AthTheme.colors.dark300)
        }

        if (includeDivider) {
            BoxScoreFooterDivider(false)
        }
    }
}

@Composable
private fun ArticleRow(
    title: String,
    imageUrl: String,
    authors: ResourceString,
    commentCount: String,
    showCommentCount: Boolean,
    analyticsPayload: RelatedStoriesUi.RelatedStoriesAnalyticsPayload,
    onArticleClick: (analyticsPayload: RelatedStoriesUi.RelatedStoriesAnalyticsPayload) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = AthTheme.colors.dark200)
            .clickable {
                onArticleClick(analyticsPayload)
            },
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 100.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {

            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = title,
                style = AthTextStyle.TiemposHeadline.Regular.ExtraExtraSmall,
                color = AthTheme.colors.dark700,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = authors.asString(),
                    style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                    color = AthTheme.colors.dark500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (showCommentCount) {
                    CommentCount(commentCount)
                }
            }
        }

        if (imageUrl.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .aspectRatio(1f),
                propagateMinConstraints = true
            ) {
                RemoteImageAsync(
                    url = imageUrl,
                    contentScale = ContentScale.FillHeight,
                )
            }
        }
    }
}

@Composable
private fun CommentCount(commentCount: String) {
    ResourceIcon(
        resourceId = R.drawable.ic_news_comment,
        tint = AthTheme.colors.dark500,
        modifier = Modifier
            .padding(start = 8.dp)
            .size(10.dp, 10.dp)
    )
    Text(
        modifier = Modifier.padding(start = 4.dp, end = 8.dp),
        text = commentCount,
        style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
        color = AthTheme.colors.dark500,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview
@Composable
private fun RelatedStories_Preview() {
    RelatedStories(
        relatedStoriesMock
    ) { relatedStoriesInteractorMock }
}

@Preview(device = Devices.PIXEL)
@Composable
private fun RelatedStories_PreviewSmallDevice() {
    RelatedStories(
        relatedStoriesMock
    ) { relatedStoriesInteractorMock }
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun RelatedStories_PreviewLargeDevice() {
    RelatedStories(
        relatedStoriesMock
    ) { relatedStoriesInteractorMock }
}

@Preview
@Composable
private fun RelatedStories_PreviewLight() {
    AthleticTheme(lightMode = true) {
        RelatedStories(
            relatedStoriesMock
        ) { relatedStoriesInteractorMock }
    }
}