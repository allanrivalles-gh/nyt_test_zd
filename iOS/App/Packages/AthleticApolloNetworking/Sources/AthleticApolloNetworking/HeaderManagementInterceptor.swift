//
//  HeaderManagementInterceptor.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 20/9/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import AthleticFoundation
import Foundation

final class HeaderManagementInterceptor: ApolloInterceptor {
    func interceptAsync<Operation: GraphQLOperation>(
        chain: RequestChain,
        request: HTTPRequest<Operation>,
        response: HTTPResponse<Operation>?,
        completion: @escaping CompletionResult<GraphQLResult<Operation.Data>>
    ) {
        if let accessToken = ATHKeychain.main.accessToken {
            request.addHeader(
                name: AthleticApolloNetwork.Constants.authHeaderKey,
                value: "\(accessToken)"
            )
        }

        request.addHeader(
            name: "x-emb-path",
            value: "/graphql/" + request.operation.operationName
        )

        chain.proceedAsync(
            request: request,
            response: response,
            completion: completion
        )
    }
}
