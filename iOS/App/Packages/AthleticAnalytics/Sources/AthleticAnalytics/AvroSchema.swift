//
//  AvroSchema.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 6/02/20.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import Foundation

// MARK: - AvroSchema
struct AvroSchema<AnalyticRecord: Codable>: Codable {
    let schemaID: Int
    let topic: String
    let platform: String
    let version: String
    let records: [AnalyticRecord]

    // MARK: - Decoding
    enum CodingKeys: String, CodingKey {
        case schemaID = "schema_id"
        case platform = "platform"
        case topic = "topic"
        case version = "version"
        case records = "records"
    }

    init(records: [AnalyticRecord], configuration: AnalyticsConfiguration) {
        self.records = records
        self.version = configuration.version
        self.schemaID = configuration.schemaID
        self.topic = configuration.topic
        self.platform = "ios"
    }
}
