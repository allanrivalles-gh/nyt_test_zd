//
//  TemporaryBanViewModel.swift
//
//
//  Created by kevin fremgen on 7/11/23.
//

import Foundation

public struct TemporaryBanViewModel {
    public let daysRemaining: Int

    public var daysRemainingText: String {
        if daysRemaining > 0 {
            return String(
                format: Strings.temporaryBanCtaFormatDays.localized,
                daysRemaining + 1
            )
        } else {
            return Strings.temporaryBanCtaFormatTomorrow.localized
        }
    }

    public init(daysRemaining: Int) {
        self.daysRemaining = daysRemaining
    }
}
