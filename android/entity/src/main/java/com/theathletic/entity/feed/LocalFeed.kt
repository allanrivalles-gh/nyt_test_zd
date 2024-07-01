package com.theathletic.entity.feed

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.main.Sport
import com.theathletic.scores.data.local.GameCoverageType
import com.theathletic.scores.data.local.GameState

@JsonClass(generateAdapter = true)
data class LocalFeed(
    @field:Json(name = "id") override val id: String,
    @field:Json(name = "layouts") val layouts: List<LocalLayout>,
    @field:Json(name = "pageInfo") val pageInfo: PageInfo,
) : AthleticEntity {
    override val type: AthleticEntity.Type = AthleticEntity.Type.FEED

    @JsonClass(generateAdapter = true)
    data class PageInfo(
        @field:Json(name = "currentPage") val currentPage: Int,
        @field:Json(name = "hasNextPage") val hasNextPage: Boolean,
    )
}

@JsonClass(generateAdapter = true)
data class LocalLayout(
    @field:Json(name = "title") val title: String,
    @field:Json(name = "icon") val icon: String,
    @field:Json(name = "action") val action: String,
    @field:Json(name = "deepLink") val deepLink: String,
    @field:Json(name = "layoutType") val layoutType: String,
    @Transient val items: List<AthleticEntity> = emptyList(),
    @field:Json(name = "itemIds") val itemIds: List<String> = items.map { it.entityId.toString() }
) {
    @Transient val entityIds = itemIds.mapNotNull { AthleticEntity.Id.parse(it) }
}

@JsonClass(generateAdapter = true)
data class LocalArticle(
    @field:Json(name = "id") override val id: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "excerpt") val excerpt: String,
    @field:Json(name = "image") val image: String,
    @field:Json(name = "author") val author: Author,
    @field:Json(name = "commentCount") val commentCount: Int,
    @field:Json(name = "isSaved") val isBookmarked: Boolean,
    @field:Json(name = "isRead") val isRead: Boolean,
    @field:Json(name = "publishedAt") val publishedAt: Long?,
    @field:Json(name = "startedAt") val startedAt: Long?,
    @field:Json(name = "endedAt") val endedAt: Long?,
    @field:Json(name = "permalink") val permalink: String,
    @field:Json(name = "postTypeId") val postTypeId: String
) : AthleticEntity {
    override val type: AthleticEntity.Type = AthleticEntity.Type.FEED_ARTICLE

    @JsonClass(generateAdapter = true)
    data class Author(val firstName: String, val lastName: String)
}

@JsonClass(generateAdapter = true)
data class LocalLiveBlog(
    @field:Json(name = "id") override val id: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "image") val image: String,
    @field:Json(name = "isLive") val isLive: Boolean,
    @field:Json(name = "permalink") val permalink: String,
    @field:Json(name = "lastActivity") val lastActivity: Long
) : AthleticEntity {
    override val type: AthleticEntity.Type = AthleticEntity.Type.FEED_LIVE_BLOG
}

@JsonClass(generateAdapter = true)
data class LocalA1(
    @field:Json(name = "id") override val id: String,
    @field:Json(name = "article") val article: LocalArticle,
    @field:Json(name = "authors") val authors: List<Author>,
    @field:Json(name = "createdAt") val createdAt: Long,
    @field:Json(name = "updatedAt") val updatedAt: Long,
    @field:Json(name = "contentType") val contentType: String
) : AthleticEntity {
    override val type: AthleticEntity.Type = AthleticEntity.Type.FEED_A1

    @JsonClass(generateAdapter = true)
    data class Author(val name: String, val picture: String)
}

@JsonClass(generateAdapter = true)
data class LocalHeadline(
    @field:Json(name = "id") override val id: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "image") val image: String,
    @field:Json(name = "permalink") val permalink: String
) : AthleticEntity {
    override val type: AthleticEntity.Type = AthleticEntity.Type.FEED_HEADLINE
}

@JsonClass(generateAdapter = true)
data class LocalPodcastEpisode(
    @field:Json(name = "id") override val id: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "image") val image: String,
    @field:Json(name = "publishedAt") val publishedAt: Long,
    @field:Json(name = "duration") val duration: Int,
    @field:Json(name = "permalink") val permalink: String
) : AthleticEntity {
    override val type: AthleticEntity.Type = AthleticEntity.Type.FEED_PODCAST_EPISODE
}

@JsonClass(generateAdapter = true)
data class LocalFeaturedGame(
    @field:Json(name = "id") override val id: String,
    @field:Json(name = "gameTitle") val gameTitle: List<String>,
    @field:Json(name = "seriesTitle") val seriesTitle: String?,
    @field:Json(name = "game") val game: LocalGame,
    @field:Json(name = "gameLinks") val gameLinks: List<LocalGameLink>,
    @field:Json(name = "relatedContent") val relatedContent: LocalRelatedContent? = null,
    @field:Json(name = "relatedComment") val relatedComment: LocalRelatedComment? = null,
) : AthleticEntity {
    override val type: AthleticEntity.Type = AthleticEntity.Type.FEED_FEATURED_GAME

    @JsonClass(generateAdapter = true)
    data class LocalRelatedContent(
        val id: String,
        val title: String,
        val imageUrl: String?,
        val permalink: String,
        val type: String,
        val authors: List<Author>
    )

    @JsonClass(generateAdapter = true)
    data class LocalRelatedComment(
        val id: String,
        val type: String,
        val authorName: String,
        val authorAvatarUrl: String?,
        val authorUserLevel: Int,
        val authorGameFlairName: String?,
        val authorGameFlairColor: String?,
        val comment: String,
        val permalink: String?,
        val commentedAt: Long
    )

    @JsonClass(generateAdapter = true)
    data class Author(
        val firstName: String,
        val lastName: String
    )

    @JsonClass(generateAdapter = true)
    data class LocalGameLink(
        val label: String,
        val appLink: String
    )
}

@JsonClass(generateAdapter = true)
data class LocalScoresGame(
    @field:Json(name = "id") override val id: String,
    @field:Json(name = "scrollIndex") val scrollIndex: Int,
    @field:Json(name = "game") val game: LocalGame
) : AthleticEntity {
    override val type: AthleticEntity.Type = AthleticEntity.Type.FEED_GAME
}

@JsonClass(generateAdapter = true)
data class LocalGame(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "permalink") val permalink: String?,
    @field:Json(name = "scheduledAt") val scheduledAt: Long?,
    @field:Json(name = "timeTDB") val timeTBD: Boolean,
    @field:Json(name = "state") val state: GameState = GameState.UPCOMING,
    @field:Json(name = "gameStatus") val gameStatus: LocalGameStatus,
    @field:Json(name = "gameTitle") val gameTitle: String?,
    @field:Json(name = "firstTeam") val firstTeam: LocalTeam,
    @field:Json(name = "secondTeam") val secondTeam: LocalTeam,
    @field:Json(name = "sport") val sport: Sport,
    @field:Json(name = "teamWithPossession") val teamWithPossession: String? = null,
    @field:Json(name = "relatedGameScheduledAt") val relatedGameScheduledAt: Long? = null,
    @field:Json(name = "coverage") val coverage: List<GameCoverageType> = emptyList()
) {

    @JsonClass(generateAdapter = true)
    data class LocalGameStatus(
        val main: String?,
        val extra: String?
    )

    @JsonClass(generateAdapter = true)
    data class LocalTeam(
        val id: String,
        val teamId: String?,
        val alias: String?,
        val primaryColor: String?,
        val accentColor: String?,
        val logoUrl: String?,
        val score: Int,
        val currentRecord: String?,
        val penaltyScore: Int? = null,
        val aggregatedScore: Int? = null,
        val lastSixGames: String? = null
    )
}

@JsonClass(generateAdapter = true)
data class LocalDropzone(
    @field:Json(name = "id") override val id: String,
    @field:Json(name = "dropzoneId") val dropzoneId: String,
    @field:Json(name = "unitPath") val unitPath: String?,
) : AthleticEntity {
    override val type: AthleticEntity.Type = AthleticEntity.Type.FEED_DROPZONE
}