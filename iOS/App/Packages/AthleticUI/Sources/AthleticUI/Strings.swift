//
//  Strings.swift
//
//
//  Created by Kyle Browning on 7/13/22.
//

import AthleticFoundation
import Foundation

internal enum Strings: String, Localizable, CaseIterable {
    var bundle: Bundle { .module }
    var baseFilename: String { "en" }

    case copyLink
    case createAnAccount
    case featureTourNewIndicator
    case live
    case login
    case openInSafari
    case paywallTitle
    case paywallSubtitle
    case paywallButtonTitle
    case profileStartFreeTrial
    case read
    case reload
    case startReading
    case subscribe
    case viaTheAthleticTwitter
    case next
    case done
    case submit

    // MARK: - Code of conduct
    case codeOfConductTitle
    case codeOfConductHeader
    case codeOfConductSection0Title
    case codeOfConductSection0Info
    case codeOfConductSection1Title
    case codeOfConductSection1Info
    case codeOfConductSection2Title
    case codeOfConductSection2Info
    case codeOfConductSection3Title
    case codeOfConductSection3Info
    case codeOfConductFooter
    case codeOfConductAgreeButton
    case codeOfConductDisagreeButton
}
