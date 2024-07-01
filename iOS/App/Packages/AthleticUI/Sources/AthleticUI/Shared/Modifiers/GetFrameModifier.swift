//
//  GetFrameModifier.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 3/3/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

extension View {
    public func getFrame(
        in coordinateSpace: CoordinateSpace,
        perform: @escaping (CGRect) -> Void
    ) -> some View {
        modifier(GetFrameModifier(coordinateSpace: coordinateSpace))
            .onPreferenceChange(FramePreferenceKey.self) {
                perform($0)
            }
    }
}

private struct FramePreferenceKey: PreferenceKey {
    static var defaultValue: CGRect = .zero

    static func reduce(value: inout CGRect, nextValue: () -> CGRect) {
        value = nextValue()
    }
}

private struct GetFrameModifier: ViewModifier {
    let coordinateSpace: CoordinateSpace

    func body(content: Content) -> some View {
        content.overlay(
            GeometryReader { geometry in
                Color.clear.preference(
                    key: FramePreferenceKey.self,
                    value: geometry.frame(in: coordinateSpace)
                )
            }
        )
    }
}
