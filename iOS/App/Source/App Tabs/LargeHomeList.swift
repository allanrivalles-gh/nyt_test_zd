//
//  LargeHomeList.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 11/5/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticNavigation
import Foundation
import SwiftUI

/// Home list used for large screen size (iPad).
struct LargeHomeList: View {
    @EnvironmentObject private var user: UserModel
    @EnvironmentObject private var compass: Compass
    @EnvironmentObject private var navigationModel: NavigationModel
    @State private var navigatedEntity: FollowingEntity?

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
        FeedV2List(
            viewModel: FeedV2ViewModel(
                id: "followingFeed",
                requestConfiguration: requestConfiguration,
                navigationModel: navigationModel,
                analyticsSourceView: .home
            )
        )
        .navigationTitle(Strings.myFeed.localized)
        .navigationBarTitleDisplayMode(.inline)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
