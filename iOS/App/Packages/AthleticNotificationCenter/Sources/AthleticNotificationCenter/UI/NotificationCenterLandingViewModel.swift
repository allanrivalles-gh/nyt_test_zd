//
//  NotificationCenterLandingViewModel.swift
//
//
//  Created by Jason Leyrer on 9/6/23.
//

import AthleticApolloTypes
import AthleticFoundation
import Foundation
import UserNotifications

protocol UserNotificationCenterProtocol {
    func notificationSettings() async -> UNNotificationSettings
}

@MainActor
final class NotificationCenterLandingViewModel: ObservableObject {

    struct NotificationCounts {
        let activityTotal: Int
        let activityUnread: Int
        let updatesTotal: Int
    }

    @Published private(set) var loadingState: LoadingState = .initial
    @Published private(set) var notificationCounts: NotificationCounts? = nil
    @Published private(set) var haveNotificationsBeenDenied: Bool = false

    private let network: NotificationCenterNetworking
    private let userNotificationCenter: UserNotificationCenterProtocol

    init(
        network: NotificationCenterNetworking,
        userNotificationCenter: UserNotificationCenterProtocol = UNUserNotificationCenter.current()
    ) {
        self.network = network
        self.userNotificationCenter = userNotificationCenter
    }

    func onAppear() async {
        await checkNotificationsPermissions()
        await fetchNotificationCounts()
    }

    func checkNotificationsPermissions() async {
        haveNotificationsBeenDenied =
            await userNotificationCenter.notificationSettings().authorizationStatus
            == .denied
    }

    func fetchNotificationCounts() async {
        guard loadingState == .initial else { return }

        let result = try? await network.fetchNotificationCounts()

        if let result {
            notificationCounts = NotificationCounts(
                activityTotal: result.total.activity,
                activityUnread: result.unread.activity,
                updatesTotal: result.total.updates
            )
        }

        /// Proceed regardless of what was returned. This is for the purposes of tab
        /// pre-selection, which is a nice-to-have for UX purposes, but shouldn't be blocking

        loadingState = .loaded
    }
}

extension UNUserNotificationCenter: UserNotificationCenterProtocol {}
