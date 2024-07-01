package com.theathletic.analytics.newarch

import com.theathletic.analytics.newarch.CollectorKey.ARTICLE_VIEW_PHP
import com.theathletic.analytics.newarch.CollectorKey.DATADOG
import com.theathletic.analytics.newarch.CollectorKey.FIREBASE
import com.theathletic.analytics.newarch.CollectorKey.KOCHAVA
import com.theathletic.analytics.newarch.CollectorKey.MAIN
import com.theathletic.analytics.newarch.schemas.AnalyticsSchema
import com.theathletic.analytics.newarch.schemas.KafkaTopic
import com.theathletic.annotation.AnalyticsEvents
import com.theathletic.annotation.NoisyEvent

/**
 * This class is strictly for event definitions.
 *
 * Events marked with the @HasDynamicProperties annotation have properties not defined by a strict
 * contract (eg they might be passed in via the query parameters of a deep link)
 *
 * Events marked with the @NoisyEvent annotation will not display a toast when fired nor will they
 * show up in the analytics history page.
 */
@AnalyticsEvents
sealed class Event(
    val eventName: String,
    val collectors: List<CollectorKey>,
    val kafkaTopic: KafkaTopic? = null
) {
    constructor(eventName: String, collector: CollectorKey) : this(eventName, listOf(collector))

    constructor(
        eventName: String,
        collector: CollectorKey,
        kafkaTopic: KafkaTopic
    ) : this(eventName, listOf(collector), kafkaTopic)

    class Ads {
        data class AdRequest(
            val view: String,
            val ad_view_id: String,
            val pos: String,
        ) : Event("ad-request", listOf(DATADOG))

        data class AdResponseSuccess(
            val view: String,
            val ad_view_id: String,
            val pos: String
        ) : Event("ad-response-success", listOf(DATADOG))

        data class AdResponseFailed(
            val view: String,
            val ad_view_id: String,
            val pos: String,
            val error: String
        ) : Event("ad-response-failed", listOf(DATADOG))

        data class AdNoFill(
            val view: String,
            val ad_view_id: String,
            val pos: String
        ) : Event("ad-no-fill", listOf(DATADOG))

        data class AdImpression(
            val view: String,
            val ad_view_id: String,
            val pos: String
        ) : Event("ad-impression", listOf(DATADOG))

        data class AdPageView(
            val view: String,
            val ad_view_id: String
        ) : Event("ad-page-view", listOf(DATADOG))
    }

    class AppLifecycle {
        object OpenAnonymousUser : Event("app_open_anonymous_user", listOf(FIREBASE))

        object OpenNewUser : Event("app_open_new_user", listOf(FIREBASE))

        object OpenNonSubscriber : Event("app_open_nonsubscriber", listOf(FIREBASE))

        object OpenSubscriber : Event("app_open_subscriber", listOf(FIREBASE))

        object Terminated : Event("app_will_terminate", MAIN)

        object ToBackground : Event("app_background", MAIN)

        object ToForeground : Event("app_foreground", MAIN)
    }

    class AppRating {
        object DialogRequested : Event("app_rating_dialog_requested", listOf(FIREBASE))
    }

    interface Link {
        class NavigateUniversalLinkSuccess(
            val view: String = "universal_link",
            val url: String
        ) : Event("success", MAIN)

        class NavigateDeepLinkSuccess(
            val view: String = "deep_link",
            val url: String = ""
        ) : Event("success", MAIN)

        class NavigateUniversalLinkFailure(
            val view: String = "universal_link",
            val url: String
        ) : Event("failure", MAIN)

        class NavigateDeepLinkFailure(
            val view: String = "deep_link",
            val url: String
        ) : Event("failure", MAIN)
    }

    class Article {
        class BottomBarShareBegins(
            val view: String = "share_article",
            val object_type: String = "article_id",
            val object_id: String
        ) : Event("click", MAIN)

        class BottomBarShareComplete(
            val view: String = "share_article",
            val object_type: String = "article_id",
            val object_id: String
        ) : Event("leave_app", MAIN)

        class CommentAdded(
            val view: String = "comment",
            val element: String,
            val object_type: String,
            val object_id: String
        ) : Event("add", MAIN)

        object FreeArticleRead : Event("Free Article Read", listOf(KOCHAVA))

        object PaywallView : Event("Paywall View", listOf(KOCHAVA))

        class RecommendedView(
            val view: String = "recommended_articles"
        ) : Event("view", MAIN)

        class View(
            val article_id: String,
            val has_paywall: String = "0",
            val percent_read: String,
            val source: String,
            val paywall_type: String = ""
        ) : Event("article-view", listOf(ARTICLE_VIEW_PHP, FIREBASE))

        class TextStyleClick(
            val view: String = "article",
            val element: String = "settings_drawer",
            val object_type: String = "article_id",
            val object_id: String
        ) : Event("click", MAIN)

        class InContentModuleClick(
            val view: String,
            val element: String = "in_content_module",
            val object_type: String = "article_id",
            val object_id: String,
            val module_category: String = "commercial",
            val module_type: String,
            val module_id: String = "1",
            val product_id: String
        ) : Event("click", MAIN)
    }

    class Authentication {
        class Login(
            val object_type: String = "login_method_used",
            val object_id: String,
            val login_entry_point: String,
            val success: String,
            val error_code: String = ""
        ) : Event("login", listOf(MAIN, FIREBASE))

        class SignInPageView(
            val view: String = "sign_in_page",
            val login_entry_point: String = ""
        ) : Event("view", MAIN)

        class SignUpPageView(
            val view: String = "sign_up_page",
            val login_entry_point: String = ""
        ) : Event("view", MAIN)

        class SignUp(
            val method: String,
            val success: String
        ) : Event("sign_up", listOf(FIREBASE))

        class GetStartedClick(
            val view: String = "start_screen",
            val element: String = "get_started"
        ) : Event("click", MAIN)

        class ClickLoginLink(
            val view: String = "start_screen",
            val element: String = "login"
        ) : Event("click", MAIN)

        class StartScreenView(
            val view: String = "start_screen"
        ) : Event("view", MAIN)

        class ClickSignUpPage(
            val view: String = "sign_up_page",
            val element: String
        ) : Event("click", MAIN)

        class ClickSignInPage(
            val view: String = "sign_in_page",
            val element: String
        ) : Event("click", MAIN)

        class ClickEmailContinue(
            val view: String = "email_create_account_page",
            val element: String = "continue"
        ) : Event("click", MAIN)
    }

    class Discover {
        class Click(
            val view: String = "discover",
            val element: String,
            val object_type: String,
            val object_id: String
        ) : Event("click", MAIN)

        class SubTabView(
            val view: String = "discover",
            val element: String
        ) : Event("view", MAIN)
    }

    interface Frontpage {
        data class View(
            val view: String = "front_page",
            val element: String = "",
            val object_type: String = "",
            val object_id: String = ""
        ) : Event("view", collector = MAIN)

        data class Click(
            val view: String = "front_page",
            val element: String = "",
            val object_type: String = "",
            val object_id: String = "",

            val v_index: String = "",
            val h_index: String = "",
            val container: String = "",
            val page_order: String = ""
        ) : Event("click", collector = MAIN)

        data class Play(
            val view: String = "front_page",
            val element: String = "podcast_episode",
            val object_type: String = "podcast_episode_id",
            val object_id: String,

            val v_index: String = "",
            val h_index: String = "",
            val container: String = "",
            val page_order: String = ""
        ) : Event("play", collector = MAIN)

        data class Pause(
            val view: String = "front_page",
            val element: String = "podcast_episode",
            val object_type: String = "podcast_episode_id",
            val object_id: String,

            val v_index: String = "",
            val h_index: String = "",
            val container: String = "",
            val page_order: String = ""
        ) : Event("pause", collector = MAIN)

        class SearchClick(
            val view: String = "front_page",
            val element: String = "search"
        ) : Event("click", MAIN)

        @NoisyEvent
        class Impression(
            override val verb: String = "impress",
            override val view: String = "front_page",
            override val filter_type: String? = null,
            override val filter_id: Long? = null,
            override val object_type: String,
            override val object_id: String,
            override val impress_start_time: Long,
            override val impress_end_time: Long,
            override val v_index: Long = -1L,
            override val h_index: Long = -1L,
            override val element: String,
            override val container: String? = null,
            override val page_order: Long? = null,
            override val parent_object_type: String? = null,
            override val parent_object_id: String? = null
        ) : ImpressionEvent(kafkaTopic = KafkaTopic.FRONTPAGE_IMPRESSION)
    }

    interface Home {
        class NavigationClick(
            val view: String = "home",
            val element: String = "feed_navigation",
            val object_type: String,
            val object_id: String,
            val h_index: String = ""
        ) : Event("click", MAIN)

        class View(
            val view: String = "home",
            val element: String = "feed_navigation",
            val object_type: String,
            val object_id: String,

            val h_index: String = ""
        ) : Event("view", collector = MAIN)
    }

    interface Topic {
        class View(
            val view: String = "topic",
            val element: String,
            val object_type: String,
            val object_id: String,

            val brief_id: String = "",
            val topic_id: String = "",
            val v_index: String = ""
        ) : Event("view", collector = MAIN)

        class Click(
            val view: String = "topic",
            val element: String,
            val object_type: String,
            val object_id: String,

            val topic_id: String = "",
            val v_index: String = "",
            val next_tag_id: String = ""
        ) : Event("click", collector = MAIN)

        class Add(
            val view: String = "topic",
            val element: String,
            val object_type: String,
            val object_id: String,

            val topic_id: String = "",
            val v_index: String = ""
        ) : Event("add", collector = MAIN)

        @NoisyEvent
        class Impression(
            override val verb: String = "impress",
            override val view: String = "topic",
            override val filter_type: String? = null,
            override val filter_id: Long? = null,
            override val object_type: String,
            override val object_id: String,
            override val impress_start_time: Long,
            override val impress_end_time: Long,
            override val v_index: Long = -1L,
            override val h_index: Long = -1L,
            override val element: String,
            override val container: String? = null,
            override val page_order: Long? = null,
            override val parent_object_type: String?,
            override val parent_object_id: String?
        ) : ImpressionEvent(kafkaTopic = KafkaTopic.REALTIME_IMPRESSION)
    }

    interface MiniHeadline {
        class View(
            val view: String = "mini_headline",
            val element: String,
            val object_type: String,
            val object_id: String,

            val headline_index: String = "",
            val parent_id: String = ""
        ) : Event("view", collector = MAIN)

        data class Click(
            val view: String = "mini_headline",
            val element: String = "",
            val object_type: String = "",
            val object_id: String = "",

            val headline_index: String = "",
            val unlike: String = ""
        ) : Event("click", collector = MAIN)

        class Add(
            val view: String = "mini_headline",
            val element: String,
            val object_type: String,
            val object_id: String
        ) : Event("add", collector = MAIN)
    }

    interface Headline {
        // V2.1
        class View(
            val view: String = "headline",
            val element: String,
            val object_type: String,
            val object_id: String,

            val headline_index: String = "",
            val source: String = ""
        ) : Event("view", collector = MAIN)

        class HeadlineWidget(val view: String = "app-widget",) : Event("view", collector = MAIN)

        object HeadlineViewKochava : Event("Headline View", collector = KOCHAVA)

        class Click(
            val view: String = "headline",
            val element: String,
            val object_type: String,
            val object_id: String,

            val section: String = ""
        ) : Event("click", collector = MAIN)

        class Add(
            val view: String = "headline",
            val element: String,
            val object_type: String,
            val object_id: String
        ) : Event("add", collector = MAIN)

        class Like(
            val view: String = "headline",
            val element: String,
            val object_type: String,
            val object_id: String
        ) : Event("like", collector = MAIN)

        class Unlike(
            val view: String = "headline",
            val element: String,
            val object_type: String,
            val object_id: String
        ) : Event("unlike", collector = MAIN)

        class Update(
            val view: String = "headline",
            val element: String,
            val object_type: String,
            val object_id: String
        ) : Event("update", collector = MAIN)

        class Delete(
            val view: String = "headline",
            val element: String,
            val object_type: String,
            val object_id: String
        ) : Event("delete", collector = MAIN)

        class Flag(
            val view: String = "headline",
            val element: String,
            val object_type: String,
            val object_id: String,
            val filter_type: String = "",
            val v_index: String,
            val team_id: String = ""
        ) : Event("flag", collector = MAIN)

        class TextStyleClick(
            val view: String = "headline",
            val element: String = "settings_drawer",
            val object_type: String = "object_id",
            val object_id: String
        ) : Event("click", MAIN)
    }

    class Feed {
        class Click(
            val view: String,
            val element: String,
            val object_type: String,
            val object_id: String,
            val container: String,
            val page_order: String,
            val v_index: String,
            val h_index: String,
            val parent_object_type: String,
            val parent_object_id: String,
            val game_id: String = "",
            val box_score_tab: String = ""
        ) : Event("click", collector = MAIN)

        class Play(
            val view: String,
            val element: String,
            val object_type: String = "podcast_episode_id",
            val object_id: String,

            val container: String,
            val page_order: String,
            val v_index: String,
            val h_index: String,
            val parent_object_type: String,
            val parent_object_id: String
        ) : Event("play", collector = MAIN)

        class Pause(
            val view: String,
            val element: String,
            val object_type: String = "podcast_episode_id",
            val object_id: String,

            val container: String,
            val page_order: String,
            val v_index: String,
            val h_index: String,
            val parent_object_type: String,
            val parent_object_id: String
        ) : Event("pause", collector = MAIN)

        @NoisyEvent
        class Impression(
            override val verb: String = "impress",
            override val view: String,
            override val filter_type: String? = null,
            override val filter_id: Long? = null,
            override val object_type: String,
            override val object_id: String,
            override val impress_start_time: Long,
            override val impress_end_time: Long,
            override val v_index: Long = -1L,
            override val h_index: Long = -1L,
            override val element: String,
            override val container: String? = null,
            override val page_order: Long? = null,
            override val parent_object_type: String? = null,
            override val parent_object_id: String? = null
        ) : ImpressionEvent(kafkaTopic = KafkaTopic.FEED_IMPRESSION)
    }

    interface GameFeed {
        class View(
            val view: String = "feed",
            val object_type: String,
            val object_id: String,
        ) : Event("view", collector = MAIN)

        class Click(
            val view: String = "feed",
            val element: String,
            val object_type: String,
            val object_id: String,

            val container: String = "",
            val page_order: String = "",
            val v_index: String = "",
            val h_index: String = "",
            val filter: String = "",
            val filter_id: String = "",
            val parent_object_type: String = "",
            val parent_object_id: String = "",
            val game_id: String = "",
            val league_id: String = "",
            val team_id: String = "",
            val blog_id: String = ""
        ) : Event("click", collector = MAIN)
    }

    interface PlayerGrades {
        class View(
            val view: String,
            val element: String,
            val object_type: String,
            val object_id: String,

            val game_id: String = "",
            val league_id: String,
            val team_member_id: String = "",
            val team_id: String = ""
        ) : Event("view", collector = MAIN)

        class Click(
            val view: String,
            val element: String,
            val object_type: String,
            val object_id: String,

            val game_id: String = "",
            val league_id: String,
            val team_member_id: String = "",
            val team_id: String = ""
        ) : Event("click", collector = MAIN)
    }

    interface BoxScore {
        class View(
            val view: String,
            val element: String = "",
            val object_type: String = "game_id",
            val object_id: String,

            val league_id: String,
            val team_id: String,
            val blog_id: String = ""
        ) : Event("view", collector = MAIN)

        class Click(
            val view: String,
            val element: String,
            val object_type: String,
            val object_id: String,

            val game_id: String,
            val league_id: String,
            val page_order: String = "",
            val h_index: String = "",
            val v_index: String = "",
            val ticket_partner: String = ""
        ) : Event("click", collector = MAIN)

        class ClickDiscoveryBoxScore(
            val view: String,
            val element: String = "box_score_discuss",
            val object_type: String = "game_id",
            val object_id: String,
            val parent_object_type: String = "league_id",
            val parent_object_id: String,
            val team_id: String = ""
        ) : Event("click", MAIN)
    }

    interface Discuss {
        class View(
            val view: String,
            val element: String = "",
            val object_type: String = "game_id",
            val object_id: String,
            val league_id: String,
            val team_id: String,
            val comment_view_link_id: String,
        ) : Event("view", collector = MAIN)

        class Click(
            val view: String,
            val element: String,
            val object_type: String = "game_id",
            val object_id: String = "",
            val game_id: String,
            val league_id: String,
            val team_id: String
        ) : Event("click", collector = MAIN)
    }
    interface LeagueHub {
        class View(
            val view: String,
            val element: String,
            val object_type: String,
            val object_id: String = "",

            val league_id: String,
            val meta_blob: String = ""
        ) : Event("view", collector = MAIN)

        class Click(
            val view: String,
            val element: String,
            val object_type: String,
            val object_id: String = "",

            val league_id: String,
            val h_index: String = "",
            val meta_blob: String = "",
        ) : Event("click", collector = MAIN)
    }

    interface TeamHub {
        class View(
            val view: String,
            val element: String,
            val object_type: String = "team_id",
            val object_id: String,

            val league_id: String
        ) : Event("view", collector = MAIN)

        class Click(
            val view: String,
            val element: String,
            val object_type: String = "team_id",
            val object_id: String,

            val league_id: String,
            val h_index: String = "",
        ) : Event("click", collector = MAIN)
    }

    interface Brackets {
        class View(
            val view: String = "brackets",
            val element: String = "",
            val object_type: String,
            val object_id: String,
        ) : Event("view", collector = MAIN)

        class Click(
            val view: String,
            val element: String,
            val object_type: String,
            val object_id: String = "",
            val league_id: String = "",
            val parent_object_type: String = "",
            val parent_object_id: String = ""
        ) : Event("click", collector = MAIN)
    }

    class Gift {
        class CheckoutPress(
            val view: String = "gift",
            val element: String = "plan_selection",
            val object_type: String = "product_id",
            val object_id: String
        ) : Event("click", MAIN)

        class CheckoutPurchase(
            val object_type: String = "product_id",
            val object_id: String,
            val deliveryMethod: String
        ) : Event("gift_checkout_success", MAIN)

        class ViewedGiftDialog(
            val view: String = "gift"
        ) : Event("view", MAIN)
    }

    class Global {
        class GenericShare(
            val method: String,
            val content_type: String,
            val content: String,
            val item_id: String
        ) : Event("share", listOf(FIREBASE))

        @NoisyEvent
        class RequestFinish(
            val duration: String,
            val endpoint: String
        ) : Event("request_finished", listOf(FIREBASE))

        @NoisyEvent
        class RequestFailed(
            val endpoint: String,
            val error_description: String
        ) : Event("request_failed", listOf(FIREBASE))

        class View(
            val content_type: String,
            val item_id: String
        ) : Event("view_item", listOf(FIREBASE))

        class AdOnLoad(
            val view: String,
            val ad_view_id: String
        ) : Event("ad-on-load", listOf(MAIN))
    }

    interface LiveBlog {
        class Click(
            val view: String = "blog",
            val element: String,
            val object_type: String,
            val object_id: String,
            val blog_id: String = "",
            val article_id: String = "",
            val author_id: String = "",
            val page_order: String = ""
        ) : Event("click", collector = MAIN)

        class View(
            val view: String = "blog",
            val element: String,
            val object_type: String,
            val object_id: String,
            val blog_id: String = "",
            val box_score_state: String = ""
        ) : Event("view", collector = MAIN)

        class Slide(
            val view: String = "settings_drawer",
            val element: String,
            val object_type: String,
            val object_id: String,
            val blog_id: String = "",
        ) : Event("slide", collector = MAIN)

        @NoisyEvent
        class Impression(
            override val verb: String = "impress",
            override val view: String,
            override val filter_type: String? = null,
            override val filter_id: Long? = null,
            override val object_type: String,
            override val object_id: String,
            override val impress_start_time: Long,
            override val impress_end_time: Long,
            override val v_index: Long = -1L,
            override val h_index: Long = -1L,
            override val element: String,
            override val container: String? = null,
            override val page_order: Long? = null,
            override val parent_object_type: String? = null,
            override val parent_object_id: String? = null
        ) : ImpressionEvent(kafkaTopic = KafkaTopic.LIVE_BLOG_IMPRESSION)
    }

    interface Listen {
        data class View(
            val view: String = "listen",
            val element: String = ""
        ) : Event("view", collector = MAIN)

        class Click(
            val view: String = "listen",
            val element: String,
            val object_type: String,
            val object_id: String,
            val page_order: String,
            val h_index: String = "",
            val v_index: String = "",
        ) : Event("click", collector = MAIN)

        @NoisyEvent
        class Impression(
            override val verb: String = "impress",
            override val view: String = "listen",
            override val filter_type: String? = null,
            override val filter_id: Long? = null,
            override val object_type: String,
            override val object_id: String,
            override val impress_start_time: Long,
            override val impress_end_time: Long,
            override val v_index: Long = -1L,
            override val h_index: Long = -1L,
            override val element: String,
            override val container: String? = null,
            override val page_order: Long? = null,
            override val parent_object_type: String? = null,
            override val parent_object_id: String? = null
        ) : ImpressionEvent(kafkaTopic = KafkaTopic.LISTEN_TAB_IMPRESSION)
    }

    interface LiveRoom {
        class View(
            val view: String = "liveroom_mainstage",
            val object_type: String,
            val object_id: String,
            val element: String,
            val room_id: String,
            val entry_point: String = "",
            val is_live: String = "",
        ) : Event("view", collector = MAIN)

        class Click(
            val view: String = "liveroom_mainstage",
            val element: String,
            val object_type: String,
            val object_id: String,
            val room_id: String = "",
        ) : Event("click", collector = MAIN)

        class Custom(
            val verb: String,
            val view: String = "liveroom_mainstage",
            val element: String,
            val object_type: String,
            val object_id: String,
            val room_id: String = "",
        ) : Event(verb, collector = MAIN)
    }

    class Meta {
        class ReceiveKochavaAttribution(
            val article_id: String,
            val time_interval_from_start: String
        ) : Event("kochava_attribution_received", listOf(FIREBASE))
    }

    class Navigation {
        // v2.1
        class SwitchPrimaryTab(
            val view: String,
            val element: String = "core_navigation",
            val object_type: String,
            val object_id: String = ""
        ) : Event("click", collector = MAIN)
    }

    class Notification {
        class Open(
            val type: String,
            val message_id: String,
            val is_ghost_push: String,
            val push_url: String,
            val campaign_id: String,
            val content_id: String,
        ) : Event("push_open", listOf(FIREBASE))

        class SystemNotificationSettingDisabled(
            val view: String = "push_permission_settings"
        ) : Event("turn_off", MAIN)

        class SystemNotificationSettingEnabled(
            val view: String = "push_permission_settings"
        ) : Event("turn_on", MAIN)
    }

    class Onboarding {
        object Finished : Event("onboarding-completed", listOf(FIREBASE, KOCHAVA))

        class AccountCreated(
            val view: String = "onboarding",
            val object_type: String = "login_method_used",
            val object_id: String,
            val success: String,
            val error_code: String = "",
            val login_entry_point: String
        ) : Event("create_account", listOf(MAIN, FIREBASE, KOCHAVA))

        class FreeTrialDisplayed(
            val view: String = "onboarding_trial_interstitial",
            val object_type: String = "product_id",
            val object_id: String
        ) : Event("view", MAIN)

        class NoTrialDisplayed(
            val view: String = "onboarding_no_trial_interstitial",
            val object_type: String = "product_id",
            val object_id: String
        ) : Event("view", MAIN)

        class TrialButtonPressSkip(
            val view: String = "onboarding_interstitial",
            val element: String = "skip"
        ) : Event("click", MAIN)

        class StartFreeTrialPress(
            val view: String = "onboarding_interstitial",
            val element: String = "start_subscription",
            val object_type: String = "product_id",
            val object_id: String
        ) : Event("click", MAIN)

        class FollowTeamView(
            val view: String = "follow_team"
        ) : Event("view", MAIN)

        class FollowLeagueView(
            val view: String = "follow_league"
        ) : Event("view", MAIN)

        class FollowPodcastView(
            val view: String = "follow_podcast"
        ) : Event("view", MAIN)

        object OldOnboardingStart : Event("old_onboarding_start", FIREBASE)
        object NewOnboardingStart : Event("new_onboarding_start", FIREBASE)
    }

    class Payments {
        class PlanScreenView(
            val view: String = "plans_page",
            val element: String,
            val object_type: String = "product_id",
            val object_id: String,
            val article_id: String,
            val room_id: String,
        ) : Event("view", MAIN)

        class Click(
            val view: String = "plans_page",
            val element: String,
            val object_type: String = "product_id",
            val object_id: String,
            val article_id: String,
            val room_id: String,
            val action: String,
        ) : Event("click", MAIN)

        object KochavaDiscountedPlanScreenView : Event("Plans View", listOf(KOCHAVA))

        object NativePurchaseDialogDisplayed : Event("Checkout Start", listOf(KOCHAVA))

        object PlanScreenViewKochava : Event("Plans View", listOf(KOCHAVA))

        class ProductPurchase(
            val productId: String,
            val priceValue: String,
            val purchaseSignature: String,
            val purchaseOriginalJson: String,
            val currency: String,
            val priceLong: String,
            val isTrialPurchase: String
        ) : Event("Product Purchase", listOf(KOCHAVA, DATADOG))
    }

    interface Podcast {
        class DownloadSelected(
            val podcast_episode_id: String
        ) : Event("podcast_download_press", listOf(FIREBASE))

        class FollowClick(
            val podcast_id: String,
            val source: String
        ) : Event("podcast_follow_tapped", listOf(FIREBASE))

        data class Click(
            val view: String,
            val element: String,
            val object_type: String = "",
            val object_id: String = "",
            val v_index: String = "",
            val h_index: String = ""
        ) : Event("click", collector = MAIN)

        class Download(
            val view: String,
            val element: String,
            val object_type: String = "podcast_episode_id",
            val object_id: String = ""
        ) : Event("download", collector = MAIN)

        class Seek(
            val view: String = "podcast_player",
            val element: String = "timebar",
            val object_type: String = "podcast_episode_id",
            val object_id: String
        ) : Event("seek", collector = MAIN)

        class Share(
            val view: String = "player_menu",
            val element: String
        ) : Event("share", collector = MAIN)

        class Add(
            val view: String,
            val element: String = "follow",
            val object_type: String = "podcast_id",
            val object_id: String
        ) : Event("add", collector = MAIN)

        class Remove(
            val view: String,
            val element: String = "unfollow",
            val object_type: String = "podcast_id",
            val object_id: String
        ) : Event("remove", collector = MAIN)

        class View(
            val view: String,
            val element: String,
            val object_type: String = "",
            val object_id: String = ""
        ) : Event("view", collector = MAIN)

        class Play(
            val view: String,
            val element: String,
            val object_id: String,
            val object_type: String = "podcast_episode_id"
        ) : Event("play", collector = MAIN)

        class Pause(
            val view: String,
            val element: String,
            val object_id: String,
            val object_type: String = "podcast_episode_id"
        ) : Event("pause", collector = MAIN)
    }

    interface Profile {
        data class Click(
            val view: String = "profile",
            val element: String,
            val object_type: String = "",
            val object_id: String = ""
        ) : Event("click", MAIN)

        class View(
            val view: String,
            val element: String = ""
        ) : Event("view", MAIN)
    }

    interface Preferences {
        class View(
            val view: String = "preferences",
            val element: String
        ) : Event("view", MAIN)

        class Click(
            val view: String = "preferences",
            val element: String,
            val object_type: String,
            val object_id: String = "",
            val id_type: String = "",
            val id: String = ""
        ) : Event("click", MAIN)
    }

    class Referrals {
        class PageView(
            val view: String = "referrals_page"
        ) : Event("view", MAIN)

        class ShareLink(
            val view: String = "share_referral_code",
            val object_type: String = "referral_link_id",
            val object_id: String,
            val referrals_available: String
        ) : Event("leave_app", MAIN)

        class RequestMore(
            val view: String = "referrals_page",
            val element: String = "request_more_referrals"
        ) : Event("click", MAIN)
    }

    interface SavedStories {
        class View(
            val view: String = "saved_stories"
        ) : Event("view", MAIN)

        data class Click(
            val view: String = "saved_stories",
            val element: String,
            val object_type: String = "",
            val object_id: String = ""
        ) : Event("click", MAIN)
    }

    interface ManageAccount {
        class View(
            val view: String = "manage_account"
        ) : Event("view", MAIN)

        data class Click(
            val view: String = "manage_account",
            val element: String
        ) : Event("click", MAIN)
    }

    class Scores {
        class TabClick(
            val view: String = "scores",
            val element: String = "search"
        ) : Event("click", MAIN)

        // v2.1
        class ViewTab(
            val view: String = "scores",
            val element: String = "feed_navigation",
            val object_type: String,
            val object_id: String,

            val h_index: String = ""
        ) : Event("view", collector = MAIN)

        class Click(
            val view: String = "scores",
            val element: String,
            val object_type: String,
            val object_id: String,

            val h_index: String = "",
            val v_index: String = ""
        ) : Event("click", MAIN)

        class ViewBoxScore(
            val view: String = "box_score",
            val object_type: String = "game_id",
            val object_id: String
        ) : Event("view", MAIN)
    }

    class ScoresFeedTab {
        class View(
            val view: String = "scores",
            val element: String,
            val object_type: String = "",
            val object_id: String = "",
            val search: String = "",
            val h_index: String = "",
            val slate: String = "",
        ) : Event("view", MAIN)

        class Click(
            val view: String = "scores",
            val element: String,
            val object_type: String = "",
            val object_id: String = "",

            val search: String = "",
            val v_index: String = "",
            val h_index: String = "",
            val parent_object_type: String = "",
            val parent_object_id: String = "",
            val slate: String = "",
            val current_slate: String = "",
            val team_id: String = ""
        ) : Event("click", MAIN)

        @NoisyEvent
        class Impression(
            override val verb: String = "impress",
            override val view: String = "scores",
            override val filter_type: String? = null,
            override val filter_id: Long? = null,
            override val object_type: String,
            override val object_id: String,
            override val impress_start_time: Long,
            override val impress_end_time: Long,
            override val v_index: Long = -1L,
            override val h_index: Long = -1L,
            override val element: String,
            override val container: String? = null,
            override val page_order: Long? = null,
            override val parent_object_type: String? = null,
            override val parent_object_id: String? = null
        ) : ImpressionEvent(kafkaTopic = KafkaTopic.SCORES_IMPRESSION)
    }

    class ScoresTabs {
        class View(
            val view: String = "scores",
            val element: String,
            val object_type: String = "",
            val object_id: String = "",

            val slate: String = ""
        ) : Event("view", MAIN)

        class Click(
            val view: String,
            val element: String,
            val object_type: String,
            val object_id: String,

            val v_index: String = "",
            val parent_object_type: String = "",
            val parent_object_id: String = "",
            val slate: String = "",
            val current_slate: String = "",
            val ticket_partner: String = "",
        ) : Event("click", MAIN)

        @NoisyEvent
        class Impression(
            override val verb: String = "impress",
            override val view: String,
            override val filter_type: String? = null,
            override val filter_id: Long? = null,
            override val object_type: String,
            override val object_id: String,
            override val impress_start_time: Long,
            override val impress_end_time: Long,
            override val v_index: Long = -1L,
            override val h_index: Long = -1L,
            override val element: String,
            override val container: String? = null,
            override val page_order: Long? = null,
            override val parent_object_type: String? = null,
            override val parent_object_id: String? = null
        ) : ImpressionEvent(kafkaTopic = KafkaTopic.SCORES_IMPRESSION)
    }

    class Standings {
        class View(
            val view: String = "standings",
            val element: String,
            val object_type: String = "",
            val object_id: String = ""
        ) : Event("view", MAIN)

        class Click(
            val view: String = "standings",
            val element: String,
            val object_type: String = "",
            val object_id: String = ""
        ) : Event("click", MAIN)
    }

    class Search {
        class CancelClick(
            val view: String = "search",
            val element: String = "cancel"
        ) : Event("click", MAIN)

        // Nav V2
        class View(
            val view: String = "search"
        ) : Event("view", MAIN)

        class Click(
            val view: String = "search",
            val element: String = "search_result",
            val object_type: String,
            val object_id: String
        ) : Event("click", MAIN)
    }

    class Newsroom {
        class Click(
            val view: String,
            val element: String = "latest_news",
            val object_type: String = "",
            val object_id: String = "",
            val game_id: String = ""
        ) : Event("click", MAIN)

        class Play(
            val view: String,
            val element: String = "latest_news",
            val object_type: String = "",
            val object_id: String = "",
            val game_id: String = "",
            val podcast_episode_id: String = ""
        ) : Event("play", MAIN)

        class Add(
            val view: String,
            val element: String = "latest_news",
            val object_type: String = "",
            val object_id: String = "",
            val game_id: String = ""
        ) : Event("add", MAIN)

        class Remove(
            val view: String,
            val element: String = "latest_news",
            val object_type: String = "",
            val object_id: String = "",
            val game_id: String = ""
        ) : Event("remove", MAIN)

        class Download(
            val view: String,
            val element: String = "latest_news",
            val object_type: String = "",
            val object_id: String = "",
            val game_id: String = ""
        ) : Event("download", MAIN)
    }

    interface User {
        object LogOut : Event("log_out", MAIN)

        data class PrivacyAcknowledgment(
            val status: String
        ) : Event("privacy_policy_acknowledgment", listOf(FIREBASE))
    }

    // Followables
    interface ManageFollowing {
        class View(
            val view: String,
            val element: String = "following"
        ) : Event("view", MAIN)

        class ClickAddFollows(
            val view: String,
            val element: String = "add_follows"
        ) : Event("click", MAIN)

        class ClickEditFollowing(
            val view: String,
            val element: String = "following",
            val object_type: String = "edit"
        ) : Event("click", MAIN)

        class ReorderFollowing(
            val view: String,
            val element: String = "following",
            val object_type: String = "reorder"
        ) : Event("click", MAIN)

        class Follow(
            val view: String,
            val element: String,
            val object_type: String,
            val object_id: String,
            val team_id: String,
            val league_id: String,
            val author_id: String
        ) : Event("add", MAIN)

        class Unfollow(
            val view: String,
            val element: String,
            val object_type: String,
            val object_id: String,
            val team_id: String,
            val league_id: String,
            val author_id: String
        ) : Event("remove", MAIN)
    }

    // These events intentionally do not follow the usual avro conventions.
    // This is to keep the events the same as they are on web.
    interface AttributionSurvey {
        class View(
            val object_id: String,
            val object_type: String,
            val survey_location: String
        ) : Event("attribution_survey_view", MAIN)

        class SelectOption(
            val object_id: String,
            val object_type: String,
            val survey_location: String,
            val index_of_option: String,
            val option_selected: String
        ) : Event("attribution_survey_option_select", MAIN)

        class Submit(
            val object_id: String,
            val object_type: String,
            val survey_location: String,
            val index_of_option: String,
            val option_selected: String
        ) : Event("attribution_survey_submit", MAIN)

        class Exit(
            val object_id: String,
            val object_type: String,
            val survey_location: String
        ) : Event("attribution_survey_exit", MAIN)
    }

    interface FilterFollow {
        class View(
            val view: String = "filter_drawer"
        ) : Event("view", MAIN)

        class Click(
            val view: String = "filter_drawer",
            val element: String,
            val object_type: String,
            val object_id: String
        ) : Event("click", MAIN)
    }

    interface Billing {
        object BillingStartPurchase : Event("billingv2_start_purchase", FIREBASE)
        object BillingStartChangePlan : Event("billingv2_start_change_plan", FIREBASE)
        object BillingSuccessfulPurchase : Event("billingv2_successful_purchase", FIREBASE)
        object BillingFailedPurchase : Event("billingv2_failed_purchase", FIREBASE)
        object BillingPendingPurchase : Event("billingv2_pending_purchase", FIREBASE)
        object BillingCancelledPurchase : Event("billingv2_cancelled_purchase", FIREBASE)
        object BillingRegisterSubscription : Event("billingv2_register_subscription", FIREBASE)
        object BillingConsumePurchase : Event("billingv2_consume_purchase", FIREBASE)
        object BillingConsumeFailure : Event("billingv2_consume_failure", FIREBASE)
        class BillingMissingSku(
            val sku: String
        ) : Event("billingv2_missing_sku", FIREBASE)

        object LogGoogleSubStart : Event("log_google_sub_start", FIREBASE)
        object LogGoogleSubSuccess : Event("log_google_sub_success", FIREBASE)
        object LogGoogleSubRetry : Event("log_google_sub_retry", FIREBASE)
        object LogGoogleSubFailure : Event("log_google_sub_failure", FIREBASE)
    }

    interface InAppUpdates {
        object FlexibleUpdateShown : Event("flexible_update_shown", FIREBASE)
        object FlexibleUpdateComplete : Event("flexible_update_completed", FIREBASE)
        object FlexibleUpdateSkip : Event("flexible_update_skipped", FIREBASE)
    }

    interface ContentSettings {
        class DisplayThemeClick(
            val view: String = "settings_drawer",
            val element: String = "display_theme",
            val object_type: String
        ) : Event("click", MAIN)

        class TextSizeSlide(
            val view: String = "settings_drawer",
            val element: String = "text_size",
            val object_type: String = "slider_value",
            val object_id: String
        ) : Event("slide", MAIN)
    }

    interface LightModeIntro {
        class View(
            val view: String = "home",
            val element: String = "light_mode_popup"
        ) : Event("view", MAIN)

        class ViewOptionsClick(
            val view: String = "light_mode_popup",
            val element: String = "view_options"
        ) : Event("click", MAIN)
    }

    interface Diagnostics {
        class WebviewVersionDialogueView(
            val versionString: String
        ) : Event("webview_upgrade_dialogue_view", listOf(FIREBASE))

        object WebviewVersionDialogueUpgrade :
            Event("webview_upgrade_dialogue_upgrade", listOf(FIREBASE))

        object WebviewVersionDialogueDismiss :
            Event("webview_upgrade_dialogue_dismiss", listOf(FIREBASE))
    }

    interface Comments {
        class CommentsClick(
            val element: String = "comment_icon",
            val view: String,
            val object_type: String,
            val object_id: String
        ) : Event("click", MAIN)

        class ViewMoreClick(
            val element: String = "view_more_comments",
            val view: String,
            val object_type: String,
            val object_id: String
        ) : Event("click", MAIN)

        class Like(
            val view: String = "comments",
            val element: String = "like",
            val object_type: String,
            val object_id: String,
            val article_id: String = "",
            val headline_id: String = "",
            val podcast_episode_id: String = "",
            val filter_type: String = "",
            val v_index: String,
            val team_id: String = "",
        ) : Event("click", MAIN)

        class Unlike(
            val view: String = "comments",
            val element: String = "unlike",
            val object_type: String,
            val object_id: String,
            val article_id: String = "",
            val headline_id: String = "",
            val podcast_episode_id: String = "",
            val filter_type: String = "",
            val v_index: String,
            val team_id: String = "",
        ) : Event("click", MAIN)

        class Edit(
            val view: String = "comments",
            val element: String = "comment",
            val object_type: String = "comment_id",
            val object_id: String
        ) : Event("update", collector = MAIN)

        class Delete(
            val view: String = "comments",
            val element: String = "comment",
            val object_type: String = "comment_id",
            val object_id: String
        ) : Event("delete", collector = MAIN)

        class AllCommentsView(
            val view: String = "comments",
            val object_type: String,
            val object_id: String,
            val comment_view_link_id: String,
        ) : Event("view", MAIN)

        data class Dwell(
            val view: String,
            val element: String = "seconds",
            val object_type: String,
            val object_id: String,
            val comment_view_link_id: String,
            val seconds: String,
        ) : Event("heartbeat", MAIN)

        data class Sort(
            val view: String = "comments",
            val element: String = "sort",
            val object_type: String,
            val article_id: String = "",
            val headline_id: String = "",
            val podcast_episode_id: String = "",
            val team_id: String = "",
        ) : Event("sort", MAIN)

        class Flag(
            val view: String = "comments",
            val element: String,
            val object_type: String,
            val object_id: String,
            val filter_type: String = "",
            val v_index: String,
            val team_id: String = "",
        ) : Event("flag", collector = MAIN)
    }

    interface GameSpecificThreads {
        class ChangeTeamSpace(
            val view: String,
            val element: String = "team_space",
            val object_type: String = "game_id",
            val object_id: String = "",
            val current_team_id: String = "",
            val clicked_team_id: String = "",
        ) : Event("click", collector = MAIN)

        @NoisyEvent
        class Impression(
            override val verb: String = "impress",
            override val view: String = "box_score",
            override val filter_type: String? = "team_id",
            override val filter_id: Long? = null,
            override val object_type: String = "comment_id",
            override val object_id: String,
            override val impress_start_time: Long,
            override val impress_end_time: Long,
            override val v_index: Long = -1L,
            override val h_index: Long = -1L,
            override val element: String = "comment",
            override val container: String? = null,
            override val page_order: Long? = null,
            override val parent_object_type: String? = "game_id",
            override val parent_object_id: String? = null
        ) : ImpressionEvent(kafkaTopic = KafkaTopic.COMMENT_IMPRESSION)
    }

    class NotificationRequest {
        class View(
            val view: String,
            val experimentId: String,
            val variant: String
        ) : Event("view", MAIN)

        class Click(
            val view: String,
            val element: String,
            val experimentId: String,
            val variant: String
        ) : Event("click", MAIN)
    }

    class FeatureIntro {
        data class View(
            val view: String
        ) : Event("view", MAIN)

        data class Click(
            val view: String,
            val element: String
        ) : Event("click", MAIN)
    }

    // Schema-specific events
    abstract class ImpressionEvent(kafkaTopic: KafkaTopic) :
        Event(
            "impress",
            collector = CollectorKey.FLEXIBLE,
            kafkaTopic = kafkaTopic
        ),
        AnalyticsSchema.Contract.Impression
}