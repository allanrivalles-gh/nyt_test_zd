//
//  FeedA1Section.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 14/3/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import SwiftUI

struct FeedA1Section: View {
    let viewModel: FeedA1SectionViewModel
    let containerProxy: GeometryProxy
    let isBottomPadded: Bool

    var body: some View {
        FeedSupplementedSection(
            headerViewModel: .init(title: viewModel.title, logo: .a1),
            isBottomPadded: isBottomPadded
        ) {
            ScrollView(.horizontal, showsIndicators: false) {
                LazyHStack(spacing: 12) {
                    let _ = DuplicateIDLogger.logDuplicates(in: viewModel.items)
                    ForEach(viewModel.items) { item in
                        SpotlightItemView(viewModel: item)
                            .frame(width: 305, height: 348)
                            .trackImpressions(
                                with: item.impressionManager,
                                record: item.analyticData.impress,
                                containerProxy: containerProxy
                            )
                    }
                }
                .padding(.horizontal, 16)
            }
            .padding(.bottom, 24)
        }
    }
}
