//
//  ExtraLargeHeadlinesView.swift
//  HeadlinesWidgetExtension
//
//  Created by Kyle Browning on 5/14/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI
import WidgetKit

struct ExtraLargeHeadlinesView: View {
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
                HStack {
                    VStack {
                        let _ = DuplicateIDLogger.logDuplicates(
                            in: Array(headlines.prefix(4)),
                            id: \.self
                        )
                        ForEach(headlines.prefix(4), id: \.self) { headline in
                            let index = headlines.firstIndex { $0 == headline } ?? -1

                            if isPlaceholder {
                                HeadlinesRow(headline: headline, style: .tiemposBody.s.regular)
                                    .redacted(reason: .placeholder)
                            } else {
                                Link(
                                    destination: URL(
                                        string:
                                            "theathletic://headline-widget/\(headline.id)/\(index)"
                                    )!
                                ) {
                                    HeadlinesRow(headline: headline, style: .tiemposBody.s.regular)
                                }
                            }
                        }
                    }
                    VStack {
                        let _ = DuplicateIDLogger.logDuplicates(
                            in: Array(headlines.suffix(4)),
                            id: \.self
                        )
                        ForEach(headlines.suffix(4), id: \.self) { headline in
                            let index = (headlines.firstIndex { $0 == headline } ?? -1) + 4
                            if isPlaceholder {
                                HeadlinesRow(headline: headline, style: .tiemposBody.s.regular)
                                    .redacted(reason: .placeholder)
                            } else {
                                Link(
                                    destination: URL(
                                        string:
                                            "theathletic://headline-widget/\(headline.id)/\(index)"
                                    )!
                                ) {
                                    HeadlinesRow(headline: headline, style: .tiemposBody.s.regular)
                                }
                            }
                        }
                    }
                }
            }
        }
        .padding(.vertical, 16)
    }
}

struct ExtraLargeHeadlinesView_Previews: PreviewProvider {
    static var previews: some View {
        // Note that systemExtraLarge widgets are only available on iPad devices
        let previewDeviceString = "iPad Pro (11-inch) (4th generation)"

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
            .previewContext(WidgetPreviewContext(family: .systemExtraLarge))
            .previewDevice(PreviewDevice(rawValue: previewDeviceString))
        HeadlinesView(entry: placeholderEntry)
            .previewContext(WidgetPreviewContext(family: .systemExtraLarge))
            .previewDevice(PreviewDevice(rawValue: previewDeviceString))
        HeadlinesView(entry: staleDataEntry)
            .previewContext(WidgetPreviewContext(family: .systemExtraLarge))
            .previewDevice(PreviewDevice(rawValue: previewDeviceString))
    }
}
