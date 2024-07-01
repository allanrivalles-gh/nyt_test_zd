package com.theathletic.rooms.create.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.theathletic.themes.AthleticTheme

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CreateLiveRoomScreen_Preview() {
    CreateLiveRoomScreen(
        uiModel = PreviewData.UiModel,
        interactor = PreviewData.Interactor,
    )
}

@Preview
@Composable
fun CreateLiveRoomScreen_LightPreview() {
    AthleticTheme(lightMode = true) {
        CreateLiveRoomScreen(
            uiModel = PreviewData.UiModel,
            interactor = PreviewData.Interactor,
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CreateLiveRoomScreen_PopulatedPreview() {
    CreateLiveRoomScreen(
        uiModel = CreateLiveRoomUi(
            isEditing = false,
            titleInput = "Test Title",
            titleMaxLength = 75,
            descriptionInput = "Test Description",
            descriptionMaxLength = 500,
            topicTags = listOf("New York Yankees", "Philidelphia 76ers", "Cleveland Browns"),
            hosts = listOf("Shams Charania", "Matt Kula", "Michael Jordan"),
            categories = listOf("Q&A"),
            selfAsHost = false,
            record = false,
            sendAutoPush = false,
            disableChat = true,
            submitButtonEnabled = false,
            isCreatingRoom = false,
        ),
        interactor = PreviewData.Interactor,
    )
}

private object PreviewData {
    val Interactor = object : CreateLiveRoomUi.Interactor {
        override fun onBackClicked() {
        }

        override fun onTitleInputChanged(title: String) {
        }

        override fun onDescriptionInputChanged(description: String) {
        }

        override fun onAddHostsClicked() {
        }

        override fun onAddTagsClicked() {
        }

        override fun onAddRoomTypeClicked() {
        }

        override fun onRecordingToggled(recordingOn: Boolean) {
        }

        override fun onSendAutoPushToggled(autoPushOn: Boolean) {
        }

        override fun onCurrentUserHostToggled(userIsHost: Boolean) {
        }

        override fun onDisableChatToggled(disableChat: Boolean) {
        }

        override fun onCreateRoomClicked() {
        }
    }
    val UiModel = CreateLiveRoomUi(
        isEditing = false,
        titleInput = "",
        titleMaxLength = 75,
        descriptionInput = "",
        descriptionMaxLength = 500,
        topicTags = emptyList(),
        hosts = emptyList(),
        categories = emptyList(),
        selfAsHost = true,
        record = true,
        sendAutoPush = true,
        disableChat = false,
        submitButtonEnabled = true,
        isCreatingRoom = false,
    )
}