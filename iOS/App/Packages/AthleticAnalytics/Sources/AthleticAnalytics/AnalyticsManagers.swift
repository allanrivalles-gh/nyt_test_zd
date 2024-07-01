//
//  AnalyticsManagers.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 2/4/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

public struct AnalyticsManagers {
    public enum Environment: String, Codable {
        case production
        case staging
    }

    public static var environment: Environment = .staging

    private static let flushRecordsTimeInterval: TimeInterval = 30

    /// These are used for events
    public static var eventDecoder = JSONDecoder()

    public static var eventEncoder: JSONEncoder = {
        let coder = JSONEncoder()
        coder.keyEncodingStrategy = .convertToSnakeCase
        return coder
    }()

    public static func startListeners() {
        Task {
            await AnalyticsManagers.events.startListener()
            await AnalyticsManagers.pushEvents.startListener()
            await AnalyticsManagers.feedImpressions.startListener()
            await AnalyticsManagers.articleImpressions.startListener()
            await AnalyticsManagers.commentImpressions.startListener()
            await AnalyticsManagers.scoresImpressions.startListener()
            await AnalyticsManagers.frontpageImpressions.startListener()
            await AnalyticsManagers.liveBlogImpressions.startListener()
            await AnalyticsManagers.listenTabImpressions.startListener()
        }
    }

    public static func stopListeners() {
        Task {
            await AnalyticsManagers.events.stopListener()
            await AnalyticsManagers.pushEvents.stopListener()
            await AnalyticsManagers.feedImpressions.stopListener()
            await AnalyticsManagers.articleImpressions.stopListener()
            await AnalyticsManagers.commentImpressions.stopListener()
            await AnalyticsManagers.scoresImpressions.stopListener()
            await AnalyticsManagers.frontpageImpressions.stopListener()
            await AnalyticsManagers.liveBlogImpressions.stopListener()
            await AnalyticsManagers.listenTabImpressions.stopListener()
        }
    }

    public static let events = AnalyticEventManager(
        withConfiguration: .init(
            schemaID: 26,
            identifier: .events,
            environment: environment,
            flushRecordsTimeInterval: flushRecordsTimeInterval
        ),
        encoder: AnalyticsManagers.eventEncoder,
        decoder: AnalyticsManagers.eventDecoder
    )

    public static let pushEvents = AnalyticEventManager(
        withConfiguration: .init(
            schemaID: 26,
            identifier: .pushEvents,
            environment: environment,
            flushRecordsTimeInterval: flushRecordsTimeInterval
        ),
        encoder: AnalyticsManagers.eventEncoder,
        decoder: AnalyticsManagers.eventDecoder
    )

    /// Impressions will use default encoding/decoding
    public static let frontpageImpressions = AnalyticImpressionManager(
        withConfiguration: .init(
            schemaID: 43,
            identifier: .frontpageImpressions,
            environment: environment,
            flushRecordsTimeInterval: flushRecordsTimeInterval
        )
    )

    public static let feedImpressions = AnalyticImpressionManager(
        withConfiguration: .init(
            schemaID: 43,
            identifier: .feedImpressions,
            environment: environment,
            flushRecordsTimeInterval: flushRecordsTimeInterval
        )
    )

    public static let liveBlogImpressions = AnalyticImpressionManager(
        withConfiguration: .init(
            schemaID: 43,
            identifier: .liveBlogImpressions,
            environment: environment,
            flushRecordsTimeInterval: flushRecordsTimeInterval
        )
    )

    public static let listenTabImpressions = AnalyticImpressionManager(
        withConfiguration: .init(
            schemaID: 43,
            identifier: .listenTabImpressions,
            environment: environment,
            flushRecordsTimeInterval: flushRecordsTimeInterval
        )
    )

    public static let commentImpressions = AnalyticImpressionManager(
        withConfiguration: .init(
            schemaID: 43,
            identifier: .commentImpressions,
            environment: environment,
            flushRecordsTimeInterval: flushRecordsTimeInterval
        )
    )

    public static let scoresImpressions = AnalyticImpressionManager(
        withConfiguration: .init(
            schemaID: 43,
            identifier: .scoresImpressions,
            environment: environment,
            flushRecordsTimeInterval: flushRecordsTimeInterval
        )
    )

    public static let articleImpressions = AnalyticImpressionManager(
        withConfiguration: .init(
            schemaID: 43,
            identifier: .articleImpressions,
            environment: environment,
            flushRecordsTimeInterval: flushRecordsTimeInterval
        )
    )
}
