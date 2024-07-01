//
//  Strings.swift
//
//
//  Created by Leonardo da Silva on 8/27/22.
//

import AthleticFoundation
import Foundation

internal enum Strings: String, Localizable, CaseIterable {
    var bundle: Bundle { .module }
    var baseFilename: String { "en" }

    case reload
    case upcomingGameTitle
    case seriesScheduleTitle
    case bestOfCount
    case postGamePrefix
    case postGamePrefixSoccer
    case mlbInningTopAbbreviation
    case mlbInningMiddleAbbreviation
    case mlbInningBottomAbbreviation
    case mlbInningOverAbbreviation
}
