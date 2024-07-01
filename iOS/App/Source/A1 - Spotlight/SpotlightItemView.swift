//
//  SpotlightItemView.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 11/29/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticUI
import SwiftUI

struct SpotlightItemView: View {

    private struct Constants {
        static let avatarWidth: CGFloat = 24
        static let imageWidth: CGFloat = 305
        static let imageHeight: CGFloat = imageWidth / AspectRatio.fourThree.value
    }

    @ObservedObject var viewModel: SpotlightViewModel

    var body: some View {
        NavigationLink(screen: .feed(.article(.detail(id: viewModel.articleId, commentId: nil)))) {
            VStack(spacing: 0) {
                PlaceholderLazyImage(
                    imageUrl: viewModel.imageUrl.cdnImageUrl(pixelWidth: 650),
                    placeholder: {
                        Rectangle().fill(Color.chalk.dark300)
                    },
                    modifyImage: { image in
                        image.aspectRatio(contentMode: .fill)
                    }
                )
                .frame(width: Constants.imageWidth, height: Constants.imageHeight)
                .clipped()
                .overlay(
                    DateCapsuleView(date: viewModel.scheduledAt).padding(10),
                    alignment: .topTrailing
                )
                .overlay(
                    LinearGradient(
                        colors: [
                            Color.chalk.dark200.opacity(0),
                            Color.chalk.dark200.opacity(0.15),
                        ],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )

                VStack(alignment: .leading, spacing: 0) {
                    Text(viewModel.title)
                        .lineLimit(3)
                        .multilineTextAlignment(.leading)
                        .fontStyle(.tiemposHeadline.xxs.regular)
                        .foregroundColor(.chalk.dark700)

                    Spacer()

                    BylineView(
                        isSaved: viewModel.isSaved,
                        author: viewModel.byline,
                        commentCount: viewModel.commentCount,
                        highContrast: true
                    ) {
                        if !viewModel.authorImageUrls.isEmpty {
                            Facepile(imageUrls: viewModel.authorImageUrls)
                                .padding(.trailing, 8)
                        }
                    }
                }
                .padding(16)
            }
            .background(Color.chalk.dark300)
            .frame(width: 305, height: 348)
            .cornerRadius(2)
            .task {
                await viewModel.prefetchContentIfNeeded()
            }
        }
        .trackClick(viewModel: viewModel)
    }
}

extension SpotlightItemView: TrackableView {
    var analyticalModel: Analytical {
        viewModel
    }

    var impressionManager: AnalyticImpressionManager {
        viewModel.impressionManager
    }
}
