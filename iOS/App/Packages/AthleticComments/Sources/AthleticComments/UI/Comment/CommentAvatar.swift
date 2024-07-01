//
//  CommentAvatar.swift
//
//
//  Created by Jason Leyrer on 8/2/22.
//

import SwiftUI

public struct CommentAvatar: View {
    let initial: String
    let backgroundColorHex: String
    @ScaledMetric var size: CGFloat

    public var body: some View {

        Text(initial)
            .fontStyle(.calibreUtility.xs.medium)
            .foregroundColor(.chalk.dark800)
            .darkScheme()
            .frame(width: size, height: size, alignment: .center)
            .background(Color(hex: backgroundColorHex), in: Circle())
    }

    public init(
        initial: String,
        backgroundColorHex: String,
        size: CGFloat
    ) {
        self.initial = initial
        self.backgroundColorHex = backgroundColorHex
        self._size = .init(wrappedValue: size)
    }
}
