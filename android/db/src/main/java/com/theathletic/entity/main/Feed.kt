package com.theathletic.entity.main

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.squareup.moshi.JsonClass
import com.theathletic.data.LocalModel
import com.theathletic.data.RemoteModel
import com.theathletic.entity.local.AthleticEntity
import java.io.Serializable
import java.util.UUID

// TT FeedResponse - Root class for feed responses
@Entity(tableName = "feed_response", primaryKeys = ["feedId"])
data class FeedResponse(
    var feedId: String = "",
    var metadata: ResponseMetadata = ResponseMetadata(0L),
    @Ignore var feed: MutableList<FeedItem> = mutableListOf(),
    var metadataJSON: String = "",
    var localChangeTimestamp: String = ""
) : RemoteModel, LocalModel {

    val allEntities get() = feed.flatMap { it.entities } +
        feed.flatMap { it.compoundEntities.flatten() }
}

data class ResponseMetadata(
    var lastRefreshed: Long?
)

// TT FeedItem - Represents one item in a feed - can contain multiple entities (subitems)
@Entity(tableName = "feed_item", primaryKeys = ["id", "feedId"])
class FeedItem : Serializable {
    // we create own ID
    var id: String = UUID.randomUUID().toString()
    var feedId: String = "" // used as foreign key
    var feedSlug: String? = null
    var feedSportType: String? = null
    var feedLeagueName: String? = null
    var adUnitPath: String? = null

    /**
     * PageIndex contains the calculated index within a given page of an API result.
     * This value resets to 0 for every page and should not be used for analytics tracking.
     */
    @ColumnInfo(name = "index")
    var pageIndex: Long = -1L
    var page: Int = 0
    var hasNextPage: Boolean = true
    var tertiaryGroup: TertiaryGroup? = null

    var itemType: FeedItemType = FeedItemType.UNKNOWN
    var style: FeedItemStyle = FeedItemStyle.UNKNOWN

    var title: String = ""
    var description: String = ""
    var titleImageUrl: String = ""

    var action: FeedItemAction? = null

    var container: String? = null

    var entityCuratedTitles = mutableMapOf<AthleticEntity.Id, String?>()
    var entityCuratedImageUrls = mutableMapOf<AthleticEntity.Id, String?>()
    var entityCuratedDescriptions = mutableMapOf<AthleticEntity.Id, String?>()
    var entityCuratedDisplayOrder = mutableMapOf<AthleticEntity.Id, Short?>()

    var entityIds: List<AthleticEntity.Id> = mutableListOf()

    @Ignore
    var entities: List<AthleticEntity> = mutableListOf()
        set(value) {
            field = value
            entityIds = value.map { it.entityId }
        }

    var compoundEntityIds: List<List<AthleticEntity.Id>> = mutableListOf()

    @Ignore
    var compoundEntities: List<List<AthleticEntity>> = mutableListOf()
        set(value) {
            field = value
            compoundEntityIds = value.map { list -> list.map { it.entityId } }
        }

    private fun isContentTheSame(other: Any?): Boolean = when {
        other !is FeedItem -> false
        pageIndex != other.pageIndex -> false
        itemType != other.itemType -> false
        style != other.style -> false
        title != other.title -> false
        description != other.description -> false
        else -> {
            var isTheSame = true
            for ((idx, entity) in entities.withIndex()) {
                if (entity != other.entities.getOrNull(idx)) {
                    isTheSame = false
                }
            }
            isTheSame
        }
    }

    override fun equals(other: Any?) = when {
        this === other -> true
        other !is FeedItem -> false
        id != other.id -> false
        else -> isContentTheSame(other)
    }

    override fun hashCode() = id.hashCode()
}

@JsonClass(generateAdapter = true)
data class FeedItemAction(
    val actionText: String,
    val deeplink: String
)

enum class FeedItemType(val value: String) {
    CAROUSEL("carousel"),
    ROW("row"),
    UNKNOWN("unknown");

    companion object {
        fun from(value: String?): FeedItemType = values().firstOrNull { it.value == value }
            ?: UNKNOWN
    }
}

enum class FeedItemStyle {
    // row
    ARTICLE,
    ARTICLE_FEATURED,
    ARTICLE_FRANCHISE,
    IPM_ANNOUNCEMENT,
    DISCUSSION,
    LIVE_DISCUSSION,
    PODCAST_EPISODE,
    HEADLINE_LIST,
    HEADLINE,
    LIVE_ROOM,

    // carousel
    CAROUSEL_SCORES,
    CAROUSEL_LIVE_BLOGS,
    CAROUSEL_TOPICS,
    CAROUSEL_EVERGREEN,
    CAROUSEL_RECOMMENDED_PODCASTS,

    // Scores
    SCORES_BOX_SCORE,

    // Game Feed
    GAME_FEED_LIVE_BLOG_HEADER,
    GAME_FEED_LIVE_BLOG_POST,
    GAME_FEED_BRIEF,

    // Feed Types
    FEED_THREE_FOUR_CONTENT,
    ONE_HERO,
    TWO_HERO,
    THREE_HERO,
    FOUR_HERO,
    FIVE_HERO,
    SIX_HERO,
    SEVEN_PLUS_HERO,
    TOPPER,
    FOUR_FIVE_GALLERY,
    SIX_PLUS_GALLERY,
    SPOTLIGHT,
    FRONTPAGE_MOST_POPULAR_ARTICLES,
    FRONTPAGE_INSIDERS_CAROUSEL,

    DROPZONE,

    UNKNOWN;
}

@JsonClass(generateAdapter = true)
data class TertiaryGroup(
    val id: String = "",
    val title: String = "",
    val subtitle: String? = null,
    val position: Int = 0,

    val legacyGrouping: LegacyGrouping? = null
) {
    enum class LegacyGrouping {
        DAY,
        WEEK,
        MONTH,
    }
}