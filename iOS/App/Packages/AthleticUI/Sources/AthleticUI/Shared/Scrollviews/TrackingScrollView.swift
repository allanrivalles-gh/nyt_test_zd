//
//  TrackingScrollView.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 26/11/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

public struct TrackingScrollView<Content: View>: View {
    let axes: Axis.Set
    let showsIndicators: Bool

    private let trackOffset: (CGPoint) -> Void
    private let content: () -> Content

    @State private var globalFrame: CGRect = .zero

    public init(
        _ axes: Axis.Set = .vertical,
        showsIndicators: Bool = true,
        trackOffset: @escaping (CGPoint) -> Void,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.axes = axes
        self.showsIndicators = showsIndicators
        self.trackOffset = trackOffset
        self.content = content
    }

    public var body: some View {
        ScrollView(axes, showsIndicators: showsIndicators) {
            content().getScrollViewOffset { contentPosition in
                let offset = CGPoint(
                    x: -(contentPosition.x - globalFrame.minX),
                    y: -(contentPosition.y - globalFrame.minY)
                )
                trackOffset(offset)
            }
        }
        .getFrame(in: .global) {
            globalFrame = $0
        }
    }
}

// MARK: - Get Scroll Offset

extension View {
    func getScrollViewOffset(perform: @escaping (CGPoint) -> Void) -> some View {
        modifier(ScrollViewOffsetModifier())
            .onPreferenceChange(ScrollViewOffsetPreferenceKey.self) {
                perform($0)
            }
    }
}

public struct ScrollViewOffsetPreferenceKey: PreferenceKey {
    public static var defaultValue: CGPoint = .zero

    public static func reduce(value: inout CGPoint, nextValue: () -> CGPoint) {
        value = nextValue()
    }
}

private struct ScrollViewOffsetModifier: ViewModifier {
    func body(content: Content) -> some View {
        content.overlay(
            GeometryReader { proxy in
                let frame = proxy.frame(in: .global)
                Color.clear.preference(
                    key: ScrollViewOffsetPreferenceKey.self,
                    value: CGPoint(x: frame.minX, y: frame.minY)
                )
            }
        )
    }
}
