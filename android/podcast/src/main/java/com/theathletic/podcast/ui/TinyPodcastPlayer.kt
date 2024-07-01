package com.theathletic.podcast.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.podcast.R
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.widgets.ResourceIcon

data class TinyPodcastPlayerUiModel(
    val duration: String,
    val progress: Float,
    val playbackState: PlaybackState,
    val downloadState: DownloadState
)

@Composable
fun TinyPodcastPlayer(
    model: TinyPodcastPlayerUiModel,
    modifier: Modifier = Modifier,
    itemInteractor: PodcastEpisodeInteractor
) {
    Row(
        modifier = modifier
            .background(color = AthTheme.colors.dark200)
            .height(32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayPauseButton(
                playbackState = model.playbackState,
                itemInteractor = itemInteractor
            )
            PodcastProgressIndicator(model.progress)
            ElapsedTimeDisplay(model)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (model.downloadState) {
                DownloadState.NOT_DOWNLOADED -> {}
                DownloadState.DOWNLOADING -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(16.dp)
                            .height(16.dp),
                        color = AthTheme.colors.dark800
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                DownloadState.DOWNLOADED -> {
                    ResourceIcon(
                        resourceId = R.drawable.ic_feed_podcast_downloaded,
                        tint = AthTheme.colors.dark800
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            ResourceIcon(
                resourceId = R.drawable.ic_three_dot,
                tint = AthTheme.colors.dark800,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { itemInteractor.onMenuClick() }
            )
        }
    }
}

@Composable
fun PodcastProgressIndicator(progress: Float) {
    LinearProgressIndicator(
        modifier = Modifier
            .width(120.dp)
            .height(4.dp),
        backgroundColor = AthTheme.colors.dark300,
        color = AthTheme.colors.dark800,
        progress = progress
    )
}

@Composable
fun PlayPauseButton(
    playbackState: PlaybackState,
    itemInteractor: PodcastEpisodeInteractor
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                color = AthTheme.colors.dark300,
                shape = RoundedCornerShape(size = 100.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = Color.Gray,
                    radius = 14.dp,
                    bounded = true
                )
            ) {
                itemInteractor.onPlayControlClick()
            }
    ) {
        when (playbackState) {
            is PlaybackState.Playing -> {
                ResourceIcon(
                    resourceId = R.drawable.ic_pause_2,
                    tint = AthTheme.colors.dark700,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }
            is PlaybackState.Completed,
            is PlaybackState.None -> {
                ResourceIcon(
                    resourceId = R.drawable.ic_play_2,
                    tint = AthTheme.colors.dark700,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }
            is PlaybackState.Loading -> {
                CircularProgressIndicator(
                    color = AthTheme.colors.dark700,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun ElapsedTimeDisplay(model: TinyPodcastPlayerUiModel) {
    if (model.playbackState !is PlaybackState.Completed) {
        Text(
            text = model.duration,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark700,
        )
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ResourceIcon(
                resourceId = R.drawable.ic_check,
                modifier = Modifier.height(10.dp),
                tint = AthTheme.colors.dark500
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.podcast_played),
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                color = AthTheme.colors.dark500
            )
        }
    }
}

private data class TinyPodcastPlayerPreviewParams(
    val duration: String = "1h 45m",
    val progress: Float = 0.4f,
    val playbackState: PlaybackState = PlaybackState.Playing,
    val downloadState: DownloadState = DownloadState.NOT_DOWNLOADED
) {
    val model
        get() = TinyPodcastPlayerUiModel(
            duration = duration,
            progress = progress,
            playbackState = playbackState,
            downloadState = downloadState
        )
}

private class TinyPodcastPlayerPreviewParamsProvider :
    PreviewParameterProvider<TinyPodcastPlayerPreviewParams> {
    override val values: Sequence<TinyPodcastPlayerPreviewParams> = sequenceOf(
        TinyPodcastPlayerPreviewParams(
            playbackState = PlaybackState.None,
            downloadState = DownloadState.DOWNLOADED
        ),
        TinyPodcastPlayerPreviewParams(
            playbackState = PlaybackState.Playing,
            downloadState = DownloadState.NOT_DOWNLOADED
        ),
        TinyPodcastPlayerPreviewParams(
            progress = 1f,
            playbackState = PlaybackState.Completed,
            downloadState = DownloadState.DOWNLOADED
        )
    )
}

@Preview
@Composable
private fun TinyPodcastPlayerPreview(
    @PreviewParameter(TinyPodcastPlayerPreviewParamsProvider::class)
    params: TinyPodcastPlayerPreviewParams
) {
    return AthleticTheme(false) {
        val model = params.model
        TinyPodcastPlayer(
            model = model,
            itemInteractor = PodcastEpisodeInteractor(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}