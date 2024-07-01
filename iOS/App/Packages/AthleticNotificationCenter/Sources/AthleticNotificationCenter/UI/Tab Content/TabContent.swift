//
//  TabContent.swift
//
//
//  Created by Jason Leyrer on 7/11/23.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticNavigation
import AthleticUI
import Foundation
import SwiftUI

struct TabContent: View {

    @StateObject private var viewModel: TabContentViewModel
    @State private var shouldShowMoreUnreadButton: Bool = false
    private let analyticsDefaults: AnalyticsRequiredValues

    init(
        type: NotificationCenterLanding.TabType,
        network: NotificationCenterNetworking,
        navigationModel: NavigationModel,
        deeplinkScreenProvider: @escaping DeeplinkScreenProvider,
        analyticsDefaults: AnalyticsRequiredValues
    ) {
        _viewModel = StateObject(
            wrappedValue: TabContentViewModel(
                type: type,
                network: network,
                navigationModel: navigationModel,
                deeplinkScreenProvider: deeplinkScreenProvider,
                analyticsDefaults: analyticsDefaults
            )
        )

        self.analyticsDefaults = analyticsDefaults
    }

    var body: some View {
        GeometryReader { proxy in
            RefreshableScrollView(refreshAction: {
                await viewModel.fetchNotifications(isInteractive: true)
            }) {
                ScrollViewReader { scrollProxy in
                    if !viewModel.notificationModels.isEmpty {
                        LazyVStack(spacing: 0) {
                            let _ = DuplicateIDLogger.logDuplicates(
                                in: viewModel.notificationModels
                            )
                            ForEach(viewModel.notificationModels) { model in
                                NotificationCenterRow(viewModel: model)
                                    .onSimultaneousTapGesture {
                                        await viewModel.notificationTapped(notificationModel: model)
                                    }
                                    .onAppear {
                                        viewModel.notificationAppeared(notificationModel: model)
                                    }
                                    .notificationContextMenu(model: model)
                            }
                        }
                        .onReceive(viewModel.$focusedItemId) { newValue in
                            guard let newValue else { return }

                            withAnimation {
                                scrollProxy.scrollTo(newValue, anchor: .bottom)
                            }
                        }
                    } else if let displayType = viewModel.emptyContentDisplayType,
                        [.noActivity, .noUpdates].contains(displayType)
                    {
                        /// Allow pull to refresh if notifications can show but are empty
                        VStack(spacing: 0) {
                            Spacer(minLength: 0)

                            NoNotificationsContent(
                                type: displayType,
                                isShowingSubscriptionPlans: .constant(false),
                                analyticsDefaults: analyticsDefaults
                            )

                            Spacer(minLength: 0)
                        }
                        .frame(minHeight: proxy.size.height)
                    }
                }
            }
            .onReceive(viewModel.$hasNonvisibleBadgedUnread) { newValue in
                guard shouldShowMoreUnreadButton != newValue else { return }

                withAnimation(.easeInOut) {
                    shouldShowMoreUnreadButton = newValue
                }
            }
            .overlay(alignment: .bottom) {
                if shouldShowMoreUnreadButton {
                    MoreUnreadButton {
                        viewModel.didTapMoreUnread()
                    }
                    .padding(.bottom, 24)
                    .transition(.opacity)
                }
            }
            .overlay(
                EmptyContent(
                    state: viewModel.loadingState,
                    backgroundColor: .chalk.dark200,
                    errorMessage: Strings.genericError.localized,
                    showProgressViewOnLoading: viewModel.loadingState
                        == .loading(showPlaceholders: true)
                ) {
                    Task {
                        await viewModel.fetchNotifications(isInitialLoad: true)
                    }
                }
            )
            .overlay(
                /// Disallow pull to refresh if notifications cannot be shown for this tab
                Group {
                    if let displayType = viewModel.emptyContentDisplayType,
                        [.allNotificationsOff, .commentNotificationsOff, .notSubscribed].contains(
                            displayType
                        )
                    {
                        NoNotificationsContent(
                            type: displayType,
                            /// We don't navigate to subscripton plans from within a tab, only
                            /// at the landing level.
                            isShowingSubscriptionPlans: .constant(false),
                            analyticsDefaults: analyticsDefaults
                        )
                    } else {
                        EmptyView()
                    }
                }
            )
            .onAppear {
                Task {
                    await viewModel.onAppear()
                }
            }
            .onDisappear {
                viewModel.onDisappear()
            }
        }
    }
}
