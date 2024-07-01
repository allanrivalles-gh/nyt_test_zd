//
//  File.swift
//
//
//  Created by Kyle Browning on 11/1/19.
//

import Foundation
import Logging

public class NetworkConfiguration {

    public enum Environment: Equatable, CaseIterable, Codable, Hashable, CustomStringConvertible {
        case production
        case betaiac
        case stage
        case testParty
        case custom(String)

        public var value: String {
            switch self {
            case .stage:
                return "api-staging.theathletic.com"
            case .betaiac:
                return "api-beta-iac.theathletic.com"
            case .production:
                return "api.theathletic.com"
            case .testParty:
                return "api-testparty.theathletic.com"
            case .custom(let value):
                return value
            }
        }

        public var description: String {
            switch self {
            case .stage:
                return "Staging"
            case .production:
                return "Production"
            case .betaiac:
                return "Beta IAC"
            case .testParty:
                return "Test Party"
            case .custom(let value):
                return value
            }
        }

        public static var allCases: [Environment] = [
            .production,
            .betaiac,
            .stage,
            .testParty,
        ]

        public var isCustom: Bool {
            switch self {
            case .custom:
                return true
            default:
                return false
            }
        }
    }

    public struct HeadersConfig {
        public init(
            platform: String,
            client: String,
            version: String,
            language: String,
            userAgent: String
        ) {
            self.platform = platform
            self.client = client
            self.version = version
            self.language = language
            self.userAgent = userAgent
        }

        public let platform: String
        public let client: String
        public let version: String
        public let language: String
        public let userAgent: String
    }

    public let baseURL: URL
    public let session: URLSession
    public let headersConfig: HeadersConfig
    public let logger: Logger?
    public let listeners: [NetworkListener]

    internal var accessToken: String?

    // We have to define a public initializer because when a private property exists, the compiler will NOT synthesize a public initalizer
    // swiftlint:disable force_unwrapping
    public init(
        environment: Environment = .production,
        accessToken: String? = nil,
        configuration: URLSessionConfiguration? = nil,
        headersConfig: HeadersConfig,
        logger: Logger? = nil,
        listeners: [NetworkListener] = []
    ) {
        baseURL = URL(string: "https://\(environment.value)/")!

        var logger = logger
        logger?[metadataKey: "env"] = "\(environment)"
        self.headersConfig = headersConfig

        if let config = configuration {
            self.session = URLSession(configuration: config)
        } else {
            self.session = URLSession.shared
        }
        self.logger = logger
        self.listeners = listeners

        self.accessToken = accessToken
    }
}
