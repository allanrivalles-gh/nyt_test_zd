//
//  Provider.swift
//  HeadlinesWidgetExtension
//
//  Created by Kyle Browning on 8/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import AthleticApolloNetworking
import AthleticApolloTypes
import AthleticFoundation
import WidgetKit

struct Provider: IntentTimelineProvider {

    private struct Constants {
        static let minutesAfterSuccessfulFetch = 5
        static let minutesAfterFailedFetch = 1
        static let minutesAfterStaleFetch = 15
        static let hoursToStaleThreshold = 24
    }

    func placeholder(in context: Context) -> HeadlineTimelineEntry {
        HeadlineTimelineEntry(
            headlines: sampleHeadlines,
            date: Date(),
            configuration: ConfigurationIntent(),
            isPlaceholder: true
        )
    }

    func getSnapshot(
        for configuration: ConfigurationIntent,
        in context: Context,
        completion: @escaping (HeadlineTimelineEntry) -> Void
    ) {
        let entry = HeadlineTimelineEntry(
            headlines: sampleHeadlines,
            date: Date(),
            configuration: configuration,
            isPlaceholder: true
        )
        completion(entry)
    }

    func getTimeline(
        for configuration: ConfigurationIntent,
        in context: Context,
        completion: @escaping (Timeline<HeadlineTimelineEntry>) -> Void
    ) {
        fetchData { headlines, isDataCached, isDataStale in
            let entry = HeadlineTimelineEntry(
                headlines: headlines,
                date: Date(),
                configuration: configuration,
                isDataStale: isDataStale
            )
            let nextUpdate = nextUpdate(
                isFailedFetch: headlines.isEmpty || isDataCached,
                isDataStale: isDataStale
            )
            let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
            completion(timeline)
        }
    }

    private func fetchData(
        completion: @escaping ([Headline], Bool, Bool) -> Void
    ) {
        let query = GQL.WidgetHeadlinesQuery(filter: .init(region: Locale.current.region))
        let network = AthleticApolloNetwork(environment: .production)

        let _ = network.client.fetch(
            query: query,
            cachePolicy: .fetchIgnoringCacheData,
            contextIdentifier: UUID(),
            queue: .main
        ) { result in
            switch result {
            case .success(let data):
                let isDataCached = false
                let isDataStale = false
                guard let data = data.data else {
                    completion([], isDataCached, isDataStale)
                    return
                }
                updateLastSuccessfulFetchDate()
                let headlines = processedHeadlines(data)
                completion(headlines, isDataCached, isDataStale)
            case .failure(_):
                fetchDataFromCache(query: query, network: network) { cachedHeadlines, isDataStale in
                    let isDataCached = true
                    completion(cachedHeadlines, isDataCached, isDataStale)
                }
            }
        }
    }

    private func processedHeadlines(_ data: GQL.WidgetHeadlinesQuery.Data) -> [Headline] {
        let news = data.newsV2.items
        let headlines: [Headline] = news.compactMap { $0?.fragments.newsItemLite }
            .map { news in
                .init(
                    id: news.id,
                    title: news.headline,
                    tag:
                        news.primaryTag?.fragments.tagDetailWrapper
                        .tagDetail?.shortname ?? "",
                    date: news.createdAt,
                    imageURI: news.images.first?.fragments.newsImage.imageUri
                )
            }
        return headlines
    }

    private func fetchDataFromCache(
        query: GQL.WidgetHeadlinesQuery,
        network: AthleticApolloNetwork,
        completion: @escaping ([Headline], Bool) -> Void
    ) {
        let _ = network.client.fetch(
            query: query,
            cachePolicy: .returnCacheDataDontFetch,
            contextIdentifier: UUID(),
            queue: .main
        ) { cacheResult in
            let isDataStale = isCachedDataStale()
            switch cacheResult {
            case .success(let cachedData):
                guard let cachedData = cachedData.data else {
                    completion([], isDataStale)
                    return
                }
                let cachedHeadlines = processedHeadlines(cachedData)
                completion(cachedHeadlines, isDataStale)
            case .failure:
                completion([], isDataStale)
            }
        }
    }

    private func isCachedDataStale() -> Bool {
        guard let lastFetch = UserDefaults.lastHeadlinesWidgetFetchDate else {
            return true
        }

        let staleDate = Calendar.current.date(
            byAdding: .hour,
            value: Constants.hoursToStaleThreshold,
            to: lastFetch
        )!

        return staleDate < Date()
    }

    private func updateLastSuccessfulFetchDate() {
        UserDefaults.appGroup.set(
            Date(),
            forKey: "lastHeadlinesWidgetFetchDate"
        )
    }

    private func nextUpdate(isFailedFetch: Bool, isDataStale: Bool) -> Date {
        let minutesToNextUpdate =
            if isFailedFetch {
                Constants.minutesAfterFailedFetch
            } else if isDataStale {
                Constants.minutesAfterStaleFetch
            } else {
                Constants.minutesAfterSuccessfulFetch
            }

        let nextUpdate = Calendar.current.date(
            byAdding: .minute,
            value: minutesToNextUpdate,
            to: Date()
        )!

        return nextUpdate
    }
}

extension Locale {
    var region: GQL.Region? {
        if isCanada {
            return .ca
        } else if isPartOfEU {
            return .uk
        } else {
            return .us
        }
    }
}

extension UserDefaults {
    static var appGroup: UserDefaults {
        UserDefaults(suiteName: Global.Widget.appGroupIdentifier)!
    }

    static var lastHeadlinesWidgetFetchDate: Date? {
        appGroup.object(forKey: "lastHeadlinesWidgetFetchDate") as? Date
    }
}
