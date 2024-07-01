//
//  MetricScaler.swift
//
//
//  Created by Mark Corbyn on 16/6/2022.
//

import SwiftUI
import UIKit

/// Scales metric values based on the users content size preference.
public enum MetricScaler {

    /// Scale the given `CGFloat` up/down according to the users content size preference,
    ///
    /// Discussion: When scaling the size of an asset such as an image that is aligned with some text, it is good to provide the `style`
    /// parameter matching the font style of the text. This ensures the metric scaler scales the dimension at the same increments as the
    /// font size is scaled.
    /// - Parameters:
    ///   - value: The value to scale
    ///   - maxScaleFactor: Maximum scaling amount to prevent scaling too big. Provide `nil` for no limit.
    ///   - style: The text style to follow when determining increments in size between size categories. Uses `.body` if `nil`.
    /// - Returns: A scaled float
    public static func scale(
        _ value: CGFloat,
        maxScaleFactor: CGFloat?,
        following style: AthleticFont.Style?
    ) -> CGFloat {
        let textStyle: UIFont.TextStyle
        if let athleticStyle = style {
            textStyle = UIFont.font(for: athleticStyle).estimatedTextStyle
        } else {
            textStyle = .body
        }

        let proposedValue = textStyle.metrics.scaledValue(for: value)
        if let maxScaleFactor = maxScaleFactor {
            return CGFloat.minimum(proposedValue, value * maxScaleFactor)
        } else {
            return proposedValue
        }
    }

    /// Convenience function for scaling an `Int` into a `CGFloat`.
    public static func scale(
        _ value: Int,
        maxScaleFactor: CGFloat?,
        following style: AthleticFont.Style?
    ) -> CGFloat {
        Self.scale(CGFloat(value), maxScaleFactor: maxScaleFactor, following: style)
    }
}
