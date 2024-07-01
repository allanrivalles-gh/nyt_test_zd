//
//  ExperimentProtocol.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 1/31/20.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import Foundation

protocol CompassExperiment: Codable {
    static var id: String { get set }
    var id: String { get }
    var variant: CompassExperimentVariant { get set }
    static func initialize(with packet: ExperimentRaw) -> Self?
}

extension CompassExperiment {
    static func initialize<T: Decodable>(with packet: ExperimentRaw) -> T? {
        do {
            return try decoder.decode(
                T.self,
                from: Self.normalize(packet: packet)
            )
        } catch {
            print("Failed to decode compass experiement with error: \(error)")
            return nil
        }
    }
}

extension CompassExperiment {

    static var decoder: JSONDecoder {
        let decoder = JSONDecoder()
        decoder.keyDecodingStrategy = .convertFromSnakeCase
        return decoder
    }

    var id: String {
        Self.id
    }

    static func normalize(packet: ExperimentRaw) throws -> Data {
        var result: [String: Any] = [:]
        guard let data = packet.data else {
            result["variant"] = packet.variant
            return try JSONSerialization.data(withJSONObject: result, options: [])
        }
        for property in data {
            switch property.type {
            case .int: result[property.key] = Int(property.value)
            case .bool: result[property.key] = Bool(property.value)
            case .double: result[property.key] = Double(property.value)
            case .string: result[property.key] = property.value
            }
        }
        result["variant"] = packet.variant
        return try JSONSerialization.data(withJSONObject: result, options: [])
    }
}

extension Collection where Element == CompassExperiment {
    subscript<T: CompassExperiment>(type: T.Type) -> Element? {
        return lazy.compactMap({ $0 as? T }).first
    }
}
