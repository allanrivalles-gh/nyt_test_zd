//
//  EntityTag.swift
//
//
//  Created by Eric Yang on 17/1/20.
//

import Foundation

// MARK: - EntityTag
public struct EntityTag: Codable {
    public let type: String?
    public let id: IntCodable?
    public let color, label: String?
}
