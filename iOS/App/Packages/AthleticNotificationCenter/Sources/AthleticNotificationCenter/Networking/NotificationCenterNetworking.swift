//
//  NotificationCenterNetworking.swift
//
//
//  Created by Jason Leyrer on 7/11/23.
//

import Apollo
import AthleticApolloNetworking
import AthleticApolloTypes
import AthleticFoundation
import AthleticRestNetwork
import Foundation

public protocol NotificationCenterNetworking {

    var apolloStore: ApolloStore { get }

    /// Queries
    func fetchNotifications(tab: NotificationCenterLanding.TabType) async throws
        -> [GQL.NotificationsQuery.Data.Notification]

    func fetchNotificationCounts() async throws
        -> GQL.NotificationCountsQuery.Data.NotificationCount

    @discardableResult
    func fetchSavedArticles(usingCache: Bool) async throws -> [GQL.ArticleLiteAuthor]

    /// Mutations
    func markNotificationsReadState(ids: [String], isRead: Bool) async throws

    func updateArticleSaveState(id: String, isSaved: Bool) async throws

    func updateArticleReadState(id: String, isRead: Bool, percentRead: CGFloat?) async
}

extension NetworkModel: NotificationCenterNetworking {

    // MARK: - Queries
    public func fetchNotifications(tab: NotificationCenterLanding.TabType) async throws
        -> [GQL.NotificationsQuery.Data.Notification]
    {
        let gqlTab: GQL.NotificationTab

        switch tab {
        case .activity:
            gqlTab = .activity
        case .updates:
            gqlTab = .updates
        }

        return try await graphFetch(
            query: GQL.NotificationsQuery(tab: gqlTab),
            cachePolicy: .fetchIgnoringCacheData
        ).notifications
    }

    public func fetchNotificationCounts() async throws
        -> GQL.NotificationCountsQuery.Data.NotificationCount
    {
        try await graphFetch(
            query: GQL.NotificationCountsQuery(),
            cachePolicy: .fetchIgnoringCacheData
        ).notificationCounts
    }

    @discardableResult
    public func fetchSavedArticles(usingCache: Bool) async throws -> [GQL.ArticleLiteAuthor] {
        let query = GQL.UserArticlesQuery()
        let cachePolicy: CachePolicy =
            usingCache ? .returnCacheDataDontFetch : .fetchIgnoringCacheData
        let response = try await graphFetch(query: query, cachePolicy: cachePolicy)

        return response.userArticles.map { $0.fragments.articleLiteAuthor }
    }

    public func markNotificationsReadState(ids: [String], isRead: Bool) async throws {
        try await graphPerform(
            mutation: GQL.MarkNotificationsReadStateMutation(ids: ids, isNotificationRead: isRead)
        )
    }

    public func updateArticleSaveState(id: String, isSaved: Bool) async throws {
        if isSaved {
            try await graphPerform(mutation: GQL.SaveArticleMutation(id: id), queue: .main)
        } else {
            try await graphPerform(mutation: GQL.UnsaveArticleMutation(id: id), queue: .main)
        }

        UserDynamicData.article.save(for: id.intValue, value: isSaved)

        apolloStore.withinReadWriteTransaction { transaction in

            /// Update saved articles list
            if isSaved {
                Task { [weak self] in
                    guard let self else { return }

                    do {
                        let articles = try await self.fetchSavedArticles(usingCache: false)
                        if !articles.contains(where: { $0.id == id }) {
                            self.logger.warning(
                                "Following a save of articleId \(id), saved articles network fetch does not contain the newly saved article."
                            )
                        }
                    } catch let error {
                        self.logger.error(
                            "Following a save of articleId \(id), fetching saved articles from the network failed with: \(error.localizedDescription)"
                        )
                    }
                }
            } else {
                try? transaction.update(query: GQL.UserArticlesQuery()) {
                    (data: inout GQL.UserArticlesQuery.Data) in

                    let filteredArticles = data.userArticles.filter {
                        $0.fragments.articleLiteAuthor.id != id
                    }

                    data.userArticles = filteredArticles
                }
            }

            postNotification(Notifications.SavedArticleUpdated, info: [id: isSaved])
        }
    }

    public func updateArticleReadState(
        id: String,
        isRead: Bool,
        percentRead: CGFloat? = nil
    ) async {

        let percentReadValue: Int?

        if let percentRead = percentRead {
            percentReadValue = Int(percentRead)
        } else {
            percentReadValue = nil
        }

        do {
            ArticleReadStateRecord.remove(with: id)

            try await graphPerform(
                mutation: GQL.LogArticleReadMutation(
                    input: GQL.LogArticleReadInput(
                        articleId: id,
                        isRead: isRead,
                        percentRead: percentReadValue
                    )
                )
            )
        } catch {
            /// Persist the record so we can send it later
            ArticleReadStateRecord(
                articleId: id,
                isRead: isRead,
                percentRead: percentRead
            ).createOrUpdate()
        }

        UserDynamicData.article.read(for: id.intValue, value: isRead)
        postNotification(Notifications.ReadArticleUpdated, info: [id: isRead])
    }
}
