package com.theathletic.links

import android.net.Uri
import android.webkit.URLUtil
import com.iterable.iterableapi.IterableApi
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.newarch.context.DeepLinkParams
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.article.data.ArticleRepository
import com.theathletic.billing.SpecialOffer
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.article.isArticlePost
import com.theathletic.entity.article.isDiscussionPost
import com.theathletic.entity.article.isHeadlinePost
import com.theathletic.entity.article.isQAndAPost
import com.theathletic.feed.FeedType
import com.theathletic.followable.rawId
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.hub.HubTabType
import com.theathletic.links.deep.DeeplinkDestination
import com.theathletic.links.deep.DeeplinkType
import com.theathletic.notification.NotificationOption
import com.theathletic.profile.manage.UserTopicId
import com.theathletic.rooms.analytics.LiveRoomEntryPoint
import com.theathletic.scores.GameDetailTab
import com.theathletic.utility.toTitleCase
import timber.log.Timber
import java.util.regex.Pattern
import kotlin.coroutines.suspendCoroutine

private const val COMMENT_DEEPLINK_QUERY_KEY = "comment_id"
private const val COMMENT_QUERY_KEY = "comment"
private const val COMMENT_NOT_SELECTED = ""
private const val EPISODE_QUERY_KEY = "episode"

@Suppress("LongParameterList")
class LinkParser @AutoKoin(Scope.SINGLE) constructor(
    private val contextUpdater: AnalyticsContextUpdater,
    private val linkAnalytics: LinkAnalytics,
    private val articleRepository: ArticleRepository,
    private val followableRepository: FollowableRepository,
    private val linkHelper: LinkHelper,
) {

    suspend fun parseDeepLink(data: Uri): DeeplinkDestination {
        return when (val deepLinkType = linkHelper.uriToAthleticLinkType(data)) {
            LinkHelper.AthleticLinkType.ITERABLE -> parseIterableLink(data, deepLinkType)
            LinkHelper.AthleticLinkType.EXTERNAL -> parseExternalLink(data)
            LinkHelper.AthleticLinkType.ATHLETIC_UNIVERSAL,
            LinkHelper.AthleticLinkType.ATHLETIC_INTERNAL -> parseAthleticDeepLink(data, deepLinkType)
            LinkHelper.AthleticLinkType.NONE -> DeeplinkDestination.None
        }
    }

    private suspend fun parseIterableLink(
        data: Uri,
        linkType: LinkHelper.AthleticLinkType
    ): DeeplinkDestination {
        // send links to Iterable in case they were campaign emails that should re-map
        val result = parseIterableDeeplink(data.toString())
        return if (result != null) {
            parseAthleticDeepLink(Uri.parse(result), linkType)
        } else {
            DeeplinkDestination.None
        }
    }

    private fun parseExternalLink(data: Uri) = if (URLUtil.isValidUrl(data.toString())) {
        DeeplinkDestination.External(data.toString())
    } else {
        DeeplinkDestination.None
    }.also {
        linkAnalytics.trackUniversalLink(
            url = data.toString(),
            isSuccess = false
        )
    }

    private fun parseNonSupportedUniversalLink(data: Uri) = if (URLUtil.isValidUrl(data.toString())) {
        DeeplinkDestination.Universal(data.toString())
    } else {
        DeeplinkDestination.None
    }

    private suspend fun parseAthleticDeepLink(
        data: Uri,
        athleticDeepLinkType: LinkHelper.AthleticLinkType
    ): DeeplinkDestination {
        Timber.d("Parse deepLink: $data")
        val deeplinkParams = linkHelper.parseUriToDeepLinkParams(data)
        contextUpdater.updateContext(deeplinkParams)

        return if (athleticDeepLinkType == LinkHelper.AthleticLinkType.ATHLETIC_INTERNAL) {
            parseInternalLink(data, deeplinkParams).also { trackDeeplink(data, it) }
        } else {
            parseUniversalLink(data, deeplinkParams).also { trackUniversalLink(data, it) }
        }
    }

    private fun trackDeeplink(data: Uri, it: DeeplinkDestination) {
        linkAnalytics.trackDeeplink(
            url = data.toString(),
            isSuccess = it != DeeplinkDestination.None
        )
    }

    private fun trackUniversalLink(data: Uri, it: DeeplinkDestination) {
        linkAnalytics.trackUniversalLink(
            url = data.toString(),
            isSuccess = it != DeeplinkDestination.None && it != DeeplinkDestination.Universal(data.toString())
        )
    }

    // e.g. theathletic://plans/discount66?source=ipm&campaign=TEST66ipmapp
    @Suppress("ComplexMethod", "LongMethod")
    private fun parseInternalLink(
        deeplinkUri: Uri,
        deeplinkParams: DeepLinkParams?
    ): DeeplinkDestination {
        val parsedLink = parseDeeplink(deeplinkUri)
        Timber.d("parseInternalLink: $parsedLink")

        return when (parsedLink.linkType) {
            DeeplinkType.OPEN_APP -> DeeplinkDestination.OpenApp

            // theathletic://article/:articleId?comment_id=123
            DeeplinkType.ARTICLE -> parseArticleDestination(deeplinkParams, parsedLink)

            DeeplinkType.LEAGUE -> parseLeagueHubDestination(
                leagueId = parsedLink.second?.toLongOrNull() ?: -1,
                tab = parsedLink.third
            )
            DeeplinkType.TEAM -> parseTeamHubDestination(
                teamId = parsedLink.second?.toLongOrNull() ?: -1,
                tab = parsedLink.third
            )
            DeeplinkType.AUTHOR ->
                DeeplinkDestination.StandaloneFeedAuthor(parsedLink.second?.toLongOrNull() ?: -1)
            DeeplinkType.CATEGORY -> {
                val categoryId = parsedLink.second?.toLongOrNull() ?: -1
                val name = parsedLink.parameters["title"]
                    ?.replace("-", " ")
                    ?.replace("_", " ")
                    ?.toTitleCase() ?: ""

                if (categoryId == -1L) {
                    DeeplinkDestination.None
                } else {
                    DeeplinkDestination.StandaloneFeedCategory(categoryId, name)
                }
            }

            DeeplinkType.HEADLINE -> parseHeadlineDestination(parsedLink, deeplinkParams)

            DeeplinkType.HEADLINE_WIDGET -> parseHeadlineWidget(parsedLink, deeplinkParams)

            DeeplinkType.HEADLINE_WIDGET_HEADER -> DeeplinkDestination.HeadlineAppWidget

            DeeplinkType.REACTIONS -> DeeplinkDestination.None

            DeeplinkType.FRONTPAGE -> DeeplinkDestination.Frontpage

            DeeplinkType.SCORES -> DeeplinkDestination.Scores

            DeeplinkType.DISCUSSIONS -> parseDiscussionDestination(parsedLink)

            DeeplinkType.LISTEN_FOLLOWING -> DeeplinkDestination.ListenFollowing

            DeeplinkType.LISTEN_DISCOVER -> DeeplinkDestination.ListenDiscover

            DeeplinkType.LIVE_DISCUSSIONS -> parseLiveDiscussionDestination(parsedLink)

            DeeplinkType.PODCAST_FEED -> DeeplinkDestination.PodcastFeed
            DeeplinkType.PODCAST -> parsePodcastDestination(parsedLink)

            DeeplinkType.GIFT -> DeeplinkDestination.GiftPurchase

            DeeplinkType.PLANS -> {
                if (parsedLink.second.orEmpty().startsWith("offer_get")) return DeeplinkDestination.None
                val specialOffer = when (parsedLink.second) {
                    "discount40" -> SpecialOffer.Annual40
                    "discount50" -> SpecialOffer.Annual50
                    "discount60" -> SpecialOffer.Annual60
                    "discount70" -> SpecialOffer.Annual70
                    else -> null
                }

                DeeplinkDestination.Plans(specialOffer, parsedLink.parameters)
            }

            DeeplinkType.SHARE -> DeeplinkDestination.Share
            DeeplinkType.SETTINGS -> DeeplinkDestination.Settings
            DeeplinkType.REGISTER -> DeeplinkDestination.CreateAccount
            DeeplinkType.LOGIN -> DeeplinkDestination.Login

            // theathletic://boxscore/:gameId <- We only supprt the new GQL game ids only
            DeeplinkType.BOXSCORE -> {
                when (val gameId = parsedLink.second) {
                    null -> DeeplinkDestination.None
                    else -> parseBoxScoreDeeplinkDestination(gameId, parsedLink)
                }
            }

            DeeplinkType.LIVE_BLOGS -> parsedLink.second?.let { id ->
                DeeplinkDestination.LiveBlog(id, parsedLink.third)
            } ?: DeeplinkDestination.None

            DeeplinkType.LIVE_ROOMS -> parseLiveRoomDestination(parsedLink.lastPathOrNull(), isDeeplink = true)

            DeeplinkType.MANAGE_TEAMS -> parseManageTopicsDestination(
                parsedLink.parameters["type"],
                parsedLink.parameters["id"]
            )

            DeeplinkType.FEED -> {
                parsedLink.parameters["league"]?.toLongOrNull()?.let {
                    return DeeplinkDestination.FeedSecondaryTab(FeedType.League(it))
                }
                parsedLink.parameters["team"]?.toLongOrNull()?.let {
                    return DeeplinkDestination.FeedSecondaryTab(FeedType.Team(it))
                }
                parsedLink.parameters["author"]?.toLongOrNull()?.let {
                    return DeeplinkDestination.FeedSecondaryTab(FeedType.Author(it))
                }
                return DeeplinkDestination.None
            }

            DeeplinkType.TAG_FEED -> {
                parsedLink.slug?.let { DeeplinkDestination.TagFeed(it) } ?: DeeplinkDestination.None
            }

            DeeplinkType.ACCOUNT_SETTINGS -> DeeplinkDestination.AccountSettings

            DeeplinkType.NOTIFICATION_SETTINGS -> parseNotificationSettings(parsedLink.third, parsedLink.second)

            DeeplinkType.EMAIL_SETTINGS -> DeeplinkDestination.EmailSettings

            else -> parseExternalLink(deeplinkUri)
        }
    }

    private suspend fun parseUniversalLink(
        weblinkUri: Uri,
        deeplinkParams: DeepLinkParams?
    ): DeeplinkDestination {
        val pattern = Pattern.compile("/(\\d+)")
        val finalUri = linkHelper.parseAthleticUri(weblinkUri)
        val matcher = pattern.matcher(finalUri.toString())
        val parsedLink = parseAthleticUniversalLink(finalUri)
        Timber.d("handleWebLink: $parsedLink")

        return when (parsedLink.linkType) {
            UniversalLinkType.PODCAST_FEED -> DeeplinkDestination.PodcastFeed

            UniversalLinkType.PODCAST -> parsePodcastDestination(parsedLink)

            UniversalLinkType.DISCUSSIONS -> parseDiscussionDestination(parsedLink)

            UniversalLinkType.LIVE_DISCUSSIONS -> parseLiveDiscussionDestination(parsedLink)

            UniversalLinkType.HEADLINE -> parseHeadlineDestination(parsedLink, deeplinkParams)

            UniversalLinkType.FRONTPAGE -> DeeplinkDestination.Frontpage

            UniversalLinkType.REACTIONS -> DeeplinkDestination.None

            UniversalLinkType.GIFT -> DeeplinkDestination.GiftPurchase

            UniversalLinkType.SHARE -> DeeplinkDestination.Share

            UniversalLinkType.LIVE_BLOGS -> parseLiveBlogDestinationFromWebLink(parsedLink)

            UniversalLinkType.LIVE_ROOMS -> parseLiveRoomDestination(
                id = parsedLink.lastPathOrNull(),
                isDeeplink = false
            )

            UniversalLinkType.CULTURE -> DeeplinkDestination.StandaloneFeedLeague(39)

            UniversalLinkType.MANAGE_TEAMS -> parseManageTopicsDestination(
                parsedLink.parameters["type"],
                parsedLink.parameters["id"]
            )

            UniversalLinkType.GAME -> parseBoxScoreUniversalLinkDestination(parsedLink)

            UniversalLinkType.TAG_FEED -> {
                parsedLink.slug?.let { DeeplinkDestination.TagFeed(it) } ?: DeeplinkDestination.None
            }

            else -> {
                if (matcher.find()) {
                    val id = matcher.group(1)!!.toLong()
                    val article = articleRepository.getArticle(id = id, forceRefresh = true)
                    if (article != null) {
                        getArticleDestination(article, deeplinkParams, parsedLink.parameters)
                    } else {
                        DeeplinkDestination.None
                    }
                } else {
                    matchFollowables(parsedLink) ?: parseNonSupportedUniversalLink(finalUri)
                }
            }
        }
    }

    private fun parsePodcastDestination(attributes: LinkAttributes): DeeplinkDestination {
        val podcastId = parseDashAppendedId(attributes.second) ?: -1L
        val podcastIdString = if (podcastId != -1L) podcastId.toString() else null
        val commentId = getCommentId(attributes.parameters)
        val episodeId = attributes.parameters[EPISODE_QUERY_KEY]?.toLongOrNull()

        return when {
            podcastId < 0 -> DeeplinkDestination.PodcastFeed
            episodeId != null -> if (podcastIdString == null) {
                DeeplinkDestination.PodcastEpisode(episodeId, "", -1, commentId)
            } else {
                DeeplinkDestination.PodcastEpisode(-1L, podcastIdString, episodeId.toInt(), commentId)
            }
            else -> DeeplinkDestination.Podcast(podcastId = podcastId)
        }
    }

    private fun parseArticleDestination(
        deeplinkParams: DeepLinkParams?,
        parsedLink: LinkAttributes
    ): DeeplinkDestination {
        val source = getClickLinkSource(deeplinkParams)
        val articleId = parsedLink.second?.toLongOrNull() ?: -1
        val initialCommentId = getCommentId(parsedLink.parameters)
        return if (hasCommentSelected(initialCommentId)) {
            DeeplinkDestination.Comments(
                sourceId = articleId.toString(),
                sourceType = CommentsSourceType.ARTICLE,
                commentId = initialCommentId,
                clickSource = source
            )
        } else {
            DeeplinkDestination.Article(articleId, source)
        }
    }

    private fun parseLeagueHubDestination(leagueId: Long, tab: String? = null): DeeplinkDestination {
        val initialTab = parseLeagueHubTab(tab)
        return DeeplinkDestination.StandaloneFeedLeague(id = leagueId, initialTab = initialTab)
    }

    private fun parseTeamHubDestination(teamId: Long, tab: String? = null): DeeplinkDestination {
        val initialTab = parseTeamHubTab(tab)
        return DeeplinkDestination.StandaloneFeedTeam(id = teamId, initialTab = initialTab)
    }

    private fun parseLeagueHubTab(tab: String?) = when (tab) {
        "schedule" -> HubTabType.Schedule
        "standings" -> HubTabType.Standings
        "bracket" -> HubTabType.Brackets
        else -> HubTabType.Home
    }
    private fun parseTeamHubTab(tab: String?) = when (tab) {
        "feed" -> HubTabType.Home
        "schedule" -> HubTabType.Schedule
        "standings" -> HubTabType.Standings
        "stats" -> HubTabType.Stats
        "roster" -> HubTabType.Roster
        else -> HubTabType.Home
    }

    private fun parseNotificationSettings(notification: String?, enable: String?): DeeplinkDestination {
        return when (notification) {
            NotificationOption.TOP_SPORTS_NEWS.notification -> DeeplinkDestination.NotificationOptIn(
                NotificationOption.TOP_SPORTS_NEWS,
                enable == "enable"
            )
            else -> DeeplinkDestination.NotificationSettings
        }
    }

    private fun parseBoxScoreUniversalLinkDestination(parsedLink: LinkAttributes): DeeplinkDestination {
        val gameId = parsedLink.slug ?: return DeeplinkDestination.None
        val selectedTab: GameDetailTab = when {
            parsedLink.parameters.containsKey("comment_id") -> GameDetailTab.DISCUSS
            parsedLink.third == "discuss" -> GameDetailTab.DISCUSS
            parsedLink.third == "plays" -> GameDetailTab.PLAYS
            parsedLink.third == "timeline" -> GameDetailTab.PLAYS
            parsedLink.third == "stats" -> GameDetailTab.PLAYER_STATS
            else -> null
        } ?: return DeeplinkDestination.MatchCentre(gameId)

        return parseGameDetailsDestination(
            gameId = gameId,
            commentId = getCommentId(parsedLink.parameters),
            selectedTab = selectedTab
        )
    }

    private fun parseBoxScoreDeeplinkDestination(gameId: String, parsedLink: LinkAttributes): DeeplinkDestination {
        val selectedTab: GameDetailTab? = when {
            parsedLink.parameters.containsKey("comment_id") -> GameDetailTab.DISCUSS
            parsedLink.slug == "plays" -> GameDetailTab.PLAYS
            parsedLink.slug == "timeline" -> GameDetailTab.PLAYS
            parsedLink.slug == "stats" -> GameDetailTab.PLAYER_STATS
            parsedLink.slug == "grades" -> GameDetailTab.GRADES
            parsedLink.slug == "live-blog" -> GameDetailTab.LIVE_BLOG
            else -> null
        }

        if (selectedTab == null) {
            return if (parsedLink.slug == "season-stats") {
                DeeplinkDestination.MatchCentre(gameId, ScrollToModule.SEASON_STATS)
            } else {
                DeeplinkDestination.MatchCentre(gameId)
            }
        }

        return parseGameDetailsDestination(
            gameId = gameId,
            commentId = getCommentId(parsedLink.parameters),
            selectedTab = selectedTab
        )
    }

    private fun parseLiveBlogDestinationFromWebLink(parsedLink: LinkAttributes): DeeplinkDestination {
        // Match center -> https://theathletic.com/live-blogs/:slug/match/:id
        return if (parsedLink.third == "match") {
            parsedLink.fourth?.let { id -> DeeplinkDestination.MatchCentre(id) }
        } else {
            parsedLink.third?.let { id -> DeeplinkDestination.LiveBlog(id, parsedLink.fourth) }
        } ?: DeeplinkDestination.None
    }

    private fun parseLiveRoomDestination(
        id: String?,
        isDeeplink: Boolean
    ) = id?.let {
        DeeplinkDestination.LiveRoom(
            id = it,
            entryPoint = when {
                isDeeplink -> LiveRoomEntryPoint.DEEPLINK
                else -> LiveRoomEntryPoint.UNIVERSAL_LINK
            }
        )
    } ?: DeeplinkDestination.None

    private fun parseHeadlineDestination(
        linkAttributes: LinkAttributes,
        params: DeepLinkParams?
    ): DeeplinkDestination {
        val headlineId = linkAttributes.lastPathOrNull() ?: return DeeplinkDestination.None
        val commentId = getCommentId(linkAttributes.parameters)

        return if (hasCommentSelected(commentId)) {
            DeeplinkDestination.Comments(
                sourceId = headlineId,
                sourceType = CommentsSourceType.HEADLINE,
                commentId = commentId
            )
        } else {
            DeeplinkDestination.Headline(
                headlineId, params?.source ?: ClickSource.DEEPLINK.value
            )
        }
    }

    private fun parseHeadlineWidget(
        linkAttributes: LinkAttributes,
        params: DeepLinkParams?
    ): DeeplinkDestination {
        val headlineId = linkAttributes.second ?: return DeeplinkDestination.None
        val index = linkAttributes.third

        return DeeplinkDestination.Headline(
            headlineId, params?.source ?: ClickSource.DEEPLINK.value, index?.toLongOrNull()
        )
    }

    private fun parseDiscussionDestination(attributes: LinkAttributes): DeeplinkDestination {
        val initialCommentId = getCommentId(attributes.parameters)
        return parseDashAppendedId(attributes.second)?.let {
            DeeplinkDestination.Comments(
                sourceId = it.toString(),
                sourceType = CommentsSourceType.DISCUSSION,
                commentId = initialCommentId
            )
        } ?: DeeplinkDestination.None
    }

    private fun parseLiveDiscussionDestination(attributes: LinkAttributes): DeeplinkDestination {
        val initialCommentId = getCommentId(attributes.parameters)
        return parseDashAppendedId(attributes.second)?.let {
            DeeplinkDestination.Comments(
                sourceId = it.toString(),
                sourceType = CommentsSourceType.QANDA,
                commentId = initialCommentId
            )
        } ?: DeeplinkDestination.None
    }

    private fun parseDashAppendedId(idString: String?): Long? {
        return idString?.split("-")?.firstOrNull()?.toLongOrNull()
    }

    private fun parseManageTopicsDestination(type: String?, id: String?): DeeplinkDestination {
        val topicId = id?.toLongOrNull() ?: return DeeplinkDestination.ManageTopics(null)
        val topic = when (type) {
            "league" -> UserTopicId.League(topicId)
            "team" -> UserTopicId.Team(topicId)
            "author" -> UserTopicId.Author(topicId)
            else -> null
        }
        return DeeplinkDestination.ManageTopics(topic)
    }

    private fun parseGameDetailsDestination(
        gameId: String?,
        commentId: String,
        selectedTab: GameDetailTab = GameDetailTab.GAME
    ): DeeplinkDestination {
        return gameId?.let {
            DeeplinkDestination.GameDetails(gameId = gameId, commentId = commentId, selectedTab = selectedTab)
        } ?: DeeplinkDestination.None
    }

    private fun getArticleDestination(
        article: ArticleEntity,
        deeplinkParams: DeepLinkParams?,
        queryParams: Map<String, String>
    ): DeeplinkDestination {
        val clickSource = getClickLinkSource(deeplinkParams)
        val commentId = getCommentId(queryParams)

        return if (hasCommentSelected(commentId) || article.isDiscussionPost || article.isQAndAPost) {
            getArticleCommentsDestination(article, commentId, clickSource)
        } else {
            DeeplinkDestination.Article(article.articleId, clickSource)
        }
    }

    private fun getArticleCommentsDestination(
        article: ArticleEntity,
        commentId: String,
        source: ClickSource
    ): DeeplinkDestination {
        val sourceType = when {
            article.isArticlePost -> CommentsSourceType.ARTICLE
            article.isHeadlinePost -> CommentsSourceType.HEADLINE
            article.isDiscussionPost -> CommentsSourceType.DISCUSSION
            article.isQAndAPost -> CommentsSourceType.QANDA
            else -> return DeeplinkDestination.None
        }

        return DeeplinkDestination.Comments(
            sourceId = article.articleId.toString(),
            sourceType = sourceType,
            commentId = commentId,
            clickSource = source
        )
    }

    private fun getClickLinkSource(deeplinkParams: DeepLinkParams?): ClickSource {
        val source = when (deeplinkParams?.source) {
            "user-shared-article" -> ClickSource.DEEPLINK_USER_SHARED
            "emp-shared-article" -> ClickSource.DEEPLINK_EMPLOYEE_SHARED
            else -> ClickSource.DEEPLINK
        }
        return source
    }

    private fun hasCommentSelected(commentId: String) = commentId != COMMENT_NOT_SELECTED

    private fun getCommentId(queryParams: Map<String, String>): String {
        return queryParams[COMMENT_DEEPLINK_QUERY_KEY]
            ?: queryParams[COMMENT_QUERY_KEY]
            ?: COMMENT_NOT_SELECTED
    }

    private suspend fun matchFollowables(
        parsedLink: LinkAttributes,
    ): DeeplinkDestination? {
        val url = when {
            parsedLink.first == "team" -> parsedLink.second
            parsedLink.second == "team" -> parsedLink.third
            parsedLink.first == "football" -> parsedLink.second
            parsedLink.first == "author" -> parsedLink.slug
            else -> parsedLink.first
        }.orEmpty()

        getTeamDestinationByUrl(url, parsedLink.slug)?.let { return it }
        getLeagueDestinationByUrl(url, parsedLink.slug)?.let { return it }
        getAuthorDestinationByUrl(url)?.let { return it }
        return null
    }

    private suspend fun getTeamDestinationByUrl(url: String, initialTab: String? = null): DeeplinkDestination? {
        return followableRepository.getTeamFromUrl(url)?.let { team ->
            DeeplinkDestination.StandaloneFeedTeam(team.rawId(), team.name, parseTeamHubTab(initialTab))
        }
    }

    private suspend fun getLeagueDestinationByUrl(url: String, initialTab: String? = null): DeeplinkDestination? {
        return followableRepository.getLeagueFromUrl(url)?.let { league ->
            DeeplinkDestination.StandaloneFeedLeague(league.rawId(), league.name, parseLeagueHubTab(initialTab))
        }
    }

    private suspend fun getAuthorDestinationByUrl(url: String): DeeplinkDestination? {
        return followableRepository.getAuthorFromUrl(url)?.let {
            DeeplinkDestination.StandaloneFeedAuthor(it.rawId(), it.name)
        }
    }
}

private suspend fun parseIterableDeeplink(url: String) = suspendCoroutine { continuation ->
    IterableApi.getInstance().getAndTrackDeepLink(url) { result ->
        continuation.resumeWith(Result.success(result))
    }
}