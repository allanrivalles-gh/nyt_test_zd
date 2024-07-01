//
//  Tab.swift
//
//
//  Created by Mark Corbyn on 5/6/2023.
//

import AthleticFoundation
import Foundation

public enum MainTab: Codable {
    case account
    case home
    case scores
    case discover
    case listen
    /// Handle following entities for user
    case entity(entity: FollowingEntity, hubType: HubTabType?)
}

extension MainTab: Identifiable {
    public var id: String {
        switch self {
        case .account:
            return "account"
        case .home:
            return "home"
        case .scores:
            return "scores"
        case .discover:
            return "front_page"
        case .listen:
            return "listen"
        case .entity(let entity, _):
            return entity.id
        }
    }
}

extension MainTab: Equatable {
    public static func == (lhs: MainTab, rhs: MainTab) -> Bool {
        return lhs.id == rhs.id
    }
}

extension MainTab: Hashable {
    public func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
}
