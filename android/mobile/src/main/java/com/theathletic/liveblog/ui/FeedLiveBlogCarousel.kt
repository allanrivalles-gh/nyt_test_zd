package com.theathletic.liveblog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.R
import com.theathletic.feed.ui.FeedContract
import com.theathletic.feed.ui.models.LiveBlogAnalyticsPayload
import com.theathletic.feed.ui.models.LiveBlogCarouselItem
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.asString

private const val FIRST_INDEX = 0

@Composable
fun FeedLiveBlogCarousel(
    liveBlogs: List<LiveBlogCarouselItem>,
    interactor: FeedContract.Presenter
) {
    if (liveBlogs.isNotEmpty()) {
        LiveBlogCarousel(
            liveBlogs = liveBlogs,
            onLiveBlogClick = { liveBlog ->
                interactor.onLiveBlogClick(liveBlog.id, liveBlog.analyticsPayload)
            }
        )
    }
}

@Composable
private fun LiveBlogCarousel(
    liveBlogs: List<LiveBlogCarouselItem>,
    onLiveBlogClick: (LiveBlogCarouselItem) -> Unit
) {
    LazyRow {
        itemsIndexed(liveBlogs) { index, liveBlog ->
            LiveBlogItem(
                liveBlog = liveBlog,
                liveIndicator = { if (index == FIRST_INDEX) LiveIndicator() },
                divider = { if (index != liveBlogs.lastIndex) RowDivider() },
                onClick = { onLiveBlogClick(liveBlog) }
            )
        }
    }
}

@Composable
private fun LiveBlogItem(
    liveBlog: LiveBlogCarouselItem,
    liveIndicator: @Composable () -> Unit = {},
    divider: @Composable () -> Unit = {},
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .height(36.dp)
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource()
            ) { onClick(liveBlog.stableId) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Spacer(modifier = Modifier.width(16.dp))

            liveIndicator()

            Text(
                modifier = Modifier.alignByBaseline(),
                text = liveBlog.title,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                color = AthTheme.colors.dark700
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier.alignByBaseline(),
                text = liveBlog.lastActivity.asString(),
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                color = AthTheme.colors.dark500
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        divider()
    }
}

@Composable
private fun LiveIndicator() {
    Text(
        modifier = Modifier.padding(end = 16.dp),
        text = "• ${stringResource(id = R.string.feed_live)}",
        style = AthTextStyle.Calibre.Utility.Medium.Small,
        color = AthTheme.colors.red
    )
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .background(color = AthTheme.colors.dark400)
            .height(12.dp)
            .width(1.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun LiveBlogItemUi_Preview() {
    LiveBlogItem(
        liveBlog = LiveBlogCarouselItem(
            id = "id",
            title = "My Live Blog Title",
            lastActivity = StringWithParams(R.string.time_min, 5),
            analyticsPayload = LiveBlogAnalyticsPayload()
        ),
        onClick = {}
    )
}

@Preview(showBackground = true, heightDp = 36)
@Composable
fun LiveBlogCarousel_Preview() {
    LiveBlogCarousel(
        liveBlogs = listOf(
            LiveBlogCarouselItem(
                id = "id",
                title = "My Live Blog Title",
                lastActivity = StringWithParams(R.string.time_min, 5),
                analyticsPayload = LiveBlogAnalyticsPayload()
            ),
            LiveBlogCarouselItem(
                id = "id",
                title = "My Second Live Blog Title",
                lastActivity = StringWithParams(R.string.plural_time_now),
                analyticsPayload = LiveBlogAnalyticsPayload()
            )
        ),
        onLiveBlogClick = {}
    )
}