//
//  CommentingUserIsStaffModifier.swift
//
//
//  Created by Jason Leyrer on 11/16/22.
//

import AthleticApolloTypes
import SwiftUI

private struct CommentingUserKey: EnvironmentKey {
    static var defaultValue: GQL.CustomerDetail? = nil
}

extension EnvironmentValues {
    var commentingUser: GQL.CustomerDetail? {
        get { self[CommentingUserKey.self] }
        set { self[CommentingUserKey.self] = newValue }
    }
}

extension View {
    public func commentingUser(_ user: GQL.CustomerDetail?) -> some View {
        environment(\.commentingUser, user)
    }
}
