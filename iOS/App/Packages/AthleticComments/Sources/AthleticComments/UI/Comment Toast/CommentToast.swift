//
// CommentToast.swift
//
//
//  Created by kevin fremgen on 7/3/23.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import Foundation
import SwiftUI

extension View {

    public func commentInteractionToast(
        commentInteractionError: Binding<String?>
    ) -> some View {
        ModifiedContent(
            content: self,
            modifier: CommentInteractionToast(
                commentInteractionError: commentInteractionError
            )
        )
    }

    public func commentUndoToast(
        dismissedComment: Binding<DismissedComment?>,
        text: Binding<String>,
        isCommentDrawerFocus: FocusState<Bool>.Binding,
        undoDismissCommentAction: @escaping () -> String?
    ) -> some View {
        ModifiedContent(
            content: self,
            modifier: CommentUndoToast(
                dismissedComment: dismissedComment,
                text: text,
                isCommentDrawerFocus: isCommentDrawerFocus,
                undoDismissCommentAction: undoDismissCommentAction
            )
        )
    }
}

private struct CommentInteractionToast: ViewModifier {

    @Binding var commentInteractionError: String?

    func body(content: Content) -> some View {
        content
            .overlay(alignment: .bottom) {
                if let commentInteractionError {
                    CommentToast(
                        duration: 3,
                        message: commentInteractionError,
                        dismissAction: action,
                        timerAction: action
                    )
                }
            }
    }

    func action() {
        withAnimation {
            commentInteractionError = nil
        }
    }
}

private struct CommentUndoToast: ViewModifier {

    @Binding var dismissedComment: DismissedComment?
    @Binding var text: String
    var isCommentDrawerFocus: FocusState<Bool>.Binding
    let undoDismissCommentAction: () -> String?

    func body(content: Content) -> some View {
        content
            .overlay(alignment: .bottom) {
                if dismissedComment != nil {
                    CommentToast(
                        duration: 6,
                        message: Strings.commentToast.localized,
                        buttonText: Strings.undo.localized
                    ) {
                        guard let previousText = undoDismissCommentAction() else {
                            return
                        }

                        text = previousText
                        isCommentDrawerFocus.wrappedValue = true
                    } timerAction: {
                        withAnimation {
                            dismissedComment = nil
                        }
                    }
                }
            }
    }
}

private struct CommentToast: View {

    let duration: TimeInterval
    let message: String
    var buttonText: String?
    let dismissAction: () -> Void
    let timerAction: () -> Void

    @State private var timer: Timer?

    init(
        duration: TimeInterval,
        message: String,
        buttonText: String? = nil,
        dismissAction: @escaping () -> Void,
        timerAction: @escaping () -> Void
    ) {
        self.duration = duration
        self.message = message
        self.buttonText = buttonText
        self.dismissAction = dismissAction
        self.timerAction = timerAction
    }

    var body: some View {

        HStack(spacing: 0) {
            Image("icon_exclamation_circle")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 30, height: 30)

            Text(message)
                .fontStyle(.calibreUtility.s.medium)
                .kerning(0.21)

            Spacer()

            Button {
                timer?.invalidate()
                timer = nil
                dismissAction()
            } label: {
                Group {
                    if let buttonText {
                        Text(buttonText)
                            .underline()
                    } else {
                        Image(systemName: "xmark")
                    }
                }
                .fontStyle(.calibreUtility.s.medium)
                .kerning(0.14)

            }

        }
        .frame(maxWidth: .infinity)
        .foregroundColor(.chalk.dark800)
        .padding(.leading, 8)
        .padding(.vertical, 9)
        .padding(.trailing, 16)
        .background(Color.chalk.dark300)
        .padding(.horizontal, 16)
        .padding(.bottom, 40)
        .transition(.move(edge: .bottom))
        .zIndex(1)
        .onAppear {
            hideToast(after: duration)
        }
    }

    private func hideToast(after seconds: TimeInterval) {
        timer?.invalidate()
        timer = Timer(timeInterval: seconds, repeats: false) { _ in
            withAnimation {
                timer?.invalidate()
                timer = nil
                timerAction()
            }
        }

        if let timer {
            RunLoop.current.add(timer, forMode: .common)
        }
    }
}
