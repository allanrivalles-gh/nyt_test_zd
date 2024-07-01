//
//  FollowingEntity+Extensions.swift
//
//
//  Created by Duncan Lau on 26/5/2023.
//

import Foundation

extension FollowingEntity: Hashable {

    public func hash(into hasher: inout Hasher) {
        hasher.combine(legacyId)
        hasher.combine(type)
        hasher.combine(isFollowing)
    }

    public static func == (lhs: FollowingEntity, rhs: FollowingEntity) -> Bool {
        return lhs.legacyId == rhs.legacyId
            && lhs.type == rhs.type
            && lhs.isFollowing == rhs.isFollowing
    }
}

extension FollowingEntity: Identifiable {
    public var id: String {
        legacyId + "-" + type.rawValue
    }
}
