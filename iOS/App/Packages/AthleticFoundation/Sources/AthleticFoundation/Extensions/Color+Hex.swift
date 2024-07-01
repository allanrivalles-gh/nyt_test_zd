//
//  Color+Hex.swift
//
//
//  Created by Kyle Browning on 5/30/22.
//

import SwiftUI
import UIKit

extension UIColor {
    public var hexString: String {
        var r: CGFloat = 0
        var g: CGFloat = 0
        var b: CGFloat = 0
        var a: CGFloat = 0

        getRed(&r, green: &g, blue: &b, alpha: &a)

        let rgb: Int = (Int)(r * 255) << 16 | (Int)(g * 255) << 8 | (Int)(b * 255) << 0

        return NSString(format: "#%06x", rgb) as String
    }

    public convenience init(hex: String, alpha: CGFloat = 1.0) {
        let rgbValue = rgbValue(fromHex: hex)

        self.init(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: alpha
        )
    }
}

extension Color {
    public init(hex: String, opacity: Double = 1.0) {
        let rgbValue = rgbValue(fromHex: hex)

        self.init(
            red: Double((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: Double((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: Double(rgbValue & 0x0000FF) / 255.0,
            opacity: opacity
        )
    }

    public var hexString: String {
        UIColor(self).hexString
    }
}

extension Color: Codable {
    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let hex = try container.decode(String.self)

        self.init(hex: hex)
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(hexString)
    }
}

private func rgbValue(fromHex hex: String) -> UInt64 {
    var cString = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()

    if cString.hasPrefix("#") {
        cString.removeFirst()
    }

    var rgbValue: UInt64 = 0
    Scanner(string: cString).scanHexInt64(&rgbValue)

    return rgbValue
}
