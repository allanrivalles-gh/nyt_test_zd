//
//  MockUserNotificationCenter.swift
//
//
//  Created by Jason Leyrer on 9/6/23.
//

import Foundation
import UserNotifications

@testable import AthleticNotificationCenter

final class MockUserNotificationCenter: UserNotificationCenterProtocol {
    func notificationSettings() async -> UNNotificationSettings {
        .init(coder: MockUserNotificationssNSCoder())!
    }
}

private final class MockUserNotificationssNSCoder: NSCoder {
    var authorizationStatus = UNAuthorizationStatus.authorized.rawValue

    override func decodeInt64(forKey key: String) -> Int64 {
        return Int64(authorizationStatus)
    }

    override func decodeBool(forKey key: String) -> Bool {
        return true
    }
}
