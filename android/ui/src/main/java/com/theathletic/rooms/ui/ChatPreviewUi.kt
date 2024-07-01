package com.theathletic.rooms.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.TransitionalLayout

@Composable
fun ChatPreviewWindow(
    currentMessage: LiveRoomUi.ChatMessage,
    onChatPreviewClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var oldMessage by remember { mutableStateOf<LiveRoomUi.ChatMessage?>(null) }
    var newMessage by remember { mutableStateOf(currentMessage) }

    val transitionPercent = remember(currentMessage.id) {
        // Don't animate first message, indicated by initial setting of newMessage
        Animatable(if (newMessage == currentMessage) 1f else 0f)
    }

    if (currentMessage.id != newMessage.id) {
        oldMessage = newMessage
        newMessage = currentMessage
    }

    LaunchedEffect(newMessage) {
        transitionPercent.animateTo(
            1.0f,
            animationSpec = tween(durationMillis = 600)
        )
    }

    Box(
        modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(AthTheme.colors.dark200, RoundedCornerShape(54.dp))
            .clickable(onClick = onChatPreviewClicked)
    ) {
        TransitionalLayout(
            transitionPercent = transitionPercent.value,
        ) {
            oldMessage?.let { ChatMessage(it) }
            ChatMessage(newMessage)
        }

        Image(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AthTheme.colors.dark500),
            modifier = Modifier
                .padding(end = 8.dp)
                .size(28.dp)
                .align(Alignment.CenterEnd),
        )
    }
}

@Composable
private fun ChatMessage(
    message: LiveRoomUi.ChatMessage,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val avatarColor = chatAvatarColor(message.authorId)

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(start = 18.dp)
                .size(28.dp)
                .background(avatarColor, CircleShape),
        ) {
            Text(
                text = message.authorInitials.asString(),
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall.copy(fontSize = 10.sp),
                color = AthTheme.colors.dark800,
            )
        }

        val initializedName = message.authorInitializedName.asString()

        val text = remember(message) {
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(initializedName.removeSuffix("."))
                    append(":  ")
                }
                append(message.content)
            }
        }

        Text(
            text = text,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark800,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 12.dp, end = 36.dp)
        )
    }
}

@Composable
@Preview
private fun Preview() {
    ChatPreviewWindow(
        currentMessage = LiveRoomPreviewData.Messages[0],
        onChatPreviewClicked = {},
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
    )
}