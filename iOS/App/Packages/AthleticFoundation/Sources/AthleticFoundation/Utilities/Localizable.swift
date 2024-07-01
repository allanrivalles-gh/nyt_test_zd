//
//  Localizable.swift
//
//
//  Created by Kyle Browning on 7/13/22.
//

import Foundation

public protocol Localizable {
    var localized: String { get }
    var bundle: Bundle { get }
    var baseFilename: String { get }
}

extension Localizable where Self: RawRepresentable, Self.RawValue == String {

    /// Returns the localized string for this enum case by searching for the string in the following order until it finds a value.
    /// 1. Find the language targeted string in the current package.
    /// 2. Find the default string in the current package.
    /// 3. Return the key as the localized string
    public var localized: String {
        localized(bundle: bundle)
    }

    private var localizedStringKey: String { rawValue }

    private func localized(bundle: Bundle) -> String {
        /// Try to get the value specific to the users regional settings
        let defaultValue = NSLocalizedString(
            localizedStringKey,
            bundle: bundle,
            comment: localizedStringKey
        )

        guard defaultValue == localizedStringKey else {
            /// If we found a value that wasn't the same as the key, return it
            return defaultValue
        }

        guard
            let baseLocalizationFile = bundle.path(forResource: baseFilename, ofType: "lproj"),
            let baseLocalizationBundle = Bundle(path: baseLocalizationFile)
        else {
            return defaultValue
        }

        /// If there's no regional specific version, return the default one (US) for this bundle
        let fallbackValue = baseLocalizationBundle.localizedString(
            forKey: localizedStringKey,
            value: nil,
            table: nil
        )
        return fallbackValue
    }
}
