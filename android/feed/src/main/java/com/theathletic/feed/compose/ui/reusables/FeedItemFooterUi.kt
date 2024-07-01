package com.theathletic.feed.compose.ui.reusables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.widgets.ResourceIcon

data class FeedItemFooterUiModel(
    val isBookmarked: Boolean,
    val byline: String,
    val commentCount: String,
    val avatarUrls: List<String>? = null
)

/**
 * FeedItemFooter displays an optional bookmarked icon, feed item byline,
 * comment count icon, and comment count.
 */
@Composable
fun FeedItemFooter(
    data: FeedItemFooterUiModel,
    modifier: Modifier = Modifier,
    authorBorderColor: Color = AthTheme.colors.dark200
) {
    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            // helps vertically align the text to the icon to match the spec visuals
            val textBottomPadding = 2.dp

            val facepileBorderWidth = 2
            var bookmarkEndPadding = 8
            if (data.avatarUrls != null) {
                // Since avatar border is same color as background, it serves as padding when facepile is visible
                bookmarkEndPadding -= facepileBorderWidth
            }

            if (data.isBookmarked) {
                ResourceIcon(
                    resourceId = R.drawable.ic_feed_bookmark,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = bookmarkEndPadding.dp)
                )
            }

            data.avatarUrls?.also { urls ->
                Facepile(
                    urls,
                    modifier = Modifier.padding(end = 6.dp),
                    maxDisplayedAvatars = 3,
                    borderColor = authorBorderColor,
                    borderWidthDp = facepileBorderWidth
                )
            }

            Text(
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                text = data.byline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(bottom = textBottomPadding),
            )

            if (data.commentCount.isNotBlank()) {
                ResourceIcon(
                    resourceId = R.drawable.ic_feed_news_comment,
                    tint = AthTheme.colors.dark500,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 8.dp),
                )

                Text(
                    color = AthTheme.colors.dark500,
                    style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                    text = data.commentCount,
                    modifier = Modifier
                        .padding(start = 2.dp, bottom = 2.dp),
                )
            }
        }
    }
}

private class FeedItemFooterPreviewProvider {
    @Composable
    fun Create(
        lightMode: Boolean = false,
        isBookmarked: Boolean = false,
        smallPreviewWidth: Boolean = false,
        avatarUrls: List<String>? = null
    ) {
        return AthleticTheme(lightMode = lightMode) {
            var boxModifier = Modifier.background(AthTheme.colors.dark200)

            if (smallPreviewWidth) {
                boxModifier = boxModifier.then(Modifier.width(110.dp))
            }

            Box(boxModifier) {
                FeedItemFooter(
                    FeedItemFooterUiModel(
                        isBookmarked = isBookmarked,
                        byline = "Marc Mazzoni, Levi Weaver, and Jonathan Stewart",
                        commentCount = "1k",
                        avatarUrls = avatarUrls
                    ),
                    authorBorderColor = AthTheme.colors.dark200
                )
            }
        }
    }
}

@Preview(widthDp = 320)
@Composable
fun FeedItemFooterPreview() = FeedItemFooterPreviewProvider().Create()

@Preview
@Composable
fun FeedItemFooterPreview_Light() = FeedItemFooterPreviewProvider().Create(lightMode = true)

@Preview
@Composable
fun FeedItemFooterPreview_Bookmarked() = FeedItemFooterPreviewProvider().Create(isBookmarked = true)

@Preview
@Composable
fun FeedItemFooterPreview_Bookmarked_Light() =
    FeedItemFooterPreviewProvider().Create(isBookmarked = true, lightMode = true)

@Preview(widthDp = 320)
@Composable
fun FeedItemFooterPreview_Bookmarked_LongByline() =
    FeedItemFooterPreviewProvider().Create(
        isBookmarked = true,
        smallPreviewWidth = true,
        avatarUrls = listOf("", "", "")
    )