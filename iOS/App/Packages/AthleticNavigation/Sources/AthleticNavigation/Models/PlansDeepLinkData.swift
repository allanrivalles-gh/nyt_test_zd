//
//  PlansDeepLinkData.swift
//
//
//  Created by Jason Leyrer on 6/23/23.
//

import AthleticFoundation
import Foundation

public struct PlansDeepLinkData: Identifiable, Codable, Hashable {
    public enum PlansType: String, Codable, Hashable {
        case introPlan1
        case introPlan2
        case introPlan3
        case introPlan4

        /// Keeping so old deeplinks at least show a plan
        case plans40 = "discount40"
        case plans50 = "discount50"
        case plans60 = "discount60"
        case plans70 = "discount70"

        case offer40 = "offer_get40"
        case offer50 = "offer_get50"
        case offer60 = "offer_get60"
        case offer70 = "offer_get70"

        public var appStoreOfferIdentifier: String {
            switch self {
            case .introPlan1, .plans40:
                return PurchaseType.introPlan1.rawValue
            case .introPlan2, .plans50:
                return PurchaseType.introPlan2.rawValue
            case .introPlan3, .plans60:
                return PurchaseType.introPlan3.rawValue
            case .introPlan4, .plans70:
                return PurchaseType.introPlan4.rawValue
            case .offer40:
                return PurchaseType.promoPlan1.rawValue
            case .offer50:
                return PurchaseType.promoPlan2.rawValue
            case .offer60:
                return PurchaseType.promoPlan3.rawValue
            case .offer70:
                return PurchaseType.promoPlan4.rawValue
            }
        }
    }

    public let type: PlansType
    public let params: [String: String]

    public var discountPercentString: String {
        switch type {
        case .plans40, .offer40, .introPlan1:
            return "40%"
        case .plans50, .offer50, .introPlan2:
            return "50%"
        case .plans60, .offer60, .introPlan3:
            return "60%"
        case .plans70, .offer70, .introPlan4:
            return "70%"
        }
    }

    public var id: String {
        type.rawValue
    }

    public init(type: PlansType, params: [String: String]) {
        self.type = type
        self.params = params
    }

    public static func == (lhs: PlansDeepLinkData, rhs: PlansDeepLinkData) -> Bool {
        lhs.type == rhs.type
            && String(describing: lhs.params) == String(describing: rhs.params)
    }
}
