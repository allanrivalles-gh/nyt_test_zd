//
//  View+GetSize.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 25/11/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

extension View {
    public func getSize(perform: @escaping (CGSize) -> Void) -> some View {
        modifier(SizeModifier())
            .onPreferenceChange(SizePreferenceKey.self) {
                perform($0)
            }
    }
}

private struct SizePreferenceKey: PreferenceKey {
    static var defaultValue: CGSize = .zero

    static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
        value = nextValue()
    }
}

private struct SizeModifier: ViewModifier {
    func body(content: Content) -> some View {
        content.overlay(
            GeometryReader { geometry in
                Color.clear.preference(
                    key: SizePreferenceKey.self,
                    value: geometry.size
                )
            }
        )
    }
}
