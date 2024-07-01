//
//  FeedGameBackgroundView.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 1/13/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import SwiftUI
import UIKit

public struct FeedGameBackgroundView: View {

    private let firstTeamColor: Color
    private let secondTeamColor: Color
    private let containerWidth: CGFloat

    @Environment(\.horizontalSizeClass) private var horizontalSizeClass

    public init(firstTeamColor: Color, secondTeamColor: Color, containerWidth: CGFloat) {
        self.firstTeamColor = firstTeamColor
        self.secondTeamColor = secondTeamColor
        self.containerWidth = containerWidth
    }

    public var body: some View {
        HStack {
            TeamColorCurtain(
                color: firstTeamColor,
                sizeVariant: horizontalSizeClass == .regular
                    ? .large(parentContainerWidth: containerWidth)
                    : .small,
                position: .leading
            )

            Spacer()

            TeamColorCurtain(
                color: secondTeamColor,
                sizeVariant: horizontalSizeClass == .regular
                    ? .large(parentContainerWidth: containerWidth)
                    : .small,
                position: .trailing
            )
        }
    }
}

struct FeedGameBackgroundView_Previews: PreviewProvider {
    static var previews: some View {
        FeedGameBackgroundView(
            firstTeamColor: Color(hex: "#6A14D6"),
            secondTeamColor: Color(hex: "#0E823C"),
            containerWidth: 375
        )
        .previewDevice(PreviewDevice(rawValue: "iPhone 14 Pro"))
        .previewDisplayName("iPhone")

        FeedGameBackgroundView(
            firstTeamColor: Color(hex: "#6A14D6"),
            secondTeamColor: Color(hex: "#0E823C"),
            containerWidth: 800
        )
        .previewDevice(PreviewDevice(rawValue: "iPad Pro (9.7-inch)"))
        .previewDisplayName("Smaller iPad")

        FeedGameBackgroundView(
            firstTeamColor: Color(hex: "#6A14D6"),
            secondTeamColor: Color(hex: "#0E823C"),
            containerWidth: 900
        )
        .previewDevice("iPad Pro (12.9-inch) (5th generation)")
        .previewDisplayName("Larger iPad")
    }
}
