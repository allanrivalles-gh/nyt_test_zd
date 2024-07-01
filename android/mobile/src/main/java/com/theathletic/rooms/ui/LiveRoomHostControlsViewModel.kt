package com.theathletic.rooms.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.audio.LiveAudioStageUser
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.rooms.LiveAudioRoomStateManager
import com.theathletic.rooms.RoomsRepository
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.rooms.ui.LiveRoomHostControlsContract.ViewState
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.collectIn

class LiveRoomHostControlsViewModel @AutoKoin constructor(
    @Assisted val params: LiveRoomHostControlsContract.Params,
    transformer: LiveRoomHostControlsTransformer,
    analytics: LiveAudioRoomAnalytics,
    userManager: IUserManager,
    private val liveAudioRoomStateManager: LiveAudioRoomStateManager,
    private val roomsRepository: RoomsRepository,
) : AthleticViewModel<LiveRoomHostControlsState, ViewState>(),
    Transformer<LiveRoomHostControlsState, ViewState> by transformer,
    LiveAudioRoomAnalytics by analytics,
    LiveRoomHostControlsContract.Presenter {

    override val initialState by lazy {
        LiveRoomHostControlsState(
            currentUserId = userManager.getCurrentUserId().toString()
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initialize() {
        listenForRenderUpdates()
    }

    private fun listenForRenderUpdates() {
        liveAudioRoomStateManager.audioEngineState.collectIn(viewModelScope) { state ->
            state ?: return@collectIn
            updateState {
                copy(
                    usersOnStage = state.usersOnStage,
                    isOnStage = state.isCurrentUserOnStage
                )
            }
        }

        liveAudioRoomStateManager.userDetailsForRoom(params.roomId).collectIn(viewModelScope) { users ->
            updateState { copy(userDetails = users) }
        }

        roomsRepository.getLiveAudioRoomFlow(params.roomId).collectIn(viewModelScope) {
            updateState { copy(liveAudioRoom = it) }
        }
    }

    override fun onRequestResponseClicked(userId: String, approved: Boolean) {
        if (approved) {
            roomsRepository.approveSpeakingRequest(userId, params.roomId)
        } else {
            roomsRepository.deleteSpeakingRequest(userId, params.roomId)
        }
    }

    override fun onRemoveClicked(userId: String) {
        trackClick(
            roomId = params.roomId,
            element = "remove_user",
            objectType = "user_id",
            objectId = userId
        )
        roomsRepository.createDemotionRequest(userId, params.roomId)
    }
}

data class LiveRoomHostControlsState(
    val currentUserId: String,
    val usersOnStage: Set<LiveAudioStageUser> = emptySet(),
    val userDetails: Map<String, LiveAudioRoomUserDetails> = emptyMap(),
    val isOnStage: Boolean = true,
    val liveAudioRoom: LiveAudioRoomEntity? = null,
) : DataState