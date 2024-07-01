//
//  PodcastDownloadOperation.swift
//  theathletic-ios
//
//  Created by Jan Remes on 25/02/2019.
//  Copyright © 2019 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation
import UIKit

struct PodcastDownload: Hashable {
    let episodeId: String
    var progress: Progress

    func hash(into hasher: inout Hasher) {
        hasher.combine(episodeId)
    }

    static func == (lhs: PodcastDownload, rhs: PodcastDownload) -> Bool {
        lhs.episodeId == rhs.episodeId
    }
}

typealias PodcastDownloadHandler = ((PodcastDownload) -> Void)?

class PodcastDownloadOperation: AsynchronousOperation {

    public let episodeId: String
    public let downloadUrl: URL
    public var progress: PodcastDownloadHandler
    public var completion: CompletionResult<URL>?

    /// Set to true if operation is part of new episodes background download queue. It respect network settings from Podcast Settings screen.
    var isNewEpisodeBackgroundFetch = false

    private var backgroundTaskIdentifier: UIBackgroundTaskIdentifier?
    private let settings = PodcastSettings()

    private var request: URLSessionDownloadTask?
    private lazy var logger = ATHLogger(category: .podcast)
    private let featureFlags = AppEnvironment.shared.compass.config.flags

    deinit {
        request = nil
        backgroundTaskIdentifier = nil
    }

    init(podcastEpisodeId: String, downloadUrl: URL) {
        self.episodeId = podcastEpisodeId
        self.downloadUrl = downloadUrl
        self.progress = nil
        self.completion = nil

        super.init()
    }

    init(
        podcastEpisodeId: String,
        downloadUrl: URL,
        progress: PodcastDownloadHandler,
        completion: CompletionResult<URL>?
    ) {
        episodeId = podcastEpisodeId
        self.progress = progress
        self.completion = completion
        self.downloadUrl = downloadUrl

        super.init()
    }

    override func execute() {

        // adjust downloading in case of fetching new episodes
        if isNewEpisodeBackgroundFetch {

            // check disk size to be greater than 200MB
            let diskSize = DiskStatus.freeDiskSpaceInMegaBytesOpportunistic
            if diskSize <= 200 {
                logger.warning("There is less than 200MB free on device, aborting download")
                cancel()
                return
            }

            let networkStatus =
                AppEnvironment.shared.network.restNetwork.availableInterfacesTypes ?? []

            switch settings.newEpisodeDownloadType {
            case .streamOnly:
                cancel()
                return
            case .downloadOnWifi:
                if !networkStatus.contains(.wifi) {
                    cancel()
                    return
                }
            case .downloadOnWifiOrCelluar:
                if !networkStatus.contains(.cellular) {
                    cancel()
                    return
                }
            }
        }

        backgroundTaskIdentifier = UIApplication.shared.beginBackgroundTask(
            withName: "podcast-fetch-\(episodeId)",
            expirationHandler: { [weak self] in

                if let identifier = self?.backgroundTaskIdentifier {
                    UIApplication.shared.endBackgroundTask(identifier)
                }
                self?.cancel()
            }
        )
        request = BackgroundFetchManager.shared.downloadTask(with: downloadUrl, op: self)
        request?.resume()
    }

    override func cancel() {
        request?.cancel()
        if let backgroundTaskIdentifier = backgroundTaskIdentifier {
            UIApplication.shared.endBackgroundTask(backgroundTaskIdentifier)
        }

        super.cancel()
    }

    func completeExecution() {
        if let backgroundTaskIdentifier = backgroundTaskIdentifier {
            UIApplication.shared.endBackgroundTask(backgroundTaskIdentifier)
        }

        onMain {
            if let completionHandler = AppDelegate.main.backgroundSessionCompletionHandler {
                AppDelegate.main.backgroundSessionCompletionHandler = nil
                completionHandler()
                self.completion?(.success(self.downloadUrl))
            }
        }
        self.finish()
    }

    func setFileProtection(fileUrl: URL) {
        do {
            // this is explicitly setting default file protection level
            try FileManager.default.setAttributes(
                [.protectionKey: FileProtectionType.completeUntilFirstUserAuthentication],
                ofItemAtPath: fileUrl.path
            )

        } catch let error as NSError {
            logger.error(
                "Error excluding \(fileUrl.lastPathComponent) from backup \(error)"
            )
        }
    }

    @discardableResult
    func addSkipBackupAttribute(fileUrl: URL) -> Bool {

        var fileUrl = fileUrl

        var success: Bool
        do {
            var resourceValues = URLResourceValues()
            resourceValues.isExcludedFromBackup = true
            try fileUrl.setResourceValues(resourceValues)

            success = true
        } catch let error as NSError {
            success = false
            logger.error(
                "Error excluding \(fileUrl.lastPathComponent) from backup \(error)"
            )
        }

        return success
    }
}

extension PodcastDownloadOperation: URLSessionDownloadDelegate {
    func urlSession(
        _ session: URLSession,
        downloadTask: URLSessionDownloadTask,
        didWriteData bytesWritten: Int64,
        totalBytesWritten: Int64,
        totalBytesExpectedToWrite: Int64
    ) {
        if downloadTask == self.request {
            DispatchQueue.main.async { [weak self] in
                if let episodeId = self?.episodeId, let progress = self?.request?.progress {
                    progress.totalUnitCount = totalBytesExpectedToWrite
                    progress.completedUnitCount = totalBytesWritten
                    self?.progress?(PodcastDownload(episodeId: episodeId, progress: progress))
                }
            }
        }
    }

    func urlSession(
        _ session: URLSession,
        downloadTask: URLSessionDownloadTask,
        didFinishDownloadingTo location: URL
    ) {
        do {
            let fileUrl = PodcastDownloadService.fileUrl(forEpisodeId: episodeId)

            logger.debug("saving podcast at \(fileUrl)")

            try FileManager.default.moveItem(at: location, to: fileUrl)
            addSkipBackupAttribute(fileUrl: fileUrl)
            setFileProtection(fileUrl: fileUrl)

            onMain { [logger, weak self] in
                logger.debug("saved podcast at \(fileUrl)")
                self?.completion?(.success(fileUrl))
                postNotification(Notifications.PodcastFileStatusChanged)
            }

            completeExecution()
        } catch {
            onMain { [logger, weak self] in
                // handle filesystem error
                logger.error("failed to save podcast \(error)")
                self?.completion?(.failure(error))
            }

            completeExecution()
        }
    }
}
