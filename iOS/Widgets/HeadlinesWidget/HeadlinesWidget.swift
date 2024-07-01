//
//  HeadlinesWidget.swift
//  HeadlinesWidget
//
//  Created by Kyle Browning on 9/10/20.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import Apollo
import AthleticFoundation
import AthleticUI
import Intents
import Nuke
import SwiftUI
import WidgetKit

let sampleHeadlines: [Headline] = [
    .init(
        id: "1234",
        title: "Brooks Koepka withdraws from U.S. Open",
        tag: "PGA",
        date: Date(),
        imageURI: nil
    ),
    .init(
        id: "1235",
        title: "Group led by ex-NBAer Arron Afflalo in talks to buy T-Wolves",
        tag: "NBA",
        date: Date().add(minutes: -2),
        imageURI: nil
    ),
    .init(
        id: "1236",
        title: "Minnesota Vikings DE Danielle Hunter placed on injured reserve",
        tag: "NFL",
        date: Date().add(minutes: -4),
        imageURI: nil
    ),
    .init(
        id: "1237",
        title: "ACC to propose every Division I team makes 2021 NCAA Tournament",
        tag: "NCAA",
        date: Date().add(minutes: -5),
        imageURI: nil
    ),
]

struct HeadlinesWidget: Widget {
    let kind: String = Global.Widget.headlinesWidget

    init() {
        AthleticUI.registerFonts()
    }

    var body: some WidgetConfiguration {
        IntentConfiguration(kind: kind, intent: ConfigurationIntent.self, provider: Provider()) {
            entry in
            HeadlinesView(entry: entry)
        }
        .configurationDisplayName("The Athletic")
        .description("The sports headlines that matter, right on your Home Screen.")
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge, .systemExtraLarge])
        .contentMarginsDisabled()
    }
}

struct HeadlinesWidget_Previews: PreviewProvider {

    @ViewBuilder
    static var content: some View {
        HeadlinesView(
            entry: HeadlineTimelineEntry(
                headlines: sampleHeadlines,
                date: Date(),
                configuration: ConfigurationIntent()
            )
        )
        .previewContext(WidgetPreviewContext(family: .systemLarge))
        HeadlinesView(
            entry: HeadlineTimelineEntry(
                headlines: sampleHeadlines,
                date: Date(),
                configuration: ConfigurationIntent()
            )
        )
        .previewContext(WidgetPreviewContext(family: .systemMedium))
        HeadlinesView(
            entry: HeadlineTimelineEntry(
                headlines: sampleHeadlines,
                date: Date(),
                configuration: ConfigurationIntent()
            )
        )
        .previewContext(WidgetPreviewContext(family: .systemSmall))
    }

    static var previews: some View {
        content.preferredColorScheme(.light)
        content.preferredColorScheme(.dark)
    }
}
