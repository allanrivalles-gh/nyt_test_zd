package com.theathletic.entity.article

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import com.theathletic.data.LocalModel
import com.theathletic.data.RemoteModel
import com.theathletic.entity.EntityState
import com.theathletic.entity.TopicTagEntity
import com.theathletic.entity.discussions.CommentEntity
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.main.FeedItemEntryType

@JsonClass(generateAdapter = true)
data class ArticleEntity(
    @SerializedName("article_id")
    var articleId: Long = 0L,

    @SerializedName("article_title")
    var articleTitle: String? = null,

    @SerializedName("article_publish_date")
    var articlePublishDate: String? = null,

    @SerializedName("scheduled_date")
    var scheduledDate: String? = null,

    @SerializedName("author_id")
    var authorId: Long? = null,

    @SerializedName("author_img")
    var authorImg: String? = null,

    @SerializedName("author_name")
    var authorName: String? = null,

    @SerializedName("authors")
    var authors: List<String> = emptyList(),

    @SerializedName("author_status")
    var authorStatus: String? = null,

    var authorDescription: String? = null,

    @SerializedName("article_header_img")
    var articleHeaderImg: String? = null, // "https://cdn.theathletic.com/app/uploads/2018/10/30192629/Lombardi-David-Headshot-102918.jpg"

    @SerializedName("is_teaser")
    var isTeaser: Boolean = false,

    @SerializedName("subscriber_score")
    var subscriberScore: Double? = null,

    @SerializedName("team_urls")
    var teams: String? = null,

    @SerializedName("league_urls")
    var leagues: String? = null,

    @SerializedName("featured")
    var featured: Boolean = false,

    @SerializedName("entry_type")
    var entryType: FeedItemEntryType = FeedItemEntryType.UNKNOWN,

    @SerializedName("article_body")
    var articleBody: String? = null,

    var excerpt: String? = "",

    @SerializedName("comments")
    var comments: List<CommentEntity>? = emptyList(),

    @SerializedName("comments_count")
    var commentsCount: Long = 0L,

    val primaryTag: String? = null,

    @SerializedName("permalink")
    var permalink: String? = null,

    @SerializedName("disable_comments")
    var commentsDisabled: Boolean = false,

    @SerializedName("locked_comments")
    var commentsLocked: Boolean = false,

    @SerializedName("news_topics")
    var newsTopics: String? = null,

    @SerializedName("show_rating")
    var ratingEnabled: Boolean = false,

    @SerializedName("start_time_gmt")
    var startTimeGmt: String? = "",

    @SerializedName("end_time_gmt")
    var endTimeGmt: String? = "",

    @SerializedName("team_hex")
    var teamHex: String = "",

    @SerializedName("entity_tags")
    var entityTags: List<TopicTagEntity>? = emptyList(),

    @SerializedName("entity_keywords")
    var entityKeywords: String? = null,

    var relatedContent: MutableList<RelatedContent>? = null,

    @SerializedName("post_type_id")
    var postTypeId: Long = 0L,

    @SerializedName("league_shortname")
    var leagueShortname: String? = null,

    @SerializedName("sport_type")
    var sportType: String? = null,

    @SerializedName("league_url")
    var leagueUrl: String? = null,

    @SerializedName("ad_unit_path")
    var adUnitPath: String? = null,

    @SerializedName("ad_targeting_params")
    var adTargetingParams: Map<String, String?>? = null,

    // to store last scroll percentage of the article
    var lastScrollPercentage: Int? = null
) : RemoteModel, LocalModel, AthleticEntity {

    override val id: String get() = articleId.toString()

    override var type: AthleticEntity.Type = AthleticEntity.Type.ARTICLE

    val state: EntityState
        get() = if (articleBody.isNullOrEmpty()) EntityState.SUMMARY else EntityState.DETAIL
}

enum class ArticleRating(val value: Long) {
    MEH(1L),
    SOLID(2L),
    AWESOME(3L)
}

@JsonClass(generateAdapter = true)
data class RelatedContent(
    var id: String = "",
    var timestampGmt: String = "",
    var title: String = "",
    var excerpt: String = "",
    var imageUrl: String = "",
    var byline: String = "",
    var commentCount: Int = 0,
    var isLive: Boolean = false,
    var contentType: ContentType = ContentType.UNKNOWN

) {
    enum class ContentType {
        ARTICLE,
        HEADLINE,
        DISCUSSION,
        QANDA,
        LIVEBLOG,
        UNKNOWN
    }
}