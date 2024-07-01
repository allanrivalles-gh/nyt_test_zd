//
//  Array+Identifiable+Extensions.swift
//
//
//  Created by Mark Corbyn on 21/6/2023.
//

import Foundation

extension Array where Element: Identifiable {
    public var ids: [AnyHashable] {
        map { $0.id }
    }
}
