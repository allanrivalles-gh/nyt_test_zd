//
//  Gift.swift
//
//
//  Created by Kyle Browning on 11/6/19.
//

import Foundation

// MARK: - GiftResponse
public struct GiftResponse: Codable {
    public let promotion: GiftPromotion?
    public let headline: String
    public var plans: [GiftPlan]
    public let shirtSizes: [GiftShirtSize]
}

// MARK: - GiftPlan
public struct GiftPlan: Codable, Equatable {
    public let id: Int
    public let appleProductId, googleProductId, name: String
    public let hasShirt, popular: Bool
    public let index: Int
    public var originalPrice: Double?
    public var newPrice: Double?
}

// MARK: - GiftPromotion
public struct GiftPromotion: Codable {
    public let text, name: String
}

// MARK: - ShirtSize
public struct GiftShirtSize: Codable, Equatable {
    public let index: Int
    public let title, value: String
}

// MARK: - GiftPurchasePayload

public struct GiftPurchasePayload: Codable {
    public init(
        deliveryMethod: GiftPurchasePayload.GiftDeliveryMethod,
        recipientName: String,
        recipientEmail: String? = nil,
        addressName: String? = nil,
        addressLine1: String? = nil,
        addressLine2: String? = nil,
        addressCity: String? = nil,
        addressState: String? = nil,
        addressZip: String? = nil,
        addressCountryCode: String? = nil,
        buyerName: String,
        buyerEmail: String,
        giftMessage: String? = nil,
        giftDeliveryDate: String? = nil,
        shirtSize: String? = nil,
        userId: Int,
        appleReceiptToken: String,
        promotion: String? = nil,
        planId: String
    ) {
        self.deliveryMethod = deliveryMethod
        self.recipientName = recipientName
        self.recipientEmail = recipientEmail
        self.addressName = addressName
        self.addressLine1 = addressLine1
        self.addressLine2 = addressLine2
        self.addressCity = addressCity
        self.addressState = addressState
        self.addressZip = addressZip
        self.addressCountryCode = addressCountryCode
        self.buyerName = buyerName
        self.giftMessage = giftMessage
        self.giftDeliveryDate = giftDeliveryDate
        self.shirtSize = shirtSize
        self.userId = userId
        self.appleReceiptToken = appleReceiptToken
        self.promotion = promotion
        self.planId = planId
        self.buyerEmail = buyerEmail
    }

    public enum GiftDeliveryMethod: String, Codable {
        case email, print
    }

    public let deliveryMethod: GiftDeliveryMethod
    public let recipientName: String
    public let recipientEmail: String?
    public let addressName: String?
    public let addressLine1: String?
    public let addressLine2: String?
    public let addressCity: String?
    public let addressState: String?
    public let addressZip: String?
    public let addressCountryCode: String?
    public let buyerName: String
    public let buyerEmail: String
    public let giftMessage: String?
    public let giftDeliveryDate: String?
    public let shirtSize: String?
    public let userId: Int
    public var appleReceiptToken: String
    public let promotion: String?
    public let planId: String

}
