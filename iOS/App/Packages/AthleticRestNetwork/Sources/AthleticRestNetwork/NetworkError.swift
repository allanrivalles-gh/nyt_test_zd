//
//  File.swift
//
//
//  Created by Kyle Browning on 11/1/19.
//

import Foundation

// MARK: - AthleticBackendError
struct BackendError: Codable {
    let error: ErrorValue
}

// MARK: - ErrorValue
struct ErrorValue: Codable {
    let message: String
}

// MARK: - errors

public enum NetworkError: LocalizedError, Equatable {
    case cancelled
    case userIsNotLoggedIn
    case resourceIsNotAvailable
    case generalNetworkRequestError
    case jsonParsingFailure
    case failedToGetResponse
    case httpStatusCodeError(statusCode: Int)
    case generalAPIError(error: String)
    case enumMappingError
    case encodingError
    case decodingError
}
