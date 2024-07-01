package com.theathletic.feed.compose.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.SOURCE_FEED
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.reusables.ArticleTitle
import com.theathletic.feed.compose.ui.reusables.ContentImage
import com.theathletic.feed.compose.ui.reusables.Date
import com.theathletic.feed.compose.ui.reusables.FeedItemFooter
import com.theathletic.feed.compose.ui.reusables.FeedItemFooterUiModel
import com.theathletic.feed.compose.ui.reusables.SmallReadIndicator
import com.theathletic.feed.compose.ui.reusables.getImage
import com.theathletic.links.deep.Deeplink
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

data class A1UiModel(
    override val id: String,
    val imageUrl: String,
    val title: String,
    val publishDate: String,
    val commentCount: String,
    val isBookmarked: Boolean,
    val isRead: Boolean,
    val byLine: String,
    val avatars: List<String>,
    override val permalink: String?,
    override val analyticsData: AnalyticsData
) : LayoutUiModel.Item {
    override fun deepLink() = Deeplink.article(id).addSource(SOURCE_FEED)
}

@Composable
fun A1(model: A1UiModel, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .width(intrinsicSize = IntrinsicSize.Min)
            .background(AthTheme.colors.dark300, shape = RoundedCornerShape(2.dp))
            .then(modifier)
    ) {
        Box(Modifier.fillMaxWidth()) {
            ContentImage(
                getImage(PostType.ARTICLE, model.imageUrl),
                isRead = model.isRead,
                overlay = { A1ImageOverlay(model) },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(200.dp)
                    .aspectRatio(ratio = 3f / 2)
            )
            SubtleOverlay(modifier = Modifier.align(Alignment.BottomCenter))
        }

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        ) {
            ArticleTitle(
                text = model.title,
                style = AthTextStyle.TiemposHeadline.Regular.ExtraSmall,
                isRead = model.isRead,
                modifier = Modifier
                    .padding(top = 16.dp),
                minLines = 3
            )
            FeedItemFooter(
                data = FeedItemFooterUiModel(
                    isBookmarked = model.isBookmarked,
                    byline = model.byLine,
                    commentCount = model.commentCount,
                    avatarUrls = model.avatars
                ),
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 16.dp),
                authorBorderColor = AthTheme.colors.dark300,
            )
        }
    }
}

@Composable
private fun A1ImageOverlay(model: A1UiModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 10.dp, end = 10.dp)
    ) {
        Date(date = model.publishDate)
        if (model.isRead) {
            SmallReadIndicator(modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
private fun SubtleOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(70.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.15f)
                    )
                )
            )
    )
}

private class A1PreviewParamProvider :
    PreviewParameterProvider<A1UiModel> {
    override val values: Sequence<A1UiModel> = sequenceOf(
        a1PreviewModel(),
        a1PreviewModel(isRead = true, commentCount = "1k"),
        a1PreviewModel(isBookmarked = true)
    )

    private fun a1PreviewModel(
        isRead: Boolean = false,
        isBookmarked: Boolean = false,
        commentCount: String = "125"
    ): A1UiModel {
        return A1UiModel(
            id = "a1Id",
            imageUrl = "",
            title = "Top 10 mock draft with The Athletic NFL Staff",
            commentCount = commentCount,
            byLine = "Marc Mazzoni and Jonathan Stewart",
            avatars = listOf("", ""),
            isBookmarked = isBookmarked,
            isRead = isRead,
            publishDate = "5 Jun",
            analyticsData = analyticsPreviewData(),
            permalink = ""
        )
    }
}

@DayNightPreview
@Composable
private fun A1Preview(
    @PreviewParameter(A1PreviewParamProvider::class)
    model: A1UiModel
) {
    return AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        A1(model)
    }
}