//
//  PodcastPlayerQueueView.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 2/4/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticUI
import SwiftUI

struct PodcastPlayerQueueView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement

    private var hasItemsInQueue: Bool {
        !listenModel.podcastPlayerQueue.isEmpty
    }

    var body: some View {
        List {
            Group {
                NowPlayingSection().environmentObject(listenModel.audioPlayerManager)

                if hasItemsInQueue {
                    PlayerQueueHeader(type: .upNext)

                    let _ = DuplicateIDLogger.logDuplicates(in: listenModel.podcastPlayerQueue)
                    ForEach(listenModel.podcastPlayerQueue) { item in
                        QueueItem(playerItem: item)
                    }
                    .onMove { listenModel.reorderQueueItems(fromOffsets: $0, toOffset: $1) }
                    .onDelete { listenModel.removeFromQueue(atOffsets: $0) }
                }
            }
            .listRowBackground(Color.chalk.dark200)
        }
        .listStyle(.plain)
        .background(Color.chalk.dark200)
        .navigationTitle(Strings.podcastQueueTitle.localized)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            EditButton()
                .fontStyle(.calibreUtility.xl.medium)
                .foregroundColor(.chalk.dark800)
                .opacity(hasItemsInQueue ? 1 : 0.4)
                .disabled(!hasItemsInQueue)
        }
        .onAppear {
            Analytics.track(
                event: .init(verb: .view, view: .podcastPlayer, element: .playerQueue)
            )
        }
    }
}

private struct NowPlayingSection: View {
    @EnvironmentObject private var audioManager: AudioPlayerManager

    var body: some View {
        if let item = audioManager.currentItem {
            PlayerQueueHeader(type: .nowPlaying)

            QueueItem(playerItem: item)
        } else {
            EmptyView()
        }
    }
}

private struct PlayerQueueHeader: View {
    enum SectionType {
        case nowPlaying
        case upNext

        var title: String {
            switch self {
            case .nowPlaying:
                return Strings.podcastQueueNowPlaying.localized
            case .upNext:
                return Strings.podcastQueueUpNext.localized
            }
        }
    }

    let type: SectionType

    var body: some View {
        HStack {
            Text(type.title)
                .fontStyle(.calibreUtility.l.medium)
                .foregroundColor(.chalk.dark500)

            Spacer()
        }
        .padding(.top, 16)
        .padding(.bottom, 4)
    }
}

private struct QueueItem: View {
    let playerItem: AudioPlayerItem

    var body: some View {
        VStack {
            HStack(spacing: 12) {
                PlaceholderLazyImage(
                    imageUrl: playerItem.imageUrl,
                    modifyImage: {
                        $0.aspectRatio(contentMode: .fit)
                    }
                )
                .frame(width: 50, height: 50)

                VStack(alignment: .leading, spacing: 4) {
                    Text(playerItem.title)
                        .fontStyle(.calibreUtility.l.medium)
                        .foregroundColor(.chalk.dark800)
                        .lineLimit(3)

                    Text(playerItem.albumName)
                        .fontStyle(.calibreUtility.l.regular)
                        .foregroundColor(.chalk.dark500)
                        .lineLimit(1)
                }

                Spacer()
            }
        }
        .padding(.vertical, 8)
    }
}

struct PodcastPlayerQueueView_Previews: PreviewProvider {
    static var previews: some View {
        PodcastPlayerQueueView()
    }
}
