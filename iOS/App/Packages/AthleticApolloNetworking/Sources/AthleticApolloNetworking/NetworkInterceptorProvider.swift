//
//  NetworkInterceptorProvider.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 20/9/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import Foundation
import UIKit

struct NetworkInterceptorProvider: InterceptorProvider {

    /// These properties will remain the same throughout the life of the `InterceptorProvider`,
    /// even though they will be handed to different interceptors.
    private let store: ApolloStore
    private let client: URLSessionClient

    init(
        store: ApolloStore,
        client: URLSessionClient
    ) {
        self.store = store
        self.client = client
    }

    func interceptors<Operation: GraphQLOperation>(
        for operation: Operation
    ) -> [ApolloInterceptor] {
        if UserDefaults.adminEnableResponseStubbing
            && StubbedResponseInterceptor.hasStubbedResponse(for: operation)
        {
            return [
                StubbedResponseInterceptor()
            ]
        } else {
            return [
                HeaderManagementInterceptor(),
                MaxRetryInterceptor(),
                CacheReadInterceptor(store: self.store),
                NetworkFetchInterceptor(client: self.client),
                ResponseLoggingInterceptor(),
                ResponseCodeInterceptor(),
                JSONResponseParsingInterceptor(cacheKeyForObject: self.store.cacheKeyForObject),
                AutomaticPersistedQueryInterceptor(),
                CacheWriteInterceptor(store: self.store),
            ]
        }
    }
}
