//
//  AppIcon.swift
//  AppIcon
//
//  Created by Kyle Browning on 8/20/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI

struct AppIcon: View {
    let iconName: String?
    var body: some View {
        iconName
            .flatMap { UIImage(named: $0) }
            .map { Image(uiImage: $0).resizable() }
    }
}
