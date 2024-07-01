//
//  LiveBlogContainerViewModel.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 5/4/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation

final class LiveBlogContainerViewModel: ObservableObject {

    let liveBlogId: String
    let postId: String?
    let gameId: String?
    @Published private(set) var title: String?
    @Published private(set) var permalink: String?
    @Published private(set) var permalinkForEmbed: String?
    private let network: LiveBlogNetworking

    private lazy var logger = ATHLogger(category: .liveBlog)

    init(
        liveBlogId: String,
        postId: String?,
        permalink: String?,
        permalinkForEmbed: String?,
        gameId: String?,
        network: LiveBlogNetworking = AppEnvironment.shared.network
    ) {
        self.liveBlogId = liveBlogId
        self.postId = postId
        self.permalink = permalink
        self.permalinkForEmbed = permalinkForEmbed
        self.gameId = gameId
        self.network = network
    }

    /// Fetches a Live Blog's permalink and permalinkForEmbed, if necessary. If we're navigating
    /// to a web-based Live Blog via deeplink, these permalinks won't be known ahead of time

    @MainActor
    func fetchPermalinksIfNeeded() async {
        guard
            permalink == nil || permalinkForEmbed == nil,
            let links = try? await network.fetchLiveBlogLinks(id: liveBlogId)
        else {
            return
        }

        let permalink = links.permalink
        var permalinkForEmbed = links.permalinkForEmbed

        if let postId, var embedUrl = URL(string: permalinkForEmbed) {
            embedUrl.appendPathComponent(postId)
            permalinkForEmbed = embedUrl.absoluteString
        }

        self.permalink = permalink
        self.permalinkForEmbed = permalinkForEmbed
    }

    @MainActor
    private func fetchLiveBlogDetails() async {
        do {
            let liveBlog = try await network.fetchLiveBlogDetails(id: liveBlogId)
            title = liveBlog.title
        } catch {
            logger.error("Failed to fetch live blog details: \(error)", .network)
        }
    }

    @MainActor
    func onAppear() async {
        await withTaskGroup(of: Void.self) { group in
            group.addTask {
                await self.fetchPermalinksIfNeeded()
            }
            group.addTask {
                await self.fetchLiveBlogDetails()
            }
        }
    }
}

// MARK: - Shareable

extension LiveBlogContainerViewModel: Shareable {
    var shareTitle: String? {
        title
    }

    var permalinkUrl: URL? {
        URL(string: permalink)
    }
}
