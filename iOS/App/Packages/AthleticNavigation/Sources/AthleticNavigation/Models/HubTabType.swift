//
//  HubTabType.swift
//
//
//  Created by Mark Corbyn on 5/6/2023.
//

import Foundation

public enum HubTabType: Hashable, Codable {
    case feed
    case threads
    case schedule
    case standings
    case bracket
    case stats
    case roster
    case podcasts
}
