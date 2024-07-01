package com.theathletic.podcast.data.local

import com.squareup.moshi.JsonClass
import com.theathletic.entity.local.AthleticEntity

@JsonClass(generateAdapter = true)
data class PodcastSeriesEntity(
    override val id: String,
    val title: String = "",
    val subtitle: String = "",
    val imageUrl: String = "",
    val category: String = "",

    val isFollowing: Boolean = false,
    val notifyEpisodes: Boolean = false
) : AthleticEntity {
    override val type = AthleticEntity.Type.PODCAST_SERIES
}