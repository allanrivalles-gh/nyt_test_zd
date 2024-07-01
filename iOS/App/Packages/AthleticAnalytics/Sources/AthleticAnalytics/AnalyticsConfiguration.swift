//
//  AnalyticsConfiguration.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 2/4/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

// MARK: - AnalyticsConfiguration
public struct AnalyticsConfiguration: Codable {
    let schemaID: Int
    let identifier: Identifier
    let environment: AnalyticsManagers.Environment
    let flushRecordsTimeInterval: TimeInterval

    public enum Identifier: String, Codable {
        case events = "events"
        case pushEvents = "push"
        case frontpageImpressions = "front-page-impressions"
        case feedImpressions = "feed-impressions"
        case liveBlogImpressions = "live-blog-impressions"
        case commentImpressions = "comments-impressions"
        case listenTabImpressions = "listen-tab-impressions"
        case scoresImpressions = "scores-impressions"
        case articleImpressions = "article-impressions"

        case testingEvents
        case testingImpressions
    }

    public init(
        schemaID: Int,
        identifier: AnalyticsConfiguration.Identifier,
        environment: AnalyticsManagers.Environment,
        flushRecordsTimeInterval: TimeInterval
    ) {
        self.schemaID = schemaID
        self.identifier = identifier
        self.environment = environment
        self.flushRecordsTimeInterval = flushRecordsTimeInterval
        self.topic = "ios-\(identifier.rawValue)"
        self.auth =
            environment == .production
            ? "Bearer 3Jsgsg2EeX24F96WMFURVhaQKfnAuMZM" : "Bearer e3p2NHDerBNWkeKQ3suKC6dywRo6xMQ7"
        self.version = String.getAppVersionSemver

        self.headers = [
            "Authorization": auth,
            "Accept-Language": Locale.current.formatted,
            "User-Agent": String.userAgent,
            "X-App-Version": version,
            "Content-Type": "application/json",
        ]
        self.endpoint = "v1/avro/send"
        let base =
            environment == .production
            ? "https://analytic-proxy.theathletic.com"
            : "https://analytic-proxy-staging.theathletic.com"
        self.url = URL(string: "\(base)/\(endpoint)")!
    }

    /// Fixed values from init
    let topic: String
    let auth: String
    let version: String
    let headers: [String: String]
    let url: URL
    let endpoint: String
}
