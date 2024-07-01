package com.theathletic.rooms.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.insets.LocalWindowInsets
import com.theathletic.rooms.ui.modifiers.topScrim
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.animation.slideInOutLeft
import com.theathletic.ui.animation.slideInOutRight
import com.theathletic.ui.binding.LinkableTag
import com.theathletic.ui.widgets.BoxWithBadge
import com.theathletic.ui.widgets.FixedSizeBadge
import com.theathletic.ui.widgets.ResourceIcon
import kotlinx.coroutines.flow.Flow

sealed class LiveRoomTab(@StringRes val titleRes: Int) {
    object Stage : LiveRoomTab(R.string.rooms_stage_tab_title)
    object Chat : LiveRoomTab(R.string.rooms_chat_tab_title)
}

class LiveRoomUi {
    data class Speaker(
        val id: String,
        val initials: ResourceString,
        val name: ResourceString,
        val subtitle: ResourceString,
        val imageUrl: String?,
        val isMuted: Boolean,
        val isVerified: Boolean,
    )

    sealed class AudienceCell {
        data class User(
            val id: String,
            val initials: ResourceString,
            val name: ResourceString,
            val imageUrl: String?,
            val locked: Boolean,
        ) : AudienceCell()

        data class OverflowIndicator(
            val overflowCount: Int,
        ) : AudienceCell()
    }

    data class ChatMessage(
        val id: String,
        val authorId: String,
        val authorAvatarUrl: String?,
        val authorInitials: ResourceString,
        val authorInitializedName: ResourceString,
        val authorIsHost: Boolean,
        val authorIsStaff: Boolean,
        val authorIsModerator: Boolean,
        val showAsLocked: Boolean,
        val content: String,
    )

    data class HostInfo(
        val id: String,
        val name: String,
        val subtitle: String?,
        val imageUrl: String,
    )

    data class TagInfo(
        val id: String,
        val deeplink: String,
        val name: String,
        val imageUrl: String,
    )

    interface Interactor : LinkableTag.Interactor {
        // Content Actions
        fun onUserClicked(id: String)
        fun onUserLongClicked(id: String)

        // Bottom Control Actions
        fun onLeaveRoomClicked()
        fun onRequestToSpeakClicked()
        fun onCancelRequestClicked()
        fun onLeaveStageClicked()
        fun onMuteControlClicked(mute: Boolean)
        fun onAudienceButtonClicked()

        // Toolbar Actions
        fun onBackButtonClicked()
        fun onShareClicked()
        fun onHostMenuClicked()
        fun onTabClicked(tab: LiveRoomTab)

        // Chat
        fun onChatInputChanged(value: String)
        fun onSendChatClicked()
        fun onMessageClicked(messageId: String)
        fun onMessageLongClicked(messageId: String)
        fun onChatPreviewClicked()
        fun onLockedChatInputClicked()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LiveRoomScreen(
    showSpinner: Boolean,
    roomTitle: String,
    roomDescription: String,
    hosts: List<LiveRoomUi.HostInfo>,
    tags: List<LiveRoomUi.TagInfo>,
    totalAudienceSize: Int,
    recording: Boolean,
    isHost: Boolean,
    isModerator: Boolean,
    isOnStage: Boolean,
    isMuted: Boolean,
    isLocked: Boolean,
    requestPending: Boolean,
    audienceRequestCount: Int,
    speakers: List<LiveRoomUi.Speaker>,
    chatInput: String,
    messages: List<LiveRoomUi.ChatMessage>,
    volumeProvider: Flow<Map<String, Int>>,
    interactor: LiveRoomUi.Interactor,
) {
    var showDetails by remember { mutableStateOf(false) }

    BackHandler(enabled = showDetails) {
        showDetails = !showDetails
    }

    if (showSpinner) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AthTheme.colors.dark600,
            )
        }
    } else {
        Column(
            Modifier
                .fillMaxSize()
                .background(AthTheme.colors.dark100)
        ) {
            Toolbar(
                title = roomTitle,
                totalAudienceSize = totalAudienceSize,
                recording = recording,
                isHost = isHost,
                isModerator = isModerator,
                onBackClicked = {
                    if (showDetails) {
                        showDetails = false
                    } else {
                        interactor.onBackButtonClicked()
                    }
                },
                onTitleClicked = { showDetails = !showDetails },
                interactor = interactor,
            )
            AnimatedContent(
                targetState = showDetails,
                transitionSpec = {
                    when {
                        targetState -> slideInOutLeft()
                        else -> slideInOutRight()
                    }
                },
            ) { state ->
                when {
                    state -> LiveRoomDetailsScreen(
                        roomTitle = roomTitle,
                        roomDescription = roomDescription,
                        hosts = hosts,
                        tags = tags,
                        recording = recording,
                        interactor = interactor,
                    )
                    else -> StageScreen(
                        isHost = isHost,
                        isModerator = isModerator,
                        isOnStage = isOnStage,
                        isMuted = isMuted,
                        isLocked = isLocked,
                        requestPending = requestPending,
                        audienceRequestCount = audienceRequestCount,
                        speakers = speakers,
                        chatInput = chatInput,
                        messages = messages,
                        volumeProvider = volumeProvider,
                        interactor = interactor,
                    )
                }
            }
        }
    }
}

@Composable
fun StageScreen(
    isHost: Boolean,
    isModerator: Boolean,
    isOnStage: Boolean,
    isMuted: Boolean,
    isLocked: Boolean,
    requestPending: Boolean,
    audienceRequestCount: Int,
    speakers: List<LiveRoomUi.Speaker>,
    chatInput: String,
    messages: List<LiveRoomUi.ChatMessage>,
    volumeProvider: Flow<Map<String, Int>>,
    interactor: LiveRoomUi.Interactor,
) {
    Column(Modifier.fillMaxSize()) {
        Stage(
            speakers = speakers,
            volumeProvider = volumeProvider,
            interactor = interactor,
        )
        Chat(
            messages = messages,
            interactor = interactor,
        )
        MuteControls(
            isOnStage = isOnStage,
            isMuted = isMuted,
            isModerator = isModerator,
            interactor = interactor,
        )
        Divider(color = AthTheme.colors.dark200)
        BottomButtonRow(
            isHost = isHost,
            isModerator = isModerator,
            isOnStage = isOnStage,
            isLocked = isLocked,
            requestPending = requestPending,
            chatInput = chatInput,
            audienceRequestCount = audienceRequestCount,
            interactor = interactor,
        )
    }
}

@Composable
private fun Toolbar(
    title: String,
    totalAudienceSize: Int,
    recording: Boolean,
    isHost: Boolean,
    isModerator: Boolean,
    onBackClicked: () -> Unit,
    onTitleClicked: () -> Unit,
    interactor: LiveRoomUi.Interactor,
) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(horizontal = 12.dp)
    ) {
        Row(Modifier.align(Alignment.CenterStart)) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                tint = AthTheme.colors.dark800,
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .clickable(onClick = onBackClicked)
                    .padding(4.dp),
            )
            if (isHost || isModerator) {
                Spacer(Modifier.width(4.dp))
                ResourceIcon(
                    resourceId = R.drawable.ic_dots,
                    tint = AthTheme.colors.dark800,
                    modifier = Modifier
                        .size(26.dp)
                        .padding(3.dp)
                        .clickable(onClick = interactor::onHostMenuClicked)
                )
            }
        }
        ToolbarText(
            title = title,
            totalAudienceSize = totalAudienceSize,
            recording = recording,
            modifier = Modifier.clickable(
                onClick = onTitleClicked,
                indication = rememberRipple(bounded = false),
                interactionSource = remember { MutableInteractionSource() },
            ),
        )
        Text(
            text = stringResource(R.string.rooms_leave),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
            color = AthTheme.colors.red,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable(onClick = interactor::onLeaveRoomClicked),
        )
    }
}

@Composable
fun BoxScope.ToolbarText(
    title: String,
    totalAudienceSize: Int,
    recording: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.align(Alignment.Center),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (recording) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color = AthTheme.colors.red, shape = CircleShape)
                )
            }
            Text(
                text = title,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = AthTheme.colors.dark800,
                modifier = Modifier
                    .widthIn(max = 200.dp)
                    .padding(start = 4.dp),
            )
            ResourceIcon(
                resourceId = R.drawable.ic_chevron_right,
                tint = AthTheme.colors.dark800,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = stringResource(R.string.rooms_listener_count, totalAudienceSize),
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark600,
        )
    }
}

@Composable
private fun Stage(
    speakers: List<LiveRoomUi.Speaker>,
    volumeProvider: Flow<Map<String, Int>>,
    interactor: LiveRoomUi.Interactor,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .background(AthTheme.colors.dark200)
    ) {
        if (speakers.isNotEmpty()) {
            LiveRoomStageV2(
                speakers = speakers,
                onUserClick = interactor::onUserClicked,
                onUserLongClick = interactor::onUserLongClicked,
                volumeProvider = volumeProvider,
            )
        } else {
            StageNoHosts()
        }
    }
}

@Composable
private fun BoxScope.StageNoHosts() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .align(Alignment.Center)
            .padding(vertical = 12.dp),
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.anim_live_room_loading)
        )
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(60.dp),
        )
        Text(
            text = stringResource(id = R.string.rooms_awaiting_host),
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.dark700,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ColumnScope.Chat(
    messages: List<LiveRoomUi.ChatMessage>,
    interactor: LiveRoomUi.Interactor,
) {
    val modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .background(AthTheme.colors.dark200)
        .topScrim(AthTheme.colors.dark100)

    when (messages.isEmpty()) {
        true -> LiveRoomEmptyChat(modifier = modifier)
        else -> ChatMessageList(
            messages = messages,
            interactor = interactor,
            modifier = modifier,
        )
    }
}

@Composable
private fun MuteControls(
    isModerator: Boolean,
    isOnStage: Boolean,
    isMuted: Boolean,
    interactor: LiveRoomUi.Interactor,
) {
    Box(
        modifier = Modifier.animateContentSize(),
    ) {
        if (isOnStage || isModerator) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AthTheme.colors.dark200)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                when {
                    isModerator && isOnStage -> RequestButton(
                        modifier = Modifier.weight(1f).padding(end = 16.dp),
                        textRes = R.string.rooms_leave_stage,
                        textColor = AthTheme.colors.dark100,
                        backgroundColor = AthTheme.colors.dark800,
                        onClick = interactor::onLeaveStageClicked,
                    )
                    isModerator && !isOnStage -> RequestButton(
                        modifier = Modifier.weight(1f),
                        textRes = R.string.rooms_ask_to_speak,
                        onClick = interactor::onRequestToSpeakClicked
                    )
                }
                if (isOnStage) {
                    MuteButton(isMuted = isMuted, interactor = interactor)
                }
            }
        } else {
            Box(
                Modifier
                    .height(0.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RowScope.MuteButton(
    isMuted: Boolean,
    interactor: LiveRoomUi.Interactor,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = { interactor.onMuteControlClicked(!isMuted) })
            .then(
                if (isMuted) {
                    Modifier.background(AthTheme.colors.red, RoundedCornerShape(40.dp))
                } else {
                    Modifier.background(LiveRoomBubbleGradient, RoundedCornerShape(40.dp))
                }
            )
            .padding(vertical = 8.dp)
    ) {
        ResourceIcon(
            resourceId = when {
                isMuted -> R.drawable.ic_live_audio_mic_off
                else -> R.drawable.ic_live_audio_mic_on
            }
        )
        Text(
            text = stringResource(
                when {
                    isMuted -> R.string.rooms_mic_off
                    else -> R.string.rooms_mic_on
                }
            ),
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthColor.Gray800,
        )
    }
}

@Composable
private fun BottomButtonRow(
    isHost: Boolean,
    isModerator: Boolean,
    isOnStage: Boolean,
    isLocked: Boolean,
    requestPending: Boolean,
    chatInput: String,
    audienceRequestCount: Int,
    interactor: LiveRoomUi.Interactor,
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp)
    ) {
        val keyboardVisible = LocalWindowInsets.current.ime.isVisible
        if (!keyboardVisible) {
            RequestButton(
                isHost = isHost,
                isModerator = isModerator,
                isOnStage = isOnStage,
                requestPending = requestPending,
                audienceRequestCount = audienceRequestCount,
                interactor = interactor,
            )
            Spacer(Modifier.width(16.dp))
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            ChatInputTextField(
                chatInput = chatInput,
                interactor = interactor,
                modifier = Modifier.fillMaxSize()
                    .alpha(if (isLocked) 0.5f else 1f)
            )

            if (isLocked) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clickable(onClick = interactor::onLockedChatInputClicked)
                )
            }
        }
        if (!keyboardVisible) {
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Share,
                tint = AthTheme.colors.dark800,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .clickable(onClick = interactor::onShareClicked)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun RequestButton(
    isHost: Boolean,
    isModerator: Boolean,
    isOnStage: Boolean,
    requestPending: Boolean,
    audienceRequestCount: Int,
    interactor: LiveRoomUi.Interactor,
) {
    when {
        (isHost && isOnStage) || isModerator -> RequestButton(
            textRes = R.string.rooms_stage_queue,
            textColor = AthTheme.colors.dark100,
            backgroundColor = AthTheme.colors.dark800,
            onClick = interactor::onAudienceButtonClicked
        ) {
            if (audienceRequestCount > 0) {
                FixedSizeBadge(text = audienceRequestCount.toString())
            }
        }
        isOnStage -> RequestButton(
            textRes = R.string.rooms_leave_stage,
            textColor = AthTheme.colors.dark100,
            backgroundColor = AthTheme.colors.dark800,
            onClick = interactor::onLeaveStageClicked,
        )
        isHost -> RequestButton(
            textRes = R.string.rooms_create_go_live,
            onClick = interactor::onRequestToSpeakClicked,
        )
        requestPending -> RequestButton(
            textRes = R.string.rooms_ask_cancel,
            textColor = AthTheme.colors.red,
            backgroundColor = AthTheme.colors.dark800,
            onClick = interactor::onCancelRequestClicked,
        )
        else -> RequestButton(
            textRes = R.string.rooms_ask_to_speak,
            onClick = interactor::onRequestToSpeakClicked,
        )
    }
}

@Composable
private fun RequestButton(
    modifier: Modifier = Modifier,
    @StringRes textRes: Int,
    textColor: Color = AthColor.Gray800,
    backgroundColor: Color = Color.Unspecified,
    onClick: () -> Unit,
    backgroundBrush: Brush = LiveRoomBubbleGradient,
    badgeContent: @Composable() (BoxScope.() -> Unit) = {},
) {
    BoxWithBadge(
        badgeContent = badgeContent,
        modifier = modifier
            .height(36.dp)
            .clickable(onClick = onClick)
            .then(
                if (backgroundColor.isSpecified) {
                    Modifier.background(backgroundColor, RoundedCornerShape(40.dp))
                } else {
                    Modifier.background(backgroundBrush, RoundedCornerShape(40.dp))
                }
            )
    ) {
        Text(
            text = stringResource(textRes),
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = textColor,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        )
    }
}