@file:Suppress("LongParameterList")

package com.theathletic.feed.compose.data

import com.theathletic.datetime.Datetime
import com.theathletic.entity.main.Sport
import com.theathletic.scores.data.local.GameState

internal data class Feed(val id: String, val layouts: List<Layout>, val pageInfo: PageInfo) {
    data class PageInfo(val currentPage: Int, val hasNextPage: Boolean)
}

internal interface Layout {
    val id: String
    val title: String
    val icon: String
    val action: String
    val deepLink: String
    val type: Type
    val index: Int
    val items: List<Item>

    interface Item {
        val id: String
    }

    fun copy(
        id: String = this.id,
        title: String = this.title,
        icon: String = this.icon,
        action: String = this.action,
        deepLink: String = this.deepLink,
        type: Type = this.type,
        index: Int = this.index,
        items: List<Item> = this.items
    ) = layout(
        id,
        title,
        icon,
        action,
        deepLink,
        type,
        index,
        items
    )

    enum class Type {
        ONE_CONTENT_CURATED,
        TWO_CONTENT_CURATED,
        FOUR_CONTENT_CURATED,

        THREE_HERO_CURATION,
        FOUR_HERO_CURATION,
        FIVE_HERO_CURATION,
        SIX_HERO_CURATION,
        SEVEN_PLUS_HERO_CURATION,

        A1,
        HIGHLIGHT_THREE_CONTENT,

        HEADLINE,
        FOR_YOU,
        TOPPER,
        MOST_POPULAR,
        MY_PODCASTS,

        FEATURE_GAME,
        SCORES,

        DROPZONE,
    }
}

internal fun layout(
    id: String,
    title: String,
    icon: String = "",
    action: String = "",
    deepLink: String = "",
    type: Layout.Type,
    index: Int,
    items: List<Layout.Item> = emptyList(),
) = object : Layout {
    override val id: String = id
    override val title: String = title
    override val icon: String = icon
    override val action = action
    override val deepLink: String = deepLink
    override val type: Layout.Type = type
    override val index: Int = index
    override val items: List<Layout.Item> = items
}

internal data class TopperHeroLayout(private val layout: Layout) : Layout by layout
internal data class HeroListLayout(private val layout: Layout) : Layout by layout
internal data class HeroCarouselLayout(private val layout: Layout) : Layout by layout
internal data class ListLayout(private val layout: Layout) : Layout by layout
internal data class MostPopularLayout(private val layout: Layout) : Layout by layout
internal data class MyPodcastLayout(private val layout: Layout) : Layout by layout
internal data class DropzoneLayout(private val layout: Layout) : Layout by layout {
    override val items: List<Dropzone> = layout.items.mapNotNull { it as? Dropzone }
}

internal data class HeadlineLayout(private val layout: Layout) : Layout by layout
internal data class A1Layout(private val layout: Layout) : Layout by layout
internal data class FeaturedGameLayout(private val layout: Layout) : Layout by layout {
    override val items: List<FeaturedGameItem> = layout.items.mapNotNull { it as? FeaturedGameItem }
}
internal data class ScoresCarouselLayout(private val layout: Layout) : Layout by layout {
    override val items: List<ScoresCarouselItem> = layout.items.mapNotNull { it as? ScoresCarouselItem }
}

internal data class Article(
    override val id: String,
    val title: String,
    val excerpt: String,
    val image: String,
    val author: Author,
    val commentCount: Int,
    val isBookmarked: Boolean,
    val isRead: Boolean,
    val publishedAt: Datetime?,
    val startedAt: Datetime?,
    val endedAt: Datetime?,
    val permalink: String,
    private val postTypeId: String
) : Layout.Item {

    fun getPostType(now: Datetime = Datetime(0)): PostType = when (postTypeId) {
        POST_ID_ARTICLE -> PostType.ARTICLE
        POST_ID_DISCUSSION -> PostType.DISCUSSION
        POST_ID_Q_AND_A -> getQAndAPostType(now)
        else -> PostType.ARTICLE
    }

    private fun getQAndAPostType(now: Datetime = Datetime(0)) = when {
        isLive(now) -> PostType.Q_AND_A_LIVE
        isUpcoming(now) -> PostType.Q_AND_A_UPCOMING
        else -> PostType.Q_AND_A_RECAP
    }

    private fun isLive(now: Datetime) = if (startedAt != null && endedAt != null) {
        now in startedAt..endedAt
    } else {
        false
    }

    private fun isUpcoming(now: Datetime): Boolean = if (startedAt != null) now < startedAt else false
}

enum class PostType {
    ARTICLE,
    DISCUSSION,
    Q_AND_A_LIVE,
    Q_AND_A_UPCOMING,
    Q_AND_A_RECAP
}

internal data class A1(
    override val id: String,
    val article: Article,
    val authors: List<Author>,
    val createdAt: Datetime,
    val updatedAt: Datetime,
    val type: String
) : Layout.Item {
    data class Author(val name: String, val picture: String)
}

internal data class LiveBlog(
    override val id: String,
    val title: String,
    val description: String,
    val image: String,
    val isLive: Boolean,
    val permalink: String,
    val lastActivity: Datetime
) : Layout.Item

internal data class Headline(
    override val id: String,
    val title: String,
    val image: String,
    val permalink: String
) : Layout.Item

internal data class PodcastEpisodeItem(
    override val id: String,
    val title: String,
    val description: String,
    val image: String,
    val publishedAt: Datetime,
    val duration: Int,
    val permalink: String
) : Layout.Item

internal data class FeaturedGameItem(
    override val id: String,
    val gameTitle: List<String>?,
    val seriesTitle: String?,
    val game: GameItem,
    val gameLinks: List<GameLink>,
    val relatedContent: RelatedContent?
) : Layout.Item {
    data class GameLink(
        val label: String,
        val appLink: String
    )

    sealed class RelatedContent {
        data class ArticleLiveBlog(
            val id: String,
            val title: String,
            val imageUrl: String?,
            val permalink: String,
            val authors: List<RelatedContentAuthor>
        ) : RelatedContent()

        data class TopComment(
            val id: String,
            val permalink: String?,
            val author: CommentAuthor,
            val comment: String,
            val commentedAt: Datetime
        ) : RelatedContent()

        data class RelatedContentAuthor(
            val firstName: String,
            val lastName: String
        )

        data class CommentAuthor(
            val name: String,
            val avatarUrl: String?,
            val isStaff: Boolean,
            val gameFlairName: String?,
            val gameFlairColor: String?
        )
    }
}

data class GameItem(
    override val id: String,
    val permalink: String?,
    val scheduledAt: Datetime?,
    val timeTBD: Boolean,
    val state: GameState,
    val gameStatus: GameStatus,
    val firstTeam: Team,
    val secondTeam: Team,
    val sport: Sport,
    val teamWithPossession: String?,
    val relatedGameScheduledAt: Datetime?
) : Layout.Item {

    data class Team(
        val id: String,
        val teamId: String?,
        val alias: String?,
        val primaryColor: String?,
        val accentColor: String?,
        val logoUrl: String?,
        val score: Int?,
        val currentRecord: String?,
        val penaltyScore: Int?,
        val aggregatedScore: Int?,
        val lastSixGames: String?
    )

    data class GameStatus(
        val main: String?,
        val extra: String?
    )
}

internal data class Dropzone(override val id: String, val unitPath: String?) : Layout.Item

internal data class Author(val firstName: String, val lastName: String)

internal data class FeedRequest(
    val feedType: FeedType,
    val feedId: Int? = null,
    val feedUrl: String? = null,
    val filter: FeedFilter? = null,
    val locale: String? = null
) {
    val key = listOf(feedType.name, feedId?.toString(), feedUrl)
        .mapNotNull { it }
        .joinToString("-")
}

internal enum class FeedType {
    AUTHOR,
    FOLLOWING,
    LEAGUE,
    DISCOVER,
    PLAYER,
    TAG,
    TEAM,
    TEAM_PODCASTS
}

internal enum class ConsumableType {
    ACTION,
    ANNOUNCEMENT,
    ARTICLE,
    BRIEF,
    DISCUSSION,
    DROPZONE,
    EVERGREEN,
    FEATURED_FEED_GAME,
    FEED_GAME,
    GAME,
    GAME_V2,
    INSIDER,
    LIVEBLOG,
    LIVEBLOGBANNER,
    LIVEROOM,
    NEWS,
    PODCAST,
    PODCAST_EPISODE,
    QANDA,
    SPOTLIGHT,
    STATS_SCORES,
    TEAM_WIDGETS,
    TOPIC
}

internal data class FeedFilter(
    val layouts: Map<Layout.Type, List<ConsumableType>>
)