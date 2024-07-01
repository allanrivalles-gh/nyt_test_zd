//
//  LeaguePodcastList.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 1/29/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticNavigation
import AthleticUI
import SwiftUI

struct LeaguePodcastList: View {
    @StateObject var viewModel: LeaguePodcastListViewModel

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 24) {
                if !viewModel.nationalPodcasts.isEmpty {
                    PodcastGridView(
                        title: Strings.nationalPodcasts.localized,
                        podcasts: viewModel.nationalPodcasts
                    )
                }

                if !viewModel.localPodcasts.isEmpty {
                    PodcastGridView(
                        title: Strings.podcastsLocal.localized,
                        podcasts: viewModel.localPodcasts
                    )
                }
            }
            .padding(.bottom, 24)
        }
        .background(Color.chalk.dark200)
        .toolbar {
            ToolbarItem(placement: .principal) {
                NavigationBarTitleText(viewModel.leagueName)
            }
        }
        .navigationBarDefaultBackgroundColor()
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            viewModel.didAppear()

            Analytics.track(
                event: .init(
                    verb: .view,
                    view: .podcastsPage
                )
            )
        }
    }
}

private struct PodcastGridView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement

    let title: String
    let podcasts: [PodcastSeries]

    private var items: [GridItem] {
        Array(repeating: .init(.flexible(), spacing: 32), count: 2)
    }

    var body: some View {
        ListenHeaderView(title: title, topPadding: 8)
            .frame(height: 44)

        LazyVGrid(columns: items, spacing: 16) {
            let _ = DuplicateIDLogger.logDuplicates(in: podcasts, id: \.id)
            ForEach(podcasts, id: \.id) { podcast in
                VStack(alignment: .leading) {
                    PodcastShowItem(
                        podcast: podcast,
                        analytics: .init(
                            sourceView: .podcastBrowse,
                            fromElement: .discover
                        )
                    )
                }
            }
        }
        .padding(.horizontal, 16)
    }
}

struct LeaguePodcastList_Previews: PreviewProvider {
    static var previews: some View {
        LeaguePodcastList(
            viewModel:
                LeaguePodcastListViewModel(
                    channel:
                        ListenModel.PodcastCategory(
                            id: "1-nhl",
                            imageUrl:
                                "https://s3-us-west-2.amazonaws.com/theathletic-league-logos/league-1-podcasts@3x.png",
                            name: "NHL",
                            type: "league",
                            url: "nhl"
                        ),
                    listenModel: AppEnvironment.shared.listen,
                    nationalPodcasts: PodcastPreviewHelper.national,
                    localPodcasts: PodcastPreviewHelper.local
                )
        )
    }
}
