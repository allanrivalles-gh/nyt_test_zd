//
//  PodcastSeriesListView.swift
//  theathletic-ios
//
//  Created by Charles Huang on 1/31/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticUI
import SwiftUI

struct PodcastSeriesListView: View {

    private struct Constants {
        static let scroll: String = "scroll"
        static let navigationTitleOpacityThreshold: CGFloat = 300
    }

    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var store: Store
    @EnvironmentObject private var compass: Compass

    @StateObject var viewModel: PodcastSeriesListViewModel
    @State private var isPaywallPresented = false
    @State private var navigationTitleOpacity: CGFloat = 0.0

    var body: some View {
        RefreshableScrollView {
            await viewModel.fetchData()
        } content: {
            LazyVStack(spacing: 0) {
                if viewModel.podcast != nil {
                    PodcastSeriesHeaderView(viewModel: viewModel)
                        .frame(height: viewModel.headerHeight)
                        .padding(.bottom, 16)
                }

                if !viewModel.episodes.isEmpty {
                    let _ = DuplicateIDLogger.logDuplicates(in: viewModel.episodes, id: \.episodeId)
                    ForEach(viewModel.episodes, id: \.episodeId) { model in
                        VStack(spacing: 0) {
                            PodcastSeriesListItemView(
                                model: model,
                                isPaywallPresented: $isPaywallPresented
                            )
                            .padding(.horizontal, 8)

                            DividerView(style: .extraLarge, color: .chalk.dark100)
                        }
                    }
                }
            }
            .background(
                GeometryReader {
                    Color.clear.preference(
                        key: ViewOffsetKey.self,
                        value: -$0.frame(
                            in: .named(Constants.scroll)
                        ).origin.y
                    )
                }
            )
            .onPreferenceChange(ViewOffsetKey.self) { offset in
                if offset > Constants.navigationTitleOpacityThreshold {
                    withAnimation(Animation.easeIn(duration: 0.3)) {
                        navigationTitleOpacity = 1.0
                    }
                } else {
                    withAnimation(Animation.easeIn(duration: 0.3)) {
                        navigationTitleOpacity = 0.0
                    }
                }
            }
        }
        .coordinateSpace(name: Constants.scroll)
        .background(Color.chalk.dark100)
        .overlay(
            EmptyContent(
                state: viewModel.loadingState,
                showProgressViewOnLoading:
                    viewModel.loadingState == .loading(showPlaceholders: true)
            ) {
                Task {
                    await viewModel.fetchData(isInitialLoad: true)
                }
            }
        )
        .toolbar {
            ToolbarItem(placement: .principal) {
                NavigationBarTitleText(viewModel.podcast?.title ?? "")
                    .opacity(navigationTitleOpacity)
            }
            ToolbarItem(placement: .navigationBarTrailing) {
                if let shareItem = viewModel.podcast?.shareItem {
                    ShareLink(item: shareItem.url, message: shareItem.titleText) {
                        ShareIcon()
                    }
                }
            }
        }
        .navigationBarDefaultBackgroundColor()
        .navigationBarTitleDisplayMode(.inline)
        .deeplinkListeningSheet(isPresented: $isPaywallPresented) {
            SubscriptionsNavigationView(
                viewModel: SubscriptionsNavigationViewModel(
                    store: store,
                    entitlement: entitlement,
                    source: .podcasts(podcastId: viewModel.podcastId)
                )
            )
        }
        .onAppear {
            viewModel.listDidAppear()

            Analytics.track(
                event: .init(
                    verb: .view,
                    view: .podcastsPage,
                    element: viewModel.fromElement,
                    objectType: .podcastId,
                    objectIdentifier: viewModel.podcastId
                )
            )
        }
    }
}

private struct ViewOffsetKey: PreferenceKey {
    typealias Value = CGFloat
    static var defaultValue = CGFloat.zero
    static func reduce(
        value: inout Value,
        nextValue: () -> Value
    ) {
        value += nextValue()
    }
}
