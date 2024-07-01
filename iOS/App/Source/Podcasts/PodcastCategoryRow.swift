//
//  PodcastCategoryRow.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 12/22/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticNavigation
import AthleticUI
import SwiftUI

struct PodcastCategoryRow: View {
    let model: LeaguePodcastListViewModel

    var body: some View {
        NavigationLink(screen: .listen(.leaguePodcasts(channel: model.channelDto))) {
            HStack(spacing: 16) {
                PlaceholderLazyImage(
                    imageUrl: model.imageUrl,
                    modifyImage: {
                        $0.aspectRatio(contentMode: .fit)
                    }
                )
                .frame(width: 30, height: 30)

                Text(model.leagueName)
                    .fontStyle(.calibreUtility.xl.medium)
                    .foregroundColor(.chalk.dark800)

                Spacer()

                Chevron()
            }
            .padding(.leading, 18)
            .padding(.trailing, 26)
            .padding(.vertical, 16)
        }
    }
}

struct PodcastCategoryRow_Previews: PreviewProvider {
    static var previews: some View {
        PodcastCategoryRow(
            model:
                LeaguePodcastListViewModel(
                    channel: ListenModel.PodcastCategory(
                        id: "1-nhl",
                        imageUrl:
                            "https://s3-us-west-2.amazonaws.com/theathletic-league-logos/league-1-podcasts@3x.png",
                        name: "NHL",
                        type: "league",
                        url: "nhl"
                    ),
                    listenModel: AppEnvironment.shared.listen
                )
        )
    }
}
