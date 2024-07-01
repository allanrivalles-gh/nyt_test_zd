//
//  ApplePushData.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/19/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation

// MARK: - Aps
struct Aps: Codable {
    let alert: AlertMetaData?
    let mutableContent: Int?
    let relevanceScore: Double?
    let interruptionLevel: String?

    enum CodingKeys: String, CodingKey {
        case alert
        case mutableContent = "mutable-content"
        case relevanceScore = "relevance-score"
        case interruptionLevel = "interruption-level"
    }
}

// MARK: - Alert
struct AlertMetaData: Codable {
    let body, title: String
}
