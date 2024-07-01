//
//  ExternalDeeplinkMetadata.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/19/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticStorage
import Foundation

protocol ExternalDeeplinkMetadata: Codable {
    var source: String { get }

    func convertToDictionary() -> [String: Any]?
    func saveToFileName(_ name: String) throws
}

extension ExternalDeeplinkMetadata {

    func convertToDictionary() -> [String: Any]? {
        let jsonEncoder = JSONEncoder()
        jsonEncoder.keyEncodingStrategy = .convertToSnakeCase

        guard
            let data = try? jsonEncoder.encode(self),
            let params = try? JSONSerialization.jsonObject(with: data, options: [])
                as? [String: Any]
        else {
            return nil
        }

        return params
    }

    func saveToFileName(_ name: String) throws {
        try Storage.save(self, to: .documents, as: name)
    }
}
