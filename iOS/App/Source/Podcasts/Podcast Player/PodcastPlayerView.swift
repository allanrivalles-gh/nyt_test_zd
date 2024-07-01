//
//  PodcastPlayerView.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 1/26/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AVKit
import AthleticAnalytics
import AthleticNavigation
import AthleticUI
import SwiftUI

struct PodcastPlayerView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement
    @Environment(\.safeAreaInsets) private var safeAreaInsets
    @Environment(\.presentationMode) var presentationMode
    @State private var isPaywallPresented: Bool = false

    var body: some View {
        VStack(spacing: 0) {
            PlaceholderLazyImage(
                imageUrl: listenModel.currentlyPlayingItem?.imageUrl,
                placeholder: { Rectangle().fill(Color.chalk.dark300) },
                modifyImage: {
                    $0.aspectRatio(contentMode: .fit)
                }
            )
            .aspectRatio(1.0, contentMode: .fit)
            .cornerRadius(2)

            Spacer()

            Text(listenModel.currentlyPlayingItem?.title ?? "")
                .fontStyle(.calibreHeadline.s.semibold)
                .lineLimit(5)
                .multilineTextAlignment(.center)
                .truncationMode(.tail)
                .padding(.vertical, 16)

            Spacer()

            PodcastPlayerControlsView()
        }
        .padding([.horizontal, .top], 16)
        .padding(.bottom, safeAreaInsets.bottom + 16)
        .background(Color.chalk.dark200)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button {
                    presentationMode.wrappedValue.dismiss()
                } label: {
                    Image(systemName: "xmark")
                        .foregroundColor(.chalk.dark800)
                        .padding(.top, -3)
                }
            }

            ToolbarItem(placement: .principal) {
                NavigationBarTitleText(listenModel.currentlyPlayingItem?.albumName ?? "")
            }

            ToolbarItem(placement: .navigationBarTrailing) {
                trailingButton
            }
        }
        .onAppear {
            Analytics.track(
                event: .init(
                    verb: .view,
                    view: .podcastPlayer,
                    element: .player,
                    objectType: .podcastEpisodeId,
                    objectIdentifier: listenModel.currentlyPlayingItem?.itemId
                )
            )
        }
    }

    @ViewBuilder
    private var trailingButton: some View {
        if let model = listenModel.currentlyPlayingItem?.podcastEpisodeViewModel {
            PodcastMenuEllipsis(
                episodeModel: model,
                context: .audioPlayer,
                isPaywallPresented: $isPaywallPresented,
                analyticsSource: .init(view: .podcastPlayer)
            )
            .onSimultaneousTapGesture {
                Analytics.track(
                    event: .init(
                        verb: .click,
                        view: .podcastPlayer,
                        element: .playerMenu
                    )
                )
            }
            .padding(.trailing, -4)
        } else {
            EmptyView()
        }
    }
}

private struct PodcastPlayerControlsView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement

    var body: some View {
        VStack(spacing: 8) {
            Group {
                PodcastPlayerProgressView().environmentObject(listenModel.audioPlayerManager)
                PodcastPlayerButtonsView().environmentObject(listenModel.audioPlayerManager)

                HStack {
                    SleepTimerView().environmentObject(listenModel.audioPlayerManager)
                    Spacer()

                    NavigationLink(screen: .listen(.podcastPlayerQueue)) {
                        Image(systemName: "music.note.list")
                    }
                }
            }
            .foregroundColor(.chalk.dark800)
        }
    }
}

private struct PodcastPlayerProgressView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var audioPlayerManager: AudioPlayerManager

    @State private var dragValue: Double = 0
    @State private var isDragging: Bool = false

    private var elapsedTimeDisplay: String {
        let elapsed = isDragging ? dragValue : elapsedTime
        guard
            let elapsedString = elapsed < 60
                ? Date.podcastSliderShortTimeFormatter.string(from: round(elapsed))
                : Date.podcastSliderTimeFormatter.string(from: round(elapsed))
        else {
            return ""
        }

        return elapsedString
    }

    private var remainingTimeDisplay: String {
        let remaining =
            isDragging || listenModel.isPendingPlaybackProgressUpdate
            ? (audioPlayerManager.playbackProgress?.duration ?? 0) - dragValue
            : audioPlayerManager.playbackProgress?.remaining ?? 0

        guard
            let remainingString = remaining < 60
                ? Date.podcastSliderShortTimeFormatter.string(from: round(remaining))
                : Date.podcastSliderTimeFormatter.string(from: round(remaining))
        else {
            return ""
        }

        return "-\(remainingString)"
    }

    private var elapsedTime: Double {
        listenModel.isPendingPlaybackProgressUpdate
            ? dragValue
            : audioPlayerManager.playbackProgress?.elapsed ?? 0
    }

    var body: some View {
        VStack(spacing: 0) {
            PodcastPlayerSlider(
                value: .constant(elapsedTime),
                dragValue: $dragValue,
                isDragging: $isDragging,
                range: (
                    min: 0,
                    max: audioPlayerManager.playbackProgress?.duration ?? 0.1
                ),
                onChange: { value in
                    Analytics.track(
                        event: .init(
                            verb: .seek,
                            view: .podcastPlayer,
                            element: .timebar,
                            objectType: .podcastEpisodeId,
                            objectIdentifier: listenModel.currentlyPlayingItem?.itemId
                        )
                    )

                    listenModel.seekPodcast(to: value)
                }
            )
            .frame(height: 15)
            .padding(.bottom, 5)

            HStack {
                Text(elapsedTimeDisplay)
                    .fontStyle(.calibreUtility.l.regular)

                Spacer()

                Text(remainingTimeDisplay)
                    .fontStyle(.calibreUtility.l.regular)
            }
        }
    }
}

private struct PodcastPlayerButtonsView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var audioPlayerManager: AudioPlayerManager
    @State private var showRateMultiplierSelector: Bool = false

    var body: some View {
        ZStack {
            HStack {
                Button {
                    showRateMultiplierSelector = true
                } label: {
                    Text(audioPlayerManager.playbackRateMultiplier.displayText)
                        .fontStyle(.calibreUtility.xl.regular)
                }
                .actionSheet(isPresented: $showRateMultiplierSelector) {
                    ActionSheet(
                        title: Text("Select Playback Rate"),
                        buttons:
                            AudioPlayerManager.PlayerRateMultiplier.allCases.reversed().map {
                                rate in
                                .default(
                                    Text(rate.displayText).foregroundColor(.chalk.dark800)
                                ) {
                                    Analytics.track(
                                        event: .init(
                                            verb: .click,
                                            view: .podcastPlayer,
                                            element: .playSpeed,
                                            objectType: .playSpeed,
                                            objectIdentifier: String(rate.rawValue)
                                        )
                                    )

                                    listenModel.setPodcastPlayRate(multiplier: rate)
                                }
                            } + [.cancel()]
                    )
                }

                Spacer()

                RoutePickerView()
                    .frame(width: 30, height: 30)
                    .padding(.trailing, -4)
            }

            Group {
                HStack(spacing: 130) {
                    Button {
                        Analytics.track(
                            event: .init(
                                verb: .click,
                                view: .podcastPlayer,
                                element: .rewindBackward,
                                objectType: .podcastEpisodeId,
                                objectIdentifier: listenModel.currentlyPlayingItem?.itemId
                            )
                        )

                        listenModel.skipPodcast(direction: .backward)
                    } label: {
                        Image("rw10").overlay(Text("\(UserDefaults.audioSeekBackBy)"))
                    }

                    Button {
                        Analytics.track(
                            event: .init(
                                verb: .click,
                                view: .podcastPlayer,
                                element: .fastForward,
                                objectType: .podcastEpisodeId,
                                objectIdentifier: listenModel.currentlyPlayingItem?.itemId
                            )
                        )

                        listenModel.skipPodcast(direction: .forward)
                    } label: {
                        Image("ffw10").overlay(Text("\(UserDefaults.audioSeekForwardBy)"))
                    }
                }
            }
            .fontStyle(.calibreUtility.s.regular)

            HStack {
                Button {
                    switch audioPlayerManager.playerState {
                    case .playing, .waiting:
                        Analytics.track(
                            event: .init(
                                verb: .pause,
                                view: .podcastPlayer,
                                element: .player,
                                objectType: .podcastEpisodeId,
                                objectIdentifier: listenModel.currentlyPlayingItem?.itemId
                            )
                        )
                        listenModel.pausePodcast()
                    case .failed, .paused, .stopped:
                        Analytics.track(
                            event: .init(
                                verb: .play,
                                view: .podcastPlayer,
                                element: .player,
                                objectType: .podcastEpisodeId,
                                objectIdentifier: listenModel.currentlyPlayingItem?.itemId
                            )
                        )
                        listenModel.resumePodcast()
                    }
                } label: {
                    Image(audioPlayerManager.playerState.isPlaying ? "pauseIconsLg" : "playIconLg")
                }
            }
        }
    }
}

private struct SleepTimerView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var audioPlayerManager: AudioPlayerManager
    @State private var showSleepTimerSelector: Bool = false

    var body: some View {
        HStack {
            Button {
                showSleepTimerSelector = true
            } label: {
                HStack {
                    Image(systemName: "moon.fill")
                }

                if let sleepTimerTime = audioPlayerManager.sleepTimerTimeRemaining,
                    let timeText = Date.podcastSliderShortTimeFormatter.string(
                        from: sleepTimerTime
                    )
                {
                    Text(timeText).fontStyle(.calibreUtility.xl.regular)
                }
            }
        }
        .frame(height: 20)
        .actionSheet(isPresented: $showSleepTimerSelector) {
            ActionSheet(
                title: Text(Strings.podcastSleepTimerTitle.localized),
                buttons:
                    AudioSleepTimerType.allCases.map { timerType in
                        .default(
                            Text(timerType.title).foregroundColor(.chalk.dark800)
                        ) {
                            Analytics.track(
                                event: .init(
                                    verb: .click,
                                    view: .podcastPlayer,
                                    element: .sleepTimer,
                                    objectType: .timerLength,
                                    objectIdentifier: timerType.title
                                )
                            )

                            listenModel.setPodcastSleepTimer(type: timerType)
                        }
                    } + [.cancel()]
            )
        }
    }
}

private struct RoutePickerView: UIViewRepresentable {
    func makeUIView(context: UIViewRepresentableContext<RoutePickerView>) -> AVRoutePickerView {
        let view = AVRoutePickerView()
        view.tintColor = UIColor(Color.chalk.dark800)

        return view
    }

    func updateUIView(_ uiView: UIViewType, context: UIViewRepresentableContext<RoutePickerView>) {}
}

private struct SafeAreaInsetsKey: EnvironmentKey {
    static let defaultValue: EdgeInsets = EdgeInsets()
}

extension EnvironmentValues {
    var safeAreaInsets: EdgeInsets {
        get { self[SafeAreaInsetsKey.self] }
        set { self[SafeAreaInsetsKey.self] = newValue }
    }
}

struct PodcastPlayerView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            PodcastPlayerView()
                .preferredColorScheme(.light)
            PodcastPlayerView()
                .preferredColorScheme(.dark)
        }
    }
}
