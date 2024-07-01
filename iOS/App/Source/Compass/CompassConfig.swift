//
//  CompassConfig.swift
//  theathletic-ios
//
//  Created by Jan Remes on 13/01/2020.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import Foundation

struct CompassConfig {
    let timestamp: Date
    var experiments: [CompassExperiment]
    let flags: FeatureFlags
}

// MARK: - Experiment
struct ExperimentRaw: Codable {
    let id, variant: String
    let data: [DataPacket]?
}

struct DataPacket: Codable {
    let value: String
    let key: String
    let type: CompassValueType
}

enum CompassValueType: String, Codable {
    case double
    case bool
    case int
    case string
}
