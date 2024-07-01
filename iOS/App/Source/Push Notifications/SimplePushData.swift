//
//  SimplePushData.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/19/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import Foundation

// MARK: - SimplePushData
struct SimplePushData: PushNotificationProviding {
    static let jsonDecoder: JSONDecoder = JSONDecoder()

    let aps: Aps?
    let url: String?
    var source: String { "in_app_push" }

    init(decoding userInfo: [AnyHashable: Any]) throws {
        let data = try JSONSerialization.data(withJSONObject: userInfo, options: .prettyPrinted)
        self = try SimplePushData.jsonDecoder.decode(SimplePushData.self, from: data)
    }

    var analyticCampaignData: AnalyticCampaignData {
        AnalyticCampaignData(
            source: source,
            isGhostPush: false,
            url: url
        )
    }
}
