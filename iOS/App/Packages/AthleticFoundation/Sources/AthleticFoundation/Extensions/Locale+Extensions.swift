//
//  Locale+Extensions.swift
//
//
//  Created by Kyle Browning on 5/12/22.
//

import Foundation

extension Locale {
    /// EU countries list, used for GDPR
    static public let euCountries: [String] = [
        "AT",  // Austria
        "BE",  // Belgium
        "BG",  // Bulgaria
        "CY",  // Cyprus
        "CZ",  // Czech Republic
        "DE",  // Germany
        "DK",  // Denmark
        "EE",  // Estonia
        "ES",  // Spain
        "FI",  // Finland
        "FR",  // France
        "GB",  // United Kingdom
        "GR",  // Greece
        "HR",  // Croatia
        "HU",  // Hungary
        "IE",  // Ireland
        "IT",  // Italy
        "LT",  // Lithuania
        "LU",  // Luxembourg
        "LV",  // Latvia
        "MT",  // Malta
        "NL",  // Netherlands
        "PL",  // Poland
        "PT",  // Portugal
        "RO",  // Romania
        "SE",  // Sweden
        "SI",  // Slovenia
        "SK",  // Slovakia
        /// the following are recommended by NYTimes Data Governance, for the reasons given in the comments
        "IS",  // Iceland (additional EEA)
        "LI",  // Liechtenstein (additional EEA)
        "NO",  // Norway (additional EEA)
        "CH",  // Switzerland (similar legislation)
        "BB",  // Barbados (similar legislation)
        "BR",  // Brazil (similar legislation)
        "AE",  // United Arab Emirates (similar legislation)
        "BV",  // Bouvet Island (technically uninhabited but Norway)
        "GF",  // French Guiana (France)
        "GI",  // Gibraltar (UK)
        "GP",  // Guadeloupe (France)
        "MQ",  // Martinique (France)
        "YT",  // Mayotte (France)
        "RE",  // Réunion (France)
        "SJ",  // Svalbard & Jan Mayen (Norway)
    ]

    public var isPartOfEU: Bool {
        guard let regionCode = language.region?.identifier else {
            return false
        }
        return Locale.euCountries.contains(regionCode.uppercased())
    }

    public var isUK: Bool {
        language.region?.identifier.uppercased().contains("GB") == true
    }

    public var isUS: Bool {
        guard let regionCode = language.region?.identifier.uppercased() else {
            // if no region information is available, default to US location
            return true
        }

        return regionCode.contains("US")
    }

    public var isNorthAmerica: Bool {
        guard let regionCode = language.region?.identifier.uppercased() else {
            return false
        }
        return regionCode.contains("US") || regionCode.contains("CA")
    }

    public var isCanada: Bool {
        language.region?.identifier.uppercased().contains("CA") == true
    }

    public var isAus: Bool {
        language.region?.identifier.uppercased().contains("AU") == true
    }

    public var formatted: String {
        Locale.current.identifier.replacingOccurrences(of: "_", with: "-")
    }
}
