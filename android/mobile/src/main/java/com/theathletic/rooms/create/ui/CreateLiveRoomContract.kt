package com.theathletic.rooms.create.ui

import com.theathletic.presenter.Interactor
import com.theathletic.ui.ResourceString

interface CreateLiveRoomContract {

    interface Presenter :
        Interactor,
        CreateLiveRoomUi.Interactor {
        override fun onTitleInputChanged(title: String)
        override fun onDescriptionInputChanged(description: String)
        override fun onRecordingToggled(recordingOn: Boolean)
        override fun onSendAutoPushToggled(autoPushOn: Boolean)
        override fun onCurrentUserHostToggled(userIsHost: Boolean)
        override fun onDisableChatToggled(disableChat: Boolean)

        override fun onAddTagsClicked()
        override fun onAddHostsClicked()

        override fun onCreateRoomClicked()
    }

    data class ViewState(
        val title: String,
        val description: String,
        val titleMaxCharacterCount: Int,
        val descriptionMaxCharacterCount: Int,
        val topics: List<String> = emptyList(),
        val hosts: List<String> = emptyList(),
        val categories: List<ResourceString> = emptyList(),
        val currentUserIsHost: Boolean = true,
        val recordingOn: Boolean = true,
        val sendAutoPushOn: Boolean = true,
        val disableChatOn: Boolean = false,
        val enableCreateButton: Boolean,
        val showCreationSpinner: Boolean,
        val isInEditMode: Boolean,
    ) : com.theathletic.ui.ViewState
}