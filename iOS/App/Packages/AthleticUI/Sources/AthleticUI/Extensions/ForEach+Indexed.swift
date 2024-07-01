//
//  ForEach+Indexed.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 6/4/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation
import SwiftUI

extension ForEach where Content: View {
    public init<T: Identifiable>(
        indexed items: [T],
        file: String = #file,
        function: String = #function,
        line: Int = #line,
        @ViewBuilder content: @escaping (Int, T) -> Content
    ) where Data == [(Int, T)], ID == T.ID {
        let _ = DuplicateIDLogger.logDuplicates(
            in: Array(zip(items.indices, items)),
            id: \.1.id,
            file: file,
            function: function,
            line: line
        )
        self.init(
            Array(zip(items.indices, items)),
            id: \.1.id,
            content: content
        )
    }

    public init<T>(
        indexed items: [T],
        id: KeyPath<T, ID>,
        file: String = #file,
        function: String = #function,
        line: Int = #line,
        @ViewBuilder content: @escaping (Int, T) -> Content
    ) where Data == [(Int, T)] {
        let _ = DuplicateIDLogger.logDuplicates(
            in: Array(zip(items.indices, items)),
            id: (\Data.Element.1).appending(path: id),
            file: file,
            function: function,
            line: line
        )
        self.init(
            Array(zip(items.indices, items)),
            id: (\Data.Element.1).appending(path: id),
            content: content
        )
    }
}
