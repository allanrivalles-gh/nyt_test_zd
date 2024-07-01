//
//  SpotlightViewModel.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 5/21/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import Foundation

final class SpotlightViewModel: ObservableObject {

    // MARK: - Properties
    let id: String
    let articleId: String
    let title: String
    let excerpt: String
    let imageUrl: URL
    let permalink: String?
    let scheduledAt: Date
    var byline: String
    var authorImageUrls: [URL]
    @Published private(set) var commentCount: Int
    var disableComments: Bool
    var isSaved: Bool

    var impressionManager: AnalyticImpressionManager {
        analyticsConfiguration.impressionManager
    }

    private let article: GQL.SpotlightArticle
    private let analyticsConfiguration: FeedSectionAnalyticsConfiguration
    private let network: ArticlesNetworking
    private var hasSuccessfullyPrefetchedContent: Bool = false
    private lazy var logger = ATHLogger(category: .article)

    // MARK: - Initialization

    init?(
        id: String,
        scheduledAt: Timestamp,
        article: GQL.SpotlightArticle,
        analytics: FeedSectionAnalyticsConfiguration,
        network: ArticlesNetworking = AppEnvironment.shared.network
    ) {
        guard
            let imageUrlString = article.imageUri,
            let imageUrl = URL(string: imageUrlString)
        else { return nil }
        self.article = article
        self.id = id
        self.articleId = article.id
        self.title = article.title
        self.excerpt = article.excerpt
        self.imageUrl = imageUrl
        self.permalink = article.permalink
        self.scheduledAt = scheduledAt
        self.commentCount = article.commentCount
        self.disableComments = !article.shouldDisplayCommentInfo
        self.authorImageUrls = article.articleAuthors
            .sorted(by: { $0.displayOrder < $1.displayOrder })
            .compactMap { $0.author.asStaff?.avatarUri.flatMap { URL(string: $0) } }

        self.byline = article.author.name

        isSaved = article.isSaved

        ImageService.preheatImage(url: imageUrl)

        analyticsConfiguration = analytics
        self.network = network
    }

    convenience init?(
        spotlightConsumable: GQL.SpotlightConsumable,
        analytics: FeedSectionAnalyticsConfiguration,
        network: ArticlesNetworking = AppEnvironment.shared.network
    ) {
        guard let article = spotlightConsumable.article?.fragments.spotlightArticle else {
            return nil
        }
        self.init(
            id: spotlightConsumable.id,
            scheduledAt: spotlightConsumable.spotlightScheduledAt,
            article: article,
            analytics: analytics,
            network: network
        )
    }

    convenience init?(
        spotlight: GQL.Spotlight,
        analytics: FeedSectionAnalyticsConfiguration,
        network: ArticlesNetworking = AppEnvironment.shared.network
    ) {
        guard let article = spotlight.article?.fragments.spotlightArticle else { return nil }

        self.init(
            id: spotlight.id,
            scheduledAt: spotlight.spotlightScheduledAt,
            article: article,
            analytics: analytics,
            network: network
        )
    }

    func prefetchContentIfNeeded() async {
        await invalidateCommentCount()

        guard !hasSuccessfullyPrefetchedContent else { return }

        do {
            try await network.fetchArticle(id: articleId, usingCache: true)
            await invalidateCommentCount()
            hasSuccessfullyPrefetchedContent = true
        } catch {
            do {
                try await network.fetchArticle(id: articleId, usingCache: false)
                await invalidateCommentCount()
                hasSuccessfullyPrefetchedContent = true
            } catch let error {
                logger.error("Failed to fetch article ID \(articleId): \(error)")
            }
        }
    }

    @MainActor private func invalidateCommentCount() {
        guard
            let cachedCommentCount = UserDynamicData.commentCounts.count(
                forId: articleId,
                contentType: GQL.ContentType.post.rawValue
            )
        else {
            return
        }

        commentCount = cachedCommentCount
    }
}

// MARK: - Hashable
extension SpotlightViewModel: Hashable {
    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
        hasher.combine(title)
        hasher.combine(commentCount)
        hasher.combine(excerpt)
    }

    static func == (lhs: SpotlightViewModel, rhs: SpotlightViewModel) -> Bool {
        lhs.title == rhs.title
            && lhs.id == rhs.id
            && lhs.commentCount == rhs.commentCount
            && lhs.excerpt == rhs.excerpt
            && lhs.isSaved == rhs.isSaved
    }
}

// MARK: - Identifiable
extension SpotlightViewModel: Identifiable {}

// MARK: - Analytical
extension SpotlightViewModel: Analytical {
    var analyticData: AnalyticData {
        AnalyticData(
            config: analyticsConfiguration,
            objectIdentifier: article.id,
            eventTypes: [.click, .impress, .view]
        )
    }
}
