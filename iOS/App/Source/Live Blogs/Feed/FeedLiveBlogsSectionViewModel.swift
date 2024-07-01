//
//  FeedLiveBlogsSectionViewModel.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 6/16/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import Foundation
import SwiftUI

final class FeedLiveBlogsSectionViewModel: Identifiable, ObservableObject {
    typealias UpdatedLiveBlog = GQL.LiveBlogBannerInfoUpdatesSubscription.Data.UpdatedLiveBlog

    let id: Int
    var isActive: Bool = false

    @Published private(set) var items: [LiveBlogBannerItemViewModel]
    @Published private(set) var isReordering: Bool = false
    private let network: LiveUpdatesNetworking
    private var cancellables = Cancellables()

    init?(
        items: [LiveBlogBannerItemViewModel],
        network: LiveUpdatesNetworking = AppEnvironment.shared.network
    ) {
        guard !items.isEmpty else { return nil }

        self.id = items.sorted(by: { $0.id > $1.id }).reduce("") { $0 + $1.id }.hashValue
        self.items = items.sorted(by: { $0.lastActivityAt > $1.lastActivityAt })
        self.network = network

        startLiveUpdates(items: items)
    }

    convenience init?(
        liveBlogsConsumables: [GQL.LiveBlogConsumable],
        analytics: FeedSectionAnalyticsConfiguration,
        network: LiveUpdatesNetworking = AppEnvironment.shared.network
    ) {
        let items = liveBlogsConsumables.enumerated().map { index, liveBlog in
            LiveBlogBannerItemViewModel(
                liveBlogConsumable: liveBlog,
                analytics: FeedSectionAnalyticsConfiguration(from: analytics, index: index)
            )
        }

        self.init(items: items, network: network)
    }

    convenience init?(
        liveBlogs: [GQL.LiveBlogContent],
        analytics: FeedSectionAnalyticsConfiguration,
        network: LiveUpdatesNetworking = AppEnvironment.shared.network
    ) {
        let items = liveBlogs.enumerated().map { index, liveBlog in
            LiveBlogBannerItemViewModel(
                liveBlog: liveBlog,
                analytics: FeedSectionAnalyticsConfiguration(from: analytics, index: index)
            )
        }

        self.init(items: items, network: network)
    }

    private func startLiveUpdates(items: [LiveBlogBannerItemViewModel]) {
        items.forEach { item in
            network
                .startLiveUpdates(
                    subscription: GQL.LiveBlogBannerInfoUpdatesSubscription(id: item.id)
                )
                .receive(on: RunLoop.main)
                .sink { [weak self] data in
                    self?.handleUpdate(for: data.updatedLiveBlog)
                }
                .store(in: &cancellables)
        }
    }

    private func handleUpdate(for liveBlog: UpdatedLiveBlog) {
        guard let item = items.first(where: { $0.id == liveBlog.id }),
            item.title != liveBlog.title || item.lastActivityAt != liveBlog.lastActivityAt
        else {
            return
        }

        item.update(
            title: liveBlog.title,
            lastActivityAt: liveBlog.lastActivityAt,
            animated: isActive
        )

        /// only need to re-sort if the latest updated blog is not already at the front
        guard let itemIndex = items.firstIndex(of: item), itemIndex != 0 else { return }

        if isActive {
            FeedLiveBlogsSectionViewModel.animatedUpdateSequence(
                trigger: { [weak self] in
                    self?.isReordering.toggle()
                },
                update: { [weak self] in
                    self?.items.sort(by: { $0.lastActivityAt > $1.lastActivityAt })
                }
            )
        } else {
            items.sort(by: { $0.lastActivityAt > $1.lastActivityAt })
        }
    }

    static func animatedUpdateSequence(
        trigger: @escaping VoidClosure,
        update: @escaping VoidClosure
    ) {
        withAnimation(.easeInOut(duration: 0.3)) {
            trigger()
        }

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
            update()
        }

        withAnimation(.easeInOut(duration: 0.3).delay(0.3)) {
            trigger()
        }
    }
}

extension FeedSectionAnalyticsConfiguration {
    fileprivate init(from analytics: FeedSectionAnalyticsConfiguration, index: Int) {
        self.init(
            objectType: analytics.objectType,
            element: analytics.element,
            container: analytics.container,
            sourceView: analytics.sourceView,
            filter: analytics.filter,
            impressionManager: analytics.impressionManager,
            indexPath: IndexPath(item: index, section: analytics.indexPath.section),
            parentObjectType: analytics.parentObjectType,
            parentObjectId: analytics.parentObjectId,
            pageOrder: analytics.pageOrder,
            layoutDirection: analytics.layoutDirection
        )
    }
}
