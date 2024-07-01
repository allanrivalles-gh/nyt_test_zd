//
//  Analytics.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 6/28/22.
//

import AthleticFoundation

public enum AnalyticsEvent {
    public struct MetaBlob: Codable, Equatable, Hashable {
        enum CodingKeys: String, CodingKey {
            case hasPaywall = "has_paywall"
            case isSubscriber = "is_subscriber"
            case hIndex = "h_index"
            case vIndex = "v_index"
            case carouselType = "carousel_type"
            case status, reminder, value, search
            case commentId = "comment_id"
            case idType = "id_type"
            case id
            case campaignId = "campaign_id"
            case templateId = "template_id"
            case messageId = "message_id"
            case notificationType = "notification_type"
            case source
            case isGhostPush = "is_ghost_push"
            case giftCardType = "gift_card_type"
            case headlineIndex = "headline_index"
            case authorId = "author_id"
            case userReaction = "user_reaction"
            case parentId = "parent_id"
            case pageOrder = "page_order"
            case planId = "plan_id"
            case container
            case unlike
            case loginEntryPoint = "login_entry_point"
            case nextTagId = "next_tag_id"
            case topicId = "topic_id"
            case leagueId = "league_id"
            case gameId = "game_id"
            case articleId = "article_id"
            case liveBlogId = "liveblog_id"
            case teamId = "team_id"
            case currentTeamId = "current_team_id"
            case clickedTeamId = "clicked_team_id"
            case teamMemberId = "team_member_id"
            case referralsAvailable = "referrals_available"
            case surveyLocation = "survey_location"
            case indexOfOption = "index_of_option"
            case optionSelected = "option_selected"
            case locale
            case deliveryMethod = "delivery_method"
            case errorCode = "error_code"
            case success
            case searchQuery = "search_query"
            case searchResultPosition = "search_result_position"
            case parentObjectType = "parent_object_type"
            case parentObjectIdentifier = "parent_object_id"
            case referrer
            case roomId = "room_id"
            case twitterHandle = "twitter_handle"
            case entryPoint = "entry_point"
            case blogId = "blog_id"
            case boxScoreState = "box_score_state"
            case contentType = "content_type"
            case contentId = "content_id"
            case action = "action"
            case isLive = "is_live"
            case isBeta = "is_beta"
            case tabTitle = "tab_title"
            case slate
            case currentSlate = "current_slate"
            case adViewId = "ad_view_id"
            case adTestVariant = "ads_v1_variant"
            case headlineId = "headline_id"
            case podcastId = "podcast_id"
            case podcastEpisodeId = "podcast_episode_id"
            case filterType = "filter_type"
            case boxScoreTab = "box_score_tab"
            case contentViewId = "content_view_id"
            case fbp
            case url
            case timezoneName = "timezone_name"
            case paywallCTA = "paywall_cta"
            case subscriberScore = "subscriber_score"
            case plan
            case commentViewLinkId = "comment_view_link_id"
            case seconds = "seconds"
            case isPhantom = "is_phantom"
            case ticketPartner = "ticket_partner"
            case isUnread = "unread"
        }

        // requires default values, non-optional
        let hasPaywall: Int
        let isSubscriber: Int
        let isBeta: Int

        public var hIndex: Int?
        public var vIndex: Int?
        public let pageOrder: Int?
        let carouselType: String?
        let status: String?
        let reminder: String?
        let value: String?
        let search: String?
        let commentId: String?
        let idType: String?
        let id: String?
        let campaignId: String?
        let templateId: String?
        let messageId: String?
        let notificationType: String?
        let source: String?
        let isGhostPush: Int?
        let giftCardType: String?
        let headlineIndex: Int?
        let authorId: String?
        let userReaction: String?
        let parentId: String?
        let planId: String?
        let container: String?
        let unlike: Int?
        let loginEntryPoint: String?
        let nextTagId: String?
        let topicId: String?
        let articleId: String?
        let liveBlogId: String?
        let leagueId: String?
        let gameId: String?
        let teamId: String?
        let currentTeamId: String?
        let clickedTeamId: String?
        let teamMemberId: String?
        let referralsAvailable: Int?
        let surveyLocation: String?
        let indexOfOption: Int?
        let optionSelected: String?
        let locale: String?
        let deliveryMethod: String?
        let errorCode: String?
        let success: Int?
        let searchQuery: String?
        let searchResultPosition: Int?
        let parentObjectType: AnalyticsEvent.ObjectType?
        let parentObjectIdentifier: String?
        let referrer: String?
        let roomId: String?
        let twitterHandle: String?
        let entryPoint: String?
        let blogId: String?
        let boxScoreState: String?
        let contentType: String?
        let contentId: String?
        let action: String?
        let isLive: Bool?
        let tabTitle: String?
        let slate: String?
        let currentSlate: String?
        let adViewId: String?
        let adTestVariant: String?
        let headlineId: String?
        let podcastId: String?
        let podcastEpisodeId: String?
        let filterType: String?
        let contentViewId: String?
        let url: String?
        let fbp: String?
        let timezoneName: String?
        let subscriberScore: Double?
        let paywallCTA: String?
        let plan: String?
        let commentViewLinkId: String?
        let seconds: Int?
        let isPhantom: Bool
        let ticketPartner: String?
        public var boxScoreTab: String?
        let isUnread: Bool?

        public init(
            indexH: Int? = -1,
            indexV: Int? = -1,
            carouselType: String? = nil,
            status: String? = nil,
            reminder: String? = nil,
            value: String? = nil,
            search: String? = nil,
            commentId: String? = nil,
            idType: String? = nil,
            id: String? = nil,
            giftCardType: String? = nil,
            headlineIndex: Int? = nil,
            authorId: String? = nil,
            userReaction: String? = nil,
            parentId: String? = nil,
            pageOrder: Int? = nil,
            planId: String? = nil,
            container: String? = nil,
            unlike: Bool? = nil,
            loginEntryPoint: String? = nil,
            nextTagId: String? = nil,
            topicId: String? = nil,
            articleId: String? = nil,
            liveBlogId: String? = nil,
            hasPaywall: Bool? = nil,
            leagueId: String? = nil,
            gameId: String? = nil,
            teamId: String? = nil,
            currentTeamId: String? = nil,
            clickedTeamId: String? = nil,
            teamMemberId: String? = nil,
            referralsAvailable: Int? = nil,
            surveyLocation: String? = nil,
            indexOfOption: Int? = nil,
            optionSelected: String? = nil,
            locale: String? = nil,
            deliveryMethod: String? = nil,
            errorCode: String? = nil,
            success: Bool? = nil,
            searchQuery: String? = nil,
            searchResultPosition: Int? = nil,
            parentObjectType: AnalyticsEvent.ObjectType? = nil,
            parentObjectIdentifier: String? = nil,
            referrer: String? = nil,
            roomId: String? = nil,
            twitterHandle: String? = nil,
            entryPoint: String? = nil,
            blogId: String? = nil,
            boxScoreState: String? = nil,
            contentType: String? = nil,
            contentId: String? = nil,
            action: String? = nil,
            isLive: Bool? = nil,
            tabTitle: String? = nil,
            slate: String? = nil,
            currentSlate: String? = nil,
            campaignData: AnalyticCampaignData? = nil,
            adViewId: String? = nil,
            adTestVariant: String? = nil,
            headlineId: String? = nil,
            podcastId: String? = nil,
            podcastEpisodeId: String? = nil,
            filterType: String? = nil,
            boxScoreTab: String? = nil,
            contentViewId: String? = nil,
            url: String? = nil,
            fbp: String? = nil,
            timezoneName: String? = nil,
            subscriberScore: Double? = nil,
            paywallCTA: String? = nil,
            plan: String? = nil,
            commentViewLinkId: String? = nil,
            seconds: Int? = nil,
            isPhantom: Bool = false,
            ticketPartner: String? = nil,
            isUnread: Bool? = nil,
            notificationType: String? = nil,
            requiredValues: AnalyticsRequiredValues
        ) {
            self.hIndex = indexH
            self.vIndex = indexV
            self.carouselType = carouselType
            self.status = status
            self.reminder = reminder
            self.search = search
            self.commentId = commentId
            self.idType = idType
            self.id = id
            self.giftCardType = giftCardType
            self.headlineIndex = headlineIndex
            self.authorId = authorId
            self.userReaction = userReaction
            self.parentId = parentId
            self.pageOrder = pageOrder
            self.planId = planId
            self.container = container
            self.unlike = unlike?.toInt()
            self.loginEntryPoint = loginEntryPoint
            self.nextTagId = nextTagId
            self.topicId = topicId
            self.articleId = articleId
            self.liveBlogId = liveBlogId
            self.leagueId = leagueId
            self.gameId = gameId
            self.teamId = teamId
            self.currentTeamId = currentTeamId
            self.clickedTeamId = clickedTeamId
            self.teamMemberId = teamMemberId
            self.referralsAvailable = referralsAvailable
            self.surveyLocation = surveyLocation
            self.indexOfOption = indexOfOption
            self.optionSelected = optionSelected
            self.locale = locale
            self.deliveryMethod = deliveryMethod
            self.errorCode = errorCode
            self.success = success?.toInt()
            self.searchQuery = searchQuery
            self.searchResultPosition = searchResultPosition
            self.parentObjectType = parentObjectType
            self.parentObjectIdentifier = parentObjectIdentifier
            self.isSubscriber = requiredValues.isSubscriber == true ? 1 : 0
            self.hasPaywall = requiredValues.isSubscriber == true ? 0 : 1
            self.isBeta = requiredValues.isBeta == true ? 1 : 0

            if let campaignData = campaignData {
                self.campaignId = campaignData.campaignId
                self.isGhostPush = campaignData.isGhostPush ? 1 : 0
                self.templateId = campaignData.templateId
                self.messageId = campaignData.messageId
                self.notificationType = campaignData.notificationType
                self.value = campaignData.url
                self.source = campaignData.source
                self.referrer = nil
            } else {
                self.campaignId = nil

                self.isGhostPush = nil
                self.templateId = nil
                self.messageId = nil
                self.notificationType = notificationType
                self.value = value
                self.source = requiredValues.source
                self.referrer = referrer
            }

            self.roomId = roomId
            self.twitterHandle = twitterHandle
            self.entryPoint = entryPoint
            self.blogId = blogId
            self.boxScoreState = boxScoreState
            self.contentType = contentType
            self.contentId = contentId
            self.action = action
            self.isLive = isLive
            self.tabTitle = tabTitle
            self.slate = slate
            self.currentSlate = currentSlate
            self.adViewId = adViewId
            self.adTestVariant = adTestVariant
            self.headlineId = headlineId
            self.podcastId = podcastId
            self.podcastEpisodeId = podcastEpisodeId
            self.filterType = filterType
            self.boxScoreTab = boxScoreTab
            self.contentViewId = contentViewId
            self.url = url
            self.fbp = fbp
            self.timezoneName = timezoneName
            self.subscriberScore = subscriberScore
            self.paywallCTA = paywallCTA
            self.plan = plan
            self.commentViewLinkId = commentViewLinkId
            self.seconds = seconds
            self.isPhantom = isPhantom
            self.ticketPartner = ticketPartner
            self.isUnread = isUnread
        }
    }

    public enum Verb: String, Codable, Equatable {
        case accepted
        case add
        case articleSingleView = "article_single_view"
        case articleAppeared = "article_appeared"
        case appActive = "app_active"
        case appBackground = "app_background"
        case appForeground = "app_foreground"
        case appWillResignActive = "app_will_resign_active"
        case appWillTerminate = "app_will_terminate"
        case attributionSurveyExit = "attribution_survey_exit"
        case attributionSurveyOptionSelect = "attribution_survey_option_select"
        case attributionSurveySubmit = "attribution_survey_submit"
        case attributionSurveyView = "attribution_survey_view"
        case backgroundFetch = "background_fetch"
        case click
        case createAccount = "create_account"
        case download
        case failure
        case giftCheckoutSuccess = "gift_checkout_success"
        case ghostPushReceived = "ghost_push_received"
        case heartbeat = "heartbeat"
        case markOutdated = "mark_outdated"
        case impress
        case install
        case joinRoom = "join_room"
        case leaveApp = "leave_app"
        case login
        case logout = "log_out"
        case pause
        case play
        case pushOpen = "push_open"
        case on
        case remove
        case removed
        case seek
        case skip
        case success
        case turnOff = "turn_off"
        case uninstall
        case view
    }

    public enum Element: String, Codable, Equatable {
        case a1
        case activity
        case allow
        case allGames = "all_games"
        case allGrades = "all_grades"
        case allPlays = "all_plays"
        case announcement = "announcement"
        case apple = "apple"
        case appIconSetting = "app_icon_setting"
        case appSettings = "app_settings"
        case article
        case articleSingle = "article_single"
        case author
        case rewindBackward = "rewind_backward"
        case baseballCurrentInning = "baseball_current_inning"
        case baseballFinalPlayers = "baseball_final_players"
        case baseballStartingPitchers = "baseball_starting_pitchers"
        case boxScore = "box_score"
        case boxScoreDiscuss = "box_score_discuss"
        case boxScoreNav = "box_score_nav"
        case bracketsNav = "brackets_nav"
        case cancelRequest = "cancel_request"
        case clickNext = "click_next"
        case clickPrev = "click_prev"
        case clickToTop = "click_to_top"
        case close
        case comment
        case commentIcon = "comment_icon"
        case continueButton = "continue"
        case coreNavigation = "core_navigation"
        case dataPreferences = "data_preferences"
        case deleteAccount = "delete_account"
        case discover
        case discuss
        case dismiss
        case displayTheme = "display_theme"
        case dontAllow = "dont_allow"
        case downloads
        case emailPreferenceOff = "email_preference_off"
        case emailPreferenceOn = "email_preference_on"
        case emailPreferences = "email_preferences"
        case facebook = "facebook"
        case fastForward = "fast_forward"
        case feedNavigation = "feed_navigation"
        case fiveHero = "five_hero"
        case flag
        case follow
        case following
        case fourContent = "four_content"
        case fourHero = "four_hero"
        case gameDetails = "game_details"
        case gameModule = "game_module"
        case gameModuleBoxScore = "game_module_box_score"
        case gameModuleGrades = "game_module_grades"
        case gameModuleLiveBlog = "game_module_live_blog"
        case gameModuleComments = "game_module_comments"
        case gameModuleStats = "game_module_stats"
        case gameModulePlays = "game_module_plays"
        case gameModuleGame = "game_module_game"
        case gameOdds = "game_odds"
        case gameTab = "game_tab"
        case google = "google"
        case grade
        case gradePlayers = "grade_players"
        case gradePlayersView = "grade_players_view"
        case headline
        case headlineMultiple = "headline_multiple"
        case headlineSingle = "headline_single"
        case home
        case hostProfile = "host_profile"
        case inGameBoxScore = "ingame_box_score"
        case inGameBoxScoreGame = "ingame_box_score_game"
        case injuryReport = "injury_report"
        case inTextLink = "in_text_link"
        case keyMoments = "key_moments"
        case latestNews = "latest_news"
        case latestPodcastEpisodes = "latest_podcast_episodes"
        case league
        case leagueHeader = "league_header"
        case learnMore = "learn_more"
        case leagueScoresAndSchedules = "league_scores_and_schedules"
        case leaveRoom = "leave_room"
        case leaveStage = "leave_stage"
        case like
        case liveBlogs = "live_blogs"
        case liveRoom = "live_room"
        case liveRoomDetails = "liveroom_details"
        case logout
        case manageSubscriptions = "manage_subscriptions"
        case minimizeRoom = "minimize_room"
        case moreForYou = "more_for_you"
        case mute
        case newLiveBlogUpdateCta = "new_update_cta"
        case news
        case nflDownAndDistance = "nfl_down_and_distance"
        case notificationSettings = "notification_settings"
        case notifications
        case notificationsOff = "notifications_off"
        case notificationsOn = "notifications_on"
        case notNow = "not_now"
        case newYorkTimes = "new_york_times"
        case open
        case ok
        case planSelection = "plan_selection"
        case player
        case playerGrades = "player_grades"
        case playerLineup = "player_lineup"
        case playerMenu = "player_menu"
        case playerQueue = "player_queue"
        case playerStats = "player_stats"
        case playLog = "play_log"
        case playSpeed = "play_speed"
        case playsTabNav = "plays_tab_nav"
        case podcast
        case podcastEpisode = "podcast_episode"
        case podcastEpisodeClip = "podcast_episode_clip"
        case popular
        case postGameBoxScore = "postgame_box_score"
        case postGameBoxScoreGame = "postgame_box_score_game"
        case preGameBoxScore = "pregame_box_score"
        case progress
        case preGameBoxScoreGame = "pregame_box_score_game"
        case privacyPage = "privacy_page"
        case pushNotification = "push_notification"
        case pushNotificationOpenArticle = "push_notification_open_article"
        case pushNotificationSaveArticle = "push_notification_save_article"
        case pushNotificationDismiss = "push_notification_dismiss"
        case rankedModule = "ranked_module"
        case reaction
        case readMore = "read_more"
        case recentGames = "recent_games"
        case recentPlays = "recent_plays"
        case recommendedPodcasts = "recommended_podcasts"
        case recover
        case referralsPage = "referrals_page"
        case refresh
        case region
        case relatedStories = "related_stories"
        case removeArticle = "remove_article"
        case requestMoreReferrals = "request_more_referrals"
        case requestToSpeak = "request_to_speak"
        case restorePurchase = "restore_purchase"
        case roomEndAcknowledge = "room_end_acknowledge"
        case savedStories = "saved_stories"
        case saveProfile = "save_profile"
        case scoringBreakdown = "scoring_breakdown"
        case scoringSummary = "scoring_summary"
        case search
        case seasonStats = "season_stats"
        case seconds = "seconds"
        case sendMessage = "send_message"
        case sevenPlusHero = "sevenplus_hero"
        case share
        case sixHero = "six_hero"
        case skip
        case slateNav = "slate_nav"
        case sleepTimer = "sleep_timer"
        case soccerGoalsSummary = "soccer_goals_summary"
        case sort
        case speak
        case standings = "standings"
        case startSubscription = "start_subscription"
        case subscribe = "subscribe"
        case statsTabNav = "stats_tab_nav"
        case swipeNext = "swipe_next"
        case swipePrev = "swipe_prev"
        case tickets
        case timelineTabNav = "timeline_tab_nav"
        case team
        case teamComparison = "team_comparison"
        case teamLeaders = "team_leaders"
        case teamSpace = "team_space"
        case teamScoresAndSchedules = "team_scores_and_schedules"
        case teamStats = "team_stats"
        case teamPlayer = "team_player"
        case teamRoster = "team_roster"
        case textSize = "text_size"
        case threeContent = "three_content"
        case threeHero = "three_hero"
        case timebar
        case titleTags = "title_tags"
        case today
        case topic
        case topComments = "top_comments"
        case topPerformers = "top_performers"
        case twitter
        case twoHero = "two_hero"
        case topCommentsModuleReply = "top_comment_module_reply"
        case topCommentsModuleJoinDiscussion = "top_comment_module_join_discussion"
        case topCommentsModuleComment = "top_comment_module_comment"
        case unfollow
        case unlike
        case updates
        case userProfile = "user_profile"
        case userReaction = "user_reaction"
        case viewMoreComments = "view_more_comments"
        case voiceOver = "voiceOver"
    }

    public enum Container: String, Codable, Equatable {
        case a1 = "a1"
        case announcement = "announcement"
        case article
        case articleBoxScore = "article_box_score"
        case articleSingle = "article_single"
        case boxScore = "box_score"
        case contentTopperHero = "content_topper_hero"
        case feed
        case fiveHero = "five_hero"
        case following
        case fourContent = "four_content"
        case fourHero = "four_hero"
        case gameModule = "game_module"
        case headlineMultiple = "headline_multiple"
        case headlineSingle = "headline_single"
        case latestPodcastEpisodes = "latest_podcast_episodes"
        case league
        case liveBlog = "live_blog"
        case liveBlogs = "live_blogs"
        case liveRoom = "live_room"
        case moreForYou = "more_for_you"
        case podcast
        case popular
        case rankedModule = "ranked_module"
        case recommendedPodcasts = "recommended_podcasts"
        case relatedStories = "related_stories"
        case sevenPlusHero = "seven_plus_hero"
        case singleContent = "single_content"
        case sixHero = "six_hero"
        case threeHero = "three_hero"
        case threeContent = "three_content"
        case topic
        case twoHero = "two_hero"
        case url
    }

    public enum ObjectType: String, Codable, Equatable {
        case activity
        case all
        case allPlays = "all_plays"
        case allMoments = "all_moments"
        case appIconName = "app_icon_name"
        case announcementId = "announcement_id"
        case articleId = "article_id"
        case authorId = "author_id"
        case blogId = "blog_id"
        case blogPostId = "blog_post_id"
        case boxScore = "box_score"
        case brackets
        case cancel
        case clearInput = "clear_input"
        case collegeHoopsWeekly = "college_hoops_weekly"
        case commentId = "comment_id"
        case commentReplies = "comment_replies"
        case communityNotifications = "community_notifications"
        case content
        case dark
        case defaultObjectType = "object_type"
        case discover
        case discuss
        case discussTab = "discuss_tab"
        case discussionId = "discussion_id"
        case edit
        case filter
        case following
        case frontPageAlternate = "frontpage"
        case frontPage = "front_page"
        case gameId = "game_id"
        case gameResults = "game_results"
        case gameStart = "game_start"
        case games
        case gameTab = "game_tab"
        case grade
        case gradesTab = "grades_tab"
        case global
        case headlineId = "headline_id"
        case home
        case keyMoments = "key_moments"
        case largeHeadlines = "large_headlines"
        case latestPodcasts = "latest_podcasts"
        case leagueId = "league_id"
        case light
        case listen
        case liveBlogId = "live_blog_id"
        case liveBlogTab = "liveblog_tab"
        case loginMethodUsed = "login_method_used"
        case matchTime = "match_time"
        case mediumHeadlines = "medium_headlines"
        case messageId = "message_id"
        case mostLiked = "most_liked"
        case newest
        case off
        case oldest
        case on
        case optOut = "opt_out"
        case optIn = "opt_in"
        case player
        case playerId = "player_id"
        case playId = "play_id"
        case playSpeed = "player_speed"
        case playsTab = "plays_tab"
        case podcasts
        case podcastEpisode = "podcast_episode"
        case podcastEpisodeClip = "podcast_episode_clip"
        case podcastEpisodeId = "podcast_episode_id"
        case podcastId = "podcast_id"
        case podcastSeries = "podcast_series"
        case postId = "post_id"
        case productId = "product_id"
        case profile
        case progress
        case qandaId = "qanda_id"
        case referralLinkId = "referral_link_id"
        case reorder
        case roster
        case roomId = "room_id"
        case round1Tab = "first_round_tab"
        case round2Tab = "second_round_tab"
        case round3Tab = "third_round_tab"
        case round4Tab = "fourth_round_tab"
        case round5Tab = "fifth_round_tab"
        case round6Tab = "sixth_round_tab"
        case round7Tab = "seventh_round_tab"
        case schedule
        case scores
        case scoringPlays = "scoring_plays"
        case search
        case smallHeadlines = "small_headlines"
        case standings
        case stats
        case statsTab = "stats_tab"
        case stories
        case system
        case teamId = "team_id"
        case currentTeamId = "current_team_id"
        case clickedTeamId = "clicked_team_id"
        case teamMemberId = "team_member_id"
        case theAthleticNews = "the_athletic_news"
        case timelineTab = "timeline_tab"
        case timerLength = "timer_length"
        case topic
        case topicId = "topic_id"
        case topSportsNews = "top_sports_news"
        case trending
        case ungrade
        case unknown
        case updates
        case userId = "user_id"
        case viewLinkId = "view_link_id"
    }

    public enum View: String, Codable, Equatable {
        case appIconSetting = "app_icon_setting"
        case appWidget = "app_widget"
        case article
        case attributionSurvey = "attribution_survey"
        case blog
        case boxScore = "box_score"
        case boxScorePreGame = "pregame_box_score_game"
        case boxScoreInGame = "ingame_box_score_game"
        case boxScorePostGame = "postgame_box_score_game"
        case boxScoreLiveBlogPreGame = "pregame_box_score_liveblog"
        case boxScoreLiveBlogInGame = "ingame_box_score_liveblog"
        case boxScoreLiveBlogPostGame = "postgame_box_score_liveblog"
        case boxScoreDiscussPreGame = "pregame_box_score_discuss"
        case boxScoreDiscussInGame = "ingame_box_score_discuss"
        case boxScoreDiscussPostGame = "postgame_box_score_discuss"
        case boxScoreGradesInGame = "ingame_box_score_grades"
        case boxScoreGradesPostGame = "postgame_box_score_grades"
        case boxScorePlaysInGame = "ingame_box_score_plays"
        case boxScorePlaysPostGame = "postgame_box_score_plays"
        case boxScoreStatsInGame = "ingame_box_score_stats"
        case boxScoreStatsPostGame = "postgame_box_score_stats"
        case boxScoreTimelineInGame = "ingame_box_score_timeline"
        case boxScoreTimelinePostGame = "postgame_box_score_timeline"
        case brackets
        case comment
        case comments
        case commentsDrawer = "comments_drawer"
        case commentsPreview = "comments_preview"
        case discover
        case deeplink = "deep_link"
        case editFollowing = "edit_following"
        case emailCreateAccountPage = "email_create_account_page"
        case empty = ""
        case feed
        case frontPage = "front_page"
        case gameFeed = "game_feed"
        case gameTab = "game_tab"
        case gift
        case gradePlayersGameTab = "grade_players_game_tab"
        case gradePlayersGradesTab = "grade_players_grades_tab"
        case gradesTab = "grades_tab"
        case gradesTabList = "grades_tab_list"
        case gradesTabModal = "grades_tab_modal"
        case headline
        case home
        case hostProfile = "host_profile"
        case league
        case listen
        case liveblog
        case liveRoomChat = "liveroom_chat"
        case liveRoomMainStage = "liveroom_mainstage"
        case liveRoomMiniPlayer = "liveroom_miniplayer"
        case logout = "log_out"
        case manageAccount = "manage_account"
        case matchStats = "match_stats"
        case offerInvalidPopUp = "offer_invalid_pop_up"
        case onboarding
        case onboardingInterstitial = "onboarding_interstitial"
        case onboardingNoTrialInterstitial = "onboarding_no_trial_interstitial"
        case onboardingTrialInterstitial = "onboarding_trial_interstitial"
        case nbaGameHubModal = "nba_game_hub_modal"
        case nhlGameHubModal = "nhl_game_hub_modal"
        case notifications
        case paywall = "paywall"
        case playerPage = "player_page"
        case plansPage = "plans_page"
        case podcasts
        case podcastBrowse = "podcast_browse"
        case podcastDownloads = "podcast_downloads"
        case podcastEpisode = "podcast_episode"
        case podcastPlayer = "podcast_player"
        case podcastsPage = "podcast_page"
        case podcastShow = "podcast_show"
        case preferences
        case privacyPage = "privacy_page"
        case profile
        case pushPermissionSettings = "push_permission_settings"
        case pushPrePrompt = "push_pre_prompt"
        case pushPrePromptSystem = "push_pre_prompt_system"
        case pushNotification = "push_notification"
        case referralsPage = "referrals_page"
        case roster
        case savedStories = "saved_stories"
        case schedule
        case scores
        case search
        case shareArticle = "share_article"
        case shareHeadline = "share_headline"
        case shareReferralCode = "share_referral_code"
        case sideBar
        case signInPage = "sign_in_page"
        case signUpPage = "sign_up_page"
        case standings = "standings"
        case stats
        case team
        case universalLink = "universal_link"
    }

    public enum filterType: String, Codable, Equatable {
        case topCommentsModule = "top_comment_module"
    }
}

public enum Analytics {

    public static func track<T>(event: T?, manager: AvroAnalyticsActor<T>) {
        guard let event = event else { return }
        Task {
            await manager.track(record: event)
        }
    }

    public static func track(event: AnalyticsEventRecord?) {
        guard let event = event else { return }
        Analytics.track(event: event, manager: AnalyticsManagers.events)
    }

    /// Async event tracking
    public static func track<T>(event: T?, manager: AvroAnalyticsActor<T>) async {
        guard let event = event else { return }

        await manager.track(record: event)
    }

    public static func track(event: AnalyticsEventRecord?) async {
        guard let event = event else { return }

        await Analytics.track(event: event, manager: AnalyticsManagers.events)
    }
}
