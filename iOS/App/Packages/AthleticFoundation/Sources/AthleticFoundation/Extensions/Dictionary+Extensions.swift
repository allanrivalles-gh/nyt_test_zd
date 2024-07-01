//
//  Dictionary+Extensions.swift
//
//
//  Created by Kyle Browning on 5/12/22.
//

import Foundation

extension Dictionary {

    public mutating func merge(with dictionary: Dictionary) {
        dictionary.forEach { updateValue($1, forKey: $0) }
    }

    public func merged(with dictionary: Dictionary) -> Dictionary {
        var dict = self
        dict.merge(with: dictionary)
        return dict
    }
}
