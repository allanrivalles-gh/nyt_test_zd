package com.theathletic.rooms.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun ChatMessageList(
    messages: List<LiveRoomUi.ChatMessage>,
    interactor: LiveRoomUi.Interactor,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 10.dp),
        verticalArrangement = spacedBy(2.dp),
        reverseLayout = true,
        modifier = modifier
    ) {
        items(messages) { message ->
            ChatMessage(
                message = message,
                onClick = interactor::onMessageClicked,
                onLongClick = interactor::onMessageLongClicked,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatMessage(
    message: LiveRoomUi.ChatMessage,
    onClick: (messageId: String) -> Unit,
    onLongClick: (messageId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val avatarColor = chatAvatarColor(message.authorId)

    val haptic = LocalHapticFeedback.current

    val shouldHighlight = message.authorIsStaff || message.authorIsHost || message.authorIsModerator

    Row(
        horizontalArrangement = spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(if (shouldHighlight) AthTheme.colors.dark200 else Color.Transparent)
            .combinedClickable(
                onClick = { onClick(message.id) },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick(message.id)
                },
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        AvatarWithInitials(
            initials = message.authorInitials.asString(),
            imageUrl = message.authorAvatarUrl,
            isModerator = message.authorIsModerator,
            color = avatarColor,
        )
        Column {
            Row {
                Text(
                    text = when {
                        message.authorIsModerator -> stringResource(R.string.rooms_moderator_name)
                        else -> message.authorInitializedName.asString()
                    },
                    color = AthTheme.colors.dark800,
                    style = AthTextStyle.Calibre.Utility.Medium.Small,
                )
                MessageDecorations(message = message)
            }
            Text(
                text = message.content,
                color = AthTheme.colors.dark700,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun RowScope.MessageDecorations(
    message: LiveRoomUi.ChatMessage,
) {
    if (message.showAsLocked) {
        ResourceIcon(
            resourceId = R.drawable.ic_locked,
            tint = AthTheme.colors.red,
            modifier = Modifier
                .padding(start = 6.dp)
                .size(10.dp)
                .align(Alignment.CenterVertically)
        )
    }

    if (!message.authorIsModerator && (message.authorIsHost || message.authorIsStaff)) {
        Text(
            text = when {
                message.authorIsHost -> stringResource(R.string.rooms_host).uppercase()
                else -> stringResource(R.string.rooms_staff).uppercase()
            },
            style = AthTextStyle.LiveRoom.ChatBadge,
            color = AthTheme.colors.dark800,
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
                .background(AthTheme.colors.dark100, RoundedCornerShape(90.dp))
                .padding(horizontal = 8.dp, vertical = 1.dp)
        )
    }
}

@Composable
private fun AvatarWithInitials(
    initials: String,
    imageUrl: String?,
    isModerator: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(20.dp)
            .background(
                color = if (isModerator) AthTheme.colors.dark300 else color,
                shape = CircleShape
            )
    ) {
        if (isModerator) {
            ResourceIcon(
                resourceId = R.drawable.ic_athletic_a_logo,
                tint = AthTheme.colors.dark800,
                modifier = Modifier.size(10.dp).align(Alignment.Center),
            )
        } else {
            Text(
                text = initials,
                color = AthColor.Gray800,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Visible,
            )
        }
        if (imageUrl?.isEmpty() == false && !isModerator) {
            Image(
                painter = rememberImagePainter(
                    data = imageUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                        crossfade(true)
                    }
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF000000)
private fun ChatList_Preview() {
    ChatMessageList(
        messages = LiveRoomPreviewData.Messages,
        interactor = LiveRoomPreviewData.Interactor,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF000000)
private fun ChatMessage_Preview() {
    ChatMessage(
        message = LiveRoomPreviewData.Messages[0],
        onClick = {},
        onLongClick = {},
    )
}