package com.theathletic.rooms.create.ui

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.ui.asString
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CreateLiveRoomComposeFragment : AthleticComposeFragment<
    CreateLiveRoomViewModel,
    CreateLiveRoomContract.ViewState
    >() {

    companion object {
        const val EXTRA_ROOM_TO_EDIT = "extra_room_to_edit"

        fun newInstance(roomToEditId: String?) = CreateLiveRoomComposeFragment().apply {
            arguments = bundleOf(EXTRA_ROOM_TO_EDIT to roomToEditId)
        }
    }

    override fun setupViewModel() = getViewModel<CreateLiveRoomViewModel> {
        parametersOf(
            CreateLiveRoomViewModel.Params(
                liveRoomToEdit = arguments?.getString(EXTRA_ROOM_TO_EDIT)
            ),
            navigator
        )
    }

    @Composable
    override fun Compose(state: CreateLiveRoomContract.ViewState) {
        CreateLiveRoomScreen(
            uiModel = CreateLiveRoomUi(
                isEditing = state.isInEditMode,
                titleInput = state.title,
                titleMaxLength = state.titleMaxCharacterCount,
                descriptionInput = state.description,
                descriptionMaxLength = state.descriptionMaxCharacterCount,
                topicTags = state.topics,
                hosts = state.hosts,
                categories = state.categories.map { it.asString() },
                selfAsHost = state.currentUserIsHost,
                record = state.recordingOn,
                sendAutoPush = state.sendAutoPushOn,
                disableChat = state.disableChatOn,
                submitButtonEnabled = state.enableCreateButton,
                isCreatingRoom = state.showCreationSpinner,
            ),
            interactor = viewModel,
        )
    }
}