//
//  GQL.PodcastDetail+AsPodcastSeries.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 04/08/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticNavigation
import Foundation

extension GQL.PodcastDetail {
    var asPodcastSeries: PodcastSeries {
        PodcastSeries(
            id: id,
            title: title,
            description: description,
            isFollowing: isFollowing,
            imageUrl: URL(string: imageUrl),
            metadataString: metadataString,
            permalinkUrl: URL(string: permalinkUrl),
            isNotificationEnabled: notifEpisodesOn ?? false
        )
    }
}
