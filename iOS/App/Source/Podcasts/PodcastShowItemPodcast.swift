//
//  PodcastShowItemPodcast.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 04/08/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticNavigation
import Foundation

protocol PodcastShowItemPodcast {
    var id: String { get }
    var imageUrl: URL? { get }
    var title: String { get }
    var metadataString: String? { get }
}

struct MinimalPodcastShowItemPodcast: PodcastShowItemPodcast {
    let id: String
    let imageUrl: URL?
    let title: String
    let metadataString: String?
}

extension PodcastSeries: PodcastShowItemPodcast {}
