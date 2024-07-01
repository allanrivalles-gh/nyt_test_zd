//
//  CGFloat+Utils.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 3/6/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import UIKit

extension CGFloat {

    static var singlePoint: CGFloat { 1 }

    static var singlePixel: CGFloat {
        1 / UIScreen.main.scale
    }

    func ceiling() -> Self {
        ceil(self)
    }
}
