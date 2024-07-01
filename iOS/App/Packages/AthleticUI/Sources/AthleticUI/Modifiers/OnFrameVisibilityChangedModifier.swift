//
//  OnFrameVisibilityChangedModifier.swift
//
//
//  Created by Leonardo da Silva on 28/02/23.
//

import SwiftUI

extension View {
    /// When setting `hiddenThreshold` to nil, not within `visibleThreshold` is used instead.
    public func onFrameVisibilityChanged(
        parentGeometry: GeometryProxy,
        visibleThreshold: CGFloat = 0.5,
        hiddenThreshold: CGFloat? = 0.0,
        onChanged: @escaping (Bool) -> Void
    ) -> some View {
        modifier(
            OnFrameVisibilityChangedModifier(
                parentGeometry: parentGeometry,
                visibleThreshold: visibleThreshold,
                hiddenThreshold: hiddenThreshold,
                onChanged: onChanged
            )
        )
    }
}

private struct OnFrameVisibilityChangedModifier: ViewModifier {
    @State private var isActive = false
    @State private var isImpressing = false
    @State private var isVisible = false
    private let parentGeometry: GeometryProxy
    private let visibleThreshold: CGFloat
    private let hiddenThreshold: CGFloat?
    private let onChanged: (Bool) -> Void

    init(
        parentGeometry: GeometryProxy,
        visibleThreshold: CGFloat,
        hiddenThreshold: CGFloat?,
        onChanged: @escaping (Bool) -> Void
    ) {
        self.parentGeometry = parentGeometry
        self.visibleThreshold = visibleThreshold
        self.hiddenThreshold = hiddenThreshold
        self.onChanged = onChanged
    }

    func body(content: Content) -> some View {
        content
            .background(
                GeometryReader { proxy in
                    Color.clear
                        .onAppear {
                            onVisibility(getVisibility(for: proxy))
                        }
                        .onChange(of: getVisibility(for: proxy)) {
                            onVisibility($0)
                        }
                }
            )
            .onAppear { isActive = true }
            .onDisappear { isActive = false }
            .onChange(of: isActive) { isVisible = $0 && isImpressing }
            .onChange(of: isImpressing) { isVisible = $0 && isActive }
            .onChange(of: isVisible, perform: onChanged)
    }

    private func onVisibility(_ visibility: Visibility?) {
        guard let visibility else { return }
        guard let hiddenThreshold else {
            isImpressing = visibility.isConsideredVisible(
                threshold: visibleThreshold
            )
            return
        }

        if isImpressing {
            if visibility.isConsideredHidden(threshold: hiddenThreshold) {
                isImpressing = false
            }
        } else {
            if visibility.isConsideredVisible(threshold: visibleThreshold) {
                isImpressing = true
            }
        }
    }

    private struct Visibility: Equatable {
        let horizontal: CGFloat
        let vertical: CGFloat

        func isConsideredHidden(threshold: CGFloat) -> Bool {
            return horizontal <= threshold || vertical <= threshold
        }

        func isConsideredVisible(threshold: CGFloat) -> Bool {
            return horizontal >= threshold && vertical >= threshold
        }
    }

    private func getVisibility(for proxy: GeometryProxy) -> Visibility? {
        let containerFrame = parentGeometry.frame(in: .global)
        let frame = proxy.frame(in: .global)

        guard frame.height > 0 && frame.width > 0 else { return nil }

        /// When referencing the `.global` frames they're relative the the global position, meaning from the top/leading edge
        /// of the screen. Our container might not be at the top of the screen, as is the case with the box score for example,
        /// so we normalize the child offsets relative to where the container is positioned.

        let verticalOnScreenPercentage = getOnScreenPercentage(
            minFrameOffset: frame.minY - containerFrame.minY,
            maxFrameOffset: frame.maxY - containerFrame.minY,
            containerDimension: containerFrame.height
        )

        let horizontalOnScreenPercentage = getOnScreenPercentage(
            minFrameOffset: frame.minX - containerFrame.minX,
            maxFrameOffset: frame.maxX - containerFrame.minX,
            containerDimension: containerFrame.width
        )

        return Visibility(
            horizontal: horizontalOnScreenPercentage,
            vertical: verticalOnScreenPercentage
        )
    }

    private func getOnScreenPercentage(
        minFrameOffset: CGFloat,
        maxFrameOffset: CGFloat,
        containerDimension: CGFloat
    ) -> CGFloat {
        let amountOnScreen: CGFloat

        if minFrameOffset > containerDimension || maxFrameOffset < 0 {
            /// Our child is completely off screen
            return 0
        }

        if minFrameOffset < 0 && maxFrameOffset > 0 {
            /// Our child is partially on screen either at the top or the leading edge of our container.
            amountOnScreen = maxFrameOffset
        } else if minFrameOffset >= 0 && maxFrameOffset <= containerDimension {
            /// Our child is completey on screen.
            return 1
        } else if minFrameOffset < containerDimension && maxFrameOffset > containerDimension {
            /// Our child is partially on screen at the bottom or trailing edge of our container.
            amountOnScreen = containerDimension - minFrameOffset
        } else {
            return 0
        }

        return amountOnScreen / (maxFrameOffset - minFrameOffset)
    }
}
