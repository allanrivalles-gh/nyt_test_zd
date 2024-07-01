//
//  TabContentViewModelTests.swift
//
//
//  Created by Jason Leyrer on 7/12/23.
//

import AthleticApolloTypes
import AthleticNavigation
import XCTest

@testable import AthleticAnalytics
@testable import AthleticNotificationCenter
@testable import AthleticTestUtils

extension AnalyticsConfiguration {
    static var eventTesting: Self {
        .init(
            schemaID: 26,
            identifier: .testingEvents,
            environment: .staging,
            flushRecordsTimeInterval: 30
        )
    }
}

extension AnalyticEventManager {
    static var testing: Self {
        .init(
            withConfiguration: .eventTesting,
            encoder: AnalyticsManagers.eventEncoder,
            decoder: AnalyticsManagers.eventDecoder
        )
    }
}

final class TabContentViewModelTests: XCTestCase {

    private var network: MockNotificationCenterNetwork!
    private var application: MockApplication!
    private var navigationModel = NavigationModel()
    private let eventManager = AnalyticEventManager.testing

    private let gqlNotifications: [GQL.NotificationsQuery.Data.Notification] = [
        .init(
            id: "1",
            type: .commentReply,
            title: "Title",
            subtitle: "Subtitle",
            createdAt: Date(),
            deeplink: "theathletic://article/123456?comment_id=123456",
            permalink: "https://theathletic.com/123456",
            isBadgeable: true,
            isNotificationRead: false,
            platform: "iterable"
        ),
        .init(
            id: "2",
            type: .commentLikeThreshold,
            title: "Title",
            subtitle: "Subtitle",
            createdAt: Date().add(days: -1),
            deeplink: "theathletic://article/123456?comment_id=123456",
            permalink: "https://theathletic.com/123456",
            isBadgeable: true,
            isNotificationRead: false,
            platform: "iterable"
        ),
        .init(
            id: "3",
            type: .post,
            title: "Title",
            subtitle: "Subtitle",
            createdAt: Date().add(days: -2),
            deeplink: "theathletic://article/123456",
            permalink: "https://theathletic.com/123456",
            isBadgeable: false,
            isNotificationRead: false,
            platform: "iterable"
        ),
        .init(
            id: "4",
            type: .boxscore,
            title: "Title",
            subtitle: "Subtitle",
            createdAt: Date().add(days: -3),
            deeplink: "theathletic://boxscore/abc123",
            permalink: "https://theathletic.com/123456",
            isBadgeable: false,
            isNotificationRead: false,
            platform: "iterable"
        ),
    ]

    override func setUp() async throws {
        try await super.setUp()

        application = MockApplication()
        network = MockNotificationCenterNetwork()
        network.fetchNotificationsShouldReturn = gqlNotifications

        await eventManager.debugClearRecords()
    }

    override func tearDown() async throws {
        await eventManager.debugClearRecords()
    }

    func testFetchNotifications_activityTab_isPopulatedIfCommentNotificationsAreEnabled() async {
        let subject = constructViewModel(type: .activity(areCommentNotificationsEnabled: true))
        await subject.fetchNotifications()

        XCTAssertEqual(subject.notificationModels.count, 4)
        XCTAssertNil(subject.emptyContentDisplayType)
    }

    func testFetchNotifications_activityTab_isEmptyIfCommentNotificationsAreDisabled() async {
        let subject = constructViewModel(type: .activity(areCommentNotificationsEnabled: false))
        await subject.fetchNotifications()

        XCTAssertTrue(subject.notificationModels.isEmpty)
        XCTAssertEqual(subject.emptyContentDisplayType, .commentNotificationsOff)
    }

    func testFetchNotifications_activityTab_showsEmptyState() async {
        network.fetchNotificationsShouldReturn = []

        let subject = constructViewModel(type: .activity(areCommentNotificationsEnabled: true))
        await subject.fetchNotifications()

        XCTAssertTrue(subject.notificationModels.isEmpty)
        XCTAssertEqual(subject.emptyContentDisplayType, .noActivity)
    }

    func testFetchNotifications_updatesTab_showsEmptyState() async {
        network.fetchNotificationsShouldReturn = []

        let subject = constructViewModel(type: .updates)
        await subject.fetchNotifications()

        XCTAssertTrue(subject.notificationModels.isEmpty)
        XCTAssertEqual(subject.emptyContentDisplayType, .noUpdates)
    }

    func testMarkRead_unreadNotification_becomesRead() async {
        let subject = constructViewModel(type: .activity(areCommentNotificationsEnabled: true))
        await subject.fetchNotifications()
        await subject.markRead(notificationModel: subject.notificationModels[0])

        XCTAssertEqual(network.markNotificationsReadStateCallCount, 1)
        XCTAssertTrue(subject.notificationModels.first!.isNotificationRead)

        /// Ensure there are no redundant calls on subsequent taps once already marked read

        await subject.markRead(notificationModel: subject.notificationModels[0])

        XCTAssertEqual(network.markNotificationsReadStateCallCount, 1)
        XCTAssertTrue(subject.notificationModels.first!.isNotificationRead)
    }

    @MainActor
    func testMarkRead_whenBadgeable_decrementsAppBadgeValue() async {
        application.applicationIconBadgeNumber = 2

        let subject = constructViewModel(type: .activity(areCommentNotificationsEnabled: true))
        await subject.fetchNotifications()
        await subject.markRead(notificationModel: subject.notificationModels[0])

        XCTAssertEqual(network.markNotificationsReadStateCallCount, 1)
        XCTAssertEqual(application.applicationIconBadgeNumber, 1)
    }

    @MainActor
    func testMarkRead_whenNotBadgeable_doesNotDecrementAppBadgeValue() async {
        application.applicationIconBadgeNumber = 2

        let subject = constructViewModel(type: .activity(areCommentNotificationsEnabled: true))
        await subject.fetchNotifications()
        await subject.markRead(notificationModel: subject.notificationModels[3])

        XCTAssertEqual(network.markNotificationsReadStateCallCount, 1)
        XCTAssertEqual(application.applicationIconBadgeNumber, 2)
    }

    func testFetchNotifications_interactive_marksAllNotificationsAsRead() async {
        let subject = constructViewModel(type: .activity(areCommentNotificationsEnabled: true))
        await subject.fetchNotifications(isInteractive: true)

        XCTAssertEqual(network.markNotificationsReadStateCallCount, 1)
        XCTAssertTrue(subject.notificationModels.filter({ !$0.isNotificationRead }).isEmpty)
    }

    @MainActor
    func testFetchNotifications_interactive_resetsBadgeNumber() async {
        application.applicationIconBadgeNumber = 2

        let subject = constructViewModel(type: .activity(areCommentNotificationsEnabled: true))
        await subject.fetchNotifications(isInteractive: true)

        XCTAssertEqual(network.markNotificationsReadStateCallCount, 1)
        XCTAssertEqual(application.applicationIconBadgeNumber, 0)
    }

    @MainActor
    func testFetchNotifications_noninteractive_doesNotResetBadgeNumber() async {
        application.applicationIconBadgeNumber = 2

        let subject = constructViewModel(type: .activity(areCommentNotificationsEnabled: true))
        await subject.fetchNotifications(isInteractive: false)

        XCTAssertEqual(network.markNotificationsReadStateCallCount, 0)
        XCTAssertEqual(application.applicationIconBadgeNumber, 2)
    }

    @MainActor
    func testDidTapMoreUnread_whenBadgedUnreadExist_focusesOldestBadgedUnread() async {
        let subject = constructViewModel(type: .activity(areCommentNotificationsEnabled: true))
        await subject.fetchNotifications()

        subject.notificationAppeared(notificationModel: subject.notificationModels[0])
        subject.didTapMoreUnread()

        XCTAssertEqual(subject.focusedItemId, subject.notificationModels[1].id)
    }

    func testNotificationTapped_tracksClick_andMarksAsRead() async {
        var subject = constructViewModel(
            type: .activity(areCommentNotificationsEnabled: true),
            deeplinkScreenProvider: { _ in
                return .feed(.article(.detail(id: "123456", commentId: "123456")))
            }
        )

        await subject.fetchNotifications()
        var clickRecords: [AnalyticsEventRecord] = []

        clickRecords.append(
            constructEventRecord(
                element: .activity,
                notificationModel: subject.notificationModels[0],
                objectIdentifier: "123456",
                analyticsNotificationType: "reply"
            )
        )

        await subject.notificationTapped(notificationModel: subject.notificationModels[0])
        XCTAssertEqual(network.markNotificationsReadStateCallCount, 1)

        clickRecords.append(
            constructEventRecord(
                element: .activity,
                notificationModel: subject.notificationModels[1],
                objectIdentifier: "123456",
                analyticsNotificationType: "likes"
            )
        )

        await subject.notificationTapped(notificationModel: subject.notificationModels[1])
        XCTAssertEqual(network.markNotificationsReadStateCallCount, 2)

        subject = constructViewModel(
            type: .updates,
            deeplinkScreenProvider: { _ in
                return .feed(.article(.detail(id: "123456", commentId: nil)))
            }
        )

        await subject.fetchNotifications()

        clickRecords.append(
            constructEventRecord(
                element: .updates,
                notificationModel: subject.notificationModels[2],
                objectIdentifier: "123456"
            )
        )

        await subject.notificationTapped(notificationModel: subject.notificationModels[2])
        XCTAssertEqual(network.markNotificationsReadStateCallCount, 3)

        subject = constructViewModel(
            type: .updates,
            deeplinkScreenProvider: { _ in
                return .scores(.boxScore(.init(gameId: "abc123")))
            }
        )

        await subject.fetchNotifications()

        clickRecords.append(
            constructEventRecord(
                element: .updates,
                notificationModel: subject.notificationModels[3],
                objectIdentifier: "abc123"
            )
        )

        await subject.notificationTapped(notificationModel: subject.notificationModels[3])
        XCTAssertEqual(network.markNotificationsReadStateCallCount, 4)

        let hasRecords = await eventManager.has(records: clickRecords)
        XCTAssertTrue(hasRecords)
    }

    private func constructViewModel(
        type: NotificationCenterLanding.TabType,
        deeplinkScreenProvider: @escaping (URL?) -> AthleticScreen? = { _ in return nil }
    ) -> TabContentViewModel {
        TabContentViewModel(
            type: type,
            network: network,
            navigationModel: navigationModel,
            deeplinkScreenProvider: deeplinkScreenProvider,
            analyticsDefaults: PreviewAnalyticDefaults(),
            eventManager: eventManager,
            application: application
        )
    }

    private func constructEventRecord(
        element: AnalyticsEvent.Element,
        notificationModel: NotificationCenterRowViewModel,
        objectIdentifier: String,
        analyticsNotificationType: String? = nil
    ) -> AnalyticsEventRecord {
        AnalyticsEventRecord(
            verb: .click,
            view: .notifications,
            element: element,
            objectType: notificationModel.analyticsObjectType,
            objectIdentifier: objectIdentifier,
            metaBlob: .init(
                isUnread: !notificationModel.isNotificationRead,
                notificationType: analyticsNotificationType,
                requiredValues: PreviewAnalyticDefaults()
            ),
            requiredValues: PreviewAnalyticDefaults()
        )
    }
}
