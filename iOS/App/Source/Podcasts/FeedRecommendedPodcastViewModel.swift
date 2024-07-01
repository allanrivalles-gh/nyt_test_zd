//
//  FeedRecommendedPodcastViewModel.swift
//  theathletic-ios
//
//  Created by Andrew Fannin on 5/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticNavigation
import Foundation

struct FeedRecommendedPodcastViewModel: Identifiable {
    var id: String { viewModel.podcast.id }
    let viewModel: AsyncPodcastShowItemViewModel

    private let analyticsConfiguration: FeedSectionAnalyticsConfiguration
    var impressionManager: AnalyticImpressionManager {
        analyticsConfiguration.impressionManager
    }

    init(
        podcast: PodcastShowItemPodcast,
        network: PodcastsNetworking,
        analytics: FeedSectionAnalyticsConfiguration
    ) {
        self.viewModel = AsyncPodcastShowItemViewModel(
            podcast: podcast,
            network: network
        )
        self.analyticsConfiguration = analytics
    }
}

// MARK: - Analytical
extension FeedRecommendedPodcastViewModel: Analytical {
    var analyticData: AnalyticData {
        AnalyticData(
            config: analyticsConfiguration,
            objectIdentifier: id,
            eventTypes: [.click, .impress],
            indexHOverride: analyticsConfiguration.indexPath.section,
            indexVOverride: analyticsConfiguration.indexPath.item
        )
    }
}
