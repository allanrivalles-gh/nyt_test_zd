//
//  AthleticButtonStyle.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 22/12/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI

/// Style guide core button styles
public struct AthleticButtonStyle: ButtonStyle {

    public enum Size {
        case regular
        case fitted
        case small
    }

    public enum Level {
        case primary
        case secondary
    }

    public enum IconEdge {
        public struct Set: OptionSet {
            public let rawValue: Int

            public init(rawValue: Int) {
                self.rawValue = rawValue
            }

            public static let leading: Self = IconEdge.Set(rawValue: 1 << 0)
            public static let trailing: Self = IconEdge.Set(rawValue: 1 << 1)

            public static let both: Self = [.leading, .trailing]
            public static let none: Self = []
        }
    }

    private struct Properties {
        let font: Font
        let textColor: Color
        let leadingPadding: CGFloat
        let trailingPadding: CGFloat
        let backgroundColor: Color
        let minHeight: CGFloat
        let maxWidth: CGFloat?
    }

    public let size: Size
    public let level: Level
    public let iconEdges: IconEdge.Set

    public init(size: Size, level: Level, iconEdges: IconEdge.Set) {
        self.size = size
        self.level = level
        self.iconEdges = iconEdges
    }

    @Environment(\.colorScheme) var colorScheme

    private func makeProperties() -> Properties {
        let font: Font
        let leadingPadding: CGFloat
        let trailingPadding: CGFloat
        let minHeight: CGFloat
        let maxWidth: CGFloat?
        switch size {
        case .regular:
            font = .font(for: .calibreUtility.xl.medium)
            leadingPadding = iconEdges.contains(.leading) ? 12 : 20
            trailingPadding = iconEdges.contains(.trailing) ? 12 : 20
            minHeight = 48
            maxWidth = .infinity

        case .fitted:
            font = .font(for: .calibreUtility.l.medium)
            leadingPadding = iconEdges.contains(.leading) ? 12 : 20
            trailingPadding = iconEdges.contains(.trailing) ? 12 : 20
            minHeight = 48
            maxWidth = nil

        case .small:
            font = .font(for: .calibreUtility.s.medium)
            leadingPadding = iconEdges.contains(.leading) ? 8 : 16
            trailingPadding = iconEdges.contains(.trailing) ? 8 : 16
            minHeight = 32
            maxWidth = nil
        }

        let textColor: Color
        let backgroundColor: Color
        switch level {
        case .primary:
            textColor = .chalk.dark200
            backgroundColor = .chalk.dark800
        case .secondary:
            textColor = .chalk.dark700
            backgroundColor = .chalk.dark300
        }

        return Properties(
            font: font,
            textColor: textColor,
            leadingPadding: leadingPadding,
            trailingPadding: trailingPadding,
            backgroundColor: backgroundColor,
            minHeight: minHeight,
            maxWidth: maxWidth
        )
    }

    public func makeBody(configuration: Configuration) -> some View {
        let properties = makeProperties()
        configuration.label
            .font(properties.font)
            .frame(maxWidth: properties.maxWidth, minHeight: properties.minHeight)
            .foregroundColor(properties.textColor)
            .padding(.leading, properties.leadingPadding)
            .padding(.trailing, properties.trailingPadding)
            .background(
                RoundedRectangle(cornerRadius: 2)
                    .fill(properties.backgroundColor)
            )
            .opacity(configuration.isPressed ? 0.5 : 1)
    }
}

extension ButtonStyle where Self == AthleticButtonStyle {
    public static func core(
        size: AthleticButtonStyle.Size,
        level: AthleticButtonStyle.Level,
        iconEdges: AthleticButtonStyle.IconEdge.Set = []
    )
        -> AthleticButtonStyle
    {
        AthleticButtonStyle(size: size, level: level, iconEdges: iconEdges)
    }
}

struct AthleticButtonStyle_Previews: PreviewProvider {

    static var regularStyles: some View {
        VStack(alignment: .leading) {
            Text("Regular")

            Button("Primary", action: {})
                .buttonStyle(.core(size: .regular, level: .primary))

            Button("Secondary", action: {})
                .buttonStyle(.core(size: .regular, level: .secondary))

            Button(action: {}) {
                HStack(spacing: 0) {
                    Image(systemName: "mic.slash")
                    Spacer(minLength: 8)
                    Text("Icon")
                    Spacer()
                }
            }
            .buttonStyle(.core(size: .regular, level: .primary, iconEdges: .leading))
        }
    }

    static var fittedStyles: some View {
        VStack(alignment: .leading) {
            Text("Fitted")

            Button("Primary", action: {})
                .buttonStyle(.core(size: .fitted, level: .primary))

            Button("Secondary", action: {})
                .buttonStyle(.core(size: .fitted, level: .secondary))

            Button(action: {}) {
                HStack(spacing: 8) {
                    Image(systemName: "mic.slash")
                    Text("Icon")
                }
            }
            .buttonStyle(.core(size: .fitted, level: .primary, iconEdges: .leading))
        }
    }

    static var smallStyles: some View {
        VStack(alignment: .leading) {
            Text("Small")

            Button("Primary", action: {})
                .buttonStyle(.core(size: .small, level: .primary))

            Button("Secondary", action: {})
                .buttonStyle(.core(size: .small, level: .secondary))

            Button(action: {}) {
                HStack(spacing: 8) {
                    Text("In Progress")
                    ProgressView().progressViewStyle(
                        CircularProgressViewStyle(
                            tint: .chalk.dark200
                        )
                    )
                }
            }
            .buttonStyle(.core(size: .small, level: .primary, iconEdges: .trailing))
        }
    }

    static var content: some View {
        VStack(alignment: .leading) {
            regularStyles
            fittedStyles
            smallStyles
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .padding(20)
        .background(Color.chalk.dark100)
        .previewLayout(.fixed(width: 375, height: 600))
    }

    static var previews: some View {
        content
            .preferredColorScheme(.dark)
        content
            .preferredColorScheme(.light)
    }

}
