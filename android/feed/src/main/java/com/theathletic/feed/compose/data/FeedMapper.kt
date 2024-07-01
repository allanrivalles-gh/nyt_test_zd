package com.theathletic.feed.compose.data

import com.theathletic.datetime.Datetime
import com.theathletic.entity.authentication.UserPrivilegeLevel
import com.theathletic.entity.feed.LocalA1
import com.theathletic.entity.feed.LocalArticle
import com.theathletic.entity.feed.LocalDropzone
import com.theathletic.entity.feed.LocalFeaturedGame
import com.theathletic.entity.feed.LocalFeed
import com.theathletic.entity.feed.LocalGame
import com.theathletic.entity.feed.LocalHeadline
import com.theathletic.entity.feed.LocalLiveBlog
import com.theathletic.entity.feed.LocalPodcastEpisode
import com.theathletic.entity.feed.LocalScoresGame
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.scores.data.local.GameCoverageType
import com.theathletic.scores.data.local.GameState
import java.util.UUID

internal fun LocalFeed.toDomain(isUserASubscriber: Boolean): Feed {

    val layouts = layouts.mapIndexedNotNull { index, localLayout ->

        val type = when (localLayout.layoutType) {
            "one_content" -> Layout.Type.ONE_CONTENT_CURATED
            "four_content" -> Layout.Type.FOUR_CONTENT_CURATED
            "one_content_curated" -> Layout.Type.ONE_CONTENT_CURATED
            "two_content_curated" -> Layout.Type.TWO_CONTENT_CURATED
            "three_hero_curation" -> Layout.Type.THREE_HERO_CURATION
            "five_hero_curation" -> Layout.Type.FIVE_HERO_CURATION
            "six_hero_curation" -> Layout.Type.SIX_HERO_CURATION
            "seven_plus_hero_curation" -> Layout.Type.SEVEN_PLUS_HERO_CURATION
            "highlight_three_content" -> Layout.Type.HIGHLIGHT_THREE_CONTENT
            "four_content_curated" -> Layout.Type.FOUR_CONTENT_CURATED
            "content_list_curated" -> Layout.Type.HEADLINE
            "end_cap" -> Layout.Type.FOR_YOU
            "most_popular_articles" -> Layout.Type.MOST_POPULAR
            "podcast_episodes_list" -> Layout.Type.MY_PODCASTS
            "spotlight" -> Layout.Type.A1
            "game" -> Layout.Type.FEATURE_GAME
            "scores" -> Layout.Type.SCORES
            "dropzone" -> Layout.Type.DROPZONE
            else -> return@mapIndexedNotNull null
        }

        val items = localLayout.items.toDomain(isUserASubscriber)

        layout(
            id = UUID.randomUUID().toString(),
            title = localLayout.title,
            icon = localLayout.icon,
            action = localLayout.action,
            deepLink = localLayout.deepLink,
            type = type,
            index = index,
            items = items
        )
    }

    return Feed(id, layouts, pageInfo.toDomain())
}

private fun LocalFeed.PageInfo.toDomain() = Feed.PageInfo(currentPage = currentPage, hasNextPage = hasNextPage)

internal fun List<AthleticEntity>.toDomain(isUserASubscriber: Boolean) = mapNotNull { it.toDomain(isUserASubscriber) }

private fun AthleticEntity.toDomain(isUserASubscriber: Boolean): Layout.Item? {
    return when (this) {
        is LocalA1 -> toDomain()
        is LocalArticle -> toDomain()
        is LocalLiveBlog -> toDomain()
        is LocalHeadline -> toDomain()
        is LocalPodcastEpisode -> toDomain()
        is LocalFeaturedGame -> toDomain()
        is LocalScoresGame -> toDomain(isUserASubscriber)
        is LocalDropzone -> toDomain()
        else -> null
    }
}

internal fun LocalArticle.toDomain(): Article {
    val author = Author(
        firstName = author.firstName,
        lastName = author.lastName
    )

    return Article(
        id = id,
        title = title,
        excerpt = excerpt,
        image = image,
        author = author,
        commentCount = commentCount,
        isBookmarked = isBookmarked,
        isRead = isRead,
        publishedAt = null,
        startedAt = Datetime(startedAt ?: 0),
        endedAt = Datetime(endedAt ?: Long.MAX_VALUE),
        permalink = permalink,
        postTypeId = postTypeId
    )
}

internal fun LocalLiveBlog.toDomain(): LiveBlog {
    return LiveBlog(
        id = id,
        title = title,
        description = description,
        image = image,
        isLive = isLive,
        permalink = permalink,
        lastActivity = Datetime(lastActivity)
    )
}

const val POST_ID_ARTICLE = "1"
const val POST_ID_DISCUSSION = "29"
const val POST_ID_Q_AND_A = "31"

internal fun LocalHeadline.toDomain(): Headline {
    return Headline(
        id = id,
        title = title,
        permalink = permalink,
        image = image
    )
}

internal fun LocalA1.toDomain(): A1 {
    val article = article
    val authors = authors.map {
        A1.Author(
            name = it.name,
            picture = it.picture
        )
    }

    return A1(
        id = id,
        article = article.toDomain(),
        authors = authors,
        createdAt = Datetime(createdAt),
        updatedAt = Datetime(updatedAt),
        type = contentType
    )
}

internal fun LocalPodcastEpisode.toDomain(): PodcastEpisodeItem {
    return PodcastEpisodeItem(
        id = id,
        title = title,
        description = description,
        image = image,
        publishedAt = Datetime(publishedAt),
        duration = duration,
        permalink = permalink
    )
}

internal fun LocalDropzone.toDomain(): Dropzone {
    return Dropzone(id = dropzoneId, unitPath = unitPath)
}

internal fun LocalFeaturedGame.toDomain(): FeaturedGameItem {
    return FeaturedGameItem(
        id = id,
        gameTitle = gameTitle,
        seriesTitle = seriesTitle,
        game = game.toFeaturedGameDomain(),
        gameLinks = gameLinks.map {
            FeaturedGameItem.GameLink(
                label = it.label,
                appLink = it.appLink
            )
        },
        relatedContent = toRelatedContentDomain(),
    )
}

private fun LocalGame.toFeaturedGameDomain() = GameItem(
    id = id,
    permalink = permalink,
    scheduledAt = scheduledAt?.let { Datetime(it) },
    timeTBD = timeTBD,
    state = state,
    gameStatus = GameItem.GameStatus(
        main = gameStatus.main,
        extra = gameStatus.extra
    ),
    firstTeam = firstTeam.toFeaturedGameTeamDomain(),
    secondTeam = secondTeam.toFeaturedGameTeamDomain(),
    sport = sport,
    teamWithPossession = teamWithPossession,
    relatedGameScheduledAt = relatedGameScheduledAt?.let { Datetime(it) }
)

private fun LocalScoresGame.toDomain(isUserASubscriber: Boolean) = ScoresCarouselItem(
    id = game.id,
    scrollIndex = scrollIndex,
    game = game.toDomain(isUserASubscriber)
)

private fun LocalGame.toDomain(isUserASubscriber: Boolean) = ScoresCarouselItem.Game(
    id = id,
    permalink = permalink,
    scheduledAt = scheduledAt?.let { Datetime(it) },
    timeTBD = timeTBD,
    state = state,
    statusDisplay = ScoresCarouselItem.StatusDisplay(
        main = gameStatus.main,
        extra = gameStatus.extra
    ),
    firstTeam = firstTeam.toDomain(showTeamAsLost(forFirstTeam = true)),
    secondTeam = secondTeam.toDomain(showTeamAsLost(forFirstTeam = false)),
    hasLiveDiscussion = hasLiveDiscussion(isUserASubscriber)
)

private fun LocalGame.showTeamAsLost(forFirstTeam: Boolean): Boolean {
    return when {
        state != GameState.FINAL -> false
        firstTeam.score == secondTeam.score -> false
        firstTeam.score < secondTeam.score && forFirstTeam -> true
        firstTeam.score > secondTeam.score && forFirstTeam.not() -> true
        else -> false
    }
}

private fun LocalGame.hasLiveDiscussion(isUserASubscriber: Boolean): Boolean {
    val hasLiveDiscussion = coverage.contains(GameCoverageType.DISCOVERABLE_COMMENTS)
    val hasTeamSpecificNavigation = coverage.contains(GameCoverageType.COMMENTS_NAVIGATION)
    return hasLiveDiscussion && hasTeamSpecificNavigation && isUserASubscriber
}

private fun LocalGame.LocalTeam.toFeaturedGameTeamDomain() = GameItem.Team(
    id = id,
    teamId = teamId,
    alias = alias,
    primaryColor = primaryColor,
    accentColor = accentColor,
    logoUrl = logoUrl,
    score = score,
    currentRecord = currentRecord,
    penaltyScore = penaltyScore,
    aggregatedScore = aggregatedScore,
    lastSixGames = lastSixGames
)

private fun LocalGame.LocalTeam.toDomain(showTeamAsLost: Boolean) = ScoresCarouselItem.Team(
    alias = alias,
    logoUrl = logoUrl,
    score = score,
    penaltyScore = penaltyScore,
    lost = showTeamAsLost
)

private fun LocalFeaturedGame.toRelatedContentDomain(): FeaturedGameItem.RelatedContent? {
    relatedContent?.let { content ->
        return FeaturedGameItem.RelatedContent.ArticleLiveBlog(
            id = content.id,
            title = content.title,
            imageUrl = content.imageUrl,
            permalink = content.permalink,
            authors = content.authors.map {
                FeaturedGameItem.RelatedContent.RelatedContentAuthor(it.firstName, it.lastName)
            }
        )
    }
    relatedComment?.let { comment ->
        return FeaturedGameItem.RelatedContent.TopComment(
            id = comment.id,
            permalink = comment.permalink,
            author = FeaturedGameItem.RelatedContent.CommentAuthor(
                name = comment.authorName,
                avatarUrl = comment.authorAvatarUrl,
                isStaff = comment.authorUserLevel > UserPrivilegeLevel.REGULAR_USER.value,
                gameFlairName = comment.authorGameFlairName,
                gameFlairColor = comment.authorGameFlairColor
            ),
            comment = comment.comment,
            commentedAt = Datetime(comment.commentedAt)
        )
    }
    return null
}