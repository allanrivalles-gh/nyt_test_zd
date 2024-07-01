//
//  LiveBlogBannerItemViewModel.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 6/16/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import Combine
import Foundation
import SwiftUI

final class LiveBlogBannerItemViewModel: Analytical, Identifiable, ObservableObject {

    @Published private(set) var title: String
    @Published private(set) var lastActivityDisplay: String?
    @Published private(set) var isUpdatingLastActivity: Bool = false

    let id: String
    let gameId: String?
    let analytics: FeedSectionAnalyticsConfiguration
    let permalink: String
    let permalinkForEmbed: String

    private var timerCancellable: Cancellable?

    private(set) var lastActivityAt: Date {
        didSet {
            invalidateLastActivity()
        }
    }

    var impressionManager: AnalyticImpressionManager {
        analytics.impressionManager
    }

    var analyticData: AnalyticData {
        AnalyticData(
            config: analytics,
            objectIdentifier: id,
            eventTypes: [.click, .impress],
            indexHOverride: analytics.indexPath.item
        )
    }

    init(
        id: String,
        gameId: String?,
        title: String,
        lastActivityAt: Date,
        analytics: FeedSectionAnalyticsConfiguration,
        permalink: String,
        permalinkForEmbed: String
    ) {
        self.id = id
        self.gameId = gameId
        self.title = title
        self.lastActivityAt = lastActivityAt
        self.analytics = analytics
        self.permalink = permalink
        self.permalinkForEmbed = permalinkForEmbed

        invalidateLastActivity()
        setLastActivityTimer()
    }

    convenience init(
        liveBlogConsumable: GQL.LiveBlogConsumable,
        analytics: FeedSectionAnalyticsConfiguration
    ) {
        self.init(
            id: liveBlogConsumable.consumableId,
            gameId: liveBlogConsumable.findGameId(),
            title: liveBlogConsumable.shortTitle,
            lastActivityAt: liveBlogConsumable.lastActivityAt,
            analytics: analytics,
            permalink: liveBlogConsumable.permalink,
            permalinkForEmbed: liveBlogConsumable.permalinkForEmbed
        )
    }

    convenience init(liveBlog: GQL.LiveBlogContent, analytics: FeedSectionAnalyticsConfiguration) {
        self.init(
            id: liveBlog.id,
            gameId: liveBlog.findGameId(),
            title: liveBlog.shortTitle,
            lastActivityAt: liveBlog.lastActivityAt,
            analytics: analytics,
            permalink: liveBlog.permalink,
            permalinkForEmbed: liveBlog.permalinkForEmbed
        )
    }

    func update(title: String, lastActivityAt: Date, animated: Bool) {
        self.title = title

        if animated {
            FeedLiveBlogsSectionViewModel.animatedUpdateSequence(
                trigger: { [weak self] in
                    self?.isUpdatingLastActivity.toggle()
                },
                update: { [weak self] in
                    self?.lastActivityAt = lastActivityAt
                }
            )
        } else {
            self.lastActivityAt = lastActivityAt
        }

        setLastActivityTimer()
    }

    // MARK: - Helpers

    private func invalidateLastActivity() {
        /// if last update was > 30 mins ago, don't provide an activity timestamp
        guard let minutesAgo = lastActivityAt.timeAgo(component: .minute), minutesAgo <= 30
        else {
            lastActivityDisplay = nil
            timerCancellable = nil
            return
        }

        lastActivityDisplay =
            minutesAgo > 0
            ? String(format: Strings.minFormat.localized, minutesAgo)
            : Strings.justNow.localized
    }

    private func setLastActivityTimer() {
        timerCancellable = Timer.publish(every: 10, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                self?.invalidateLastActivity()
            }
    }
}

// MARK: - Equatable
extension LiveBlogBannerItemViewModel: Equatable {
    static func == (lhs: LiveBlogBannerItemViewModel, rhs: LiveBlogBannerItemViewModel) -> Bool {
        lhs.id == rhs.id && lhs.title == rhs.title && lhs.lastActivityAt == rhs.lastActivityAt
    }
}
