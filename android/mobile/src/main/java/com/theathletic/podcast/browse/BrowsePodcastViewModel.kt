package com.theathletic.podcast.browse

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.entity.main.PodcastTopicEntryType
import com.theathletic.podcast.ui.PodcastListItem
import com.theathletic.ui.DynamicListSection
import com.theathletic.ui.LoadingState
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.LegacyAthleticListViewModel
import com.theathletic.ui.list.ListVerticalPadding
import com.theathletic.ui.list.list
import com.theathletic.utility.coroutines.collectIn

class BrowsePodcastViewModel(
    extras: Bundle
) : LegacyAthleticListViewModel() {

    private val _uiModels = MutableLiveData<List<UiModel>>()
    override val uiModels: LiveData<List<UiModel>> = _uiModels

    private val topicId = extras.getLong(BrowsePodcastActivity.EXTRA_TOPIC_ID, -1L)
    private val topicEntryType = PodcastTopicEntryType.valueOf(
        extras.getString(BrowsePodcastActivity.EXTRA_TOPIC_ENTRY_TYPE) ?: "UNKNOWN"
    )
    private val dataLoader = when (topicEntryType) {
        PodcastTopicEntryType.LEAGUE -> PodcastBrowseLeagueDataLoader(topicId)
        PodcastTopicEntryType.CHANNEL -> PodcastBrowseChannelDataLoader(topicId)
        else -> throw IllegalStateException("$topicEntryType not supported")
    }

    private var browseSections: PodcastSectionedList = PodcastSectionedList()

    init {
        loadData()
    }

    fun loadData() {
        setLoadingState(LoadingState.INITIAL_LOADING)
        dataLoader.collectIn(viewModelScope) { sections ->
            setLoadingState(LoadingState.FINISHED)
            browseSections = sections
            rerender()
        }
        dataLoader.load()
    }

    fun rerender() {
        _uiModels.postValue(generateList())
    }

    private fun generateList() = when {
        loadingState.value == LoadingState.INITIAL_LOADING -> LOADING_LIST
        browseSections.hasMultipleSections -> getMultiSectionList()
        else -> getSingleSectionList()
    }

    private fun getSingleSectionList() = list {
        section(null) {
            browseSections.values.toList().firstOrNull()?.mapIndexed { index, podcastItem ->
                PodcastListItem.fromDataModel(podcastItem, index)
            } ?: emptyList()
        }
    }

    private fun getMultiSectionList() = list {
        browseSections.entries.map { browseSection ->
            val section = browseSection.key
            section(DynamicListSection(section.name, section.titleId)) {
                browseSection.value.mapIndexed { index, podcastItem ->
                    PodcastListItem.fromDataModel(podcastItem, index)
                } + ListVerticalPadding(R.dimen.global_spacing_20)
            }
        }
    }

    private val PodcastSectionedList.hasMultipleSections: Boolean get() {
        return entries.count { it.value.isNotEmpty() } > 1
    }
}