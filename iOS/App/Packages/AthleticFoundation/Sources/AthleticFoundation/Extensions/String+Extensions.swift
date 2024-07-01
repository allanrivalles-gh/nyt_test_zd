//
//  String+Extensions.swift
//
//
//  Created by Kyle Browning on 5/12/22.
//

import Foundation
import UIKit

extension String {

    public var url: URL? {
        return URL(string: self)
    }

    public var doubleValue: Double {
        return Double(self) ?? 0
    }

    public var intValue: Int {
        return Int(self) ?? 0
    }

    public func isValidEmail() -> Bool {
        let emailRegEx = "^[+\\w\\.-]+@([\\w\\-]+\\.)+[A-Za-z]{2,64}$"
        let emailTest = NSPredicate(format: "SELF MATCHES %@", emailRegEx)
        return emailTest.evaluate(with: self)
    }

    public func customEmailValidation() -> Bool {
        if self.hasSuffix(".con") {
            return false
        }

        let parts = self.split(separator: "@")
        if parts.count >= 2 {
            let last = parts.last!

            if !last.contains(".") {
                return false
            }

            return true

        } else {
            return false
        }
    }

    public static func getAppVersionString() -> String {
        let version: String = .getAppVersionSemver
        let build: String = .getBuildVersion

        var text = "\(version) (\(build)d)"
        #if PRODUCTION
            text = "\(version) (\(build)p)"
        #endif

        return text
    }

    public static func redactionPlaceholder(length: Int) -> String {
        String(Array(repeating: "X", count: length))
    }

    public static var getAppVersionSemver: String {
        guard let dictionary = Bundle.main.infoDictionary,
            let version = dictionary["CFBundleShortVersionString"] as? String
        else {
            return ""
        }
        return version
    }

    public static var getBuildVersion: String {
        guard let dictionary = Bundle.main.infoDictionary,
            let version = dictionary["CFBundleVersion"] as? String
        else {
            return ""
        }
        return version
    }

    public static var platform: String {
        var sysinfo = utsname()
        uname(&sysinfo)  // ignore return value
        return String(
            bytes: Data(bytes: &sysinfo.machine, count: Int(_SYS_NAMELEN)),
            encoding: .ascii
        )!
        .trimmingCharacters(in: .controlCharacters)
    }

    public static let userAgent: String = {
        //eg. iOS/10_1
        let currentDevice = UIDevice.current

        //eg. iPhone5,2
        var sysinfo = utsname()
        uname(&sysinfo)
        let deviceName =
            String(
                bytes: Data(bytes: &sysinfo.machine, count: Int(_SYS_NAMELEN)),
                encoding: .ascii
            )?
            .trimmingCharacters(in: .controlCharacters) ?? ""

        //eg. MyApp/1
        let dictionary = Bundle.main.infoDictionary
        let name = dictionary?["CFBundleName"] as? String ?? ""
        let version = dictionary?["CFBundleShortVersionString"] as? String ?? ""
        let build = dictionary?["CFBundleVersion"] as? String ?? ""
        var versionBuildString = "\(version),(\(build)d)"
        #if PRODUCTION
            versionBuildString = "\(version),(\(build)p)"
        #endif

        //eg. CFNetwork/808.3
        let networkDictionary = Bundle(identifier: "com.apple.CFNetwork")?.infoDictionary
        let networkString = networkDictionary?["CFBundleShortVersionString"] as? String ?? "unknown"

        //eg. Darwin/16.3.0
        let darwinString =
            String(
                bytes: Data(bytes: &sysinfo.release, count: Int(_SYS_NAMELEN)),
                encoding: .ascii
            )?
            .trimmingCharacters(in: .controlCharacters) ?? "unknown"

        return
            "\(name)/\(versionBuildString) \(deviceName) \(currentDevice.systemName)/\(currentDevice.systemVersion) CFNetwork/\(networkString) Darwin/\(darwinString)"
    }()

    public func parseDuration() -> TimeInterval {
        guard !self.isEmpty else {
            return 0
        }

        var interval: Double = 0

        let parts = self.components(separatedBy: ":")
        for (index, part) in parts.reversed().enumerated() {
            interval += (Double(part) ?? 0) * pow(Double(60), Double(index))
        }

        return interval
    }

    public func trimWhitespace() -> String {
        trimmingCharacters(in: .whitespacesAndNewlines)
    }

    public func removingCharacters(in characterSet: CharacterSet) -> String {
        components(separatedBy: characterSet).joined(separator: "")
    }

    public var isNumeric: Bool {
        let badCharacters = CharacterSet.decimalDigits.inverted

        if self.rangeOfCharacter(from: badCharacters) != nil {
            return false
        } else {
            return true
        }
    }

    public var isTime: Bool {
        contains(":") && allSatisfy { $0.isNumber || $0 == ":" }
    }

    public var bool: Bool? {
        switch self.lowercased() {
        case "true", "t", "yes", "y", "1":
            return true
        case "false", "f", "no", "n", "0":
            return false
        default:
            return nil
        }
    }

    /// Appends a string with an optional joiner string
    ///
    /// Use for combining optional strings. If `component` is `nil`, no changes are made to
    /// the receiver. If `component` exists, append it to the receiver using an optional `joiner`
    /// string. If the receiver is an empty string, ignore `joiner` and just return `component`.
    ///
    /// - Parameters:
    ///   - component: the string to append to the receiver
    ///   - joiner: the string to place between the receiver and `component`
    /// - Returns: the newly concatenated string

    public func append(component: String?, joiner: String? = nil) -> String {
        guard let component = component, !component.isEmpty else {
            return self
        }

        var result = self

        if result.isEmpty {
            result = component
        } else if let joiner = joiner {
            result = "\(result)\(joiner)\(component)"
        } else {
            result = "\(result)\(component)"
        }

        return result
    }

    /**
     Gets the true class name without generic information

     Names such as "FeedViewController\<FeedViewModel\>" are reduced
     to "FeedViewController"
     */
    public func sanitizeGenericType() -> String {
        guard let genericStart = range(of: "<")?.lowerBound else {
            return self
        }

        return String(self[..<genericStart])
    }

    /// Returns a string with matching regex replacement pattern
    public func replacingRegex(
        matching pattern: String,
        findingOptions: NSRegularExpression.Options = .caseInsensitive,
        replacingOptions: NSRegularExpression.MatchingOptions = [],
        with template: String
    ) throws -> String {
        let regex = try NSRegularExpression(pattern: pattern, options: findingOptions)
        let range = NSRange(startIndex..., in: self)
        return regex.stringByReplacingMatches(
            in: self,
            options: replacingOptions,
            range: range,
            withTemplate: template
        )
    }

    public var asFormattedMarkdown: AttributedString {
        do {
            return try AttributedString(
                markdown: self,
                options: AttributedString.MarkdownParsingOptions(
                    interpretedSyntax: .inlineOnlyPreservingWhitespace
                )
            )
        } catch {
            return AttributedString(self)
        }
    }
}

// MARK: - Bounding Size

extension NSAttributedString {
    public func height(constrainedWidth width: CGFloat = .greatestFiniteMagnitude) -> CGFloat {
        let constraintRect = CGSize(width: width, height: .greatestFiniteMagnitude)
        let boundingBox = boundingRect(
            with: constraintRect,
            options: .usesLineFragmentOrigin,
            context: nil
        )

        return ceil(boundingBox.height)
    }

    public func width(constrainedHeight height: CGFloat = .greatestFiniteMagnitude) -> CGFloat {
        let constraintRect = CGSize(width: .greatestFiniteMagnitude, height: height)
        let boundingBox = boundingRect(
            with: constraintRect,
            options: .usesLineFragmentOrigin,
            context: nil
        )
        return ceil(boundingBox.width)
    }

    public func numberOfLines(with font: UIFont, width: CGFloat) -> Int {
        let rect = CGSize(width: width, height: CGFloat.greatestFiniteMagnitude)
        let size = self.boundingRect(with: rect, options: .usesLineFragmentOrigin, context: nil)
        return Int(ceil(CGFloat(size.height) / font.lineHeight))
    }
}

extension String {
    public func height(
        with font: UIFont,
        constrainedWidth width: CGFloat = .greatestFiniteMagnitude
    ) -> CGFloat {
        let constraintRect = CGSize(width: width, height: .greatestFiniteMagnitude)
        let boundingBox = self.boundingRect(
            with: constraintRect,
            options: .usesLineFragmentOrigin,
            attributes: [.font: font],
            context: nil
        )
        return ceil(boundingBox.height)
    }

    public func width(
        with font: UIFont,
        constrainedHeight height: CGFloat = .greatestFiniteMagnitude
    ) -> CGFloat {
        let constraintRect = CGSize(width: .greatestFiniteMagnitude, height: height)
        let boundingBox = self.boundingRect(
            with: constraintRect,
            options: .usesLineFragmentOrigin,
            attributes: [.font: font],
            context: nil
        )
        return ceil(boundingBox.width)
    }
}

extension String {
    public static let emDash = "—"
    public static let enDash = "–"
}
