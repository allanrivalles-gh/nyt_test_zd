//
//  NotificationCenterLanding.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 6/29/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloNetworking
import AthleticFoundation
import AthleticNavigation
import AthleticUI
import SwiftUI

public typealias DeeplinkScreenProvider = (URL?) -> AthleticScreen?

public struct NotificationCenterLanding: View {
    public enum TabType: Hashable, Identifiable {
        case activity(areCommentNotificationsEnabled: Bool)
        case updates

        public var title: String {
            switch self {
            case .activity:
                return Strings.activity.localized
            case .updates:
                return Strings.updates.localized
            }
        }

        public var id: String {
            switch self {
            case .activity(let areCommentNotificationsEnabled):
                return "activity-\(areCommentNotificationsEnabled)"
            case .updates:
                return "updates"
            }
        }

        var analyticsElement: AnalyticsEvent.Element {
            switch self {
            case .activity:
                return .activity
            case .updates:
                return .updates
            }
        }

        var analyticsObjectType: AnalyticsEvent.ObjectType {
            switch self {
            case .activity:
                return .activity
            case .updates:
                return .updates
            }
        }
    }

    private final class Tab: PagingTab {
        let type: TabType
        var id: String { type.id }
        var title: String { type.title }

        init(type: TabType) {
            self.type = type
        }

        static func == (
            lhs: NotificationCenterLanding.Tab,
            rhs: NotificationCenterLanding.Tab
        ) -> Bool {
            lhs.type == rhs.type
        }

        func hash(into hasher: inout Hasher) {
            hasher.combine(type)
        }
    }

    let analyticsDefaults: AnalyticsRequiredValues
    @State private var selectedTab: Tab
    @StateObject private var viewModel: NotificationCenterLandingViewModel
    private let deeplinkScreenProvider: DeeplinkScreenProvider
    private let hasAccessToContent: Bool
    @Binding private var isShowingSubscriptionPlans: Bool
    private let areCommentNotificationsEnabled: Bool
    private let network: NotificationCenterNetworking

    private let tabs: [Tab]

    @Environment(\.scenePhase) private var scenePhase
    @EnvironmentObject private var navigationModel: NavigationModel

    private var activityTab: Tab? {
        tabs.first(where: { $0.type.id.contains("activity") })
    }

    private var updatesTab: Tab? {
        tabs.first(where: { $0.type.id == "updates" })
    }

    public init(
        selectedTab type: TabType,
        deeplinkScreenProvider: @escaping DeeplinkScreenProvider,
        hasAccessToContent: Bool,
        areCommentNotificationsEnabled: Bool,
        isShowingSubscriptionPlans: Binding<Bool>,
        analyticsDefaults: AnalyticsRequiredValues,
        network: NotificationCenterNetworking
    ) {
        tabs = [
            Tab(type: .activity(areCommentNotificationsEnabled: areCommentNotificationsEnabled)),
            Tab(type: .updates),
        ]

        _selectedTab = State(initialValue: .init(type: type))
        self.deeplinkScreenProvider = deeplinkScreenProvider
        self.hasAccessToContent = hasAccessToContent
        _isShowingSubscriptionPlans = isShowingSubscriptionPlans
        self.analyticsDefaults = analyticsDefaults
        self.areCommentNotificationsEnabled = areCommentNotificationsEnabled
        self.network = network

        _viewModel = StateObject(wrappedValue: .init(network: network))
    }

    public var body: some View {
        PagingTabView(
            tabs: tabs,
            selectedTab: $selectedTab,
            type: .fixed,
            onSelectTab: { currentTab, newTab in
                guard currentTab != newTab else { return }

                Analytics.track(
                    event: .init(
                        verb: .click,
                        view: .notifications,
                        element: currentTab.type.analyticsElement,
                        objectType: newTab.type.analyticsObjectType,
                        requiredValues: analyticsDefaults
                    )
                )
            }
        ) { tab in
            TabContent(
                type: tab.type,
                network: network,
                navigationModel: navigationModel,
                deeplinkScreenProvider: deeplinkScreenProvider,
                analyticsDefaults: analyticsDefaults
            )
            .onAppear {
                Analytics.track(
                    event: .init(
                        verb: .view,
                        view: .notifications,
                        element: tab.type.analyticsElement,
                        requiredValues: analyticsDefaults
                    )
                )
            }
        }
        .overlay(
            Group {
                /// User is subscribed
                if hasAccessToContent {
                    /// User has previously denied push notifications
                    if viewModel.haveNotificationsBeenDenied {
                        NoNotificationsContent(
                            type: .allNotificationsOff,
                            isShowingSubscriptionPlans: .constant(false),
                            analyticsDefaults: analyticsDefaults
                        )
                    } else {
                        if viewModel.loadingState == .loaded {
                            /// Proceed to Notification Center
                            EmptyView()
                        } else {
                            ProgressView()
                                .progressViewStyle(.athletic)
                                .frame(maxWidth: .infinity, maxHeight: .infinity)
                                .background(Color.chalk.dark200)
                        }
                    }
                } else {
                    /// User is not subscribed
                    NoNotificationsContent(
                        type: .notSubscribed,
                        isShowingSubscriptionPlans: $isShowingSubscriptionPlans,
                        analyticsDefaults: analyticsDefaults
                    )
                }
            }
        )
        .task {
            await viewModel.onAppear()
        }
        .onChange(of: scenePhase) { newPhase in
            switch newPhase {
            case .active:
                /// Check permissions if user is coming back from Settings
                Task {
                    await viewModel.checkNotificationsPermissions()
                }
            default:
                break
            }
        }
        .onReceive(viewModel.$notificationCounts) { counts in
            guard
                let counts, !areCommentNotificationsEnabled || counts.activityUnread == 0
            else {
                return
            }

            if counts.updatesTotal > 0, let updatesTab {
                selectedTab = updatesTab
            }
        }
        .background(Color.chalk.dark200)
        .navigationTitle(Strings.notifications.localized)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                NavigationLink(screen: .account(.notificationSettings(nil))) {
                    Image("icon_profile_gear")
                }
            }
        }
    }
}
