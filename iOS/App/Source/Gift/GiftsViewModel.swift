//
//  GiftsViewModel.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 28/09/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloNetworking
import AthleticFoundation
import AthleticRestNetwork
import Combine
import StoreKit
import SwiftUI

@MainActor
final class GiftsViewModel: ObservableObject {
    struct Plan: Identifiable {
        let id: Int
        let name: String
        let marketingDisplay: GiftMarketingDisplay?
        let product: SKProduct

        func formattedPrice() -> String? {
            let formatter = NumberFormatter()
            formatter.numberStyle = .currency
            formatter.locale = product.priceLocale
            let price = Double(truncating: product.price).rounded(toPlaces: 2)
            return formatter.string(from: price as NSNumber)
        }
    }

    enum LoadingState {
        case loading
        case failed
        case loaded(plans: [Plan])
    }

    enum SubmitState {
        case submitting
        case error(title: String, message: String)
        case confirmation(
            payload: GiftPurchasePayload,
            planMarketingDisplay: GiftMarketingDisplay?
        )
    }

    private var cancellables = Cancellables()

    var isSubmitting: Bool {
        if case .submitting = submitState { return true }
        return false
    }

    @Published var loadingState: LoadingState?
    @Published var submitState: SubmitState?
    let network: NetworkModel
    let store: Store

    init(
        store: Store,
        network: NetworkModel
    ) {
        self.store = store
        self.network = network
    }

    func selectedPlan(in form: GiftsForm) -> Plan? {
        if case .loaded(let plans) = loadingState {
            return plans.first { $0.id == form.selectedPlanId }
        }
        return nil
    }

    func footerTerms(for plan: Plan) -> String {
        return String(
            format: Strings.giftFooterTerms.localized,
            plan.formattedPrice() ?? "",
            plan.name,
            Global.General.policiesUrl.absoluteString
        )
    }

    func reload() {
        loadingState = .loading
        network.fetchGifts()
            .receive(on: RunLoop.main)
            .sink { [self] result in
                switch result {
                case .failure:
                    loadingState = .failed
                case .success(let response):
                    let plans: [GiftsViewModel.Plan] = response.plans
                        .compactMap { plan in
                            var plan = plan
                            // it is required to ensure new price set
                            // or else the marketingDisplay will be nil
                            // when converting to a view model plan
                            plan.ensureNewPriceSet()
                            return plan.forViewModel(store: store)
                        }
                    loadingState = .loaded(plans: plans)
                }
            }
            .store(in: &cancellables)
    }

    func submit(form: GiftsForm) {
        guard form.validateFields() else {
            return
        }

        // reaching this condition is not an exception, it is an error
        // so we should not try to handle it
        guard let plan = selectedPlan(in: form) else {
            fatalError("Can not submit while there is no selected plan.")
        }

        Analytics.track(
            event: .init(
                verb: .click,
                view: .gift,
                element: .planSelection,
                objectType: .productId,
                objectIdentifier: plan.product.productIdentifier
            )
        )

        submitState = .submitting
        store.buyProduct(plan.product) { [self] result in
            switch result {
            case .failure(let error):
                submitState = .error(
                    title: Strings.purchaseError.localized,
                    message: error.localizedDescription
                )
            case .success:
                let receiptString = try? Bundle.main.appStoreReceiptBase64()
                let metaBlob = AnalyticsEvent.MetaBlob(
                    locale: Locale.current.formatted,
                    deliveryMethod: form.selectedDeliveryMethod.rawValue
                )
                Analytics.track(
                    event: .init(
                        verb: .giftCheckoutSuccess,
                        view: .empty,
                        objectType: .productId,
                        objectIdentifier: plan.product.productIdentifier,
                        metaBlob: metaBlob
                    )
                )
                onPaymentCompleted(
                    form: form,
                    plan: plan,
                    receiptString: receiptString ?? ""
                )
            }
        }
    }

    private func onPaymentCompleted(
        form: GiftsForm,
        plan: Plan,
        receiptString: String
    ) {
        let payload = form.createPayload(
            receiptString: receiptString,
            planId: String(plan.id)
        )

        network.purchaseGift(payload: payload)
            .receive(on: RunLoop.main)
            .sink { [self] result in
                switch result {
                case .failure(let error):
                    submitState = .error(
                        title: Strings.purchaseError.localized,
                        message: error.localizedDescription
                    )
                case .success:
                    submitState = .confirmation(
                        payload: payload,
                        planMarketingDisplay: plan.marketingDisplay
                    )
                }
            }
            .store(in: &cancellables)
    }
}

extension GiftPlan {
    fileprivate mutating func ensureNewPriceSet() {
        newPrice = newPrice ?? originalPrice
    }

    fileprivate func forViewModel(store: Store) -> GiftsViewModel.Plan? {
        let purchaseOption = store.purchaseOption(for: appleProductId)
        guard let product = purchaseOption?.product else {
            return nil
        }
        return .init(
            id: id,
            name: name,
            marketingDisplay: createMarketingDisplay(),
            product: product
        )
    }
}
