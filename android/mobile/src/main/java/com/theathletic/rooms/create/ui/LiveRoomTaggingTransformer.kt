package com.theathletic.rooms.create.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.rooms.create.data.local.LiveRoomCreationSearchMode
import com.theathletic.rooms.create.data.local.LiveRoomHostOption
import com.theathletic.rooms.create.data.local.LiveRoomTagOption
import com.theathletic.ui.Transformer
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.Divider
import com.theathletic.ui.list.ListRoot

class LiveRoomTaggingTransformer @AutoKoin constructor() :
    Transformer<LiveRoomTaggingState, LiveRoomTaggingContract.ViewState> {

    override fun transform(data: LiveRoomTaggingState): LiveRoomTaggingContract.ViewState {
        var dividerSeed = 0
        val uiModels = mutableListOf<UiModel>(ListRoot)

        val trimmedSearchText = data.searchText.trim()
        when {
            trimmedSearchText.isEmpty() -> data.tagOptions
            else -> data.tagOptions.filterByQuery(trimmedSearchText)
        }.forEach { tag ->
            uiModels.add(
                LiveRoomTagSearchResultUiModel(
                    id = tag.id,
                    type = tag.type,
                    name = tag.name,
                    logoUri = tag.logoUrl,
                    isChecked = data.creationInput?.tags?.contains(tag) == true,
                )
            )
            uiModels.add(Divider(++dividerSeed))
        }

        when {
            trimmedSearchText.isEmpty() -> data.hostOptions
            else -> data.hostOptions.filterHostsByQuery(trimmedSearchText)
        }.forEach { tag ->
            uiModels.add(
                LiveRoomHostSearchResultUiModel(
                    id = tag.id,
                    name = tag.name,
                    avatarUrl = tag.avatarUrl,
                    isChecked = data.creationInput?.hosts?.contains(tag) == true,
                )
            )
            uiModels.add(Divider(++dividerSeed))
        }

        return LiveRoomTaggingContract.ViewState(
            searchText = data.searchText,
            showClearButton = data.searchText.isNotEmpty(),
            resultsUiModels = uiModels,
            selectedChipModels = getChipModels(data),
        )
    }

    private fun getChipModels(state: LiveRoomTaggingState): List<LiveRoomTagSearchChipUiModel> {
        return when (state.searchMode) {
            LiveRoomCreationSearchMode.TAGS -> state.creationInput?.tags?.map {
                LiveRoomTagSearchChipUiModel(it.id, it.type, it.title)
            }
            LiveRoomCreationSearchMode.HOSTS -> state.creationInput?.hosts?.map {
                LiveRoomTagSearchChipUiModel(it.id, LiveRoomTagType.NONE, it.name)
            }
        } ?: emptyList()
    }

    private fun List<LiveRoomTagOption>.filterByQuery(query: String) = filter {
        it.name.contains(query, ignoreCase = true) ||
            it.shortname.contains(query, ignoreCase = true) ||
            it.title.contains(query, ignoreCase = true)
    }

    private fun List<LiveRoomHostOption>.filterHostsByQuery(query: String) = filter {
        it.name.contains(query, ignoreCase = true)
    }
}