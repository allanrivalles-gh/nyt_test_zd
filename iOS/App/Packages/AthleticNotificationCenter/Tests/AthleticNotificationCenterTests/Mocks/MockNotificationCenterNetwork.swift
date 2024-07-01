//
//  MockNotificationCenterNetwork.swift
//
//
//  Created by Jason Leyrer on 7/12/23.
//

import Apollo
import AthleticApolloNetworking
import AthleticApolloTypes
import Foundation

@testable import AthleticNotificationCenter

final class MockNotificationCenterNetwork: NotificationCenterNetworking {

    var apolloStore = ApolloStore()
    var fetchNotificationsShouldReturn: [GQL.NotificationsQuery.Data.Notification] = []
    var fetchNotificationCountsShouldReturn: GQL.NotificationCountsQuery.Data.NotificationCount =
        .init(total: .init(activity: 0, updates: 0), unread: .init(activity: 0))
    var fetchSavedArticlesShouldReturn = [GQL.ArticleLiteAuthor.makeMock()]

    private(set) var fetchNotificationsCallCount: Int = 0
    private(set) var fetchNotificationCountCallCount: Int = 0
    private(set) var markNotificationsReadStateCallCount: Int = 0
    private(set) var fetchSavedArticlesCallCount: Int = 0
    private(set) var updateArticleSaveStateCallCount: Int = 0
    private(set) var updateArticleReadStateCallCount: Int = 0

    func fetchNotifications(tab: AthleticNotificationCenter.NotificationCenterLanding.TabType)
        async throws -> [GQL.NotificationsQuery.Data.Notification]
    {
        fetchNotificationsCallCount += 1
        return fetchNotificationsShouldReturn
    }

    func fetchNotificationCounts() async throws
        -> GQL.NotificationCountsQuery.Data.NotificationCount
    {
        fetchNotificationCountCallCount += 1
        return fetchNotificationCountsShouldReturn
    }

    func markNotificationsReadState(ids: [String], isRead: Bool) async throws {
        markNotificationsReadStateCallCount += 1
    }

    @discardableResult
    func fetchSavedArticles(usingCache: Bool) async throws -> [GQL.ArticleLiteAuthor] {
        fetchSavedArticlesCallCount += 1
        return fetchSavedArticlesShouldReturn
    }

    func updateArticleSaveState(id: String, isSaved: Bool) async throws {
        updateArticleSaveStateCallCount += 1
    }

    func updateArticleReadState(id: String, isRead: Bool, percentRead: CGFloat?) async {
        updateArticleReadStateCallCount += 1
    }
}
