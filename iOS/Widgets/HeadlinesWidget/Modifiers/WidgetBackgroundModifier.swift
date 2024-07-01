//
//  WidgetBackgroundModifier.swift
//  Widgets
//
//  Created by Jason Leyrer on 10/26/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

extension View {
    func widgetBackground(backgroundView: some View) -> some View {
        if #available(iOSApplicationExtension 17.0, *) {
            return containerBackground(for: .widget) {
                backgroundView
            }
        } else {
            return background(backgroundView)
        }
    }
}
