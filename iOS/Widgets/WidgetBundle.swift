//
//  WidgetBundle.swift
//  Production
//
//  Created by Duncan Lau on 8/2/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI
import WidgetKit

@main
struct AthleticWidgets: WidgetBundle {
    @WidgetBundleBuilder
    var body: some Widget {
        HeadlinesWidget()

        if #available(iOS 16.2, *) {
            LiveScoreActivityWidget()
        }
    }
}
