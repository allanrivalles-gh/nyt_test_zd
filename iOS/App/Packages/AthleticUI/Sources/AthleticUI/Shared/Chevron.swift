//
//  Chevron.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 2/15/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

public struct Chevron: View {
    public enum Direction {
        case up, down, left, right

        fileprivate var rotationAngle: CGFloat {
            switch self {
            case .up:
                return 270
            case .down:
                return 90
            case .left:
                return 180
            case .right:
                return 0
            }
        }
    }

    let foregroundColor: Color
    let width: CGFloat
    let height: CGFloat
    let direction: Direction
    let topPadding: CGFloat

    public init(
        foregroundColor: Color = .chalk.dark800,
        width: CGFloat = 8.5,
        height: CGFloat = 14,
        direction: Chevron.Direction = .right,
        topPadding: CGFloat = 0
    ) {
        self.foregroundColor = foregroundColor
        self.width = width
        self.height = height
        self.direction = direction
        self.topPadding = topPadding
    }

    public var body: some View {
        Image("chevron", bundle: .athleticUI)
            .renderingMode(.template)
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(width: width, height: height)
            .foregroundColor(foregroundColor)
            .rotationEffect(.degrees(direction.rotationAngle))
            .animation(.default, value: direction)
            .padding(.top, topPadding)
    }
}

struct Chevron_Previews: PreviewProvider {
    static var previews: some View {
        Chevron()
    }
}
