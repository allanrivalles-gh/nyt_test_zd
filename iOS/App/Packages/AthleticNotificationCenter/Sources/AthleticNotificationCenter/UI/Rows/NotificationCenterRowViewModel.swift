//
//  NotificationCenterRowViewModel.swift
//
//
//  Created by Jason Leyrer on 7/6/23.
//

import AthleticAnalytics
import AthleticApolloNetworking
import AthleticApolloTypes
import AthleticFoundation
import AthleticNavigation
import Foundation

final class NotificationCenterRowViewModel: ObservableObject, Identifiable {

    let id: String
    let title: String
    let subtitle: String
    let type: GQL.NotificationType
    let imageUrl: URL?
    let displayDate: String
    let contentId: String?
    let createdAt: Timestamp
    let permalinkUrl: URL?
    private(set) var isNotificationRead: Bool
    private(set) var hasAppeared = false
    private let isBadgeable: Bool

    /// shouldShowUnreadNotificationState reflects a combination of read state and badgeability
    @Published private(set) var shouldShowUnreadNotificationState: Bool = false
    @Published var isContentRead: Bool = false
    @Published var isContentSaved: Bool = false

    private let deeplinkUrl: URL?
    private let deeplinkScreenProvider: DeeplinkScreenProvider
    let network: NotificationCenterNetworking
    private var cancellables = Cancellables()

    var hasImage: Bool {
        imageUrl != nil || isCommentActivity
    }

    var navigationDestination: AthleticScreen? {
        deeplinkScreenProvider(deeplinkUrl)
    }

    var isReadableContent: Bool {
        [.post, .headline, .discussion].contains(type)
    }

    var isCommentActivity: Bool {
        [.commentLikeThreshold, .commentReply].contains(type)
    }

    var analyticsObjectType: AnalyticsEvent.ObjectType {
        switch type {
        case .post:
            return .articleId
        case .boxscore, .gameStart, .gameResult:
            return .gameId
        case .commentLikeThreshold, .commentReply:
            return .commentId
        case .headline:
            return .headlineId
        case .discussion:
            return .discussionId
        case .podcast:
            return .podcastId
        case .liveRoom:
            return .roomId
        case .__unknown(_):
            return .unknown
        }
    }

    init(
        notification: GQL.NotificationsQuery.Data.Notification,
        deeplinkScreenProvider: @escaping DeeplinkScreenProvider,
        network: NotificationCenterNetworking
    ) {
        id = notification.id
        title = notification.title
        subtitle = notification.subtitle
        type = notification.type
        imageUrl = URL(string: notification.imageUrl)
        isNotificationRead = notification.isNotificationRead
        isBadgeable = notification.isBadgeable
        deeplinkUrl = URL(string: notification.deeplink)
        permalinkUrl = URL(string: notification.permalink)
        createdAt = notification.createdAt
        displayDate = createdAt.timeShort()
        self.deeplinkScreenProvider = deeplinkScreenProvider
        self.network = network
        contentId = NotificationCenterRowViewModel.contentId(
            from: deeplinkUrl,
            deeplinkScreenProvider: deeplinkScreenProvider
        )

        if let contentId {
            isContentRead = UserDynamicData.article.isRead(for: contentId.intValue)
            isContentSaved = UserDynamicData.article.isSaved(for: contentId.intValue)
        }

        refreshUnreadNotificationDisplayState()
        configureObservers()
    }

    func onAppear() {
        hasAppeared = true
    }

    @MainActor
    func markAsRead(_ isRead: Bool) {
        isNotificationRead = true
        refreshUnreadNotificationDisplayState()
    }

    private static func contentId(
        from deeplink: URL?,
        deeplinkScreenProvider: DeeplinkScreenProvider
    ) -> String? {
        guard let screen = deeplinkScreenProvider(deeplink) else { return nil }

        switch screen {
        case .feed(.article(.unknown(let articleId, _))):
            return articleId
        case .feed(.discussion(let discussionDestination)):
            return discussionDestination.id
        default:
            return nil
        }
    }

    // MARK: - State updates

    private func configureObservers() {
        NotificationCenter.default.publisher(for: Notifications.SavedArticleUpdated)
            .filter { [weak self] in self?.updatedArticleNotificationMatches($0) == true }
            .receive(on: RunLoop.main)
            .sink { [weak self] _ in
                guard let self, let contentId = self.contentId, self.isReadableContent else {
                    return
                }

                self.isContentSaved = UserDynamicData.article.isSaved(
                    for: contentId.intValue
                )
            }
            .store(in: &cancellables)

        NotificationCenter.default.publisher(for: Notifications.ReadArticleUpdated)
            .filter { [weak self] in self?.updatedArticleNotificationMatches($0) == true }
            .receive(on: RunLoop.main)
            .sink { [weak self] _ in
                guard let self, let contentId = self.contentId, self.isReadableContent else {
                    return
                }

                self.isContentRead = UserDynamicData.article.isRead(for: contentId.intValue)
            }
            .store(in: &cancellables)
    }

    private func updatedArticleNotificationMatches(
        _ notification: NotificationCenter.Publisher.Output
    ) -> Bool {
        guard
            let userInfo = notification.userInfo as? [String: Bool],
            let id = userInfo.keys.first
        else {
            return false
        }

        return id == contentId
    }

    private func refreshUnreadNotificationDisplayState() {
        shouldShowUnreadNotificationState = !isNotificationRead && isBadgeable
    }
}

extension NotificationCenterRowViewModel: Shareable {
    var shareTitle: String? {
        title
    }
}
