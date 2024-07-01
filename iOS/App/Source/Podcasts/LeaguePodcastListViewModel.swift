//
//  LeaguePodcastListViewModel.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 2/1/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Apollo
import AthleticApolloTypes
import AthleticNavigation
import Foundation

class LeaguePodcastListViewModel: ObservableObject {
    @Published private(set) var nationalPodcasts: [PodcastSeries]
    @Published private(set) var localPodcasts: [PodcastSeries]

    var leagueId: String {
        channel.id
    }

    var leagueName: String {
        channel.name ?? Strings.podcasts.localized
    }

    var imageUrl: URL? {
        URL(string: channel.mobileImageUrl)
    }

    var channelDto: PodcastChannel {
        PodcastChannel(
            id: channel.id,
            imageUrlString: channel.imageUrl,
            mobileImageUrlString: channel.mobileImageUrl,
            name: channel.name,
            type: channel.type,
            urlString: channel.url
        )
    }

    private let network = AppEnvironment.shared.network
    private(set) var channel: GQL.PodcastFeedQuery.Data.PodcastFeed.PodcastChannel
    private let listenModel: ListenModel

    init(
        channel: ListenModel.PodcastCategory,
        listenModel: ListenModel,
        nationalPodcasts: [GQL.PodcastDetail] = [],
        localPodcasts: [GQL.PodcastDetail] = []
    ) {
        self.channel = channel
        self.listenModel = listenModel
        self.nationalPodcasts = nationalPodcasts.map { $0.asPodcastSeries }
        self.localPodcasts = localPodcasts.map { $0.asPodcastSeries }
    }

    func didAppear() {
        Task {
            try await fetchLeaguePodcasts()
        }
    }

    @MainActor
    private func fetchLeaguePodcasts() async throws {
        if let cacheResponse = try? await fetchPodcastLeagueFeed(
            useCache: true,
            forLeagueId: leagueId
        ) {
            nationalPodcasts = cacheResponse.national.map { $0.asPodcastSeries }
            localPodcasts = cacheResponse.local.map { $0.asPodcastSeries }
        }

        let ignoredCached = try await fetchPodcastLeagueFeed(useCache: false, forLeagueId: leagueId)
        nationalPodcasts = ignoredCached.national.map { $0.asPodcastSeries }
        localPodcasts = ignoredCached.local.map { $0.asPodcastSeries }
    }

    private func fetchPodcastLeagueFeed(useCache: Bool, forLeagueId leagueId: String) async throws
        -> (national: [GQL.PodcastDetail], local: [GQL.PodcastDetail])
    {
        let cachePolicy: CachePolicy =
            useCache ? .returnCacheDataDontFetch : .fetchIgnoringCacheData
        let response = try await network.fetchPodcastLeagueFeed(
            forLeagueId: leagueId,
            cachePolicy: cachePolicy
        )

        let nationalPodcasts =
            response.podcastLeagueFeed?.national?.compactMap { $0?.fragments.podcastDetail } ?? []
        let localPodcasts =
            response.podcastLeagueFeed?.local?.compactMap { $0?.fragments.podcastDetail } ?? []

        return (national: nationalPodcasts, local: localPodcasts)
    }
}
