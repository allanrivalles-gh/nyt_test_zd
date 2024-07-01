package com.theathletic.podcast.browse

import com.theathletic.entity.main.PodcastItem
import com.theathletic.entity.main.PodcastLeagueFeed
import com.theathletic.extension.applySchedulers
import com.theathletic.podcast.data.LegacyPodcastRepository
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.rx2.asFlow

abstract class PodcastTopicDataLoader : AbstractFlow<PodcastSectionedList>() {

    abstract val flow: Flow<PodcastSectionedList>
    abstract fun load()

    override suspend fun collectSafely(collector: FlowCollector<PodcastSectionedList>) {
        flow.collect { collector.emit(it) }
    }
}

class PodcastBrowseLeagueDataLoader(val leagueId: Long) : PodcastTopicDataLoader() {

    private val leagueFeedData = LegacyPodcastRepository.getPodcastLeagueFeedData(leagueId)
    override val flow: Flow<PodcastSectionedList> get() = leagueFeedData.getDataObservable()
        .applySchedulers()
        .asFlow()
        .mapNotNull { it.data }
        .map { transform(it) }

    private fun transform(feed: PodcastLeagueFeed) = linkedMapOf(
        BrowsePodcastSection.NATIONAL to feed.national,
        BrowsePodcastSection.TEAMS to feed.teams
    )

    override fun load() {
        leagueFeedData.load()
    }
}

class PodcastBrowseChannelDataLoader(channelId: Long) : PodcastTopicDataLoader() {

    private val channelFeedData = LegacyPodcastRepository.getPodcastChannelFeedData(channelId)
    override val flow: Flow<PodcastSectionedList> get() = channelFeedData.getDataObservable()
        .applySchedulers()
        .asFlow()
        .mapNotNull { it.data }
        .map { transform(it) }

    private fun transform(items: List<PodcastItem>) = linkedMapOf(
        BrowsePodcastSection.CHANNEL to items
    )

    override fun load() {
        channelFeedData.load()
    }
}