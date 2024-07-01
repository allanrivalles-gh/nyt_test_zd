//
//  SceneModifier.swift
//
//
//  Created by Duncan Lau on 2/11/2023.
//

import Foundation
import SwiftUI

struct SceneModifier: ViewModifier {
    @Environment(\.scenePhase) private var scenePhase

    var foregroundAction: (() -> Void)? = nil
    var backgroundAction: (() -> Void)? = nil

    func body(content: Content) -> some View {
        content
            .onChange(of: scenePhase) { newPhase in
                switch newPhase {
                case .active:
                    foregroundAction?()
                case .background:
                    backgroundAction?()
                case .inactive:
                    break
                @unknown default:
                    // Fallback for future cases
                    break
                }
            }
    }
}

extension View {
    public func onForeground(
        _ foregroundAction: @escaping () -> Void
    ) -> some View {
        modifier(
            SceneModifier(foregroundAction: foregroundAction)
        )
    }

    public func onBackground(
        _ backgroundAction: @escaping () -> Void
    ) -> some View {
        modifier(
            SceneModifier(backgroundAction: backgroundAction)
        )
    }
}
