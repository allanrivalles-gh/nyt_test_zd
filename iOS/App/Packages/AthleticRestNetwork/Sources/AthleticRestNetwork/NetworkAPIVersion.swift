//
//  File.swift
//
//
//  Created by Kyle Browning on 11/5/19.
//

import Foundation

public enum NetworkAPIVersion: String {
    case v1
    case v3
    case v4
    case v4cached
    case v5
    case v5cached

    var path: String {
        "\(self.rawValue)/"
    }
}
