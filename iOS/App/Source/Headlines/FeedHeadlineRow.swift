//
//  FeedHeadlineRow.swift
//  theathletic-ios
//
//  Created by Andrew Fannin on 11/5/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import SwiftUI
import UIKit

struct FeedHeadlineRow: View {

    var viewModel: SingleItemLiteViewModel

    var body: some View {
        NavigationLink(screen: viewModel.navigationDestination) {
            HStack(alignment: .top, spacing: 0) {
                Image("square_bullet")
                    .foregroundColor(.chalk.dark400)
                    .offset(x: 0, y: 8)
                    .padding(.trailing, 16)

                Text(viewModel.title)
                    .fontName(.calibreRegular, size: 17)
                    .foregroundColor(.chalk.dark700)
                    .multilineTextAlignment(.leading)

                Spacer(minLength: 0)
            }
        }
        .onSimultaneousTapGesture {
            Analytics.track(event: viewModel.analyticData.click)
        }
    }
}

extension FeedHeadlineRow: TrackableView {
    var analyticalModel: Analytical {
        viewModel
    }

    var impressionManager: AnalyticImpressionManager {
        viewModel.impressionManager
    }
}

struct FeedHeadlineRow_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            VStack(spacing: 0) {
                FeedHeadlineRow(viewModel: normalRowViewModel)
                FeedHeadlineRow(viewModel: longRowViewModel)
            }
            .previewLayout(.sizeThatFits)
            VStack(spacing: 0) {
                FeedHeadlineRow(viewModel: longRowViewModel)
                FeedHeadlineRow(viewModel: normalRowViewModel)
            }
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.dark)
        }
    }

    fileprivate static var normalRowViewModel: SingleItemLiteViewModel = .init(
        type: .headline(identifier: "1", title: "This is a test headline", tagTitle: nil),
        analytics: analyticsConfig
    )

    fileprivate static var longRowViewModel: SingleItemLiteViewModel = .init(
        type: .headline(
            identifier: "2",
            title:
                "This is an extra long headline with a lot of extra text which should be two lines",
            tagTitle: nil
        ),
        analytics: analyticsConfig
    )

    static private var analyticsConfig = FeedSectionAnalyticsConfiguration(
        objectType: .headlineId,
        element: .headlineMultiple,
        container: .headlineMultiple,
        sourceView: .home,
        feedFilter: .init(page: 0),
        impressionManager: AnalyticsManagers.feedImpressions,
        indexPath: IndexPath(item: 0, section: 1),
        pageOrder: 1
    )
}
