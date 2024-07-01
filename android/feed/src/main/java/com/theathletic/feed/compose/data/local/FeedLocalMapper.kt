package com.theathletic.feed.compose.data.local

import com.theathletic.FeedLiveGamesSubscription
import com.theathletic.NewFeedQuery
import com.theathletic.entity.feed.LocalA1
import com.theathletic.entity.feed.LocalArticle
import com.theathletic.entity.feed.LocalDropzone
import com.theathletic.entity.feed.LocalFeaturedGame
import com.theathletic.entity.feed.LocalFeed
import com.theathletic.entity.feed.LocalGame
import com.theathletic.entity.feed.LocalHeadline
import com.theathletic.entity.feed.LocalLayout
import com.theathletic.entity.feed.LocalLiveBlog
import com.theathletic.entity.feed.LocalPodcastEpisode
import com.theathletic.entity.feed.LocalScoresGame
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.feed.compose.data.FeedRequest
import com.theathletic.fragment.A1Content
import com.theathletic.fragment.ArticleContent
import com.theathletic.fragment.FeaturedGameContent
import com.theathletic.fragment.FeedGameAmericanFootball
import com.theathletic.fragment.FeedGameBaseball
import com.theathletic.fragment.FeedGameBasketball
import com.theathletic.fragment.FeedGameGenericTeam
import com.theathletic.fragment.FeedGameHockey
import com.theathletic.fragment.FeedGameSoccer
import com.theathletic.fragment.FeedGameSoccerTeam
import com.theathletic.fragment.GameContent
import com.theathletic.fragment.HeadlineContent
import com.theathletic.fragment.LiveBlogContent
import com.theathletic.fragment.PodcastEpisodeContent
import com.theathletic.fragment.TeamLite
import com.theathletic.gamedetail.data.remote.toLocal
import com.theathletic.scores.data.local.GameState
import com.theathletic.scores.data.remote.toAvailableCoverage
import com.theathletic.type.GameStatusCode
import com.theathletic.utility.safeLet
import java.util.UUID

internal fun NewFeedQuery.FeedMulligan.toLocal(feedRequest: FeedRequest): LocalFeed {
    val layouts = layouts.mapNotNull { remoteLayout ->
        val items = if (remoteLayout.type != "dropzone") {
            remoteLayout.contents.toLocal()
        } else {
            remoteLayout.dropzone_id
                ?.let { dropzoneId ->
                    listOf(
                        LocalDropzone(
                            id = UUID.randomUUID().toString(),
                            dropzoneId = dropzoneId,
                            unitPath = ad_unit_path,
                        )
                    )
                }
                ?: return@mapNotNull null
        }
        LocalLayout(
            items = items,
            title = remoteLayout.title?.app_text ?: "",
            icon = remoteLayout.tag?.image_url ?: "",
            action = remoteLayout.action?.raw_string ?: "",
            deepLink = remoteLayout.action?.app_linked_string ?: "",
            layoutType = remoteLayout.type
        )
    }

    return LocalFeed(id = feedRequest.key, layouts, pageInfo.toLocal())
}

private fun NewFeedQuery.PageInfo.toLocal() = LocalFeed.PageInfo(currentPage = currentPage, hasNextPage = hasNextPage)

internal fun List<NewFeedQuery.Content>.toLocal() = mapNotNull { it.toLocal() }

private fun NewFeedQuery.Content.toLocal(): AthleticEntity? {
    val consumable = asConsumableV2 ?: return null
    val fragments = consumable.fragments
    return when (consumable.type) {
        "article" -> createLocalArticle(fragments.articleContent)
        "liveBlog" -> createLocalLiveBlog(fragments.liveBlogContent)
        "news" -> createLocalHeadline(fragments.headlineContent)
        "podcast_episode" -> createPodcastEpisode(fragments.podcastEpisodeContent)
        "spotlight" -> createLocalA1(fragments.a1Content)
        "featured_feed_game" -> createFeaturedGame(fragments.featuredGameContent)
        "game" -> createScoresGame(fragments.gameContent)
        else -> null
    }
}

internal fun createLocalArticle(content: ArticleContent?): LocalArticle? {
    val article = content ?: return null
    val author = LocalArticle.Author(
        firstName = article.author?.first_name ?: "",
        lastName = article.author?.last_name ?: ""
    )

    return LocalArticle(
        id = article.consumable_id,
        title = article.title,
        excerpt = article.excerpt,
        image = article.image_uri ?: "",
        author = author,
        commentCount = article.comments_count ?: 0,
        isBookmarked = article.is_saved,
        isRead = article.is_read,
        publishedAt = null,
        startedAt = article.ended_at,
        endedAt = article.ended_at,
        permalink = article.permalink,
        postTypeId = article.post_type_id ?: ""
    )
}

internal fun createLocalA1(content: A1Content?): LocalA1? {
    val a1 = content ?: return null
    val article = a1.article ?: return null
    val authors = article.authors.map {
        LocalA1.Author(
            name = it.author.fragments.author.name,
            picture = it.author.fragments.author.asStaff?.avatar_uri ?: ""
        )
    }

    return LocalA1(
        id = a1.consumable_id,
        article = article.toLocal(),
        authors = authors,
        createdAt = content.created_at,
        updatedAt = content.updated_at,
        contentType = content.type
    )
}

internal fun createLocalLiveBlog(content: LiveBlogContent?): LocalLiveBlog? {
    val liveBlog = content ?: return null
    val isLive = liveBlog.status == "live"

    return LocalLiveBlog(
        id = liveBlog.consumable_id,
        title = liveBlog.title,
        description = liveBlog.description ?: "",
        image = liveBlog.image_uri ?: "",
        isLive = isLive,
        permalink = liveBlog.permalink,
        lastActivity = liveBlog.last_activity_at
    )
}

internal fun createLocalHeadline(headlineContent: HeadlineContent?): LocalHeadline? {
    val headline = headlineContent ?: return null
    return LocalHeadline(
        id = headline.consumable_id,
        title = headline.title,
        permalink = headline.permalink,
        image = headline.image_uri ?: ""
    )
}

internal fun A1Content.Article.toLocal(): LocalArticle {
    val author = LocalArticle.Author(author.first_name, author.last_name)
    return LocalArticle(
        id = id,
        title = title,
        excerpt = excerpt,
        author = author,
        commentCount = comment_count,
        image = image_uri ?: "",
        isRead = is_read ?: false,
        isBookmarked = is_saved ?: false,
        startedAt = null,
        endedAt = null,
        publishedAt = published_at,
        postTypeId = post_type_id ?: "",
        permalink = permalink
    )
}

internal fun createPodcastEpisode(content: PodcastEpisodeContent?): LocalPodcastEpisode? {
    val podcast = content ?: return null

    return LocalPodcastEpisode(
        id = podcast.id,
        title = podcast.title,
        description = podcast.description ?: "",
        image = podcast.image_url ?: "",
        publishedAt = podcast.published_at,
        duration = podcast.duration ?: 0,
        permalink = podcast.permalink
    )
}

internal fun createFeaturedGame(content: FeaturedGameContent?): LocalFeaturedGame? {
    val featuredGame = content ?: return null
    val game = featuredGame.game?.toLocalModel() ?: return null
    return LocalFeaturedGame(
        id = game.id,
        gameTitle = featuredGame.toLocalTitle(game.gameTitle),
        seriesTitle = featuredGame.series_title,
        game = game,
        gameLinks = featuredGame.links?.mapNotNull { it.toLocalModel() } ?: emptyList(),
        relatedContent = featuredGame.featured_content?.toLocalRelatedContent(),
        relatedComment = featuredGame.featured_content?.toLocalRelatedComment()
    )
}

internal fun createScoresGame(content: GameContent?): LocalScoresGame? {
    val scoresGame = content ?: return null
    val game = scoresGame.game?.toLocalModel() ?: return null
    return LocalScoresGame(
        id = game.id,
        scrollIndex = scoresGame.index,
        game = game
    )
}

private fun GameContent.Game.toLocalModel(): LocalGame? {
    fragments.feedGameAmericanFootball?.let { return it.toLocalModel() }
    fragments.feedGameHockey?.let { return it.toLocalModel() }
    fragments.feedGameBasketball?.let { return it.toLocalModel() }
    fragments.feedGameBaseball?.let { return it.toLocalModel() }
    fragments.feedGameSoccer?.let { return it.toLocalModel() }
    return null
}

private fun FeaturedGameContent.toLocalTitle(gameTitle: String?): List<String> {
    val titleParts = game_title_parts?.mapNotNull { it } ?: emptyList()
    return when {
        titleParts.isNotEmpty() -> titleParts
        gameTitle != null -> listOf(gameTitle)
        else -> emptyList()
    }
}

private fun FeaturedGameContent.Game.toLocalModel(): LocalGame? {
    fragments.feedGameAmericanFootball?.let { return it.toLocalModel() }
    fragments.feedGameHockey?.let { return it.toLocalModel() }
    fragments.feedGameBasketball?.let { return it.toLocalModel() }
    fragments.feedGameSoccer?.let { return it.toLocalModel() }
    return null
}

fun FeedLiveGamesSubscription.LiveScoreUpdates.toLocalModel(): LocalGame? {
    fragments.feedGameAmericanFootball?.let { return it.toLocalModel() }
    fragments.feedGameHockey?.let { return it.toLocalModel() }
    fragments.feedGameBasketball?.let { return it.toLocalModel() }
    fragments.feedGameSoccer?.let { return it.toLocalModel() }
    return null
}

private fun FeedGameAmericanFootball.toLocalModel(): LocalGame? {
    val firstTeam = first_team?.fragments?.feedGameGenericTeam?.toLocalModel() ?: return null
    val secondTeam = second_team?.fragments?.feedGameGenericTeam?.toLocalModel() ?: return null
    return LocalGame(
        id = id,
        scheduledAt = scheduled_at,
        timeTBD = time_tbd ?: false,
        state = status.toLocalModel(),
        gameTitle = game_title,
        permalink = permalink,
        firstTeam = firstTeam,
        secondTeam = secondTeam,
        gameStatus = LocalGame.LocalGameStatus(
            main = game_status?.main,
            extra = game_status?.extra
        ),
        sport = sport.toLocal(),
        teamWithPossession = possession?.team?.id,
        relatedGameScheduledAt = null,
        coverage = coverage?.available_data?.map { it.toAvailableCoverage() } ?: emptyList()
    )
}

private fun FeedGameHockey.toLocalModel(): LocalGame? {
    val firstTeam = first_team?.fragments?.feedGameGenericTeam?.toLocalModel() ?: return null
    val secondTeam = second_team?.fragments?.feedGameGenericTeam?.toLocalModel() ?: return null
    return LocalGame(
        id = id,
        scheduledAt = scheduled_at,
        timeTBD = time_tbd ?: false,
        state = status.toLocalModel(),
        gameTitle = game_title,
        permalink = permalink,
        firstTeam = firstTeam,
        secondTeam = secondTeam,
        gameStatus = LocalGame.LocalGameStatus(
            main = game_status?.main,
            extra = game_status?.extra
        ),
        sport = sport.toLocal(),
        relatedGameScheduledAt = null,
        coverage = coverage?.available_data?.map { it.toAvailableCoverage() } ?: emptyList()
    )
}

private fun FeedGameBasketball.toLocalModel(): LocalGame? {
    val firstTeam = first_team?.fragments?.feedGameGenericTeam?.toLocalModel() ?: return null
    val secondTeam = second_team?.fragments?.feedGameGenericTeam?.toLocalModel() ?: return null
    return LocalGame(
        id = id,
        scheduledAt = scheduled_at,
        timeTBD = time_tbd ?: false,
        state = status.toLocalModel(),
        gameTitle = game_title,
        permalink = permalink,
        firstTeam = firstTeam,
        secondTeam = secondTeam,
        gameStatus = LocalGame.LocalGameStatus(
            main = game_status?.main,
            extra = game_status?.extra
        ),
        sport = sport.toLocal(),
        relatedGameScheduledAt = null,
        coverage = coverage?.available_data?.map { it.toAvailableCoverage() } ?: emptyList()
    )
}

private fun FeedGameBaseball.toLocalModel(): LocalGame? {
    val firstTeam = first_team?.fragments?.feedGameGenericTeam?.toLocalModel() ?: return null
    val secondTeam = second_team?.fragments?.feedGameGenericTeam?.toLocalModel() ?: return null
    return LocalGame(
        id = id,
        scheduledAt = scheduled_at,
        timeTBD = time_tbd ?: false,
        state = status.toLocalModel(),
        gameTitle = game_title,
        permalink = permalink,
        firstTeam = firstTeam,
        secondTeam = secondTeam,
        gameStatus = LocalGame.LocalGameStatus(
            main = game_status?.main,
            extra = game_status?.extra
        ),
        sport = sport.toLocal(),
        relatedGameScheduledAt = null,
        coverage = coverage?.available_data?.map { it.toAvailableCoverage() } ?: emptyList()
    )
}

private fun FeedGameSoccer.toLocalModel(): LocalGame? {
    val firstTeam = first_team?.fragments?.feedGameSoccerTeam?.toLocalModel() ?: return null
    val secondTeam = second_team?.fragments?.feedGameSoccerTeam?.toLocalModel() ?: return null
    return LocalGame(
        id = id,
        scheduledAt = scheduled_at,
        timeTBD = time_tbd ?: false,
        state = status.toLocalModel(),
        gameTitle = game_title,
        permalink = permalink,
        firstTeam = firstTeam,
        secondTeam = secondTeam,
        gameStatus = LocalGame.LocalGameStatus(
            main = game_status?.main,
            extra = game_status?.extra
        ),
        sport = sport.toLocal(),
        relatedGameScheduledAt = related_game?.scheduled_at,
        coverage = coverage?.available_data?.map { it.toAvailableCoverage() } ?: emptyList()
    )
}

private fun FeedGameGenericTeam.toLocalModel(): LocalGame.LocalTeam {
    val teamLite = team?.fragments?.teamLite
    return LocalGame.LocalTeam(
        id = id,
        teamId = teamLite?.id,
        alias = teamLite?.alias,
        primaryColor = teamLite?.color_primary,
        accentColor = teamLite?.color_accent,
        logoUrl = teamLite?.logos?.toPreferredSize(),
        score = score ?: 0,
        currentRecord = current_record
    )
}

private fun FeedGameSoccerTeam.toLocalModel(): LocalGame.LocalTeam {
    val teamLite = team?.fragments?.teamLite
    return LocalGame.LocalTeam(
        id = id,
        teamId = teamLite?.id,
        alias = teamLite?.alias,
        primaryColor = teamLite?.color_primary,
        accentColor = teamLite?.color_accent,
        logoUrl = teamLite?.logos?.toPreferredSize(),
        score = score ?: 0,
        currentRecord = current_record,
        penaltyScore = penalty_score,
        aggregatedScore = aggregate_score,
        lastSixGames = last_six
    )
}

private fun FeaturedGameContent.Link.toLocalModel(): LocalFeaturedGame.LocalGameLink? {
    return safeLet(app_linked_string, raw_string) { safeAppLink, safeLabel ->
        LocalFeaturedGame.LocalGameLink(
            label = safeLabel,
            appLink = safeAppLink
        )
    }
}

private fun GameStatusCode?.toLocalModel() = when (this) {
    GameStatusCode.final -> GameState.FINAL
    GameStatusCode.in_progress -> GameState.LIVE
    GameStatusCode.cancelled -> GameState.CANCELED
    GameStatusCode.delayed -> GameState.DELAYED
    GameStatusCode.if_necessary -> GameState.IF_NECESSARY
    GameStatusCode.postponed -> GameState.POSTPONED
    GameStatusCode.suspended -> GameState.SUSPENDED
    else -> GameState.UPCOMING
}

private fun List<TeamLite.Logo>.toPreferredSize(): String? {
    val icon = sortedBy { it.fragments.logoFragment.width }
        .firstOrNull { it.fragments.logoFragment.width >= 72 } ?: lastOrNull()
    return icon?.fragments?.logoFragment?.uri
}

private fun FeaturedGameContent.Featured_content.toLocalRelatedContent(): LocalFeaturedGame.LocalRelatedContent? {
    fragments.featuredGameArticle?.let { article ->
        return LocalFeaturedGame.LocalRelatedContent(
            id = article.id,
            title = article.title,
            imageUrl = article.image_uri,
            permalink = article.permalink,
            authors = listOf(
                LocalFeaturedGame.Author(article.author.first_name, article.author.last_name)
            ),
            type = article.type
        )
    }
    fragments.featuredGameLiveBlog?.let { liveBlog ->
        return LocalFeaturedGame.LocalRelatedContent(
            id = liveBlog.id,
            title = liveBlog.title,
            imageUrl = liveBlog.images.firstOrNull()?.image_uri,
            permalink = liveBlog.permalink,
            authors = liveBlog.byline_authors.map {
                LocalFeaturedGame.Author(it.first_name, it.last_name)
            },
            type = liveBlog.type
        )
    }
    return null
}

private fun FeaturedGameContent.Featured_content.toLocalRelatedComment(): LocalFeaturedGame.LocalRelatedComment? {
    fragments.featuredGameComment?.let { comment ->
        return LocalFeaturedGame.LocalRelatedComment(
            id = comment.id,
            type = comment.type,
            authorName = comment.comment.author_name,
            authorAvatarUrl = comment.comment.avatar_url,
            authorUserLevel = comment.comment.author_user_level,
            authorGameFlairName = comment.comment.author_game_flairs.firstOrNull()?.name,
            authorGameFlairColor = comment.comment.author_game_flairs.firstOrNull()?.icon_contrast_color,
            comment = comment.comment.comment,
            permalink = comment.comment.comment_permalink,
            commentedAt = comment.comment.commented_at
        )
    }
    return null
}