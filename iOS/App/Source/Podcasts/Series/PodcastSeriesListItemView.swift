//
//  PodcastSeriesListItemView.swift
//  theathletic-ios
//
//  Created by Charles Huang on 1/31/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticNavigation
import AthleticUI
import Combine
import SwiftUI

struct PodcastSeriesListItemView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var navigationRestoration: NavigationRestorationController
    @EnvironmentObject private var navigationModel: NavigationModel

    @ObservedObject var model: PodcastEpisodeViewModel
    let fromElement: AnalyticsEvent.Element?
    @Binding private(set) var isPaywallPresented: Bool

    init(
        model: PodcastEpisodeViewModel,
        fromElement: AnalyticsEvent.Element? = nil,
        isPaywallPresented: Binding<Bool>
    ) {
        self.model = model
        self.fromElement = fromElement
        self._isPaywallPresented = isPaywallPresented
    }

    var body: some View {
        Group {
            VStack(alignment: .leading, spacing: 0) {
                Button(
                    action: {
                        if model.isTeaser || entitlement.hasAccessToContent {
                            Task {
                                await listenModel.handlePlayAction(
                                    model: model,
                                    analyticsView: .podcastsPage,
                                    navigationRestorationController: navigationRestoration
                                )
                            }
                        } else {
                            isPaywallPresented.toggle()
                        }
                    },
                    label: {
                        VStack(alignment: .leading, spacing: 0) {
                            HStack(alignment: .center) {
                                if model.isTeaser && !entitlement.hasAccessToContent {
                                    Image(systemName: "lock.open.fill")
                                        .resizable()
                                        .scaledToFit()
                                        .foregroundColor(.chalk.dark800)
                                        .frame(width: 8, alignment: .leading)
                                        .padding(.trailing, 2)
                                }

                                Text(model.dateDisplayString)
                                    .fontStyle(.calibreUtility.s.medium)
                                    .foregroundColor(.chalk.dark500)
                            }
                            .padding(.bottom, 8)

                            Text(model.title)
                                .lineLimit(3)
                                .multilineTextAlignment(.leading)
                                .fontStyle(.calibreHeadline.s.medium)
                                .foregroundColor(.chalk.dark800)
                                .padding(.bottom, 12)
                            Text(model.episodeDescription)
                                .lineLimit(2)
                                .multilineTextAlignment(.leading)
                                .fontStyle(.calibreUtility.l.regular)
                                .foregroundColor(.chalk.dark500)
                        }
                        .padding(16)
                    }
                )

                DividerView(style: .small, color: .chalk.dark100)

                PodcastSeriesListItemBottomView(
                    model: model,
                    isPaywallPresented: $isPaywallPresented,
                    fromElement: fromElement
                )
                .frame(height: 60, alignment: .center)
                .padding(.horizontal, 16)
            }
        }
        .background(Color.chalk.dark200)
        .cornerRadius(2)
        .podcastContextMenu(
            context: .episodeInfo,
            model: model,
            isPaywallPresented: $isPaywallPresented
        )
    }
}

private struct PodcastSeriesListItemBottomView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var navigationModel: NavigationModel

    @ObservedObject var model: PodcastEpisodeViewModel
    @Binding private(set) var isPaywallPresented: Bool
    @State private var downloadProgress: Double = -1
    @State private var listenedProgress: TimeInterval = 0.0
    @State private var currentItemCancellable: AnyCancellable?
    @State private var playerStateCancellables = Cancellables()
    @State private var playerState: AudioPlayerState? {
        didSet {
            if playerState == .playing && isCurrentListeningItem {
                model.isPlaying = true
                subscribeToAudioPlayerUpdates()
            } else {
                model.isPlaying = false
                currentItemCancellable = nil
            }
        }
    }

    let fromElement: AnalyticsEvent.Element?

    private var isCurrentListeningItem: Bool {
        listenModel.audioPlayerManager.currentItem?.podcastEpisodeViewModel.episodeId
            == model.episodeId
    }

    private var shouldShowDownloadIndicator: Bool {
        0.nextUp..<1 ~= downloadProgress
            || listenModel.downloadService.downloadState(episodeId: model.episodeId) == .downloaded
            || listenModel.downloadService.activeDownloads.contains(
                where: { $0.episodeId == model.episodeId }
            )
    }

    var body: some View {
        HStack {
            NavigationLink(
                screen: .listen(.podcastEpisode(model.podcastEpisodeDto, focusedCommentId: nil))
            ) {
                Text(Strings.details.localized.uppercased())
                    .fontStyle(.calibreUtility.s.medium)
                    .foregroundColor(.chalk.red)
            }
            .onSimultaneousTapGesture {
                Analytics.track(
                    event: .init(
                        verb: .click,
                        view: .podcastBrowse,
                        element: fromElement,
                        objectType: .podcastEpisodeId,
                        objectIdentifier: model.episodeId
                    )
                )
            }

            if (listenedProgress > 0 || model.timeElapsed > 0)
                && !model.hasFinished
                && !model.markedAsPlayed
            {
                ProgressView(
                    value: model.playingProgress(progress: listenedProgress),
                    total: 1.0
                )
                .progressViewStyle(LinearProgressViewStyle(tint: .chalk.dark400))
                .background(Color.chalk.dark100)
                .frame(width: isIpad() ? 260 : 100)
                .padding(.leading, 8)
            }

            Text(model.playingDescription(progress: listenedProgress))
                .fontStyle(.calibreUtility.s.medium)
                .foregroundColor(.chalk.dark700)
                .padding(.leading, 8)

            Spacer()

            if shouldShowDownloadIndicator {
                PodcastDownloadProgressView(
                    progress: downloadProgress,
                    state: listenModel.downloadService.downloadState(episodeId: model.episodeId)
                )
                .padding(.trailing, 8)
            }

            PodcastMenuEllipsis(
                episodeModel: model,
                context: .episodeInfo,
                foregroundColor: .chalk.dark700,
                isPaywallPresented: $isPaywallPresented,
                analyticsSource: .init(view: .podcastsPage)
            )
        }
        .onAppear {
            subscribeToDownloadProgressUpdates()
            subscribeToPlayerState()
        }
        .onDisappear {
            playerStateCancellables.removeAll()
            currentItemCancellable = nil
        }
    }

    private func subscribeToDownloadProgressUpdates() {
        listenModel.downloadService.$activeDownloads
            .compactMap { $0.first(where: { $0.episodeId == model.episodeId }) }
            .receive(on: DispatchQueue.main)
            .sink { download in
                downloadProgress = download.progress.fractionCompleted
            }
            .store(in: &playerStateCancellables)
    }

    private func subscribeToPlayerState() {
        listenModel.audioPlayerManager.$playerState
            .receive(on: DispatchQueue.main)
            .sink { state in
                playerState = state
            }
            .store(in: &playerStateCancellables)
    }

    private func subscribeToAudioPlayerUpdates() {
        currentItemCancellable = listenModel.audioPlayerManager.$currentItem
            .filter { $0?.itemId == model.episodeId }
            .flatMap { _ in
                listenModel.audioPlayerManager.$playbackProgress
            }
            .receive(on: DispatchQueue.main)
            .sink { progress in
                model.markedAsPlayed = false
                listenedProgress = progress?.elapsed ?? 0.0
            }
    }
}
