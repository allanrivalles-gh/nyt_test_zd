//
//  OutsideSafeAreaColorModifier.swift
//
//
//  Created by Mark Corbyn on 6/9/2022.
//

import Foundation
import SwiftUI

extension View {
    public func aboveSafeAreaColor(
        _ color: Color,
        animatesOnSizeChange: Bool = false
    ) -> some View {
        modifier(
            OutsideSafeAreaColorModifier(
                color: color,
                edge: .top,
                animatesOnSizeChange: animatesOnSizeChange
            )
        )
    }

    public func belowSafeAreaColor(
        _ color: Color,
        animatesOnSizeChange: Bool = false
    ) -> some View {
        modifier(
            OutsideSafeAreaColorModifier(
                color: color,
                edge: .bottom,
                animatesOnSizeChange: animatesOnSizeChange
            )
        )
    }

    public func navigationBarDefaultBackgroundColor(
        animatesOnSizeChange: Bool = false
    ) -> some View {
        aboveSafeAreaColor(.chalk.dark200, animatesOnSizeChange: animatesOnSizeChange)
    }
}

private struct OutsideSafeAreaColorModifier: ViewModifier {
    let color: Color
    let edge: VerticalEdge
    let animatesOnSizeChange: Bool

    func body(content: Content) -> some View {
        content.overlay(alignment: alignment) {
            GeometryReader { geometry in
                color
                    .ignoresSafeArea(.all, edges: edgeSet)
                    .frame(height: 0)
                    .animation(
                        animatesOnSizeChange ? .linear(duration: 0.1) : nil,
                        value: safeAreaInset(geometry: geometry)
                    )
            }
        }
    }

    private var edgeSet: Edge.Set {
        switch edge {
        case .top:
            return .top
        case .bottom:
            return .bottom
        }
    }

    private var alignment: Alignment {
        switch edge {
        case .top:
            return .top
        case .bottom:
            return .bottom
        }
    }

    private func safeAreaInset(geometry: GeometryProxy) -> CGFloat {
        switch edge {
        case .top:
            return geometry.safeAreaInsets.top
        case .bottom:
            return geometry.safeAreaInsets.bottom
        }
    }
}
