//
//  GQLPeriod+Title.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 20/5/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import Foundation

extension GQL.Period {
    public func title(for sportType: SportType) -> String {
        switch self {
        case .kickOff:
            return Strings.gqlPeriodKickOffTitle.localized
        case .extraTimeFirstHalf:
            return Strings.gqlPeriodExtraTimeFirstHalfTitle.localized
        case .extraTimeSecondHalf:
            return Strings.gqlPeriodExtraTimeSecondHalfTitle.localized
        case .penaltyShootout:
            return Strings.gqlPeriodPenaltyShootoutTitle.localized
        case .halfTime:
            return Strings.gqlPeriodHalftimeTitle.localized
        case .fullTime:
            return sportType == .soccer
                ? Strings.gqlPeriodFullTimeTitle.localized
                : Strings.gqlPeriodFinalTitle.localized
        case .preGame:
            return Strings.gqlPeriodPreGameTitle.localized
        case .firstHalf:
            return Strings.gqlPeriodFirstHalfTitle.localized
        case .secondHalf:
            return Strings.gqlPeriodSecondHalfTitle.localized
        case .firstQuarter:
            return Strings.gqlPeriodFirstQuarterTitle.localized
        case .secondQuarter:
            return Strings.gqlPeriodSecondQuarterTitle.localized
        case .thirdQuarter:
            return Strings.gqlPeriodThirdQuarterTitle.localized
        case .fourthQuarter:
            return Strings.gqlPeriodFourthQuarterTitle.localized
        case .overTime:
            return Strings.gqlPeriodOvertimeTitle.localized
        case .overTime_2:
            return String(format: Strings.gqlPeriodOvertimeNumberedTitleFormat.localized, 2)
        case .overTime_3:
            return String(format: Strings.gqlPeriodOvertimeNumberedTitleFormat.localized, 3)
        case .overTime_4:
            return String(format: Strings.gqlPeriodOvertimeNumberedTitleFormat.localized, 4)
        case .overTime_5:
            return String(format: Strings.gqlPeriodOvertimeNumberedTitleFormat.localized, 5)
        case .overTime_6:
            return String(format: Strings.gqlPeriodOvertimeNumberedTitleFormat.localized, 6)
        case .overTime_7:
            return String(format: Strings.gqlPeriodOvertimeNumberedTitleFormat.localized, 7)
        case .overTime_8:
            return String(format: Strings.gqlPeriodOvertimeNumberedTitleFormat.localized, 8)
        case .overTime_9:
            return String(format: Strings.gqlPeriodOvertimeNumberedTitleFormat.localized, 9)
        case .overTime_10:
            return String(format: Strings.gqlPeriodOvertimeNumberedTitleFormat.localized, 10)
        case .fullTimeOt:
            return sportType == .soccer
                ? Strings.gqlPeriodFullTimeOvertimeTitle.localized
                : Strings.gqlPeriodFinalOvertimeTitle.localized
        case .fullTimeOt_2:
            return String(format: Strings.gqlPeriodFinalOvertimeNumberedTitleFormat.localized, 2)
        case .fullTimeOt_3:
            return String(format: Strings.gqlPeriodFinalOvertimeNumberedTitleFormat.localized, 3)
        case .fullTimeOt_4:
            return String(format: Strings.gqlPeriodFinalOvertimeNumberedTitleFormat.localized, 4)
        case .fullTimeOt_5:
            return String(format: Strings.gqlPeriodFinalOvertimeNumberedTitleFormat.localized, 5)
        case .fullTimeOt_6:
            return String(format: Strings.gqlPeriodFinalOvertimeNumberedTitleFormat.localized, 6)
        case .fullTimeOt_7:
            return String(format: Strings.gqlPeriodFinalOvertimeNumberedTitleFormat.localized, 7)
        case .fullTimeOt_8:
            return String(format: Strings.gqlPeriodFinalOvertimeNumberedTitleFormat.localized, 8)
        case .fullTimeOt_9:
            return String(format: Strings.gqlPeriodFinalOvertimeNumberedTitleFormat.localized, 9)
        case .fullTimeOt_10:
            return String(format: Strings.gqlPeriodFinalOvertimeNumberedTitleFormat.localized, 10)
        case .fullTimeSo:
            return sportType == .soccer
                ? Strings.gqlPeriodFullTimeShootOutTitle.localized
                : Strings.gqlPeriodFinalShootOutTitle.localized
        case .shootout:
            return Strings.gqlPeriodShootoutTitle.localized
        case .firstPeriod:
            return Strings.gqlPeriodFirstPeriodTitle.localized
        case .secondPeriod:
            return Strings.gqlPeriodSecondPeriodTitle.localized
        case .thirdPeriod:
            return Strings.gqlPeriodThirdPeriodTitle.localized
        case .inningBottom:
            return Strings.mlbInningBottomTitle.localized
        case .inningDelayed:
            return Strings.gameStatusDelayedTitle.localized
        case .inningMiddle:
            return Strings.mlbInningMiddleTitle.localized
        case .inningOver:
            return Strings.mlbInningOverTitle.localized
        case .inningTop:
            return Strings.mlbInningTopTitle.localized
        case .__unknown:
            return Strings.gqlPeriodUnknownTitle.localized
        }
    }

    var shortTitle: String? {
        switch self {
        case .firstQuarter, .firstPeriod, .firstHalf:
            return NumberFormatter.ordinal.string(from: 1)?.uppercased()
        case .secondQuarter, .secondPeriod, .secondHalf:
            return NumberFormatter.ordinal.string(from: 2)?.uppercased()
        case .thirdQuarter, .thirdPeriod:
            return NumberFormatter.ordinal.string(from: 3)?.uppercased()
        case .fourthQuarter:
            return NumberFormatter.ordinal.string(from: 4)?.uppercased()
        case .overTime:
            return Strings.overTimeAbbreviation.localized
        case .overTime_2:
            return String(format: Strings.overTimeAbbreviationNumberedFormat.localized, 2)
        case .overTime_3:
            return String(format: Strings.overTimeAbbreviationNumberedFormat.localized, 3)
        case .overTime_4:
            return String(format: Strings.overTimeAbbreviationNumberedFormat.localized, 4)
        case .overTime_5:
            return String(format: Strings.overTimeAbbreviationNumberedFormat.localized, 5)
        case .fullTimeOt:
            return Strings.fullTimeOverTimeAbbreviation.localized
        case .fullTimeOt_2:
            return String(format: Strings.fullTimeOverTimeAbbreviationNumberedFormat.localized, 2)
        case .fullTimeOt_3:
            return String(format: Strings.fullTimeOverTimeAbbreviationNumberedFormat.localized, 3)
        case .fullTimeOt_4:
            return String(format: Strings.fullTimeOverTimeAbbreviationNumberedFormat.localized, 4)
        case .fullTimeOt_5:
            return String(format: Strings.fullTimeOverTimeAbbreviationNumberedFormat.localized, 5)
        case .halfTime:
            return Strings.gqlPeriodHalftimeTitle.localized
        case .shootout:
            return Strings.shootoutAbbreviation.localized
        default:
            return nil
        }
    }
}
