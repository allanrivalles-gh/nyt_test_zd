//
//  GiftPlan+UI.swift
//  theathletic-ios
//
//  Created by Jan Remes on 17/03/2020.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import AthleticRestNetwork
import Foundation
import SwiftUI
import UIKit

struct GiftMarketingDisplay {
    let name: String
    let originalPrice: String
    let newPrice: String
    let saveAmount: String

    func formattedAsMarkdown() -> LocalizedStringKey {
        return "\(name) - ~\(originalPrice)~ \(newPrice) Save (\(saveAmount))"
    }
}

extension GiftPlan {
    func createMarketingDisplay() -> GiftMarketingDisplay? {
        guard let newPrice = newPrice, let originalPrice = originalPrice else {
            return nil
        }
        let savedAmount = originalPrice - newPrice
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        guard let originalPriceFormatted = formatter.string(from: originalPrice as NSNumber) else {
            return nil
        }
        guard let newPriceFormatted = formatter.string(from: newPrice as NSNumber) else {
            return nil
        }
        formatter.maximumFractionDigits = 0
        guard let savedAmountFormatted = formatter.string(from: savedAmount as NSNumber) else {
            return nil
        }

        return GiftMarketingDisplay(
            name: name,
            originalPrice: originalPriceFormatted,
            newPrice: newPriceFormatted,
            saveAmount: savedAmountFormatted
        )
    }
}
