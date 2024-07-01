//
//  GradeStar.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 20/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

/// A star used in the player grades feature
struct GradeStar: View {

    let color: Color
    let isFilled: Bool
    let size: CGFloat

    var body: some View {
        Image(systemName: isFilled ? "star.fill" : "star")
            .resizable()
            .font(Font.title.weight(.light))
            .aspectRatio(contentMode: .fit)
            .foregroundColor(color)
            .frame(width: size, height: size)
    }
}
