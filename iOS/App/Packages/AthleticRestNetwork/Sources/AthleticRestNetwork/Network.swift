//
//  APIManager.swift
//
//
//  Created by Kyle Browning on 10/30/19.
//

import Combine
import Foundation
import Logging

public typealias ATHNetworkPublisher<T> = AnyPublisher<T, Error>
public typealias ATHNetworkPublisherNever<T> = AnyPublisher<T, Never>

public class Network {
    internal struct K {
        static let authHeaderKey = "Authorization"
        static let platformHeaderKey = "X-Ath-Platform"
        static let clientHeaderKey = "X-Ath-Client"
        static let versionHeaderKey = "X-Ath-Version"
        static let authHeaderKeyNew = "x-ath-auth"
        static let oldVersionHeaderKey = "X-App-Version"
        static let acceptLanguage = "Accept-Language"
        static let userAgent = "User-Agent"
    }

    public enum InitiliazerType {
        case new(
            NetworkConfiguration.Environment,
            NetworkConfiguration.HeadersConfig,
            Logger? = nil,
            [NetworkListener] = []
        )
        case withConfiguration(NetworkConfiguration)
    }

    var configuration: NetworkConfiguration
    var urlFormEncoder = URLFormEncoder.default

    public init(configuration initializierType: InitiliazerType) {
        switch initializierType {
        case .new(let env, let headersConfig, let logger, let listeners):
            self.configuration = NetworkConfiguration(
                environment: env,
                headersConfig: headersConfig,
                logger: logger,
                listeners: listeners
            )
        case .withConfiguration(let config):
            self.configuration = config
        }
    }

    public func updateAccessToken(_ token: String?) {
        configuration.accessToken = token
    }

    @discardableResult
    public func buildRequest(endpoint: Endpoint) -> URLRequest {
        let endpointParams = endpoint.params
        var url = configuration.baseURL
        if let apiVersion = endpoint.apiVersion {
            url.appendPathComponent(apiVersion.path)
        }
        url.appendPathComponent(endpoint.path)
        /// we need to apply our params to the URL for GET requests
        var request: URLRequest = URLRequest(url: url)
        request.httpMethod = endpoint.httpMethod.rawValue
        request = urlFormEncoder.encode(request, with: endpointParams)

        if endpoint.httpEncodingType == .json {
            request.httpBody = endpoint.encoded
        }
        if let logger = configuration.logger {
            if logger.logLevel <= .debug {
                if let data = request.httpBody,
                    let endpointParams = String(bytes: data, encoding: .utf8),
                    let dataString = String(bytes: data, encoding: .utf8)
                {
                    configuration.logger?.trace(
                        "\(endpoint.httpMethod.rawValue) request \(url) - \(endpointParams) - \(dataString)"
                    )
                } else {
                    configuration.logger?.debug(
                        "\(endpoint.httpMethod.rawValue) request \(url) - \(endpointParams)"
                    )
                }
            } else {
                configuration.logger?.debug("\(endpoint.httpMethod.rawValue) request \(url)")
            }
        }
        request.addValue(endpoint.httpEncodingType.contentType, forHTTPHeaderField: "Content-Type")
        if let token = configuration.accessToken {
            request.addValue("Bearer \(token)", forHTTPHeaderField: K.authHeaderKey)
            request.addValue(token, forHTTPHeaderField: K.authHeaderKeyNew)
        }
        request.addValue(
            configuration.headersConfig.platform,
            forHTTPHeaderField: K.platformHeaderKey
        )
        request.addValue(configuration.headersConfig.client, forHTTPHeaderField: K.clientHeaderKey)
        request.addValue(
            configuration.headersConfig.version,
            forHTTPHeaderField: K.versionHeaderKey
        )
        request.addValue(
            configuration.headersConfig.version,
            forHTTPHeaderField: K.oldVersionHeaderKey
        )
        request.addValue(configuration.headersConfig.language, forHTTPHeaderField: K.acceptLanguage)
        request.addValue(configuration.headersConfig.userAgent, forHTTPHeaderField: K.userAgent)
        request.httpMethod = endpoint.httpMethod.rawValue
        return request
    }

    public func executeRequest(request: URLRequest) -> ATHNetworkPublisher<Data> {
        return configuration.session.dataTaskPublisher(for: request)
            .mapError({ [weak self] error -> Error in
                self?.requestFailed(
                    endpoint: request.url?.pathExtension ?? "unknown",
                    error: error,
                    data: nil
                )
                return error
            })
            .customValidate { [weak self] output in
                guard let httpResponse = output.response as? HTTPURLResponse else {
                    throw URLError(.cannotParseResponse)
                }
                try self?.checkStatusCode(httpResponse.statusCode)
                try self?.checkForErrors(output.data, decoder: .snakeCaseConverting)
            }
            .map(\.data)
            .eraseToAnyPublisher()
    }

    private func checkForErrors(_ data: Data, decoder: JSONDecoder) throws {
        guard let foundError = try? decoder.decode(BackendError.self, from: data) else {
            return
        }
        log("General API Error: \(foundError)")
        throw NetworkError.generalAPIError(error: foundError.error.message)
    }

    internal func log(_ string: String) {
        configuration.logger?.info("\(string)")
    }

    private func checkStatusCode(_ statusCode: Int) throws {
        switch statusCode {
        case 401:
            throw NetworkError.userIsNotLoggedIn
        case 404:
            throw NetworkError.resourceIsNotAvailable
        case 405...500:
            throw NetworkError.httpStatusCodeError(statusCode: statusCode)
        default:
            break
        }
    }

    private func requestFinished(endpoint: String, data: Data?) {
        configuration.listeners.forEach { $0.requestFinished(endpoint: endpoint, data: data) }
        guard let logger = configuration.logger else {
            return
        }
        if logger.logLevel <= .debug {
            logger.trace(
                "Request successfuly finished: \(endpoint) data: \(String(describing: data))"
            )
        } else {
            logger.debug("Request successfuly finished: \(endpoint)")
        }
    }

    private func requestFailed(endpoint: String, error: Error, data: Data?) {
        configuration.listeners.forEach {
            $0.requestFailed(endpoint: endpoint, error: error, data: data)
        }
        guard let logger = configuration.logger else {
            return
        }
        if logger.logLevel <= .debug {
            logger.trace("Request failed: \(endpoint) data: \(String(describing: data))")
        } else {
            logger.debug("Request failed: \(endpoint)")
        }
    }
}

public enum HTTPMethod: String {
    case get = "GET"
    case post = "POST"
}
