//
//  NetworkModel.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 9/15/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import ApolloWebSocket
import AthleticApolloTypes
import AthleticFoundation
import AthleticRestNetwork
import Combine
import Foundation
import KeychainSwift

public protocol NetworkModelProtocol {
    var apolloClient: ApolloClientProtocol { get }
    var restNetwork: APIManager { get }
    var accessToken: String? { get }
    var apolloStore: ApolloStore { get }
    var isWebSocketEnabled: Bool { get }
    var isWebSocketConnected: Bool { get }
    var isWebSocketReconnectScheduled: Bool { get }
    var webSocketState: AthleticApolloNetwork.WebSocketState { get }
    var webSocketReconnectionCount: Int { get }
    var webSocketKeyEvents: [AthleticApolloNetwork.WebSocketKeyEvent] { get }
    var isReachable: Bool { get }

    func logWebSocketKeyEvent(_ event: String)
}

public typealias NetworkPublisher<T> = AnyPublisher<Result<T, Error>, Never>
public typealias QueryWatcherResultHandler<T> = CompletionResult<GraphQLResult<T>>
public typealias NetworkSubscriptionPublisher<T> = AnyPublisher<T, Never>

final public class NetworkModel: NetworkModelProtocol, ObservableObject {
    final public class SubscriptionsManager {
        public lazy var liveRoom = LiveRoomSubscriptionsManager(apolloClient: client)

        public lazy var liveChat = LiveChatSubscriptionsManager(apolloClient: client)

        public lazy var tournamentGames = LiveScoreSubscriptionsManager(
            apolloClient: client
        ) {
            GQL.TournamentGameUpdatesSubscription(gameIds: $0)
        }

        public lazy var liveScore = LiveScoreSubscriptionsManager(
            apolloClient: client
        ) {
            GQL.LiveScoreUpdatesSubscription(gameIds: $0)
        }

        public lazy var scoresFeedBlock = LiveScoreSubscriptionsManager(apolloClient: client) {
            GQL.ScoresFeedBlockUpdatesSubscription(blockIds: $0)
        }

        public lazy var feedLiveScore = LiveScoreSubscriptionsManager(
            apolloClient: client
        ) {
            GQL.FeedLiveScoreUpdatesSubscription(gameIds: $0)
        }

        public lazy var featuredGameLiveScore = LiveScoreSubscriptionsManager(
            apolloClient: client
        ) {
            GQL.FeaturedGameLiveScoreUpdatesSubscription(gameIds: $0)
        }

        public lazy var boxScore = LiveScoreSubscriptionsManager(
            apolloClient: client
        ) {
            GQL.BoxScoreGameUpdatesSubscription(gameIds: $0)
        }

        private let client: ApolloClientProtocol

        public init(client: ApolloClientProtocol) {
            self.client = client
        }

        public static func shouldSuppress(_ error: Error) -> Bool {
            var error = error
            if let innerError = (error as? WebSocketError)?.error {
                error = innerError
            }

            let nsError = error as NSError

            if nsError.domain == NSPOSIXErrorDomain {
                return [
                    POSIXErrorCode.ENETDOWN,
                    .ENETUNREACH,
                    .ENETRESET,
                    .ECONNRESET,
                    .ENOTCONN,
                    .ESHUTDOWN,
                ]
                .lazy
                .map { Int($0.rawValue) }
                .contains(nsError.code)
            } else if let error = error as? WebSocket.WSError {
                return error.type == .protocolError
                    && [
                        /// See Apollo.WebSocket.swift for close codes.

                        /// Normal close code.
                        1000
                    ]
                    .contains(error.code)
            } else {
                return false
            }
        }
    }

    public lazy var logger = ATHLogger(category: .network)

    public let restNetwork: APIManager
    public let graphNetwork: AthleticApolloNetwork

    public var cancellables = Cancellables()

    public var accessToken: String? {
        keychain.accessToken
    }

    public var apolloClient: ApolloClientProtocol {
        graphNetwork.client
    }

    public var apolloStore: ApolloStore {
        apolloClient.store
    }

    public var isReachable: Bool {
        restNetwork.isReachable
    }

    public var isWebSocketEnabled: Bool {
        graphNetwork.isWebSocketEnabled
    }

    public var isWebSocketConnected: Bool {
        graphNetwork.isWebSocketConnected
    }

    public var isWebSocketReconnectScheduled: Bool {
        graphNetwork.isWebSocketReconnectScheduled
    }

    public var webSocketState: AthleticApolloNetwork.WebSocketState {
        graphNetwork.webSocketState
    }

    public var webSocketReconnectionCount: Int {
        graphNetwork.webSocketReconnectionCount
    }

    public var webSocketKeyEvents: [AthleticApolloNetwork.WebSocketKeyEvent] {
        graphNetwork.webSocketKeyEvents
    }

    public lazy var legacyFeedEncoder: JSONEncoder = {
        let encoder = JSONEncoder()
        encoder.keyEncodingStrategy = .convertToSnakeCase
        encoder.dateEncodingStrategy = .formatted(Date.gmtApiFormatter)
        return encoder
    }()

    private let keychain: ATHKeychain

    public lazy var subscriptionsManager = SubscriptionsManager(client: apolloClient)

    // MARK: - Initialization

    public init(
        graphNetwork: AthleticApolloNetwork,
        restNetwork: APIManager,
        keychain: ATHKeychain = ATHKeychain.main
    ) {
        self.restNetwork = restNetwork
        self.graphNetwork = graphNetwork
        self.keychain = keychain
    }

    public func logWebSocketKeyEvent(_ event: String) {
        graphNetwork.logWebSocketKeyEvent(event)
    }

    public func graphFetch<Query: GraphQLQuery>(
        query: Query,
        cachePolicy: CachePolicy,
        queue: DispatchQueue = .global(qos: .userInitiated)
    ) async throws -> Query.Data {
        try await withCheckedThrowingContinuation { continuation in
            _ = apolloClient.fetch(
                query: query,
                cachePolicy: cachePolicy,
                contextIdentifier: nil,
                queue: queue
            ) { response in
                switch response {
                case .success(let result):
                    guard let data = result.data else {
                        let error: Error = result.errors?.first ?? AthError.failedQueryError
                        continuation.resume(throwing: error)
                        return
                    }
                    continuation.resume(returning: data)

                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }
        }
    }

    public func graphFetchWithSource<Query: GraphQLQuery>(
        query: Query,
        cachePolicy: CachePolicy,
        queue: DispatchQueue = .global(qos: .userInitiated)
    ) async throws -> (Query.Data, GraphQLResult<Query.Data>.Source) {
        try await withCheckedThrowingContinuation { continuation in
            _ = apolloClient.fetch(
                query: query,
                cachePolicy: cachePolicy,
                contextIdentifier: nil,
                queue: queue
            ) { response in
                switch response {
                case .success(let result):
                    guard let data = result.data else {
                        let error: Error = result.errors?.first ?? AthError.failedQueryError
                        continuation.resume(throwing: error)
                        return
                    }

                    continuation.resume(returning: (data, result.source))

                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }
        }
    }

    @discardableResult
    public func graphPerform<Mutation: GraphQLMutation>(
        mutation: Mutation,
        queue: DispatchQueue = .global(qos: .userInitiated)
    ) async throws -> Mutation.Data {
        try await withCheckedThrowingContinuation { continuation in
            _ = apolloClient.perform(
                mutation: mutation,
                publishResultToStore: true,
                queue: queue
            ) { response in
                switch response {
                case .success(let result):
                    guard let data = result.data else {
                        let error: Error = result.errors?.first ?? AthError.failedQueryError
                        continuation.resume(throwing: error)
                        return
                    }

                    continuation.resume(returning: data)

                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }
        }
    }
}

extension GQL.TournamentGameUpdatesSubscription.Data.LiveScoreUpdate: Identifiable {
    public var id: String {
        fragments.tournamentGame.id
    }
}

extension GQL.TournamentGameUpdatesSubscription.Data: LiveScoreSubscriptionData {}

extension GQL.LiveScoreUpdatesSubscription.Data.LiveScoreUpdate: Identifiable {
    public var id: String {
        fragments.gameV2Lite.id
    }
}

extension GQL.ScoresFeedBlockUpdatesSubscription.Data: LiveScoreSubscriptionData {
    public var liveScoreUpdates: GQL.ScoresFeedBlockUpdatesSubscription.Data.ScoresFeedUpdate? {
        scoresFeedUpdates
    }
}

extension GQL.ScoresFeedBlockUpdatesSubscription.Data.ScoresFeedUpdate: Identifiable {
    public var id: String {
        block.fragments.scoresFeedBlock.id
    }
}

extension GQL.LiveScoreUpdatesSubscription.Data: LiveScoreSubscriptionData {}

extension GQL.FeedLiveScoreUpdatesSubscription.Data.LiveScoreUpdate: Identifiable {
    public var id: String {
        fragments.gameV2Lite.id
    }
}

extension GQL.FeedLiveScoreUpdatesSubscription.Data: LiveScoreSubscriptionData {}

extension GQL.BoxScoreGameUpdatesSubscription.Data.LiveScoreUpdate: Identifiable {
    public var id: String {
        fragments.gameV2InGame.fragments.gameV2.id
    }
}

extension GQL.BoxScoreGameUpdatesSubscription.Data: LiveScoreSubscriptionData {}

extension GQL.FeaturedGameLiveScoreUpdatesSubscription.Data: LiveScoreSubscriptionData {}

extension GQL.FeaturedGameLiveScoreUpdatesSubscription.Data.LiveScoreUpdate: Identifiable {
    public var id: String {
        fragments.featuredGameV2.id
    }
}
