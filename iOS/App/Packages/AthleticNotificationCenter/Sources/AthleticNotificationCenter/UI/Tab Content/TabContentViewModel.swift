//
//  TabContentViewModel.swift
//
//
//  Created by Jason Leyrer on 7/12/23.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticNavigation
import Combine
import Foundation
import UIKit

final class TabContentViewModel: ObservableObject {

    @Published private(set) var notificationModels: [NotificationCenterRowViewModel] = []
    @Published private(set) var loadingState: LoadingState = .initial
    @Published private(set) var emptyContentDisplayType: EmptyContentDisplayType? = nil
    @Published private(set) var focusedItemId: String? = nil
    @Published private(set) var hasNonvisibleBadgedUnread: Bool = false

    private let tabType: NotificationCenterLanding.TabType
    private let network: NotificationCenterNetworking
    private let navigationModel: NavigationModel
    private let deeplinkScreenProvider: DeeplinkScreenProvider
    private let analyticsDefaults: AnalyticsRequiredValues
    private let eventManager: AnalyticEventManager
    private var application: UIApplicationProtocol
    private var hasAppeared: Bool = false
    private var selectedTabSubscription: AnyCancellable?

    private var earliestUnreadBadgedNotification: NotificationCenterRowViewModel? {
        notificationModels
            .filter { $0.shouldShowUnreadNotificationState }
            .min(by: { $0.createdAt < $1.createdAt })
    }

    init(
        type: NotificationCenterLanding.TabType,
        network: NotificationCenterNetworking,
        navigationModel: NavigationModel,
        deeplinkScreenProvider: @escaping DeeplinkScreenProvider,
        analyticsDefaults: AnalyticsRequiredValues,
        eventManager: AnalyticEventManager = AnalyticsManagers.events,
        application: UIApplicationProtocol = UIApplication.shared
    ) {
        self.tabType = type
        self.network = network
        self.navigationModel = navigationModel
        self.deeplinkScreenProvider = deeplinkScreenProvider
        self.analyticsDefaults = analyticsDefaults
        self.eventManager = eventManager
        self.application = application

        configureSubscriptions()
    }

    @MainActor
    func onAppear() async {
        hasAppeared = true

        guard loadingState != .loaded else { return }
        await fetchNotifications(isInitialLoad: loadingState == .initial)
    }

    func onDisappear() {
        /// if the tab has been seen, there are no offscreen badged items, and we've backed out
        /// of Notification Center, mark everything as read
        guard hasAppeared, !hasNonvisibleBadgedUnread, navigationModel.accountPath.isEmpty else {
            return
        }

        Task {
            await markAllNotificationsRead()
        }
    }

    @MainActor
    func didTapMoreUnread() {
        focusedItemId = earliestUnreadBadgedNotification?.id
    }

    @MainActor
    func notificationAppeared(notificationModel: NotificationCenterRowViewModel) {
        notificationModel.onAppear()
        invalidateHasNonvisibleBadgedUnread()
    }

    func notificationTapped(notificationModel: NotificationCenterRowViewModel) async {
        await trackClick(notificationModel: notificationModel)
        await markRead(notificationModel: notificationModel)
    }

    func configureSubscriptions() {
        selectedTabSubscription = navigationModel.selectedTab.$value
            .sink { [weak self] newTab in
                /// Only mark all as read when switching tabs if the user has seen the list,
                /// and there are no unseen, badged notifications offscreen
                guard
                    let self,
                    self.hasAppeared,
                    newTab != .account,
                    !self.hasNonvisibleBadgedUnread
                else {
                    return
                }

                Task {
                    await self.markAllNotificationsRead()
                }
            }
    }

    @MainActor
    func fetchNotifications(isInitialLoad: Bool = false, isInteractive: Bool = false) async {
        if case .activity(let areCommentNotificationsEnabled) = tabType,
            !areCommentNotificationsEnabled
        {
            loadingState = .loaded
            emptyContentDisplayType = .commentNotificationsOff
            return
        }

        do {
            loadingState = .loading(showPlaceholders: isInitialLoad)
            notificationModels = try await network.fetchNotifications(tab: tabType)
                .map {
                    NotificationCenterRowViewModel(
                        notification: $0,
                        deeplinkScreenProvider: deeplinkScreenProvider,
                        network: network
                    )
                }

            if isInteractive {
                await markAllNotificationsRead()
            }

            loadingState = .loaded

            invalidateHasNonvisibleBadgedUnread()

            switch tabType {
            case .activity:
                emptyContentDisplayType = notificationModels.isEmpty ? .noActivity : nil
                let unreadCount =
                    notificationModels
                    .filter { $0.shouldShowUnreadNotificationState }
                    .count

                if unreadCount != application.applicationIconBadgeNumber {
                    application.applicationIconBadgeNumber = unreadCount
                    NotificationCenter.default.post(name: .AppIconBadgeNumberChanged, object: nil)
                }

            case .updates:
                emptyContentDisplayType = notificationModels.isEmpty ? .noUpdates : nil
            }
        } catch {
            loadingState = .failed
            emptyContentDisplayType = nil
        }
    }

    func markRead(notificationModels: [NotificationCenterRowViewModel]) async {
        let unreadNotifications =
            notificationModels
            .filter { !$0.isNotificationRead }
        let unreadBadgeableCount =
            unreadNotifications
            .filter { $0.shouldShowUnreadNotificationState }
            .count

        guard !unreadNotifications.isEmpty else { return }

        await unreadNotifications.concurrentForEach { await $0.markAsRead(true) }
        await decrementAppIconBadge(by: unreadBadgeableCount)

        do {
            try await network.markNotificationsReadState(
                ids: unreadNotifications.map { $0.id },
                isRead: true
            )
        } catch {
            /// Fall back to previous UI state if the call fails
            await unreadNotifications.concurrentForEach { await $0.markAsRead(false) }
            await decrementAppIconBadge(by: -unreadBadgeableCount)
        }
    }

    func markRead(notificationModel: NotificationCenterRowViewModel) async {
        await markRead(notificationModels: [notificationModel])
    }

    func markAllNotificationsRead() async {
        await markRead(notificationModels: notificationModels)
    }

    @MainActor private func decrementAppIconBadge(by value: Int) {
        if application.applicationIconBadgeNumber >= value {
            application.applicationIconBadgeNumber -= value
        } else {
            application.applicationIconBadgeNumber = 0
        }

        NotificationCenter.default.post(name: .AppIconBadgeNumberChanged, object: nil)
    }

    @MainActor private func resetAppIconBadge() {
        decrementAppIconBadge(by: application.applicationIconBadgeNumber)
    }

    private func invalidateHasNonvisibleBadgedUnread() {
        /// Introduce a slight delay to give all rows a chance to appear after initial load.
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self] in
            self?.hasNonvisibleBadgedUnread =
                self?.earliestUnreadBadgedNotification?.hasAppeared == false
        }
    }

    // MARK: - Analytics

    private func trackClick(notificationModel: NotificationCenterRowViewModel) async {

        let metablob: AnalyticsEvent.MetaBlob
        let objectIdentifier: String?

        if notificationModel.isCommentActivity {
            metablob = .init(
                isUnread: !notificationModel.isNotificationRead,
                notificationType: notificationModel.type == .commentReply ? "reply" : "likes",
                requiredValues: analyticsDefaults
            )
        } else {
            metablob = .init(
                isUnread: !notificationModel.isNotificationRead,
                requiredValues: analyticsDefaults
            )
        }

        switch notificationModel.navigationDestination {
        case .feed(.article(.detail(let articleId, let commentId))):
            objectIdentifier = notificationModel.isCommentActivity ? commentId : articleId
        case .liveBlog(let liveBlogId, _):
            objectIdentifier = liveBlogId
        case .feed(.discussion(let discussionDestination)):
            objectIdentifier =
                notificationModel.isCommentActivity
                ? discussionDestination.focusCommentId : discussionDestination.id
        case .listen(.podcast(let seriesId, _, let commentId)):
            objectIdentifier = notificationModel.isCommentActivity ? commentId : seriesId
        case .scores(.boxScore(let boxScoreDesination)):
            if case .comments(let commentId) = boxScoreDesination.initialSelectionOverride {
                objectIdentifier = commentId
            } else {
                objectIdentifier = boxScoreDesination.gameId
            }
        case .liveRoom(.room(let roomId)):
            objectIdentifier = roomId
        default:
            objectIdentifier = nil
        }

        await Analytics.track(
            event: .init(
                verb: .click,
                view: .notifications,
                element: tabType.analyticsElement,
                objectType: notificationModel.analyticsObjectType,
                objectIdentifier: objectIdentifier,
                metaBlob: metablob,
                requiredValues: analyticsDefaults
            ),
            manager: eventManager
        )
    }
}
