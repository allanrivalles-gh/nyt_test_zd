//
//  PodcastDownloadService.swift
//  theathletic-ios
//
//  Created by Jan Remes on 18/11/2019.
//  Copyright © 2019 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation

enum PodcastDownloadState {
    case empty
    case downloading
    case downloaded
}

final class PodcastDownloadService: ObservableObject {
    static let shared = PodcastDownloadService()

    @Published private(set) var activeDownloads: Set<PodcastDownload> = []

    private let downloadOperationQueue = OperationQueue()
    private let dispatchQueue = DispatchQueue(
        label: "PodcastFetchQueue",
        qos: .userInitiated,
        attributes: .concurrent
    )

    private let settings = PodcastSettings()
    private var fetchTimer: Timer?
    private var cancellables = Cancellables()
    private lazy var logger = ATHLogger(category: .podcast)

    private static let storageDirectoryName = "podcast_episodes_v2"

    var totalEpisodesAllocatedSize: UInt64 {
        var size: UInt64 = 0

        do {
            size = try FileManager.default.allocatedSizeOfDirectory(
                at:
                    FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
                    .appendingPathComponent(
                        PodcastDownloadService.storageDirectoryName,
                        isDirectory: true
                    )
            )
        } catch {}

        return size
    }

    init() {
        downloadOperationQueue.maxConcurrentOperationCount = 3
        downloadOperationQueue.underlyingQueue = dispatchQueue

        if let legacyDownloadsDirectory = try? FileManager.default.url(
            for: .documentDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: false
        ).appendingPathComponent("podcast_episodes", isDirectory: true) {
            try? FileManager.default.removeItem(atPath: legacyDownloadsDirectory.relativePath)
        }
    }

    func initializeStorage() {
        let podcastsDir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
            .appendingPathComponent(PodcastDownloadService.storageDirectoryName)

        do {
            try FileManager.default.createDirectory(
                atPath: podcastsDir.path,
                withIntermediateDirectories: true,
                attributes: nil
            )
        } catch let error as NSError {
            logger.error("Unable to create podcasts directory \(error.debugDescription)")
        }
    }

    static func fileUrl(forEpisodeId episodeId: String) -> URL {
        FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
            .appendingPathComponent(PodcastDownloadService.storageDirectoryName, isDirectory: true)
            .appendingPathComponent("podcast_episode_\(episodeId).mp3")
    }

    func downloadState(episodeId: String) -> PodcastDownloadState {
        if isDownloaded(episodeId: episodeId) {
            return .downloaded
        } else if isDownloadInProgress(episodeId: episodeId) {
            return .downloading
        } else {
            return .empty
        }
    }

    func isDownloadInProgress(episodeId: String) -> Bool {
        downloadOperation(forEpisodeId: episodeId) != nil
    }

    func isDownloaded(episodeId: String) -> Bool {
        FileManager.default.fileExists(atPath: Self.fileUrl(forEpisodeId: episodeId).path)
    }

    func stopDownload(episodeId: String) {
        guard let operation = downloadOperation(forEpisodeId: episodeId) else { return }

        operation.cancel()
    }

    func downloadEpisode(
        episodeId: String,
        mediaUrl: URL,
        completion: CompletionResult<URL>?
    ) {
        activeDownloads.insert(PodcastDownload(episodeId: episodeId, progress: Progress()))

        let progressHandler: PodcastDownloadHandler = { [weak self] download in
            guard download.progress.fractionCompleted < 1 else {
                self?.activeDownloads.remove(download)
                return
            }

            self?.activeDownloads.update(with: download)
        }

        if let operation = downloadOperation(forEpisodeId: episodeId) {
            operation.progress = progressHandler
            operation.completion = completion

            return
        }

        let operation = PodcastDownloadOperation(
            podcastEpisodeId: episodeId,
            downloadUrl: mediaUrl,
            progress: progressHandler,
            completion: completion
        )

        downloadOperationQueue.addOperation(operation)
    }

    func removeDownloadedFile(forEpisodeId episodeId: String) {
        try? FileManager.default.removeItem(at: Self.fileUrl(forEpisodeId: episodeId))
    }

    // MARK: - Helpers

    private func downloadOperation(forEpisodeId episodeId: String) -> PodcastDownloadOperation? {
        downloadOperationQueue.operations
            .compactMap({ $0 as? PodcastDownloadOperation })
            .first(where: { $0.episodeId == episodeId })
    }
}
