//
//  GameStackedTitlesViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 5/11/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticUI
import Foundation
import SwiftUI
import UIKit

struct GameStackedTitlesViewModel {
    struct Title {
        let text: String
        var style: AthleticFont.Style? = nil
        var color: Color? = nil
    }

    let pretitle: Title?
    let title: Title?
    let subtitle1: Title?
    let subtitle2: Title?
}
