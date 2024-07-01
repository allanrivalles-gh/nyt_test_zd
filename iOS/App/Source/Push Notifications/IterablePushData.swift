//
//  IterablePushData.swift
//  theathletic-ios
//
//  Created by Jan Remes on 26/02/2020.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticStorage
import Foundation

// MARK: - IterablePushData
struct IterablePushData: PushNotificationProviding {
    static let jsonDecoder: JSONDecoder = JSONDecoder()

    let aps: Aps?
    let url: String?
    let itbl: IterablePushMetaData
    var source: String { "iterable_push" }

    init(decoding userInfo: [AnyHashable: Any]) throws {
        let data = try JSONSerialization.data(withJSONObject: userInfo, options: .prettyPrinted)
        self = try IterablePushData.jsonDecoder.decode(IterablePushData.self, from: data)
    }

    var analyticCampaignData: AnalyticCampaignData {
        AnalyticCampaignData(
            source: source,
            isGhostPush: isGhostPush,
            url: url,
            campaignId: itbl.campaignId?.string,
            templateId: itbl.templateId?.string,
            messageId: itbl.messageId,
            notificationType: itbl.defaultAction?.type
        )
    }

    var isGhostPush: Bool { itbl.isGhostPush ?? false }

    enum CodingKeys: CodingKey {
        case aps
        case url
        case itbl
        case source
    }

    init(from decoder: Decoder) throws {
        let container: KeyedDecodingContainer<IterablePushData.CodingKeys> = try decoder.container(
            keyedBy: IterablePushData.CodingKeys.self
        )

        aps = try container.decodeIfPresent(Aps.self, forKey: IterablePushData.CodingKeys.aps)
        url = try container.decodeIfPresent(String.self, forKey: IterablePushData.CodingKeys.url)
        itbl = try container.decode(
            IterablePushMetaData.self,
            forKey: IterablePushData.CodingKeys.itbl
        )
    }

    func encode(to encoder: Encoder) throws {
        var container: KeyedEncodingContainer<IterablePushData.CodingKeys> = encoder.container(
            keyedBy: IterablePushData.CodingKeys.self
        )

        try container.encodeIfPresent(aps, forKey: CodingKeys.aps)
        try container.encodeIfPresent(url, forKey: CodingKeys.url)
        try container.encode(itbl, forKey: CodingKeys.itbl)
        try container.encode(source, forKey: CodingKeys.source)
    }
}

// MARK: - Itbl
struct IterablePushMetaData: Codable {
    let attachmentURL: String?
    let defaultAction: DefaultAction?
    let isGhostPush: Bool?
    let campaignId: Int?
    let messageId: String?
    let templateId: Int?

    let source: String = "iterable_push"

    enum CodingKeys: String, CodingKey {
        case attachmentURL = "attachment-url"
        case defaultAction, isGhostPush
        case messageId = "messageId"
        case campaignId = "campaignId"
        case templateId = "templateId"
        case source
    }
}

// MARK: - DefaultAction
struct DefaultAction: Codable {
    let data: String
    let type: String
}
