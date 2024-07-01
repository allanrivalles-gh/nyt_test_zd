//
//  Strings.swift
//  HeadlinesWidgetExtension
//
//  Created by Leonardo da Silva on 24/05/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation

enum Strings: String, Localizable, CaseIterable {

    var bundle: Bundle { .main }
    var baseFilename: String { "Base" }

    case noContentMessage
    case noNetworkMessage
}
