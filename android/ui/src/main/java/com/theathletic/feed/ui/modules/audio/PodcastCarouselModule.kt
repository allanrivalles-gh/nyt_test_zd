package com.theathletic.feed.ui.modules.audio

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedAnalyticsPayload
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.RemoteImage

data class PodcastCarouselModule(
    val id: String,
    @StringRes val title: Int,
    val podcasts: List<Podcast>,
) : FeedModuleV2 {

    override val moduleId: String
        get() = "PodcastsCarouselModule-$id"

    data class Podcast(
        val id: String,
        val title: String,
        val subtitle: String,
        val imageUrl: String,
        val analyticsPayload: Payload = Payload(),
    ) {
        data class Payload(
            val moduleIndex: Int = -1,
            val hIndex: Int = -1,
        ) : FeedAnalyticsPayload
    }

    @Composable
    override fun Render() {
        if (podcasts.isEmpty()) return

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AthTheme.colors.dark200)
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
            Text(
                text = stringResource(title),
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Slab.Bold.Small,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = spacedBy(4.dp),
            ) {
                items(podcasts) { podcast -> PodcastItem(podcast) }
            }
        }
    }

    sealed class Interaction : FeedInteraction {
        data class PodcastClick(val id: String, val payload: Podcast.Payload) : Interaction()
    }
}

@Composable
private fun PodcastItem(
    podcast: PodcastCarouselModule.Podcast,
) {
    val interactor = LocalFeedInteractor.current
    Column(
        modifier = Modifier
            .width(186.dp)
            .clickable {
                interactor.send(
                    PodcastCarouselModule.Interaction.PodcastClick(
                        id = podcast.id,
                        payload = podcast.analyticsPayload
                    )
                )
            }
            .padding(8.dp)
    ) {
        RemoteImage(
            url = podcast.imageUrl,
            modifier = Modifier
                .size(170.dp)
                .background(AthTheme.colors.dark300)
        )
        Text(
            text = podcast.title,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
        )
        if (podcast.subtitle.isNotEmpty()) {
            Text(
                text = podcast.subtitle,
                color = AthTheme.colors.dark400,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun PodcastCarouselModulePreview() {
    PodcastCarouselModule(
        id = "1",
        title = R.string.podcast_feed_recommended,
        podcasts = listOf(
            PodcastCarouselModule.Podcast(
                id = "1",
                title = "Hello World",
                subtitle = "NBA",
                imageUrl = "https://cdn.theathletic.com/app/uploads/2019/10/07174314/Legal-The-Athletic-No-Dunks.jpg",
            ),
        )
    ).Render()
}