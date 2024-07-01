//
//  PodcastSeriesHeaderMainView.swift
//  theathletic-ios
//
//  Created by Charles Huang on 2/2/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

struct PodcastSeriesHeaderMainView: View {
    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement

    @ObservedObject var viewModel: PodcastSeriesListViewModel

    var logger = ATHLogger(category: .podcast)

    var body: some View {
        VStack(alignment: .center, spacing: 20) {
            PlaceholderLazyImage(
                imageUrl: viewModel.podcast?.imageUrl,
                modifyImage: {
                    $0.aspectRatio(contentMode: .fit)
                }
            )
            .frame(width: 220, height: 220)
            .padding(.top, 20)

            if let title = viewModel.podcast?.title {
                Text(title)
                    .fontStyle(.calibreHeadline.m.semibold)
                    .multilineTextAlignment(.center)
                    .foregroundColor(.chalk.dark800)
                    .padding(.horizontal, 12)
            }

            Button(
                action: {
                    viewModel.followPodcast()
                },
                label: {
                    Spacer()

                    if viewModel.isFollowing {
                        Text(Strings.following.localized.uppercased())
                            .fontStyle(.calibreUtility.l.medium)
                            .foregroundColor(.chalk.dark700)
                            .frame(height: 48)
                    } else {
                        Text(Strings.follow.localized.uppercased())
                            .fontStyle(.calibreUtility.l.medium)
                            .foregroundColor(.chalk.dark200)
                            .frame(height: 48)
                    }

                    Spacer()
                }
            )
            .disabled(viewModel.loadingState.isLoading)
            .background(
                viewModel.isFollowing
                    ? Color.chalk.dark300
                    : Color.chalk.dark800
            )
            .cornerRadius(2)
            .padding(.horizontal, 16)
            .padding(.bottom, 20)

            Spacer()
        }
    }
}
