//
//  MenuLabel.swift
//
//
//  Created by Duncan Lau on 22/11/2023.
//

import SwiftUI

public struct MenuLabel: View {
    public let title: String
    public let contentShapePadding: CGFloat

    public init(
        title: String,
        contentShapePadding: CGFloat = 0
    ) {
        self.title = title
        self.contentShapePadding = contentShapePadding
    }

    public var body: some View {
        HStack(alignment: .center, spacing: 6) {
            Text(title)
                .fontStyle(.calibreUtility.s.medium)
                .foregroundColor(.chalk.dark700)
                .fixedSize()
            Chevron(direction: .down)
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 2)
        .overlay(
            Capsule()
                .stroke(Color.chalk.dark700, lineWidth: 1)
        )
        .foregroundColor(.chalk.dark700)
        .padding(contentShapePadding)
        .contentShape(Rectangle())
    }
}
