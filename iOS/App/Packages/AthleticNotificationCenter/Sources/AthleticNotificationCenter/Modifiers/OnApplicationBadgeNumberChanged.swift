//
//  OnApplicationBadgeNumberChanged.swift
//
//
//  Created by Jason Leyrer on 8/8/23.
//

import SwiftUI

extension Notification.Name {
    public static let AppIconBadgeNumberChanged = Notification.Name("AppIconBadgeNumberChanged")
}

extension View {
    public func onApplicationBadgeNumberChanged(onChange: @escaping (Int) -> Void) -> some View {
        modifier(OnApplicationBadgeNumberChanged(onChange: onChange))
    }
}

private struct OnApplicationBadgeNumberChanged: ViewModifier {
    var onChange: (Int) -> Void
    @Environment(\.scenePhase) private var scenePhase

    func body(content: Content) -> some View {
        content
            .onAppear {
                invalidateAppIconBadgeNumber()
            }
            .onReceive(NotificationCenter.default.publisher(for: .AppIconBadgeNumberChanged)) { _ in
                invalidateAppIconBadgeNumber()
            }
            .onChange(of: scenePhase) { newPhase in
                switch newPhase {
                case .active:
                    invalidateAppIconBadgeNumber()
                default:
                    break
                }
            }
    }

    private func invalidateAppIconBadgeNumber() {
        onChange(UIApplication.shared.applicationIconBadgeNumber)
    }
}
