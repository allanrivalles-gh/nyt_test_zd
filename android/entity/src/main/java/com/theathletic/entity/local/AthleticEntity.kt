package com.theathletic.entity.local

import com.theathletic.article.data.local.InsiderEntity
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.article.TrendingTopicsEntity
import com.theathletic.entity.chat.ChatRoomEntity
import com.theathletic.entity.feed.LocalA1
import com.theathletic.entity.feed.LocalArticle
import com.theathletic.entity.feed.LocalDropzone
import com.theathletic.entity.feed.LocalFeaturedGame
import com.theathletic.entity.feed.LocalFeed
import com.theathletic.entity.feed.LocalHeadline
import com.theathletic.entity.feed.LocalLiveBlog
import com.theathletic.entity.feed.LocalScoresGame
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.feed.data.local.AnnouncementEntity
import com.theathletic.headline.data.local.HeadlineEntity
import com.theathletic.liveblog.data.local.LiveBlogEntity
import com.theathletic.liveblog.data.local.LiveBlogPostEntity
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.local.PodcastSeriesEntity
import com.theathletic.scores.data.local.BoxScoreEntity
import com.theathletic.utility.safeValueOf
import kotlin.reflect.KClass

interface AthleticEntity {
    val id: String
    val type: Type

    val entityId get() = Id(id, type)

    enum class Type {
        // Keep alphabetical
        ANNOUNCEMENT,
        ARTICLE,
        BOX_SCORE,
        CHAT_ROOM,
        HEADLINE,
        INSIDER,
        LIVE_AUDIO_ROOM,
        LIVE_BLOG,
        LIVE_BLOG_POST,
        PODCAST_EPISODE,
        PODCAST_SERIES,
        TRENDING_TOPIC,

        // Feed
        FEED,
        FEED_ARTICLE,
        FEED_A1,
        FEED_LIVE_BLOG,
        FEED_HEADLINE,
        FEED_PODCAST_EPISODE,
        FEED_FEATURED_GAME,
        FEED_GAME,
        FEED_DROPZONE,

        UNKNOWN;
    }

    /**
     * A simple representation for an entity which is a combination of its type and id. A example
     * id would look like:
     *
     * ARTICLE:412351234
     *
     * This allows us to store entities on feed items by storing lists of IDs. We can then parse
     * those IDs and query from the entity DB table with the id and type.
     */
    data class Id(
        val id: String,
        val type: Type
    ) {
        override fun toString() = "$type:$id"

        companion object {
            fun parse(value: String): Id? {
                if (value.isEmpty()) return null

                val (type, id) = value.split(":")
                val entityType = safeValueOf<Type>(type) ?: return null

                return Id(id = id, type = entityType)
            }
        }
    }
}

val AthleticEntity.Type.typeToEntity
    get() = when (this) {
        AthleticEntity.Type.ANNOUNCEMENT -> AnnouncementEntity::class
        AthleticEntity.Type.ARTICLE -> ArticleEntity::class
        AthleticEntity.Type.BOX_SCORE -> BoxScoreEntity::class
        AthleticEntity.Type.CHAT_ROOM -> ChatRoomEntity::class
        AthleticEntity.Type.HEADLINE -> HeadlineEntity::class
        AthleticEntity.Type.INSIDER -> InsiderEntity::class
        AthleticEntity.Type.LIVE_AUDIO_ROOM -> LiveAudioRoomEntity::class
        AthleticEntity.Type.LIVE_BLOG -> LiveBlogEntity::class
        AthleticEntity.Type.LIVE_BLOG_POST -> LiveBlogPostEntity::class
        AthleticEntity.Type.PODCAST_EPISODE -> PodcastEpisodeEntity::class
        AthleticEntity.Type.PODCAST_SERIES -> PodcastSeriesEntity::class
        AthleticEntity.Type.TRENDING_TOPIC -> TrendingTopicsEntity::class

        // Feed
        AthleticEntity.Type.FEED -> LocalFeed::class
        AthleticEntity.Type.FEED_ARTICLE -> LocalArticle::class
        AthleticEntity.Type.FEED_A1 -> LocalA1::class
        AthleticEntity.Type.FEED_LIVE_BLOG -> LocalLiveBlog::class
        AthleticEntity.Type.FEED_HEADLINE -> LocalHeadline::class
        AthleticEntity.Type.FEED_FEATURED_GAME -> LocalFeaturedGame::class
        AthleticEntity.Type.FEED_GAME -> LocalScoresGame::class
        AthleticEntity.Type.FEED_DROPZONE -> LocalDropzone::class

        else -> null
    }

val <T : AthleticEntity> KClass<T>.entityToType
    get() = when (this) {
        AnnouncementEntity::class -> AthleticEntity.Type.ANNOUNCEMENT
        ArticleEntity::class -> AthleticEntity.Type.ARTICLE
        BoxScoreEntity::class -> AthleticEntity.Type.BOX_SCORE
        ChatRoomEntity::class -> AthleticEntity.Type.CHAT_ROOM
        HeadlineEntity::class -> AthleticEntity.Type.HEADLINE
        InsiderEntity::class -> AthleticEntity.Type.INSIDER
        LiveAudioRoomEntity::class -> AthleticEntity.Type.LIVE_AUDIO_ROOM
        LiveBlogEntity::class -> AthleticEntity.Type.LIVE_BLOG
        LiveBlogPostEntity::class -> AthleticEntity.Type.LIVE_BLOG_POST
        PodcastEpisodeEntity::class -> AthleticEntity.Type.PODCAST_EPISODE
        PodcastSeriesEntity::class -> AthleticEntity.Type.PODCAST_SERIES
        TrendingTopicsEntity::class -> AthleticEntity.Type.TRENDING_TOPIC

        // Feed
        LocalFeed::class -> AthleticEntity.Type.FEED
        LocalArticle::class -> AthleticEntity.Type.FEED_ARTICLE
        LocalA1::class -> AthleticEntity.Type.FEED_A1
        LocalLiveBlog::class -> AthleticEntity.Type.FEED_LIVE_BLOG
        LocalHeadline::class -> AthleticEntity.Type.FEED_HEADLINE
        LocalFeaturedGame::class -> AthleticEntity.Type.FEED_FEATURED_GAME
        LocalScoresGame::class -> AthleticEntity.Type.FEED_GAME
        LocalDropzone::class -> AthleticEntity.Type.FEED_DROPZONE

        else -> AthleticEntity.Type.UNKNOWN
    }