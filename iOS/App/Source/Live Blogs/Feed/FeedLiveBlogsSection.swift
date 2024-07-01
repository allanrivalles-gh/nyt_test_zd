//
//  FeedLiveBlogsSection.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 6/16/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticNavigation
import AthleticUI
import SwiftUI

struct FeedLiveBlogsSection: View {
    @StateObject var viewModel: FeedLiveBlogsSectionViewModel
    let containerProxy: GeometryProxy

    var body: some View {
        EdgeGradientScrollView {
            HStack(spacing: 0) {
                if let item = viewModel.items.first {
                    NavigationLink(
                        screen: .liveBlog(id: item.id, gameId: item.gameId)
                    ) {
                        LiveIndicator(textColor: .chalk.red)
                            .padding(.leading, 16)
                    }
                    .onSimultaneousTapGesture {
                        /// Tapping the LIVE indicator should navigate to the first blog item
                        if let item = viewModel.items.first {
                            Analytics.track(event: item.analyticData.click)
                        }
                    }
                }

                ForEach(indexed: viewModel.items) { index, liveBlog in
                    if index > 0 {
                        DividerView(color: .chalk.dark400, axis: .vertical)
                            .frame(height: 12)
                    }

                    LiveBlogsBannerItem(viewModel: liveBlog)
                        .trackImpressions(
                            with: liveBlog.analytics.impressionManager,
                            record: liveBlog.analyticData.impress,
                            containerProxy: containerProxy
                        )
                }
                .opacity(viewModel.isReordering ? 0 : 1)
            }
        }
        .background(Color.chalk.dark100)
        .onAppear {
            viewModel.isActive = true
        }
        .onDisappear {
            viewModel.isActive = false
        }
    }
}

struct LiveBlogsBanner_Previews: PreviewProvider {
    private static let liveBlog1 = GQL.LiveBlogContent(
        id: "1",
        permalink: "https://theathletic.com/",
        permalinkForEmbed: "https://theathletic.com/",
        liveBlogStatus: "live",
        title: "Test Live Blog In Progress Now",
        shortTitle: "Test Live Blog",
        lastActivityAt: Date().add(minutes: -15),
        updatedAt: Date().add(minutes: -15),
        contentImages: [],
        user: .makeStaff(name: "Bob"),
        liveBlogTags: []
    )

    private static let liveBlog2 = GQL.LiveBlogContent(
        id: "2",
        permalink: "https://theathletic.com/",
        permalinkForEmbed: "https://theathletic.com/",
        liveBlogStatus: "live",
        title: "Another Test Live Blog In Progress Now",
        shortTitle: "Another Test Live Blog",
        lastActivityAt: Date().add(minutes: -25),
        updatedAt: Date().add(minutes: -25),
        contentImages: [],
        user: .makeStaff(name: "Bob"),
        liveBlogTags: []
    )

    private static let analytics = FeedSectionAnalyticsConfiguration(
        objectType: .liveBlogId,
        element: .liveBlogs,
        container: .liveBlogs,
        sourceView: .home,
        feedFilter: GQL.FeedRequest(page: 0),
        impressionManager: AnalyticsManagers.feedImpressions,
        indexPath: IndexPath(item: -1, section: 0),
        pageOrder: 0
    )

    private static let viewModel = FeedLiveBlogsSectionViewModel(
        liveBlogs: [liveBlog1, liveBlog2],
        analytics: analytics
    )!

    static var previews: some View {
        GeometryReader { proxy in
            FeedLiveBlogsSection(
                viewModel: viewModel,
                containerProxy: proxy
            )
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.light)

            FeedLiveBlogsSection(
                viewModel: viewModel,
                containerProxy: proxy
            )
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.dark)
        }
    }
}
