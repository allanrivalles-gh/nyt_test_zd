//
//  TeamColorCurtain.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 1/19/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import SwiftUI

public struct TeamColorCurtain: View {

    public enum Position {
        case leading
        case trailing
    }

    public enum SizeVariant {
        case small
        case large(parentContainerWidth: CGFloat)

        var containerWidth: CGFloat {
            switch self {
            case .small:
                return 78
            case .large:
                return 132
            }
        }

        var trapezoidTopWidth: CGFloat {
            switch self {
            case .small:
                return 80
            case .large:
                return 132
            }
        }

        var trapezoidBottomWidth: CGFloat {
            switch self {
            case .small:
                return 32
            case .large:
                return 78
            }
        }

        var trapezoidSpacing: CGFloat {
            switch self {
            case .small:
                return 18
            case .large:
                return 26
            }
        }

        var height: CGFloat {
            switch self {
            case .small:
                return 120
            case .large:
                return 130
            }
        }

        var padding: CGFloat {
            switch self {
            case .small:
                return 16
            case .large(let parentContainerWidth):
                return parentContainerWidth > 800 ? 0 : 40
            }
        }

        var gradientHeight: CGFloat {
            switch self {
            case .small:
                return 90
            case .large:
                return 100
            }
        }

        func offsetX(index: Int) -> CGFloat {
            -trapezoidSpacing * CGFloat(2 - index) - padding
        }
    }

    let color: Color
    let sizeVariant: SizeVariant
    let position: Position

    public init(color: Color, sizeVariant: SizeVariant, position: Position) {
        self.color = color
        self.sizeVariant = sizeVariant
        self.position = position
    }

    public var body: some View {
        ZStack {
            trapezoid(opacity: 0.5, index: 0)
            trapezoid(opacity: 0.3, index: 1)
            trapezoid(opacity: 0.25, index: 2)
        }
        .frame(width: sizeVariant.containerWidth, height: sizeVariant.height)
        .clipped()
        .scaleEffect(x: position == .leading ? 1 : -1, y: 1, anchor: .center)
        .overlay(alignment: .bottom) {
            Rectangle()
                .fill(
                    LinearGradient(
                        stops: [
                            .init(
                                color: .chalk.dark200.opacity(0),
                                location: 0
                            ),
                            .init(
                                color: .chalk.dark200.opacity(0.77),
                                location: 2 / 3
                            ),
                            .init(
                                color: .chalk.dark200.opacity(0.9635),
                                location: 1
                            ),
                        ],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )
                .frame(height: sizeVariant.gradientHeight)
        }
    }

    private func trapezoid(opacity: CGFloat, index: Int) -> some View {
        Path { path in
            path.move(to: CGPoint(x: 0, y: 0))
            path.addLine(to: CGPoint(x: sizeVariant.trapezoidTopWidth, y: 0))
            path.addLine(to: CGPoint(x: sizeVariant.trapezoidBottomWidth, y: sizeVariant.height))
            path.addLine(to: CGPoint(x: 0, y: sizeVariant.height))
            path.closeSubpath()
        }
        .fill(color)
        .opacity(opacity)
        .offset(x: sizeVariant.offsetX(index: index), y: 0)
    }
}

struct TeamColorCurtain_Previews: PreviewProvider {

    static var previews: some View {
        Group {
            TeamColorCurtain(
                color: Color(hex: "#6A14D6"),
                sizeVariant: .small,
                position: .leading
            )
            .previewDisplayName("iPhone Leading")

            TeamColorCurtain(
                color: Color(hex: "#6A14D6"),
                sizeVariant: .small,
                position: .trailing
            )
            .previewDisplayName("iPhone Trailing")

            TeamColorCurtain(
                color: Color(hex: "#6A14D6"),
                sizeVariant: .large(parentContainerWidth: 800),
                position: .leading
            )
            .previewDisplayName("Small iPad Leading")

            TeamColorCurtain(
                color: Color(hex: "#6A14D6"),
                sizeVariant: .large(parentContainerWidth: 800),
                position: .trailing
            )
            .previewDisplayName("Small iPad Trailing")

            TeamColorCurtain(
                color: Color(hex: "#6A14D6"),
                sizeVariant: .large(parentContainerWidth: 900),
                position: .leading
            )
            .previewDisplayName("Large iPad Leading")

            TeamColorCurtain(
                color: Color(hex: "#6A14D6"),
                sizeVariant: .large(parentContainerWidth: 900),
                position: .trailing
            )
            .previewDisplayName("Large iPad Trailing")
        }
        .previewLayout(.sizeThatFits)
    }
}
