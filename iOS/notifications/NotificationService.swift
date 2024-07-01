//
//  NotificationService.swift
//  notifications
//
//  Created by Jan Remes on 11/01/2018.
//  Copyright © 2018 The Athletic. All rights reserved.
//

import IterableAppExtensions
import UserNotifications

final class NotificationService: UNNotificationServiceExtension {
    /// It is safe to store a local property because a new `NotificationService` instance is created for each notification received.
    /// https://github.com/TheAthletic/iOS/pull/4153#discussion_r1250908485
    private var activeExtension: UNNotificationServiceExtension?

    override func didReceive(
        _ request: UNNotificationRequest,
        withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void
    ) {
        if request.content.userInfo.keys.contains("athletic") {
            activeExtension = AthleticNotificationServiceExtension()
        } else {
            activeExtension = ITBNotificationServiceExtension()
        }

        activeExtension?.didReceive(request, withContentHandler: contentHandler)
    }

    override func serviceExtensionTimeWillExpire() {
        activeExtension?.serviceExtensionTimeWillExpire()
    }
}
