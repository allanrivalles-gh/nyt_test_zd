package com.theathletic.rooms.create.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.entity.room.LiveRoomCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * As the Create Live Room flow spans multiple screens, this class is meant to hold the current
 * state longer than the lifecycle of a single activity. Rather than passing around lists of items
 * through Bundle, this keeps track of the state and any of the Create Live Room screens can update
 * it with the values its responsible for.
 *
 * We can use this as the single source of truth from which to make the final "Create Live Room"
 * mutation call with the data stored here.
 */
class LiveRoomCreationInputStateHolder @AutoKoin(Scope.SINGLE) constructor() {

    private val _currentInput = MutableStateFlow(LiveRoomCreationInput())
    val currentInput: StateFlow<LiveRoomCreationInput> get() = _currentInput

    fun reset() {
        _currentInput.value = LiveRoomCreationInput()
    }

    fun setTitle(title: String) {
        _currentInput.value = currentInput.value.copy(title = title)
    }

    fun setDescription(description: String) {
        _currentInput.value = currentInput.value.copy(description = description)
    }

    fun setCurrentUserIsHost(userIsHost: Boolean) {
        _currentInput.value = currentInput.value.copy(currentUserIsHost = userIsHost)
    }

    fun setRecorded(recorded: Boolean) {
        _currentInput.value = currentInput.value.copy(recorded = recorded)
    }

    fun setSendAutoPush(sendAutoPush: Boolean) {
        _currentInput.value = currentInput.value.copy(sendAutoPush = sendAutoPush)
    }

    fun setDisableChat(disableChat: Boolean) {
        _currentInput.value = currentInput.value.copy(disableChat = disableChat)
    }

    fun addTag(tag: LiveRoomTagOption) {
        val current = currentInput.value
        _currentInput.value = current.copy(tags = current.tags + tag)
    }

    fun removeTag(tag: LiveRoomTagOption) {
        val current = currentInput.value
        _currentInput.value = current.copy(tags = current.tags - tag)
    }

    fun addHost(host: LiveRoomHostOption) {
        val current = currentInput.value
        _currentInput.value = current.copy(hosts = current.hosts + host)
    }

    fun removeHost(host: LiveRoomHostOption) {
        val current = currentInput.value
        _currentInput.value = current.copy(hosts = current.hosts - host)
    }

    fun addCategory(category: LiveRoomCategory) {
        val current = currentInput.value
        _currentInput.value = current.copy(categories = current.categories + category)
    }

    fun removeCategory(category: LiveRoomCategory) {
        val current = currentInput.value
        _currentInput.value = current.copy(categories = current.categories - category)
    }

    fun setFromEntity(entity: LiveAudioRoomEntity) {
        _currentInput.value = currentInput.value.copy(
            title = entity.title,
            description = entity.description,
            recorded = entity.isRecording,
            tags = entity.tags.map { tag ->
                LiveRoomTagOption(
                    id = tag.id,
                    type = tag.type,
                    title = tag.title,
                    name = tag.name,
                    shortname = tag.shortname,
                )
            }.toSet(),
            categories = entity.categories.toSet(),
            hosts = entity.hosts.map { host ->
                LiveRoomHostOption(
                    id = host.id,
                    name = host.name,
                    avatarUrl = host.imageUrl,
                )
            }.toSet(),
            sendAutoPush = entity.autoPushEnabled,
            disableChat = entity.chatDisabled,
        )
    }
}

data class LiveRoomCreationInput(
    val title: String = "",
    val description: String = "",
    val currentUserIsHost: Boolean = true,
    val recorded: Boolean = true,
    val sendAutoPush: Boolean = true,
    val disableChat: Boolean = false,
    val tags: Set<LiveRoomTagOption> = emptySet(),
    val hosts: Set<LiveRoomHostOption> = emptySet(),
    val categories: Set<LiveRoomCategory> = emptySet(),
)