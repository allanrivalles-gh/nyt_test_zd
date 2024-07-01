package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.BoxScorePreviewData.latestNewsArticleOnlyMock
import com.theathletic.boxscore.ui.modules.LatestNewsModule
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.items.imageitem.ArticleRightImage
import com.theathletic.feed.compose.ui.items.imageitem.latestNewsAspect
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.podcast.ui.PodcastEpisodeInteractor
import com.theathletic.podcast.ui.PodcastEpisodeItem
import com.theathletic.podcast.ui.PodcastEpisodeUiModel
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DevicePreviewSmallAndLarge

@Composable
fun LatestNewsroomUi(
    latestNewsUiModel: BoxScoreUiModel.LatestNewsUiModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {

        Text(
            text = latestNewsUiModel.header?.title.orEmpty(),
            color = AthTheme.colors.dark700,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            style = AthTextStyle.Slab.Bold.Small
        )

        latestNewsUiModel.blocks.forEachIndexed { index, uiModel ->
            NewsRoomItem(uiModel)

            if (latestNewsUiModel.blocks.isEmpty().not() &&
                index != latestNewsUiModel.blocks.lastIndex
            ) {
                Divider(
                    color = AthTheme.colors.dark300,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@SuppressWarnings("LongMethod")
@Composable
private fun NewsRoomItem(uiModel: LayoutUiModel.Item) {
    val interactor = LocalFeedInteractor.current

    when (uiModel) {
        is ArticleUiModel -> {
            ArticleRightImage(
                uiModel = uiModel,
                maxLines = 3,
                modifier = Modifier.latestNewsAspect(),
                itemInteractor = ItemInteractor(
                    onClick = {
                        interactor.send(LatestNewsModule.Interaction.LatestNewsArticle(uiModel.permalink, uiModel.id))
                    },
                    onLongClick = {
                        interactor.send(
                            LatestNewsModule.Interaction.ArticleLongClick(
                                uiModel.id,
                                uiModel.isRead,
                                uiModel.isBookmarked,
                                uiModel.permalink
                            )
                        )
                    }
                )
            )
        }
        is PodcastEpisodeUiModel -> {
            PodcastEpisodeItem(
                uiModel = uiModel,
                imageScale = ContentScale.FillBounds,
                imageModifier = Modifier.latestNewsAspect(),
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 20.dp
                ),
                itemInteractor = PodcastEpisodeInteractor(
                    onPlayControlClick = {
                        interactor.send(
                            LatestNewsModule.Interaction.PodcastPlayControl(
                                episodeId = uiModel.id
                            )
                        )
                    },
                    onMenuClick = {
                        interactor.send(
                            LatestNewsModule.Interaction.PodcastOptionsMenu(
                                episodeId = uiModel.id,
                                podcastId = uiModel.podcastId,
                                permalink = uiModel.permalink
                            )
                        )
                    },
                    onClick = {
                        interactor.send(
                            LatestNewsModule.Interaction.PodcastClick(episodeId = uiModel.id)
                        )
                    }
                )
            )
        }
    }
}

@DevicePreviewSmallAndLarge
@Composable
private fun LatestNewsroom_PreviewLargeDevice() {
    LatestNewsroomUi(latestNewsArticleOnlyMock)
}

@DevicePreviewSmallAndLarge
@Composable
private fun LatestNewsroom_PreviewLargeDeviceLight() {
    AthleticTheme(lightMode = true) {
        LatestNewsroomUi(latestNewsArticleOnlyMock)
    }
}