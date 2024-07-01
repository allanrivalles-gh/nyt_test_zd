package com.theathletic.links.deep

import com.theathletic.analytics.data.ClickSource
import com.theathletic.billing.SpecialOffer
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.feed.FeedType
import com.theathletic.hub.HubTabType
import com.theathletic.notification.NotificationOption
import com.theathletic.profile.manage.UserTopicId
import com.theathletic.rooms.analytics.LiveRoomEntryPoint
import com.theathletic.scores.GameDetailTab

sealed class DeeplinkDestination {
    object None : DeeplinkDestination()
    object OpenApp : DeeplinkDestination()

    data class Article(
        val articleId: Long,
        val source: ClickSource
    ) : DeeplinkDestination()

    data class Comments(
        val sourceId: String,
        val sourceType: CommentsSourceType,
        val commentId: String = "",
        val clickSource: ClickSource = ClickSource.DEEPLINK
    ) : DeeplinkDestination()

    data class StandaloneFeedLeague(
        val id: Long,
        val name: String? = null,
        val initialTab: HubTabType = HubTabType.Home
    ) : DeeplinkDestination()

    data class StandaloneFeedTeam(
        val id: Long,
        val name: String? = null,
        val initialTab: HubTabType = HubTabType.Home
    ) : DeeplinkDestination()

    data class StandaloneFeedAuthor(
        val id: Long,
        val name: String? = null
    ) : DeeplinkDestination()

    data class StandaloneFeedCategory(val id: Long, val name: String) : DeeplinkDestination()
    data class FeedSecondaryTab(val feedType: FeedType) : DeeplinkDestination()
    data class Headline(val id: String, val source: String, val index: Long? = null) : DeeplinkDestination()
    object HeadlineAppWidget : DeeplinkDestination()

    object PodcastFeed : DeeplinkDestination()
    data class Podcast(val podcastId: Long) : DeeplinkDestination()
    data class PodcastEpisode(
        val episodeId: Long,
        val podcastId: String,
        val episodeNumber: Int,
        val commentId: String
    ) : DeeplinkDestination()

    data class TagFeed(val slug: String) : DeeplinkDestination()

    data class LiveBlog(val id: String, val postId: String?) : DeeplinkDestination()
    data class LiveRoom(val id: String, val entryPoint: LiveRoomEntryPoint? = null) :
        DeeplinkDestination()

    object Frontpage : DeeplinkDestination()

    object Scores : DeeplinkDestination()

    data class MatchCentre(val gameId: String, val scrollToModule: ScrollToModule = ScrollToModule.NONE) : DeeplinkDestination()

    object GiftPurchase : DeeplinkDestination()
    data class Plans(
        val offer: SpecialOffer?,
        val queryParams: Map<String, String>
    ) : DeeplinkDestination()

    object Share : DeeplinkDestination()
    object Settings : DeeplinkDestination()
    object ListenFollowing : DeeplinkDestination()
    object ListenDiscover : DeeplinkDestination()
    object CreateAccount : DeeplinkDestination()
    object Login : DeeplinkDestination()
    data class ManageTopics(
        val userTopicId: UserTopicId?
    ) : DeeplinkDestination()

    object AccountSettings : DeeplinkDestination()
    object EmailSettings : DeeplinkDestination()
    object NotificationSettings : DeeplinkDestination()
    data class NotificationOptIn(val notificationOptIn: NotificationOption?, val enable: Boolean = false) : DeeplinkDestination()
    object RegionSettings : DeeplinkDestination()
    data class External(val url: String) : DeeplinkDestination()
    data class Universal(val url: String) : DeeplinkDestination()
    data class GameDetails(
        val gameId: String,
        val commentId: String,
        val selectedTab: GameDetailTab = GameDetailTab.GAME,
    ) : DeeplinkDestination()
}