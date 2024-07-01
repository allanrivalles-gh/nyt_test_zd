//
// CommentDrawerDismissedModifier.swift
//
//
//  Created by kevin fremgen on 7/24/23.
//

import Combine
import SwiftUI

private struct CommentDrawerDismissedKey: EnvironmentKey {
    static var defaultValue: AnyPublisher<Void, Never> = AnyPublisher(Empty())
}

extension EnvironmentValues {
    var commentDrawerDismissed: AnyPublisher<Void, Never> {
        get { self[CommentDrawerDismissedKey.self] }
        set { self[CommentDrawerDismissedKey.self] = newValue }
    }
}

extension View {
    public func commentDrawerDismissed(_ commentDrawerDismissed: AnyPublisher<Void, Never>)
        -> some View
    {
        environment(\.commentDrawerDismissed, commentDrawerDismissed)
    }
}
