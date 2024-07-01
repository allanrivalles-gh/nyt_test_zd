//
//  BannerViewModel.swift
//
//
//  Created by Kyle Browning on 5/11/22.
//

import Foundation
import SwiftUI

public struct BannerViewModel: Equatable {

    public let imageConfig: ImageConfig?
    public let message: String
    public let textColor: Color
    public let backgroundColor: Color

    public init(
        imageConfig: ImageConfig?,
        message: String,
        textColor: Color,
        backgroundColor: Color
    ) {
        self.imageConfig = imageConfig
        self.message = message
        self.textColor = textColor
        self.backgroundColor = backgroundColor
    }
}
