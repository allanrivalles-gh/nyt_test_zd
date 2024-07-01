//
//  HubToast.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 26/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticUI
import Foundation
import SwiftUI

struct ToastView: View {

    let message: String
    let systemImage: String?

    @Environment(\.horizontalSizeClass) private var horizontalSizeClass

    var body: some View {
        HStack(spacing: 8) {
            if let systemImage {
                Image(systemName: systemImage)
            }
            Text(message)
                .fontStyle(.calibreUtility.l.regular)
        }
        .foregroundColor(.chalk.dark200)
        .padding(.vertical, 10)
        .frame(maxWidth: horizontalSizeClass == .compact ? .infinity : 400)
        .background(Color.chalk.dark800.cornerRadius(4))
        .padding(.horizontal, 16)
        .padding(.bottom, 8)
    }

}
