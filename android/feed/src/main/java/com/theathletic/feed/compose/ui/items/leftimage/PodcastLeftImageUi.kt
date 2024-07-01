package com.theathletic.feed.compose.ui.items.leftimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.theathletic.feed.R
import com.theathletic.feed.compose.ui.reusables.ArticleTitle
import com.theathletic.podcast.ui.DownloadState
import com.theathletic.podcast.ui.PlaybackState
import com.theathletic.podcast.ui.PodcastEpisodeInteractor
import com.theathletic.podcast.ui.TinyPodcastPlayer
import com.theathletic.podcast.ui.TinyPodcastPlayerUiModel
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.widgets.RemoteImageAsync

data class PodcastLeftImageUiModel(
    val imageUrl: String,
    val title: String,
    val podcastPlayerModel: TinyPodcastPlayerUiModel
)

@Composable
fun PodcastLeftImage(
    uiModel: PodcastLeftImageUiModel,
    itemInteractor: PodcastEpisodeInteractor
) {
    LeftImage(
        image = { modifier -> CompositeImage(uiModel, modifier) },
        header = { Title(uiModel) },
        footer = { modifier -> Footer(uiModel, modifier, itemInteractor) }
    )
}

@Composable
private fun Title(uiModel: PodcastLeftImageUiModel) {
    ArticleTitle(
        text = uiModel.title,
        style = AthTextStyle.TiemposHeadline.Regular.ExtraExtraSmall,
        isRead = false
    )
}

@Composable
private fun CompositeImage(uiModel: PodcastLeftImageUiModel, modifier: Modifier) {
    Box(modifier) {
        val imageModifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .align(Alignment.Center)
        Image(
            painter = painterResource(R.drawable.img_feed_left_image_item_podcast_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = imageModifier
        )
        RemoteImageAsync(
            alignment = Alignment.Center,
            url = uiModel.imageUrl,
            modifier = imageModifier
        )
    }
}

@Composable
private fun Footer(
    uiModel: PodcastLeftImageUiModel,
    modifier: Modifier,
    itemInteractor: PodcastEpisodeInteractor
) {
    TinyPodcastPlayer(
        model = uiModel.podcastPlayerModel,
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth(),
        itemInteractor = itemInteractor
    )
}

private data class PodcastLeftImagePreviewParams(
    val lightMode: Boolean = false,
    val playbackState: PlaybackState = PlaybackState.None,
    val downloadState: DownloadState = DownloadState.NOT_DOWNLOADED
) {
    fun getModel() = PodcastLeftImageUiModel(
        imageUrl = "",
        title = "Top 10 mock draft with The Athletic NFL Staff",
        podcastPlayerModel = TinyPodcastPlayerUiModel(
            duration = "24m",
            progress = 0.99f,
            playbackState = playbackState,
            downloadState = downloadState
        )
    )
}

private class PodcastLeftImagePreviewParamProvider :
    PreviewParameterProvider<PodcastLeftImagePreviewParams> {
    override val values: Sequence<PodcastLeftImagePreviewParams> = sequenceOf(
        PodcastLeftImagePreviewParams(),
        PodcastLeftImagePreviewParams(lightMode = true),
        PodcastLeftImagePreviewParams(playbackState = PlaybackState.Playing),
        PodcastLeftImagePreviewParams(downloadState = DownloadState.DOWNLOADED)
    )
}

@Preview
@Composable
private fun PodcastLeftImagePreview(
    @PreviewParameter(PodcastLeftImagePreviewParamProvider::class)
    params: PodcastLeftImagePreviewParams
) {
    return AthleticTheme(lightMode = params.lightMode) {
        PodcastLeftImage(params.getModel(), PodcastEpisodeInteractor())
    }
}