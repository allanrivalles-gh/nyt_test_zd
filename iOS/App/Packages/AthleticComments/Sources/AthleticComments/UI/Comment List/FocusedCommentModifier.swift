//
//  FocusedCommentModifier.swift
//
//
//  Created by Jason Xu on 2/15/23.
//

import Foundation
import SwiftUI

public struct FocusedComment: Equatable {

    public let comment: CommentViewModel
    public let isReply: Bool

    public init(comment: CommentViewModel, isReply: Bool = false) {
        self.comment = comment
        self.isReply = isReply
    }
}

private struct FocusedCommentKey: EnvironmentKey {
    static var defaultValue: Binding<FocusedComment?> = .constant(nil)
}

extension EnvironmentValues {
    public var focusedComment: Binding<FocusedComment?> {
        get { self[FocusedCommentKey.self] }
        set { self[FocusedCommentKey.self] = newValue }
    }
}

extension View {
    public func focusedComment(_ focusedComment: Binding<FocusedComment?>) -> some View {
        environment(\.focusedComment, focusedComment)
    }
}
