//
//  IntervalFadeModifier.swift
//
//
//  Created by Duncan Lau on 24/5/2023.
//

import SwiftUI

struct IntervalFadeModifier: AnimatableModifier {
    let start: Double
    let end: Double
    var animatableData: Double

    func body(content: Content) -> some View {
        content
            .opacity(opacity(for: animatableData))
    }

    private func opacity(for fraction: Double) -> Double {
        // If the fraction is less than the start of the interval or greater than the end,
        // the opacity should be 0 or 1, respectively.
        if fraction < start {
            return 0
        } else if fraction > end {
            return 1
        }

        // Otherwise, interpolate the opacity linearly between the start and end of the interval.
        let intervalLength = end - start
        let intervalFraction = (fraction - start) / intervalLength
        return intervalFraction
    }
}
