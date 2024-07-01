//
//  ScrollViewModifiers.swift
//
//
//  Created by Jason Leyrer on 8/19/22.
//

import SwiftUI

extension ScrollView {
    public func focusResignation<Element: Hashable>(element: FocusState<Element?>) -> some View {
        modifier(ScrollViewResignFocusModifier(focusElement: element))
    }
}

private struct ScrollViewResignFocusModifier<Element: Hashable>: ViewModifier {

    @FocusState var focusElement: Element?

    func body(content: Content) -> some View {
        content
            .onSimultaneousDragGesture(onChanged: { _ in
                focusElement = nil
            })
            .onSimultaneousTapGesture {
                focusElement = nil
            }
    }
}
