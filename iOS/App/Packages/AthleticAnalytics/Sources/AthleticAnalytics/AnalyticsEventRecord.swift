//
//  AnalyticsEventRecord.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 2/4/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation

// MARK: - Event Record - ios-events kafka stream
public struct AnalyticsEventRecord: Codable, Hashable, AnalyticsRequiredValues {
    // MARK: - Required Values
    public let userIdentifier: Int
    public let deviceIdentifier: String
    public let isSubscriber: Bool
    public let isBeta: Bool
    public let platform: String
    public let userAgent: String
    public let sessionIdentifier: String
    public let locale: String
    public let browserVersion: String
    public let browser: String
    public let source: String?

    // MARK: - Required Properties

    public let verb: AnalyticsEvent.Verb
    public let view: AnalyticsEvent.View

    // MARK: - Nullable Properties

    public let element: AnalyticsEvent.Element?
    public let objectType: AnalyticsEvent.ObjectType?
    public let objectIdentifier: String?
    public let previousView: AnalyticsEvent.View?
    public var metaBlob: AnalyticsEvent.MetaBlob?
    public let container: String?
    public let parentObjectType: AnalyticsEvent.ObjectType?
    public let parentObjectIdentifier: String?
    public let filterType: String?
    public let filterId: Int?
    public var indexH: Int?
    public var indexV: Int?
    public let eventTimestamp: Int

    public init(
        verb: AnalyticsEvent.Verb,
        view: AnalyticsEvent.View,
        element: AnalyticsEvent.Element? = nil,
        objectType: AnalyticsEvent.ObjectType? = nil,
        objectIdentifier: String? = nil,
        previousView: AnalyticsEvent.View? = nil,
        metaBlob: AnalyticsEvent.MetaBlob? = nil,
        container: String? = nil,
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
        self.objectIdentifier = objectIdentifier ?? nil
        self.previousView = previousView
        self.metaBlob = metaBlob ?? AnalyticsEvent.MetaBlob(requiredValues: requiredValues)
        self.container = container
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
        case container, source
        case eventTimestamp = "event_timestamp"
        case previousView = "previous_view"
        case objectType = "object_type"
        case objectIdentifier = "object_id"
        case filterType = "filter_type"
        case filterId = "filter_id"
        case indexH = "h_index"
        case indexV = "v_index"
        case parentObjectType = "parent_object_type"
        case parentObjectIdentifier = "parent_object_id"
        case metaBlob = "meta_blob"

        /// Required Values
        case userIdentifier = "user_id"
        case deviceIdentifier = "device_id"
        case isSubscriber = "is_subscriber"
        case platform, browser
        case locale
        case userAgent = "user_agent"
        case sessionIdentifier = "session_id"
        case browserVersion = "browser_version"
        case isBeta = "is_beta"
    }
}

extension AnalyticsEventRecord: CustomDebugStringConvertible {
    public var debugDescription: String {
        [
            verb.rawValue,
            view.rawValue,
            element?.rawValue,
            objectType?.rawValue,
            objectIdentifier,
            container,
            filterType,
            metaBlob?.debugDescription,
        ]
        .compactMap { $0 }
        .joined(separator: "-")
    }
}

extension AnalyticsEvent.MetaBlob: CustomDebugStringConvertible {
    /// Creates a string from all the non-nil properties as key/value pairs, comma separated.
    public var debugDescription: String {
        var dict: [String: Any] = [:]
        let mirror = Mirror(reflecting: self)
        for child in mirror.children {
            if let key = child.label {
                let valueMirror = Mirror(reflecting: child.value)
                if valueMirror.displayStyle == .optional {
                    if let valueChild = valueMirror.children.first {
                        dict[key] = valueChild.value
                    } else {
                        continue
                    }
                } else {
                    dict[key] = child.value
                }
            }
        }
        return
            dict
            .compactMapValues { $0 }
            .map { key, value in
                "\(key):\(value)"
            }
            .joined(separator: ", ")
    }
}

extension AnalyticsEventRecord: TimestampIndependentEquatable {
    public func isSameDisregardingTimestamp(record: AnalyticsEventRecord) -> Bool {
        userIdentifier == record.userIdentifier
            && deviceIdentifier == record.deviceIdentifier
            && isSubscriber == record.isSubscriber
            && isBeta == record.isBeta
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
            && previousView == record.previousView
            && metaBlob == record.metaBlob
            && container == record.container
            && parentObjectType == record.parentObjectType
            && parentObjectIdentifier == record.parentObjectIdentifier
            && filterType == record.filterType
            && filterId == record.filterId
            && indexH == record.indexH
            && indexV == record.indexV
    }
}
