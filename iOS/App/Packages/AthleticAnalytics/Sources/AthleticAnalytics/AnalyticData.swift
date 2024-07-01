//
//  AnalyticData.swift
//
//
//  Created by Mark Corbyn on 19/5/2023.
//

import Foundation

public struct AnalyticData: Equatable {
    public enum EventType {
        case click
        case impress
        case view
    }

    public var click: AnalyticsEventRecord?
    public var impress: AnalyticsImpressionRecord?
    public var view: AnalyticsEventRecord?

    public init(
        click: AnalyticsEventRecord? = nil,
        impress: AnalyticsImpressionRecord? = nil,
        view: AnalyticsEventRecord? = nil
    ) {
        self.click = click
        self.impress = impress
        self.view = view
    }
}
