//
//  GiftsForm.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 28/09/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticRestNetwork
import Combine
import Foundation

@MainActor
final class GiftsForm: ObservableObject, TextValidator {
    @Published private(set) var recipientNameError: String?
    @Published private(set) var recipientEmailError: String?
    @Published private(set) var senderNameError: String?
    @Published private(set) var senderEmailError: String?
    @Published var selectedPlanId: Int?
    @Published var selectedDeliveryMethod: GiftPurchasePayload.GiftDeliveryMethod = .email
    @Published var recipientName = ""
    @Published var recipientNote = ""
    @Published var recipientEmail = ""
    @Published var deliveryDate = Date()
    @Published var senderName = ""
    @Published var senderEmail = ""

    let recipientNameFieldId = UUID()
    let recipientEmailFieldId = UUID()
    let senderNameFieldId = UUID()
    let senderEmailFieldId = UUID()
    let firstInputWithErrorId = PassthroughSubject<UUID, Never>()
    let user: GQL.CustomerDetail

    init(user: GQL.CustomerDetail) {
        self.user = user
        senderName = user.name
        senderEmail = user.email
    }

    func validateFields() -> Bool {
        self.recipientNameError = nil
        self.recipientEmailError = nil
        self.senderNameError = nil
        self.senderEmailError = nil

        var firstFieldWithErrorId: UUID?

        let recipientNameError = validateText(
            text: recipientName,
            validator: .name
        )
        if let recipientNameError = recipientNameError {
            firstFieldWithErrorId = firstFieldWithErrorId ?? recipientNameFieldId
            let field = Strings.recipientNamePlaceholder.localized
            let message = recipientNameError.errorMessage
            self.recipientNameError = "\(field) \(message)"
        }

        if selectedDeliveryMethod == .email {
            let recipientEmailError = validateText(
                text: recipientEmail,
                validator: .email
            )
            if let recipientEmailError = recipientEmailError {
                firstFieldWithErrorId = firstFieldWithErrorId ?? recipientEmailFieldId
                let field = Strings.recipientEmailPlaceholder.localized
                let message = recipientEmailError.errorMessage
                self.recipientEmailError = "\(field) \(message)"
            }
        }

        let senderNameError = validateText(
            text: senderName,
            validator: .name
        )
        if let senderNameError = senderNameError {
            firstFieldWithErrorId = firstFieldWithErrorId ?? senderNameFieldId
            let field = Strings.confirmNamePlaceholder.localized
            let message = senderNameError.errorMessage
            self.senderNameError = "\(field) \(message)"
        }

        let senderEmailError = validateText(
            text: senderEmail,
            validator: .email
        )
        if let senderEmailError = senderEmailError {
            firstFieldWithErrorId = firstFieldWithErrorId ?? senderEmailFieldId
            let field = Strings.confirmEmailPlaceholder.localized
            let message = senderEmailError.errorMessage
            self.senderEmailError = "\(field) \(message)"
        }

        if let firstFieldWithErrorId = firstFieldWithErrorId {
            firstInputWithErrorId.send(firstFieldWithErrorId)
            return false
        }

        return true
    }

    func createPayload(receiptString: String, planId: String) -> GiftPurchasePayload {
        let dateString = Date.userEndDateShortFormatter.string(from: deliveryDate)
        return GiftPurchasePayload(
            deliveryMethod: selectedDeliveryMethod,
            recipientName: recipientName,
            recipientEmail: recipientEmail,
            addressName: nil,
            addressLine1: nil,
            addressLine2: nil,
            addressCity: nil,
            addressState: nil,
            addressZip: nil,
            addressCountryCode: nil,
            buyerName: senderName,
            buyerEmail: senderEmail,
            giftMessage: recipientNote,
            giftDeliveryDate: dateString,
            shirtSize: nil,
            userId: user.id.intValue,
            appleReceiptToken: receiptString,
            promotion: nil,
            planId: planId
        )
    }
}
