//
//  PodcastSourceElement.swift
//
//
//  Created by Jason Leyrer on 6/6/23.
//

import AthleticAnalytics
import Foundation

public enum PodcastSourceElement: Codable, Hashable {
    case feedNavigation
    case discover
    case following

    public var analyticsElement: AnalyticsEvent.Element {
        switch self {
        case .feedNavigation:
            return .feedNavigation
        case .discover:
            return .discover
        case .following:
            return .following
        }
    }
}
