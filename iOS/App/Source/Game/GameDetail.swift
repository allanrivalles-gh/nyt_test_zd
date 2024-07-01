//
//  GameDetail.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 1/2/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import ActivityKit
import AthleticAnalytics
import AthleticApolloTypes
import AthleticComments
import AthleticFoundation
import AthleticNavigation
import AthleticUI
import Combine
import Foundation
import SwiftUI

struct GameDetail: View {
    enum PresentationStyle {
        case navigation
        case modal(dismissAction: VoidClosure)
    }

    private struct Constants {
        static let tvImage: UIImage? = #imageLiteral(resourceName: "tv_regular_m").imageFrom(
            color: .chalk.dark500
        )
    }

    @EnvironmentObject private var compass: Compass
    @State private var shouldShowGameShareButton = true
    @StateObject var viewModel: GameScreenViewModel
    var presentationStyle: PresentationStyle = .navigation

    private var isLiveActivitiesEnabled: Bool {
        compass.config.flags.isLiveActivitiesEnabled
    }

    var body: some View {
        VStack(spacing: 0) {
            if let pagingViewModel = viewModel.pagingViewModel {
                if let headerViewModel = viewModel.header {
                    GameLargeScoreHeader(viewModel: headerViewModel)
                }
                PagingContent(
                    viewModel: pagingViewModel,
                    onPullToRefresh: {
                        viewModel.loadData()
                    },
                    onSelectItem: {
                        viewModel.trackSelectEvent(for: $0)
                    }
                )
            } else {
                EmptyContent(state: viewModel.state)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            }

            TransparentNavigationBarStylingView(
                foregroundColor: UIColor(.chalk.dark800),
                shouldRestoreOnDisappear: false
            )
            .fixedSize()
        }
        .background(Color.chalk.dark200)
        .onAppear {
            viewModel.loadData()
        }
        .onForeground {
            viewModel.appWillForeground()
        }
        .onBackground {
            viewModel.appDidBackground()
        }
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                if case let .modal(dismissAction) = presentationStyle {
                    Button(action: { dismissAction() }) {
                        Image(systemName: "xmark")
                            .foregroundColor(.chalk.dark800)
                    }
                }
            }
            ToolbarItem(placement: .principal) {
                navigationTitle
            }
            ToolbarItem(placement: .navigationBarTrailing) {
                HStack {
                    if #available(iOS 16.2, *), isLiveActivitiesEnabled {
                        if !viewModel.hasLiveActivityStarted {
                            Button(
                                action: {
                                    Task {
                                        do {
                                            try await viewModel.startLiveActivity()
                                            /// show success toast
                                        } catch {
                                            /// show error toast
                                        }
                                    }
                                }
                            ) {
                                Image(systemName: "l.circle")
                                    .renderingMode(.template)
                                    .foregroundColor(.chalk.dark800)
                            }
                        } else {
                            Button(
                                action: {
                                    Task {
                                        await viewModel.endLiveActivity()
                                        /// show ended toast
                                    }
                                }
                            ) {
                                Image(systemName: "l.circle.fill")
                                    .renderingMode(.template)
                                    .foregroundColor(.chalk.dark800)
                            }
                        }
                    }

                    if shouldShowGameShareButton, let url = viewModel.shareUrl {
                        ShareLink(item: url) {
                            ShareIcon()
                        }
                        .onSimultaneousTapGesture {
                            viewModel.trackShareTapped()
                        }
                    }
                }
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .onPreferenceChange(ShareButtonProvidedKey.self) { shareButtonProvided in
            /// We don't want the view to show its own share button if some tab is already doing it.
            /// This way we avoid having two share buttons visible at the same time.
            shouldShowGameShareButton = !shareButtonProvided
        }
        .environment(\.gameLeagueString, viewModel.leagueString)
    }

    @ViewBuilder
    private var navigationTitle: some View {
        if let headerViewModel = viewModel.header {
            NavigationBarTitleText(headerViewModel.navigationTitle ?? "")
        }
    }
}

private struct PagingContent: View {
    @EnvironmentObject private var compass: Compass
    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var user: UserModel
    @EnvironmentObject private var navigationModel: NavigationModel
    @Environment(\.colorScheme) private var colorScheme
    @ObservedObject var viewModel: GamePagingViewModel
    @State private var commentingPlay: CommentingPlay?
    @State private var focusedComment: FocusedComment?
    @State private var commentDrawerDismissed = PassthroughSubject<Void, Never>()
    @StateObject private var navigationCoordinator = ContentWebViewNavigationCoordinator()
    let onPullToRefresh: VoidClosure
    let onSelectItem: (GameScreenMenuItem) -> Void

    private var canCommentOnPlay: Bool {
        compass.config.flags.isCommentOnPlayEnabled && entitlement.hasAccessToContent
    }

    var body: some View {
        if canCommentOnPlay, let discussTab = findDiscussTab() {
            let game = viewModel.game
            pagingView
                .commentingPlay($commentingPlay)
                .focusedComment($focusedComment)
                .environment(
                    \.boxScoreDiscussPlayContext,
                    .init(
                        analytics: .init(
                            gamePhase: game.phase,
                            gameId: game.id,
                            leagueId: game.leagueCode.rawValue
                        ),
                        discuss: { play in
                            viewModel.selectedTab = discussTab
                            commentingPlay = play
                        }
                    )
                )
                .environment(\.gameSelectTab, selectGameTab)
        } else {
            pagingView
                .environment(\.gameSelectTab, selectGameTab)
        }
    }

    @ViewBuilder
    private var pagingView: some View {
        if viewModel.tabs.count > 1 {
            PagingTabView(
                tabs: viewModel.tabs,
                selectedTab: $viewModel.selectedTab,
                onSelectTab: { _, newTab in
                    /// Going to different tab we dismiss keyboard
                    commentDrawerDismissed.send()
                    onSelectItem(newTab.item)
                }
            ) { tab in
                content(for: tab)
            }
            .pagingTabSizing(.equalSpacing)
        } else {
            content(for: viewModel.selectedTab)
        }
    }

    @ViewBuilder
    private func content(for tab: GamePagingViewModel.Tab) -> some View {
        Group {
            switch tab.item {
            case let .game(viewModel):
                BoxScoreGame(
                    viewModel: viewModel,
                    onPullToRefresh: onPullToRefresh,
                    onTapSeeAllPlays: {
                        guard let tab = self.viewModel.tabs.first(where: { $0.isPlayByPlay }) else {
                            return
                        }
                        self.viewModel.selectedTab = tab
                        viewModel.trackTappedSeeAllPlaysEvent()
                    },
                    showDiscussTab: {
                        if let discussTab = findDiscussTab() {
                            self.viewModel.selectedTab = discussTab
                        }
                    }
                )
            case let .liveBlog(_, permalinkForEmbed, _):
                InAppWebView(
                    viewModel: WebviewViewModel(
                        type: .webViewTest(permalinkForEmbed),
                        navigationModel: navigationModel,
                        navigatedExternalUrl: $navigationCoordinator.externalUrl,
                        isRefreshable: true,
                        isThemeAware: true,
                        adType: .matchLiveBlog
                    )
                )
                .safariView(url: $navigationCoordinator.externalUrl)

            case let .playerGrades(viewModel):
                GamePlayerGradesList(
                    viewModel: viewModel,
                    didPullToRefresh: onPullToRefresh
                )

            case let .stats(viewModel):
                GameStatsDetail(
                    viewModel: viewModel,
                    didPullToRefresh: onPullToRefresh
                )

            case let .playByPlay(viewModel), let .timeline(viewModel):
                PlayByPlayDetail(viewModel: viewModel)

            case let .comments(model: viewModel, isBadged: _):
                let game = self.viewModel.game
                CommentList(
                    viewModel: viewModel,
                    surface: .init(
                        .game(
                            id: game.id,
                            leagueCode: game.leagueCode,
                            view: AnalyticsEvent.View.discussView(for: game.phase)
                        ),
                        filterType: nil
                    ),
                    contrastingBackgroundInLightMode: false
                )
                .commentDrawerDismissed(commentDrawerDismissed.eraseToAnyPublisher())
                .commentingUser(user.current)
            }
        }
        .environment(\.boxScoreDiscussPlayOriginTab, tab)
    }

    private func selectGameTab(menuItem: GameScreenMenuItem) {
        guard let tab = findTab(menuItem) else {
            return
        }
        viewModel.selectedTab = tab
    }

    private func findDiscussTab() -> GamePagingViewModel.Tab? {
        viewModel.tabs.first { tab in
            if case .comments = tab.item {
                return true
            }
            return false
        }
    }

    private func findTab(_ menuItem: GameScreenMenuItem) -> GamePagingViewModel.Tab? {
        viewModel.tabs.first { tab in
            tab.item == menuItem
        }
    }
}

private struct ShareButtonProvidedKey: PreferenceKey {
    static var defaultValue: Bool = false

    static func reduce(value: inout Bool, nextValue: () -> Bool) {
        value = value || nextValue()
    }
}

extension GamePagingViewModel.Tab {
    fileprivate var isPlayByPlay: Bool {
        switch item {
        case .playByPlay, .timeline:
            return true
        default:
            return false
        }
    }
}

private struct GameSelectTabKey: EnvironmentKey {
    static var defaultValue: (GameScreenMenuItem) -> Void = { _ in }
}

private struct GameLeagueString: EnvironmentKey {
    static let defaultValue: String? = nil
}

extension EnvironmentValues {
    var gameSelectTab: (GameScreenMenuItem) -> Void {
        get { self[GameSelectTabKey.self] }
        set { self[GameSelectTabKey.self] = newValue }
    }

    var gameLeagueString: String? {
        get { self[GameLeagueString.self] }
        set { self[GameLeagueString.self] = newValue }
    }
}
