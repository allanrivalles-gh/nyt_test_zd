//
//  Constants.swift
//  theathletic-ios
//
//  Created by Jan Remes on 06.01.15.
//  Copyright (c) 2015 Jan Remes. All rights reserved.
//

import Foundation
import UIKit

public struct Global {

    public static let teamLogosUrl = "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/"

    public struct General {
        public static let policiesHostString = "privacy.theathletic.com"
        public static let policiesUrl = URL(string: "https://\(policiesHostString)")!

        public static let supportUrl = URL(string: "https://theathletic.zendesk.com/hc/en-us")!
        public static let codeOfConductUrl = URL(
            string: "https://theathletic.com/code-of-conduct/"
        )!
        public static let iTunesAppUrl =
            "https://itunes.apple.com/us/app/the-athletic/id1135216317?ls=1&mt=8"
        public static let iTunesReviewAppUrl =
            "itms-apps://itunes.apple.com/app/id1135216317?action=write-review"
        public static let athleticDomainString = "theathletic.com"
        public static let supportEmailUrl = URL(
            string: "https://theathletic.zendesk.com/hc/en-us/requests/new"
        )!
        public static let athleticWebSettingsUrl = URL(string: "https://theathletic.com/settings")!
        public static let deeplinkUrl = "theathletic://"
        public static let twitterUrl = "https://twitter.com/"
        public static let twitterWidgetsJsUrl = URL(
            string: "https://platform.twitter.com/widgets.js"
        )!

        public static let productionDomainURL = "https://theathletic.com/"
        public static let stagingDomainURL = "https://staging2.theathletic.com/"
    }

    public struct TCFPrivacy {
        public static let productionUrlString =
            "https://cdn.transcend.io/cm/ee571c7f-030a-41b2-affa-70df8a47b57b/airgap.js"
        public static let stagingUrlString =
            "https://cdn.transcend.io/cm-test/ee571c7f-030a-41b2-affa-70df8a47b57b/airgap.js"
    }

    public struct Widget {
        public static let headlinesWidget = "Headlines"
        public static let appGroupIdentifier = "group.theathletic.main"

        @available(iOS 16.2, *)
        public struct LiveActivity {
            public static let rootDirectory = FileManager.default
                .containerURL(forSecurityApplicationGroupIdentifier: appGroupIdentifier)!
                .appending(component: "liveActivities", directoryHint: .isDirectory)
        }
    }

    public static let articleCacheTTL: TimeInterval = 5.minutes
    public static let feedCacheTTL: Int = 5 /* minutes */

    public static let hashSecret: String = "1CnGYuU4wwaseYPbWuEHM9fCTscaf7wCQ1"
    public static let hashUserSalt: String = "user"
    public static let hashMininumLength: UInt = 12

    public static let accessTokenSourceKey: String = "access_keychain_source"

    public static let playerRegexMatchString =
        #"<a class='ath_autolink' [^>]+?\/player\/[^>]+?>(.*?)<\/a>"#

    public struct UserDefaults {
        public static let presentedFBLink: String = "presentedFBLink"
        public static let awesomeReviewCount: String = "awesomeReviewCount"
        public static let selectedSpeechControllerRate = "selectedSpeechControllerRate"
        public static let existsLogSub = "existsLogSub"

        public static let purchaseSource = "purchaseSource"

        public static let kochavaDefferedDeeplinkUsed = "kochavaDefferedDeeplinkUsed"
        public static let paywallPodcastEpisodeId = "paywallPodcastEpisodeId"
        public static let isKochavaIndentityLinkSent = "isKochavaIndentityLinkSent"
    }

    public static let iPadPortraitMargin: CGFloat = 80
    public static let iPadLandscapeMargin: CGFloat = 120
}

public enum SportType: String, Codable {
    case soccer
    case americanFootball = "football"
    case basketball
    case baseball
    case hockey
    case golf
    case mma
    case boxing
    case formula1
    case nascar
    case tennis
    case mixed
    case unknown
}

public enum LeagueId: String {

    // MARK: - Non-Soccer

    case NHL = "1"
    case NFL = "2"
    case NBA = "3"
    case MLB = "4"

    /// College American Football
    case NCAAFB = "9"

    /// College Men's Basketball
    case NCAAMB = "10"

    /// College Women's Basketball
    case NCAAWB = "36"

    /// French term for NHL
    case LNH = "12"

    /// Women's NBA
    case WNBA = "13"

    case FANTASY_FOOTBALL = "21"
    case FANTASY_BASEBALL = "27"
    case MMA = "29"
    case BOXING = "34"

    // MARK: - Soccer

    /// US
    case MLS = "5"
    case USL = "19"
    case NPSL = "18"
    case NWSL = "20"

    /// UK
    case PREMIER_LEAGUE = "6"
    case SCOTTISH_PREMIERSHIP = "33"
    case UK_WOMAN_SOCCER = "35"
    case EFL = "32"
    case LEAGUE_ONE = "47"
    case LEAGUE_TWO = "48"
    case FA_CUP = "46"
    case EFL_CUP = "49"

    /// European
    case CHAMPIONS_LEAGUE = "7"
    case EUROPA = "37"
    case WOMENS_EUROS = "45"

    /// International
    case MENS_WORLD_CUP = "44"
    case WOMENS_WORLD_CUP = "55"
    case INTERNATION_SOCCER = "16"
    case FRIENDLIES = "41"

    /// Mexico
    case LIGA_MX = "8"

    /// Spanish
    case LA_LIGA = "17"
    case COPA_DEL_REY = "52"

    /// German
    case BUNDES_LIGA = "22"

    /// Italy
    case SERIE_A = "23"

    /// French
    case LIGUE_1 = "24"

    /// Other
    case SOCCER = "15"
    case unknown = "unknown"

    public static let collegeCases: [Self] = {
        [
            .NCAAFB,
            .NCAAMB,
            .NCAAWB,
        ]
    }()

    // for live box score
    public var gamePartsCount: Int {
        switch self {
        case .MLB:
            return 9
        case .NFL, .NCAAFB:
            return 4
        case .NHL:
            return 3
        case .NBA, .WNBA, .NCAAWB:
            return 4
        case .NCAAMB:
            return 2
        default:
            assertionFailure(
                "Attempted to use `gamePartsCount` for an unsupported league id: \(self)"
            )
            return 0
        }
    }

    // live box score screen layout
    public var sport: SportType {
        switch self {
        case .SOCCER,

            /// UK
            .PREMIER_LEAGUE,
            .EFL,
            .LEAGUE_ONE,
            .LEAGUE_TWO,
            .FA_CUP,
            .EFL_CUP,
            .SCOTTISH_PREMIERSHIP,
            .UK_WOMAN_SOCCER,

            /// Euro / International
            .MENS_WORLD_CUP,
            .WOMENS_WORLD_CUP,
            .INTERNATION_SOCCER,
            .CHAMPIONS_LEAGUE,
            .EUROPA,
            .FRIENDLIES,
            .WOMENS_EUROS,

            /// US
            .MLS,
            .USL,
            .NPSL,
            .NWSL,

            /// Other nations
            .BUNDES_LIGA,
            .SERIE_A,
            .LIGUE_1,
            .LIGA_MX,
            .LA_LIGA,
            .COPA_DEL_REY:
            return .soccer

        case .MLB:
            return .baseball

        case .NFL, .NCAAFB:
            return .americanFootball

        case .NBA, .NCAAMB, .NCAAWB, .WNBA:
            return .basketball

        case .NHL, .LNH:
            return .hockey

        case .unknown, .FANTASY_FOOTBALL, .FANTASY_BASEBALL, .MMA, .BOXING:
            return .unknown
        }
    }

    public var shortSportIdentifier: String {
        switch self {
        case .NFL, .NCAAFB:
            return "FB"
        case .NBA, .NCAAMB, .WNBA:
            return "BB"
        default:
            return ""
        }
    }
}

public enum StorageLocation: String {
    case overrideExperiments
}
