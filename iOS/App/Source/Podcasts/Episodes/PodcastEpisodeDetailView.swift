//
//  PodcastEpisodeDetailView.swift
//  theathletic-ios
//
//  Created by Charles Huang on 2/8/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticComments
import AthleticFoundation
import AthleticNavigation
import AthleticUI
import SwiftUI

struct PodcastEpisodeDetailView: View {
    private struct Constants {
        static let horizontalPadding: CGFloat = 20
    }

    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var store: Store
    @EnvironmentObject private var user: UserModel
    @EnvironmentObject private var compass: Compass
    @EnvironmentObject private var navigationModel: NavigationModel

    @StateObject var model: PodcastEpisodeDetailViewModel
    @State var focusedCommentId: String?

    init(
        model: @autoclosure @escaping () -> PodcastEpisodeDetailViewModel,
        focusedCommentId: String? = nil
    ) {
        _model = StateObject(wrappedValue: model())
        _focusedCommentId = State(wrappedValue: focusedCommentId)
    }

    @State private var navigatedCommentList: CommentListViewModel?
    @State private var isPaywallPresented = false
    @State private var listenedProgress: TimeInterval = 0.0
    @State private var downloadProgress: Double = -1
    @State private var cancellables = Cancellables()

    private var shouldShowDownloadIndicator: Bool {
        0.nextUp..<1 ~= downloadProgress
            || listenModel.downloadService.downloadState(
                episodeId: model.episode.episodeId
            ) == .downloaded
            || listenModel.downloadService.activeDownloads.contains(where: {
                $0.episodeId == model.episode.episodeId
            })
    }

    var body: some View {
        let surface: AnalyticsCommentSpecification.Surface = .init(
            .podcastEpisode(id: model.episode.episodeId)
        )
        ZStack {
            GeometryReader { geometry in
                PlaceholderLazyImage(
                    imageUrl: model.episode.imageUrl,
                    modifyImage: {
                        $0.aspectRatio(contentMode: .fill)
                    }
                )
                .blur(radius: 35)
                .frame(width: geometry.size.width)
                .clipped()

                Rectangle()
                    .fill(Color.chalk.dark100)
                    .opacity(0.75)

                ScrollView {
                    VStack(alignment: .leading, spacing: 24) {
                        Text(model.episode.dateDisplayString)
                            .fontStyle(.calibreUtility.l.regular)
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.chalk.dark600)

                        Text(model.episode.title)
                            .fontStyle(.calibreHeadline.m.semibold)
                            .foregroundColor(.chalk.dark800)

                        HStack(spacing: 12) {
                            PodcastPlayView(
                                model: model.episode,
                                style: .large,
                                isPaywallPresented: $isPaywallPresented
                            )

                            Text(model.episode.playingDescription(progress: listenedProgress))
                                .fontStyle(.calibreUtility.s.medium)
                                .foregroundColor(.chalk.dark800)

                            Spacer()

                            if !model.episode.disableComments {
                                Button(
                                    action: {
                                        AnalyticsCommentSpecification.onClickCommentsIcon(
                                            surface: surface,
                                            requiredValues: AnalyticDefaults()
                                        )

                                        if model.episode.isTeaser || entitlement.hasAccessToContent
                                        {
                                            navigationModel.addScreenToSelectedTab(
                                                .listen(
                                                    .podcastEpisodeComments(
                                                        episodeId: model.episode.episodeId,
                                                        title: model.episode.title,
                                                        focusedCommentId: focusedCommentId
                                                    )
                                                )
                                            )
                                        } else {
                                            isPaywallPresented.toggle()
                                        }
                                    },
                                    label: {
                                        VStack(alignment: .trailing, spacing: 4) {
                                            Text(model.episode.commentDescription)
                                                .fontStyle(.calibreUtility.s.medium)
                                                .foregroundColor(.chalk.dark800)
                                            Rectangle()
                                                .fill(Color.chalk.dark800)
                                                .frame(height: 2)
                                        }
                                        .fixedSize()
                                    }
                                )
                            }
                        }
                        .padding(.vertical, 12)

                        Text(model.episode.episodeDescription.asFormattedMarkdown)
                            .foregroundColor(.chalk.dark800)
                            .fontStyle(.calibreUtility.l.regular)
                        Spacer()
                    }
                    .padding(.top, 24)
                    .padding(.horizontal, Constants.horizontalPadding)
                }
            }
        }
        .onAppear {
            Analytics.track(
                event: .init(
                    verb: .view,
                    view: .podcastEpisode,
                    element: model.fromElement,
                    objectType: .podcastEpisodeId,
                    objectIdentifier: model.episode.episodeId
                )
            )

            guard let focusedCommentId = focusedCommentId, !model.episode.disableComments else {
                return
            }

            Task { @MainActor in

                try await Task.sleep(seconds: 0.50)

                navigationModel.listenPath.push(
                    .listen(
                        .podcastEpisodeComments(
                            episodeId: model.episode.episodeId,
                            title: model.episode.title,
                            focusedCommentId: focusedCommentId
                        )
                    )
                )
                self.focusedCommentId = nil
            }
        }
        .toolbar {
            ToolbarItem(placement: .principal) {
                NavigationBarTitleText(model.episode.podcastTitle)
            }
        }
        .navigationBarItems(
            trailing:
                HStack(spacing: 8) {
                    if shouldShowDownloadIndicator {
                        PodcastDownloadProgressView(
                            progress: downloadProgress,
                            state: listenModel.downloadService.downloadState(
                                episodeId: model.episode.episodeId
                            )
                        )
                    }

                    PodcastMenuEllipsis(
                        episodeModel: model.episode,
                        context: .episodeInfo,
                        isPaywallPresented: $isPaywallPresented,
                        analyticsSource: .init(view: .podcastEpisode)
                    )
                }
                .padding(.trailing, -4)
        )
        .navigationBarDefaultBackgroundColor()
        .navigationBarTitleDisplayMode(.inline)
        .deeplinkListeningSheet(isPresented: $isPaywallPresented) {
            SubscriptionsNavigationView(
                viewModel: SubscriptionsNavigationViewModel(
                    store: store,
                    entitlement: entitlement,
                    source: .podcastsEpisode(podcastEpisodeId: model.episode.episodeId)
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

    private func subscribeToAudioPlayerUpdates() {
        listenModel.audioPlayerManager.$currentItem
            .filter { $0?.itemId == model.episode.episodeId }
            .flatMap { _ in
                listenModel.audioPlayerManager.$playbackProgress
            }
            .receive(on: DispatchQueue.main)
            .sink { progress in
                model.episode.markedAsPlayed = false
                listenedProgress = progress?.elapsed ?? 0.0
            }
            .store(in: &cancellables)
    }

    private func subscribeToDownloadProgressUpdates() {
        listenModel.downloadService.$activeDownloads
            .compactMap { $0.first(where: { $0.episodeId == model.episode.episodeId }) }
            .receive(on: DispatchQueue.main)
            .sink { download in
                downloadProgress = download.progress.fractionCompleted
            }
            .store(in: &cancellables)
    }
}
