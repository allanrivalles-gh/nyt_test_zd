//
//  FeatureTourViewModel.swift
//
//
//  Created by Kyle Browning on 5/13/22.
//

import SwiftUI

public struct FeatureTourViewModel {
    public var items: [FeatureTourItemViewModel]

    public init(items: [FeatureTourItemViewModel]) {
        self.items = items
    }

    func itemId(afterId: String) -> String? {
        guard let currentIndex = items.firstIndex(where: { $0.id == afterId }) else {
            return nil
        }

        let nextIndex = items.index(after: currentIndex)
        guard nextIndex != items.endIndex else {
            return nil
        }

        return items[nextIndex].id
    }
}
