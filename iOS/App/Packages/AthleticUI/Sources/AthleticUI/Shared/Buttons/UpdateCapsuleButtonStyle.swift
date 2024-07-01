//
//  UpdateCapsuleButtonStyle.swift
//
//
//  Created by Jason Leyrer on 8/10/23.
//

import AthleticFoundation
import SwiftUI

public struct UpdateCapsuleButtonStyle: ButtonStyle {

    public init() {}

    public func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .fontStyle(.calibreUtility.l.medium)
            .foregroundColor(
                configuration.isPressed
                    ? Color(.chalk.constant.gray700)
                    : Color(.chalk.constant.gray800)
            )
            .background(Capsule().fill(Color.chalk.red))
    }
}
