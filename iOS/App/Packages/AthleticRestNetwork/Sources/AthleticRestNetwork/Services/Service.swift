//
//  Service.swift
//
//
//  Created by Kyle Browning on 11/7/19.
//

import Combine
import Foundation

public struct Service<TypeErasedEndpoint: Endpoint> {
    public static func requestAndDecode<Object: Decodable>(
        for endpoint: TypeErasedEndpoint,
        on network: Network
    ) -> AnyPublisher<Object, Error> {
        Service.request(for: endpoint, on: network)
            .decode(type: Object.self, decoder: endpoint.decoder)
            .eraseToAnyPublisher()
    }

    public static func request(for endpoint: TypeErasedEndpoint, on network: Network)
        -> AnyPublisher<Data, Error>
    {
        let request = network.buildRequest(endpoint: endpoint)
        return network.executeRequest(request: request)
    }
}

enum NetworkRequestError: Error {
    case couldNotBuildRequest
}
