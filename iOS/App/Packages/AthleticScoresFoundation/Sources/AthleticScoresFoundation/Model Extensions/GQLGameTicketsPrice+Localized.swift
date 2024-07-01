//
//  GQLGameTicketsPrice+Localized.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 4/7/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

extension GQL.GameTicketsPrice {

    public func localized(locale: Locale = .current) -> String {
        let localCurrency: GQL.GameTicketsCurrency?
        if locale.isUS {
            localCurrency = .usd
        } else if locale.isUK {
            localCurrency = .__unknown("gbp")
        } else if locale.isAus {
            localCurrency = .__unknown("aud")
        } else if locale.isPartOfEU {
            localCurrency = .__unknown("eur")
        } else {
            localCurrency = nil
        }

        let priceInteger = Int(amount)
        let priceUnits = NumberFormatter.thousands.string(for: priceInteger) ?? String(priceInteger)

        /// If the currency is the same as the user's local currency or it has a unique symbol, show the symbol, otherwise show the currency code.
        let showSymbolIfPossible =
            currency.rawValue == localCurrency?.rawValue
            || currency.hasUniqueSymbol

        if showSymbolIfPossible, let currencySymbol = currency.symbol {
            return "\(currencySymbol)\(priceUnits)"
        } else {
            return "\(priceUnits) \(currency.rawValue.uppercased())"
        }
    }

}

extension GQL.GameTicketsCurrency {
    fileprivate var hasUniqueSymbol: Bool {
        switch self {
        case .usd:
            return false
        default:
            return true
        }
    }

    fileprivate var symbol: String? {
        switch self {
        case .usd:
            return "$"
        case .__unknown("gbp"):
            return "£"
        case .__unknown("aud"):
            return "A$"
        case .__unknown("eur"):
            return "€"
        case .__unknown:
            return nil
        }
    }
}
