//
//  AnalyticData.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 2/4/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

public protocol Analytical {
    var analyticData: AnalyticData { get }
    func trackClickEvent(manager: AnalyticEventManager) async
    func trackViewEvent(manager: AnalyticEventManager) async
}

extension Analytical {
    public func trackClickEvent(manager: AnalyticEventManager = AnalyticsManagers.events) async {
        await Analytics.track(event: analyticData.click, manager: manager)
    }

    public func trackViewEvent(manager: AnalyticEventManager = AnalyticsManagers.events) async {
        await Analytics.track(event: analyticData.view, manager: manager)
    }
}
