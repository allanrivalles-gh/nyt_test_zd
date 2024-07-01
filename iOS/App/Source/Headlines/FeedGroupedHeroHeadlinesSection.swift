//
//  FeedGroupedHeroHeadlinesSection.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 22/4/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticUI
import Foundation
import SwiftUI

struct FeedGroupedHeroHeadlinesSection: View {

    private struct Constants {
        static let padding: CGFloat = 20
    }

    let viewModel: FeedGroupedHeroHeadlinesSectionViewModel
    let containerProxy: GeometryProxy

    private var hStackSpacing: CGFloat {
        /// We add 1 for account for the divider. The divider doesn't layout properly on rotate when part of the hierarchy but it does so
        /// as an overlay.
        Constants.padding * 2 + 1
    }

    private var topperWidth: CGFloat {
        (containerProxy.size.width - Constants.padding * 2 - hStackSpacing) / 2
    }

    var body: some View {

        VStack(alignment: .leading, spacing: 24) {
            HStack(alignment: .top, spacing: hStackSpacing) {
                VTopperRow(
                    feedItem: viewModel.hero.item,
                    bodyInsets: 0,
                    containerProxy: containerProxy,
                    containerWidthOverride: topperWidth
                )
                .background(Color.chalk.dark200)
                .ifLet(viewModel.hero.item.impressionManager) { view, impressionManager in
                    view
                        .trackImpressions(
                            with: impressionManager,
                            record: viewModel.hero.item.impressionRecord,
                            containerProxy: containerProxy
                        )
                }
                .frame(maxWidth: .infinity)

                VStack(alignment: .leading, spacing: 16) {
                    Text(viewModel.headlines.title)
                        .fontStyle(.slab.s.bold)
                        .foregroundColor(Color.chalk.dark700)
                        .padding(.bottom, 8)

                    ForEach(indexed: viewModel.headlines.items) { index, item in
                        FeedHeadlineRow(viewModel: item)
                            .trackImpressions(
                                with: item.impressionManager,
                                record: item.analyticData.impress,
                                containerProxy: containerProxy
                            )
                    }
                }
                .frame(maxWidth: .infinity)
            }
            .overlay(
                DividerView(axis: .vertical)
            )
            DividerView()
        }
        .padding([.horizontal, .top], Constants.padding)
        .padding(.bottom, 24)
        .background(Color.chalk.dark200)
    }
}
