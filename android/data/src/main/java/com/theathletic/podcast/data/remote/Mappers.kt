package com.theathletic.podcast.data.remote

import com.theathletic.datetime.Datetime
import com.theathletic.datetime.parseDateFromGMT
import com.theathletic.entity.main.PodcastEpisodeDetailStoryItem
import com.theathletic.entity.main.PodcastEpisodeDetailTrackItem
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.PodcastFeed
import com.theathletic.entity.main.PodcastItem
import com.theathletic.entity.main.PodcastTopic
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.local.PodcastSeriesEntity

fun PodcastFeedRemote.toDbModel(): PodcastFeed {
    return PodcastFeed(
        id = id,
        featuredPodcasts = featuredPodcasts.map { it.toDbModel() },
        userPodcastEpisodes = userPodcastEpisodes.map { it.toDbModel() }.toMutableList(),
        recommendedPodcasts = recommendedPodcasts.map { it.toDbModel() },
        browse = browse.map { it.toDbModel() }
    )
}

fun PodcastRemote.toDbModel(): PodcastItem {
    return PodcastItem().also { dbModel ->
        dbModel.id = id
        dbModel.topicIds = topicIds.toMutableList()
        dbModel.title = title
        dbModel.description = description
        dbModel.imageUrl = imageUrl
        dbModel.permalinkUrl = permalinkUrl
        dbModel.metadataString = metadataString
        dbModel.isFollowing = isFollowing
        dbModel.episodes = episodes.map { it.toDbModel() }.toMutableList()
    }
}

fun PodcastRemote.toEntity(): PodcastSeriesEntity {
    return PodcastSeriesEntity(
        id = id.toString(),
        title = title,
        subtitle = description.orEmpty(),
        imageUrl = imageUrl.orEmpty(),
        isFollowing = isFollowing,
        notifyEpisodes = notifyEpisodes
    )
}

fun PodcastEpisodeRemote.toDbModel(): PodcastEpisodeItem {
    return PodcastEpisodeItem().also { dbModel ->
        dbModel.id = id
        dbModel.podcastId = podcastId
        dbModel.title = title
        dbModel.description = description
        dbModel.duration = duration
        dbModel.timeElapsed = timeElapsed
        dbModel.moreEpisodesCount = moreEpisodesCount
        dbModel.finished = finished
        dbModel.dateGmt = dateGmt
        dbModel.commentsDisabled = commentsDisabled
        dbModel.commentsLocked = commentsLocked
        dbModel.numberOfComments = numberOfComments
        dbModel.mp3Url = mp3Url
        dbModel.imageUrl = imageUrl
        dbModel.permalinkUrl = permalinkUrl
        dbModel.isDownloaded = isDownloaded
        dbModel.isUserFeed = isUserFeed
        dbModel.isTeaser = isTeaser
        dbModel.tracks = tracks.map { it.toDbModel() }
        dbModel.stories = stories.map { it.toDbModel() }
    }
}

fun PodcastEpisodeRemote.toEntity(): PodcastEpisodeEntity {
    return PodcastEpisodeEntity(
        id = id.toString(),
        episodeNumber = episodeNumber,
        seriesId = podcastId.toString(),
        seriesTitle = podcastTitle.orEmpty(),
        title = title,
        description = description.orEmpty(),
        duration = duration,
        timeElapsedMs = timeElapsed.toLong() * 1000,
        mp3Url = mp3Url,
        imageUrl = imageUrl.orEmpty(),
        permalinkUrl = permalinkUrl.orEmpty(),
        isFinished = finished,
        publishedAt = Datetime(parseDateFromGMT(dateGmt).time)
    )
}

fun PodcastEpisodeItem.toEntity(): PodcastEpisodeEntity {
    return PodcastEpisodeEntity(
        id = id.toString(),
        episodeNumber = episodeNumber,
        seriesId = podcastId.toString(),
        seriesTitle = "",
        title = title,
        description = description.orEmpty(),
        duration = duration,
        timeElapsedMs = timeElapsed.toLong() * 1000,
        mp3Url = mp3Url,
        imageUrl = imageUrl.orEmpty(),
        permalinkUrl = permalinkUrl.orEmpty(),
        isFinished = finished,
        publishedAt = Datetime(parseDateFromGMT(dateGmt).time)
    )
}

fun PodcastEpisodeDetailTrackRemote.toDbModel(): PodcastEpisodeDetailTrackItem {
    return PodcastEpisodeDetailTrackItem().also { dbModel ->
        dbModel.id = id
        dbModel.title = title
        dbModel.description = description
        dbModel.startPosition = startPosition
        dbModel.endPositionNullable = endPosition
        dbModel.trackNumber = trackNumber
        dbModel.duration = duration
        dbModel.permalink = permalink
    }
}

fun PodcastEpisodeDetailStoryRemote.toDbModel(): PodcastEpisodeDetailStoryItem {
    return PodcastEpisodeDetailStoryItem().also { dbModel ->
        dbModel.id = id
        dbModel.postTypeId = postTypeId
        dbModel.title = title
        dbModel.excerpt = excerpt
        dbModel.datetimeGmt = datetimeGmt
        dbModel.imgUrl = imgUrl
        dbModel.heading = heading
        dbModel.headingType = headingType
    }
}

fun PodcastTopicRemote.toDbModel(): PodcastTopic {
    return PodcastTopic().also { dbModel ->
        dbModel.id = id
        dbModel.name = name
        dbModel.type = type
        dbModel.imageUrl = imageUrl
        dbModel.teamHex = teamHex
    }
}