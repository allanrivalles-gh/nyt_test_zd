//
//  CompactHomeList.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 3/22/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticNavigation
import SwiftUI

/// Home list used for small screen size (iPhone).
struct CompactHomeList: View {
    @EnvironmentObject private var user: UserModel
    @EnvironmentObject private var compass: Compass
    @EnvironmentObject private var navigationModel: NavigationModel

    private var requestConfiguration: FeedV2Request.Configuration {
        if compass.config.flags.followingFeedCuration, user.isStaff {
            return .makeLoggedInExperiment(
                user: user
            )
        } else {
            return .makeFollowing(
                user: user
            )
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            StatusBarBackgroundColor()
            TopNavigation()
            FeedV2List(
                viewModel: FeedV2ViewModel(
                    id: "feed",
                    requestConfiguration: requestConfiguration,
                    navigationModel: navigationModel,
                    analyticsSourceView: .home
                )
            )
        }
        .background(Color.chalk.dark200)
    }
}

private struct TopNavigation: View {

    @EnvironmentObject private var following: FollowingModel
    @Environment(\.safeAreaInsets) private var safeAreaInsets

    @State private var isEditPresented: Bool = false

    var body: some View {
        HStack(spacing: 0) {
            if following.followingEntities.isEmpty {
                PersonalizeFeedButton(
                    isEditPresented: $isEditPresented
                )
                .padding(.horizontal, 16)
                .padding(.bottom, 12)
            } else {
                FollowingItemScrollView(
                    context: .feed,
                    isEditPresented: $isEditPresented
                )
                .padding(.bottom, 12)
                /// give more padding to non-notched devices
                .padding(.top, safeAreaInsets.top > 20 ? 8 : 16)
            }
        }
        .background(backgroundColor)
    }

    private var backgroundColor: Color {
        following.followingEntities.isEmpty ? Color.chalk.dark100 : Color.chalk.dark200
    }
}

struct HomeListView_Previews: PreviewProvider {
    static var previews: some View {
        CompactHomeList()
    }
}
