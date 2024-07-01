//
//  TimeTagView.swift
//  HeadlinesWidgetExtension
//
//  Created by Kyle Browning on 8/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

struct TimeTagView: View {
    let tag: String?
    let date: Date
    let color: Color

    var body: some View {
        HStack {
            Group {
                if let unwrappedTag = tag {
                    Text(unwrappedTag)
                    Text("•")
                }
                Text(date.timeShort())
            }
        }
        .fontStyle(.calibreUtility.xs.regular)
        .foregroundColor(color)
    }
}
