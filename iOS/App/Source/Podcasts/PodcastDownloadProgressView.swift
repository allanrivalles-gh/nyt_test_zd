//
//  PodcastDownloadProgressView.swift
//  theathletic-ios
//
//  Created by Charles Huang on 1/27/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

struct PodcastDownloadProgressView: View {
    let progress: CGFloat
    let state: PodcastDownloadState

    var body: some View {
        Group {
            if state == .downloaded {
                Image("podcast_download")
                    .renderingMode(.template)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .foregroundColor(.chalk.dark700)

            } else if state == .downloading {
                DownloadProgressCircleView(progress: progress)
            }
        }
        .frame(width: 13, height: 13)
    }
}
