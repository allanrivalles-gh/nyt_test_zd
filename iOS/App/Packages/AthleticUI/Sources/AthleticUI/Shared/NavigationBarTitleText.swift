//
//  NavigationBarTitleText.swift
//
//  Created by Mark Corbyn on 3/10/2022.
//

import SwiftUI

public struct NavigationBarTitleText: View {
    private let title: String

    public init(_ title: String) {
        self.title = title
    }

    public var body: some View {
        Text(title)
            .fontStyle(.slab.s.bold)
            .lineLimit(1)
            .padding(.horizontal, 4)
            .frame(maxHeight: .infinity, alignment: .center)
    }
}
