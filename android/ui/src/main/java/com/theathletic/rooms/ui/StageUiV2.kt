package com.theathletic.rooms.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.RemoteImage
import com.theathletic.ui.widgets.ResourceIcon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun LiveRoomStageV2(
    speakers: List<LiveRoomUi.Speaker>,
    onUserClick: (String) -> Unit,
    onUserLongClick: (String) -> Unit,
    volumeProvider: Flow<Map<String, Int>>,
) {
    val visibleKeyboard = LocalWindowInsets.current.ime.isVisible

    val chunkedSpeakers = remember(speakers, visibleKeyboard) {
        speakers.chunked(if (visibleKeyboard) 3 else 6)
    }
    val pagerState = rememberPagerState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            .animateContentSize()
    ) {
        HorizontalPager(
            state = pagerState,
            count = chunkedSpeakers.size,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            SpeakerPage(
                users = chunkedSpeakers[page],
                onSpeakerClick = onUserClick,
                onSpeakerLongClick = onUserLongClick,
                volumeProvider = volumeProvider,
                showExtraSpace = page > 0 && !visibleKeyboard,
            )
        }

        if (chunkedSpeakers.size > 1) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = AthTheme.colors.dark800,
                inactiveColor = AthTheme.colors.dark300,
                indicatorWidth = 5.dp,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            )
        }
    }
}

@Composable
private fun SpeakerPage(
    users: List<LiveRoomUi.Speaker>,
    onSpeakerClick: (String) -> Unit,
    onSpeakerLongClick: (String) -> Unit,
    volumeProvider: Flow<Map<String, Int>>,
    showExtraSpace: Boolean,
) {
    val chunked = users.chunked(3)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SpeakerRow(
            users = chunked[0],
            onSpeakerClick = onSpeakerClick,
            onSpeakerLongClick = onSpeakerLongClick,
            volumeProvider = volumeProvider,
        )

        if (chunked.size > 1) {
            SpeakerRow(
                users = chunked[1],
                onSpeakerClick = onSpeakerClick,
                onSpeakerLongClick = onSpeakerLongClick,
                volumeProvider = volumeProvider,
            )
        } else if (showExtraSpace) {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
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
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
    ) {
        users.forEach { speaker ->
            Box(
                modifier = Modifier
                    .wrapContentHeight()
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
                    imageUrl = speaker.imageUrl,
                    isMuted = speaker.isMuted,
                    isVerified = speaker.isVerified,
                    volume = volume.getOrElse(speaker.id) { 0 } / 255f,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
private fun SpeakerOnStage(
    initials: String,
    name: String,
    imageUrl: String?,
    isMuted: Boolean,
    isVerified: Boolean,
    volume: Float,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(90.dp)
    ) {
        Box(modifier = Modifier.size(64.dp)) {
            SpeakingIndicator(
                currentVolume = volume,
                size = 64.dp,
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
                RemoteImage(
                    url = imageUrl,
                    circular = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                )
            }

            if (isMuted) {
                ResourceIcon(
                    resourceId = R.drawable.ic_live_audio_mic_off,
                    tint = AthColor.Gray800,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .background(AthTheme.colors.red, shape = CircleShape)
                        .padding(5.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 6.dp)
        ) {
            Text(
                text = name,
                color = AthTheme.colors.dark700,
                textAlign = TextAlign.Center,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            if (isVerified) {
                ResourceIcon(
                    resourceId = R.drawable.ic_verified_check,
                    tint = AthTheme.colors.dark700,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(12.dp)
                )
            }
        }
    }
}

@Preview(backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun LiveRoomStageV2_Preview() {
    LiveRoomStageV2(
        speakers = LiveRoomPreviewData.Speakers,
        onUserClick = {},
        onUserLongClick = {},
        volumeProvider = emptyFlow(),
    )
}

@Preview(backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun LiveRoomStageV2Populated_Preview() {
    LiveRoomStageV2(
        speakers = LiveRoomPreviewData.Speakers + LiveRoomPreviewData.Speakers.take(2),
        onUserClick = {},
        onUserLongClick = {},
        volumeProvider = emptyFlow(),
    )
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun LiveRoomStageV2_LightPreview() {
    AthleticTheme(lightMode = true) {
        LiveRoomStageV2(
            speakers = LiveRoomPreviewData.Speakers,
            onUserClick = {},
            onUserLongClick = {},
            volumeProvider = emptyFlow(),
        )
    }
}

@Preview(backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun SpeakerOnStage_Preview() {
    SpeakerOnStage(
        name = "Donald Duck",
        initials = "DD",
        volume = 0.0f,
        imageUrl = null,
        isMuted = true,
        isVerified = true,
    )
}