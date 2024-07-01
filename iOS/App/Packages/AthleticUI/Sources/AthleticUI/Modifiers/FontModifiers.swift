//
//  FontModifiers.swift
//
//
//  Created by Kyle Browning on 5/11/22.
//

import SwiftUI

struct AthleticStyleFontModifier: ViewModifier {
    @Environment(\.dynamicTypeSize) private var systemDynamicTypeSize

    let style: AthleticFont.Style
    let hugSingleLineHeight: Bool

    func body(content: Content) -> some View {
        let content = content.font(
            .preferredFont(
                for: style,
                dynamicTypeSize: systemDynamicTypeSize
            )
        )

        if hugSingleLineHeight {
            content.hugSingleLineHeight(style)
        } else {
            content
        }
    }
}

struct AthleticCustomFontModifier: ViewModifier {
    @Environment(\.dynamicTypeSize) private var systemDynamicTypeSize

    let name: AthleticFont.Name
    let size: CGFloat

    func body(content: Content) -> some View {
        content
            .font(
                .preferredFont(
                    name: name,
                    defaultSize: size,
                    dynamicTypeSize: systemDynamicTypeSize
                )
            )
    }
}

struct HugSingleLineHeightModifier: ViewModifier {
    @Environment(\.dynamicTypeSize) private var systemDynamicTypeSize

    let style: AthleticFont.Style

    func body(content: Content) -> some View {
        content
            .frame(
                height: UIFont.preferredFont(for: style).pointSize
            )
    }

}

extension View {

    public func fontName(_ name: AthleticFont.Name, size: CGFloat) -> some View {
        ModifiedContent(
            content: self,
            modifier: AthleticCustomFontModifier(name: name, size: size)
        )
    }

    public func fontStyle(
        _ style: AthleticFont.Style,
        hugSingleLineHeight: Bool = false
    ) -> some View {
        ModifiedContent(
            content: self,
            modifier: AthleticStyleFontModifier(
                style: style,
                hugSingleLineHeight: hugSingleLineHeight
            )
        )
    }

    public func hugSingleLineHeight(_ style: AthleticFont.Style) -> some View {
        ModifiedContent(
            content: self,
            modifier: HugSingleLineHeightModifier(style: style)
        )
    }
}
