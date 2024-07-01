//
//  PodcastSeries.swift
//
//
//  Created by Jason Leyrer on 6/6/23.
//

import AthleticFoundation
import Foundation

public struct PodcastSeries: Codable, Hashable {
    public let id: String
    public let title: String
    public let description: String
    public var isFollowing: Bool
    public let imageUrl: URL?
    public let metadataString: String?
    public let permalinkUrl: URL?
    public var isNotificationEnabled: Bool

    public init(
        id: String,
        title: String,
        description: String,
        isFollowing: Bool,
        imageUrl: URL? = nil,
        metadataString: String? = nil,
        permalinkUrl: URL? = nil,
        isNotificationEnabled: Bool
    ) {
        self.id = id
        self.title = title
        self.description = description
        self.isFollowing = isFollowing
        self.imageUrl = imageUrl
        self.metadataString = metadataString
        self.permalinkUrl = permalinkUrl
        self.isNotificationEnabled = isNotificationEnabled
    }
}

// MARK: - Shareable

extension PodcastSeries: Shareable {
    public var shareTitle: String? {
        title
    }
}
