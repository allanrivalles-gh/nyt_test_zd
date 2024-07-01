//
//  AthleticNotificationServiceExtension.swift
//  notifications
//
//  Created by Leonardo da Silva on 30/06/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import UserNotifications

final class AthleticNotificationServiceExtension: UNNotificationServiceExtension {
    override func didReceive(
        _ request: UNNotificationRequest,
        withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void
    ) {
        /// We call this so that the system can early stop the extension before expiring if we didn't succeed to change the content.
        @Sendable func onContentUnchanged() { contentHandler(request.content) }

        let bestAttemptContent = request.content.mutableCopy() as? UNMutableNotificationContent
        guard let bestAttemptContent else {
            onContentUnchanged()
            return
        }

        /// We fail here if either the server set `mutableContent` and didn't specify the image url or if the url is malformed.
        guard
            let imageUrlString = bestAttemptContent.userInfo["image"] as? String,
            let imageUrl = URL(string: imageUrlString)
        else {
            onContentUnchanged()
            return
        }

        Task {
            guard let attachment = await downloadImage(from: imageUrl) else {
                onContentUnchanged()
                return
            }

            bestAttemptContent.attachments = [attachment]
            contentHandler(bestAttemptContent)
        }
    }
}

private func downloadImage(from url: URL) async -> UNNotificationAttachment? {
    let fileManager = FileManager.default
    let urlSession = URLSession.shared
    let processInfo = ProcessInfo.processInfo

    do {
        let (downloadedUrl, response) = try await urlSession.download(from: url)
        let filename = response.suggestedFilename ?? url.lastPathComponent

        let directory = NSURL(fileURLWithPath: NSTemporaryDirectory())
            .appendingPathComponent(processInfo.globallyUniqueString, isDirectory: true)!
        let filePath = directory.appendingPathComponent(filename)

        try fileManager.createDirectory(
            at: directory,
            withIntermediateDirectories: true,
            attributes: nil
        )
        try fileManager.moveItem(at: downloadedUrl, to: filePath)

        return try UNNotificationAttachment(identifier: "", url: filePath, options: nil)
    } catch {
        return nil
    }
}
