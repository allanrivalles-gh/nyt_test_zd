//
//  PurchaseType.swift
//
//
//  Created by Jason Leyrer on 6/23/23.
//

import Foundation

public enum PurchaseType: String, CaseIterable {
    case annual = "com.theathletic.annual.72"
    case monthly = "com.theathletic.monthly.7"
    case promoPlan1 = "com.theathletic.annual.72.promo.1"
    case promoPlan2 = "com.theathletic.annual.72.promo.2"
    case promoPlan3 = "com.theathletic.annual.72.promo.3"
    case promoPlan4 = "com.theathletic.annual.72.promo.4"

    case introPlan1 = "com.theathletic.annual.72.intro.1"
    case introPlan2 = "com.theathletic.annual.72.intro.2"
    case introPlan3 = "com.theathletic.annual.72.intro.3"
    case introPlan4 = "com.theathletic.annual.72.intro.4"

    case gift3Month = "gift_subscription_3_month_non_renew"
    case gift1Year = "gift_subscription_1_year_non_renew"
    case gift2Year = "gift_subscription_2_year_non_renew"

    case tmobileTuesday = "com.theathletic.annual.72.tmobile"

    public var numberOfMonths: Int {
        switch self {
        case .monthly:
            return 1
        default:
            return 12
        }
    }

    public var textRepresentation: String {
        switch self {
        case .monthly:
            return Strings.subscriptionMonthly.localized
        default:
            return Strings.subscriptionAnnual.localized
        }
    }

    /// Sort order for Plans Screen
    public var order: Int {
        switch self {
        case .monthly:
            return 2
        default:
            return 1
        }
    }
}
