//
//  CommentingPlayModifier.swift
//
//
//  Created by Leonardo da Silva on 25/10/22.
//

import SwiftUI

public struct CommentingPlay: Equatable {
    public let id: String
    public let description: String
    public let occurredAtString: String

    public var commentDrawerDescription: String {
        let stringArray = description.components(separatedBy: .whitespacesAndNewlines)
        return stringArray.joined(separator: " ")
    }

    public init(id: String, description: String, occurredAtString: String) {
        self.id = id
        self.description = description
        self.occurredAtString = occurredAtString
    }
}

private struct CommentingPlayKey: EnvironmentKey {
    static var defaultValue: Binding<CommentingPlay?> = .constant(nil)
}

extension EnvironmentValues {
    var commentingPlay: Binding<CommentingPlay?> {
        get { self[CommentingPlayKey.self] }
        set { self[CommentingPlayKey.self] = newValue }
    }
}

extension View {
    public func commentingPlay(_ commentingPlay: Binding<CommentingPlay?>) -> some View {
        environment(\.commentingPlay, commentingPlay)
    }
}
