//
//  NotificationCenterLandingTests.swift
//
//
//  Created by Jason Leyrer on 8/23/23.
//

import XCTest

@testable import AthleticAnalytics
@testable import AthleticNotificationCenter
@testable import AthleticTestUtils

final class NotificationCenterLandingTests: XCTestCase {

    func testTabType_analyticsValues_areCorrect() {
        XCTAssertEqual(
            NotificationCenterLanding.TabType.activity(areCommentNotificationsEnabled: true)
                .analyticsElement,
            .activity
        )
        XCTAssertEqual(
            NotificationCenterLanding.TabType.activity(areCommentNotificationsEnabled: true)
                .analyticsObjectType,
            .activity
        )
        XCTAssertEqual(NotificationCenterLanding.TabType.updates.analyticsElement, .updates)
        XCTAssertEqual(NotificationCenterLanding.TabType.updates.analyticsObjectType, .updates)
    }
}
