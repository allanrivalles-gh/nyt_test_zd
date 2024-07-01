//
//  Color+Theme.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 29/7/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI
import UIKit

// MARK: - Design System Colors
// These colors are part of the approved design system

extension UIColor {
    public struct Chalk {
        public struct Constants {

            // MARK: - Grays
            public let gray100 = UIColor.color(forName: "gray100")
            public let gray200 = UIColor.color(forName: "gray200")
            public let gray300 = UIColor.color(forName: "gray300")
            public let gray400 = UIColor.color(forName: "gray400")
            public let gray500 = UIColor.color(forName: "gray500")
            public let gray600 = UIColor.color(forName: "gray600")
            public let gray700 = UIColor.color(forName: "gray700")
            public let gray800 = UIColor.color(forName: "gray800")

            // MARK: - Colors
            public let red100 = UIColor.color(forName: "red100")
            public let red800 = UIColor.color(forName: "red800")
            public let yellow100 = UIColor.color(forName: "yellow100")
            public let yellow800 = UIColor.color(forName: "yellow800")
            public let green100 = UIColor.color(forName: "green100")
            public let green800 = UIColor.color(forName: "green800")
            public let blue100 = UIColor.color(forName: "blue100")
            public let blue800 = UIColor.color(forName: "blue800")
            public let purple100 = UIColor.color(forName: "purple100")
            public let purple800 = UIColor.color(forName: "purple800")

            // MARK: - Semantic
            public let link100 = UIColor.color(forName: "link100")
            public let link800 = UIColor.color(forName: "link800")

            // MARK: - User
            public let purpleUser = UIColor.color(forName: "purpleUser")
            public let navyUser = UIColor.color(forName: "navyUser")
            public let blueUser = UIColor.color(forName: "blueUser")
            public let turquoiseUser = UIColor.color(forName: "turquoiseUser")
            public let greenUser = UIColor.color(forName: "greenUser")
            public let yellowUser = UIColor.color(forName: "yellowUser")
            public let orangeUser = UIColor.color(forName: "orangeUser")
            public let redUser = UIColor.color(forName: "redUser")
            public let maroonUser = UIColor.color(forName: "maroonUser")
            public let grayUser = UIColor.color(forName: "grayUser")
        }

        public let constant = Constants()

        // MARK: - Grays
        public let dark100 = UIColor(
            dark: .chalk.constant.gray100,
            light: .chalk.constant.gray700
        )
        public let dark200 = UIColor(
            dark: .chalk.constant.gray200,
            light: .chalk.constant.gray800
        )
        public let dark300 = UIColor(
            dark: .chalk.constant.gray300,
            light: .chalk.constant.gray700
        )
        public let dark400 = UIColor(
            dark: .chalk.constant.gray400,
            light: .chalk.constant.gray500
        )
        public let dark500 = UIColor(
            dark: .chalk.constant.gray500,
            light: .chalk.constant.gray400
        )
        public let dark600 = UIColor(
            dark: .chalk.constant.gray600,
            light: .chalk.constant.gray300
        )
        public let dark700 = UIColor(
            dark: .chalk.constant.gray700,
            light: .chalk.constant.gray200
        )
        public let dark800 = UIColor(
            dark: .chalk.constant.gray800,
            light: .chalk.constant.gray100
        )

        // MARK: - Colors
        public let red = UIColor(
            dark: .chalk.constant.red800,
            light: .chalk.constant.red100
        )
        public let yellow = UIColor(
            dark: .chalk.constant.yellow800,
            light: .chalk.constant.yellow100
        )
        public let green = UIColor(
            dark: .chalk.constant.green800,
            light: .chalk.constant.green100
        )
        public let blue = UIColor(
            dark: .chalk.constant.blue800,
            light: .chalk.constant.blue100
        )
        public let purple = UIColor(
            dark: .chalk.constant.purple800,
            light: .chalk.constant.purple100
        )

        // MARK: - Semantic
        public let link = UIColor(
            dark: .chalk.constant.link800,
            light: .chalk.constant.link100
        )
    }

    public static let chalk = Chalk()

    private static func color(forName name: String) -> UIColor {
        UIColor(named: name, in: .athleticUI, compatibleWith: nil)!
    }
}

extension Color {
    public struct Chalk {
        public struct Constants {

            // MARK: - Grays
            public let gray100 = Color(uiColor: .chalk.constant.gray100)
            public let gray200 = Color(uiColor: .chalk.constant.gray200)
            public let gray300 = Color(uiColor: .chalk.constant.gray300)
            public let gray400 = Color(uiColor: .chalk.constant.gray400)
            public let gray500 = Color(uiColor: .chalk.constant.gray500)
            public let gray600 = Color(uiColor: .chalk.constant.gray600)
            public let gray700 = Color(uiColor: .chalk.constant.gray700)
            public let gray800 = Color(uiColor: .chalk.constant.gray800)

            // MARK: - Colors
            public let red100 = Color(uiColor: .chalk.constant.red100)
            public let red800 = Color(uiColor: .chalk.constant.red800)
            public let yellow100 = Color(uiColor: .chalk.constant.yellow100)
            public let yellow800 = Color(uiColor: .chalk.constant.yellow800)
            public let green100 = Color(uiColor: .chalk.constant.green100)
            public let green800 = Color(uiColor: .chalk.constant.green800)
            public let blue100 = Color(uiColor: .chalk.constant.blue100)
            public let blue800 = Color(uiColor: .chalk.constant.blue800)
            public let purple100 = Color(uiColor: .chalk.constant.purple100)
            public let purple800 = Color(uiColor: .chalk.constant.purple800)

            // MARK: - Semantic
            public let link100 = Color(uiColor: .chalk.constant.link100)
            public let link800 = Color(uiColor: .chalk.constant.link800)

            // MARK: - User
            public let purpleUser = Color(uiColor: .chalk.constant.purpleUser)
            public let navyUser = Color(uiColor: .chalk.constant.navyUser)
            public let blueUser = Color(uiColor: .chalk.constant.blueUser)
            public let turquoiseUser = Color(uiColor: .chalk.constant.turquoiseUser)
            public let greenUser = Color(uiColor: .chalk.constant.greenUser)
            public let yellowUser = Color(uiColor: .chalk.constant.yellowUser)
            public let orangeUser = Color(uiColor: .chalk.constant.orangeUser)
            public let redUser = Color(uiColor: .chalk.constant.redUser)
            public let maroonUser = Color(uiColor: .chalk.constant.maroonUser)
            public let grayUser = Color(uiColor: .chalk.constant.grayUser)
        }

        public let constant = Constants()

        // MARK: - Grays
        public let dark100 = Color(uiColor: .chalk.dark100)
        public let dark200 = Color(uiColor: .chalk.dark200)
        public let dark300 = Color(uiColor: .chalk.dark300)
        public let dark400 = Color(uiColor: .chalk.dark400)
        public let dark500 = Color(uiColor: .chalk.dark500)
        public let dark600 = Color(uiColor: .chalk.dark600)
        public let dark700 = Color(uiColor: .chalk.dark700)
        public let dark800 = Color(uiColor: .chalk.dark800)

        // MARK: - Colors
        public let red = Color(uiColor: .chalk.red)
        public let yellow = Color(uiColor: .chalk.yellow)
        public let green = Color(uiColor: .chalk.green)
        public let blue = Color(uiColor: .chalk.blue)
        public let purple = Color(uiColor: .chalk.purple)

        // MARK: - Semantic
        public let link = Color(uiColor: .chalk.link)
    }

    public static let chalk = Chalk()
}

// MARK: - Forced color variants

// UIKit
extension UIColor {
    public var darkAppearance: UIColor {
        resolvedColor(with: UITraitCollection(userInterfaceStyle: .dark))
    }

    public var lightAppearance: UIColor {
        resolvedColor(with: UITraitCollection(userInterfaceStyle: .light))
    }
}

extension UIColor {
    public convenience init(
        dark darkModeColor: @escaping @autoclosure () -> UIColor,
        light lightModeColor: @escaping @autoclosure () -> UIColor
    ) {
        self.init { traitCollection in
            switch traitCollection.userInterfaceStyle {
            case .light:
                return lightModeColor()
            case .dark, .unspecified:
                return darkModeColor()
            @unknown default:
                return darkModeColor()
            }
        }
    }
}

extension Color {
    public init(
        dark darkModeColor: @escaping @autoclosure () -> Color,
        light lightModeColor: @escaping @autoclosure () -> Color
    ) {
        self.init(
            uiColor: UIColor(
                dark: UIColor(darkModeColor()),
                light: UIColor(lightModeColor())
            )
        )
    }
}

/// SwiftUI
///
/// SwiftUI doesn't support returning a `Color` instance in a given color scheme, instead we set the color scheme on the view.
/// This setting propagates to any subviews of the given view.
extension View {
    public func darkScheme() -> some View {
        environment(\.colorScheme, .dark)
    }

    public func darkScheme(isEnabled: Bool) -> some View {
        modifier(OptionalColorSchemeModifier(scheme: .dark, enabled: isEnabled))
    }

    public func lightScheme() -> some View {
        environment(\.colorScheme, .light)
    }

    public func lightScheme(isEnabled: Bool) -> some View {
        modifier(OptionalColorSchemeModifier(scheme: .light, enabled: isEnabled))
    }
}

// this is required to avoid modifying the tree which otherwise messes with the animations
private struct OptionalColorSchemeModifier: ViewModifier {
    @Environment(\.colorScheme) private var currentScheme
    let scheme: ColorScheme
    let enabled: Bool

    func body(content: Content) -> some View {
        content
            .environment(\.colorScheme, enabled ? scheme : currentScheme)
    }
}
