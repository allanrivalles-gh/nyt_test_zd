package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.podcast.ui.DownloadState
import com.theathletic.podcast.ui.PlaybackState
import com.theathletic.podcast.ui.PodcastEpisodeInteractor
import com.theathletic.podcast.ui.TinyPodcastPlayer
import com.theathletic.podcast.ui.TinyPodcastPlayerUiModel
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.widgets.RemoteImageAsync

data class PodcastHeroUiModel(
    val imageUrl: String,
    val title: String,
    val description: String? = null,
    val podcastPlayerModel: TinyPodcastPlayerUiModel
)

/**
 * Hero item for a podcast.
 *
 * Note: Created as a Composable without having seen a podcast hero item in the existing
 * app. If you think you've found something that's wrong, you probably have — and you
 * shouldn't hesitate to change it.
 *
 * For other podcast items, the isPlayed status is rendered via the duration string; I've
 * assumed here that hero podcast items get the same treatment. If we want to handle it
 * some other way, go for it.
 *
 * It's also worth noting that the app currently doesn't support bookmarking of podcast
 * episodes.
 */
@Composable
fun PodcastHero(
    uiModel: PodcastHeroUiModel,
    itemInteractor: PodcastEpisodeInteractor
) {
    Box(
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = AthTheme.colors.dark200)
                .fillMaxWidth()
        ) {
            PodcastImageOverlay(uiModel.imageUrl)

            Text(
                color = AthTheme.colors.dark700,
                style = AthTextStyle.TiemposHeadline.Regular.ExtraSmall,
                text = uiModel.title,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 16.dp)
            )

            uiModel.description?.also {
                Text(
                    color = AthTheme.colors.dark500,
                    style = AthTextStyle.TiemposBody.Regular.Small,
                    text = uiModel.description,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 12.dp)
            ) {
                TinyPodcastPlayer(
                    model = uiModel.podcastPlayerModel,
                    itemInteractor = itemInteractor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PodcastImageOverlay(imageUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.img_feed_podcast_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
        )
        RemoteImageAsync(
            alignment = Alignment.Center,
            url = imageUrl,
            modifier = Modifier
                .padding(vertical = 20.dp)
                .height(153.dp)
                .width(153.dp)
                .align(Alignment.Center)
        )
    }
}

private data class PodcastHeroPreviewParams(
    val lightMode: Boolean = false,
    val description: String? = null,
    val progress: Float = 0f,
    val playbackState: PlaybackState =
        PlaybackState.None,
    val downloadState: DownloadState =
        DownloadState.NOT_DOWNLOADED
) {
    fun getModel(): PodcastHeroUiModel {
        return PodcastHeroUiModel(
            imageUrl = "https://cdn.theathletic.com/app/uploads/2023/06/25145437/" +
                "USATSI_19636655-scaled.jpg",
            title = "Top 10 mock draft with The Athletic NFL Staff",
            description = description,
            podcastPlayerModel = TinyPodcastPlayerUiModel(
                duration = "1h 45m",
                progress = progress,
                playbackState = playbackState,
                downloadState,
            )
        )
    }
}

private class PodcastHeroPreviewParamsProvider :
    PreviewParameterProvider<PodcastHeroPreviewParams> {
    override val values: Sequence<PodcastHeroPreviewParams> = sequenceOf(
        PodcastHeroPreviewParams(
            description = "Which QB will the 49ers take at No. 3? Who will land Kyle Pitts? We " +
                "break it all down. 1h 45m",
            progress = 0.7f,
            playbackState = PlaybackState.Playing,
            downloadState = DownloadState.NOT_DOWNLOADED
        ),
        PodcastHeroPreviewParams(
            lightMode = true,
            playbackState = PlaybackState.None,
            downloadState = DownloadState.DOWNLOADED
        )
    )
}

@Preview
@Composable
private fun PodcastHeroPreview(
    @PreviewParameter(PodcastHeroPreviewParamsProvider::class)
    params: PodcastHeroPreviewParams
) {
    return AthleticTheme(lightMode = params.lightMode) {
        val model = params.getModel()
        PodcastHero(model, PodcastEpisodeInteractor())
    }
}