//
//  View+TrackImpressions.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 20/12/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import Combine
import Foundation
import SwiftUI

extension View {
    public func trackImpressions(
        with manager: AnalyticImpressionManager,
        record: @autoclosure @escaping () -> AnalyticsImpressionRecord?,
        containerProxy: GeometryProxy
    ) -> some View {
        modifier(
            ImpressionTrackingModifier(
                viewModel: ImpressionTrackingViewModel(manager: manager, makeRecord: record),
                containerProxy: containerProxy
            )
        )
    }
}

final class ImpressionTrackingViewModel {
    let manager: AnalyticImpressionManager
    let makeRecord: () -> AnalyticsImpressionRecord?

    private(set) var canImpress = true
    private(set) var startTime = Date()

    init(
        manager: AnalyticImpressionManager,
        makeRecord: @escaping () -> AnalyticsImpressionRecord?
    ) {
        self.manager = manager
        self.makeRecord = makeRecord
    }

    func handle(isImpressing: Bool) {
        guard canImpress else { return }

        if isImpressing {
            startTime = Date()
        } else {
            Task {
                await self.sendImpression(impressStartTime: startTime)
            }
            /// Set canImpress to false
            /// We do this because we don't want to impress again unless the view goes completely off screen and then back on
            canImpress = false
        }
    }

    func sendImpression(impressStartTime: Date, impressionEndTime: Date = Date()) async {
        let startTime = impressStartTime.millisecondsSince1970
        let endTime = impressionEndTime.millisecondsSince1970
        guard endTime - startTime >= 500 else { return }
        guard var record = makeRecord() else { return }

        record.impressStartTime = startTime
        record.impressEndTime = endTime
        await manager.track(record: record)
    }

    func allowImpression() {
        canImpress = true
    }
}

private struct ImpressionTrackingModifier: ViewModifier {
    @State var viewModel: ImpressionTrackingViewModel
    let containerProxy: GeometryProxy

    func body(content: Self.Content) -> some View {
        content
            .onFrameVisibilityChanged(
                parentGeometry: containerProxy,
                visibleThreshold: 0.8,
                hiddenThreshold: nil,
                onChanged: viewModel.handle
            )
            .onFrameVisibilityChanged(
                parentGeometry: containerProxy,
                visibleThreshold: 0.1,
                hiddenThreshold: 0.0
            ) { if !$0 { viewModel.allowImpression() } }
    }
}
