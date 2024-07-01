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

    // MARK: General
    case live
    case tbd

    // MARK: - Periods
    case gqlPeriodKickOffTitle
    case gqlPeriodExtraTimeFirstHalfTitle
    case gqlPeriodExtraTimeSecondHalfTitle
    case gqlPeriodPenaltyShootoutTitle
    case gqlPeriodHalftimeTitle
    case gqlPeriodFullTimeTitle
    case gqlPeriodFinalTitle
    case gqlPeriodPreGameTitle
    case gqlPeriodFirstHalfTitle
    case gqlPeriodSecondHalfTitle
    case gqlPeriodFirstQuarterTitle
    case gqlPeriodSecondQuarterTitle
    case gqlPeriodThirdQuarterTitle
    case gqlPeriodFourthQuarterTitle
    case gqlPeriodOvertimeTitle
    case gqlPeriodOvertimeNumberedTitleFormat
    case gqlPeriodFullTimeOvertimeTitle
    case gqlPeriodFinalOvertimeTitle
    case gqlPeriodFinalOvertimeNumberedTitleFormat
    case gqlPeriodFullTimeShootOutTitle
    case gqlPeriodFinalShootOutTitle
    case gqlPeriodShootoutTitle
    case gqlPeriodFirstPeriodTitle
    case gqlPeriodSecondPeriodTitle
    case gqlPeriodThirdPeriodTitle
    case gqlPeriodUnknownTitle

    // MARK: MLB innings
    case mlbInningTopTitle
    case mlbInningMiddleTitle
    case mlbInningBottomTitle
    case mlbInningOverTitle

    // MARK: - Game Status
    case gameStatusScheduledTitle
    case gameStatusInProgressTitle
    case gameStatusFinalTitle
    case gameStatusSuspendedTitle
    case gameStatusPostponedTitle
    case gameStatusCancelledTitle
    case gameStatusIfNecessaryTitle
    case gameStatusIfNecessaryShortTitle
    case gameStatusUnnecessaryTitle
    case gameStatusUnknownTitle
    case gameStatusDelayedTitle

    case overTimeAbbreviation
    case overTimeAbbreviationNumberedFormat
    case fullTimeOverTimeAbbreviation
    case fullTimeOverTimeAbbreviationNumberedFormat

    case shootoutAbbreviation

    // MARK: - Tickets

    case buyTickets
    case ticketsFromPriceFormat
}
