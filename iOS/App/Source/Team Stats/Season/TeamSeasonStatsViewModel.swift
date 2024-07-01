//
//  TeamSeasonStatsViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 19/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation

struct TeamSeasonStatsViewModel: Identifiable {
    struct Item: Identifiable {
        let id: String
        let title: String
        let value: String
        let suffix: String?
        let isTitleDimmed: Bool
        let hasTopDivider: Bool
    }

    let id: String
    let title: String
    let items: [Item]
}

extension TeamSeasonStatsViewModel {
    init?(id: String, models: [GameStat]) {
        guard !models.isEmpty else {
            return nil
        }
        self.id = id
        self.title = Strings.seasonStatsTitle.localized
        self.items = models.map { model in
            Item(
                id: model.id,
                title: model.label,
                value: model.value.displayValue,
                suffix: model.value.secondaryDisplayValue.map({ "\($0)" }),
                isTitleDimmed: model.parentIdentifier != nil,
                hasTopDivider: model.parentIdentifier == nil
            )
        }
    }
}
