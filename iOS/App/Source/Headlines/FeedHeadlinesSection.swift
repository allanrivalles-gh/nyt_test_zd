//
//  FeedHeadlinesSection.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 14/3/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

struct FeedHeadlinesSection: View {
    let viewModel: FeedHeadlinesSectionViewModel
    let containerProxy: GeometryProxy
    let isBottomPadded: Bool

    var body: some View {
        FeedSupplementedSection(
            headerViewModel: .init(title: viewModel.title),
            isBottomPadded: isBottomPadded
        ) {
            ForEach(indexed: viewModel.items) { index, item in
                let isFirst = index == 0
                let isLast = index == viewModel.items.count - 1

                FeedHeadlineRow(viewModel: item)
                    .padding(.horizontal, 16)
                    .padding(.top, isFirst ? 0 : 8)
                    .padding(.bottom, isLast ? 32 : 8)
                    .trackImpressions(
                        with: item.impressionManager,
                        record: item.analyticData.impress,
                        containerProxy: containerProxy
                    )
            }
        }
    }
}
