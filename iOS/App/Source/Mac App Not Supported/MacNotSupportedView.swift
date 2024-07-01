//
//  MacNotSupportedView.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 17/10/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticUI
import SwiftUI

struct MacNotSupportedView: View {

    @EnvironmentObject private var compass: Compass

    @Binding var isShowing: Bool

    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                Text(compass.config.flags.macAppNotSupportedTitle)
                    .fontName(.regularSlabBold, size: 55)
                    .foregroundColor(.chalk.dark800)
                    .multilineTextAlignment(.center)
                    .padding(.bottom, 25)

                Text(compass.config.flags.macAppNotSupportedMessage)
                    .fontName(.calibreRegular, size: 24)
                    .foregroundColor(.chalk.dark800)
                    .multilineTextAlignment(.center)
                    .padding(.bottom, 40)

                Button(action: { isShowing = false }) {
                    Text("OK, Got It")
                        .fontName(.calibreMedium, size: 27)
                        .foregroundColor(.chalk.dark200)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 12)
                }
                .buttonStyle(.core(size: .fitted, level: .primary))
            }
            .frame(maxWidth: 650)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.chalk.dark100)
    }
}

#Preview {
    MacNotSupportedView(isShowing: .constant(true))
        .environmentObject(AppEnvironment.shared.compass)

}
