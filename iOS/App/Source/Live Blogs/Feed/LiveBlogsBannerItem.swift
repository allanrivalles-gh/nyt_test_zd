//
//  LiveBlogsBannerItem.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 6/16/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticNavigation
import SwiftUI

struct LiveBlogsBannerItem: View {
    @StateObject var viewModel: LiveBlogBannerItemViewModel

    var body: some View {
        NavigationLink(
            screen: .liveBlog(id: viewModel.id, gameId: viewModel.gameId)
        ) {
            HStack(spacing: 8) {
                Text(viewModel.title)
                    .fontStyle(.calibreUtility.s.medium)
                    .foregroundColor(.chalk.dark700)

                if let lastActivityText = viewModel.lastActivityDisplay {
                    Text(lastActivityText)
                        .fontStyle(.calibreUtility.xs.regular)
                        .foregroundColor(.chalk.dark500)
                        .opacity(viewModel.isUpdatingLastActivity ? 0 : 1)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
        }
        .trackClick(viewModel: viewModel)
    }
}

struct LiveBlogsBannerItem_Previews: PreviewProvider {
    private static let liveBlog = GQL.LiveBlogContent(
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

    static var previews: some View {
        LiveBlogsBannerItem(viewModel: .init(liveBlog: liveBlog, analytics: analytics))
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.light)

        LiveBlogsBannerItem(viewModel: .init(liveBlog: liveBlog, analytics: analytics))
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.dark)
    }
}
