//
//  UIApplication+Extensions.swift
//
//
//  Created by Jason Xu on 11/13/23.
//

import Foundation
import SwiftUI

extension UIApplication {
    public var currentUIWindow: UIWindow? {
        let connectedScenes = self.connectedScenes
            .filter({
                $0.activationState == .foregroundActive
            })
            .compactMap({ $0 as? UIWindowScene })

        let window = connectedScenes.first?
            .windows
            .first { $0.isKeyWindow }

        return window

    }

    public var firstSceneWindow: UIWindow? {
        guard
            let scene = self.connectedScenes.first as? UIWindowScene,
            let window = scene.windows.first
        else { return nil }

        return window

    }
}
