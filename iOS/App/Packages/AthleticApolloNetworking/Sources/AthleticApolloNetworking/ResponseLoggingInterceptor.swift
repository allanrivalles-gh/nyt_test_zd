//
//  ResponseLoggingInterceptor.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 20/9/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import AthleticFoundation
import Foundation

final class ResponseLoggingInterceptor: ApolloInterceptor {

    private static var logger = ATHLogger(category: .apollo)

    enum ResponseLoggingError: Error {
        case notYetReceived
    }

    func interceptAsync<Operation: GraphQLOperation>(
        chain: RequestChain,
        request: HTTPRequest<Operation>,
        response: HTTPResponse<Operation>?,
        completion: @escaping CompletionResult<GraphQLResult<Operation.Data>>
    ) {

        defer {
            // Even if we can't log, we still want to keep going.
            chain.proceedAsync(
                request: request,
                response: response,
                completion: completion
            )
        }

        guard let receivedResponse = response else {
            chain.handleErrorAsync(
                ResponseLoggingError.notYetReceived,
                request: request,
                response: response,
                completion: completion
            )
            return
        }

        let isConsoleLoggingEnabled = UserDefaults.adminEnableNetworkConsoleLogging
        let isDiskWritingEnabled = UserDefaults.adminEnableNetworkDiskLogging

        if isConsoleLoggingEnabled || isDiskWritingEnabled {
            if isConsoleLoggingEnabled {
                Self.logger.debug("HTTP Response: \(receivedResponse.httpResponse)")

                if let stringData = String(bytes: receivedResponse.rawData, encoding: .utf8) {
                    Self.logger.debug("Data: \(stringData)")
                } else {
                    Self.logger.error("Could not convert data to string!")
                }
            }

            if isDiskWritingEnabled {
                ResponseDiskWriter.write(request: request, response: receivedResponse)
            }
        }
    }

    static func receivedSocketText(_ text: String) {
        let isConsoleLoggingEnabled = UserDefaults.adminEnableNetworkConsoleLogging
        let isDiskWritingEnabled = UserDefaults.adminEnableNetworkDiskLogging

        guard isConsoleLoggingEnabled || isDiskWritingEnabled else {
            return
        }

        if isConsoleLoggingEnabled {
            logger.debug("Socket Update: \(text)")
        }

        if isDiskWritingEnabled {
            ResponseDiskWriter.write(socketText: text)
        }
    }
}
