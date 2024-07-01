//
//  PodcastSeriesHeaderDetailView.swift
//  theathletic-ios
//
//  Created by Charles Huang on 2/2/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

struct PodcastSeriesHeaderDetailView: View {
    private struct Constants {
        static let horizontalPadding: CGFloat = 20
    }

    @StateObject var viewModel: PodcastSeriesListViewModel

    var body: some View {
        GeometryReader { geometry in
            VStack {
                Spacer()

                if let description = viewModel.podcast?.description {
                    AttributedLabelView(
                        attributedText: description.style(
                            tags: viewModel.headerStyles.tags,
                            transformers: AttributedLabelView.tagTransformers
                        )
                        .styleLinks(viewModel.headerStyles.linkStyle)
                        .styleAll(viewModel.headerStyles.allStyle),
                        maxWidth: geometry.size.width - (2 * Constants.horizontalPadding),
                        delegate: viewModel,
                        configureLabel: { $0.numberOfLines = 0 }
                    )
                    .fixedSize()
                    .padding(.horizontal, Constants.horizontalPadding)
                }

                Spacer()
            }
            .safariView(url: $viewModel.navigatedExternalUrl)
        }
    }
}
