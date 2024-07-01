//
//  AnimatedGradeStarsBar.swift
//  theathletic-ios
//
//  Created by Duncan Lau on 6/2/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

struct AnimatedGradeStarsBar: View {

    @State private var numOfOverlayStarsDisplayed: Int?

    let filledColor: Color
    let unfilledColor: Color
    let numberFilled: Int
    let size: CGFloat
    let spacing: CGFloat
    let onTapStar: ((Int) -> Void)?

    private static let nextStarAnimationDelay = 0.04

    var body: some View {
        GradeStarsBar(
            filledColor: filledColor,
            unfilledColor: unfilledColor,
            numberFilled:
                numOfOverlayStarsDisplayed != nil
                ? 0
                : numberFilled,
            size: size,
            spacing: spacing,
            onTapStar: { grade in
                let existingGrade = numberFilled
                let newGrade = grade == numberFilled ? 0 : grade

                numOfOverlayStarsDisplayed = existingGrade

                let starAnimationSequence =
                    newGrade <= existingGrade
                    ? Array((newGrade...existingGrade).reversed())
                    : Array(existingGrade + 1...newGrade)

                var timing = 0.0
                for number in starAnimationSequence {
                    withAnimation(
                        .interpolatingSpring(stiffness: 80, damping: 8)
                            .delay(timing)
                    ) {
                        numOfOverlayStarsDisplayed = number
                    }
                    timing += AnimatedGradeStarsBar.nextStarAnimationDelay
                }

                Task {
                    try await Task.sleep(seconds: timing + 0.5)
                    numOfOverlayStarsDisplayed = nil
                }

                onTapStar?(grade)
            }
        )
        .disabled(numOfOverlayStarsDisplayed != nil)
        .overlay(
            GradeStarsBar(
                filledColor: filledColor,
                unfilledColor: unfilledColor,
                numberFilled: 5,
                numberDisplayed: numOfOverlayStarsDisplayed ?? 0,
                size: size,
                spacing: spacing
            )
        )

    }
}
