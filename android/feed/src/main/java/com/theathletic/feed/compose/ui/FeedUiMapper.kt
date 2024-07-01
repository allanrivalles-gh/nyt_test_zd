package com.theathletic.feed.compose.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.TimeProvider
import com.theathletic.datetime.asGMTString
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.entity.main.Sport
import com.theathletic.featureswitch.Features
import com.theathletic.feed.R
import com.theathletic.feed.compose.data.A1
import com.theathletic.feed.compose.data.A1Layout
import com.theathletic.feed.compose.data.Article
import com.theathletic.feed.compose.data.Dropzone
import com.theathletic.feed.compose.data.DropzoneLayout
import com.theathletic.feed.compose.data.FeaturedGameItem
import com.theathletic.feed.compose.data.FeaturedGameLayout
import com.theathletic.feed.compose.data.Feed
import com.theathletic.feed.compose.data.GameItem
import com.theathletic.feed.compose.data.Headline
import com.theathletic.feed.compose.data.HeadlineLayout
import com.theathletic.feed.compose.data.HeroCarouselLayout
import com.theathletic.feed.compose.data.HeroListLayout
import com.theathletic.feed.compose.data.Layout
import com.theathletic.feed.compose.data.Layout.Type.FOUR_CONTENT_CURATED
import com.theathletic.feed.compose.data.Layout.Type.TOPPER
import com.theathletic.feed.compose.data.ListLayout
import com.theathletic.feed.compose.data.LiveBlog
import com.theathletic.feed.compose.data.MostPopularLayout
import com.theathletic.feed.compose.data.MyPodcastLayout
import com.theathletic.feed.compose.data.PodcastEpisodeItem
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.data.ScoresCarouselItem
import com.theathletic.feed.compose.data.ScoresCarouselLayout
import com.theathletic.feed.compose.data.TopperHeroLayout
import com.theathletic.feed.compose.data.displayScore
import com.theathletic.feed.compose.data.identifier
import com.theathletic.feed.compose.data.isTextDimmed
import com.theathletic.feed.compose.ui.ads.FeedAdsState
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.feed.compose.ui.analytics.container
import com.theathletic.feed.compose.ui.analytics.createAnalyticsData
import com.theathletic.feed.compose.ui.components.A1LayoutUiModel
import com.theathletic.feed.compose.ui.components.DropzoneLayoutUiModel
import com.theathletic.feed.compose.ui.components.FeaturedGameLayoutUiModel
import com.theathletic.feed.compose.ui.components.HeadlinesLayoutUiModel
import com.theathletic.feed.compose.ui.components.HeroCarouselLayoutUiModel
import com.theathletic.feed.compose.ui.components.HeroListLayoutUiModel
import com.theathletic.feed.compose.ui.components.ListLayoutUiModel
import com.theathletic.feed.compose.ui.components.LiveBlogUiModel
import com.theathletic.feed.compose.ui.components.MostPopularLayoutUiModel
import com.theathletic.feed.compose.ui.components.ScoresCarouselLayoutUiModel
import com.theathletic.feed.compose.ui.components.TopCommentUiModel
import com.theathletic.feed.compose.ui.components.TopperHeroLayoutUiModel
import com.theathletic.feed.compose.ui.components.podcast.FeedPodcastEpisodeUiModel
import com.theathletic.feed.compose.ui.components.podcast.PodcastLayoutUiModel
import com.theathletic.feed.compose.ui.formatter.ScoresCarouselItemFormatter
import com.theathletic.feed.compose.ui.items.A1UiModel
import com.theathletic.feed.compose.ui.items.DropzoneUiModel
import com.theathletic.feed.compose.ui.items.HeadlineUiModel
import com.theathletic.feed.compose.ui.items.featuredgame.FeaturedGameUiModel
import com.theathletic.feed.compose.ui.items.mostpopular.MostPopularItemUiModel
import com.theathletic.feed.compose.ui.items.scores.ScoresCarouselItemUiModel
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.feed.compose.ui.reusables.Image
import com.theathletic.feed.ui.models.SeeAllAnalyticsPayload
import com.theathletic.podcast.ui.DownloadState
import com.theathletic.podcast.ui.PlaybackState
import com.theathletic.scores.data.local.GameState
import com.theathletic.ui.ResourceString
import com.theathletic.ui.formatter.CountFormatter
import com.theathletic.ui.formatter.TimeAgoDateFormatter
import com.theathletic.ui.formatter.UpdatedTimeAgoDateFormatter
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.datetime.DateUtilityImpl
import com.theathletic.utility.orShortDash
import java.util.Date

internal class FeedUiMapper @AutoKoin constructor(
    private val timeProvider: TimeProvider,
    private val updatedTimeAgoFormatter: UpdatedTimeAgoDateFormatter,
    private val timeAgoDateFormatter: TimeAgoDateFormatter,
    private val countFormatter: CountFormatter,
    private val dateFormatter: DateUtility,
    private val localeUtility: LocaleUtility,
    private val features: Features,
    private val scoresCarouselItemFormatter: ScoresCarouselItemFormatter
) {
    fun toUiModel(feed: Feed, adsState: FeedAdsState): FeedUiModel = feed.mapToUiModel(adsState)

    private fun Feed.mapToUiModel(adsState: FeedAdsState): FeedUiModel {
        var scoresCarouselPosition = 0
        val layout = layouts.mapNotNull { layout ->
            when (layout) {
                is TopperHeroLayout -> layout.toUiModel()
                is HeroListLayout -> layout.toUiModel()
                is HeroCarouselLayout -> layout.toUiModel()
                is HeadlineLayout -> layout.toUiModel()
                is ListLayout -> layout.toUiModel()
                is MostPopularLayout -> layout.toUiModel()
                is MyPodcastLayout -> layout.toUiModel()
                is A1Layout -> layout.toUiModel()
                is FeaturedGameLayout -> layout.toUiModel()
                is ScoresCarouselLayout -> {
                    val carousel = layout.toUiModel()
                    scoresCarouselPosition = carousel.scrollIndex
                    carousel
                }
                is DropzoneLayout -> layout.toUiModel(adsState)
                else -> null
            }
        }
        return FeedUiModel(id, layout, pageInfo.toUiModel(), scoresCarouselPosition)
    }

    private fun TopperHeroLayout.toUiModel(): TopperHeroLayoutUiModel {
        val items = items.mapIndexedNotNull { itemIndex, item ->
            val container = if (itemIndex == 0) TOPPER.container else FOUR_CONTENT_CURATED.container
            val layoutIndex = if (itemIndex == 0) index else index + 1
            item.itemToUiModel(createAnalyticsData(container, layoutIndex = layoutIndex, verticalIndex = itemIndex - 1))
        }
        return TopperHeroLayoutUiModel(createLayout(items))
    }

    private fun HeroListLayout.toUiModel(): HeroListLayoutUiModel {
        val items = items.mapIndexedNotNull { index, item ->
            item.itemToUiModel(createAnalyticsData(verticalIndex = index - 1))
        }
        return HeroListLayoutUiModel(createLayout(items))
    }

    private fun HeroCarouselLayout.toUiModel(): HeroCarouselLayoutUiModel {
        val items = items.mapIndexedNotNull { index, item ->
            val verticalIndex = if (index == 0) 0 else 1
            item.itemToUiModel(createAnalyticsData(verticalIndex = verticalIndex, horizontalIndex = index - 1))
        }
        return HeroCarouselLayoutUiModel(createLayout(items))
    }

    private fun HeadlineLayout.toUiModel(): HeadlinesLayoutUiModel {
        val items = items.mapIndexedNotNull { index, item ->
            item.itemToUiModel(createAnalyticsData(verticalIndex = index))
        }
        return HeadlinesLayoutUiModel(createLayout(items))
    }

    private fun ListLayout.toUiModel(): ListLayoutUiModel {
        val items = items.mapIndexedNotNull { index, item ->
            item.itemToUiModel(createAnalyticsData(verticalIndex = index))
        }
        return ListLayoutUiModel(createLayout(items))
    }

    private fun MostPopularLayout.toUiModel(): MostPopularLayoutUiModel {
        val items = items.mapIndexedNotNull { index, item ->
            val analyticsData = createAnalyticsData(verticalIndex = index)
            (item as? Article)?.toMostPopular(analyticsData, position = index + 1)
        }
        return MostPopularLayoutUiModel(createLayout(items))
    }

    private fun MyPodcastLayout.toUiModel(): PodcastLayoutUiModel {
        val items = items.mapIndexedNotNull { index, item ->
            item.itemToUiModel(createAnalyticsData(verticalIndex = index))
        }
        return PodcastLayoutUiModel(createLayout(items))
    }

    private fun A1Layout.toUiModel(): A1LayoutUiModel {
        val items = items.mapIndexedNotNull { index, item ->
            item.itemToUiModel(createAnalyticsData(type.container, verticalIndex = index))
        }
        return A1LayoutUiModel(createLayout(items), SeeAllAnalyticsPayload(type.container, this.index))
    }

    private fun FeaturedGameLayout.toUiModel(): FeaturedGameLayoutUiModel {
        val items = items.mapIndexedNotNull { index, item ->
            item.itemToUiModel(createAnalyticsData(verticalIndex = index))
        }
        return FeaturedGameLayoutUiModel(createLayout(items))
    }

    private fun ScoresCarouselLayout.toUiModel(): ScoresCarouselLayoutUiModel {
        val items = items.mapIndexedNotNull { index, item ->
            item.itemToUiModel(createAnalyticsData(horizontalIndex = index))
        }
        return ScoresCarouselLayoutUiModel(createLayout(items))
    }

    private fun DropzoneLayout.toUiModel(adsState: FeedAdsState): DropzoneLayoutUiModel {
        val items = items.map { it.toUiModel(adsState) }
        return DropzoneLayoutUiModel(createLayout(items))
    }

    private fun Layout.createLayout(uiItems: List<LayoutUiModel.Item>): LayoutUiModel {
        return layoutUiModel(
            id = id,
            title = title,
            icon = icon,
            action = action,
            deepLink = deepLink,
            items = uiItems
        )
    }

    private fun Layout.Item.itemToUiModel(analyticsData: AnalyticsData): LayoutUiModel.Item? = when (this) {
        is A1 -> toUiModel(analyticsData)
        is Article -> toUiModel(analyticsData)
        is LiveBlog -> toUiModel(analyticsData)
        is Headline -> toUiModel(analyticsData)
        is PodcastEpisodeItem -> toUiModel()
        is FeaturedGameItem -> toUiModel(analyticsData)
        is ScoresCarouselItem -> toUiModel(analyticsData)
        else -> null
    }

    private fun Article.toUiModel(analyticsData: AnalyticsData) = ArticleUiModel(
        id = id,
        title = title,
        excerpt = excerpt,
        imageUrl = image,
        byline = "${author.firstName} ${author.lastName}",
        commentCount = countFormatter.formatCommentCount(commentCount),
        isBookmarked = isBookmarked,
        isRead = isRead,
        permalink = permalink,
        postType = getPostType(timeProvider.currentDatetime),
        analyticsData = analyticsData
    )

    private fun A1.toUiModel(analyticsData: AnalyticsData): A1UiModel {
        val publishDate = article.publishedAt?.let {
            DateUtilityImpl.formatGMTDate(it.timeMillis, DisplayFormat.MONTH_DATE_SHORT)
        } ?: ""

        return A1UiModel(
            id = id,
            imageUrl = article.image,
            title = article.title,
            permalink = article.permalink,
            commentCount = countFormatter.formatCommentCount(article.commentCount),
            isBookmarked = article.isBookmarked,
            isRead = article.isRead,
            byLine = "${article.author.firstName} ${article.author.lastName}",
            avatars = authors.map { it.picture },
            publishDate = publishDate,
            analyticsData = analyticsData
        )
    }

    private fun LiveBlog.toUiModel(analyticsData: AnalyticsData) = LiveBlogUiModel(
        id = id,
        title = title,
        description = description,
        imageUrl = image,
        isLive = isLive,
        lastActivity = updatedTimeAgoFormatter.format(lastActivity),
        permalink = permalink,
        analyticsData = analyticsData
    )

    private fun Headline.toUiModel(analyticsData: AnalyticsData) = HeadlineUiModel(
        id = id,
        title = title,
        image = image,
        permalink = permalink,
        analyticsData = analyticsData
    )

    private fun Article.toMostPopular(analyticsData: AnalyticsData, position: Int) = MostPopularItemUiModel(
        id = id,
        number = position.toString(),
        text = title,
        image = Image.RemoteImage(image),
        isRead = isRead,
        permalink = permalink,
        analyticsData = analyticsData
    )

    private fun PodcastEpisodeItem.toUiModel() = FeedPodcastEpisodeUiModel(
        id = id,
        podcastId = "", // todo Corey: This will need a podcast id
        date = DateUtilityImpl.formatPodcastDate(Date(this.publishedAt.timeMillis).asGMTString()),
        title = this.title,
        description = this.description,
        duration = DateUtilityImpl.formatPodcastDuration(this.publishedAt.timeMillis),
        progress = 0f,
        imageUrl = this.image,
        permalink = this.permalink,
        downloadState = DownloadState.NOT_DOWNLOADED, // TODO: Where do we get the proper value for this?
        playbackState = PlaybackState.None,
        analyticsData = null
    )

    private fun Dropzone.toUiModel(adsState: FeedAdsState) = DropzoneUiModel(
        id = id,
        adState = adsState.stateForAd(id),
    )

    private fun FeaturedGameItem.toUiModel(analyticsData: AnalyticsData): FeaturedGameUiModel = FeaturedGameUiModel(
        id = game.id,
        title = gameTitle.toTitle(seriesTitle),
        firstTeam = game.firstTeam.toUiModel(game.teamWithPossession),
        secondTeam = game.secondTeam.toUiModel(game.teamWithPossession),
        gameStatus = game.toGameState(),
        navLinks = gameLinks.toUiModel(),
        sport = game.sport,
        relatedContent = relatedContent?.toUiModel(analyticsData.copy(gameId = game.id)),
        permalink = game.permalink,
        analyticsData = analyticsData
    )

    private fun GameItem.Team.toUiModel(teamWithPossession: String?) = FeaturedGameUiModel.Team(
        id = id,
        alias = alias.orShortDash(),
        colors = accentColor ?: primaryColor,
        logoUrl = logoUrl,
        score = score.toString(),
        currentRecord = currentRecord,
        winLossRecord = lastSixGames,
        isWinLossReversed = localeUtility.isUnitedStatesOrCanada().not(),
        hasPossession = hasPossession(teamWithPossession)
    )

    private fun GameItem.Team.hasPossession(teamWithPossession: String?): Boolean {
        return teamId != null && teamId == teamWithPossession
    }

    private fun ScoresCarouselItem.toUiModel(analyticsData: AnalyticsData) = ScoresCarouselItemUiModel(
        id = id,
        permalink = game.permalink,
        analyticsData = analyticsData,
        rows = Pair(
            game.firstTeam.toUiModel(
                scoresCarouselItemFormatter.formatFirstStatusString(game),
                isStatusTextHighlighted = isFirstStatusTextHighlighted
            ),
            game.secondTeam.toUiModel(
                scoresCarouselItemFormatter.formatSecondStatusString(game),
            ),
        ),
        shouldHideScores = shouldHideScores,
        showCommentsButton = game.hasLiveDiscussion.showDiscussionForGame,
        scrollIndex = scrollIndex
    )

    private val Boolean.showDiscussionForGame: Boolean
        get() = this && features.isBoxScoresDiscussTabEnabled && features.areTeamSpecificCommentsEnabled

    private fun ScoresCarouselItem.Team?.toUiModel(
        statusString: ResourceString,
        isStatusTextHighlighted: Boolean = false,
    ) = ScoresCarouselItemUiModel.Row(
        logoUrl = this?.logoUrl,
        identifier = identifier,
        displayScore = displayScore,
        isTextDimmed = isTextDimmed,
        isStatusTextHighlighted = isStatusTextHighlighted,
        statusString = statusString,
    )

    private fun List<String>?.toTitle(seriesTitle: String?): String {
        return this?.joinToString(
            separator = "\n",
            postfix = if (seriesTitle != null) "\n$seriesTitle" else ""
        ) ?: (seriesTitle ?: "")
    }

    private fun GameItem.toGameState(): FeaturedGameUiModel.GameStatus {
        return FeaturedGameUiModel.GameStatus(
            state = state.toUiModel(),
            gameDate = scheduledAt?.let { at ->
                dateFormatter.formatGMTDate(at, DisplayFormat.WEEKDAY_MONTH_DATE_SHORT)
            }.orShortDash(),
            gameTime = scheduledAt?.let { at ->
                dateFormatter.formatGMTDate(at, DisplayFormat.HOURS_MINUTES)
            },
            period = gameStatus.main,
            clock = gameStatus.extra,
            aggregate = toAggregateScore()
        )
    }

    private fun List<FeaturedGameItem.GameLink>.toUiModel(): List<FeaturedGameUiModel.NavLink> {
        return map { link ->
            FeaturedGameUiModel.NavLink(label = link.label, appLink = link.appLink)
        }
    }

    private fun GameState.toUiModel() = when (this) {
        GameState.FINAL -> FeaturedGameUiModel.GameState.POSTGAME
        GameState.LIVE -> FeaturedGameUiModel.GameState.LIVE_GAME
        else -> FeaturedGameUiModel.GameState.PREGAME
    }

    private fun GameItem.toAggregateScore(): ResourceString? {
        return when {
            sport != Sport.SOCCER -> null
            scheduledAt == null -> null
            relatedGameScheduledAt != null && (relatedGameScheduledAt > scheduledAt) -> null
            firstTeam.aggregatedScore == null || secondTeam.aggregatedScore == null -> null
            else -> ResourceString.StringWithParams(
                R.string.box_score_soccer_header_aggregate_score,
                firstTeam.aggregatedScore,
                secondTeam.aggregatedScore
            )
        }
    }

    private fun FeaturedGameItem.RelatedContent.toUiModel(analyticsData: AnalyticsData): LayoutUiModel.Item? {
        return when (this) {
            is FeaturedGameItem.RelatedContent.ArticleLiveBlog -> {
                val byLine = "${authors.firstOrNull()?.firstName.orEmpty()} ${authors.firstOrNull()?.lastName.orEmpty()}"
                ArticleUiModel(
                    id = id,
                    permalink = permalink,
                    title = title,
                    excerpt = "",
                    imageUrl = imageUrl.orEmpty(),
                    byline = byLine,
                    commentCount = "",
                    isBookmarked = false,
                    isRead = false,
                    postType = PostType.ARTICLE,
                    analyticsData = analyticsData
                )
            }
            is FeaturedGameItem.RelatedContent.TopComment -> {
                TopCommentUiModel(
                    id = id,
                    author = author.name,
                    avatarUrl = author.avatarUrl,
                    isStaff = author.isStaff,
                    flairColor = author.gameFlairColor,
                    flairName = author.gameFlairName,
                    comment = comment,
                    commentedAt = timeAgoDateFormatter.format(commentedAt, TimeAgoDateFormatter.Params(showAgo = false)),
                    permalink = permalink,
                    analyticsData = analyticsData
                )
            }
        }
    }
}

private fun Feed.PageInfo.toUiModel(): FeedUiModel.PageInfo = FeedUiModel.PageInfo(currentPage = currentPage, hasNextPage = hasNextPage)