package com.theathletic.feed.compose.ui.components.podcast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.header
import com.theathletic.feed.compose.ui.items.LayoutHeader
import com.theathletic.feed.compose.ui.layoutUiModel
import com.theathletic.feed.compose.ui.reusables.ContentDivider
import com.theathletic.podcast.ui.DownloadState
import com.theathletic.podcast.ui.PlaybackState
import com.theathletic.podcast.ui.PodcastEpisodeInteractor
import com.theathletic.podcast.ui.PodcastEpisodeItem
import com.theathletic.podcast.ui.PodcastEpisodeUiModel
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

internal data class PodcastLayoutUiModel(private val layout: LayoutUiModel) : LayoutUiModel by layout {
    override val items: List<FeedPodcastEpisodeUiModel> = layout.items.mapNotNull { it as? FeedPodcastEpisodeUiModel }
}

@Composable
internal fun PodcastLayout(
    uiModel: PodcastLayoutUiModel
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        LayoutHeader(uiModel = uiModel.header)
        ContentList(items = uiModel.items)
        Footer()
    }
}

@Composable
private fun ContentList(
    items: List<PodcastEpisodeUiModel>
) {
    Column {
        items.forEach { uiModel ->
            PodcastEpisodeItem(
                uiModel = uiModel,
                modifier = Modifier.padding(vertical = 24.dp),
                itemInteractor = PodcastEpisodeInteractor()
            )
            ContentDivider()
        }
    }
}

@Composable
private fun Footer() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.podcast_feed_see_all),
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.dark400,
            modifier = Modifier.padding(vertical = 20.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = stringResource(id = R.string.podcast_feed_see_all),
            colorFilter = ColorFilter.tint(AthTheme.colors.dark400),
            modifier = Modifier.height(12.dp)
        )
    }
}

@DayNightPreview
@Composable
private fun Preview(@PreviewParameter(PreviewProvider::class) uiModel: PodcastLayoutUiModel) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        PodcastLayout(uiModel = uiModel)
    }
}

private class PreviewProvider : PreviewParameterProvider<PodcastLayoutUiModel> {
    private val layout = layoutUiModel(
        id = "layoutId",
        title = "My Podcasts",
        items = previewItems.toList()
    )
    override val values: Sequence<PodcastLayoutUiModel>
        get() = sequenceOf(PodcastLayoutUiModel(layout))
}

private fun podcastEpisodeFixture(
    playbackState: PlaybackState,
    downloadState: DownloadState
) = FeedPodcastEpisodeUiModel(
    id = "podcastId",
    podcastId = "002",
    date = "Today",
    title = "Week 1 Reaction: Cowboys Newton & Rodgers, Lions",
    description = "In the final episode of A King's Reign, Sam Amick and Jovan Buha discuss what they think of the extra text added to ensure this view works well with it.",
    duration = "1h24m",
    progress = 0.66f,
    imageUrl = "",
    permalink = "",
    playbackState = playbackState,
    downloadState = downloadState,
    analyticsData = analyticsPreviewData()
)

private val previewItems = sequenceOf(
    podcastEpisodeFixture(
        playbackState = PlaybackState.None,
        downloadState = DownloadState.NOT_DOWNLOADED
    ),
    podcastEpisodeFixture(
        playbackState = PlaybackState.Playing,
        downloadState = DownloadState.DOWNLOADING
    ),
    podcastEpisodeFixture(
        playbackState = PlaybackState.Loading,
        downloadState = DownloadState.DOWNLOADED
    )
)