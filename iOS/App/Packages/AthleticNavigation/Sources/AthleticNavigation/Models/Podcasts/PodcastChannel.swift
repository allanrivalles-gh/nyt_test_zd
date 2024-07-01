//
//  PodcastChannel.swift
//
//
//  Created by Jason Leyrer on 6/6/23.
//

import Foundation

public struct PodcastChannel: Codable, Hashable {
    public let id: String
    public let imageUrlString: String?
    public let mobileImageUrlString: String?
    public let name: String?
    public let type: String?
    public let urlString: String?

    public init(
        id: String,
        imageUrlString: String?,
        mobileImageUrlString: String?,
        name: String?,
        type: String?,
        urlString: String?
    ) {
        self.id = id
        self.imageUrlString = imageUrlString
        self.mobileImageUrlString = mobileImageUrlString
        self.name = name
        self.type = type
        self.urlString = urlString
    }
}
