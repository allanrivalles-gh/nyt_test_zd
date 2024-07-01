//
//  Font+Theme.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 29/7/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI
import UIKit

public enum AthleticFont {

    /// Approved design system font styles to be used throughout the app.
    /// Only add to this if the new style is officially approved and included in the design system.
    /// One-off styles or unapproved styles should be handled by other means.
    ///
    /// The naming convention for these fonts is: .style.size.weight
    /// e.g.
    /// - `.calibreUtility.l.medium` is `Utility` style, `Large` size, `Medium` weight.
    ///
    /// The fonts are ordered top to bottom, by font style, then size largest to smallest, then weight heaviest to lightest.
    /// Please try to keep them ordered if new fonts are added to the design system.
    public struct Style: Equatable {
        fileprivate let font: UIFont

        public static func == (lhs: AthleticFont.Style, rhs: AthleticFont.Style) -> Bool {
            lhs.font == rhs.font
        }

        public struct Slab {
            public struct L {
                public let bold = Style(font: .font(name: .regularSlabBold, size: 42))
            }

            public struct M {
                public let bold = Style(font: .font(name: .regularSlabBold, size: 27))
            }

            public struct S {
                public let bold = Style(font: .font(name: .regularSlabBold, size: 18))
            }

            public let l = L()
            public let m = M()
            public let s = S()
        }

        public struct TiemposHeadline {
            public struct XXL {
                public let medium = Style(font: .font(name: .tiemposHeadlineMedium, size: 42))
                public let regular = Style(font: .font(name: .tiemposHeadlineRegular, size: 42))
            }

            public struct XL {
                public let medium = Style(font: .font(name: .tiemposHeadlineMedium, size: 36))
                public let regular = Style(font: .font(name: .tiemposHeadlineRegular, size: 36))
            }

            public struct L {
                public let medium = Style(font: .font(name: .tiemposHeadlineMedium, size: 30))
                public let regular = Style(font: .font(name: .tiemposHeadlineRegular, size: 30))
            }

            public struct M {
                public let regular = Style(font: .font(name: .tiemposHeadlineRegular, size: 24))
            }

            public struct S {
                public let regular = Style(font: .font(name: .tiemposHeadlineRegular, size: 20))
            }

            public struct XS {
                public let regular = Style(font: .font(name: .tiemposHeadlineRegular, size: 18))
            }

            public struct XXS {
                public let regular = Style(font: .font(name: .tiemposHeadlineRegular, size: 16))
            }

            public let xxl = XXL()
            public let xl = XL()
            public let l = L()
            public let m = M()
            public let s = S()
            public let xs = XS()
            public let xxs = XXS()
        }

        public struct TiemposBody {
            public struct L {
                public let medium = Style(font: .font(name: .tiemposTextMedium, size: 18))
                public let regular = Style(font: .font(name: .tiemposTextRegular, size: 18))
            }

            public struct M {
                public let medium = Style(font: .font(name: .tiemposTextMedium, size: 16))
                public let regular = Style(font: .font(name: .tiemposTextRegular, size: 16))
            }

            public struct S {
                public let medium = Style(font: .font(name: .tiemposTextMedium, size: 14))
                public let regular = Style(font: .font(name: .tiemposTextRegular, size: 14))
            }

            public struct XS {
                public let medium = Style(font: .font(name: .tiemposTextMedium, size: 12))
                public let regular = Style(font: .font(name: .tiemposTextRegular, size: 12))
            }

            public let l = L()
            public let m = M()
            public let s = S()
            public let xs = XS()
        }

        public struct CalibreHeadline {
            public struct XL {
                public let regular = Style(font: .font(name: .calibreRegular, size: 48))
            }

            public struct L {
                public let semibold = Style(font: .font(name: .calibreSemibold, size: 36))
            }

            public struct M {
                public let semibold = Style(font: .font(name: .calibreSemibold, size: 28))
            }

            public struct S {
                public let semibold = Style(font: .font(name: .calibreSemibold, size: 20))
                public let medium = Style(font: .font(name: .calibreMedium, size: 20))
            }

            public let xl = XL()
            public let l = L()
            public let m = M()
            public let s = S()
        }

        public struct CalibreUtility {
            public struct XXL {
                public let medium = Style(font: .font(name: .calibreMedium, size: 24))
                public let regular = Style(font: .font(name: .calibreRegular, size: 24))
            }

            public struct XL {
                public let medium = Style(font: .font(name: .calibreMedium, size: 18))
                public let regular = Style(font: .font(name: .calibreRegular, size: 18))
            }

            public struct L {
                public let medium = Style(font: .font(name: .calibreMedium, size: 16))
                public let regular = Style(font: .font(name: .calibreRegular, size: 16))
            }

            public struct S {
                public let medium = Style(font: .font(name: .calibreMedium, size: 14))
                public let regular = Style(font: .font(name: .calibreRegular, size: 14))
            }

            public struct XS {
                public let medium = Style(font: .font(name: .calibreMedium, size: 12))
                public let regular = Style(font: .font(name: .calibreRegular, size: 12))
            }

            public let xxl = XXL()
            public let xl = XL()
            public let l = L()
            public let s = S()
            public let xs = XS()
        }

        public struct CalibreTag {
            public struct L {
                public let medium = Style(font: .font(name: .calibreMedium, size: 15))
            }

            public struct XS {
                public let medium = Style(font: .font(name: .calibreMedium, size: 10))
            }

            public let l = L()
            public let xs = XS()
        }

        // MARK: - Chalk Styles

        public static let slab = Slab()
        public static let tiemposHeadline = TiemposHeadline()
        public static let tiemposBody = TiemposBody()
        public static let calibreHeadline = CalibreHeadline()
        public static let calibreUtility = CalibreUtility()
        public static let calibreTag = CalibreTag()
        public static let sohneData = Style(font: .font(name: .sohneRegular, size: 14))
    }

    /// The font names of fonts used by The Athletic
    ///
    /// This is for creating fonts in size/weight combinations that are not in the style guide.
    /// Rather than using this, always prefer to use a font style instead.
    public enum Name: String, CaseIterable {
        case regularSlabBold = "AthleticRegularSlab-Bold"
        case regularSlabInline = "AthleticRegularSlab-Inline"

        case tiemposHeadlineBold = "TiemposHeadline-Bold"
        case tiemposHeadlineMedium = "TiemposHeadline-Medium"
        case tiemposHeadlineRegular = "TiemposHeadline-Regular"

        case tiemposTextMedium = "TiemposText-Medium"
        case tiemposTextRegular = "TiemposText-Regular"
        case tiemposTextRegularItalic = "TiemposText-RegularItalic"

        case calibreSemibold = "Calibre-Semibold"
        case calibreMedium = "Calibre-Medium"
        case calibreRegular = "Calibre-Regular"
        case calibreMediumItalic = "Calibre-MediumItalic"
        case calibreRegularItalic = "Calibre-RegularItalic"

        case sohneRegular = "Sohne-Buch"
    }

    public enum DynamicTypeRestrictions {

        /// No restrictions imposed. The font will be scaled to match the users preference regardless of how large or small their preference is.
        case none

        /// Limit the scaled font size increase to the maximum point size provided
        case maxPointSize(CGFloat)

        /// Limit the scaled font size by the given scale factor of it's default size, where 1.0 is equal to the base size & for example 3.0 is 300% of the default size etc.
        case maxScaleFactor(CGFloat)

        /// The default restrictions
        public static let `default`: Self = .maxScaleFactor(2.0)
    }
}

extension Font {

    // MARK: - Styled Fonts

    /// Returns an instance of the font associated with the text style and scaled appropriately for the user's selected content size category.
    /// - Parameters:
    ///   - style: Athletic text style
    ///   - restrictions: Restrictions to impose on the scaling of the font
    ///   - dynamicTypeSize: Overrides system dynamic type size
    /// - Returns: The font for the given style, scaled for the users content size category
    public static func preferredFont(
        for style: AthleticFont.Style,
        restrictions: AthleticFont.DynamicTypeRestrictions = .default,
        dynamicTypeSize: DynamicTypeSize? = nil
    ) -> Font {
        Font(
            UIFont
                .font(for: style)
                .scaled(restrictions: restrictions, dynamicTypeSize: dynamicTypeSize) as CTFont
        )
    }

    /// A feature switch convenience method to return a preferred font if the condition is `true`, else a fixed size font if `false`.
    /// - Parameters:
    ///   - style: Athletic font style
    ///   - restrictions: Retrictions to apply to the dynamic font if enabled
    ///   - condition: Whether to return a preferred font or a fixed size font
    /// - Returns: Font for the requested style
    public static func preferredFont(
        for style: AthleticFont.Style,
        restrictions: AthleticFont.DynamicTypeRestrictions = .default,
        if condition: Bool
    ) -> Font {
        return
            condition
            ? .preferredFont(for: style, restrictions: restrictions)
            : .font(for: style)
    }

    /// Returns an instance of the font associated with the text style
    /// *Think carefully before using this method* because it will have a fixed font size, not scaled to the users content size preference.
    /// Generally prefer to use `preferredFont(for: AthleticFont.Style)`.
    /// - Parameter style: Athletic text style
    /// - Returns: Fix size font for the given style
    public static func font(for style: AthleticFont.Style) -> Font {
        Font(UIFont.font(for: style) as CTFont)
    }

    // MARK: - Named Fonts

    /// Returns an instance of the font scaled appropriately for the user's selected content size category.
    /// - Parameters:
    ///   - name: A font name of an Athletic supported font (not a font style)
    ///   - defaultSize: The default point size of the font, before user font size preference is applied
    ///   - restrictions: Restrictions to impose on the scaling of the font
    ///   - dynamicTypeSize: Overrides system dynamic type size
    /// - Returns: The font for the given name, scaled for the users content size category
    public static func preferredFont(
        name: AthleticFont.Name,
        defaultSize: CGFloat,
        restrictions: AthleticFont.DynamicTypeRestrictions = .default,
        dynamicTypeSize: DynamicTypeSize? = nil
    ) -> Font {
        Font(
            UIFont
                .font(name: name, size: defaultSize)
                .scaled(restrictions: restrictions, dynamicTypeSize: dynamicTypeSize) as CTFont
        )
    }

    /// Returns an instance of the font for the given font name in the given size for SwiftUI
    /// *Think carefully before using this method* because it will have a fixed font size, not scaled to the users content size preference.
    /// Generally prefer to use `preferredFont(name:defaultSize:restrictions:)`.
    /// - Parameters:
    ///   - name: A font name of an Athletic supported font (not a font style)
    ///   - size: Size in points for the font
    /// - Returns: Fix size font in the requested size
    public static func font(name: AthleticFont.Name, size: CGFloat) -> Font {
        Font(UIFont.font(name: name, size: size) as CTFont)
    }

}

extension UIFont {

    // MARK: - Styled Fonts

    /// Returns an instance of the font associated with the text style and scaled appropriately for the user's selected content size category.
    /// - Parameters:
    ///   - style: Athletic text style
    ///   - restrictions: Restrictions to impose on the scaling of the font
    ///   - dynamicTypeSize: Overrides system dynamic type size
    /// - Returns: The font for the given style, scaled for the users content size category
    public static func preferredFont(
        for style: AthleticFont.Style,
        restrictions: AthleticFont.DynamicTypeRestrictions = .default,
        dynamicTypeSize: DynamicTypeSize? = nil
    ) -> UIFont {
        font(for: style).scaled(restrictions: restrictions, dynamicTypeSize: dynamicTypeSize)
    }

    /// Returns an instance of the font associated with the text style
    /// *Think carefully before using this method* because it will have a fixed font size, not scaled to the users content size preference.
    /// Generally prefer to use `preferredFont(for: AthleticFont.Style)`.
    /// - Parameter style: Athletic text style
    /// - Returns: Fix size font for the given style
    public static func font(for style: AthleticFont.Style) -> UIFont {
        style.font
    }

    // MARK: - Named Fonts

    /// Returns an instance of the font scaled appropriately for the user's selected content size category.
    /// - Parameters:
    ///   - name: A font name of an Athletic supported font (not a font style)
    ///   - defaultSize: The default point size of the font, before user font size preference is applied
    ///   - restrictions: Restrictions to impose on the scaling of the font
    /// - Returns: The font for the given name, scaled for the users content size category
    public static func preferredFont(
        name: AthleticFont.Name,
        defaultSize: CGFloat,
        restrictions: AthleticFont.DynamicTypeRestrictions = .default
    ) -> UIFont {
        .font(name: name, size: defaultSize).scaled(restrictions: restrictions)
    }

    /// Returns an instance of the font for the given font name in the given size
    /// *Think carefully before using this method* because it will have a fixed font size, not scaled to the users content size preference.
    /// Generally prefer to use `preferredFont(name:defaultSize:restrictions:)`.
    /// - Parameters:
    ///   - name: A font name of an Athletic supported font (not a font style)
    ///   - size: Size in points for the font
    /// - Returns: Fix size font in the requested size
    public static func font(name: AthleticFont.Name, size: CGFloat) -> UIFont {
        UIFont(name: name.rawValue, size: size)!
    }

}

// MARK: - Internal
extension UIFont {
    /// Returns a scaled version of `self`, limiting the size by any given restrictions
    /// NB: The font must not already be a scaled font.
    /// - Parameters:
    ///   - restrictions: Restrictions to impose when scaling the font
    ///   - dynamicTypeSize: Overrides system dynamic type size
    /// - Returns: Scaled font
    fileprivate func scaled(
        restrictions: AthleticFont.DynamicTypeRestrictions,
        dynamicTypeSize: DynamicTypeSize? = nil
    ) -> UIFont {
        let traitCollection = dynamicTypeSize.map {
            UITraitCollection(preferredContentSizeCategory: UIContentSizeCategory($0))
        }

        switch restrictions {
        case .none:
            return scaled(traitCollection: traitCollection)

        case let .maxPointSize(maxPointSize):
            return scaled(maxPointSize: maxPointSize, traitCollection: traitCollection)

        case let .maxScaleFactor(maxScaleFactor):
            let maxPointSize = pointSize * maxScaleFactor
            return scaled(maxPointSize: maxPointSize, traitCollection: traitCollection)
        }
    }

    /// Returns a scaled version of this font for the users content size category preference setting
    /// NB: The font must not already be a scaled font.
    /// - Parameters:
    ///   - maxPointSize: Maximum point size the font can be scaled to
    ///   - traitCollection: Scaled font will be compatible with it
    /// - Returns: Scaled font
    private func scaled(
        maxPointSize: CGFloat? = nil,
        traitCollection: UITraitCollection? = nil
    ) -> UIFont {
        let style = estimatedTextStyle

        if let maxSize = maxPointSize {
            return style.metrics.scaledFont(
                for: self,
                maximumPointSize: maxSize,
                compatibleWith: traitCollection
            )
        } else {
            return style.metrics.scaledFont(for: self, compatibleWith: traitCollection)
        }
    }

    /// Estimated text style based on the point size.
    /// Style/point size mappings from Apple: https://developer.apple.com/design/human-interface-guidelines/ios/visual-design/typography/
    var estimatedTextStyle: TextStyle {
        switch pointSize {
        case ..<11.5:
            return .caption2
        case 11.5..<12.5:
            return .caption1
        case 12.5...14:
            return .footnote
        case 14..<15.5:
            return .subheadline
        case 15.5..<16.5:
            return .callout
        case 16.5..<19:
            return .body
        case 19..<21:
            return .title3
        case 21..<25:
            return .title2
        case 25..<31:
            return .title1
        case 31...:
            return .largeTitle
        default:
            return .body
        }
    }
}

extension View {
    /// Attach this to any Xcode Preview's view to have custom fonts displayed
    /// Note: Not needed for the actual app
    public func loadCustomFonts() -> some View {
        AthleticUI.registerFonts()
        return self
    }
}
