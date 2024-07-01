//
//  FeedV2List.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 4/20/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticNavigation
import AthleticUI
import OrderedCollections
import SwiftUI

struct FeedV2List: View {
    @StateObject var viewModel: FeedV2ViewModel
    var onScrollOffsetChanged: ((CGFloat) -> Void)?

    var body: some View {
        FeedV2ListView(viewModel: viewModel, onScrollOffsetChanged: onScrollOffsetChanged)
            /// This is done to handle url when entering from background
            /// Reason being is onOpenURL in TheAthleticApp takes to long to proccess and causes appWillForeground() to always send is_phantom as false
            .onOpenURL { _ in
                /// This is done specifically for sending analytics when entering from the background with an url
                viewModel.hasDeeplinkFromBackground = true
            }
    }
}

struct FeedV2ListView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var user: UserModel
    @EnvironmentObject private var navigationModel: NavigationModel

    @ObservedObject var viewModel: FeedV2ViewModel
    var onScrollOffsetChanged: ((CGFloat) -> Void)?

    @State private var isScrollToTopActive: Bool = false

    private let scrollViewNamespace = "feed-list-scrollview"

    var body: some View {
        GeometryReader { geometryProxy in
            ScrollViewReader { scrollProxy in
                ZStack(alignment: .bottom) {
                    RefreshableScrollView {
                        await viewModel.reloadContent(isInteractive: true)
                    } content: {
                        FeedV2ListContent(
                            viewModel: viewModel,
                            geometryProxy: geometryProxy,
                            scrollToTop: { isScrollToTopActive = true }
                        )
                        .onChange(of: isScrollToTopActive) { active in
                            guard let firstSection = viewModel.sections.first, active else {
                                isScrollToTopActive = false
                                return
                            }

                            withAnimation {
                                scrollProxy.scrollTo(firstSection.id, anchor: .top)
                            }

                            isScrollToTopActive = false
                        }
                        .onAppTabReselect { isVisible in
                            if isVisible {
                                isScrollToTopActive = true
                            }
                        }
                        .getVerticalOffset(in: .named(scrollViewNamespace)) { offset in
                            onScrollOffsetChanged?(offset)
                        }
                    }
                    .overlay(
                        ProgressView()
                            .progressViewStyle(.athletic)
                            .opacity(shouldShowProgress ? 1 : 0)
                            .padding(.bottom, 44)
                    )
                    .overlay(
                        EmptyContent(state: viewModel.state) {
                            Task {
                                await viewModel.reloadContent(isInteractive: true)
                            }
                        }
                        .opacity(viewModel.state == .failed ? 1 : 0)
                    )
                    .coordinateSpace(name: scrollViewNamespace)
                }
            }
            .background(Color.chalk.dark100)
            .navigationBarDefaultBackgroundColor()
            .outdatableContent()
        }
    }

    private var shouldShowProgress: Bool {
        /// We don't want to show the spinner if pulled to refresh or paging
        if case .loading(isInteractive: false, _) = viewModel.state {
            return true
        }
        return false
    }
}

struct FeedV2ListContent: View {
    @EnvironmentObject private var deeplinkModel: DeeplinkModel
    private let agoraManager = AgoraManager.shared
    @ObservedObject var viewModel: FeedV2ViewModel

    @Preference(\.hideScoresInFeed) private var hideScores

    let geometryProxy: GeometryProxy
    let scrollToTop: VoidClosure?

    var body: some View {
        LazyVStack(spacing: 0) {
            ForEach(indexed: viewModel.sections) { index, section in
                content(
                    forSection: section,
                    atIndex: index,
                    containerProxy: geometryProxy
                )
                .task {
                    /// Content load for next page when necessary
                    await viewModel.loadMoreContentIfNeeded(sectionIndex: index)
                }
            }
        }
        .background(Color.chalk.dark200)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            viewModel.isActive = true
        }
        .onDisappear {
            viewModel.isActive = false
        }
        .getSize { size in
            viewModel.updateAdWidth(width: size.width)
        }
    }

    @ViewBuilder
    private func content(
        forSection section: AnyIdentifiable,
        atIndex index: Int,
        containerProxy: GeometryProxy
    ) -> some View {
        Group {
            switch section.base {
            case let model as AnnouncementViewModel:
                FeedAnnouncementSection(
                    controller: viewModel.announcements,
                    viewModel: model,
                    containerProxy: containerProxy
                )
            case let section as FeedScoresSectionViewModel:
                if !hideScores {
                    FeedScoresSection(
                        viewModel: section,
                        containerProxy: containerProxy,
                        isBottomPadded: !shouldHideBottomDivider(at: index)
                    )
                }
            case let section as FeedLiveBlogsSectionViewModel:
                FeedLiveBlogsSection(
                    viewModel: section,
                    containerProxy: containerProxy
                )
            case let section as FeedGroupedHeroHeadlinesSectionViewModel:
                FeedGroupedHeroHeadlinesSection(
                    viewModel: section,
                    containerProxy: containerProxy
                )
            case let section as FeedGroupedHeroArticlesSectionViewModel:
                FeedGroupedHeroArticlesSection(
                    viewModel: section,
                    containerProxy: containerProxy
                )
            case let section as FeedHeroSectionViewModel:
                FeedHeroSection(
                    viewModel: section,
                    containerProxy: containerProxy,
                    isBottomPadded: !shouldHideBottomDivider(at: index)
                )
                .debugType(section.type.rawValue)
            case let section as FeedRankedSectionViewModel:
                Group {
                    switch section.layout {
                    case .topper(let topperSectionViewModel):
                        FeedRankedTopperSection(
                            viewModel: topperSectionViewModel,
                            containerProxy: containerProxy
                        )
                    case .mid(let midSectionViewModel):
                        FeedRankedMidSection(
                            viewModel: midSectionViewModel,
                            containerProxy: containerProxy
                        )
                    }
                }
                .debugType(section.type.rawValue)
            case let section as FeedArticleMultipleSectionViewModel:
                FeedArticleMultipleSection(
                    viewModel: section,
                    containerProxy: containerProxy,
                    isBottomPadded: !shouldHideBottomDivider(at: index)
                )
                .debugType(section.type.rawValue)
            case let section as FeedArticleSingleSectionViewModel:
                FeedArticleSingleSection(
                    viewModel: section,
                    containerProxy: containerProxy,
                    isBottomPadded: !shouldHideBottomDivider(at: index)
                )
                .debugType(section.type.rawValue)
            case let section as FeedHeadlinesSectionViewModel:
                FeedHeadlinesSection(
                    viewModel: section,
                    containerProxy: containerProxy,
                    isBottomPadded: !shouldHideBottomDivider(at: index)
                )
                .debugType(section.type.rawValue)
            case let section as FeedA1SectionViewModel:
                FeedA1Section(
                    viewModel: section,
                    containerProxy: containerProxy,
                    isBottomPadded: !shouldHideBottomDivider(at: index)
                )
                .debugType(section.type.rawValue)
            case let section as FeedMostPopularSectionViewModel:
                FeedMostPopularSection(
                    viewModel: section,
                    containerProxy: containerProxy,
                    isBottomPadded: !shouldHideBottomDivider(at: index)
                )
                .debugType(section.type.rawValue)
            case let section as FeedPodcastEpisodesSectionViewModel:
                FeedPodcastEpisodesSection(
                    viewModel: section,
                    containerProxy: containerProxy,
                    isBottomPadded: !shouldHideBottomDivider(at: index)
                )
                .debugType(section.type.rawValue)
            case let section as FeedRecommendedPodcastSectionViewModel:
                FeedRecommendedPodcastsSection(
                    viewModel: section,
                    containerProxy: containerProxy,
                    isBottomPadded: !shouldHideBottomDivider(at: index)
                )
                .debugType(section.type.rawValue)
            case let section as EndOfFeedListSectionViewModel:
                EndOfFeedListSection(viewModel: section, buttonAction: { scrollToTop?() })

            case let section as LiveRoomFeedViewModel:
                if !agoraManager.isInRoom {
                    FeedLiveRoomSection(
                        viewModel: section,
                        containerProxy: containerProxy,
                        isBottomPadded: !shouldHideBottomDivider(at: index)
                    )
                } else {
                    EmptyView()
                }
            case let section as FeedAdvertisementViewModel:
                AdvertisementSection(viewModel: section)
            case let section as MoreStoriesSectionViewModel:
                MoreStoriesSection(
                    viewModel: section,
                    containerProxy: containerProxy,
                    isBottomPadded: !shouldHideBottomDivider(at: index)
                )
                .debugType(
                    section.type.rawValue
                )
            case let section as FeedGameSectionViewModel:
                if !hideScores {
                    FeedGameSection(
                        viewModel: section,
                        containerProxy: containerProxy,
                        isBottomPadded: !shouldHideBottomDivider(at: index)
                    )
                    .debugType(section.type.rawValue)
                }
            default:
                EmptyView()
            }
        }
        .contentShape(Rectangle())
    }

    private func shouldHideBottomDivider(at index: Int) -> Bool {
        guard let nextSection = viewModel.sections[safe: index + 1] else { return false }
        return nextSection.base is FeedLiveBlogsSectionViewModel
            || nextSection.base is FeedAdvertisementViewModel
    }
}

// MARK: - Previews
struct FeedV2List_Previews: PreviewProvider {
    static var previews: some View {
        FeedV2List(
            viewModel: FeedV2ViewModel(id: "preview", navigationModel: NavigationModel())
        )
    }
}
