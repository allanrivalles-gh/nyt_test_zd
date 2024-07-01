package com.theathletic.rooms.ui

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

@Composable
internal fun LiveRoomScreen(
    state: LiveAudioRoomContract.ViewState,
    interactor: LiveAudioRoomContract.Presenter,
    volumeProvider: Flow<Map<String, Int>>,
) {
    return LiveRoomScreen(
        showSpinner = state.showSpinner,
        roomTitle = state.roomTitle,
        roomDescription = state.roomDescription,
        totalAudienceSize = state.totalAudienceSize,
        recording = state.recording,
        hosts = state.hosts,
        tags = state.tags,
        isHost = state.isHost,
        isModerator = state.isModerator,
        isOnStage = state.isOnStage,
        isMuted = state.isMuted,
        isLocked = state.isLocked,
        requestPending = state.hasPendingRequest,
        audienceRequestCount = state.audienceControlsBadgeCount,
        speakers = state.speakers,
        chatInput = state.chatInput,
        messages = state.messages,
        interactor = interactor,
        volumeProvider = volumeProvider,
    )
}