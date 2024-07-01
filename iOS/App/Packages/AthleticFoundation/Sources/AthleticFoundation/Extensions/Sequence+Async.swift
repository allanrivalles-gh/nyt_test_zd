//
//  Sequence+Async.swift
//
//
//  Created by Jason Leyrer on 5/19/23.
//

import Foundation

extension Sequence {
    public func concurrentForEach(_ operation: @escaping (Element) async -> Void) async {
        await withTaskGroup(of: Void.self) { group in
            for element in self {
                group.addTask {
                    await operation(element)
                }
            }
        }
    }
}
