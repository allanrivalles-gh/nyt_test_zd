//
//  Image+Placeholder.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 8/12/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

extension Image {
    public static func teamLogoPlaceholder(ofSize size: CGFloat) -> some View {
        placeholder(named: "logo_team_placeholder", size: size)
    }

    public static func playerHeadshotPlaceholder(ofSize size: CGFloat) -> some View {
        placeholder(named: "no-photo-avatar", size: size)
    }

    /// Even though this function is only used privately, when `private` access control is used the Xcode 14.3 compiler strips it out in `Release` configuration, causing a build failure.
    /// Declaring it as `public` prevents it being stripped.
    public static func placeholder(named name: String, size: CGFloat) -> some View {
        Image(name)
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(
                width: size,
                height: size
            )
    }
}
