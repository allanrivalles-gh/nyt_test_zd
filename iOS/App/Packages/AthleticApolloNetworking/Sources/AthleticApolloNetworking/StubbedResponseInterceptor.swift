//
//  StubbedResponseInterceptor.swift
//
//
//  Created by Mark Corbyn on 16/2/2023.
//

import Apollo
import Foundation
import UIKit

struct StubbedResponseInterceptor: ApolloInterceptor {

    private struct ResponseConfig {
        let time: TimeInterval
        let assetName: String
    }

    private static var timing: TimingConfig = {
        guard let configAsset = NSDataAsset(name: "_Timing", bundle: .main) else {
            return TimingConfig(fallback: 0, operations: [:])
        }

        do {
            return try JSONDecoder().decode(TimingConfig.self, from: configAsset.data)
        } catch {
            return TimingConfig(fallback: 0, operations: [:])
        }
    }()

    private static var nextOperationSequenceNumber: [String: Int] = [:]

    static func hasStubbedResponse<Operation: GraphQLOperation>(
        for operation: Operation
    ) -> Bool {
        hasConstantResponse(forOperationName: operation.operationName)
            || hasSequenceResponse(forOperationName: operation.operationName)
    }

    private static func hasConstantResponse(forOperationName operationName: String) -> Bool {
        NSDataAsset(name: operationName, bundle: .main) != nil
    }

    private static func hasSequenceResponse(forOperationName operationName: String) -> Bool {
        NSDataAsset(name: operationName + "_01", bundle: .main) != nil
    }

    func interceptAsync<Operation: GraphQLOperation>(
        chain: RequestChain,
        request: HTTPRequest<Operation>,
        response: HTTPResponse<Operation>?,
        completion: @escaping (Result<GraphQLResult<Operation.Data>, Error>) -> Void
    ) {
        let responseConfig = Self.responseConfig(operationName: request.operation.operationName)

        do {
            guard
                let asset = NSDataAsset(name: responseConfig.assetName, bundle: .main)
            else {
                throw JSONResponseParsingInterceptor.JSONResponseParsingError.noResponseToParse
            }

            guard
                let body = try? JSONSerializationFormat.deserialize(
                    data: asset.data
                ) as? JSONObject
            else {
                throw JSONResponseParsingInterceptor.JSONResponseParsingError.couldNotParseToJSON(
                    data: asset.data
                )
            }

            let graphQLResponse = GraphQLResponse(operation: request.operation, body: body)
            let result = try graphQLResponse.parseResultFast()

            let response = HTTPResponse<Operation>(
                response: .init(),
                rawData: asset.data,
                parsedResponse: result
            )
            response.legacyResponse = graphQLResponse

            DispatchQueue.main.asyncAfter(deadline: .now() + responseConfig.time) {
                chain.proceedAsync(
                    request: request,
                    response: response,
                    completion: completion
                )
            }

        } catch {
            DispatchQueue.main.asyncAfter(deadline: .now() + responseConfig.time) {
                chain.handleErrorAsync(
                    error,
                    request: request,
                    response: response,
                    completion: completion
                )
            }
        }
    }

    private static func responseConfig(operationName: String) -> ResponseConfig {
        let assetName: String
        let responseTime: TimeInterval

        if hasSequenceResponse(forOperationName: operationName) {
            let number = nextOperationSequenceNumber[operationName] ?? 1

            /// Increment the number for the next call if there's an asset available, otherwise leave the current asset as the one returned
            let nextNumber = number + 1
            let possibleNextAssetName = sequencedAssetName(
                operationName: operationName,
                sequence: nextNumber
            )
            if NSDataAsset(name: possibleNextAssetName, bundle: .main) != nil {
                nextOperationSequenceNumber[operationName] = nextNumber
            }

            assetName = sequencedAssetName(operationName: operationName, sequence: number)
            responseTime = timing.responseTime(
                forOperationName: operationName,
                sequenceNumber: number.zeroPadded
            )
        } else {
            assetName = operationName
            responseTime = timing.responseTime(
                forOperationName: operationName,
                sequenceNumber: nil
            )
        }

        return ResponseConfig(
            time: responseTime,
            assetName: assetName
        )
    }

    private static func sequencedAssetName(operationName: String, sequence: Int) -> String {
        operationName + "_" + sequence.zeroPadded
    }
}

extension Int {
    fileprivate var zeroPadded: String {
        String(format: "%02d", self)
    }
}
