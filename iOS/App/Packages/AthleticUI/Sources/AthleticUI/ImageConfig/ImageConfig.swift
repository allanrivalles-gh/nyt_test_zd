//
//  ImageConfig.swift
//
//
//  Created by Kyle Browning on 5/13/22.
//

import Foundation
import SwiftUI

public enum ImageConfig: Equatable, Hashable {
    case system(String, ContentMode)
    case custom(String, ContentMode)
    case customWithBundle(String, Bundle, ContentMode)
}
