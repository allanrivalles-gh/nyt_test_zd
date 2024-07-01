package com.theathletic.podcast.data.remote

import com.google.gson.annotations.SerializedName
import com.theathletic.data.RemoteModel
import com.theathletic.entity.main.PodcastTopicEntryType

data class PodcastFeedRemote(
    @SerializedName("id") var id: Long = 0L,
    @SerializedName("featured_podcasts") var featuredPodcasts: List<PodcastRemote>,
    @SerializedName("user_podcast_episodes")
    var userPodcastEpisodes: List<PodcastEpisodeRemote>,
    @SerializedName("recommended_podcasts") var recommendedPodcasts: List<PodcastRemote>,
    @SerializedName("discover") var browse: List<PodcastTopicRemote>
) : RemoteModel

data class PodcastRemote(
    var id: Long = -1L,
    @SerializedName("topic_ids") var topicIds: List<String> = emptyList(),
    var title: String = "",
    var description: String? = "",
    @SerializedName("image_url") var imageUrl: String? = "",
    @SerializedName("permalink_url") var permalinkUrl: String? = "",
    @SerializedName("metadata_string") var metadataString: String? = "",
    @SerializedName("is_following") var isFollowing: Boolean = false,
    @SerializedName("notif_episodes_on") var notifyEpisodes: Boolean = false,
    var episodes: List<PodcastEpisodeRemote> = emptyList()
) : RemoteModel

data class PodcastEpisodeRemote(
    var id: Long = -1L,
    @SerializedName("podcast_id") var podcastId: Long = -1L,
    @SerializedName("podcast_title") var podcastTitle: String? = "",
    @SerializedName("number") var episodeNumber: Int = -1,
    var title: String = "",
    var description: String? = "",
    var duration: Long = -1L,
    @SerializedName("time_elapsed") var timeElapsed: Int = -1,
    @SerializedName("more_episodes_count") var moreEpisodesCount: Int = 0,
    var finished: Boolean = false,
    @SerializedName("date_gmt") var dateGmt: String = "",
    @SerializedName("disable_comments") var commentsDisabled: Boolean = false,
    @SerializedName("locked_comments") var commentsLocked: Boolean = false,
    @SerializedName("num_comments") var numberOfComments: Int = 0,
    @SerializedName("mp3_url") var mp3Url: String = "",
    @SerializedName("image_url") var imageUrl: String? = "",
    @SerializedName("permalink_url") var permalinkUrl: String? = "",
    @SerializedName("is_downloaded") var isDownloaded: Boolean = false,
    // Unused variable. Can be removed!
    @SerializedName("is_user_feed") var isUserFeed: Boolean = false,
    @SerializedName("is_teaser") var isTeaser: Boolean = false,
    var tracks: List<PodcastEpisodeDetailTrackRemote> = emptyList(),
    var stories: List<PodcastEpisodeDetailStoryRemote> = emptyList()
) : RemoteModel

data class PodcastEpisodeDetailTrackRemote(
    var id: Long = -1L,
    var title: String = "",
    var description: String? = "",
    @SerializedName("start_position") var startPosition: Long = -1L,
    @SerializedName("end_position") var endPosition: Long? = null,
    @SerializedName("track_number") var trackNumber: Long = -1L,
    var duration: Long = -1L,
    @SerializedName("permalink_url") var permalink: String = ""
) : RemoteModel

data class PodcastEpisodeDetailStoryRemote(
    @SerializedName("article_id") var id: Long = 0,
    @SerializedName("post_type_id") var postTypeId: Long = 0,
    var title: String? = "",
    var excerpt: String? = "",
    @SerializedName("datetime_gmt") var datetimeGmt: String = "",
    @SerializedName("img_url") var imgUrl: String = "",
    var heading: String = "",
    @SerializedName("heading_type") var headingType: String = ""
) : RemoteModel

data class PodcastTopicRemote(
    @SerializedName("id") var id: Long = -1L,
    @SerializedName("name") var name: String = "",
    @SerializedName("type") var type: PodcastTopicEntryType = PodcastTopicEntryType.UNKNOWN,
    @SerializedName("image_url") var imageUrl: String = "",
    @SerializedName("team_hex") var teamHex: String = ""
) : RemoteModel