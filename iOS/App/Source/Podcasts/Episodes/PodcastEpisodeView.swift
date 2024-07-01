//
//  PodcastEpisodeView.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 12/16/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticUI
import SwiftUI

struct PodcastEpisodeView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var store: Store
    @EnvironmentObject private var compass: Compass

    @ObservedObject var model: PodcastEpisodeViewModel
    @State private var isPaywallPresented = false
    @State private var listenedProgress: TimeInterval = 0.0
    @State private var cancellables = Cancellables()
    @State private var downloadProgress: Double = -1

    let analyticData: AnalyticData

    private var shouldShowDownloadIndicator: Bool {
        0.nextUp..<1 ~= downloadProgress
            || listenModel.downloadService.downloadState(episodeId: model.episodeId) == .downloaded
            || listenModel.downloadService.activeDownloads.contains(where: {
                $0.episodeId == model.episodeId
            })
    }

    var body: some View {
        NavigationLink(
            screen: .listen(.podcastEpisode(model.podcastEpisodeDto, focusedCommentId: nil))
        ) {
            HStack(spacing: 12) {
                PlaceholderLazyImage(
                    imageUrl: model.imageUrl?.cdnImageUrl(pointHeight: 86),
                    modifyImage: {
                        $0.aspectRatio(contentMode: .fit)
                    }
                )
                .frame(width: 86, height: 86)

                VStack(alignment: .leading, spacing: 4) {
                    Text(model.dateDisplayString)
                        .fontStyle(.calibreUtility.xs.regular)
                        .foregroundColor(.chalk.dark600)

                    Text(model.title)
                        .lineLimit(2)
                        .multilineTextAlignment(.leading)
                        .fontStyle(.calibreUtility.l.medium)
                        .foregroundColor(.chalk.dark800)
                        .frame(height: 40, alignment: .topLeading)

                    HStack(alignment: .center, spacing: 8) {
                        PodcastPlayView(
                            model: model,
                            style: .regular,
                            isPaywallPresented: $isPaywallPresented
                        )

                        Text(model.playingDescription(progress: listenedProgress))
                            .fontStyle(.calibreUtility.xs.medium)
                            .foregroundColor(.chalk.dark700)

                        Spacer()

                        if shouldShowDownloadIndicator {
                            PodcastDownloadProgressView(
                                progress: downloadProgress,
                                state: listenModel.downloadService.downloadState(
                                    episodeId: model.episodeId
                                )
                            )
                            .padding(.trailing, 46)
                        }

                        PodcastMenuEllipsis(
                            episodeModel: model,
                            context: .episodeInfo,
                            foregroundColor: .chalk.dark700,
                            isPaywallPresented: $isPaywallPresented
                        )
                    }
                }
            }
            .background(Color.chalk.dark200)
            .podcastContextMenu(
                context: .episodeInfo,
                model: model,
                isPaywallPresented: $isPaywallPresented
            )
            .deeplinkListeningSheet(isPresented: $isPaywallPresented) {
                SubscriptionsNavigationView(
                    viewModel: SubscriptionsNavigationViewModel(
                        store: store,
                        entitlement: entitlement,
                        source: .podcastsEpisode(podcastEpisodeId: model.episodeId)
                    )
                )
            }
            .onAppear {
                subscribeToAudioPlayerUpdates()
                subscribeToDownloadProgressUpdates()
            }
            .onDisappear {
                cancellables.removeAll()
            }
        }
        .onSimultaneousTapGesture {
            Analytics.track(event: analyticData.click)
        }
    }

    private func subscribeToAudioPlayerUpdates() {
        listenModel.audioPlayerManager.$currentItem
            .filter { $0?.itemId == model.episodeId }
            .flatMap { _ in
                listenModel.audioPlayerManager.$playbackProgress
            }
            .receive(on: DispatchQueue.main)
            .sink { progress in
                model.markedAsPlayed = false
                listenedProgress = progress?.elapsed ?? 0.0
            }
            .store(in: &cancellables)
    }

    private func subscribeToDownloadProgressUpdates() {
        listenModel.downloadService.$activeDownloads
            .compactMap { $0.first(where: { $0.episodeId == model.episodeId }) }
            .receive(on: DispatchQueue.main)
            .sink { download in
                downloadProgress = download.progress.fractionCompleted
            }
            .store(in: &cancellables)
    }
}
