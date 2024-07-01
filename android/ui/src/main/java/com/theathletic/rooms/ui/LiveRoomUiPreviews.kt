package com.theathletic.rooms.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.theathletic.themes.AthleticTheme
import kotlinx.coroutines.flow.emptyFlow

@Composable
@Preview(backgroundColor = 0xFF000000, showBackground = true)
private fun LiveRoomScreen_Preview() {
    LiveRoomScreen(
        showSpinner = false,
        roomTitle = LiveRoomPreviewData.RoomTitle,
        roomDescription = LiveRoomPreviewData.RoomDescription,
        hosts = emptyList(),
        tags = emptyList(),
        totalAudienceSize = 100,
        recording = true,
        isHost = true,
        isModerator = false,
        isOnStage = true,
        isMuted = false,
        isLocked = false,
        requestPending = false,
        audienceRequestCount = 8,
        speakers = LiveRoomPreviewData.Speakers,
        chatInput = "",
        messages = LiveRoomPreviewData.Messages,
        interactor = LiveRoomPreviewData.Interactor,
        volumeProvider = emptyFlow(),
    )
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
private fun LiveRoomScreen_LightPreview() {
    AthleticTheme(lightMode = true) {
        LiveRoomScreen(
            showSpinner = false,
            roomTitle = LiveRoomPreviewData.RoomTitle,
            roomDescription = LiveRoomPreviewData.RoomDescription,
            hosts = emptyList(),
            tags = emptyList(),
            totalAudienceSize = 100,
            recording = true,
            isHost = true,
            isModerator = false,
            isOnStage = true,
            isMuted = false,
            isLocked = false,
            requestPending = false,
            audienceRequestCount = 8,
            speakers = LiveRoomPreviewData.Speakers,
            chatInput = "",
            messages = LiveRoomPreviewData.Messages,
            interactor = LiveRoomPreviewData.Interactor,
            volumeProvider = emptyFlow(),
        )
    }
}