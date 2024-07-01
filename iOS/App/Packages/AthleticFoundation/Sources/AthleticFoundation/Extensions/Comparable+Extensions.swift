//
//  Comparable+Extensions.swift
//
//
//  Created by Jason Xu on 10/25/22.
//

import Foundation

extension Comparable {
    public func clamped(to limits: ClosedRange<Self>) -> Self {
        return min(max(self, limits.lowerBound), limits.upperBound)
    }
}
