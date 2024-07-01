//
//  HeadlinesView.swift
//  HeadlinesWidgetExtension
//
//  Created by Kyle Browning on 8/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

struct HeadlinesView: View {

    private struct Constants {
        static let imageSize: CGFloat = 54
        static let backgroundColor: Color = .chalk.dark200
    }

    @Environment(\.widgetFamily) var widgetFamily

    var entry: Provider.Entry

    var body: some View {
        let headlines = entry.headlines
        let isPlaceholder = entry.isPlaceholder
        let isDataStale = entry.isDataStale

        Group {
            switch widgetFamily {
            case .systemExtraLarge:
                ExtraLargeHeadlinesView(
                    headlines: Array(headlines.prefix(8)),
                    isPlaceholder: isPlaceholder,
                    isDataStale: isDataStale
                )
            case .systemLarge:
                LargeHeadlinesView(
                    headlines: Array(headlines.prefix(4)),
                    isPlaceholder: isPlaceholder,
                    isDataStale: isDataStale
                )
            case .systemMedium:
                MediumHeadlinesView(
                    headlines: Array(headlines.prefix(2)),
                    isPlaceholder: isPlaceholder,
                    isDataStale: isDataStale
                )
            case .systemSmall:
                SmallHeadlinesView(
                    headlines: Array(headlines.prefix(1)),
                    isPlaceholder: isPlaceholder,
                    isDataStale: isDataStale
                )
            @unknown default:
                let _ = DuplicateIDLogger.logDuplicates(in: headlines, id: \.self)
                ForEach(headlines, id: \.self) { headline in
                    HeadlinesRow(headline: headline, style: .calibreUtility.xs.regular)
                }
            }
        }
        .widgetBackground(backgroundView: Rectangle().fill(Constants.backgroundColor))
    }
}
