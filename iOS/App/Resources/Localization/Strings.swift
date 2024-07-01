//
//  Strings.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 4/16/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation

// swift-format-ignore
enum Strings: String, Localizable, CaseIterable {

    var bundle: Bundle { .main }
    var baseFilename: String { "Base" }

    // MARK: - General
    case yes
    case theAthletic
    case add
    case `continue`

    case lastUpdated = "last_updated"
    case genericError = "generic_error"
    case tryAgainLaterErrorMessage
    case invalidUrl
    case league
    case team

    // MARK: - Team Hubs
    case hubFeedTabTitle
    case hubThreadsTabTitle
    case hubScheduleTabTitle
    case hubStandingsTabDefaultTitle
    case hubStandingsTabSoccerTitle
    case hubRosterTabTitle
    case hubStatsTabTitle
    case hubSquadTabTitle
    case hubThreadsMoreButton
    case hubBracketTabTitleNorthAmerica
    case hubBracketTabTitleWorld

    case unfollow
    case pushNotifications
    case unfollowAlertConfirmationTitle

    case youreNowFollowingTeam
    case youreNowFollowingLeague
    case youreNowFollowingAuthor
    case youreNoLongerFollowingTeam
    case youreNoLongerFollowingLeague
    case youreNoLongerFollowingAuthor

    case standingsNoData
    case standingsNoDataExtraFormat

    case hubSchedulesNoGamesForSelectedFilter

    // MARK: - Player Hubs

    case playerBio
    case age
    case born
    case nationality
    case handedness
    case college
    case height
    case weight

    // MARK: - Team Stats
    case teamStatsTeamTitle
    case teamStatsPlayerTitle
    case playerStatsNameTitle
    case playerStatsGoalkeeperPlayersTitle
    case playerStatsOutfieldPlayersTitle
    case playerStatsSkatingPlayersTitle
    case playerStatsGoaltendingPlayersTitle
    case playerStatsNoDataTitle
    case playerStatsNoDataBody

    // MARK: - Team Roster
    case teamRosterNoDataTitle
    case teamRosterNoDataBody
    case teamRosterPositionAbbreviation
    case teamRosterHeightAbbreviation
    case teamRosterWeightAbbreviation
    case teamRosterDateOfBirthAbbreviation
    case teamRosterAgeAbbreviation
    case teamRosterPitchersTitle
    case teamRosterCatchersTitle
    case teamRosterInfieldersTitle
    case teamRosterOutfieldersTitle
    case teamRosterDesignatedHittersTitle
    case teamRosterOffenseTitle
    case teamRosterDefenseTitle
    case teamRosterSpecialTeamsTitle
    case teamRosterForwardTitle
    case teamRosterCentersTitle
    case teamRosterLeftWingsTitle
    case teamRosterRightWingsTitle
    case teamRosterGoaliesTitle
    case teamRosterGoalkeepersTitle
    case teamRosterOutfieldPlayersTitle

    // MARK: - Leagues
    case leagueNcaaf
    case leagueNcaamb
    case leagueNcaawb

    // MARK: Stats
    case expectedGoalsAbbreviation
    case goalkeeperAbbreviation
    case defenderAbbreviation
    case wingBackAbbreviation
    case defensiveMidfielderAbbreviation
    case midfielderAbbreviation
    case attackingMidfielderAbbreviation
    case attackerAbbreviation
    case strikerAbbreviation
    case substituteAbbreviation
    case centerAbbreviation
    case cornerbackAbbreviation
    case defensiveBackAbbreviation
    case defensiveEndAbbreviation
    case defensiveLinemanAbbreviation
    case defensiveTackleAbbreviation
    case fullbackAbbreviation
    case freeSafetyAbbreviation
    case kickerAbbreviation
    case insideLinebackerAbbreviation
    case linebackerAbbreviation
    case longSnapperAbbreviation
    case middleLinebackerAbbreviation
    case noseTackleAbbreviation
    case offensiveGuardAbbreviation
    case offensiveLinemanAbbreviation
    case outsideLinebackerAbbreviation
    case offensiveTackleAbbreviation
    case punterAbbreviation
    case quarterbackAbbreviation
    case runningBackAbbreviation
    case safetyAbbreviation
    case strongSafetyAbbreviation
    case tightEndAbbreviation
    case wideReceiverAbbreviation
    case smallForwardAbbreviation
    case shootingGuardAbbreviation
    case powerForwardAbbreviation
    case goalieAbbreviation
    case defenseAbbreviation
    case forwardAbbreviation
    case leftWingAbbreviation
    case rightWingAbbreviation
    case pointGuardAbbreviation
    case catcherAbbreviation
    case centerFieldAbbreviation
    case designatedHitterAbbreviation
    case firstBaseAbbreviation
    case leftFieldAbbreviation
    case pinchHitterAbbreviation
    case pinchRunnerAbbreviation
    case pitcherAbbreviation
    case reliefPitcherAbbreviation
    case rightFieldAbbreviation
    case secondBaseAbbreviation
    case shortStopAbbreviation
    case startingPitcherAbbreviation
    case thirdBaseAbbreviation
    case centerForwardAbbreviation
    case forwardCenterAbbreviation
    case forwardGuardAbbreviation
    case guardAbbreviation
    case guardForwardAbbreviation

    // MARK: Stats category
    case statCategoryStandardTitle
    case statCategoryAdvancedTitle
    case statCategorySummaryTitle
    case statCategoryPassingTitle
    case statCategoryRushingTitle
    case statCategoryReceivingTitle
    case statCategoryPuntsTitle
    case statCategoryPuntReturnsTitle
    case statCategoryPenaltiesTitle
    case statCategoryMiscReturnsTitle
    case statCategoryKickoffsTitle
    case statCategoryKickReturnsTitle
    case statCategoryIntReturnsTitle
    case statCategoryFumblesTitle
    case statCategoryFieldGoalsTitle
    case statCategoryExtraPointsKicksTitle
    case statCategoryExtraPointsConversionsTitle
    case statCategoryDefenseTitle
    case statCategoryEfficiencyGoalToGoTitle
    case statCategoryEfficiencyRedzoneTitle
    case statCategoryEfficiencyThirdDownTitle
    case statCategoryEfficiencyFourthDownTitle
    case statCategoryFirstDownsTitle
    case statCategoryInterceptionsTitle
    case statCategoryTouchdownsTitle
    case statCategoryKickingTitle
    case statCategoryBattingTitle
    case statCategoryPitchingTitle
    case statCategoryFieldingTitle

    // MARK: - Scores Today/Landing
    case scoresSearchPlaceholder
    case scoresSearchResultSingularFormat
    case scoresSearchResultPluralFormat
    case scoresTopGamesTitle
    case scoresTopGamesSubtitle
    case standingsTeamHeading

    // MARK: - Standings
    case standingsNameDefault
    case standingsNameSoccerSingular
    case standingsNameSoccerPlural
    case seasonNameFormat
    case standingsChampionsLeagueLegendTitle
    case standingsEuropaLeagueLegendTitle
    case standingsEuropaConferenceLeagueLegendTitle
    case standingsPromotionLegendTitle
    case standingsPromotionPlayoffsLegendTitle
    case standingsRelegationLegendTitle
    case standingsRelegationPlayoffsLegendTitle
    case standingsRoundOf16QualificationLegendTitle

    // MARK: - Box score

    case gameBoxScoreTabTitle
    case gameDiscussTabTitle
    case gameMatchTabTitle
    case gameGradesTabTitle
    case loadingFailed
    case previousGamesTitle
    case recentMatchesTitle
    case noRecentGames
    case noRecentMatches
    case gameOdds
    case gameOddsPoweredByTitle
    case result
    case winSymbol
    case lossSymbol
    case tieSymbol
    case drawSymbol
    case aggregateScoreAbbreviationFormat
    case spread
    case total
    case money
    case scoringSummary
    case totalLongAbbreviation
    case totalShortAbbreviation
    case oddsLine
    case oddsDirectionOver
    case oddsDirectionOverAbbreviation
    case oddsDirectionUnder
    case oddsDirectionUnderAbbreviation
    case oddsEven
    case gameDetailsTitle
    case gameDetailsNetworkTitle
    case gameDetailsLocationTitle
    case gameDetailsWeatherTitle
    case gameDetailsOpeningOddsTitle
    case gameDetailsOfficialsTitle
    case gameDetailsSoccerOfficialRefereeTitle
    case gameDetailsSoccerOfficialAssistantTitle
    case gameDetailsSoccerOfficialFourthTitle
    case gameDetailsSoccerOfficialVARTitle
    case gameDetailsSoccerOfficialAssistantVARTitle
    case gameTeamComparisonTitle
    case seasonStatsTitle
    case teamComparisonMatchupTitle
    case playerLineUpTitle
    case relatedArticlesTitle
    case americanFootballPossessionSummaryFormat
    case americanFootballPossessionGoal
    case recentPlaysTitle
    case recentMomentsTitle
    case recentKeyMomentsTitle
    case recentPlaysAllPlaysTitle
    case recentMomentsAllMomentsTitle
    case shotGoalTitle
    case shotSaveTitle
    case mlbOutTitle
    case mlbGamesPlayedAbbreviation
    case winLossAbbreviation
    case startingPitchersTitle
    case probablePitchersTitle
    case probable
    case teamLeadersTitle
    case topPerformersTitle
    case injuryReport
    case noInjuries
    case oneMoreInjury
    case injuryStatusD7
    case injuryStatusD10
    case injuryStatusD15
    case injuryStatusD60
    case injuryStatusDay
    case injuryStatusDayToDay
    case injuryStatusDoubtful
    case injuryStatusQuestionable
    case injuryStatusOut
    case injuryStatusOutForSeason
    case injuryStatusOutIndefinitely
    case multipleMoreInjuriesFormat
    case mlbRunsAbbreviation
    case mlbHitsAbbreviation
    case mlbErrorsAbbreviation
    case mlbRightHandPitcherAbbreviation
    case mlbLeftHandPitcherAbbreviation
    case mlbRightHandBatterAbbreviation
    case mlbLeftHandBatterAbbreviation
    case mlbStrikeoutAbbreviation
    case mlbWalkAbbreviation
    case mlbWinTitle
    case mlbLossTitle
    case mlbSaveTitle
    case baseballCurrentInningTitle
    case baseballCurrentInningBallsTitle
    case baseballCurrentInningStrikesTitle
    case baseballCurrentInningOutsTitle
    case baseballCurrentInningNextBatterFormat
    case teamWinsOnAggregateFormat
    case teamWinsOnPenaltiesFormat
    case discussPlayOption
    case delay
    case downAndDistanceTitle

    // MARK: - Grades
    case gradesNotAvailable
    case gradesYourGradeFormat
    case gradesNotGraded
    case gradedAllPlayers
    case gradesTotalGradesSingularFormat
    case gradesTotalGradesPluralFormat
    case gradeThisPerformance
    case showMoreStats
    case showLessStats
    case gradeSubmitted
    case gradeDeleted
    case averageOfGradesSingularFormat
    case averageOfGradesPluralFormat
    case gradePlayers
    case viewPlayerGrades
    case seeAllPlayerGrades
    case playerGradesTitle
    case notApplicableAbbreviation

    // MARK: - Game Stats
    case gameStatsTabTitle
    case gameStatsStartersTitle
    case gameStatsBenchTitle
    case gameStatsGoaliesTitle
    case gameStatsSkatersTitle
    case gameStatsNotAvailable

    // MARK: - Game Plays
    case gamePlayByPlayTabTitle
    case gameTimelineTabTitle
    case playByPlayNotAvailable
    case playByPlayAllPlaysTitle
    case playByPlayScoringPlaysTitle
    case playByPlayAllDrivesTitle
    case playByPlayScoringDrivesTitle
    case playByPlayAllMomentsTitle
    case playByPlayKeyMomentsTitle
    case playByPlayShotsOnGoalAbbreviation
    case playByPlayTotalShotsFormat
    case playByPlayRunFormat
    case playByPlayRunsFormat
    case playByPlayHitFormat
    case playByPlayHitsFormat
    case playByPlaySingularPlayFormat
    case playByPlayPluralPlaysFormat
    case playByPlaySingularYardFormat
    case playByPlayPluralYardsFormat
    case playByPlayPenaltyShootoutRoundFormat
    case playByPlayChancesCreatedFormat
    case soccerNoKeyMoments
    case soccerNoKeyMomentsInPeriod

    // MARK: - Game Live Blog
    case gameLiveBlogTabTitle

    case error
    case flag
    case cancel

    // MARK: - Comments
    case comment
    case comments
    case actions
    case edit
    case areYouSure
    case commentsReviewInfo
    case skip
    case staff
    case topComments

    // MARK: - Article
    case ok
    case whatDidYouThinkOfThisStory
    case purchaseError
    case meh
    case solid
    case awesome
    case wereSorry
    case thanksForYourInputTryFree
    case boomGladYouReallyEnjoyedIt
    case thanksForYourInput
    case justNow
    case yesterday
    case earlierToday

    // MARK: - Feed
    case feedToday
    case reload
    case seeAll
    case personalizeFeed
    case followTeamsLeaguesAuthors
    case outdate
    case outdateConfirmation
    case outdateSuccess
    case topComment

    // MARK: - Topics
    case done
    case next

    // MARK: - Privacy Policy Updates
    case updatedPolicies
    case newPolicies
    case clickHere
    case weveUpdatedOurTermsDefault
    case weveUpdatedOurTermsCanadaAus
    case iAccept
    case privacyChoicesMessage
    case switchToggleOptOut
    case successfullyOptedOut

    // MARK: - Login
    case privacyPolicy
    case loginFailed
    case email
    case password
    case logIn
    case or
    case firstName
    case lastName
    case registrationFailed
    case pleaseEnterAValidEmail
    case initialLoadingFailureMessage

    // MARK: - Settings
    case deleteAccount
    case restorePurchases
    case manageSubscriptions
    case subscribe
    case termsOfService
    case logOut
    case search
    case leagues
    case notifications
    case notificationSettings
    case notificationsAreDisabled
    case goToSettings
    case changesToYourFacebookProfileWillAppearHere
    case deleteMessage
    case normal
    case medium
    case large
    case extraLarge
    case managePrivacyPreferences

    case noNetworkAlert

    // MARK: - Region Settings

    case region
    case northAmerica
    case international
    case regionSettingsFootnote

    // MARK: - Underscore
    case dismiss
    case loginEmail
    case loginWelcome
    case loginTitleEmail
    case loginTitleApple
    case loginTitleGoogle
    case loginTitleFacebook
    case loginTitleNewYorkTimes
    case loginForgotPass
    case loginResetPasswordTitle
    case loginResetPasswordInstruction
    case loginResetPasswordFailedTitle
    case loginResetPasswordAccountNotFound
    case loginResetPasswordEmailSentTitle
    case loginResetPasswordEmailSentInstruction
    case subscriptionMonthly
    case subscriptionAnnual
    case notificationStories
    case notificationResults
    case notificationGameStart
    case noComments
    case markUnread
    case tabTitleListen
    case tabTitleScoresLanding
    case authors
    case articles
    case teams
    case markRead
    case myFeed
    case results
    case mostPopular
    case follow
    case savedStoriesTitle
    case savedStoriesEmpty
    case readStory
    case listen
    case speechRate

    // MARK: - Article by
    case textSizeSettings
    case learnMoreTextSize
    case learnMoreTextSizeAdjust
    case clear
    case clearAllSavedStories
    case speakArticle
    case play
    case pause
    case fastRewind
    case fastForward
    case restorePurchasesCompleted
    case saveStory
    case unsaveStory
    case viewMoreComments
    case podcasts
    case updateNow
    case updateRequired
    case forceUpdateInfo
    case freetrialLongInfo
    case freetrialLongInfoMonthly
    case planTermsShort
    case notifSaveStory
    case readMore
    case share
    case shareUniversalLink
    case notrialLongInfo
    case timeLeft
    case annualPrice
    case following
    case commentFlagReason1
    case commentFlagReason2
    case commentFlagReason3
    case createAnAccount
    case createAccount
    case updateLocation
    case onboardingLeaguesSubtitle
    case followLeagues
    case followTeams
    case followingSectionPlaceholder
    case registerInfoTitle1
    case alreadyASubscriber

    // MARK: - Notifications

    case pleaseAllowNotifications
    case allow

    // MARK: - Podcasts
    case downloadedPodcasts
    case downloadedPodcasts2
    case nationalPodcasts
    case titleEmptyPodcast
    case infoEmptyPodcast
    case actionEmptyPodcast
    case planTermsShortDiscounted
    case played
    case podcastUnknownAuthor
    case downloadedPodcastsEmpty
    case podcastPlaybackDisabled
    case podcastDisabledForLiveRoomHosts
    case signupTermsPrivacy
    case plansButtonContinue
    case freeTrialTitle
    case save25
    case chooseYourPlan
    case onboardingPodcastsSubtitle
    case followPodcasts
    case failureCheckSubscriptionOffer
    case subscriptionOfferNotEligible
    case retry
    case home
    case offerIneligibleTitle
    case offerIneligibleSubtitle
    case soccerNoLineupTitle
    case soccerNoLineupSubtitle
    case soccerNoFormationTitle
    case soccerNoFormationSubtitle
    case soccerLineupStarting
    case soccerLineupSubstitutes
    case soccerLineupManager
    case soccerLineupImageLoadingFailed
    case soccerCaptainAbbreviation
    case soccerOwnGoalShortcut
    case close
    case soccerPenaltyAbbreviation
    case onboardingTmobileTitle
    case onboardingTmobileSubtitle
    case oiInfoTextAnnual
    case oiInfoTextMonthly
    case oiStartTrial
    case oiPaymentInfoText
    case oiPaymentInfoTextMonthly
    case oiPaymentInfoTextTrial
    case oiPaymentInfoTextMonthlyTrial
    case oiHeadlineTextTrialRebrand
    case oiSubheadline1
    case oiSubheadline2
    case startScreenTitle
    case podcastSleepTimerTitle
    case podcastSleepTimerOff
    case podcastSleepTimerMinutes
    case podcastSleepTimerHour
    case podcastSleepTimerEpisode
    case podcastQueueNowPlaying
    case podcastQueueUpNext
    case podcastQueueAdd
    case podcastMarkAsPlayed
    case podcastMarkAsUnplayed
    case podcastEpisodeDownload
    case podcastQueueTitle
    case podcastsDeleteManually
    case podcastsDeleteAfterCompletion
    case podcastDownloadOff
    case podcastDownloadWifi
    case podcastDownloadWifiCellular
    case yourShows
    case recommendedShows
    case followPodcast
    case unfollowPodcast
    case podcastEpisodes
    case podcastNotificationsEmpty
    case seriesDetails

    // MARK: - Gift flow
    case thanksTitle
    case chooseGift
    case giftRecipientTitle
    case confirmInfoTitle
    case confirmNamePlaceholder
    case confirmEmailPlaceholder
    case deliveryMethodTitle
    case deliveryPrintTitle
    case deliveryPrintDetail
    case deliveryEmailTitle
    case deliveryEmailDetail
    case recipientNamePlaceholder
    case recipientMsgPlaceholder
    case recipientEmailPlaceholder
    case recipientEmailAction
    case recipientDateTitle
    case giftActionPay
    case giftPlanHeaderTitle
    case giftRecipientHeaderTitle
    case giftSenderHeaderTitle
    case giftThankTitle
    case giftThankRecipientTitle
    case giftThankPlanTitle
    case giftThankDeliveryTitle
    case giftThankAnother
    case giftFooterTerms

    case removeDownload

    case details

    case pushTitle
    case pushSubtitle
    case pushCancel

    case welcomeTrialTitle

    case validatorErrorEmpty
    case validatorErrorInvalid
    case validatorErrorMin

    case downloadedWithColon

    case visitWebSettings
    case manageSubscriptionWeb

    case profileEmptyAdd
    case profileSaveStory
    case profilePreference
    case profileGiveGift
    case profileRateApp
    case profileFaq
    case profileSupport
    case profileLogout2
    case profileInfoText1
    case profileCommentReplies
    case profileTopSportsNewsTitle
    case profileTopSportsNewsDescription
    case profileMyPodcasts
    case profileEmailPreferences
    case profileAccountSettings
    case profileTitle
    case profileAppSettings
    case profileAppIconStyle
    case profileDeleteAccountTitle
    case profilePrivacyChoicesTitle
    case profileDisplay
    case profileHideScoresTicker
    case profileHideFilterTitles
    case profileFeed
    case profileTheme
    case profileVoiceOverArticles
    case profileKeepAppAwake
    case profileHideArticleNavbarOnScroll
    case profileArticleSection
    case profileMiscSection
    case profileClearCache

    case accountFbLinked
    case accountLabelEmail
    case accountUserId

    case noCurrentGame

    case referralsTitle
    case referralsSubtitle
    case referralsSubtitleRedeemed
    case referralsShare
    case referralsGuestPass
    case referralsPassesRedeemed
    case of
    case referralsCta
    case referralsRequestMore
    case referralsRequestSent
    case referralsEmailSubject

    case searchTeams
    case searchLeagues
    case discover
    case podcastsBrowseCategories
    case podcastsLocal

    // MARK: - Updated Article Paywall
    case billedAnnually
    case billedAnnuallyTrial
    case billedMonthly
    case monthlyPeriodShort
    case paywallButtonTitle
    case paywallHeroTitle
    case localExpertise
    case yearlyPeriod

    // MARK: - Interstitial tests
    case getUnrivaledCoverage

    // MARK: - Required app login
    case createAccountReason
    case dontHaveAccountSignup
    case alreadyHaveAccount

    // MARK: - Already Subscriber
    case alreadySubscriber

    // MARK: - Profile Admin
    case profileAdmin

    // MARK: - Profile Feature Flags
    case featureFlagsTitle
    case featureFlagsStaleOverrideTitle
    case featureFlagsListEnabledLabel
    case featureFlagsListDisabledLabel
    case featureFlagsClearOverridingButton
    case featureFlagsOverridingLabel
    case featureFlagsDefaultValueLabel
    case featureFlagsRemoteValueLabel
    case featureFlagsValueCopiedIndicator
    case featureFlagsDeleteElementButton
    case featureFlagsAddElementButton
    case featureFlagsOverriddenTitle
    case featureFlagsClearAllOverridingButton
    case featureFlagsOverriddenCountMessage
    case featureFlagsOverriddenCountMessageSingular

    // MARK: - Live Room
    case liveRoom
    case leave
    case roomYourself
    case roomRoleSubscriber
    case roomRequestPending
    case roomRequestGranted
    case roomRequestError
    case roomRequestCanceled
    case roomRemovedStage
    case roomMutedByHost
    case roomUserRemoved
    case roomUserJoined
    case roomRequestsOnStage
    case roomRequestsHost
    case endRoom
    case endRoomConfirmation
    case leaveRoomQuestion
    case endRoomUserTitle
    case endRoomUserMessage
    case microphoneDisabled
    case microphoneMessage
    case roomRecordingWarning
    case roomRecordingConfirmation
    case roomNotStartedTitle
    case roomNotStartedMessage
    case roomOther
    case roomOthers
    case roomCreate
    case roomTitlePlaceholder
    case roomDescriptionPlaceholder
    case roomTagsPlaceholder
    case roomHostsPlaceholder
    case roomRecordTitle
    case roomAddSelfAsHostTitle
    case roomAddHosts
    case roomTypesPlaceholder
    case roomSelectTags
    case roomSelectHosts
    case roomPreview
    case roomCreateRoom
    case roomGoLive
    case roomFullTitle
    case roomFullMessage
    case roomScheduled
    case roomLinkCopied
    case roomFallbackTitle
    case roomChatStart
    case roomChatStartDisabled
    case roomChatFansHear
    case roomChatFansDisabled
    case roomLiveRec
    case roomRemoveComment
    case roomLockUser
    case roomFlagComment
    case roomRemoveConfirm
    case roomRemoveFromStage
    case roomUserLocked
    case roomDisableChatTitle
    case roomAutoSendPushTitle
    case roomPushBannerTitle
    case roomMuteUser
    case roomCreateRoomTitleWarning
    case roomListener
    case roomListeners
    case roomNewMessagesButton
    case askToSpeak
    case cancelAsk
    case leaveStage
    case micOn
    case micOff
    case stageQueue
    case queue
    case hosts
    case tags
    case roomEnableSpeakerForHost
    case roomDisableSpeakerForHost

    // MARK: - Live Blogs
    case minFormat

    // MARK: - End of Feed
    case endOfFeed
    case backToTop

    // MARK: - Listen Tab
    case latestEpisodes

    // MARK: - Ads
    case adSlug

    // MARK: - Delete Account
    case deleteAccountConfirmationMessage
    case deleteAccountSuccessTitle
    case deleteAccountSuccessMessage

    // MARK: - Notification Center Feature Tour
    case notificationCenterFeatureTourViewAllTitle
    case notificationCenterFeatureTourViewAllSubtitle

    case notificationCenterFeatureTourAccountTabTitle
    case notificationCenterFeatureTourAccountTabSubtitle

    case notificationCenterFeatureTourEverythingYouNeedTitle
    case notificationCenterFeatureTourEverythingYouNeedSubtitle
    case notificationCenterFeatureTourEverythingYouNeedAction

    case notificationCenterFeatureTourActivityTitle
    case notificationCenterFeatureTourActivitySubtitle

    case notificationCenterFeatureTourUpdatesTitle
    case notificationCenterFeatureTourUpdatesSubtitle

    case notificationCenterFeatureTourManageTitle
    case notificationCenterFeatureTourManageSubtitle
    case notificationCenterFeatureTourManageAction

    case notificationCenterFeatureTourIntroTitle
    case notificationCenterFeatureTourIntroSubtitle
    case notificationCenterFeatureTourIntroAction

    case notificationCenterFeatureTourOnePlaceTitle
    case notificationCenterFeatureTourOnePlaceSubtitle
    case notificationCenterFeatureTourOnePlaceAction

    // MARK: - Sports News Notification Setting Feature Tour
    case sportsNewsNotificationSettingFeatureTourTitle
    case sportsNewsNotificationSettingFeatureTourDescription
    case sportsNewsNotificationSettingFeatureTourAction
    case sportsNewsNotificationSettingToastMessageSuccess1
    case sportsNewsNotificationSettingToastMessageSuccess2
    case sportsNewsNotificationSettingToastMessageSuccess3
    case sportsNewsNotificationSettingToastMessageFailure1
    case sportsNewsNotificationSettingToastMessageFailure2
    case sportsNewsNotificationSettingToastMessageFailure3
}
