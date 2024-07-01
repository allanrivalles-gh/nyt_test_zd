//
//  GQLInningHalf+Title.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 5/11/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

extension GQL.InningHalf {
    public func title(forInning inning: Int) -> String? {
        let title: String
        let number =
            NumberFormatter.ordinal.string(from: NSNumber(value: inning))
            ?? inning.string

        switch self {
        case .top:
            title = Strings.mlbInningTopTitle.localized
        case .middle:
            title = Strings.mlbInningMiddleTitle.localized
        case .bottom:
            title = Strings.mlbInningBottomTitle.localized
        case .over:
            title = Strings.mlbInningOverTitle.localized
        case .__unknown:
            return nil
        }

        return [title, number].joined(separator: " ")
    }
}
