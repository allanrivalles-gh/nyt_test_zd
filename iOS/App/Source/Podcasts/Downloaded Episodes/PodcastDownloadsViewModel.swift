//
//  PodcastDownloadsViewModel.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 2/17/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation

final class PodcastDownloadsViewModel: ObservableObject {

    struct ShowSection {
        let title: String
        let episodes: [PodcastEpisodeViewModel]
    }

    @Published private(set) var podcastSections: [ShowSection] = []
    @Published private(set) var totalDownloadsSizeDisplay: String = ""

    private let listenModel: ListenModel
    private var cancellables = Cancellables()

    init(listenModel: ListenModel) {
        self.listenModel = listenModel

        configureSubscribers()
    }

    func didTapClearButton() {
        listenModel.deleteAllEpisodes()
    }

    private func configureSubscribers() {
        listenModel.$downloadedEpisodes
            .receive(on: RunLoop.main)
            .sink { [weak self] models in
                self?.podcastSections =
                    models
                    .compactMap({ $0.podcastTitle }).uniques
                    .map { title in
                        ShowSection(
                            title: title,
                            episodes:
                                models
                                .filter { $0.podcastTitle == title }
                                .sorted { $0.date < $1.date }
                        )
                    }

            }
            .store(in: &cancellables)

        listenModel.$totalDownloadsSize
            .receive(on: RunLoop.main)
            .sink { [weak self] size in
                self?.totalDownloadsSizeDisplay = ByteCountFormatter.string(
                    fromByteCount: Int64(size),
                    countStyle: ByteCountFormatter.CountStyle.file
                )
            }
            .store(in: &cancellables)
    }
}
