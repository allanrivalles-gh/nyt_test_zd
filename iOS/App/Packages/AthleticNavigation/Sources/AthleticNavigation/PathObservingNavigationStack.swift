//
//  PathObservingNavigationStack.swift
//
//
//  Created by Mark Corbyn on 25/9/2023.
//

import SwiftUI

public struct PathObservingNavigationStack<Root: View>: View {
    @ObservedObject public var path: NavigationModel.Path

    @ViewBuilder public let root: () -> Root

    public init(path: NavigationModel.Path, @ViewBuilder root: @escaping () -> Root) {
        self.path = path
        self.root = root
    }

    public var body: some View {
        NavigationStack(path: $path.nodes, root: root)
    }
}
