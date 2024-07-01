//
//  DwellTimer.swift
//
//
//  Created by kevin fremgen on 6/26/23.
//

import Foundation

public final class DwellTimer {
    private var seconds: Int = 0
    private var timer: Timer?
    let action: (Int) -> Void

    public init(
        action: @escaping (Int) -> Void
    ) {
        self.action = action
    }

    public func start() {
        /// Invalidate timer
        timer?.invalidate()

        /// Create new timer
        let newTimer = Timer(
            timeInterval: 1.0,
            target: self,
            selector: #selector(tick),
            userInfo: nil,
            repeats: true
        )

        /// Run the timer
        RunLoop.current.add(newTimer, forMode: .common)

        /// Update timer
        timer = newTimer
    }

    @objc
    private func tick() {
        Task { @MainActor in
            seconds += 1
            action(seconds)
        }
    }

    public func stop() {
        timer?.invalidate()
        seconds = 0
    }
}
