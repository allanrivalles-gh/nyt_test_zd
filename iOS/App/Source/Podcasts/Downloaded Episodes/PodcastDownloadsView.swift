//
//  PodcastDownloadsView.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 2/15/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticUI
import SwiftUI

struct PodcastDownloadsView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement
    @State private var isPaywallPresented = false

    @StateObject var viewModel: PodcastDownloadsViewModel

    var body: some View {
        VStack(spacing: 0) {
            DownloadSizeHeader(sizeText: viewModel.totalDownloadsSizeDisplay)

            if !viewModel.podcastSections.isEmpty {
                ScrollView {
                    LazyVStack(spacing: 0) {
                        let _ = DuplicateIDLogger.logDuplicates(
                            in: viewModel.podcastSections,
                            id: \.title
                        )
                        ForEach(viewModel.podcastSections, id: \.title) { section in
                            Section(header: PodcastShowHeader(title: section.title)) {
                                let _ = DuplicateIDLogger.logDuplicates(
                                    in: section.episodes,
                                    id: \.episodeId
                                )
                                ForEach(section.episodes, id: \.episodeId) { model in
                                    VStack(spacing: 0) {
                                        PodcastEpisodeView(
                                            model: model,
                                            analyticData: .init(
                                                click: .init(
                                                    verb: .click,
                                                    view: .podcastDownloads,
                                                    element: .following,
                                                    objectType: .podcastEpisodeId,
                                                    objectIdentifier: model.episodeId
                                                )
                                            )
                                        )
                                        .padding(.vertical, 24)
                                        .padding(.horizontal, 16)

                                        DividerView()
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Spacer()

                Text(Strings.downloadedPodcastsEmpty.localized)
                    .fontStyle(.calibreUtility.xl.medium)
                    .foregroundColor(.chalk.dark700)

                Spacer()
            }
        }
        .background(Color.chalk.dark200)
        .toolbar {
            ToolbarItem(placement: .principal) {
                NavigationBarTitleText(Strings.downloadedPodcasts.localized)
            }

            ToolbarItemGroup(placement: .navigationBarTrailing) {
                Button(Strings.clear.localized) {
                    viewModel.didTapClearButton()
                }
                .fontStyle(.calibreUtility.xl.medium)
                .foregroundColor(.chalk.dark800)
                .opacity(!viewModel.podcastSections.isEmpty ? 1 : 0.4)
                .disabled(viewModel.podcastSections.isEmpty)
            }
        }
        .onAppear {
            Analytics.track(
                event: .init(
                    verb: .view,
                    view: .podcastDownloads,
                    element: .downloads
                )
            )
        }
    }
}

private struct DownloadSizeHeader: View {
    let sizeText: String

    var body: some View {
        HStack {
            Group {
                Text(Strings.downloadedWithColon.localized.uppercased())
                Spacer()
                Text(sizeText)
            }
            .fontStyle(.calibreUtility.s.medium)
            .foregroundColor(.chalk.dark400)
        }
        .frame(height: 40)
        .padding(.horizontal, 16)
        .background(Color.chalk.dark100)
    }
}

private struct PodcastShowHeader: View {
    let title: String

    var body: some View {
        HStack {
            Text(title)
                .fontStyle(.calibreUtility.l.medium)
                .foregroundColor(.chalk.dark500)

            Spacer()
        }
        .padding([.top, .horizontal], 16)
        .background(Color.chalk.dark200)
    }
}

struct PodcastDownloadsView_Previews: PreviewProvider {
    static var previews: some View {
        PodcastDownloadsView(
            viewModel: PodcastDownloadsViewModel(
                listenModel: ListenModel(
                    network: AppEnvironment.shared.network,
                    compass: AppEnvironment.shared.compass
                )
            )
        )
    }
}
