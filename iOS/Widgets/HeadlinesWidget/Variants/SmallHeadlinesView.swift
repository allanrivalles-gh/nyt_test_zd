//
//  SmallHeadlinesView.swift
//  HeadlinesWidgetExtension
//
//  Created by Kyle Browning on 8/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticUI
import SwiftUI
import WidgetKit

struct SmallHeadlinesView: View {
    struct Constants {
        static let foregroundColor: Color = .chalk.dark800
        static let backgroundColor: Color = .chalk.dark200
    }

    let headlines: [Headline]
    let isPlaceholder: Bool
    let isDataStale: Bool

    var body: some View {
        ZStack {
            let headline = headlines.first

            if let imageURI = headline?.imageURI,
                let url = URL(string: imageURI),
                let imageData = try? Data(contentsOf: url),
                let uiImage = UIImage(data: imageData)
            {
                Image(uiImage: uiImage)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(minWidth: 0)

                Rectangle()
                    .foregroundColor(Constants.backgroundColor)
                    .opacity(0.7)
            } else {
                Rectangle()
                    .foregroundColor(Constants.backgroundColor)
            }

            VStack(alignment: .leading, spacing: 0) {
                HStack {
                    if let headline {
                        TimeTagView(
                            tag: headline.tag,
                            date: headline.date,
                            color: Constants.foregroundColor
                        ).redacted(reason: isPlaceholder || isDataStale ? .placeholder : [])
                    }
                    Spacer()
                    Image("logo")
                        .resizable()
                        .frame(width: 16, height: 16)
                        .foregroundColor(Constants.foregroundColor)
                }
                Spacer()

                if isDataStale {
                    HeadlinesNoNetworkText()
                } else if let headline {
                    Text(headline.title)
                        .fontStyle(.tiemposBody.s.regular)
                        .redacted(reason: isPlaceholder ? .placeholder : [])
                } else {
                    HeadlinesNoContentText()
                }
            }
            .padding()
            .foregroundColor(Constants.foregroundColor)
            .widgetURL(widgetUrl)
        }
    }

    private var widgetUrl: URL? {
        if let headline = headlines.first {
            return URL(string: "theathletic://headline-widget/\(headline.id)/-1")
        }
        return URL(string: "theathletic://headline-widget-header/app_widget")
    }
}

struct SmallHeadlinesView_Previews: PreviewProvider {
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
            .previewContext(WidgetPreviewContext(family: .systemSmall))
        HeadlinesView(entry: placeholderEntry)
            .previewContext(WidgetPreviewContext(family: .systemSmall))
        HeadlinesView(entry: staleDataEntry)
            .previewContext(WidgetPreviewContext(family: .systemSmall))
    }
}
