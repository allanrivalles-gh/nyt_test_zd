//
//  GQL.CommentSortBy+Extensions.swift
//
//
//  Created by Kyle Browning on 7/23/22.
//

import AthleticAnalytics
import AthleticApolloTypes

extension GQL.CommentSortBy: Identifiable {

    public var id: String {
        self.rawValue
    }

    public var title: String {
        switch self {
        case .likes:
            return Strings.mostLiked.localized
        case .recent:
            return Strings.newest.localized
        case .time:
            return Strings.oldest.localized
        case .trending:
            return Strings.trending.localized
        case .__unknown(let rawValue):
            assertionFailure("New Sort By case not handled \(rawValue)")
            return ""
        }
    }
}
