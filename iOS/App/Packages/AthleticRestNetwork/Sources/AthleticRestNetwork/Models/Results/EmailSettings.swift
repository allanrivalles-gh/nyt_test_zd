//
//  EmailSettings.swift
//
//
//  Created by Kyle Browning on 11/7/19.
//

import Foundation

// MARK: - EmailSettings
public struct EmailSettings: Codable {
    public let emailSettings: [EmailSetting]
}

// MARK: - EmailSetting
public struct EmailSetting: Codable {
    public let title, emailType, description: String
    public var value: Bool
    public let index: Int
}

// MARK: - EmailSettingsErrorResult
struct EmailSettingsErrorResult: Codable {
    let error: ErrorModel
}
