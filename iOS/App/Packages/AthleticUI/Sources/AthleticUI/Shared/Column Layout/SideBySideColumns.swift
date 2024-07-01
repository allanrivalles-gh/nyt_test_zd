//
//  SwiftUIView.swift
//
//
//  Created by Mark Corbyn on 6/7/2022.
//

import SwiftUI

/// A view that lays out two columns of equal width with a divider view in between.
public struct SideBySideColumns<Leading: View, Trailing: View, Divider: View>: View {

    public init(
        @ViewBuilder leading: () -> Leading,
        @ViewBuilder trailing: () -> Trailing,
        @ViewBuilder divider: () -> Divider,
        alignment: VerticalAlignment = .top
    ) {
        self.leading = leading()
        self.trailing = trailing()
        self.divider = divider()
        self.alignment = alignment
    }

    private let leading: Leading
    private let trailing: Trailing
    private let divider: Divider
    private var alignment: VerticalAlignment

    public var body: some View {
        HStack(alignment: alignment, spacing: 0) {
            leading.frame(maxWidth: .infinity, alignment: .trailing)

            divider

            trailing.frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}
