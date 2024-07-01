package com.theathletic.rooms.create.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.event.SnackbarEventRes
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.rooms.RoomsRepository
import com.theathletic.rooms.create.data.LiveRoomCreationRepository
import com.theathletic.rooms.create.data.local.LiveRoomCreationInput
import com.theathletic.rooms.create.data.local.LiveRoomCreationInputStateHolder
import com.theathletic.rooms.create.data.local.LiveRoomCreationInputValidator
import com.theathletic.rooms.create.data.local.LiveRoomCreationSearchMode
import com.theathletic.rooms.create.ui.CreateLiveRoomContract.ViewState
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class CreateLiveRoomViewModel @AutoKoin constructor(
    @Assisted val params: Params,
    @Assisted val navigator: ScreenNavigator,
    userManager: IUserManager,
    private val creationInputStateHolder: LiveRoomCreationInputStateHolder,
    private val inputValidator: LiveRoomCreationInputValidator,
    private val roomsRepository: RoomsRepository,
    private val liveRoomCreationRepository: LiveRoomCreationRepository,
) :
    AthleticViewModel<CreateLiveRoomState, ViewState>(),
    CreateLiveRoomContract.Presenter {

    data class Params(val liveRoomToEdit: String? = null)

    override val initialState by lazy {
        CreateLiveRoomState(currentUserId = userManager.getCurrentUserId().toString())
    }

    private val isInEditMode get() = !params.liveRoomToEdit.isNullOrEmpty()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initialize() {
        creationInputStateHolder.reset()

        creationInputStateHolder.currentInput.collectIn(viewModelScope) {
            updateState { copy(creationInput = it) }
        }

        params.liveRoomToEdit?.let { id ->
            viewModelScope.launch {
                val room = roomsRepository.getLiveAudioRoom(id)

                if (room == null) {
                    navigator.finishActivity()
                } else {
                    creationInputStateHolder.setFromEntity(room)
                }
            }
        }
    }

    override fun onTitleInputChanged(title: String) {
        creationInputStateHolder.setTitle(title)
    }

    override fun onDescriptionInputChanged(description: String) {
        creationInputStateHolder.setDescription(description)
    }

    override fun onAddTagsClicked() {
        navigator.startLiveRoomTaggingActivity(LiveRoomCreationSearchMode.TAGS)
    }

    override fun onAddHostsClicked() {
        navigator.startLiveRoomTaggingActivity(LiveRoomCreationSearchMode.HOSTS)
    }

    override fun onAddRoomTypeClicked() {
        navigator.startLiveRoomCategoriesActivity()
    }

    override fun onCurrentUserHostToggled(userIsHost: Boolean) {
        creationInputStateHolder.setCurrentUserIsHost(userIsHost)
    }

    override fun onRecordingToggled(recordingOn: Boolean) {
        creationInputStateHolder.setRecorded(recordingOn)
    }

    override fun onSendAutoPushToggled(autoPushOn: Boolean) {
        creationInputStateHolder.setSendAutoPush(autoPushOn)
    }

    override fun onDisableChatToggled(disableChat: Boolean) {
        creationInputStateHolder.setDisableChat(disableChat)
    }

    override fun onBackClicked() {
        navigator.finishActivity()
    }

    override fun onCreateRoomClicked() {
        viewModelScope.launch {
            updateState { copy(isCreationRequestPending = true) }
            if (isInEditMode) {
                updateLiveRoom()
            } else {
                createLiveRoom()
            }
        }
    }

    private suspend fun createLiveRoom() {
        val liveRoom = liveRoomCreationRepository.createLiveRoom(
            state.currentUserId,
            state.creationInput
        )

        val roomId = liveRoom?.id
        if (!roomId.isNullOrEmpty()) {
            navigator.startLiveAudioRoomActivity(roomId)
            navigator.finishActivity()
        } else {
            sendEvent(SnackbarEventRes(R.string.global_error))
            updateState { copy(isCreationRequestPending = false) }
        }
    }

    private suspend fun updateLiveRoom() {
        val roomId = params.liveRoomToEdit ?: return
        liveRoomCreationRepository.updateLiveRoom(roomId, state.creationInput)
        navigator.finishActivity()
    }

    override fun transform(state: CreateLiveRoomState): ViewState {
        return ViewState(
            title = state.creationInput.title,
            description = state.creationInput.description,
            titleMaxCharacterCount = LiveRoomCreationInputValidator.TITLE_MAX_CHARACTERS,
            descriptionMaxCharacterCount = LiveRoomCreationInputValidator.DESCRIPTION_MAX_CHARACTERS,
            topics = state.creationInput.tags.map { it.title },
            hosts = state.creationInput.hosts.map { it.name },
            categories = state.creationInput.categories.map { it.displayString },
            currentUserIsHost = state.creationInput.currentUserIsHost,
            recordingOn = state.creationInput.recorded,
            sendAutoPushOn = state.creationInput.sendAutoPush,
            disableChatOn = state.creationInput.disableChat,
            enableCreateButton = inputValidator.isValid(state.creationInput) && !state.isCreationRequestPending,
            showCreationSpinner = state.isCreationRequestPending,
            isInEditMode = isInEditMode
        )
    }
}

data class CreateLiveRoomState(
    val currentUserId: String,
    val creationInput: LiveRoomCreationInput = LiveRoomCreationInput(),
    val isCreationRequestPending: Boolean = false,
) : DataState