package com.theathletic.podcast.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.widgets.RemoteImageAsync

interface PodcastEpisodeUiModel {
    val podcastId: String
    val id: String
    val permalink: String
    val date: String
    val title: String
    val description: String
    val duration: String
    val progress: Float
    val imageUrl: String
    val playbackState: PlaybackState
    val downloadState: DownloadState
}

@Composable
fun PodcastEpisodeItem(
    uiModel: PodcastEpisodeUiModel,
    modifier: Modifier = Modifier,
    imageScale: ContentScale = ContentScale.Crop,
    imageModifier: Modifier = Modifier
        .height(90.dp)
        .width(90.dp),
    itemInteractor: PodcastEpisodeInteractor
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .clickable { itemInteractor.onClick() }
            .then(modifier)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = uiModel.date,
                    style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                    color = AthTheme.colors.dark400
                )
                Text(
                    text = uiModel.title,
                    style = AthTextStyle.Calibre.Utility.Medium.Large,
                    color = AthTheme.colors.dark700,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = uiModel.description,
                    style = AthTextStyle.Calibre.Utility.Regular.Small,
                    color = AthTheme.colors.dark700,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            RemoteImageAsync(url = uiModel.imageUrl, modifier = imageModifier, contentScale = imageScale)
        }
        TinyPodcastPlayer(
            model = TinyPodcastPlayerUiModel(
                duration = uiModel.duration,
                progress = uiModel.progress,
                playbackState = uiModel.playbackState,
                downloadState = uiModel.downloadState
            ),
            itemInteractor = itemInteractor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@DayNightPreview
@Composable
private fun Preview(@PreviewParameter(PodcastEpisodePreviewProvider::class) uiModel: PodcastEpisodeUiModel) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        PodcastEpisodeItem(
            uiModel = uiModel,
            itemInteractor = PodcastEpisodeInteractor()
        )
    }
}

private class PodcastEpisodePreviewProvider : PreviewParameterProvider<PodcastEpisodeUiModel> {
    override val values: Sequence<PodcastEpisodeUiModel>
        get() = podcastEpisodePreviewItems
}

internal data class FixturePodcastEpisodeUiModel(
    override val podcastId: String,
    override val id: String,
    override val permalink: String,
    override val date: String,
    override val title: String,
    override val description: String,
    override val duration: String,
    override val progress: Float,
    override val imageUrl: String,
    override val playbackState: PlaybackState,
    override val downloadState: DownloadState,
) : PodcastEpisodeUiModel

private fun podcastEpisodeFixture(
    playbackState: PlaybackState,
    downloadState: DownloadState
) = FixturePodcastEpisodeUiModel(
    id = "001",
    podcastId = "002",
    date = "Today",
    title = "Week 1 Reaction: Cowboys Newton & Rodgers, Lions",
    description = "In the final episode of A King's Reign, Sam Amick and Jovan Buha discuss what they think of the extra text added to ensure this view works well with it.",
    duration = "1h24m",
    progress = 0.3f,
    imageUrl = "",
    permalink = "",
    playbackState = playbackState,
    downloadState = downloadState
)

private val podcastEpisodePreviewItems = sequenceOf(
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