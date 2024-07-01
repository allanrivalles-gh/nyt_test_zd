package com.theathletic.feed.ui.modules.audio

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.datetime.Datetime
import com.theathletic.feed.ui.FeedAnalyticsPayload
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.datetime.timeAgoShortFormat
import com.theathletic.ui.widgets.RemoteImage
import com.theathletic.ui.widgets.ResourceIcon
import java.util.concurrent.TimeUnit
import kotlin.math.max

data class LatestPodcastEpisodesModule(
    val id: String,
    val episodes: List<Episode>
) : FeedModuleV2 {

    override val moduleId: String
        get() = "PodcastsEpisodesModule-$id"

    data class Episode(
        val id: String,
        val imageUrl: String,
        val publishedDate: Datetime,
        val title: String,
        val progressMs: Long,
        val durationMs: Long,
        val playbackState: PlaybackState,
        val downloadState: DownloadState,
        val isFinished: Boolean,
        val payload: Payload = Payload(),
    ) {
        enum class PlaybackState {
            PLAYING,
            LOADING,
            NONE,
        }

        enum class DownloadState {
            NOT_DOWNLOADED,
            DOWNLOADING,
            DOWNLOADED,
        }

        data class Payload(
            val moduleIndex: Int = -1,
            val vIndex: Int = -1,
        ) : FeedAnalyticsPayload
    }

    @Composable
    override fun Render() {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AthTheme.colors.dark200)
                .padding(top = 24.dp)
        ) {
            if (episodes.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.feed_podcast_latest_episodes),
                    color = AthTheme.colors.dark800,
                    style = AthTextStyle.Slab.Bold.Small,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                EpisodeList(episodes)
            }

            MyDownloadsButton()
        }
    }

    sealed class Interaction : FeedInteraction {
        data class EpisodeClick(val id: String, val payload: Episode.Payload) : Interaction()
        data class EpisodeLongClick(val id: String) : Interaction()
        data class EpisodeMenuClick(val id: String) : Interaction()
        data class ControlClick(val id: String) : Interaction()
        object MyDownloadsClick : Interaction()
    }
}

@Composable
private fun EpisodeList(
    episodes: List<LatestPodcastEpisodesModule.Episode>
) {
    episodes.forEach { episode ->
        PodcastEpisode(episode)
        Divider(
            color = AthTheme.colors.dark300,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PodcastEpisode(
    episode: LatestPodcastEpisodesModule.Episode
) {
    val interactor = LocalFeedInteractor.current
    val hapticFeedback = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    interactor.send(
                        LatestPodcastEpisodesModule.Interaction.EpisodeClick(
                            id = episode.id,
                            payload = episode.payload
                        )
                    )
                },
                onLongClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    interactor.send(
                        LatestPodcastEpisodesModule.Interaction.EpisodeLongClick(id = episode.id)
                    )
                }
            )
            .padding(vertical = 24.dp, horizontal = 16.dp)
            .height(IntrinsicSize.Min)
    ) {
        RemoteImage(
            url = episode.imageUrl,
            modifier = Modifier
                .size(86.dp)
                .background(AthTheme.colors.dark300)
        )
        Spacer(modifier = Modifier.width(12.dp))
        PodcastEpisodeDetails(episode)
    }
}

@Composable
private fun PodcastEpisodeDetails(
    episode: LatestPodcastEpisodesModule.Episode
) {
    Column {
        Text(
            text = episode.publishedDate.timeAgoShortFormat(includeTodayTag = true),
            color = AthTheme.colors.dark600,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall
        )
        Text(
            text = episode.title,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 4.dp)
                .weight(1.0f),
        )
        ControlsRow(episode = episode)
    }
}

@Composable
private fun ControlsRow(
    episode: LatestPodcastEpisodesModule.Episode,
) {
    val interactor = LocalFeedInteractor.current

    val minutesLeft = remember(episode.durationMs, episode.progressMs) {
        TimeUnit.MILLISECONDS.toMinutes(max(episode.durationMs - episode.progressMs, 0L))
    }

    val completed = episode.isFinished && episode.playbackState == LatestPodcastEpisodesModule.Episode.PlaybackState.NONE

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp),
    ) {
        ControlButton(
            episodeId = episode.id,
            playbackState = episode.playbackState,
        )

        Text(
            text = stringResource(
                when {
                    completed -> R.string.podcast_played
                    episode.progressMs > 0L -> R.string.podcast_duration_minutes_left
                    else -> R.string.podcast_duration_minutes
                },
                minutesLeft
            ),
            color = when {
                completed -> AthTheme.colors.dark500
                else -> AthTheme.colors.dark800
            },
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1.0f),
        )

        DownloadStateIndicator(downloadState = episode.downloadState)

        ResourceIcon(
            resourceId = R.drawable.ic_dots,
            tint = AthTheme.colors.dark800,
            modifier = Modifier
                .size(27.dp)
                .clickable {
                    interactor.send(
                        LatestPodcastEpisodesModule.Interaction.EpisodeMenuClick(id = episode.id)
                    )
                }
                .padding(6.dp)
        )
    }
}

@Composable
private fun ControlButton(
    episodeId: String,
    playbackState: LatestPodcastEpisodesModule.Episode.PlaybackState,
) {
    val interactor = LocalFeedInteractor.current

    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                color = AthTheme.colors.dark300,
                shape = CircleShape,
            )
            .clickable {
                interactor.send(
                    LatestPodcastEpisodesModule.Interaction.ControlClick(episodeId)
                )
            }
    ) {
        when (playbackState) {
            LatestPodcastEpisodesModule.Episode.PlaybackState.PLAYING -> {
                ResourceIcon(
                    resourceId = R.drawable.ic_pause_2,
                    tint = AthTheme.colors.dark700,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }
            LatestPodcastEpisodesModule.Episode.PlaybackState.NONE -> {
                ResourceIcon(
                    resourceId = R.drawable.ic_play_2,
                    tint = AthTheme.colors.dark700,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }
            LatestPodcastEpisodesModule.Episode.PlaybackState.LOADING -> {
                CircularProgressIndicator(
                    color = AthTheme.colors.dark700,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                )
            }
        }
    }
}

@Composable
private fun DownloadStateIndicator(
    downloadState: LatestPodcastEpisodesModule.Episode.DownloadState,
) {
    when (downloadState) {
        LatestPodcastEpisodesModule.Episode.DownloadState.DOWNLOADED -> {
            ResourceIcon(
                resourceId = R.drawable.ic_feed_podcast_downloaded,
                tint = AthTheme.colors.dark700,
                modifier = Modifier.padding(end = 8.dp),
            )
        }
        LatestPodcastEpisodesModule.Episode.DownloadState.DOWNLOADING -> {
            CircularProgressIndicator(
                color = AthTheme.colors.dark700,
                strokeWidth = 2.dp,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(14.dp)
            )
        }
        LatestPodcastEpisodesModule.Episode.DownloadState.NOT_DOWNLOADED -> {
            // Show nothing
        }
    }
}

@Composable
private fun MyDownloadsButton() {
    val interactor = LocalFeedInteractor.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .clickable {
                interactor.send(LatestPodcastEpisodesModule.Interaction.MyDownloadsClick)
            }
    ) {
        Text(
            text = stringResource(id = R.string.podcast_feed_my_downloads),
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
            modifier = Modifier
                .padding(start = 24.dp)
                .weight(1f)
        )

        ResourceIcon(
            resourceId = R.drawable.ic_arrow_right,
            modifier = Modifier
                .padding(end = 24.dp)
                .size(24.dp),
            tint = AthTheme.colors.dark800,
        )
    }
}

@Preview
@Composable
private fun LatestPodcastEpisodesModulePreview() {
    LatestPodcastEpisodesModule(
        id = "1",
        episodes = listOf(
            LatestPodcastEpisodesModule.Episode(
                id = "1",
                title = "Podcast Loading",
                imageUrl = "https://cdn.theathletic.com/app/uploads/2019/10/07174314/Legal-The-Athletic-No-Dunks.jpg",
                publishedDate = Datetime(0),
                progressMs = 0,
                durationMs = TimeUnit.MINUTES.toMillis(30),
                playbackState = LatestPodcastEpisodesModule.Episode.PlaybackState.LOADING,
                downloadState = LatestPodcastEpisodesModule.Episode.DownloadState.NOT_DOWNLOADED,
                isFinished = false,
            ),
            LatestPodcastEpisodesModule.Episode(
                id = "2",
                title = "Podcast Playing",
                imageUrl = "https://cdn.theathletic.com/app/uploads/2019/10/07174314/Legal-The-Athletic-No-Dunks.jpg",
                publishedDate = Datetime(1649793656000),
                progressMs = TimeUnit.MINUTES.toMillis(17),
                durationMs = TimeUnit.MINUTES.toMillis(33),
                playbackState = LatestPodcastEpisodesModule.Episode.PlaybackState.PLAYING,
                downloadState = LatestPodcastEpisodesModule.Episode.DownloadState.DOWNLOADING,
                isFinished = false,
            ),
            LatestPodcastEpisodesModule.Episode(
                id = "3",
                title = "Podcast Completed",
                imageUrl = "https://cdn.theathletic.com/app/uploads/2019/10/07174314/Legal-The-Athletic-No-Dunks.jpg",
                publishedDate = Datetime(System.currentTimeMillis()),
                progressMs = 0,
                durationMs = TimeUnit.MINUTES.toMillis(30),
                playbackState = LatestPodcastEpisodesModule.Episode.PlaybackState.NONE,
                downloadState = LatestPodcastEpisodesModule.Episode.DownloadState.DOWNLOADED,
                isFinished = true,
            ),
        )
    ).Render()
}