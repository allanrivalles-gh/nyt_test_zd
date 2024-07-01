//
//  Color+ContrastRatio.swift
//
//  Created by Mark Corbyn on 8/8/2022.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI
import UIKit

extension Color {
    public static func contrastRatio(between color1: Color, and color2: Color) -> CGFloat {
        color1.contrastRatio(with: color2)
    }

    public func contrastRatio(with color: Color) -> CGFloat {
        UIColor(self).contrastRatio(with: UIColor(color))
    }

    public static func highContrastAppearance(
        of color: UIColor,
        forBackgroundColor backgroundColor: Color
    ) -> Color {
        Color(color.highContrastAppearance(forBackgroundColor: UIColor(backgroundColor)))
    }

    public static func highContrastColorScheme(
        of color: UIColor,
        forBackgroundColor backgroundColor: Color
    ) -> ColorScheme? {
        ColorScheme(color.highContrastColorScheme(forBackgroundColor: UIColor(backgroundColor)))
    }
}

extension UIColor {
    public static func contrastRatio(between color1: UIColor, and color2: UIColor) -> CGFloat {
        // https://www.w3.org/TR/WCAG20-TECHS/G18.html#G18-tests
        let luminance1 = color1.luminance()
        let luminance2 = color2.luminance()

        let luminanceDarker = min(luminance1, luminance2)
        let luminanceLighter = max(luminance1, luminance2)

        return (luminanceLighter + 0.05) / (luminanceDarker + 0.05)
    }

    public func contrastRatio(with color: UIColor) -> CGFloat {
        UIColor.contrastRatio(between: self, and: color)
    }

    public func highContrastAppearance(forBackgroundColor backgroundColor: UIColor) -> UIColor {
        switch highContrastColorScheme(forBackgroundColor: backgroundColor) {
        case .dark:
            return darkAppearance
        case .light:
            return lightAppearance
        default:
            return darkAppearance
        }
    }

    public func highContrastColorScheme(
        forBackgroundColor backgroundColor: UIColor
    ) -> UIUserInterfaceStyle {

        /// NB: For colors created from the design system, we use the UIKit `UIColor.dark/lightAppearance` so that the color doesn't
        /// change based on light & dark mode - the color we specify will be the one displayed.
        let darkAppearanceColor = darkAppearance
        let darkAppearanceContrastRatio = backgroundColor.contrastRatio(with: darkAppearanceColor)

        /// WCAG Level AAA requires a contrast ratio of at least 7:1 for normal text and 4.5:1 for large text.
        let wcagAAAContrastRatio: CGFloat = 7
        if darkAppearanceContrastRatio > wcagAAAContrastRatio {
            return .dark
        } else {
            let lightAppearanceColor = lightAppearance
            let lightAppearanceContrastRatio = backgroundColor.contrastRatio(
                with: lightAppearanceColor
            )

            if lightAppearanceContrastRatio > darkAppearanceContrastRatio {
                return .light
            } else {
                return .dark
            }
        }
    }

    private func luminance() -> CGFloat {
        // https://www.w3.org/TR/WCAG20-TECHS/G18.html#G18-tests
        let ciColor = CIColor(color: self)

        func adjust(colorComponent: CGFloat) -> CGFloat {
            return (colorComponent < 0.04045)
                ? (colorComponent / 12.92) : pow((colorComponent + 0.055) / 1.055, 2.4)
        }

        return 0.2126 * adjust(colorComponent: ciColor.red) + 0.7152
            * adjust(colorComponent: ciColor.green) + 0.0722 * adjust(colorComponent: ciColor.blue)
    }
}
