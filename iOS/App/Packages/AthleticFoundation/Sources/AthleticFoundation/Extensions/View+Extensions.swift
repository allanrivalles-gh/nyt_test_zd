//
//  View+Extensions.swift
//
//
//  Created by Jason Xu on 3/16/23.
//
import Foundation
import SwiftUI

extension View {
    /// Applies the given transform if the given condition evaluates to `true`.
    /// - Parameters:
    ///   - condition: The condition to evaluate.
    ///   - transform: The transform to apply to the source `View`.
    /// - Returns: Either the original `View` or the modified `View` if the condition is `true`.
    @ViewBuilder
    public func `if`<Content: View>(
        _ condition: Bool,
        transform: (Self) -> Content
    ) -> some View {
        if condition {
            transform(self)
        } else {
            self
        }
    }

    /// Applies the given transform if the given object is not `nil`.
    /// - Parameters:
    ///   - object: The object to test.
    ///   - transform: The transformi to apply to the source `View`.
    /// - Returns: Either the original `View` or the modified `View` and unwrapped object if the object is not `nil`.
    @ViewBuilder
    public func `ifLet`<Content: View, T>(
        _ object: T?,
        transform: (Self, T) -> Content
    ) -> some View {
        if let object {
            transform(self, object)
        } else {
            self
        }
    }
}
