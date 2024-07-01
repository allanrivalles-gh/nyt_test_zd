package com.theathletic.rooms.create.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.rooms.create.data.LiveRoomCreationRepository
import com.theathletic.rooms.create.data.local.LiveRoomCreationInput
import com.theathletic.rooms.create.data.local.LiveRoomCreationInputStateHolder
import com.theathletic.rooms.create.data.local.LiveRoomCreationSearchMode
import com.theathletic.rooms.create.data.local.LiveRoomHostOption
import com.theathletic.rooms.create.data.local.LiveRoomTagOption
import com.theathletic.rooms.create.ui.LiveRoomTaggingContract.ViewState
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.utility.coroutines.collectIn

class LiveRoomTaggingViewModel @AutoKoin constructor(
    @Assisted val params: Params,
    transformer: LiveRoomTaggingTransformer,
    private val liveRoomCreationRepository: LiveRoomCreationRepository,
    private val creationInputStateHolder: LiveRoomCreationInputStateHolder,
) :
    AthleticViewModel<LiveRoomTaggingState, ViewState>(),
    Transformer<LiveRoomTaggingState, ViewState> by transformer,
    LiveRoomTaggingContract.Presenter {

    data class Params(val searchMode: LiveRoomCreationSearchMode)

    override val initialState by lazy {
        LiveRoomTaggingState(searchMode = params.searchMode)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initialize() {
        if (params.searchMode == LiveRoomCreationSearchMode.TAGS) {
            liveRoomCreationRepository.getTagsOptions().collectIn(viewModelScope) {
                updateState { copy(tagOptions = it) }
            }
        }

        if (params.searchMode == LiveRoomCreationSearchMode.HOSTS) {
            liveRoomCreationRepository.getHostsOptions().collectIn(viewModelScope) {
                updateState { copy(hostOptions = it) }
            }
        }

        creationInputStateHolder.currentInput.collectIn(viewModelScope) {
            updateState { copy(creationInput = it) }
        }
    }

    override fun onQueryChanged(query: String) {
        updateState { copy(searchText = query) }
    }

    override fun onClearSearch() {
        updateState { copy(searchText = "") }
    }

    override fun onSearchChipClicked(id: String, type: LiveRoomTagType) {
        when (params.searchMode) {
            LiveRoomCreationSearchMode.TAGS -> onTagClicked(id, type)
            LiveRoomCreationSearchMode.HOSTS -> onHostClicked(id)
        }
    }

    override fun onTagClicked(id: String, type: LiveRoomTagType) {
        val input = state.creationInput ?: return
        val tag = state.creationInput?.tags?.find { it.id == id && it.type == type }
            ?: state.tagOptions.find { it.id == id && it.type == type } ?: return

        if (input.tags.contains(tag)) {
            creationInputStateHolder.removeTag(tag)
        } else {
            creationInputStateHolder.addTag(tag)
        }
    }

    override fun onHostClicked(id: String) {
        val input = state.creationInput ?: return
        val host = state.creationInput?.hosts?.find { it.id == id }
            ?: state.hostOptions.find { it.id == id } ?: return

        if (input.hosts.contains(host)) {
            creationInputStateHolder.removeHost(host)
        } else {
            creationInputStateHolder.addHost(host)
        }
    }
}

data class LiveRoomTaggingState(
    val searchMode: LiveRoomCreationSearchMode,
    val searchText: String = "",
    val tagOptions: List<LiveRoomTagOption> = emptyList(),
    val hostOptions: List<LiveRoomHostOption> = emptyList(),
    val creationInput: LiveRoomCreationInput? = LiveRoomCreationInput(),
) : DataState