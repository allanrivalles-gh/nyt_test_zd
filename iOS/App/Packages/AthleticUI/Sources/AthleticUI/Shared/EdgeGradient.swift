//
//  EdgeGradient.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 5/9/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

public struct EdgeGradient: View {
    public enum Position {
        case leading, trailing
    }

    let position: Position
    let color: Color?

    public init(position: Position, color: Color? = nil) {
        self.position = position
        self.color = color
    }

    public var body: some View {
        LinearGradient(
            gradient: Gradient(
                stops: [
                    Gradient.Stop(color: gradientColor, location: 0),
                    Gradient.Stop(color: gradientColor.opacity(0), location: 1),
                ]
            ),
            startPoint: position == .leading ? .leading : .trailing,
            endPoint: position == .leading ? .trailing : .leading
        )
        .frame(width: 20)
    }

    private var gradientColor: Color {
        color ?? .chalk.dark200
    }
}
