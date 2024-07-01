//
//  HeadlinesHeaderView.swift
//  HeadlinesWidgetExtension
//
//  Created by Kyle Browning on 8/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticUI
import SwiftUI

struct HeadlinesHeaderView: View {
    var body: some View {
        HStack {
            Text("Headlines")
                .fontStyle(.slab.m.bold)
                .foregroundColor(.chalk.dark800)
            Spacer()
            Image("logo")
                .resizable()
                .frame(width: 16, height: 16)
        }
        .padding(.horizontal)
        .widgetURL(URL(string: "theathletic://headline-widget-header/app_widget"))
    }
}
