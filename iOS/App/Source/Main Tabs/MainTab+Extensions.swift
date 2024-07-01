//
//  MainTab+Extensions.swift
//  theathletic-ios
//
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticNavigation
import SwiftUI
import UIKit

extension MainTab {
    var selectedImage: UIImage? {
        switch self {
        case .account:
            return #imageLiteral(resourceName: "tab_account")
        case .home:
            return #imageLiteral(resourceName: "tab_feed")
        case .scores:
            return #imageLiteral(resourceName: "tab_scores")
        case .discover:
            return #imageLiteral(resourceName: "tab_discover")
        case .listen:
            return #imageLiteral(resourceName: "icn_listen")
        case .entity:
            return nil
        }
    }

    var image: UIImage {
        switch self {
        case .account:
            return #imageLiteral(resourceName: "tab_account")
        case .home:
            return #imageLiteral(resourceName: "tab_feed")
        case .scores:
            return #imageLiteral(resourceName: "tab_scores")
        case .discover:
            return #imageLiteral(resourceName: "tab_discover")
        case .listen:
            return #imageLiteral(resourceName: "icn_listen")
        case .entity:
            return UIImage()
        }
    }

    var imageString: String {
        switch self {
        case .account:
            return "tab_account"
        case .home:
            return "tab_feed"
        case .discover:
            return "tab_discover"
        case .scores:
            return "tab_scores"
        case .listen:
            return "icn_listen"
        case .entity:
            return ""
        }
    }

    var title: String {
        switch self {
        case .account:
            return Strings.profileTitle.localized
        case .home:
            return Strings.home.localized
        case .scores:
            return Strings.tabTitleScoresLanding.localized
        case .discover:
            return Strings.discover.localized
        case .listen:
            return Strings.tabTitleListen.localized
        case .entity:
            return ""
        }
    }

    var analyticsView: AnalyticsEvent.View {
        switch self {
        case .home:
            return .home
        case .scores:
            return .scores
        case .discover:
            return .discover
        case .listen:
            return .listen
        case .account:
            return .profile
        case .entity:
            return .sideBar
        }
    }

    var analyticsObjectType: AnalyticsEvent.ObjectType? {
        switch self {
        case .home:
            return .home
        case .scores:
            return .scores
        case .discover:
            return .discover
        case .listen:
            return .listen
        case .account:
            return .profile
        case .entity:
            return nil
        }
    }

    var accessibilityIdentifier: AppTabNavigationAccessibilityIdentifiers {
        switch self {
        case .account:
            return .accountButton
        case .home:
            return .homeButton
        case .discover:
            return .discoverButton
        case .scores:
            return .scoresButton
        case .listen:
            return .listenButton
        case .entity:
            return .entityButton
        }
    }

    static var allEnabledTabs: [MainTab] {
        [.home, .scores, .discover, .listen, .account]
    }
}

extension DeepLinkType {
    var selectedTab: MainTab {
        switch self {
        case .listenDiscover, .listenFollowing, .liveRoom, .podcastsTab, .podcast:
            return .listen

        case .scores:
            return .scores

        case .accountSettings:
            return .account

        case .frontPageTab:
            return .discover

        default:
            return .home
        }
    }
}
