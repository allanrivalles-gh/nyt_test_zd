//
//  PodcastSeriesListViewModel.swift
//  theathletic-ios
//
//  Created by Charles Huang on 1/31/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticNavigation
import AthleticUI
import Foundation
import SwiftUI
import UIKit

final class PodcastSeriesListViewModel: ObservableObject {
    private struct Constants {
        static let defaultHeaderHeight: CGFloat = 395
        static let sidePadding: CGFloat = 20
    }

    @Published var navigatedExternalUrl: URL? = nil
    @Published private(set) var podcast: PodcastSeries? = nil
    @Published private(set) var episodes: [PodcastEpisodeViewModel] = []
    @Published private(set) var loadingState: LoadingState = .initial
    @Published private(set) var isFollowing: Bool = false

    let podcastId: String

    private weak var listenModel: ListenModel?
    private var cancellables = Cancellables()
    private lazy var logger = ATHLogger(category: .podcast)

    private var hasContent: Bool {
        !episodes.isEmpty
    }

    let fromElement: AnalyticsEvent.Element?

    var headerHeight: CGFloat? {
        guard let podcast else { return nil }

        let titleHeight = podcast.title.height(
            with: .font(for: .calibreHeadline.m.semibold),
            constrainedWidth: Helper.screenWidth() - ATHTheme.Dimension.large.padding
        )

        let descriptionHeight = podcast.description.height(
            with: .font(for: .calibreUtility.l.regular),
            constrainedWidth: Helper.screenWidth() - (Constants.sidePadding * 2)
        )

        return max(Constants.defaultHeaderHeight + titleHeight, descriptionHeight)
    }

    lazy var headerStyles: AttributedLabelView.HtmlContentStyles = {
        let style = AthleticFont.Style.tiemposBody.m.regular
        let color = UIColor.chalk.dark800
        let linkColor = UIColor.systemBlue

        return (
            [
                AttributedLabelView.paragraphStyle(
                    font: .font(for: style),
                    paragraphStyleColor: color,
                    lineHeightMultiple: 1.2
                ),
                AttributedLabelView.linkStyle(color: linkColor),
            ],
            AttributedLabelView.linkStyle(color: linkColor),
            AttributedLabelView.allStyle(
                font: .font(for: style),
                color: color,
                textAlignment: .center
            )
        )
    }()

    lazy var descriptionStyles: AttributedLabelView.HtmlContentStyles = {
        let style = AthleticFont.Style.calibreUtility.l.regular
        let color = UIColor.chalk.dark800
        let linkColor = UIColor.systemBlue

        return (
            [
                AttributedLabelView.paragraphStyle(
                    font: .font(for: style),
                    paragraphStyleColor: color,
                    lineHeightMultiple: 1.25
                ),
                AttributedLabelView.linkStyle(color: linkColor),
            ],
            AttributedLabelView.linkStyle(color: linkColor),
            AttributedLabelView.allStyle(
                font: .font(for: style),
                color: color,
                textAlignment: .left
            )
        )
    }()

    // MARK: - Initialization

    init(
        podcastId: String,
        fromElement: AnalyticsEvent.Element? = nil,
        listenModel: ListenModel
    ) {
        self.podcastId = podcastId
        self.listenModel = listenModel
        self.fromElement = fromElement

        isFollowing = listenModel.isFollowing(podcastId: podcastId)
    }

    // MARK: - Lifecycle

    func listDidAppear() {
        guard loadingState == .initial else { return }

        Task {
            await fetchPodcastSeries(isInitialLoad: true)
        }
    }

    func fetchData(isInitialLoad: Bool = false) async {
        await fetchPodcastSeries(useCache: false, isInitialLoad: isInitialLoad)
    }

    // MARK: - Services

    private func fetchPodcastSeries(useCache: Bool = true, isInitialLoad: Bool = false) async {
        guard let listenModel = listenModel else { return }

        if useCache,
            let response = try? await listenModel.network.fetchPodcastSeries(
                forPodcastId: podcastId,
                cachePolicy: .returnCacheDataDontFetch
            )
        {
            await MainActor.run {
                handle(podcastData: response)
            }
        }

        await MainActor.run {
            loadingState = hasContent ? .loaded : .loading(showPlaceholders: isInitialLoad)
        }

        do {
            let response = try await listenModel.network.fetchPodcastSeries(
                forPodcastId: podcastId,
                cachePolicy: .fetchIgnoringCacheData
            )

            await MainActor.run {
                handle(podcastData: response)
                loadingState = .loaded
            }
        } catch let error {
            await MainActor.run {
                logger.debug("Error fetching details for podcast ID \(podcastId): \(error)")
                loadingState = .failed
            }
        }
    }

    private func handle(podcastData: GQL.PodcastSeriesByIdQuery.Data) {

        self.podcast = podcastData.podcastSeriesById?.fragments.podcastDetail.asPodcastSeries

        guard let episodes = podcastData.podcastSeriesById?.episodes else {
            return
        }

        self.episodes =
            episodes
            .compactMap { $0?.fragments.podcastEpisodeDetail }
            .compactMap { PodcastEpisodeViewModel(episode: $0) }
    }

    func followPodcast() {
        isFollowing.toggle()

        if isFollowing {
            Analytics.track(
                event: .init(
                    verb: .add,
                    view: .podcastsPage,
                    element: .follow,
                    objectType: .podcastId,
                    objectIdentifier: podcastId
                )
            )
        } else {
            Analytics.track(
                event: .init(
                    verb: .remove,
                    view: .podcastsPage,
                    element: .unfollow,
                    objectType: .podcastId,
                    objectIdentifier: podcastId
                )
            )
        }

        listenModel?.network.legacyFollowPodcast(id: podcastId, isFollowing: isFollowing)
            .receive(on: RunLoop.main)
            .sink { [listenModel, podcastId, isFollowing] result in
                switch result {
                case .success:
                    listenModel?.followedPodcastsDidUpdate(
                        podcastId: podcastId,
                        isOn: isFollowing
                    )
                case .failure:
                    break
                }
            }
            .store(in: &cancellables)
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        podcastId = try container.decode(String.self, forKey: .podcastId)
        fromElement = try container.decodeIfPresent(
            AnalyticsEvent.Element.self,
            forKey: .fromElement
        )
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(podcastId, forKey: .podcastId)
        try container.encodeIfPresent(fromElement, forKey: .fromElement)
    }
}

extension PodcastSeriesListViewModel: AttributedLabelViewDelegate {
    func handleLinkTap(
        url: URL,
        analyticsIdentifier: AttributedLabelView.AnalyticsIdentifier? = nil
    ) {
        navigatedExternalUrl = url
    }
}

extension PodcastSeriesListViewModel: Hashable, Codable {

    enum CodingKeys: CodingKey {
        case podcastId
        case fromElement
    }

    public func hash(into hasher: inout Hasher) {
        hasher.combine(podcastId)
        hasher.combine(podcast?.title)
    }

    static func == (lhs: PodcastSeriesListViewModel, rhs: PodcastSeriesListViewModel) -> Bool {
        lhs.podcastId == rhs.podcastId
            && lhs.podcast?.title == rhs.podcast?.title
    }
}
