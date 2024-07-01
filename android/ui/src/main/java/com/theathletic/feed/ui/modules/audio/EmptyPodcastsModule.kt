package com.theathletic.feed.ui.modules.audio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon

object EmptyPodcastsModule : FeedModuleV2 {

    @Composable
    override fun Render() {
        EmptyPodcastsModule()
    }

    override val moduleId: String
        get() = "PodcastsModule-empty"

    sealed class Interaction : FeedInteraction {
        object DiscoverShowsClick : Interaction()
    }
}

@Composable
private fun EmptyPodcastsModule() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(AthTheme.colors.dark300, AthTheme.colors.dark200)
                )
            )
            .padding(top = 40.dp, bottom = 24.dp)
    ) {
        ResourceIcon(
            resourceId = R.drawable.ic_listen,
            tint = AthTheme.colors.dark800,
            modifier = Modifier.size(62.dp),
        )

        Text(
            text = stringResource(R.string.podcast_following_empty_title),
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Headline.SemiBold.Small,
            modifier = Modifier.padding(top = 32.dp),
        )

        Text(
            text = stringResource(R.string.podcast_following_empty_description),
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, start = 24.dp, end = 24.dp),
        )

        DiscoverButton()
    }
}

@Composable
private fun DiscoverButton() {
    val interactor = LocalFeedInteractor.current

    TextButton(
        onClick = { interactor.send(EmptyPodcastsModule.Interaction.DiscoverShowsClick) },
        shape = RoundedCornerShape(2.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AthTheme.colors.dark800,
            contentColor = AthTheme.colors.dark200,
            disabledBackgroundColor = AthTheme.colors.dark800,
            disabledContentColor = AthTheme.colors.dark500,
        ),
        contentPadding = PaddingValues(vertical = 13.dp, horizontal = 24.dp),
        modifier = Modifier.padding(top = 32.dp),
    ) {
        Text(
            text = stringResource(R.string.podcast_following_empty_cta),
            color = AthTheme.colors.dark200,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun EmptyPodcastsModulePreview() {
    EmptyPodcastsModule.Render()
}

@Preview
@Composable
private fun EmptyPodcastsModulePreview_Light() {
    AthleticTheme(lightMode = true) {
        EmptyPodcastsModule.Render()
    }
}