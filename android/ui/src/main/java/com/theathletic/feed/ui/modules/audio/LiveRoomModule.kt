package com.theathletic.feed.ui.modules.audio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.feed.ui.FeedAnalyticsPayload
import com.theathletic.feed.ui.FeedInteractionWithPayload
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.rooms.ui.LiveRoomBubbleGradient
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.utility.asHexColor
import com.theathletic.ui.widgets.AnimatedDrawableImage
import com.theathletic.ui.widgets.OverlappingRow
import com.theathletic.ui.widgets.RemoteImage
import com.theathletic.ui.widgets.ResourceIcon

data class LiveRoomModule(
    val id: String,
    val title: String,
    val description: String,
    val logos: List<String>,
    val hostImageUrls: List<String>,
    val backgroundTintColor: String?,
    val analyticsPayload: Payload,
    override val impressionPayload: ImpressionPayload? = null,
) : FeedModuleV2 {

    override val moduleId: String
        get() = "LiveRoomModule-$id"

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        Column(
            modifier = Modifier.clickable {
                interactor.send(
                    Interaction.LiveRoomClick(id = id, analyticsPayload = analyticsPayload)
                )
            },
        ) {
            Header(
                logos = logos,
                backgroundTintColor = backgroundTintColor,
            )
            Metadata(
                title = title,
                description = description,
                hostImageUrls = hostImageUrls,
            )
        }
    }

    interface Interaction {
        data class LiveRoomClick(
            val id: String,
            override val analyticsPayload: Payload,
        ) : FeedInteractionWithPayload<Payload>
    }

    data class Payload(
        val moduleIndex: Int = -1,
    ) : FeedAnalyticsPayload
}

@Composable
private fun Header(
    logos: List<String>,
    backgroundTintColor: String?,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundTintColor?.asHexColor ?: AthColor.Gray300)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        AthColor.Gray100.copy(alpha = .60f),
                    )
                )
            )
            .height(188.dp)
            .clipToBounds(),
    ) {
        Image(
            painter = painterResource(R.drawable.live_room_rings),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .scale(1.3f)
                .alpha(0.5f),
        )
        ImageRow(logos = logos)
        LiveBadge(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 12.dp, start = 16.dp)
        )
    }
}

@Composable
private fun BoxScope.ImageRow(logos: List<String>) {
    Row(
        horizontalArrangement = spacedBy(18.dp),
        modifier = Modifier.align(Alignment.Center)
    ) {
        when (logos.size) {
            0 -> ResourceIcon(
                resourceId = R.drawable.ic_athletic_a_logo,
                modifier = Modifier.size(78.dp),
            )
            1 -> RemoteImage(
                url = logos.first(),
                modifier = Modifier.size(78.dp),
            )
            else -> logos.take(2).forEach {
                RemoteImage(
                    url = it,
                    modifier = Modifier.size(70.dp),
                )
            }
        }
    }
}

@Composable
fun LiveBadge(
    modifier: Modifier
) {
    Text(
        text = stringResource(id = R.string.feed_live),
        color = AthColor.Gray800,
        style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
        modifier = modifier
            .background(AthTheme.colors.red, RoundedCornerShape(2.dp))
            .padding(vertical = 2.dp, horizontal = 7.dp)
    )
}

@Composable
private fun Metadata(
    title: String,
    description: String,
    hostImageUrls: List<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = AthTheme.colors.dark700,
            style = AthTextStyle.Calibre.Headline.Medium.Small,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 12.dp)
                .wrapContentHeight(),
        ) {
            Facepile(hostImageUrls = hostImageUrls.filterNot { it.isEmpty() }.take(3))
            Text(
                text = description,
                color = AthTheme.colors.dark700,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
private fun Facepile(hostImageUrls: List<String>) {
    OverlappingRow(overlap = 10.dp, leftOnTop = false) {
        AudioIndicator()
        hostImageUrls.forEach { url ->
            RemoteImage(
                url = url,
                circular = true,
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = AthTheme.colors.dark200,
                        shape = CircleShape,
                    )
                    .border(
                        width = 2.dp,
                        color = AthTheme.colors.dark200,
                        shape = CircleShape
                    )
                    .padding(1.dp)
            )
        }
    }
}

@Composable
private fun AudioIndicator() {
    AnimatedDrawableImage(
        resourceId = R.drawable.anim_mini_player_visualizer,
        tint = AthTheme.colors.dark800,
        modifier = Modifier
            .size(24.dp)
            .border(
                width = 2.dp,
                brush = LiveRoomBubbleGradient,
                shape = CircleShape
            ),
    )
}

@Preview
@Composable
fun LiveRoomPreview() {
    LiveRoomModule(
        id = "1",
        title = "Reacting to the Celtics horrible loss to the Bulls",
        description = "Jay King & Sam Packer + 101 others",
        logos = emptyList(),
        hostImageUrls = listOf("1", "2"),
        backgroundTintColor = null,
        analyticsPayload = LiveRoomModule.Payload(),
    ).Render()
}

@Preview
@Composable
fun LiveRoomPreview_Tint() {
    LiveRoomModule(
        id = "1",
        title = "Reacting to the Celtics horrible loss to the Bulls",
        description = "Jay King & Sam Packer + 101 others",
        logos = emptyList(),
        hostImageUrls = listOf("1", "2"),
        backgroundTintColor = "497AB8",
        analyticsPayload = LiveRoomModule.Payload(),
    ).Render()
}

@Preview
@Composable
fun LiveRoomPreview_Light() {
    AthleticTheme(lightMode = true) {
        LiveRoomModule(
            id = "1",
            title = "Reacting to the Celtics horrible loss to the Bulls",
            description = "Jay King & Sam Packer + 101 others",
            logos = emptyList(),
            hostImageUrls = listOf("1", "2"),
            backgroundTintColor = null,
            analyticsPayload = LiveRoomModule.Payload(),
        ).Render()
    }
}