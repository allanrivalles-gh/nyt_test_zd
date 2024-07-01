//
//  SplitTabItem.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 12/17/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import SwiftUI

public struct SplitTabItem<Tab>: View where Tab: PagingTab {

    @ObservedObject private var tab: Tab

    let isSelected: Bool
    let tabWidth: CGFloat?
    let horizontalPadding: CGFloat
    let textHorizontalPadding: CGFloat
    let interTabSpacing: CGFloat
    let namespace: Namespace.ID
    let hasSecondaryAction: Bool
    let tapAction: VoidClosure

    @Environment(\.pagingTabSelectedForegroundColor) private var selectedTextColor
    @Environment(\.pagingTabNormalForegroundColor) private var textColor
    @Environment(\.pagingTabUnderscoreStyle) private var underscoreStyle

    public init(
        tab: Tab,
        isSelected: Bool = false,
        tabWidth: CGFloat? = nil,
        horizontalPadding: CGFloat,
        textHorizontalPadding: CGFloat,
        interTabSpacing: CGFloat = 0,
        namespace: Namespace.ID,
        hasSecondaryAction: Bool,
        tapAction: @escaping VoidClosure
    ) {
        self.tab = tab
        self.isSelected = isSelected
        self.tabWidth = tabWidth
        self.horizontalPadding = horizontalPadding
        self.textHorizontalPadding = textHorizontalPadding
        self.interTabSpacing = interTabSpacing
        self.namespace = namespace
        self.hasSecondaryAction = hasSecondaryAction
        self.tapAction = tapAction
    }

    public var body: some View {
        Button(action: { tapAction() }) {
            HStack(spacing: 6) {
                if tab.shouldShowBadge {
                    tab.badge
                }

                Text(tab.title)
                    .fontStyle(.calibreUtility.xl.medium)
                    .foregroundColor(isSelected ? selectedTextColor : textColor)
                    .lineLimit(1)
                    .fixedSize()
                    .frame(maxHeight: .infinity)
                    .overlay(
                        Group {
                            if isSelected && underscoreStyle == .hugText {
                                underscore
                            }
                        },
                        alignment: .bottom
                    )

                if hasSecondaryAction {
                    Image(uiImage: #imageLiteral(resourceName: "icon_nav_expand"))
                }
            }
            .padding(.horizontal, textHorizontalPadding)
            .frame(maxHeight: .infinity)
            .frame(width: tabWidth)
            .padding(.horizontal, horizontalPadding)
            .overlay(
                Group {
                    if isSelected && underscoreStyle == .fillWidth {
                        underscore.padding(.horizontal, -interTabSpacing)
                    }
                },
                alignment: .bottom
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    private var underscore: some View {
        Rectangle()
            .fill(selectedTextColor)
            .frame(height: 2)
            .cornerRadius(2)
            .matchedGeometryEffect(id: "underline", in: namespace)
    }
}
