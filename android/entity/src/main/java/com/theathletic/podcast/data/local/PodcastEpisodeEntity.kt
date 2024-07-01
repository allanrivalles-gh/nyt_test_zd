package com.theathletic.podcast.data.local

import com.squareup.moshi.JsonClass
import com.theathletic.datetime.Datetime
import com.theathletic.entity.local.AthleticEntity

@JsonClass(generateAdapter = true)
data class PodcastEpisodeEntity(
    override val id: String,
    val seriesId: String = "",
    val episodeNumber: Int = -1,
    val seriesTitle: String = "",
    val title: String = "",
    val description: String = "",
    val duration: Long = 0L,
    val timeElapsedMs: Long = 0L,
    val mp3Url: String = "",
    val permalinkUrl: String = "",
    val imageUrl: String = "",
    val isFinished: Boolean = false,
    val publishedAt: Datetime = Datetime(0),
    val numberOfComments: Int = 0,
) : AthleticEntity {
    override val type = AthleticEntity.Type.PODCAST_EPISODE
}