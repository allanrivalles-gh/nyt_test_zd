//
//  MacNotSupportedViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 30/10/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticFoundation
import Combine
import Foundation
import SwiftUI

final class MacNotSupportedViewModel: ObservableObject {

    @Published var isModalShowing: Bool = false

    private var nextShowTimer: Timer?

    @AppStorage("lastMacAppMessageDismissalDate")
    private var lastDismissalDate: Date?

    private let timeSettings: TimeSettings
    private var dismissalObserverCancellable: AnyCancellable?

    private let displayFrequency: TimeInterval = 1.day
    private let checkFrequency: TimeInterval = 15.minutes

    private var nextShowDate: Date {
        guard let lastDate = lastDismissalDate else {
            /// If never dismissed it, present it now
            return timeSettings.now()
        }

        return lastDate.addingTimeInterval(displayFrequency)
    }

    init(timeSettings: TimeSettings = SystemTimeSettings()) {
        self.timeSettings = timeSettings
    }

    @MainActor
    func startMessageTimerIfNeeded(invalidateExisting: Bool = false, compass: Compass) {
        guard
            ProcessInfo.processInfo.isiOSAppOnMac
                && compass.config.flags.isMacAppBannerMessageEnabled
        else {
            /// Do nothing for non-Mac or if the message is flagged off.
            /// Invalidate the timer just incase the flag changed from on to off and there's a timer running.
            nextShowTimer?.invalidate()
            nextShowTimer = nil
            return
        }

        guard invalidateExisting || nextShowTimer == nil else {
            /// If there's already a timer running and we haven't been told to invalidate it, do nothing
            return
        }

        displayIfNeeded()

        nextShowTimer?.invalidate()
        nextShowTimer = Timer.scheduledTimer(
            withTimeInterval: checkFrequency,
            repeats: true,
            block: { [weak self] _ in
                self?.displayIfNeeded()
            }
        )

        dismissalObserverCancellable =
            $isModalShowing
            .dropFirst()
            .removeDuplicates()
            .filter { $0 == false }
            .sink { [weak self] _ in
                guard let self else { return }

                self.lastDismissalDate = self.timeSettings.now()
                self.startMessageTimerIfNeeded(invalidateExisting: true, compass: compass)
            }
    }

    private func displayIfNeeded() {
        if nextShowDate <= timeSettings.now() && !isModalShowing {
            isModalShowing = true
        }
    }

}
