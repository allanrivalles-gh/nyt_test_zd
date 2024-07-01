//
//  AsyncPodcastShowItemViewModel.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 03/08/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticNavigation
import Foundation

final class AsyncPodcastShowItemViewModel: ObservableObject {
    private static let logger = ATHLogger(category: .podcast)

    let network: PodcastsNetworking
    @Published private(set) var podcast: PodcastShowItemPodcast
    @Published private(set) var isLoadingPodcast: Bool = false

    init(podcast: PodcastShowItemPodcast, network: PodcastsNetworking) {
        self.podcast = podcast
        self.network = network

        ImageService.preheatImage(url: podcast.imageUrl)
    }

    @MainActor
    func loadPodcast() async -> PodcastSeries? {
        if let podcast = podcast as? PodcastSeries {
            return podcast
        }

        var podcastSeries: PodcastSeries?
        isLoadingPodcast = true
        do {
            let data = try await network.fetchPodcastSeries(
                forPodcastId: podcast.id,
                cachePolicy: .returnCacheDataElseFetch
            )
            let newPodcast = data.podcastSeriesById?.fragments.podcastDetail.asPodcastSeries
            if let newPodcast {
                podcast = newPodcast
                podcastSeries = newPodcast
            }
        } catch {
            Self.logger.debug("Error when loading podcast: \(error)", .network)
        }
        isLoadingPodcast = false
        return podcastSeries
    }
}
