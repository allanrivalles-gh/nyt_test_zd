//
//  Bool+Extensions.swift
//
//
//  Created by Kyle Browning on 7/2/22.
//

import Foundation

extension Bool {
    public func toInt() -> Int {
        return self ? 1 : 0
    }
}

extension Bool: Identifiable {
    public var id: Bool { self }
}
