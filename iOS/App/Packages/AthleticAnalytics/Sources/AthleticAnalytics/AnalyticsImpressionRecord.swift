//
//  AnalyticsImpressionRecord.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 2/4/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation

// MARK: - Impressions Record - ios-impressions kafka stream
public struct AnalyticsImpressionRecord: Codable, Hashable, AnalyticsRequiredValues {

    // MARK: - Required Values
    public let userIdentifier: Int
    public let deviceIdentifier: String
    public let isSubscriber: Bool
    public let platform: String
    public let userAgent: String
    public let sessionIdentifier: String
    public let locale: String
    public let browserVersion: String
    public let browser: String
    public let isBeta: Bool
    public let source: String?

    // MARK: - Required Properties
    public let verb: AnalyticsEvent.Verb
    public let view: AnalyticsEvent.View
    public let element: AnalyticsEvent.Element?
    public let objectType: AnalyticsEvent.ObjectType
    public let objectIdentifier: String
    public var impressStartTime: Int
    public var impressEndTime: Int
    public let eventTimestamp: Int

    // MARK: - Nullable Properties
    public let container: AnalyticsEvent.Container?
    public let pageOrder: Int?
    public let parentObjectType: AnalyticsEvent.ObjectType?
    public let parentObjectIdentifier: String?
    public let filterType: String?
    public let filterId: Int?
    public var indexH: Int?
    public var indexV: Int?

    public init(
        verb: AnalyticsEvent.Verb,
        view: AnalyticsEvent.View,
        element: AnalyticsEvent.Element?,
        objectType: AnalyticsEvent.ObjectType,
        objectIdentifier: String?,
        impressStartTime: Int = Date.distantPast.millisecondsSince1970,
        impressEndTime: Int = Date.distantPast.millisecondsSince1970,
        container: AnalyticsEvent.Container? = nil,
        pageOrder: Int? = nil,
        parentObjectType: AnalyticsEvent.ObjectType? = nil,
        parentObjectIdentifier: String? = nil,
        filterType: String? = nil,
        filterId: Int? = nil,
        indexH: Int? = -1,
        indexV: Int? = -1,
        eventTimestamp: Int = Date().millisecondsSince1970,
        requiredValues: AnalyticsRequiredValues
    ) {
        self.verb = verb
        self.view = view
        self.element = element
        self.objectType = objectType
        // since GraphQl Feed types are not safely unwrapped at instantiation
        // figured this was better than have it all over the code
        self.objectIdentifier = objectIdentifier ?? ""
        self.impressStartTime = impressStartTime
        self.impressEndTime = impressEndTime
        self.container = container
        self.pageOrder = pageOrder
        self.parentObjectType = parentObjectType
        self.parentObjectIdentifier = parentObjectIdentifier
        self.filterType = filterType
        self.filterId = filterId
        self.indexH = indexH
        self.indexV = indexV
        self.eventTimestamp = eventTimestamp

        /// Required Values
        self.userIdentifier = requiredValues.userIdentifier
        self.deviceIdentifier = requiredValues.deviceIdentifier
        self.isSubscriber = requiredValues.isSubscriber
        self.platform = requiredValues.platform
        self.browserVersion = requiredValues.browserVersion
        self.browser = requiredValues.browser
        self.locale = requiredValues.locale
        self.userAgent = requiredValues.userAgent
        self.sessionIdentifier = requiredValues.sessionIdentifier
        self.source = requiredValues.source
        self.isBeta = requiredValues.isBeta
    }

    enum CodingKeys: String, CodingKey {
        case verb, view, element
        case container
        case objectType = "object_type"
        case objectIdentifier = "object_id"
        case filterType = "filter_type"
        case filterId = "filter_id"
        case impressStartTime = "impress_start_time"
        case impressEndTime = "impress_end_time"
        case indexH = "h_index"
        case indexV = "v_index"
        case parentObjectType = "parent_object_type"
        case parentObjectIdentifier = "parent_object_id"
        case eventTimestamp = "event_timestamp"
        case pageOrder = "page_order"

        /// Required Values
        case userIdentifier = "user_id"
        case deviceIdentifier = "device_id"
        case isSubscriber = "is_subscriber"
        case platform, browser
        case locale
        case userAgent = "user_agent"
        case sessionIdentifier = "session_id"
        case browserVersion = "browser_version"
        case source
        case isBeta = "is_beta"
    }
}

extension AnalyticsImpressionRecord: CustomDebugStringConvertible {
    public var debugDescription: String {
        let description = [
            verb.rawValue,
            view.rawValue,
            element?.rawValue,
            objectType.rawValue,
            objectIdentifier,
            container?.rawValue,
            parentObjectType?.rawValue,
            parentObjectIdentifier,
            filterType,
            pageOrder?.string,
            container?.rawValue,
            indexV?.string,
        ]
        .compactMap { $0 }
        .joined(separator: "-")
        return String(description)
    }
}

extension AnalyticsImpressionRecord: TimestampIndependentEquatable {
    public func isSameDisregardingTimestamp(record: AnalyticsImpressionRecord) -> Bool {
        userIdentifier == record.userIdentifier
            && deviceIdentifier == record.deviceIdentifier
            && isSubscriber == record.isSubscriber
            && platform == record.platform
            && userAgent == record.userAgent
            && sessionIdentifier == record.sessionIdentifier
            && locale == record.locale
            && browserVersion == record.browserVersion
            && browser == record.browser
            && source == record.source
            && verb == record.verb
            && view == record.view
            && element == record.element
            && objectType == record.objectType
            && objectIdentifier == record.objectIdentifier
            && impressStartTime == record.impressStartTime
            && impressEndTime == record.impressEndTime
            && container == record.container
            && pageOrder == record.pageOrder
            && parentObjectType == record.parentObjectType
            && parentObjectIdentifier == record.parentObjectIdentifier
            && filterType == record.filterType
            && filterId == record.filterId
            && indexH == record.indexH
            && indexV == record.indexV
    }
}
