//
//  MockAnalyticDefaults.swift
//
//
//  Created by Mark Corbyn on 13/7/2023.
//

import AthleticAnalytics
import Foundation

struct MockAnalyticDefaults: Codable, Equatable, AnalyticsRequiredValues {

    let userIdentifier: Int
    let deviceIdentifier: String
    let isSubscriber: Bool
    let platform: String
    let userAgent: String
    let sessionIdentifier: String
    let locale: String
    let browserVersion: String
    let browser: String
    let isBeta: Bool
    let source: String?

    init(
        userIdentifier: Int = 12345,
        deviceIdentifier: String = "mock-device",
        isSubscriber: Bool = true,
        platform: String = "mock-platform",
        userAgent: String = "mock-user-agent",
        sessionIdentifier: String = "mock-session",
        locale: String = "mock-locale",
        browserVersion: String = "mock-browser-version",
        browser: String = "mock-browser",
        isBeta: Bool = false,
        source: String? = nil
    ) {
        self.userIdentifier = userIdentifier
        self.deviceIdentifier = deviceIdentifier
        self.isSubscriber = isSubscriber
        self.platform = platform
        self.userAgent = userAgent
        self.sessionIdentifier = sessionIdentifier
        self.locale = locale
        self.browserVersion = browserVersion
        self.browser = browser
        self.isBeta = isBeta
        self.source = source
    }
}
