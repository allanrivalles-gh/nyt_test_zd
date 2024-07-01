//
//  GQL.RecommendedPodcastConsumable+AsPodcastShowItemPodcast.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 04/08/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

extension GQL.RecommendedPodcastConsumable {
    var asPodcastShowItemPodcast: PodcastShowItemPodcast {
        MinimalPodcastShowItemPodcast(
            id: podcastId,
            imageUrl: URL(string: imageUrl),
            title: title,
            metadataString: metadataString
        )
    }
}
