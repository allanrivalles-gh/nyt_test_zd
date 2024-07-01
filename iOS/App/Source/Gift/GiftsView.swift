//
//  GiftsView.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 23/09/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloNetworking
import AthleticApolloTypes
import AthleticFoundation
import AthleticRestNetwork
import AthleticUI
import Combine
import StoreKit
import SwiftUI

struct GiftsView: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var form: GiftsForm
    @StateObject private var viewModel: GiftsViewModel
    @State private var isPickingContactEmail = false
    @State private var isPickingDate = false
    @Binding var choseToGiveAnotherGift: Bool

    init(
        store: Store,
        user: GQL.CustomerDetail,
        network: NetworkModel,
        choseToGiveAnotherGift: Binding<Bool>
    ) {
        _choseToGiveAnotherGift = choseToGiveAnotherGift
        _form = StateObject(
            wrappedValue: GiftsForm(
                user: user
            )
        )
        _viewModel = StateObject(
            wrappedValue: GiftsViewModel(
                store: store,
                network: network
            )
        )
    }

    var body: some View {
        VStack {
            switch viewModel.loadingState {
            case .loading:
                ProgressView()
                    .progressViewStyle(.athletic)
            case .failed:
                Button(Strings.reload.localized) {
                    viewModel.reload()
                }
                .buttonStyle(.core(size: .small, level: .secondary))
            case .loaded(let plans):
                ScrollViewReader { scrollView in
                    ScrollView {
                        VStack(alignment: .leading, spacing: 32) {
                            Image(uiImage: .localizedGiftAsset()!)
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(maxWidth: .infinity)
                                .padding(.horizontal, 12)

                            chooseGiftSection(plans: plans)

                            DividerView()

                            recipientSection

                            DividerView()

                            confirmInfoSection

                            if let plan = viewModel.selectedPlan(in: form) {
                                let isSubmitting = viewModel.isSubmitting
                                Button {
                                    viewModel.submit(form: form)
                                } label: {
                                    Text(Strings.giftActionPay.localized)
                                        .opacity(isSubmitting ? 0 : 1)
                                        .overlay {
                                            ProgressView()
                                                .progressViewStyle(
                                                    .inButton(
                                                        isLoading: isSubmitting
                                                    )
                                                )
                                        }
                                }
                                .buttonStyle(.core(size: .regular, level: .primary))
                                .disabled(isSubmitting)

                                Text(LocalizedStringKey(viewModel.footerTerms(for: plan)))
                                    .fontStyle(.calibreUtility.xs.regular)
                                    .foregroundColor(.chalk.dark400)
                            }
                        }
                        .padding(.horizontal, 18)
                        .padding(.bottom, 32)
                        .onReceive(form.firstInputWithErrorId) { fieldId in
                            withAnimation {
                                scrollView.scrollTo(fieldId)
                            }
                        }
                    }
                }
            default: EmptyView()
            }
        }
        .contactEmailPicker(isPresented: $isPickingContactEmail) { email in
            if let email = email {
                form.recipientEmail = email
            }
        }
        .datePickerSheet(
            Strings.recipientDateTitle.localized,
            isPresented: $isPickingDate,
            selection: $form.deliveryDate
        )
        .confirmationView(
            forState: $viewModel.submitState,
            choseToGiveAnotherGift: $choseToGiveAnotherGift
        ) {
            dismiss()
        }
        .errorAlert(forState: $viewModel.submitState)
        .aboveSafeAreaColor(.chalk.dark100)
        .background(Color.chalk.dark100)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            guard viewModel.loadingState == nil else { return }

            viewModel.reload()
        }
    }

    @ViewBuilder
    private func chooseGiftSection(plans: [GiftsViewModel.Plan]) -> some View {
        Header(
            title: Strings.chooseGift.localized,
            info: Strings.giftPlanHeaderTitle.localized
        )
        VStack(spacing: 12) {
            let _ = DuplicateIDLogger.logDuplicates(in: plans)
            ForEach(plans) { plan in
                SelectableTile(
                    title: plan.name,
                    description: plan.formattedPrice() ?? "",
                    isSelected: plan.id == form.selectedPlanId
                ) {
                    form.selectedPlanId = plan.id
                }
            }
        }
    }

    @ViewBuilder
    private var recipientSection: some View {
        Header(
            title: Strings.giftRecipientTitle.localized,
            info: Strings.giftRecipientHeaderTitle.localized
        )

        LazyVStack(spacing: 24) {
            TextField(
                Strings.recipientNamePlaceholder.localized,
                text: $form.recipientName
            )
            .textFieldStyle(error: form.recipientNameError)
            .id(form.recipientNameFieldId)

            TextEditor(text: $form.recipientNote)
                .frame(height: 75)
                .textEditorStyle(
                    placeholder: form.recipientNote.isEmpty
                        ? Strings.recipientMsgPlaceholder.localized
                        : nil
                )
        }

        Text(Strings.deliveryMethodTitle.localized)
            .fontStyle(.slab.s.bold)
            .foregroundColor(.chalk.dark800)

        VStack(spacing: 12) {
            SelectableTile(
                title: Strings.deliveryPrintTitle.localized,
                description: Strings.deliveryPrintDetail.localized,
                isSelected: form.selectedDeliveryMethod == .print
            ) {
                form.selectedDeliveryMethod = .print
            }
            SelectableTile(
                title: Strings.deliveryEmailTitle.localized,
                description: Strings.deliveryEmailDetail.localized,
                isSelected: form.selectedDeliveryMethod == .email
            ) {
                form.selectedDeliveryMethod = .email
            }
        }

        if form.selectedDeliveryMethod == .email {
            LazyVStack(alignment: .leading, spacing: 24) {
                TextField(
                    Strings.recipientEmailPlaceholder.localized,
                    text: $form.recipientEmail
                )
                .keyboardType(.emailAddress)
                .textFieldStyle(error: form.recipientEmailError)
                .id(form.recipientEmailFieldId)

                Button(Strings.recipientEmailAction.localized) {
                    isPickingContactEmail = true
                }
                .fontStyle(.calibreUtility.s.medium)
                .foregroundColor(.chalk.dark500)

                TextField(
                    Strings.recipientDateTitle.localized,
                    text: .constant(Date.giftDateFormatter.string(from: form.deliveryDate))
                )
                .disabled(true)
                .textFieldStyle()
                .onTapGesture {
                    isPickingDate = true
                }
            }
        }
    }

    @ViewBuilder
    private var confirmInfoSection: some View {
        Header(
            title: Strings.confirmInfoTitle.localized,
            info: Strings.giftSenderHeaderTitle.localized
        )
        LazyVStack(spacing: 24) {
            TextField(Strings.confirmNamePlaceholder.localized, text: $form.senderName)
                .textFieldStyle(error: form.senderNameError)
                .id(form.senderNameFieldId)
            TextField(Strings.confirmEmailPlaceholder.localized, text: $form.senderEmail)
                .keyboardType(.emailAddress)
                .textFieldStyle(error: form.senderEmailError)
                .id(form.senderEmailFieldId)
        }
    }
}

private struct Header: View {
    let title: String
    let info: String

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(title)
                .fontStyle(.slab.m.bold)
                .foregroundColor(.chalk.dark800)
            Text(info)
                .fontStyle(.calibreUtility.l.regular)
                .foregroundColor(.chalk.dark500)
        }
    }
}

private struct SelectableTile: View {
    let title: String
    let description: String
    let isSelected: Bool
    let onSelect: VoidClosure

    var body: some View {
        ZStack(alignment: .topLeading) {
            let rectangle = RoundedRectangle(cornerRadius: 2)
            rectangle
                .fill(
                    isSelected
                        ? Color.chalk.dark800
                        : Color.chalk.dark200
                )
            rectangle
                .strokeBorder(
                    isSelected
                        ? Color.chalk.dark800
                        : Color.chalk.dark300,
                    lineWidth: isSelected ? 2 : 1
                )
            VStack(alignment: .leading, spacing: 6) {
                Text(title)
                    .fontStyle(.slab.s.bold)
                    .foregroundColor(isSelected ? .chalk.dark100 : .chalk.dark800)
                Text(description)
                    .fontStyle(.calibreUtility.l.regular)
                    .foregroundColor(isSelected ? .chalk.dark400 : .chalk.dark500)
            }
            .padding(.horizontal, 20)
            .padding(.top, 26)
            .padding(.bottom, 24)
            if isSelected {
                HStack {
                    Spacer()
                    Image("gift_checkmark")
                        .resizable()
                        .frame(width: 40, height: 40)
                }
                .padding(2)
            }
        }
        .onTapGesture(perform: onSelect)
    }
}

extension View {
    @ViewBuilder
    fileprivate func errorAlert(
        forState state: Binding<GiftsViewModel.SubmitState?>
    ) -> some View {
        let info = state.wrappedValue?.asErrorInfo()
        let isPresented = Binding<Bool>(
            get: { info != nil },
            set: { if !$0 { state.wrappedValue = nil } }
        )
        alert(isPresented: isPresented) {
            Alert(
                title: Text(info?.title ?? ""),
                message: Text(info?.message ?? ""),
                dismissButton: .default(Text(Strings.ok.localized))
            )
        }
    }

    @ViewBuilder
    fileprivate func confirmationView(
        forState state: Binding<GiftsViewModel.SubmitState?>,
        choseToGiveAnotherGift: Binding<Bool>,
        dismiss: @escaping () -> Void
    ) -> some View {
        let info = state.wrappedValue?.asConfirmationInfo()
        let isPresented = Binding<Bool>(
            get: { info != nil },
            set: { if !$0 { state.wrappedValue = nil } }
        )
        fullScreenCover(isPresented: isPresented) {
            NavigationStack {
                if let info = info {
                    GiftConfirmationView(
                        choseToGiveAnotherGift: choseToGiveAnotherGift,
                        payload: info.payload,
                        planMarketingDisplay: info.planMarketingDisplay
                    )
                    .navigationBarTitleDisplayMode(.inline)
                    .onDisappear(perform: dismiss)
                }
            }
        }
    }

    fileprivate func textFieldStyle(
        error: String? = nil
    ) -> some View {
        self
            .fontStyle(.calibreUtility.l.regular)
            .foregroundColor(.chalk.dark800)
            .frame(height: 48)
            .padding(.horizontal, 16)
            .background(hasError: error != nil)
            .errorMessage(error)
    }

    fileprivate func textEditorStyle(
        placeholder: String? = nil,
        error: String? = nil
    ) -> some View {
        self
            .overlay(alignment: .topLeading) {
                if let placeholder = placeholder {
                    Text(placeholder)
                        .fontStyle(.calibreUtility.l.regular)
                        .foregroundColor(.chalk.dark400)
                        .padding(.top, 8)
                        .padding(.leading, 4)
                        .allowsHitTesting(false)
                }
            }
            .fontStyle(.calibreUtility.l.regular)
            .foregroundColor(.chalk.dark800)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .background(hasError: error != nil)
            .errorMessage(error)
    }

    private func errorMessage(_ message: String?) -> some View {
        VStack(alignment: .leading) {
            self
            if let message = message {
                Text(message)
                    .fontStyle(.calibreUtility.xs.regular)
                    .foregroundColor(.chalk.red)
            }
        }
    }

    private func background(hasError: Bool) -> some View {
        self
            .background(
                ZStack {
                    let rectangle = RoundedRectangle(cornerRadius: 2)
                    rectangle
                        .fill(Color.chalk.dark200)
                    rectangle
                        .strokeBorder(
                            Color(
                                hasError
                                    ? .chalk.red
                                    : .chalk.dark300
                            ),
                            lineWidth: 1
                        )
                }
            )
    }
}

extension GiftsViewModel.SubmitState {
    fileprivate struct ErrorInfo {
        let title: String
        let message: String
    }

    fileprivate struct ConfirmationInfo {
        let payload: GiftPurchasePayload
        let planMarketingDisplay: GiftMarketingDisplay?
    }

    fileprivate func asErrorInfo() -> ErrorInfo? {
        if case .error(let title, let message) = self {
            return ErrorInfo(title: title, message: message)
        }
        return nil
    }

    fileprivate func asConfirmationInfo() -> ConfirmationInfo? {
        if case .confirmation(let payload, let planMarketingDisplay) = self {
            return ConfirmationInfo(payload: payload, planMarketingDisplay: planMarketingDisplay)
        }
        return nil
    }
}
