package com.theathletic.article.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.UiModel
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon

object RelatedContentSectionTitle : UiModel {
    override val stableId = "RelatedArticleSectionTitle"
}

data class RelatedContentItem(
    val id: RelatedContentItemId,
    val imageUrl: String,
    val timeAgo: String,
    val showTimeAgo: Boolean,
    val title: String,
    val byline: String,
    val showByline: Boolean,
    val showComments: Boolean,
    val commentCount: String,
    val showLiveStatus: Boolean
) : UiModel {
    override val stableId = "RelatedContentItem:$id"

    interface Interactor {
        fun onRelatedContentClicked(contentId: RelatedContentItemId)
    }
}

data class RelatedContentItemId(
    val id: String,
    val type: ContentType
) {
    enum class ContentType {
        ARTICLE,
        HEADLINE,
        QANDA,
        DISCUSSION,
        LIVEBLOG
    }
}

@Composable
fun RelatedContentRow(
    relatedContentItem: RelatedContentItem,
    interactor: RelatedContentItem.Interactor
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { interactor.onRelatedContentClicked(relatedContentItem.id) }
            .padding(16.dp)
    ) {
        RemoteImageAsync(
            modifier = Modifier
                .size(108.dp),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            url = relatedContentItem.imageUrl
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 8.dp),
                text = relatedContentItem.title,
                style = AthTextStyle.TiemposHeadline.Regular.ExtraExtraSmall,
                color = AthTheme.colors.dark800,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Row {
                if (relatedContentItem.showByline) {
                    Text(
                        text = relatedContentItem.byline,
                        style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                        color = AthTheme.colors.dark500,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
                if (relatedContentItem.showComments) {
                    ResourceIcon(
                        modifier = Modifier
                            .padding(bottom = 1.dp, start = 8.dp, end = 4.dp)
                            .size(9.dp)
                            .align(Alignment.Bottom),
                        resourceId = R.drawable.ic_news_comment,
                        tint = AthTheme.colors.dark500
                    )
                    Text(
                        text = relatedContentItem.commentCount,
                        style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                        color = AthTheme.colors.dark500,
                        maxLines = 1
                    )
                }
                if (relatedContentItem.showLiveStatus) {
                    val circleColor = AthTheme.colors.red
                    Canvas(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(6.dp)
                            .align(Alignment.CenterVertically),
                        onDraw = {
                            drawCircle(color = circleColor)
                        }
                    )
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = stringResource(id = R.string.feed_live).uppercase(),
                        color = AthTheme.colors.red,
                        style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall
                    )
                }
                if (relatedContentItem.showTimeAgo) {
                    Text(
                        text = relatedContentItem.timeAgo,
                        style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                        color = AthTheme.colors.dark500,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview(backgroundColor = 0xFF000000, showBackground = true, name = "Article")
@Composable
private fun RelatedContentRow_Article_Preview() {
    AthleticTheme(lightMode = false) {
        Column {
            RelatedContentRow(article, emptyInteractor)
        }
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, name = "Article Light")
@Composable
private fun RelatedContentRow_ArticleLight_Preview() {
    AthleticTheme(lightMode = true) {
        Column {
            RelatedContentRow(article, emptyInteractor)
        }
    }
}

@Preview(backgroundColor = 0xFF000000, showBackground = true, name = "Article with Comments")
@Composable
private fun RelatedContentRow_ArticleWithComments_Preview() {
    AthleticTheme(lightMode = false) {
        Column {
            RelatedContentRow(articleWithComments, emptyInteractor)
        }
    }
}

@Preview(backgroundColor = 0xFF000000, showBackground = true, name = "Liveblog")
@Composable
private fun RelatedContentRowLiveblog_Preview() {
    AthleticTheme(lightMode = false) {
        Column {
            RelatedContentRow(liveBlog, emptyInteractor)
        }
    }
}

private val emptyInteractor = object : RelatedContentItem.Interactor {
    override fun onRelatedContentClicked(contentId: RelatedContentItemId) {
    }
}

private val article = RelatedContentItem(
    id = RelatedContentItemId("id", RelatedContentItemId.ContentType.ARTICLE),
    imageUrl = "",
    timeAgo = "2 hours ago",
    showTimeAgo = false,
    title = "49ers minutia minute: Christian McCaffrey-Deebo Samuel tandem opens up possibilities",
    byline = "Matt Barrows",
    showByline = true,
    showComments = false,
    commentCount = "33",
    showLiveStatus = false
)

private val liveBlog = RelatedContentItem(
    id = RelatedContentItemId("id", RelatedContentItemId.ContentType.LIVEBLOG),
    imageUrl = "",
    timeAgo = "2 hours ago",
    showTimeAgo = true,
    title = "49ers minutia minute: Christian McCaffrey-Deebo Samuel tandem opens up possibilities",
    byline = "Matt Barrows",
    showByline = false,
    showComments = false,
    commentCount = "33",
    showLiveStatus = true
)
private val articleWithComments = RelatedContentItem(
    id = RelatedContentItemId("id", RelatedContentItemId.ContentType.ARTICLE),
    imageUrl = "",
    timeAgo = "2 hours ago",
    showTimeAgo = false,
    title = "49ers minutia minute: Christian McCaffrey-Deebo Samuel tandem opens up possibilities",
    byline = "Matt Barrows",
    showByline = true,
    showComments = true,
    commentCount = "33",
    showLiveStatus = false
)