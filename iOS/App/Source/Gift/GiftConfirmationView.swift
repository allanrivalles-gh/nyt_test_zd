//
//  GiftConfirmationView.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 29/09/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticRestNetwork
import AthleticUI
import SwiftUI

struct GiftConfirmationView: View {
    @Environment(\.dismiss) private var dismiss
    @Binding var choseToGiveAnotherGift: Bool
    let payload: GiftPurchasePayload
    let planMarketingDisplay: GiftMarketingDisplay?

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                VStack(alignment: .leading, spacing: 24) {
                    Image(uiImage: .localizedGiftAsset()!)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(maxWidth: .infinity)
                        .padding(.horizontal, 12)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .shadow(
                            color: .chalk.dark800.opacity(0.5),
                            radius: 10
                        )
                    Text(Strings.giftThankTitle.localized)
                        .fontStyle(.slab.m.bold)
                        .foregroundColor(.chalk.dark800)
                }
                .padding(.horizontal, 8)
                .padding(.bottom, 32)
                VStack(alignment: .leading, spacing: 8) {
                    Text(Strings.giftThankRecipientTitle.localized)
                        .fontStyle(.slab.s.bold)
                        .foregroundColor(.chalk.dark800)
                    Text(payload.recipientName)
                        .fontStyle(.calibreUtility.l.regular)
                        .foregroundColor(.chalk.dark500)
                    if let recipientEmail = payload.recipientEmail,
                        payload.deliveryMethod == .email
                    {
                        Text(recipientEmail)
                            .fontStyle(.calibreUtility.l.regular)
                            .foregroundColor(.chalk.dark500)
                    }
                }
                .padding(.horizontal, 8)
                .padding(.bottom, 32)
                DividerView()
                    .padding(.bottom, 8)
                VStack(alignment: .leading, spacing: 8) {
                    Text(Strings.giftThankPlanTitle.localized)
                        .fontStyle(.slab.s.bold)
                        .foregroundColor(.chalk.dark800)
                    if let planMarketingDisplay = planMarketingDisplay {
                        Text(planMarketingDisplay.formattedAsMarkdown())
                            .fontStyle(.calibreUtility.l.regular)
                            .foregroundColor(.chalk.dark500)
                    }
                }
                .padding(.horizontal, 8)
                .padding(.bottom, 32)
                DividerView()
                    .padding(.bottom, 8)
                VStack(alignment: .leading, spacing: 8) {
                    Text(Strings.giftThankDeliveryTitle.localized)
                        .fontStyle(.slab.s.bold)
                        .foregroundColor(.chalk.dark800)
                    Text(payload.deliveryMethod.rawValue.capitalized)
                        .fontStyle(.calibreUtility.l.regular)
                        .foregroundColor(.chalk.dark500)
                }
                .padding(.horizontal, 8)
                .padding(.bottom, 32)
                DividerView()
                    .padding(.bottom, 32)
                HStack {
                    Spacer()
                    Button(Strings.giftThankAnother.localized) {
                        choseToGiveAnotherGift = true
                        dismiss()
                    }
                    .foregroundColor(.chalk.red)
                    Spacer()
                }
                .padding(.vertical, 32)
            }
            .padding(.horizontal, 16)
            .padding(.top, 16)
            .padding(.bottom, 32)
        }
        .navigationBarDefaultBackgroundColor()
        .navigationBarBackButtonHidden(true)
        .navigationTitle(Strings.thanksTitle.localized)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button {
                    dismiss()
                } label: {
                    Image(systemName: "xmark")
                        .foregroundColor(.chalk.dark800)
                }
            }
        }
    }
}
