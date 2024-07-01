//
//  Strings.swift
//
//
//  Created by Mark Corbyn on 05/11/23.
//

import AthleticFoundation
import Foundation

public enum Strings: String, Localizable, CaseIterable {
    public var bundle: Bundle { .module }
    public var baseFilename: String { "en" }

    // MARK: Game Rows
    case discussionCtaTitle
    case tournamentGroupNameFormat
}
