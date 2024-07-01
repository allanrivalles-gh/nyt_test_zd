package com.theathletic.rooms.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.AnimatedDrawableImage
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun LiveRoomMiniPlayer(
    liveRoomId: String,
    title: String,
    subtitle: String,
    onMiniPlayerClick: (String) -> Unit,
    onCloseClick: (String) -> Unit,
) {
    Column {
        MiniPlayerMain(
            liveRoomId = liveRoomId,
            title = title,
            subtitle = subtitle,
            onMiniPlayerClick = onMiniPlayerClick,
            onCloseClick = onCloseClick,
        )
        Divider(color = AthTheme.colors.dark200)
    }
}

@Composable
private fun MiniPlayerMain(
    liveRoomId: String,
    title: String,
    subtitle: String,
    onMiniPlayerClick: (String) -> Unit,
    onCloseClick: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(AthTheme.colors.dark200)
            .clickable { onMiniPlayerClick(liveRoomId) }
            .padding(horizontal = 16.dp)
    ) {
        AnimatedDrawableImage(
            resourceId = R.drawable.anim_mini_player_visualizer,
            tint = AthTheme.colors.dark800,
            modifier = Modifier
                .size(28.dp)
                .border(
                    width = 2.dp,
                    brush = LiveRoomBubbleGradient,
                    shape = CircleShape
                ),
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = title,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                color = AthTheme.colors.dark700,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                color = AthTheme.colors.dark500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        ResourceIcon(
            resourceId = R.drawable.ic_close,
            modifier = Modifier
                .size(32.dp)
                .clickable(onClick = { onCloseClick(liveRoomId) })
                .padding(5.dp),
        )
    }
}

@Preview
@Composable
private fun LiveRoomMiniPlayer_Preview() {
    LiveRoomMiniPlayer(
        liveRoomId = "1",
        title = "Live Room Mini Player",
        subtitle = "Subtitle goes here",
        onMiniPlayerClick = {},
        onCloseClick = {},
    )
}

@Preview
@Composable
private fun LiveRoomMiniPlayer_LightPreview() {
    AthleticTheme(lightMode = true) {
        LiveRoomMiniPlayer(
            liveRoomId = "1",
            title = "Live Room Mini Player",
            subtitle = "Subtitle goes here",
            onMiniPlayerClick = {},
            onCloseClick = {},
        )
    }
}