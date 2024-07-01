//
//  TwitterPayload.swift
//
//
//  Created by Eric Yang on 29/4/20.
//

import Foundation

// MARK: TwitterOembedPayload
/// - tag: TwitterOembedPayload
public struct TwitterOembedPayload: Encodable {
    public let url: String
    public let theme: String?

    public init(url: String, isDarkTheme: Bool = false) {
        self.url = url
        self.theme = isDarkTheme ? "dark" : nil
    }
}

public struct TwitterOembedResponse: Codable {
    public let url: String
    public let html: String
}
