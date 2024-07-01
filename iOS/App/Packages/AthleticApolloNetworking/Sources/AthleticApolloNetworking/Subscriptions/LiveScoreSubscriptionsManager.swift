//
//  LiveScoreSubscriptionsManager.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 10/7/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import ApolloWebSocket
import AthleticFoundation
import Combine
import Foundation

public protocol LiveScoreSubscriptionData {
    associatedtype LiveScoreUpdate: Identifiable where LiveScoreUpdate.ID == String

    var liveScoreUpdates: LiveScoreUpdate? { get }
}

public actor LiveScoreSubscriptionsManager<Subscription: GraphQLSubscription>
where Subscription.Data: LiveScoreSubscriptionData {
    private let apolloClient: ApolloClientProtocol
    private let createSubscription: ([String]) -> Subscription

    private let liveScoreSubject = PassthroughSubject<Subscription.Data, Never>()
    private var liveScoreSubscriptionCount: [String: Int] = [:]
    private var liveScoreSubscription: Apollo.Cancellable?

    private lazy var logger = ATHLogger(category: .scores)

    init(
        apolloClient: ApolloClientProtocol,
        createSubscription: @escaping ([String]) -> Subscription
    ) {
        self.apolloClient = apolloClient
        self.createSubscription = createSubscription
    }

    public nonisolated func updates(
        forIds ids: Set<String>
    ) -> AnyPublisher<Subscription.Data.LiveScoreUpdate, Never> {
        liveScoreSubject
            .handleEvents(
                receiveCancel: {
                    Task {
                        await self.unsubscribeFromLiveScoreUpdates(ids: ids)
                    }
                },
                receiveRequest: { _ in
                    Task {
                        await self.subscribeToLiveScoreUpdates(ids: ids)
                    }
                }
            )
            .compactMap { $0.liveScoreUpdates }
            .filter { ids.contains($0.id) }
            .eraseToAnyPublisher()
    }

    private func subscribeToLiveScoreUpdates(ids: Set<String>) {
        let oldKeys = Set(liveScoreSubscriptionCount.keys)

        ids.forEach { id in
            liveScoreSubscriptionCount[id] = (liveScoreSubscriptionCount[id] ?? 0) + 1
        }

        if oldKeys != Set(liveScoreSubscriptionCount.keys) {
            stopLiveScoreSubscription()
            startLiveScoreSubscriptionIfNeeded()
        }
    }

    private func unsubscribeFromLiveScoreUpdates(ids: Set<String>) {
        let oldKeys = Set(liveScoreSubscriptionCount.keys)

        ids.forEach { id in
            guard let count = liveScoreSubscriptionCount[id] else {
                return
            }
            let newCount = count - 1

            if newCount > 0 {
                liveScoreSubscriptionCount[id] = newCount
            } else {
                liveScoreSubscriptionCount.removeValue(forKey: id)
            }
        }

        if oldKeys != Set(liveScoreSubscriptionCount.keys) {
            stopLiveScoreSubscription()
            startLiveScoreSubscriptionIfNeeded()
        }
    }

    private func startLiveScoreSubscriptionIfNeeded() {
        let ids = Array(liveScoreSubscriptionCount.keys)

        guard !ids.isEmpty else {
            return
        }

        liveScoreSubscription = apolloClient.subscribe(
            subscription: createSubscription(ids),
            queue: .global(qos: .userInitiated)
        ) { [logger, weak self] result in
            guard let self = self else { return }

            Task {
                switch result {
                case let .success(result):
                    if let data = result.data {
                        self.liveScoreSubject.send(data)
                    } else if let error = result.errors?.first {
                        await self.handle(error: error)
                    } else {
                        logger.error("Could not unpack GQL live score update payload", .network)
                    }

                case let .failure(error):
                    await self.handle(error: error)
                }
            }
        }
    }

    private func stopLiveScoreSubscription() {
        liveScoreSubscription?.cancel()
        liveScoreSubscription = nil
    }

    private func handle(error: Error) {
        guard !NetworkModel.SubscriptionsManager.shouldSuppress(error) else {
            return
        }

        logger.error("Received error from live score updates: \(error)", .network)
    }
}
