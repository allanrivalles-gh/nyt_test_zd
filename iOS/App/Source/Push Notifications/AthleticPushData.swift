//
//  AthleticPushData.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/19/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import Foundation

// MARK: - AthleticPushData
struct AthleticPushData: PushNotificationProviding {
    static let jsonDecoder: JSONDecoder = JSONDecoder()

    let aps: Aps?
    let url: String?
    let athletic: AthleticPushMetaData
    var source: String { "athletic_push" }

    init(decoding userInfo: [AnyHashable: Any]) throws {
        let data = try JSONSerialization.data(withJSONObject: userInfo, options: .prettyPrinted)
        self = try AthleticPushData.jsonDecoder.decode(AthleticPushData.self, from: data)
    }

    var isGhostPush: Bool { athletic.isGhostPush }

    var analyticCampaignData: AnalyticCampaignData {
        AnalyticCampaignData(
            source: source,
            isGhostPush: athletic.isGhostPush,
            url: url,
            campaignId: athletic.campaignID?.string
        )
    }

    enum CodingKeys: CodingKey {
        case aps
        case url
        case athletic
        case source
    }

    init(from decoder: Decoder) throws {
        let container: KeyedDecodingContainer<AthleticPushData.CodingKeys> = try decoder.container(
            keyedBy: AthleticPushData.CodingKeys.self
        )

        aps = try container.decodeIfPresent(Aps.self, forKey: AthleticPushData.CodingKeys.aps)
        url = try container.decodeIfPresent(String.self, forKey: AthleticPushData.CodingKeys.url)
        athletic = try container.decode(
            AthleticPushMetaData.self,
            forKey: AthleticPushData.CodingKeys.athletic
        )
    }

    func encode(to encoder: Encoder) throws {
        var container: KeyedEncodingContainer<AthleticPushData.CodingKeys> = encoder.container(
            keyedBy: AthleticPushData.CodingKeys.self
        )

        try container.encodeIfPresent(aps, forKey: CodingKeys.aps)
        try container.encodeIfPresent(url, forKey: CodingKeys.url)
        try container.encode(athletic, forKey: CodingKeys.athletic)
        try container.encode(source, forKey: CodingKeys.source)
    }
}

// MARK: - AthleticPushMetaData
struct AthleticPushMetaData: Codable {
    let campaignID: String?
    let isGhostPush: Bool
    let source: String = "athletic_push"

    enum CodingKeys: String, CodingKey {
        case isGhostPush
        case campaignID = "campaignId"
        case source
    }
}
