//
//  PodcastClip.swift
//
//
//  Created by Kyle Browning on 6/12/23.
//

import Foundation

public struct PodcastClip: Codable, Hashable {
    public init(
        id: Int,
        title: String? = nil,
        startPosition: Int? = nil,
        endPosition: Int? = nil
    ) {
        self.id = id
        self.title = title
        self.startPosition = startPosition
        self.endPosition = endPosition
    }

    public let id: Int
    public let title: String?
    public let startPosition: Int?
    public let endPosition: Int?
}
