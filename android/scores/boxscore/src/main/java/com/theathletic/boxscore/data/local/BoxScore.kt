package com.theathletic.boxscore.data.local

import com.theathletic.datetime.Datetime
import com.theathletic.podcast.ui.DownloadState
import com.theathletic.podcast.ui.PlaybackState

interface ModuleHeader {
    val id: String
}

interface Items {
    val id: String
}

interface BoxScoreModules {
    val id: String
    val header: ModuleHeader?
    val blocks: List<Items>
}

data class BoxScorePodcastState(
    val boxScore: BoxScore?,
    val podcastEpisodeId: String,
    val playbackState: PlaybackState
)

data class BoxScore(
    val id: String,
    val sections: List<Section>
)

data class BoxScorePodcastSync(
    val boxScore: BoxScore,
    val hasPodcastEpisodes: Boolean
)

data class Section(
    val id: String,
    val type: SectionType,
    val modules: List<BoxScoreModules>
)

data class LatestNewsModule(
    override val id: String,
    override val header: ModuleHeader?,
    override val blocks: List<Items>
) : BoxScoreModules

data class BasicHeader(
    override val id: String,
    val title: String
) : ModuleHeader

data class Article(
    override val id: String,
    val authors: String?,
    val commentCount: Int,
    val description: String?,
    val imageUri: String?,
    val permalink: String?,
    val title: String,
    val articleId: String,
    var isRead: Boolean,
    var isBookmarked: Boolean
) : Items

data class PodcastEpisode(
    override val id: String,
    val description: String?,
    val duration: Int?,
    val episodeId: String,
    var finished: Boolean?,
    val imageUrl: String?,
    val permalink: String,
    val podcastId: String,
    val podcastTitle: String?,
    val publishedAt: Datetime,
    val title: String,
    val mp3Url: String?,
    var playbackState: PlaybackState,
    var timeElapsed: Int?,
    val commentCount: Int,
    var downloadState: DownloadState,
    val clips: List<PodcastEpisodeClip>?
) : Items

data class PodcastEpisodeClip(
    val id: Int,
    val title: String?,
    val startPosition: Int?,
    val endPosition: Int?
)

enum class SectionType {
    GAME, UNKNOWN
}