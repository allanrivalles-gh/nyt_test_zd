//
//  PushNotificationProviding.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/19/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import Foundation

protocol PushNotificationProviding: Codable, ExternalDeeplinkMetadata {
    var aps: Aps? { get }
    var url: String? { get }
    var analyticCampaignData: AnalyticCampaignData { get }
}
