package com.theathletic.rooms.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.asString
import com.theathletic.ui.binding.LinkableTag
import com.theathletic.ui.widgets.ResourceIcon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
internal fun LiveRoomStage(
    roomTitle: String,
    roomDescription: String,
    roomTags: List<LinkableTag>,
    chatEnabled: Boolean,
    totalAudienceSize: Int,
    speakers: List<LiveRoomUi.Speaker>,
    audience: List<LiveRoomUi.AudienceCell>,
    messages: List<LiveRoomUi.ChatMessage>,
    onUserClick: (String) -> Unit,
    onUserLongClick: (String) -> Unit,
    onTagClick: (id: String, deeplink: String) -> Unit,
    onChatPreviewClicked: () -> Unit,
    volumeProvider: Flow<Map<String, Int>>,
) {
    val chunkedSpeakers = remember(speakers) { speakers.chunked(3) }
    val chunkedAudience = remember(audience) { audience.chunked(4) }

    LiveRoomInfoSheet(
        roomTitle = roomTitle,
        roomDescription = roomDescription,
        roomTags = roomTags,
        onTagClick = onTagClick,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 150.dp),
            ) {
                if (chatEnabled) {
                    messages.firstOrNull()?.let { message ->
                        item {
                            ChatPreviewWindow(
                                currentMessage = message,
                                onChatPreviewClicked = onChatPreviewClicked,
                                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                            )
                        }
                    }
                }
                items(chunkedSpeakers) { users ->
                    SpeakerRow(
                        users = users,
                        onSpeakerClick = onUserClick,
                        onSpeakerLongClick = onUserLongClick,
                        volumeProvider = volumeProvider,
                    )
                }
                if (audience.isNotEmpty()) {
                    item { AudienceCountTitle(totalAudienceSize) }
                }
                items(chunkedAudience) { users ->
                    AudienceRow(
                        users = users,
                        onAudienceClick = onUserClick,
                        onAudienceLongClick = onUserLongClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun AudienceCountTitle(audienceCount: Int) {
    Text(
        text = stringResource(R.string.rooms_other_count, audienceCount),
        color = AthTheme.colors.dark500,
        style = AthTextStyle.Calibre.Utility.Regular.Small,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpeakerRow(
    users: List<LiveRoomUi.Speaker>,
    onSpeakerClick: (String) -> Unit,
    onSpeakerLongClick: (String) -> Unit,
    volumeProvider: Flow<Map<String, Int>>,
) {
    val haptic = LocalHapticFeedback.current
    val volume by volumeProvider.collectAsState(initial = emptyMap())

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth(),
    ) {
        users.forEach { speaker ->
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 12.dp)
                    .combinedClickable(
                        onClick = { onSpeakerClick(speaker.id) },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSpeakerLongClick(speaker.id)
                        },
                    )
            ) {
                SpeakerOnStage(
                    initials = speaker.initials.asString(),
                    name = speaker.name.asString(),
                    subtitle = speaker.subtitle.asString(),
                    imageUrl = speaker.imageUrl,
                    isMuted = speaker.isMuted,
                    volume = volume.getOrElse(speaker.id) { 0 } / 255f,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
private fun LazyItemScope.AudienceRow(
    users: List<LiveRoomUi.AudienceCell>,
    onAudienceClick: (String) -> Unit,
    onAudienceLongClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        users.forEach {
            Box(
                modifier = Modifier
                    .fillParentMaxWidth(.25f)
                    .wrapContentHeight()
                    .padding(top = 24.dp)
            ) {
                when (it) {
                    is LiveRoomUi.AudienceCell.User -> UserInAudience(
                        id = it.id,
                        initials = it.initials.asString(),
                        name = it.name.asString(),
                        imageUrl = it.imageUrl,
                        isLocked = it.locked,
                        onAudienceClick = onAudienceClick,
                        onAudienceLongClick = onAudienceLongClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    is LiveRoomUi.AudienceCell.OverflowIndicator -> OverflowCell(
                        overflowCount = it.overflowCount,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeakerOnStage(
    initials: String,
    name: String,
    subtitle: String,
    imageUrl: String?,
    isMuted: Boolean,
    volume: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.wrapContentWidth()) {
        Box(
            modifier = Modifier.size(90.dp)
                .align(Alignment.CenterHorizontally)
        ) {

            SpeakingIndicator(
                currentVolume = volume,
                size = 90.dp,
            )

            Text(
                text = initials,
                color = AthTheme.colors.dark800,
                textAlign = TextAlign.Center,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall.copy(fontSize = 20.sp),
                maxLines = 1,
                modifier = Modifier.align(Alignment.Center),
            )

            if (imageUrl?.isEmpty() == false) {
                Image(
                    painter = rememberImagePainter(
                        data = imageUrl,
                        builder = {
                            transformations(CircleCropTransformation())
                            crossfade(true)
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp),
                )
            }

            if (isMuted) {
                Icon(
                    painter = painterResource(R.drawable.ic_live_audio_mic_off),
                    tint = Color.White,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(26.dp)
                        .background(AthTheme.colors.red, shape = RoundedCornerShape(4.dp))
                        .padding(5.dp)
                )
            }
        }
        Text(
            text = name,
            color = AthTheme.colors.dark700,
            textAlign = TextAlign.Center,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 6.dp)
                .align(Alignment.CenterHorizontally),
        )
        Text(
            text = subtitle,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserInAudience(
    id: String,
    initials: String,
    name: String,
    imageUrl: String?,
    isLocked: Boolean,
    onAudienceClick: (String) -> Unit,
    onAudienceLongClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    Column(
        modifier = modifier
            .width(70.dp)
            .combinedClickable(
                onClick = { onAudienceClick(id) },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAudienceLongClick(id)
                },
            )
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(AthTheme.colors.dark300, shape = CircleShape)
                .padding(3.dp)
                .background(AthTheme.colors.dark200, shape = CircleShape)
        ) {
            Text(
                text = initials,
                color = AthTheme.colors.dark800,
                textAlign = TextAlign.Center,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall.copy(fontSize = 20.sp),
                maxLines = 1,
                modifier = Modifier.align(Alignment.Center),
            )

            if (imageUrl?.isEmpty() == false) {
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

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = name,
                color = AthTheme.colors.dark700,
                textAlign = TextAlign.Center,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (isLocked) {
                ResourceIcon(
                    resourceId = R.drawable.ic_locked,
                    tint = AthTheme.colors.red,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(10.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
private fun OverflowCell(
    overflowCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.size(70.dp),
    ) {
        Text(
            text = stringResource(R.string.rooms_overflow_count, overflowCount),
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall.copy(fontSize = 20.sp),
        )
        Text(
            text = stringResource(R.string.rooms_overflow_name),
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
        )
    }
}

@Preview(backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun LiveRoomStage_Preview() {
    LiveRoomStage(
        roomTitle = LiveRoomPreviewData.RoomTitle,
        roomDescription = LiveRoomPreviewData.RoomDescription,
        roomTags = LiveRoomPreviewData.Tags,
        chatEnabled = true,
        totalAudienceSize = 100,
        speakers = LiveRoomPreviewData.Speakers,
        audience = LiveRoomPreviewData.Audience,
        messages = LiveRoomPreviewData.Messages,
        onUserClick = {},
        onUserLongClick = {},
        onTagClick = { _, _ -> },
        onChatPreviewClicked = {},
        volumeProvider = emptyFlow(),
    )
}

@Preview(backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun SpeakerOnStage_Preview() {
    SpeakerOnStage(
        name = "Donald Duck",
        initials = "DD",
        subtitle = "NBA Insider",
        volume = 0.0f,
        imageUrl = null,
        isMuted = true,
    )
}

@Preview(backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun UserInAudience_Preview() {
    UserInAudience(
        id = "1",
        name = "Donald Duck",
        initials = "DD",
        imageUrl = null,
        isLocked = true,
        onAudienceClick = {},
        onAudienceLongClick = {},
    )
}

@Composable
@Preview
private fun OverflowCell_Preview() {
    OverflowCell(overflowCount = 5)
}