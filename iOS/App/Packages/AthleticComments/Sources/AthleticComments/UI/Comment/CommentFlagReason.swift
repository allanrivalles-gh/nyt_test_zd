//
//  CommentFlagReason.swift
//
//
//  Created by Kyle Browning on 7/14/22.
//

import AthleticApolloTypes
import Foundation

public enum CommentFlagReason: String, CaseIterable, Identifiable {
    public var id: String {
        rawValue
    }
    case abusive = "abusive_or_harmful"
    case trolling = "trolling_or_baiting"
    case spam = "spam"

    public var title: String {
        switch self {
        case .abusive:
            return Strings.commentFlagReason1.localized
        case .trolling:
            return Strings.commentFlagReason2.localized
        case .spam:
            return Strings.commentFlagReason3.localized
        }
    }

    public var flagReason: GQL.FlagReason {
        switch self {
        case .abusive:
            return .abusiveOrHarmful
        case .trolling:
            return .trollingOrBaiting
        case .spam:
            return .spam
        }
    }
}
