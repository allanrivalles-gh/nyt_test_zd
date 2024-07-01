//
//  GQLInningHalf+ShortTitle.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 4/6/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import Foundation

extension GQL.InningHalf {
    public func shortTitle(forInning inning: Int) -> String? {
        let title: String
        let number =
            NumberFormatter.ordinal.string(from: NSNumber(value: inning))
            ?? inning.string

        switch self {
        case .top:
            title = Strings.mlbInningTopAbbreviation.localized
        case .middle:
            title = Strings.mlbInningMiddleAbbreviation.localized
        case .bottom:
            title = Strings.mlbInningBottomAbbreviation.localized
        case .over:
            title = Strings.mlbInningOverAbbreviation.localized
        case .__unknown:
            return nil
        }

        return [title, number].joined(separator: " ")
    }
}
