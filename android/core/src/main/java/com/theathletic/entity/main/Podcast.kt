package com.theathletic.entity.main

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import com.theathletic.data.LocalModel
import com.theathletic.extension.ObservableString
import com.theathletic.ui.UiModel
import java.io.Serializable

enum class PodcastTopicEntryType(val value: String) {
    LEAGUE("league"),
    CHANNEL("channel"),
    USER("user"),
    UNKNOWN("unknown");

    companion object {
        fun from(value: String?): PodcastTopicEntryType = values().firstOrNull { it.value == value } ?: UNKNOWN
    }
}

// TT Podcast League Response
@Entity(tableName = "podcast_league_feed", primaryKeys = ["id"])
data class PodcastLeagueFeed(
    @SerializedName("id") var id: Long = 0L,
    @SerializedName("national") var national: List<PodcastItem>,
    @SerializedName("local") var teams: List<PodcastItem>
) : Serializable

// TT Podcast Feed Response
@Entity(tableName = "podcast_feed", primaryKeys = ["id"])
data class PodcastFeed(
    @SerializedName("id") var id: Long = 0L,
    @SerializedName("featured_podcasts") var featuredPodcasts: List<PodcastItem>,
    @SerializedName("user_podcast_episodes") @Ignore var userPodcastEpisodes: MutableList<PodcastEpisodeItem>,
    @SerializedName("recommended_podcasts") var recommendedPodcasts: List<PodcastItem>,
    @SerializedName("discover") var browse: List<PodcastTopic>
) : LocalModel {
    constructor(id: Long, recommendedPodcasts: List<PodcastItem>, browse: List<PodcastTopic>) : this(id, listOf<PodcastItem>(), arrayListOf<PodcastEpisodeItem>(), recommendedPodcasts, browse)
}

// TT Podcast Item
@Entity(tableName = "podcast_item", primaryKeys = ["id"])
class PodcastItem : LocalModel {
    @SerializedName("id")
    var id: Long = -1L

    @SerializedName("topic_ids")
    var topicIds: MutableList<String> = mutableListOf()

    @SerializedName("title")
    var title: String = ""

    @SerializedName("description")
    var description: String? = ""

    @SerializedName("image_url")
    var imageUrl: String? = ""

    @SerializedName("permalink_url")
    var permalinkUrl: String? = ""

    @SerializedName("metadata_string")
    var metadataString: String? = ""

    @SerializedName("is_following")
    var isFollowing: Boolean = false

    @SerializedName("episodes")
    @Ignore
    var episodes: MutableList<PodcastEpisodeItem> = arrayListOf()

    @Ignore
    var badge: ObservableInt = ObservableInt(0)
}

// TT Podcast Episode Base Item
open class PodcastEpisodeBaseItem : Serializable

// TT Podcast Episode Item
@Entity(tableName = "podcast_episode", primaryKeys = ["id"])
class PodcastEpisodeItem : PodcastEpisodeBaseItem(), UiModel {

    companion object;

    override val stableId: String
        get() = "PodcastEpisodeItem:$id"

    @SerializedName("id")
    var id: Long = -1L

    @SerializedName("podcast_id")
    var podcastId: Long = -1L

    @SerializedName("episode_number")
    var episodeNumber: Int = -1

    @SerializedName("title")
    var title: String = ""

    @SerializedName("description")
    var description: String? = ""

    @SerializedName("duration")
    var duration: Long = -1L

    @SerializedName("time_elapsed")
    var timeElapsed: Int = -1

    @SerializedName("more_episodes_count")
    var moreEpisodesCount: Int = 0

    @SerializedName("finished")
    var finished: Boolean = false
        set(value) {
            field = value || field
            observableIsFinished.set(value || field)
        }

    @SerializedName("date_gmt")
    var dateGmt: String = ""

    @SerializedName("disable_comments")
    var commentsDisabled: Boolean = false

    @SerializedName("locked_comments")
    var commentsLocked: Boolean = false

    @SerializedName("num_comments")
    var numberOfComments: Int = 0

    @SerializedName("mp3_url")
    var mp3Url: String = ""

    @SerializedName("image_url")
    var imageUrl: String? = ""

    @SerializedName("permalink_url")
    var permalinkUrl: String? = ""

    @SerializedName("is_downloaded")
    var isDownloaded: Boolean = false

    // Unused variable. Can be removed!
    @SerializedName("is_user_feed")
    var isUserFeed: Boolean = false

    @SerializedName("is_teaser")
    var isTeaser: Boolean = false

    @SerializedName("tracks")
    var tracks: List<PodcastEpisodeDetailTrackItem> = arrayListOf()

    @SerializedName("stories")
    var stories: List<PodcastEpisodeDetailStoryItem> = arrayListOf()

    @Transient
    var downloadProgress = ObservableInt(-1)

    @Transient
    var observableIsFinished = ObservableBoolean(finished)
}

// TT Podcast Episode Detail Base Item
abstract class PodcastEpisodeDetailBaseItem : Serializable

// TT Podcast Episode Detail Header Item, it just wraps the underlying episode to provide description info
data class PodcastEpisodeDetailHeaderItem(
    val episode: PodcastEpisodeItem,
    var showFullDescription: Boolean = false
) :
    PodcastEpisodeDetailBaseItem()

// TT Podcast Episode Track Item
class PodcastEpisodeDetailTrackItem : PodcastEpisodeDetailBaseItem() {
    @SerializedName("id")
    var id: Long = -1L

    @SerializedName("title")
    var title: String = ""

    @SerializedName("description")
    var description: String? = ""

    @SerializedName("start_position")
    var startPosition: Long = -1L

    // We need to create temporary variable for endPosition parsing, as backend can return null here.
    @SerializedName("end_position")
    var endPositionNullable: Long? = null

    val endPosition: Long
        get() = endPositionNullable ?: (startPosition + duration)

    @SerializedName("track_number")
    var trackNumber: Long = -1L

    @SerializedName("duration")
    var duration: Long = -1L

    @SerializedName("permalink_url")
    var permalink: String = ""

    @Transient
    var isCurrentlyPlayingTrack = ObservableBoolean(false)
}

// TT Podcast Episode Story Item
class PodcastEpisodeDetailStoryItem : PodcastEpisodeDetailBaseItem() {
    @SerializedName("article_id")
    var id: Long = 0

    @SerializedName("post_type_id")
    var postTypeId: Long = 0

    @SerializedName("title")
    var title: String? = ""

    @SerializedName("excerpt")
    var excerpt: String? = ""

    @SerializedName("datetime_gmt")
    var datetimeGmt: String = ""

    @SerializedName("img_url")
    var imgUrl: String = ""

    @SerializedName("heading")
    var heading: String = ""

    @SerializedName("heading_type")
    var headingType: String = ""
}

// TT Podcast Episode Story Divider Item
class PodcastEpisodeDetailStoryDividerItem : PodcastEpisodeDetailBaseItem()

// TT Podcast Topic
class PodcastTopic : Serializable {
    @SerializedName("id")
    var id: Long = -1L

    @SerializedName("name")
    var name: String = ""

    @SerializedName("type")
    var type: PodcastTopicEntryType = PodcastTopicEntryType.UNKNOWN

    @SerializedName("image_url")
    var imageUrl: String = ""

    @SerializedName("team_hex")
    var teamHex: String = ""
}

class PodcastTrack : BaseObservable() {
    companion object {
        fun fromEpisode(item: PodcastEpisodeItem): PodcastTrack {
            val track = PodcastTrack()
            track.id = item.id
            track.podcastId = item.podcastId
            track.episodeId = item.id
            track.title = item.title
            track.description = item.description ?: ""
            track.duration = item.duration
            track.url = item.mp3Url
            track.permalinkUrl = item.permalinkUrl ?: ""
            track.imageUrl = item.imageUrl ?: ""
            track.currentProgressMs = item.timeElapsed * 1000
            return track
        }
    }

    var id: Long = 0L
    var podcastId: Long = 0L
    var episodeId: Long = 0L
    var title: String = ""
    var description: String = ""
    var duration: Long = -1L
    var url: String = ""
    var permalinkUrl: String = ""
    var imageUrl: String = ""
    var bitmapKey: ObservableString = ObservableString()
    var currentProgressMs: Int = 0
}

/**
 * Used by [PodcastDownloadService] to store information about current download progress
 *
 * [progress] var is used to store information about the state of the download:
 *     -1 -> not downloaded
 *     >= 0 -> download in progress
 *     100 -> item downloaded
 */
class PodcastDownloadEntity {
    var podcastEpisodeId: Long = 0L
    var podcastEpisodeName: String = ""
    var downloadId: Long = -1L
    var progress: Long = -1L
        private set

    fun isDownloaded() = progress == 100L

    fun isDownloading(): Boolean = progress in 0L..99

    fun markAsNotDownloaded() {
        progress = -1L
    }

    fun markAsDownloaded() {
        progress = 100L
    }

    fun setProgress(progress: Long) {
        this.progress = progress
    }
}