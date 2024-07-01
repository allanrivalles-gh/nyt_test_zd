//
//  GiftService.swift
//
//
//  Created by Kyle Browning on 11/6/19.
//

import Foundation

public struct GiftService {
    public static func getGifts(on network: Network) -> ATHNetworkPublisher<GiftResponse> {
        Service.requestAndDecode(for: NetworkAPIEndpoint.gifts, on: network)
    }

    public static func postGiftPurchasePayload(
        with payload: GiftPurchasePayload,
        on network: Network
    ) -> ATHNetworkPublisher<SimpleResultSuccess> {
        Service.requestAndDecode(
            for: NetworkAPIEndpoint.purchaseGift(payload: payload),
            on: network
        )
    }
}
