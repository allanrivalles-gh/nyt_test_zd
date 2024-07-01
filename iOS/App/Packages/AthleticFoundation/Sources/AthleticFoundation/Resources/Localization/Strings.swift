//
//  Strings.swift
//
//
//  Created by Jason Leyrer on 8/1/22.
//

import Foundation

internal enum Strings: String, Localizable, CaseIterable {
    var bundle: Bundle { .module }
    var baseFilename: String { "en" }

    case earlierToday
    case justNow
    case subscriptionAnnual
    case subscriptionMonthly
    case today
    case yesterday
}
