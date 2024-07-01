//
//  FeatureFlags.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 1/27/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation

final class FeatureFlags {
    enum Group: Comparable {
        case app
        case advertisements
        case article
        case articleCutOff
        case bracket
        case discussion
        case featureTours
        case feed
        case liveRoom
        case notifications
        case onboarding
        case paywall
        case podcasts
        case scores
        case shortform

        var title: String {
            switch self {
            case .app:
                return "App"
            case .advertisements:
                return "Ads"
            case .article:
                return "Article"
            case .articleCutOff:
                return "Article Cut Off Debugging"
            case .bracket:
                return "Bracket"
            case .discussion:
                return "Discussion"
            case .featureTours:
                return "Feature Tours"
            case .feed:
                return "Feed"
            case .liveRoom:
                return "Live Room"
            case .notifications:
                return "Notifications"
            case .onboarding:
                return "Onboarding"
            case .paywall:
                return "Paywall"
            case .podcasts:
                return "Podcasts"
            case .scores:
                return "Scores"
            case .shortform:
                return "Shortform"
            }
        }

        var iconName: String {
            switch self {
            case .app:
                return "app"
            case .advertisements:
                return "megaphone"
            case .article:
                return "newspaper"
            case .articleCutOff:
                return "doc.text.below.ecg"
            case .bracket:
                return "curlybraces"
            case .discussion:
                return "person.wave.2"
            case .featureTours:
                return "map"
            case .feed:
                return "list.dash"
            case .liveRoom:
                return "mic"
            case .notifications:
                return "tray.full"
            case .onboarding:
                return "figure.and.child.holdinghands"
            case .podcasts:
                return "headphones"
            case .paywall:
                return "dollarsign"
            case .scores:
                return "number"
            case .shortform:
                return "doc.plaintext"
            }
        }
    }

    // MARK: App

    @FeatureFlag("In Review", group: .app)
    var isInReview = false

    @FeatureFlag("Force Update Versions", group: .app, transform: commaSeparatedStringArray)
    var forceUpdateVersions = [String]()

    @FeatureFlag("Logging Trace Users", group: .app, transform: commaSeparatedStringArray)
    var loggingTraceUserIds = [String]()

    @FeatureFlag("Compass Fetch TTL (seconds)", group: .app)
    var compassFetchTTL = 1.hour.toInt()

    @FeatureFlag("Learn More About Text Size Url", group: .app)
    var learnMoreSettingsTextSizeUrl = "https://support.apple.com/en-us/HT202828"

    @FeatureFlag("Background fetch interval (hours)", group: .app)
    var backgroundFetchIntervalInHours = 2

    @FeatureFlag("Background fetch Enabled", group: .app)
    var isBackgroundFetchEnabled = true

    @FeatureFlag("Enable DNS Privacy Choices", group: .app)
    var isDnsPrivacyChoicesEnabled = false

    @FeatureFlag("Enable TCF Privacy Preferences", group: .app)
    var isTcfPrivacyPreferencesEnabled = false

    @FeatureFlag("Use Production TCF URL", group: .app)
    var useProductionTcfUrl = false

    @FeatureFlag("Enable Mac App Banner Message", group: .app)
    var isMacAppBannerMessageEnabled = true

    @FeatureFlag("Mac App Not Supported Title", group: .app)
    var macAppNotSupportedTitle = "This Mac app is no longer supported."

    @FeatureFlag("Mac App Not Supported Message", group: .app)
    var macAppNotSupportedMessage =
        "This app will no longer receive important updates which means you will miss out on any new features. Please use The Athletic website, iOS or Android app for the best experience."

    // MARK: - Navigation Path Setter Crash Debugging

    @FeatureFlag("Crash: Disable focusResignation", group: .app)
    var isCrashFocusResignationDisabled = false

    @FeatureFlag("Crash: Disable SearchRow Simultaneous Tap", group: .app)
    var isCrashSearchRowSimultaneousTapDisabled = false

    @FeatureFlag("Crash: Disable Search NavigationLink", group: .app)
    var isCrashSearchRowNavigationLinkDisabled = false

    @FeatureFlag("Crash: DispatchAsync Button Action", group: .app)
    var isCrashDispatchAsyncButtonEnabled = false

    // MARK: Feed

    @FeatureFlag("Feed Query Reload Interval", group: .feed)
    var feedQueryReloadInterval = Global.feedCacheTTL

    @FeatureFlag("Following Feed Curation", group: .feed)
    var followingFeedCuration = false

    // MARK: Article Cut Off Debugging

    @FeatureFlag("Loading Timeout", group: .articleCutOff)
    var articleCutOffTimeoutInterval: TimeInterval = 30

    @FeatureFlag("Enable Blocked JavaScript Detector", group: .articleCutOff)
    var articleCutOffIsBlockedJavaScriptDetectorEnabled: Bool = true

    @FeatureFlag("Blocked JavaScript Evaluation Interval", group: .articleCutOff)
    var articleCutOffBlockedJavaScriptEvaluationInterval: TimeInterval = 3

    @FeatureFlag("Maximum Recovery Attempts", group: .articleCutOff)
    var articleCutOffMaximumRecoveryAttempts: Int = 3

    @FeatureFlag("Disable Ads At Recovery Attempt Number", group: .articleCutOff)
    var articleCutOffDisableAdsAtRecoveryAttemptNumber: Int = 2

    @FeatureFlag("Show Debug Panel Upon Failure", group: .articleCutOff)
    var articleCutOffShowDebugPanelUponFailure: Bool = false

    @FeatureFlag("Show Debug Panel To Staff", group: .articleCutOff)
    var articleCutOffShowDebugPanelToStaff: Bool = false

    @FeatureFlag("Observe Campaign ID's", group: .articleCutOff)
    var articleCutOffObserveCampaignIDs: Bool = true

    // MARK: Bracket

    @FeatureFlag("Hardcoded NBA Season", group: .bracket)
    var hardcodedNBASeason = false

    @FeatureFlag("NBA Bracket", group: .bracket)
    var isNBABracketEnabled = false

    @FeatureFlag("Hardcoded MLB Season", group: .bracket)
    var hardcodedMLBSeason = false

    @FeatureFlag("MLB Bracket", group: .bracket)
    var isMLBBracketEnabled = false

    @FeatureFlag("Hardcoded NHL Season", group: .bracket)
    var hardcodedNHLSeason = false

    @FeatureFlag("NHL Bracket", group: .bracket)
    var isNHLBracketEnabled = false

    @FeatureFlag("March Madness Bracket", group: .bracket)
    var isMarchMadnessBracketEnabled = false

    // MARK: Scores

    @FeatureFlag("Live Activities", group: .scores)
    var isLiveActivitiesEnabled = false

    @FeatureFlag("Comment On Play", group: .scores)
    var isCommentOnPlayEnabled = false

    @FeatureFlag("Top Comments", group: .scores)
    var isTopCommentsEnabled = true

    @FeatureFlag("Player Hubs", group: .scores)
    var isPlayerHubsEnabled = false

    @FeatureFlag("Soccer Player Grades", group: .scores)
    var isSoccerPlayerGradesEnabled = false

    @FeatureFlag("Basketball Player Grades", group: .scores)
    var isBasketballPlayerGradesEnabled = false

    @FeatureFlag("Baseball Player Grades", group: .scores)
    var isBaseballPlayerGradesEnabled = false

    @FeatureFlag("Hockey Player Grades", group: .scores)
    var isHockeyPlayerGradesEnabled = false

    @FeatureFlag("Ticket Purchasing", group: .scores)
    var isTicketPurchasingEnabled = false

    @FeatureFlag("Game Slide Stories", group: .scores)
    var isGameSlideStoriesEnabled = false

    // MARK: Podcasts

    @FeatureFlag("Team Hub Podcasts", group: .podcasts)
    var isTeamHubPodcastsEnabled = false

    // MARK: Onboarding

    @FeatureFlag("Onboarding Interstitial Title", group: .onboarding)
    var onboardingInterstitialTitle = Strings.welcomeTrialTitle.localized

    @FeatureFlag("Onboarding Interstitial Trial Title", group: .onboarding)
    var onboardingInterstitialTitleTrial = Strings.welcomeTrialTitle.localized

    @FeatureFlag("Onboarding Interstitial Subtitle", group: .onboarding)
    var onboardingInterstitialSubTitle = Strings.oiSubheadline2.localized

    @FeatureFlag("Onboarding Interstitial Trial Subtitle", group: .onboarding)
    var onboardingInterstitialSubTitleTrial = Strings.oiSubheadline1.localized

    @FeatureFlag("Onboarding Interstitial Button Title", group: .onboarding)
    var onboardingInterstitialButtonTitle = Strings.subscribe.localized

    @FeatureFlag("Onboarding Interstitial Button Trial Title", group: .onboarding)
    var onboardingInterstitialButtonTitleTrial = Strings.oiStartTrial.localized

    @FeatureFlag("Onboarding Interstitial Sub Button Title", group: .onboarding)
    var onboardingInterstitialSubButtonTitle = Strings.notrialLongInfo.localized

    @FeatureFlag("Onboarding Interstitial Sub Button Trial Title", group: .onboarding)
    var onboardingInterstitialSubButtonTitleTrial = Strings.freetrialLongInfo.localized

    // MARK: Paywall

    @FeatureFlag("Force Paywall", group: .paywall)
    var isForcePaywallEnabled: Bool = false

    @FeatureFlag("Paywall Title", group: .paywall)
    var paywallTitle = Strings.getUnrivaledCoverage.localized

    @FeatureFlag("Paywall Trial Title", group: .paywall)
    var paywallTitleTrial = Strings.oiHeadlineTextTrialRebrand.localized

    @FeatureFlag("Free monthly article limit", group: .paywall)
    var freeMonthlyArticleLimit = 10

    @FeatureFlag("Article subscriber score threshold", group: .paywall)
    var articleSubscriberScoreThreshold = 7.1

    // MARK: Live Chat

    @FeatureFlag("Live Room High Quality Audio", group: .liveRoom)
    var isLiveRoomHighQualityAudioEnabled = true

    @FeatureFlag("Live Chat Message Fetch Limit", group: .liveRoom)
    var liveChatMessageFetchLimit = 100

    // MARK: Advertisements

    @FeatureFlag("Master Ads Switch", group: .advertisements)
    var isMasterAdsSwitchEnabled: Bool = true

    @FeatureFlag("Home Feed Ads", group: .advertisements)
    var isFollowingAdsEnabled: Bool = true

    @FeatureFlag("Article Ads", group: .advertisements)
    var isArticleAdsEnabled: Bool = true

    @FeatureFlag("Author Feed Ads", group: .advertisements)
    var isAuthorTopicsAdsEnabled: Bool = false

    @FeatureFlag("Discover Tab Ads", group: .advertisements)
    var isDiscoverAdsEnabled: Bool = false

    @FeatureFlag("League Feed Ads", group: .advertisements)
    var isLeaguesAdsEnabled: Bool = true

    @FeatureFlag("Team Feed Ads", group: .advertisements)
    var isTeamsAdsEnabled: Bool = true

    @FeatureFlag("News Topic Feed Ads", group: .advertisements)
    var isNewsTopicsAdsEnabled: Bool = true

    @FeatureFlag("Live Blog Ads", group: .advertisements)
    var isLiveBlogAdsEnabled: Bool = true

    @FeatureFlag("RDP States", group: .advertisements, transform: commaSeparatedStringArray)
    var rdpStates: [String] = ["CA", "CO", "VA"]

    @FeatureFlag("Make native ads inspectable", group: .advertisements)
    var makeNativeAdsInspectable: Bool = false

    @FeatureFlag("Enable Geo-Based GDPR", group: .advertisements)
    var isGeoBasedGDPREnabled: Bool = true

    // MARK: Feature Tours

    var isForceUpdatedRequired: Bool {
        return forceUpdateVersions.contains(String.getAppVersionSemver)
    }

    let resolver: FeatureFlagsValueResolver
    var remoteValues: [String: Any] { resolver.remoteValues }

    init(remoteValues: [String: Any] = [:], overridingValues: [String: Any] = [:]) {
        resolver = FeatureFlagsValueResolver(
            remoteValues: remoteValues,
            overridingValues: overridingValues
        )

        /// using the subscript version of the property wrapper does not work for this use case
        /// because when we try to load the properties using mirror on the `FeatureFlagsList`,
        /// it is unable to find the correct value, so we have to manually register the instance
        let properties: [(String, FeatureFlagProperty)] = Mirror(reflecting: self).children
            .compactMap { (label, value) in
                guard var label, let value = value as? FeatureFlagProperty else {
                    return nil
                }
                /// the first character will be an underscore added because it is a property wrapper
                label = String(label.suffix(from: label.index(after: label.startIndex)))
                return (label, value)
            }
        for (label, property) in properties {
            property.register(instance: self, label: label)
        }
    }

    // MARK: Notifications

    @FeatureFlag("Notification Center", group: .notifications)
    var isNotificationCenterEnabled: Bool = false

    @FeatureFlag("Toggle Top Sports News Notifications", group: .notifications)
    var canToggleTopSportsNewsNotifications: Bool = false
}

private func commaSeparatedStringArray(source: String) -> [String] {
    source.components(separatedBy: ",")
}

protocol AnyFeatureFlag {
    var name: String { get }
    var key: String { get }
    var value: Any { get }
    var group: FeatureFlags.Group { get }
}

private protocol FeatureFlagProperty {
    func register(instance: FeatureFlags, label: String)
}

@propertyWrapper final class FeatureFlag<Source, Value>: AnyFeatureFlag, FeatureFlagProperty {
    private var resolved: Value?
    private let defaultValue: Value
    private let transform: (Source) -> Value
    /// gotta be weak to avoid retain cycle
    private(set) weak var instance: FeatureFlags!
    private(set) var label: String!
    let name: String
    var key: String { label }
    var value: Any { wrappedValue }
    let group: FeatureFlags.Group

    fileprivate func register(instance: FeatureFlags, label: String) {
        self.instance = instance
        self.label = label
    }

    convenience init(
        wrappedValue: Value,
        _ name: String,
        group: FeatureFlags.Group
    ) where Source == Value {
        self.init(
            wrappedValue: wrappedValue,
            name,
            group: group,
            transform: { $0 }
        )
    }

    init(
        wrappedValue: Value,
        _ name: String,
        group: FeatureFlags.Group,
        transform: @escaping (Source) -> Value
    ) {
        self.name = name
        self.defaultValue = wrappedValue
        self.group = group
        self.transform = transform
    }

    var wrappedValue: Value {
        guard let resolvedValue = resolved else {
            let newValue =
                instance.resolver.value(
                    forKey: key,
                    transform: transform
                ) ?? defaultValue
            resolved = newValue
            return newValue
        }
        return resolvedValue
    }
}

struct FeatureFlagsValueResolver {
    let remoteValues: [String: Any]
    let overridingValues: [String: Any]

    func value<S, V>(forKey key: String, transform: (S) -> V) -> V? {
        if let value = overridingValues[key] as? V {
            /// when overriding the value it will come with the final type already
            /// there is no need to transform it
            return value
        }
        if let value = remoteValues[key] as? S {
            return transform(value)
        }
        return nil
    }
}
