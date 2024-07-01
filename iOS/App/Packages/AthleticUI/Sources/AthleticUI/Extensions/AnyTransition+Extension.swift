//
//  AnyTransition+Extension.swift
//
//
//  Created by Duncan Lau on 19/4/2023.
//

import Foundation
import SwiftUI

extension AnyTransition {
    public static func slide(
        insertionEdge: Edge,
        insertionOffsetX: CGFloat = 0,
        insertionOffsetY: CGFloat = 0,
        removalEdge: Edge,
        removalOffsetX: CGFloat = 0,
        removalOffsetY: CGFloat = 0
    ) -> AnyTransition {
        AnyTransition
            .asymmetric(
                insertion:
                    .move(edge: insertionEdge)
                    .combined(with: .offset(x: insertionOffsetX, y: insertionOffsetY)),
                removal:
                    .move(edge: removalEdge)
                    .combined(with: .offset(x: removalOffsetX, y: removalOffsetY))
            )
    }

    public static func intervalFade(
        start: Double = 0,
        end: Double = 1
    ) -> AnyTransition {
        let insertionModifier = IntervalFadeModifier(start: start, end: end, animatableData: 0)
        let removalModifier = IntervalFadeModifier(start: start, end: end, animatableData: 1)
        return .modifier(active: insertionModifier, identity: removalModifier)
    }
}
