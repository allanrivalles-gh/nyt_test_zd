//
//  PodcastShowItem.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 1/24/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticNavigation
import NukeUI
import SwiftUI

struct AsyncPodcastShowItem: View {
    @EnvironmentObject private var navigationModel: NavigationModel
    @ObservedObject var viewModel: AsyncPodcastShowItemViewModel
    private let titleLineLimit: Int
    let analytics: PodcastShowItem.Analytics

    init(
        viewModel: AsyncPodcastShowItemViewModel,
        analytics: PodcastShowItem.Analytics = .init(),
        titleLineLimit: Int = 1
    ) {
        self.viewModel = viewModel
        self.titleLineLimit = titleLineLimit
        self.analytics = analytics
    }

    var body: some View {
        SharedPodcastShowItem(
            podcast: viewModel.podcast,
            analytics: analytics,
            titleLineLimit: titleLineLimit
        ) {
            Task {
                guard let podcastSeries = await viewModel.loadPodcast() else { return }

                navigationModel.addScreenToSelectedTab(
                    .listen(
                        .podcastSeries(
                            podcastId: podcastSeries.id,
                            fromElement: analytics.fromElement
                        )
                    )
                )
            }
        } image: { url in
            PodcastImage(url: url)
                .overlay {
                    if viewModel.isLoadingPodcast {
                        Color.chalk.dark100
                            .opacity(0.5)
                        ProgressView()
                    }
                }
        }
        .disabled(viewModel.isLoadingPodcast)
    }
}

struct PodcastShowItem: View {
    struct Analytics {
        let sourceView: AnalyticsEvent.View
        let fromElement: PodcastSourceElement

        init(
            sourceView: AnalyticsEvent.View = .listen,
            fromElement: PodcastSourceElement = .feedNavigation
        ) {
            self.sourceView = sourceView
            self.fromElement = fromElement
        }
    }

    @EnvironmentObject private var navigationModel: NavigationModel
    let podcast: PodcastSeries
    let analytics: Analytics
    let titleLineLimit: Int

    init(
        podcast: PodcastSeries,
        analytics: Analytics = .init(),
        titleLineLimit: Int = 1
    ) {
        self.podcast = podcast
        self.analytics = analytics
        self.titleLineLimit = titleLineLimit
    }

    var body: some View {
        SharedPodcastShowItem(
            podcast: podcast,
            analytics: analytics,
            titleLineLimit: titleLineLimit
        ) {
            navigationModel.addScreenToSelectedTab(
                .listen(.podcastSeries(podcastId: podcast.id, fromElement: analytics.fromElement))
            )
        } image: { url in
            PodcastImage(url: url)
        }
    }
}

private struct SharedPodcastShowItem<Image: View>: View {
    let podcast: PodcastShowItemPodcast
    let analytics: PodcastShowItem.Analytics
    let titleLineLimit: Int
    let action: () -> Void
    let image: (URL?) -> Image

    init(
        podcast: PodcastShowItemPodcast,
        analytics: PodcastShowItem.Analytics,
        titleLineLimit: Int = 1,
        action: @escaping () -> Void,
        @ViewBuilder image: @escaping (URL?) -> Image
    ) {
        self.podcast = podcast
        self.analytics = analytics
        self.titleLineLimit = titleLineLimit
        self.action = action
        self.image = image
    }

    var body: some View {
        Button {
            Analytics.track(
                event: .init(
                    verb: .click,
                    view: analytics.sourceView,
                    element: analytics.fromElement.analyticsElement,
                    objectType: .podcastId,
                    objectIdentifier: podcast.id
                )
            )

            action()
        } label: {
            VStack(alignment: .leading, spacing: 4) {
                image(podcast.imageUrl)
                VStack(alignment: .leading, spacing: 0) {
                    Text(podcast.title)
                        .fontStyle(.calibreUtility.l.medium)
                        .foregroundColor(.chalk.dark800)
                        .lineLimit(titleLineLimit)
                        .multilineTextAlignment(.leading)
                        .fixedSize(horizontal: false, vertical: true)
                    Text(podcast.metadataString ?? Strings.theAthletic.localized)
                        .fontStyle(.calibreUtility.l.regular)
                        .foregroundColor(.chalk.dark500)
                        .lineLimit(1)
                }
            }
        }
    }
}

private struct PodcastImage: View {
    let url: URL?

    var body: some View {
        LazyImage(url: url?.cdnImageUrl(pixelWidth: 200))
            .aspectRatio(1, contentMode: .fit)
    }
}

struct PodcastShowItem_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            PodcastShowItem(
                podcast: PodcastPreviewHelper.athleticHockeyShow.asPodcastSeries
            )
            .previewLayout(.sizeThatFits)
            .environmentObject(NavigationModel())
        }
    }
}
