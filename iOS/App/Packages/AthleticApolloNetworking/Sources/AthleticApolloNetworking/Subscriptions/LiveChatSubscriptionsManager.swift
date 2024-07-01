//
//  LiveChatSubscriptionsManager.swift
//  theathletic-ios
//
//  Created by Charles Huang on 10/18/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import AthleticApolloTypes
import AthleticFoundation
import Combine
import Foundation

final public class LiveChatSubscriptionsManager {
    private let apolloClient: ApolloClientProtocol

    private var liveChatUpdatesSubscription: Apollo.Cancellable?
    private var liveChatUpdatesSubject = PassthroughSubject<GQL.ChatNodeDetails, Never>()

    init(apolloClient: ApolloClientProtocol) {
        self.apolloClient = apolloClient
    }

    public func startLiveChatSubscription(chatId: String)
        -> NetworkSubscriptionPublisher<GQL.ChatNodeDetails>
    {
        liveChatUpdatesSubject.handleEvents(
            receiveCancel: { [weak self] in
                self?.liveChatUpdatesSubscription?.cancel()
            },
            receiveRequest: { [weak self] _ in
                self?.liveChatUpdatesSubscription?.cancel()
                self?.liveChatUpdatesSubscription = self?.apolloClient.subscribe(
                    subscription: GQL.ChatEventsSubscription(id: chatId),
                    queue: .global(qos: .userInitiated)
                ) { [weak self] result in
                    switch result {
                    case .success(let response):
                        guard
                            let nodeDetails = response.data?.chatEvents.fragments.chatNodeDetails
                        else {
                            return
                        }

                        self?.liveChatUpdatesSubject.send(nodeDetails)
                    case .failure(let error):
                        self?.handle(error: error)
                    }
                }
            }
        )
        .eraseToAnyPublisher()
    }

    private func handle(error: Error) {
        guard !NetworkModel.SubscriptionsManager.shouldSuppress(error) else {
            return
        }

        ATHLogger(category: .liveRooms)
            .error("Received error from live chat subscription: \(error)", .network)
    }
}
