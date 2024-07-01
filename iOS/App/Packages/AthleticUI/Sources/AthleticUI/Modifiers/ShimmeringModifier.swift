//
//  ShimmeringModifier.swift
//
//
//  Original Author https://github.com/markiv/
//  Licensed under the MIT license.
//

import SwiftUI

struct ShimmeringModifier: ViewModifier {
    struct AnimatedMask: View, Animatable {
        var phase: CGFloat = 0

        var animatableData: CGFloat {
            get { phase }
            set { phase = newValue }
        }

        var body: some View {
            GradientMask(phase: phase).scaleEffect(5)
        }
    }

    /// A slanted, animatable gradient between transparent and opaque to use as mask.
    /// The `phase` parameter shifts the gradient, moving the opaque band.
    struct GradientMask: View {
        let phase: CGFloat
        let centerColor = Color.black
        let edgeColor = Color.black.opacity(0.3)
        @Environment(\.layoutDirection) private var layoutDirection

        var body: some View {
            let isRightToLeft = layoutDirection == .rightToLeft
            LinearGradient(
                gradient: Gradient(stops: [
                    .init(color: edgeColor, location: phase),
                    .init(color: centerColor, location: phase + 0.1),
                    .init(color: edgeColor, location: phase + 0.2),
                ]),
                startPoint: isRightToLeft ? .bottomTrailing : .topLeading,
                endPoint: isRightToLeft ? .topLeading : .bottomTrailing
            )
        }
    }

    @State private var phase: CGFloat = 0
    @State private var animate = false

    func body(content: Content) -> some View {
        content
            .mask(
                AnimatedMask(phase: animate ? 0.8 : 0)
                    .task {
                        withAnimation(.linear(duration: 1.8).repeatForever(autoreverses: false)) {
                            animate.toggle()
                        }
                    }
            )
    }
}

extension View {
    public func shimmering() -> some View {
        ModifiedContent(
            content: self,
            modifier: ShimmeringModifier()
        )
    }
}
