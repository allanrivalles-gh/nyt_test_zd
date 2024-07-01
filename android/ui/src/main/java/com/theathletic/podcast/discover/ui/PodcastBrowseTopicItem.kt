package com.theathletic.podcast.discover.ui

import com.theathletic.ui.UiModel

interface IPodcastBrowseTopicView {
    fun onPodcastBrowseItemClicked(item: PodcastBrowseTopicItem)
}

sealed class PodcastBrowseTopicItem(
    override val stableId: String,
    open val id: Long,
    open val name: String,
    open val logoUri: String,
    open val vIndex: Int,
) : UiModel {

    data class League(
        override val stableId: String,
        override val id: Long,
        override val name: String,
        override val logoUri: String,
        override val vIndex: Int
    ) : PodcastBrowseTopicItem(
        stableId,
        id,
        name,
        logoUri,
        vIndex
    )

    class Channel(
        override val stableId: String,
        override val id: Long,
        override val name: String,
        override val logoUri: String,
        override val vIndex: Int
    ) : PodcastBrowseTopicItem(
        stableId,
        id,
        name,
        logoUri,
        vIndex
    )
}