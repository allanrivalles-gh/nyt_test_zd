//
//  GradeStarsBar.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 20/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation
import SwiftUI

/// A bar of 5 stars representing a grade of 1-5 or ungraded
struct GradeStarsBar: View {

    let filledColor: Color
    let unfilledColor: Color
    let numberFilled: Int
    var numberDisplayed: Int = 5
    let size: CGFloat
    let spacing: CGFloat
    var onTapStar: ((Int) -> Void)?

    var body: some View {
        HStack(spacing: spacing) {
            let _ = DuplicateIDLogger.logDuplicates(in: Array(1..<6), id: \.self)
            ForEach(1..<6) { number in
                let isFilled = number <= numberFilled
                let shouldShowStar = numberDisplayed >= number

                Group {
                    let star = GradeStar(
                        color: isFilled ? filledColor : unfilledColor,
                        isFilled: isFilled,
                        size: size
                    )
                    if let onTapStar {
                        star
                            .onTapGesture {
                                onTapStar(number)
                            }
                    } else {
                        star
                    }
                }
                .opacity(
                    shouldShowStar ? 1 : 0
                )
                .scaleEffect(
                    shouldShowStar ? 1 : 0.1
                )
            }
        }
    }
}
