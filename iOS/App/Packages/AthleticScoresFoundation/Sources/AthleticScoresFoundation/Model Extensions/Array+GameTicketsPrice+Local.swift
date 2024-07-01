//
//  Array+GameTicketsPrice+Local.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 4/7/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

extension Array where Element == GQL.GameTicketsPrice {

    public func localeBestMatch(locale: Locale = .current) -> Element? {
        guard !isEmpty else {
            return nil
        }

        let preferredCurrency: GQL.GameTicketsCurrency
        if locale.isUS {
            preferredCurrency = .usd
        } else if locale.isUK {
            preferredCurrency = .__unknown("gbp")
        } else if locale.isAus {
            preferredCurrency = .__unknown("aud")
        } else if locale.isPartOfEU {
            preferredCurrency = .__unknown("eur")
        } else {
            preferredCurrency = .usd
        }

        let fallbackCurrency: GQL.GameTicketsCurrency = .usd

        /// Try to find the price in the user's preferred currency.
        /// If there isn't one use the fallback (USD). Failing that, use which ever is the first in the array.
        return
            first(where: { $0.currency.rawValue == preferredCurrency.rawValue })
            ?? first(where: { $0.currency.rawValue == fallbackCurrency.rawValue })
            ?? self[0]
    }
}
