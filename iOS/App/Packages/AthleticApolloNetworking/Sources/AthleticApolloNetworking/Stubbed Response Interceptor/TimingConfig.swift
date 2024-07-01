//
//  TimingConfig.swift
//
//  Created by Mark Corbyn on 6/11/2023.
//

import Foundation

struct TimingConfig: Codable {
    let fallback: TimeInterval?
    let operations: [String: OperationTimingConfig]
}

struct OperationTimingConfig: Codable {
    let fallback: TimeInterval?
    let sequence: [String: TimeInterval]
}
