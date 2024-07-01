//
//  LiveRoomSubscriptionsManager.swift
//  theathletic-iosTests
//
//  Created by Tim Korotky on 14/10/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import ApolloWebSocket
import AthleticApolloTypes
import AthleticFoundation
import Combine
import Foundation

final public class LiveRoomSubscriptionsManager {
    private let apolloClient: ApolloClientProtocol

    private var liveRoomUpdatesSubscription: Apollo.Cancellable?
    private var liveRoomUpdatesSubject = PassthroughSubject<GQL.LiveRoomDetails, Never>()

    public init(apolloClient: ApolloClientProtocol) {
        self.apolloClient = apolloClient
    }

    public func startLiveRoomSubscription(roomId: String)
        -> NetworkSubscriptionPublisher<GQL.LiveRoomDetails>
    {
        liveRoomUpdatesSubject.handleEvents(
            receiveCancel: { [weak self] in
                self?.liveRoomUpdatesSubscription?.cancel()
            },
            receiveRequest: { [weak self] _ in
                self?.liveRoomUpdatesSubscription?.cancel()
                self?.liveRoomUpdatesSubscription = self?.apolloClient.subscribe(
                    subscription: GQL.LiveRoomUpdatesSubscription(liveRoomId: roomId),
                    queue: .global(qos: .userInitiated)
                ) { [weak self] result in
                    switch result {
                    case .success(let response):
                        guard
                            let roomDetails =
                                response.data?.updatedLiveRoom.fragments.liveRoomDetails
                        else {
                            return
                        }

                        self?.liveRoomUpdatesSubject.send(roomDetails)
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

        ATHLogger(category: .liveRooms).error(
            "Live Rooms subscription failed with error: \(error))",
            .network
        )
    }
}
