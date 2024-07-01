//
//  AthleticScreen.swift
//
//
//  Created by Mark Corbyn on 5/6/2023.
//

import AthleticFoundation
import Foundation

public enum AthleticScreen: Codable, Hashable {
    public enum Account: Codable, Hashable {
        public enum AdminSettings: Codable, Hashable {
            public enum Diagnostics: Codable, Hashable {
                case settings
                case filesList(URL?)
                case fileDetail(URL)
            }
            case home
            case advertising
            case app
            case diagnostics(Diagnostics)
            case environment
            case compass
            case featureFlags
            case statsAndTheGame
        }

        public enum AppSettings: Codable, Hashable {
            case list
            case appIcon
        }

        case accountSettings
        case adminSettings(AdminSettings)
        case appSettings(AppSettings)
        case createLiveRoom
        case emailSettings
        case gift
        case liveRoomsList
        case login
        case manageFollowing
        case notificationSettings(NotificationsDeepLinkData?)
        case notificationsDetail(FollowingEntity)
        case privacyChoices
        case referAFriend
        case regionSettings
        case register
        case savedStories
    }

    public enum Feed: Codable, Hashable {
        public enum Article: Codable, Hashable {
            case detail(id: String, commentId: String?)
            case settings
            case comments(id: String, title: String, isHeadline: Bool, focusedCommentId: String?)
            case unknown(id: String, commentId: String?)
        }

        case article(Article)
        case discussion(DiscussionDestination)
        case headlineWidget(String, vIndex: Int)
        case landing(ArticleFilter)
        case search
        case topic(slug: String)
        case topicUnwrapped(id: String, title: String)
    }

    public enum Listen: Codable, Hashable {
        case discover
        case following
        case leaguePodcasts(channel: PodcastChannel)
        case podcastDownloads
        case podcast(seriesId: String, episodeNumber: Int?, commentId: String?)
        case podcastEpisode(PodcastEpisode, focusedCommentId: String?)
        case podcastEpisodeComments(episodeId: String, title: String, focusedCommentId: String?)
        case podcastPlayerQueue
        case podcastSeries(podcastId: String, fromElement: PodcastSourceElement?)
    }

    public enum LiveRoom: Codable, Hashable {
        case room(id: String)
        case tagFeed(entity: FollowingEntity, title: String)
    }

    public enum NotificationCenter: Codable, Hashable {
        case activity
        case updates
    }

    public enum Onboarding: Codable, Hashable {
        case emailSent
        case forgotPassword
        case freeTrial
        case leagues
        case login
        case loginSignup(LoginSignupType)
        case name
        case plans(PlansDeepLinkData)
        case podcast
        case register
        case teams
    }

    public enum Scores: Codable, Hashable {
        public enum Player: Codable, Hashable {
            case id(playerId: String, leagueString: String?)
            case slug(slug: String, leagueString: String)
        }
        case boxScore(BoxScoreDestination)
        case gameInjuries(GameInjuries)
        case home
        case playerHub(player: Player, colors: HubColors? = nil)
    }

    case account(Account)
    case discover
    case externalUrl(URL)
    case feed(Feed)
    case headlineWidgetHeader
    case hubDetails(entity: FollowingEntity, preferredTab: HubTabType?)
    case listen(Listen)
    case liveBlog(liveBlogId: String, postId: String?)
    case liveRoom(LiveRoom)
    case notificationCenter(NotificationCenter)
    case onboarding(Onboarding)
    case scores(Scores)
    case teamThreadPost(TeamThreadPostDestination)
    case unknown
    case webview(type: WebViewType)

    public var mainTabPreselection: MainTab? {
        switch self {
        case .listen(let target):
            switch target {
            case .discover, .following, .podcast:
                return .listen
            default:
                return nil
            }
        case .liveRoom(let target):
            switch target {
            case .room:
                return .listen
            default:
                return nil
            }
        case .scores:
            return .scores
        case .account:
            return .account
        case .discover:
            return .discover
        default:
            return nil
        }
    }
}
