//
//  AnalyticsRequiredValues.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 2/4/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

public protocol AnalyticsRequiredValues: Codable {
    var userIdentifier: Int { get }
    var deviceIdentifier: String { get }
    var isSubscriber: Bool { get }
    var platform: String { get }
    var userAgent: String { get }
    var sessionIdentifier: String { get }
    var locale: String { get }
    var browserVersion: String { get }
    var browser: String { get }
    var isBeta: Bool { get }
    var source: String? { get }
}

public struct AnalyticCampaignData: Codable, Equatable {
    public let source: String
    public let isGhostPush: Bool
    public let url: String?
    public let campaignId: String?
    public let templateId: String?
    public let messageId: String?
    public let notificationType: String?

    public init(
        source: String,
        isGhostPush: Bool,
        url: String? = nil,
        campaignId: String? = nil,
        templateId: String? = nil,
        messageId: String? = nil,
        notificationType: String? = nil
    ) {
        self.source = source
        self.isGhostPush = isGhostPush
        self.url = url
        self.campaignId = campaignId
        self.templateId = templateId
        self.messageId = messageId
        self.notificationType = notificationType
    }
}

public struct PreviewAnalyticDefaults: Codable, Equatable, AnalyticsRequiredValues {
    public let userIdentifier: Int
    public let deviceIdentifier: String
    public let isSubscriber: Bool
    public let platform: String
    public let userAgent: String
    public let sessionIdentifier: String
    public let locale: String
    public let browserVersion: String
    public let browser: String
    public let source: String?
    public let isBeta: Bool
}

extension PreviewAnalyticDefaults {
    public init() {
        self = .init(
            userIdentifier: 2_822_900,
            deviceIdentifier: "123",
            isSubscriber: true,
            platform: "ios",
            userAgent: "my-agent",
            sessionIdentifier: "123",
            locale: "en/us",
            browserVersion: "123",
            browser: "ios",
            source: "my-source",
            isBeta: false
        )
    }
}
