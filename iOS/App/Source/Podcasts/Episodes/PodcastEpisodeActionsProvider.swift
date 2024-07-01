//
//  PodcastEpisodeActionsProvider.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 2/9/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticNavigation
import AthleticUI
import SwiftUI

struct PodcastEpisodeActionsProvider {

    struct AnalyticsSource {
        let view: AnalyticsEvent.View
        var metaBlob: AnalyticsEvent.MetaBlob? = nil
        var element: AnalyticsEvent.Element? = nil
    }

    let context: Context
    let listenModel: ListenModel
    let entitlement: Entitlement
    let navigationModel: NavigationModel
    let podcastEpisode: PodcastEpisodeViewModel
    let isPlayed: Bool
    let isFollowing: Bool?
    let analyticsSource: AnalyticsSource?

    @Binding private(set) var isPaywallPresented: Bool

    init(
        context: PodcastEpisodeActionsProvider.Context,
        listenModel: ListenModel,
        entitlement: Entitlement,
        navigationModel: NavigationModel,
        podcastEpisode: PodcastEpisodeViewModel,
        isPlayed: Bool,
        isFollowing: Bool?,
        isPaywallPresented: Binding<Bool>,
        analyticsSource: AnalyticsSource? = nil
    ) {
        self.context = context
        self.listenModel = listenModel
        self.entitlement = entitlement
        self.navigationModel = navigationModel
        self.podcastEpisode = podcastEpisode
        self.isPlayed = isPlayed
        self.isFollowing = isFollowing
        self._isPaywallPresented = isPaywallPresented
        self.analyticsSource = analyticsSource
    }

    var isPlaybackPermitted: Bool {
        podcastEpisode.isTeaser || entitlement.hasAccessToContent
    }

    enum Context {
        case audioPlayer
        case episodeInfo
        case scores
    }

    enum Action: Int, Identifiable {
        case share
        case playNext
        case markAsPlayed
        case markAsUnplayed
        case download
        case removeDownload
        case followPodcast
        case unfollowPodcast
        case seriesDetails

        var id: Int {
            rawValue
        }

        var title: String {
            switch self {
            case .share:
                return Strings.share.localized
            case .playNext:
                return Strings.podcastQueueAdd.localized
            case .markAsPlayed:
                return Strings.podcastMarkAsPlayed.localized
            case .markAsUnplayed:
                return Strings.podcastMarkAsUnplayed.localized
            case .download:
                return Strings.podcastEpisodeDownload.localized
            case .removeDownload:
                return Strings.removeDownload.localized
            case .followPodcast:
                return Strings.followPodcast.localized
            case .unfollowPodcast:
                return Strings.unfollowPodcast.localized
            case .seriesDetails:
                return Strings.seriesDetails.localized
            }
        }

        var menuIconTitle: String {
            switch self {
            case .share:
                return "square.and.arrow.up"
            case .playNext:
                return "text.insert"
            case .markAsPlayed:
                return "checkmark.rectangle"
            case .markAsUnplayed:
                return "minus.rectangle"
            case .download:
                return "icloud.and.arrow.down"
            case .removeDownload:
                return "trash"
            case .followPodcast:
                return "plus.circle"
            case .unfollowPodcast:
                return "minus.circle"
            case .seriesDetails:
                return "info.circle"
            }
        }
    }

    @ViewBuilder
    private func menuButton(type: Action) -> some View {
        Group {
            switch type {
            case .share:
                if let shareItem = podcastEpisode.shareItem {
                    ShareLink(item: shareItem.url, message: shareItem.titleText) {
                        Label(type.title, systemImage: type.menuIconTitle)
                    }
                }
            default:
                Button(action: buttonAction(type: type)) {
                    Label(type.title, systemImage: type.menuIconTitle)
                }
            }
        }
        .foregroundColor(.chalk.dark800)
    }

    private func actionSheetButton(type: Action) -> ActionSheet.Button {
        .default(
            Text(type.title).foregroundColor(.chalk.dark800),
            action: buttonAction(type: type)
        )
    }

    private var availableActions: [Action] {
        let downloadAction: Action =
            listenModel.downloadService.isDownloaded(episodeId: podcastEpisode.episodeId)
            ? .removeDownload : .download

        let markAction: Action = isPlayed ? .markAsUnplayed : .markAsPlayed

        var followAction: Action {
            let isFollowing =
                isFollowing ?? listenModel.isFollowing(podcastId: podcastEpisode.podcastId)
            return isFollowing ? .unfollowPodcast : .followPodcast
        }

        switch context {
        case .audioPlayer:
            return [downloadAction, markAction, followAction, .share]
        case .episodeInfo:
            return [.share, .playNext, markAction, downloadAction]
        case .scores:
            return [.share, .playNext, markAction, downloadAction, .seriesDetails, followAction]
        }
    }

    private func buttonAction(type: Action) -> VoidClosure {
        switch type {
        case .share:
            return {}
        case .playNext:
            if isPlaybackPermitted {
                return { listenModel.addToQueue(item: AudioPlayerItem(viewModel: podcastEpisode)) }
            } else {
                return { isPaywallPresented.toggle() }
            }
        case .markAsPlayed, .markAsUnplayed:
            if isPlaybackPermitted {
                return {
                    let markedAs = type == .markAsPlayed ? true : false

                    listenModel.markEpisodeAsPlayed(
                        episodeId: podcastEpisode.episodeId,
                        timeElapsed: podcastEpisode.timeElapsed,
                        isNearFinished: PodcastTime.isNearEnd(
                            remaining: podcastEpisode.timeRemaining
                        ),
                        asPlayed: markedAs
                    )

                    podcastEpisode.invalidatePlayedState()
                }
            } else {
                return { isPaywallPresented.toggle() }
            }
        case .download:
            if isPlaybackPermitted {
                return {
                    listenModel.downloadEpisode(model: podcastEpisode)
                    trackPodcastDownload()
                }
            } else {
                return { isPaywallPresented.toggle() }
            }
        case .removeDownload:
            if isPlaybackPermitted {
                return { listenModel.deleteEpisode(episodeId: podcastEpisode.episodeId) }
            } else {
                return { isPaywallPresented.toggle() }
            }
        case .followPodcast:
            return {
                guard !podcastEpisode.podcastId.isEmpty else { return }

                listenModel.toggleFollowing(podcastId: podcastEpisode.podcastId, isOn: true)
                trackPodcastFollowing(isFollow: true)
            }
        case .unfollowPodcast:
            return {
                guard !podcastEpisode.podcastId.isEmpty else { return }

                listenModel.toggleFollowing(podcastId: podcastEpisode.podcastId, isOn: false)
                trackPodcastFollowing(isFollow: false)
            }
        case .seriesDetails:
            return {
                guard !podcastEpisode.podcastId.isEmpty else { return }

                navigationModel.addScreenToSelectedTab(
                    .listen(
                        .podcastSeries(
                            podcastId: podcastEpisode.podcastId,
                            fromElement: .feedNavigation
                        )
                    )
                )
            }
        }
    }

    private func trackPodcastDownload(
        eventManager: AnalyticEventManager = AnalyticsManagers.events
    ) {
        guard let analyticsSource else { return }

        Analytics.track(
            event: AnalyticsEventRecord(
                verb: .download,
                view: analyticsSource.view,
                element: analyticsSource.element ?? .downloads,
                objectType: .podcastEpisodeId,
                objectIdentifier: podcastEpisode.episodeId,
                metaBlob: analyticsSource.metaBlob
            ),
            manager: eventManager
        )
    }

    private func trackPodcastFollowing(
        isFollow: Bool,
        eventManager: AnalyticEventManager = AnalyticsManagers.events
    ) {
        guard let analyticsSource else { return }

        var verb: AnalyticsEvent.Verb {
            isFollow ? .add : .remove
        }

        var element: AnalyticsEvent.Element {
            if let element = analyticsSource.element {
                return element
            } else {
                return isFollow ? .follow : .unfollow
            }
        }

        Analytics.track(
            event: AnalyticsEventRecord(
                verb: verb,
                view: analyticsSource.view,
                element: element,
                objectType: .podcastEpisodeId,
                objectIdentifier: podcastEpisode.episodeId,
                metaBlob: analyticsSource.metaBlob
            ),
            manager: eventManager
        )
    }

    @ViewBuilder
    var menuButtons: some View {
        let _ = DuplicateIDLogger.logDuplicates(in: availableActions)
        ForEach(availableActions) { menuButton(type: $0) }
    }

    var actionSheetButtons: [ActionSheet.Button] {
        availableActions.map { actionSheetButton(type: $0) } + [.cancel {}]
    }
}
