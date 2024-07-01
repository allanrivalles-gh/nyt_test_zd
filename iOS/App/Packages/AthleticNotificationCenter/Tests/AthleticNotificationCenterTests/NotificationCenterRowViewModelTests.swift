//
//  NotificationCenterRowViewModelTests.swift
//
//
//  Created by Jason Leyrer on 7/12/23.
//

import AthleticApolloTypes
import AthleticNavigation
import XCTest

@testable import AthleticNotificationCenter
@testable import AthleticTestUtils

final class NotificationCenterRowViewModelTests: XCTestCase {

    func testInit_usingReadableContent_succeeds() {
        let subject = viewModel(type: .post)

        XCTAssertTrue(subject.isReadableContent)
    }

    func testInit_usingReadableContent_withArticleDeeplink_hasContentId() {
        let subject = viewModel(
            type: .post,
            deeplink: "theathletic://article/123456",
            deeplinkScreenProvider: { _ in
                .feed(.article(.unknown(id: "123456", commentId: nil)))
            }
        )

        XCTAssertEqual(subject.contentId, "123456")
    }

    func testInit_usingOtherContent_succeeds() {
        let subject = viewModel(type: .podcast)

        XCTAssertFalse(subject.isReadableContent)
    }

    func testInit_usingUnreadCommentReply_isUnread() {
        let subject = viewModel(
            type: .commentReply,
            isBadgeable: true,
            isNotificationRead: false
        )

        XCTAssertTrue(subject.isCommentActivity)
        XCTAssertFalse(subject.isReadableContent)
        XCTAssertFalse(subject.isNotificationRead)
        XCTAssertTrue(subject.hasImage)
    }

    func testInit_usingReadCommentLikeThreshold_isRead() {
        let subject = viewModel(
            type: .commentLikeThreshold,
            isBadgeable: true,
            isNotificationRead: true
        )

        XCTAssertTrue(subject.isCommentActivity)
        XCTAssertFalse(subject.isReadableContent)
        XCTAssertTrue(subject.isNotificationRead)
        XCTAssertTrue(subject.hasImage)
    }

    func testMarkRead_true_updatesDisplayState() async {
        let subject = viewModel(
            type: .commentLikeThreshold,
            isBadgeable: true,
            isNotificationRead: false
        )

        XCTAssertFalse(subject.isNotificationRead)
        XCTAssertTrue(subject.shouldShowUnreadNotificationState)

        await subject.markAsRead(true)

        XCTAssertTrue(subject.isNotificationRead)
        XCTAssertFalse(subject.shouldShowUnreadNotificationState)
    }

    func testOnAppear_marksRowAsHavingAppeared() {
        let subject = viewModel(
            type: .commentLikeThreshold,
            isBadgeable: true,
            isNotificationRead: false
        )

        subject.onAppear()

        XCTAssertTrue(subject.hasAppeared)
    }

    func testAnalyticsObjectType_isCorrect() {
        XCTAssertEqual(viewModel(type: .commentReply).analyticsObjectType, .commentId)
        XCTAssertEqual(viewModel(type: .commentLikeThreshold).analyticsObjectType, .commentId)
        XCTAssertEqual(viewModel(type: .post).analyticsObjectType, .articleId)
        XCTAssertEqual(viewModel(type: .boxscore).analyticsObjectType, .gameId)
        XCTAssertEqual(viewModel(type: .headline).analyticsObjectType, .headlineId)
        XCTAssertEqual(viewModel(type: .discussion).analyticsObjectType, .discussionId)
        XCTAssertEqual(viewModel(type: .podcast).analyticsObjectType, .podcastId)
        XCTAssertEqual(viewModel(type: .gameStart).analyticsObjectType, .gameId)
        XCTAssertEqual(viewModel(type: .gameResult).analyticsObjectType, .gameId)
        XCTAssertEqual(viewModel(type: .liveRoom).analyticsObjectType, .roomId)
    }

    private func viewModel(
        type: GQL.NotificationType,
        isBadgeable: Bool = false,
        isNotificationRead: Bool = false,
        createdAt: Timestamp = Date(),
        deeplink: String = "theathletic://article/123456",
        permalink: String = "https://theathletic.com/123456",
        deeplinkScreenProvider: @escaping DeeplinkScreenProvider = { _ in return nil }
    ) -> NotificationCenterRowViewModel {
        NotificationCenterRowViewModel(
            notification: .init(
                id: "1",
                type: type,
                title: "Title",
                subtitle: "Subtitle",
                createdAt: createdAt,
                deeplink: deeplink,
                permalink: permalink,
                isBadgeable: isBadgeable,
                isNotificationRead: isNotificationRead,
                platform: "irritable"
            ),
            deeplinkScreenProvider: deeplinkScreenProvider,
            network: NotificationCenterPreviewHelper.network
        )
    }
}
