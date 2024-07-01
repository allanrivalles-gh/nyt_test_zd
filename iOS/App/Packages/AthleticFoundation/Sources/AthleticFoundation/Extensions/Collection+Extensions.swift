//
//  Collection+Extensions.swift
//
//
//  Created by Leonardo da Silva on 15/11/22.
//

import Foundation

extension Collection {
    /// Returns the element at the specified index if it is within bounds, otherwise nil.
    public subscript(safe index: Index) -> Element? {
        return indices.contains(index) ? self[index] : nil
    }

    public func prefixArray(_ count: Int) -> [Element] {
        Array(prefix(count))
    }

    public func suffixArray(_ count: Int) -> [Element] {
        Array(suffix(count))
    }
}
