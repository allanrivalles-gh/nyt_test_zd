package com.theathletic.entity.main

import com.theathletic.datetime.asGMTString
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import java.util.Date

fun PodcastEpisodeItem.Companion.fromEntity(item: PodcastEpisodeEntity): PodcastEpisodeItem {
    return PodcastEpisodeItem().apply {
        id = item.id.toLong()
        podcastId = item.seriesId.toLong()
        episodeNumber = item.episodeNumber
        title = item.title
        description = item.description
        duration = item.duration
        timeElapsed = 0
        finished = false
        dateGmt = Date(item.publishedAt.timeMillis).asGMTString()
        mp3Url = item.mp3Url
        imageUrl = item.imageUrl
        permalinkUrl = item.permalinkUrl
        moreEpisodesCount = 0
        tracks = arrayListOf()
        isDownloaded = false
        isUserFeed = false
        numberOfComments = item.numberOfComments
    }
}