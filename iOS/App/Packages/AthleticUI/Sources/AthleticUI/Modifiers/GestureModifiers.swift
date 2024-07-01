//
//  GestureModifiers.swift
//
//
//  Created by Jason Leyrer on 8/17/23.
//

import AthleticFoundation
import SwiftUI

extension View {
    public func onTapGesture(count: Int = 1, onEnded: @escaping () async -> Void) -> some View {
        modifier(TapGestureModifierAsync(count: count, onEnded: onEnded))
    }

    public func onSimultaneousTapGesture(count: Int = 1, onEnded: @escaping () -> Void) -> some View
    {
        modifier(SimultaneousTapGestureModifier(count: count, onEnded: onEnded))
    }

    public func onSimultaneousTapGesture(count: Int = 1, onEnded: @escaping () async -> Void)
        -> some View
    {
        modifier(SimultaneousTapGestureModifierAsync(count: count, onEnded: onEnded))
    }

    public func onDragGesture(
        minimumDistance: CGFloat = 10,
        onChanged: @escaping (DragGesture.Value) -> Void = { _ in },
        onEnded: @escaping (DragGesture.Value) -> Void = { _ in }
    ) -> some View {
        modifier(
            DragGestureModifier(
                minimumDistance: minimumDistance,
                onChanged: onChanged,
                onEnded: onEnded
            )
        )
    }

    public func onDragGesture(
        minimumDistance: CGFloat = 10,
        onChanged: @escaping (DragGesture.Value) async -> Void = { _ in },
        onEnded: @escaping (DragGesture.Value) async -> Void = { _ in }
    ) -> some View {
        modifier(
            DragGestureModifierAsync(
                minimumDistance: minimumDistance,
                onChanged: onChanged,
                onEnded: onEnded
            )
        )
    }

    public func onSimultaneousDragGesture(
        minimumDistance: CGFloat = 10,
        onChanged: @escaping (DragGesture.Value) -> Void = { _ in },
        onEnded: @escaping (DragGesture.Value) -> Void = { _ in }
    ) -> some View {
        modifier(
            SimultaneousDragGestureModifier(
                minimumDistance: minimumDistance,
                onChanged: onChanged,
                onEnded: onEnded
            )
        )
    }

    public func onSimultaneousDragGesture(
        minimumDistance: CGFloat = 10,
        onChanged: @escaping (DragGesture.Value) async -> Void = { _ in },
        onEnded: @escaping (DragGesture.Value) async -> Void = { _ in }
    ) -> some View {
        modifier(
            SimultaneousDragGestureModifierAsync(
                minimumDistance: minimumDistance,
                onChanged: onChanged,
                onEnded: onEnded
            )
        )
    }

    public func onSimultaneousLongPressGesture(
        minimumDuration: CGFloat = 0.5,
        onEnded: @escaping (LongPressGesture.Value) -> Void = { _ in }
    ) -> some View {
        modifier(
            SimultaneousLongPressGestureModifier(
                minimumDuration: minimumDuration,
                onEnded: onEnded
            )
        )
    }
}

private struct TapGestureModifierAsync: ViewModifier {
    let count: Int
    let onEnded: () async -> Void

    func body(content: Content) -> some View {
        content
            .onTapGesture {
                Task {
                    await onEnded()
                }
            }
    }
}

private struct SimultaneousTapGestureModifier: ViewModifier {
    let count: Int
    let onEnded: () -> Void

    func body(content: Content) -> some View {
        content
            .simultaneousGesture(
                TapGesture().onEnded {
                    onEnded()
                }
            )
    }
}

private struct SimultaneousTapGestureModifierAsync: ViewModifier {
    let count: Int
    let onEnded: () async -> Void

    func body(content: Content) -> some View {
        content
            .simultaneousGesture(
                TapGesture().onEnded {
                    Task {
                        await onEnded()
                    }
                }
            )
    }
}

private struct DragGestureModifier: ViewModifier {
    let minimumDistance: CGFloat
    let onChanged: (DragGesture.Value) -> Void
    let onEnded: (DragGesture.Value) -> Void

    func body(content: Content) -> some View {
        content
            .gesture(
                DragGesture(minimumDistance: minimumDistance)
                    .onChanged { value in
                        onChanged(value)
                    }
                    .onEnded { value in
                        onEnded(value)
                    }
            )
    }
}

private struct DragGestureModifierAsync: ViewModifier {
    let minimumDistance: CGFloat
    let onChanged: (DragGesture.Value) async -> Void
    let onEnded: (DragGesture.Value) async -> Void

    func body(content: Content) -> some View {
        content
            .gesture(
                DragGesture(minimumDistance: minimumDistance)
                    .onChanged { newValue in
                        Task {
                            await onChanged(newValue)
                        }
                    }
                    .onEnded { value in
                        Task {
                            await onEnded(value)
                        }
                    }
            )
    }
}

private struct SimultaneousDragGestureModifier: ViewModifier {
    let minimumDistance: CGFloat
    let onChanged: (DragGesture.Value) -> Void
    let onEnded: (DragGesture.Value) -> Void

    func body(content: Content) -> some View {
        content
            .simultaneousGesture(
                DragGesture(minimumDistance: minimumDistance)
                    .onChanged { value in
                        onChanged(value)
                    }
                    .onEnded { value in
                        onEnded(value)
                    }
            )
    }
}

private struct SimultaneousDragGestureModifierAsync: ViewModifier {
    let minimumDistance: CGFloat
    let onChanged: (DragGesture.Value) async -> Void
    let onEnded: (DragGesture.Value) async -> Void

    func body(content: Content) -> some View {
        content
            .simultaneousGesture(
                DragGesture(minimumDistance: minimumDistance)
                    .onChanged { newValue in
                        Task {
                            await onChanged(newValue)
                        }
                    }
                    .onEnded { value in
                        Task {
                            await onEnded(value)
                        }
                    }
            )
    }
}

private struct SimultaneousLongPressGestureModifier: ViewModifier {
    let minimumDuration: CGFloat
    let onEnded: (LongPressGesture.Value) -> Void

    func body(content: Content) -> some View {
        content
            .simultaneousGesture(
                LongPressGesture(minimumDuration: minimumDuration)
                    .onEnded { value in
                        onEnded(value)
                    }
            )
    }
}
