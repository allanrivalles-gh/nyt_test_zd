//
//  TabContent.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 11/5/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticNavigation
import AthleticNotificationCenter
import AthleticUI
import Foundation
import SwiftUI

struct TabContent: View {
    @EnvironmentObject private var compass: Compass
    @EnvironmentObject private var navigationModel: NavigationModel
    @Environment(\.scenePhase) private var scenePhase

    let tab: MainTab

    var body: some View {
        let isTabBarHidden: Bool = isIpad() ? false : true

        Group {
            switch tab {
            case .account:
                AccountTab(
                    path: navigationModel.accountPath,
                    isTabBarHidden: isTabBarHidden
                )

            case .home:
                HomeTab(
                    path: navigationModel.homePath,
                    isTabBarHidden: isTabBarHidden
                )

            case .scores:
                ScoresTab(
                    path: navigationModel.scoresPath,
                    isTabBarHidden: isTabBarHidden
                )

            case .discover:
                DiscoverTab(
                    path: navigationModel.discoverPath
                )

            case .listen:
                ListenTab(
                    path: navigationModel.listenPath,
                    isTabBarHidden: isTabBarHidden
                )

            /// We can ignore this case it is for ipad only which wont show in this
            case .entity:
                EmptyView()
            }
        }
        .background(
            TransparentNavigationBarStylingView(
                foregroundColor: .chalk.dark800,
                shouldRestoreOnDisappear: false
            )
        )
        .id(tab.id)
    }
}

private struct HomeTab: View {
    @EnvironmentObject private var following: FollowingModel
    @ObservedObject var path: NavigationModel.Path
    @EnvironmentObject private var deepLinkModel: DeeplinkModel
    @EnvironmentObject private var iterateSurvey: IterateSurveyModel
    @State private var areNotificationsAllowed: Bool?

    let isTabBarHidden: Bool

    var body: some View {
        NavigationStack(path: $path.nodes) {
            Group {
                if isIpad() {
                    LargeHomeList()
                } else {
                    CompactHomeList()
                }
            }
            .showFeatureTourIfNeeded(screen: .followingFeed)
            .navigationRestorationTrigger()
            .deepLinkingErrorTrigger()
            .handleNavigationLinks()
            .navigationBarHidden(isTabBarHidden)
            .onAppear {
                /// Check if deeplink is nil
                /// If it is not that means we are just passing over home feed and don't want to show survey
                /// This is okay to be called every time because Iterate controls how many times the survey will show
                /// If we set it to 1 time then the survey will only ever show 1 time
                if deepLinkModel.deeplink == nil {
                    iterateSurvey.triggerSurvery(name: "athletic-home-feed-survey-001")
                }
            }
        }
    }
}

private struct DiscoverTab: View {
    @ObservedObject var path: NavigationModel.Path
    @EnvironmentObject var compass: Compass

    var body: some View {
        NavigationStack(path: $path.nodes) {
            DiscoverV2List()
                .handleNavigationLinks()
        }
    }
}

private struct ScoresTab: View {
    @ObservedObject var path: NavigationModel.Path

    let isTabBarHidden: Bool

    var body: some View {
        NavigationStack(path: $path.nodes) {
            ScoresLanding()
                .handleNavigationLinks()
                .navigationBarHidden(isTabBarHidden)
                .trackRUMView(name: "ScoresLanding")
        }
    }
}

private struct ListenTab: View {
    @ObservedObject var path: NavigationModel.Path

    let isTabBarHidden: Bool

    var body: some View {
        NavigationStack(path: $path.nodes) {
            VStack(spacing: 0) {
                Rectangle()
                    .foregroundColor(Color.chalk.dark200)
                    .edgesIgnoringSafeArea(.top)
                    .frame(height: 0)
                ListenView()
            }
            .navigationBarHidden(isTabBarHidden)
            .handleNavigationLinks()

        }
    }
}

private struct AccountTab: View {
    @ObservedObject var path: NavigationModel.Path

    let isTabBarHidden: Bool

    var body: some View {
        NavigationStack(path: $path.nodes) {
            ProfileView()
                .navigationBarHidden(isTabBarHidden)
                .handleNavigationLinks()
        }
    }
}
