//
//  DragDismissible.swift
//
//
//  Created by Duncan Lau on 8/12/2023.
//

import SwiftUI

public enum DragDismissalAnimationType {
    case opacity
    case slideDown

    fileprivate func shouldDismiss(
        translation: CGSize,
        threshold: CGFloat
    ) -> Bool {
        let dismissed: CGFloat
        switch self {
        case .opacity:
            dismissed = abs(translation.height)
        case .slideDown:
            dismissed = translation.height
        }
        return dismissed >= threshold
    }

    fileprivate func offset(translation: CGSize) -> CGSize {
        switch self {
        case .opacity:
            return translation
        case .slideDown:
            return CGSize(width: 0, height: max(0, translation.height))
        }
    }
}

public struct DragDismissible<Content: View>: View {
    @Environment(\.dismiss) private var dismiss
    @GestureState private var offsetState: CGSize = .zero
    @State private var offset: CGSize = .zero
    @State private var dismissedByOpacity = false

    private let animationType: DragDismissalAnimationType
    @ViewBuilder private let content: (CGFloat) -> Content
    private let dismissOffsetThreshold: CGFloat = 200.0

    public init(
        animationType: DragDismissalAnimationType,
        @ViewBuilder content: @escaping (CGFloat) -> Content
    ) {
        self.animationType = animationType
        self.content = content
    }

    public var body: some View {
        let effectiveOffset = CGSize(
            width: offsetState.width + offset.width,
            height: offsetState.height + offset.height
        )
        /// we can not attach `onAnimationCompleted` directly to the boolean value
        /// so an intermediate `dismissalOpacity` was created to be able to listen for animation completion
        let dismissalOpacity: CGFloat = dismissedByOpacity ? 0 : 1
        let dismissProgress = (abs(effectiveOffset.height) / dismissOffsetThreshold)
            .clamped(to: 0...1)
        ZStack {
            Color.chalk.dark100
                .opacity(1 - dismissProgress)
            content(dismissProgress)
                .offset(animationType.offset(translation: effectiveOffset))
        }
        .opacity(dismissalOpacity)
        .ignoresSafeArea()
        .onAnimationCompleted(for: dismissalOpacity) {
            withoutAnimation {
                dismiss()
            }
        }
        .simultaneousGesture(
            DragGesture()
                .updating($offsetState) { currentState, gestureState, _ in
                    gestureState = currentState.translation
                }
                .onEnded { value in
                    offset = value.translation
                    let shouldDismiss = animationType.shouldDismiss(
                        translation: value.translation,
                        threshold: dismissOffsetThreshold
                    )

                    switch animationType {
                    case .opacity:
                        if shouldDismiss {
                            withAnimation {
                                dismissedByOpacity = true
                            }
                        }
                    case .slideDown:
                        if shouldDismiss {
                            withAnimation {
                                dismiss()
                            }
                        }
                    }

                    if !shouldDismiss {
                        withAnimation {
                            offset = .zero
                        }
                    }
                }
        )
    }
}
