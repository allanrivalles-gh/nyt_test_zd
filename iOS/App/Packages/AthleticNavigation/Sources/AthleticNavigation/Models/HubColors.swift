//
//  HubColors.swift
//
//
//  Created by Duncan Lau on 22/6/2023.
//

import Foundation
import SwiftUI

public struct HubColors: Codable, Hashable {
    public let background: Color
    public let foreground: Color
    public let selectedTabForeground: Color

    public init(
        background: Color,
        foreground: Color,
        selectedTabForeground: Color
    ) {
        self.background = background
        self.foreground = foreground
        self.selectedTabForeground = selectedTabForeground
    }

    public init(entityColorHex: String?) {
        let backgroundColor =
            entityColorHex.map({ Color(hex: $0) })
            ?? Color.chalk.constant.gray300

        background = backgroundColor
        foreground = Color.highContrastAppearance(
            of: .chalk.dark800,
            forBackgroundColor: backgroundColor
        )
        selectedTabForeground = Color.highContrastAppearance(
            of: .chalk.dark800,
            forBackgroundColor: backgroundColor
        )
    }
}

extension UIStatusBarStyle: Codable {}
