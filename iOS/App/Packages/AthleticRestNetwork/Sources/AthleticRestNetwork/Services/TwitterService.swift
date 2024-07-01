//
//  TwitterService.swift
//
//
//  Created by Eric Yang on 29/4/20.
//

import Combine
import Foundation
import Logging

public struct TwitterService {
    static var cancellables: [AnyCancellable] = []

    /// Get html from the Twitter oembed API.
    /// - Parameter payload: the instance of [TwitterOembedPayload](x-source-tag://TwitterOembedPayload).
    /// - Returns: The ATHNetworkPublisher with html string.
    ///
    /// ### Usage Example: ###
    ///
    /// let payload = TwitterOembedPayload(url: url)
    /// TwitterService.oembed(for: payload).whenComplete { response in
    ///     switch response {
    ///     case .success(let html):
    ///     case .failure(let error):
    /// }

    public static func oembed(payload: TwitterOembedPayload) -> ATHNetworkPublisher<
        TwitterOembedResponse
    > {
        let headersConfig: NetworkConfiguration.HeadersConfig = .init(
            platform: "ios",
            client: "swift-test",
            version: "-1",
            language: "en-US",
            userAgent: "theathletic-ios/test"
        )
        let network = Network(
            configuration: .new(.custom("publish.twitter.com"), headersConfig)
        )

        return TwitterService.oembedService(payload: payload, network: network)
    }

    /// Get htmls with an url array from the Twitter oembed API.
    /// - Parameter payload: the array of Tweet urls.
    /// - Returns: The ATHNetworkPublisher with <url: html> maps.
    ///
    /// ### Usage Example: ###
    ///
    /// TwitterService.oembed(for: payload).whenComplete { response in
    ///    switch response {
    ///    case .success(let htmlMaps):
    ///    case .failure(let error):
    /// }

    // TODO: add isDarkMode param to method in the future
    public static func oembed(urls: [String]) -> ATHNetworkPublisher<[TwitterOembedResponse]> {
        let headersConfig: NetworkConfiguration.HeadersConfig = .init(
            platform: "ios",
            client: "swift-test",
            version: "-1",
            language: "en-US",
            userAgent: "theathletic-ios/test"
        )
        let network = Network(
            configuration: .new(.custom("publish.twitter.com"), headersConfig)
        )

        var publishers: [ATHNetworkPublisher<TwitterOembedResponse>] = []
        for url in urls {
            let oembedPayload = TwitterOembedPayload(url: url)
            publishers.append(
                TwitterService.oembedService(payload: oembedPayload, network: network)
            )
        }
        return Publishers.MergeMany(publishers).collect().eraseToAnyPublisher()
    }

    // MARK: - Private service
    private static func oembedService(payload: TwitterOembedPayload, network: Network)
        -> ATHNetworkPublisher<TwitterOembedResponse>
    {
        Service.requestAndDecode(for: TwitterEndpoint.twitterOembed(payload: payload), on: network)
    }
}
