//
//  DividerView.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 2/26/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI

public struct DividerView: View {
    public enum Style {
        case small
        case medium
        case large
        case extraLarge

        var size: CGFloat {
            switch self {
            case .small:
                return .singlePoint
            case .medium:
                return 4
            case .large:
                return 6
            case .extraLarge:
                return 8
            }
        }
    }

    public enum Axis {
        case horizontal, vertical
    }

    let style: Style
    let color: Color
    let axis: Axis

    public init(
        style: Style = .small,
        color: Color = .chalk.dark300,
        axis: Axis = .horizontal
    ) {
        self.style = style
        self.color = color
        self.axis = axis
    }

    public var body: some View {
        Rectangle()
            .fill(color)
            .frame(
                width: axis == .vertical ? style.size : nil,
                height: axis == .horizontal ? style.size : nil
            )
    }
}

struct DividerView_Previews: PreviewProvider {
    static var previews: some View {
        let _ = AthleticUI.registerFonts()
        Group {
            VStack(alignment: .leading, spacing: 64) {
                DividerView()
                DividerView(style: .medium, axis: .horizontal)
                DividerView(style: .large, axis: .horizontal)
                DividerView(style: .extraLarge, axis: .horizontal)
            }
            .previewDisplayName("Horizontal - Light")
            .previewLayout(.sizeThatFits)
            .frame(width: 200)
            .padding()

            HStack(alignment: .bottom, spacing: 64) {
                DividerView(axis: .vertical)
                DividerView(style: .medium, axis: .vertical)
                DividerView(style: .large, axis: .vertical)
                DividerView(style: .extraLarge, axis: .vertical)
            }
            .frame(height: 100)
            .previewDisplayName("Vertical - Light")
            .previewLayout(.sizeThatFits)
            .padding()

            Group {
                VStack(alignment: .leading, spacing: 64) {
                    DividerView()
                    DividerView(style: .medium, axis: .horizontal)
                    DividerView(style: .large, axis: .horizontal)
                    DividerView(style: .extraLarge, axis: .horizontal)
                }
                .previewDisplayName("Horizontal - Dark")
                .previewLayout(.sizeThatFits)
                .frame(width: 200)
                .padding()

                HStack(alignment: .bottom, spacing: 64) {
                    DividerView(axis: .vertical)
                    DividerView(style: .medium, axis: .vertical)
                    DividerView(style: .large, axis: .vertical)
                    DividerView(style: .extraLarge, axis: .vertical)
                }
                .frame(height: 100)
                .previewDisplayName("Vertical - Dark")
                .previewLayout(.sizeThatFits)
                .padding()
            }
            .preferredColorScheme(.dark)

        }

    }
}
