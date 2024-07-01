//
//  HeadlineTimelineEntry.swift
//  HeadlinesWidgetExtension
//
//  Created by Kyle Browning on 8/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import WidgetKit

struct HeadlineTimelineEntry: TimelineEntry {
    let headlines: [Headline]
    let date: Date
    let configuration: ConfigurationIntent
    let isPlaceholder: Bool
    let isDataStale: Bool

    init(
        headlines: [Headline],
        date: Date,
        configuration: ConfigurationIntent,
        isPlaceholder: Bool = false,
        isDataStale: Bool = false
    ) {
        self.headlines = headlines
        self.date = date
        self.configuration = configuration
        self.isPlaceholder = isPlaceholder
        self.isDataStale = isDataStale
    }
}
