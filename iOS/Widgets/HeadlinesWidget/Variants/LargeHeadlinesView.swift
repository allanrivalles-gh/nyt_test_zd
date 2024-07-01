//
//  LargeHeadlinesView.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 8/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import SwiftUI
import WidgetKit

struct LargeHeadlinesView: View {
    let headlines: [Headline]
    let isPlaceholder: Bool
    let isDataStale: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HeadlinesHeaderView()

            if isDataStale {
                HeadlinesNoNetworkText()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .padding(.bottom, 16)
            } else if headlines.isEmpty {
                HeadlinesNoContentText()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .padding(.bottom, 16)
            } else {
                let _ = DuplicateIDLogger.logDuplicates(in: headlines, id: \.self)
                ForEach(headlines, id: \.self) { headline in
                    let index = headlines.firstIndex { $0 == headline } ?? -1

                    if isPlaceholder {
                        HeadlinesRow(headline: headline, style: .tiemposBody.xs.regular)
                            .redacted(reason: .placeholder)
                    } else {
                        Link(
                            destination: URL(
                                string:
                                    "theathletic://headline-widget/\(headline.id)/\(index)"
                            )!
                        ) {
                            HeadlinesRow(headline: headline, style: .tiemposBody.xs.regular)
                        }
                    }
                }
            }
        }
        .padding(.vertical, 16)
    }
}

struct LargeHeadlinesView_Previews: PreviewProvider {
    static var previews: some View {
        let headlinesEntry = HeadlineTimelineEntry(
            headlines: sampleHeadlines,
            date: Date(),
            configuration: ConfigurationIntent()
        )
        let placeholderEntry = HeadlineTimelineEntry(
            headlines: sampleHeadlines,
            date: Date(),
            configuration: ConfigurationIntent(),
            isPlaceholder: true
        )
        let staleDataEntry = HeadlineTimelineEntry(
            headlines: sampleHeadlines,
            date: Date(),
            configuration: ConfigurationIntent(),
            isDataStale: true
        )

        HeadlinesView(entry: headlinesEntry)
            .previewContext(WidgetPreviewContext(family: .systemLarge))
        HeadlinesView(entry: placeholderEntry)
            .previewContext(WidgetPreviewContext(family: .systemLarge))
        HeadlinesView(entry: staleDataEntry)
            .previewContext(WidgetPreviewContext(family: .systemLarge))
    }
}
