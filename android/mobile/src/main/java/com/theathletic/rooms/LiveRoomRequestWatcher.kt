package com.theathletic.rooms

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.rooms.ui.LiveAudioEvent
import com.theathletic.rooms.ui.LiveAudioEventProducer
import com.theathletic.user.IUserManager

class LiveRoomRequestWatcher @AutoKoin constructor(
    private val liveAudioEventProducer: LiveAudioEventProducer,
    private val userManager: IUserManager,
    private val roomsRepository: RoomsRepository,
) {
    suspend fun compare(
        old: LiveAudioRoomEntity?,
        new: LiveAudioRoomEntity?
    ) {
        val roomId = new?.id ?: old?.id ?: return
        val userId = userManager.getCurrentUserId().toString()

        checkForPromotions(roomId, userId, old, new)
        checkForDemotions(roomId, userId, old, new)
        checkForMutes(roomId, userId, old, new)
        checkForEndOfRoom(old, new)
        checkForAutoPushUpdate(old, new)
    }

    private suspend fun checkForPromotions(
        roomId: String,
        userId: String,
        old: LiveAudioRoomEntity?,
        new: LiveAudioRoomEntity?,
    ) {
        val oldHasApprovedPromotion = old?.promotionRequests?.any {
            it.userId == userId && it.approved
        }
        val newHasApprovedPromotion = new?.promotionRequests?.any {
            it.userId == userId && it.approved
        } ?: false

        if (newHasApprovedPromotion) {
            // Only send audio event when we actually observe the switch from unapproved to approved
            if (oldHasApprovedPromotion == false) {
                liveAudioEventProducer.emit(
                    LiveAudioEvent.SwapStageStatus(onStage = true, fromHost = true)
                )
            }
            // Call delete here every time to clear out stale requests
            roomsRepository.deleteSpeakingRequest(userId, roomId)
        }
    }

    private suspend fun checkForDemotions(
        roomId: String,
        userId: String,
        old: LiveAudioRoomEntity?,
        new: LiveAudioRoomEntity?,
    ) {
        val oldHasDemotion = old?.demotionRequests?.any { it.userId == userId }
        val newHasDemotion = new?.demotionRequests?.any { it.userId == userId } ?: false

        if (newHasDemotion) {
            // Only send audio event when we actually observe the switch from no demotion request
            // to having a demotion request
            if (oldHasDemotion == false) {
                liveAudioEventProducer.emit(
                    LiveAudioEvent.SwapStageStatus(onStage = false, fromHost = true)
                )
            }
            // Call delete here every time to clear out stale requests
            roomsRepository.deleteDemotionRequest(userId, roomId)
        }
    }

    private suspend fun checkForMutes(
        roomId: String,
        userId: String,
        old: LiveAudioRoomEntity?,
        new: LiveAudioRoomEntity?,
    ) {
        val oldHasMute = old?.muteRequests?.any { it.userId == userId }
        val newHasMute = new?.muteRequests?.any { it.userId == userId } ?: false

        if (newHasMute) {
            // Only send audio event when we actually observe the switch from no mute request
            // to having a mute request
            if (oldHasMute == false) {
                liveAudioEventProducer.emit(LiveAudioEvent.ChangeMute(true))
            }
            // Call delete here every time to clear out stale requests
            roomsRepository.deleteMuteRequest(userId, roomId)
        }
    }

    private suspend fun checkForEndOfRoom(
        old: LiveAudioRoomEntity?,
        new: LiveAudioRoomEntity?,
    ) {
        if (old?.endedAt == null && new?.endedAt != null) {
            liveAudioEventProducer.emit(LiveAudioEvent.LeaveRoom(roomEnded = true))
        }
    }

    private suspend fun checkForAutoPushUpdate(
        old: LiveAudioRoomEntity?,
        new: LiveAudioRoomEntity?,
    ) {
        if (new?.autoPushEnabled != true) {
            return
        }

        if (old?.autoPushSent == false && new.autoPushSent) {
            liveAudioEventProducer.emit(LiveAudioEvent.AutoPushSent)
        }
    }
}