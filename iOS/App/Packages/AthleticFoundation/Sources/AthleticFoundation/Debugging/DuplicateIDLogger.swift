//
//  DuplicateIDLogger.swift
//
//
//  Created by Mark Corbyn on 1/6/2023.
//

import Foundation

public struct DuplicateIDLogger {

    private static let logger = ATHLogger(category: .ui)

    public static func logDuplicates<T: Identifiable>(
        in items: [T],
        file: String = #file,
        function: String = #function,
        line: Int = #line
    ) {
        let keys: [AnyHashable] = items.map { $0.id }
        logDuplicateKeys(in: keys, file: file, function: function, line: line)
    }

    public static func logDuplicates<T, K>(
        in items: [T],
        id keyPath: KeyPath<T, K>,
        file: String = #file,
        function: String = #function,
        line: Int = #line
    ) where K: Hashable {
        let keys: [K] = items.map { $0[keyPath: keyPath] }
        logDuplicateKeys(in: keys, file: file, function: function, line: line)
    }

    private static func logDuplicateKeys<T: Hashable>(
        in keys: [T],
        file: String,
        function: String,
        line: Int
    ) {
        let uniques = Set(keys)
        if keys.count != uniques.count {
            let counts = keys.reduce([AnyHashable: Int]()) { result, id in
                let existingCount = result[id] ?? 0
                var result = result
                result[id] = existingCount + 1
                return result
            }

            let duplicates = counts.filter { $0.value > 1 }

            assertionFailure(
                "Found duplicate ID's \(duplicates) in the array, this can cause a crash in SwiftUI ForEach. Report this to #eng-ios including the full stack trace and the array values that were received. Source \(file)->\(function):Line \(line)."
            )

            logger.warning(
                "Duplicate ID for ForEach: \(duplicates) in \(file)->\(function):Line \(line)",
                file: file,
                function: function,
                line: UInt(line)
            )
        }
    }

}
