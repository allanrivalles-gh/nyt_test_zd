//
//  FeedPodcastEpisodeViewModel.swift
//  theathletic-ios
//
//  Created by Andrew Fannin on 5/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import Foundation

struct FeedPodcastEpisodeViewModel {
    let id: String
    let imageUrl: URL?
    let podcastDescription: String
    let permalinkUrl: URL?
    let title: String
    let duration: Double
    let imageRatioLayoutType: ConsumableContentViewModel.ImageRatioLayoutType?
    let impressionManager: AnalyticImpressionManager

    var episodeId: String {
        episodeModel.episodeId
    }

    var podcastId: String {
        episodeModel.podcastId
    }

    var analyticData: AnalyticData {
        let hIndex: Int
        let vIndex: Int

        if isDualAxisAnalyticsItem {
            hIndex = analyticsConfiguration.indexPath.item
            vIndex = analyticsConfiguration.indexPath.section
        } else {
            hIndex = isHorizontalItem ? analyticsConfiguration.indexPath.item : -1
            vIndex = isHorizontalItem ? -1 : analyticsConfiguration.indexPath.item
        }

        return AnalyticData(
            config: analyticsConfiguration,
            objectIdentifier: episodeModel.episodeId,
            eventTypes: [.click, .impress],
            indexHOverride: hIndex,
            indexVOverride: vIndex
        )
    }

    private let listenModel: ListenModel
    let episodeModel: PodcastEpisodeViewModel
    private let analyticsConfiguration: FeedSectionAnalyticsConfiguration
    private let isHorizontalItem: Bool
    private let isDualAxisAnalyticsItem: Bool

    init(
        with podcastEpisodeModel: PodcastEpisodeViewModel,
        overrideTitle: String? = nil,
        overrideDescription: String? = nil,
        ratioLayoutType: ConsumableContentViewModel.ImageRatioLayoutType? = nil,
        isHorizontalItem: Bool = false,
        isDualAxisAnalyticsItem: Bool = false,
        analytics: FeedSectionAnalyticsConfiguration,
        listenModel: ListenModel = AppEnvironment.shared.listen
    ) {
        episodeModel = podcastEpisodeModel

        self.id = podcastEpisodeModel.episodeId
        self.imageUrl = podcastEpisodeModel.imageUrl
        self.podcastDescription = overrideDescription ?? podcastEpisodeModel.episodeDescription
        self.permalinkUrl = podcastEpisodeModel.permalinkUrl
        self.title = overrideTitle ?? podcastEpisodeModel.title
        self.duration = Double(podcastEpisodeModel.duration)
        self.imageRatioLayoutType = ratioLayoutType
        self.impressionManager = analytics.impressionManager
        self.analyticsConfiguration = analytics
        self.isHorizontalItem = isHorizontalItem
        self.isDualAxisAnalyticsItem = isDualAxisAnalyticsItem
        self.listenModel = listenModel

        ImageService.preheatImage(url: imageUrl)
    }
}

extension FeedPodcastEpisodeViewModel: Hashable, Identifiable {

    static func == (lhs: FeedPodcastEpisodeViewModel, rhs: FeedPodcastEpisodeViewModel) -> Bool {
        return lhs.id == rhs.id
            && lhs.imageUrl == rhs.imageUrl
            && lhs.podcastDescription == rhs.podcastDescription
            && lhs.permalinkUrl == rhs.permalinkUrl
            && lhs.title == rhs.title
            && lhs.duration == rhs.duration
    }

    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
        hasher.combine(imageUrl)
        hasher.combine(podcastDescription)
        hasher.combine(permalinkUrl)
        hasher.combine(title)
        hasher.combine(duration)
    }
}
