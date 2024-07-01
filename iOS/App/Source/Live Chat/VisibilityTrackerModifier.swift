//
//  VisibilityTrackerModifier.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 19/09/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

private struct VisibilityFrameProviderModifier: ViewModifier {
    let frame: Binding<CGRect?>

    func body(content: Content) -> some View {
        content
            .overlay(
                GeometryReader { geometry in
                    Color.clear
                        .onChange(of: geometry.frame(in: .global)) { newFrame in
                            frame.wrappedValue = newFrame
                        }
                        .onAppear {
                            frame.wrappedValue = geometry.frame(in: .global)
                        }
                        .onDisappear {
                            frame.wrappedValue = nil
                        }
                }
            )
    }
}

private struct DetectIsWithinFrameModifier: ViewModifier {
    @State private var isWithinFrame: Bool!

    let frame: CGRect
    let onChanged: (Bool) -> Void

    func body(content: Content) -> some View {
        content
            .overlay(
                GeometryReader { geometry in
                    Color.clear
                        .onChange(of: frame) { newValue in
                            onVisibilityFrame(newValue, thisFrame: geometry.frame(in: .global))
                        }
                        .onChange(of: geometry.frame(in: .global)) { newValue in
                            onVisibilityFrame(frame, thisFrame: newValue)
                        }
                        .onAppear {
                            onVisibilityFrame(frame, thisFrame: geometry.frame(in: .global))
                        }
                        .onDisappear {
                            onIsWithinFrame(false)
                        }
                }
            )
    }

    private func onVisibilityFrame(_ visibilityFrame: CGRect, thisFrame: CGRect) {
        onIsWithinFrame(thisFrame.isWithin(other: visibilityFrame))
    }

    private func onIsWithinFrame(_ isWithinFrame: Bool) {
        if isWithinFrame != self.isWithinFrame {
            self.isWithinFrame = isWithinFrame
            onChanged(isWithinFrame)
        }
    }
}

extension CGRect {
    fileprivate func isWithin(other: CGRect) -> Bool {
        if minX < other.minX { return false }
        if maxX > other.maxX { return false }
        if minY < other.minY { return false }
        if maxY > other.maxY { return false }
        return true
    }
}

extension View {
    func visibilityFrameProvider(frame: Binding<CGRect?>) -> some View {
        modifier(VisibilityFrameProviderModifier(frame: frame))
    }

    func detectIsWithinFrame(_ frame: CGRect, onChanged: @escaping (Bool) -> Void) -> some View {
        modifier(DetectIsWithinFrameModifier(frame: frame, onChanged: onChanged))
    }

    func visibilityTracker<Value: Hashable>(
        frame: CGRect?,
        id: Value,
        allVisible: Binding<Set<Value>>
    ) -> some View {
        detectIsWithinFrame(frame ?? .zero) { isWithin in
            if isWithin {
                allVisible.wrappedValue.insert(id)
            } else {
                allVisible.wrappedValue.remove(id)
            }
        }
    }
}
