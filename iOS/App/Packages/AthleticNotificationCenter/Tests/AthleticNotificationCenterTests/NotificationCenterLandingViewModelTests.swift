//
//  NotificationCenterLandingViewModelTests.swift
//
//
//  Created by Jason Leyrer on 9/6/23.
//

import AthleticApolloTypes
import XCTest

@testable import AthleticNotificationCenter
@testable import AthleticTestUtils

final class NotificationCenterLandingViewModelTests: XCTestCase {

    private var network: MockNotificationCenterNetwork!
    private var subject: NotificationCenterLandingViewModel!

    @MainActor
    override func setUp() {
        super.setUp()

        network = MockNotificationCenterNetwork()
        subject = NotificationCenterLandingViewModel(
            network: network,
            userNotificationCenter: MockUserNotificationCenter()
        )
    }

    @MainActor
    func testOnAppear_fetchesNotificationCounts_exactlyOnce() async {
        network.fetchNotificationCountsShouldReturn = .init(
            total: .init(activity: 3, updates: 10),
            unread: .init(activity: 1)
        )

        await subject.onAppear()

        XCTAssertEqual(network.fetchNotificationCountCallCount, 1)
        XCTAssertEqual(subject.loadingState, .loaded)
        XCTAssertEqual(subject.notificationCounts?.activityTotal, 3)
        XCTAssertEqual(subject.notificationCounts?.activityUnread, 1)
        XCTAssertEqual(subject.notificationCounts?.updatesTotal, 10)

        await subject.onAppear()

        XCTAssertEqual(network.fetchNotificationCountCallCount, 1)
    }
}
