//
//  APIManager.swift
//  theathletic-ios
//
//  Created by Jan Remes on 19/07/16.
//  Copyright © 2016 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticRestNetwork
import Foundation
import Network
import UIKit

class NewNetworkRequestListener: NetworkListener {

    private lazy var logger = ATHLogger(category: .network)

    func requestFailed(endpoint: String, duration: TimeInterval, error: Error, data: Data?) {
        if ProcessInfo.processInfo.isEnabled(.shouldShowJSONResponse) {
            let jsonResult = getJsonDataAsDictionary(data: data)
            logger.warning(
                "Request failed: \(endpoint) - \(error.localizedDescription) - \(jsonResult)"
            )
        } else {
            logger.warning("Request failed: \(endpoint) - \(error.localizedDescription)")
        }
    }

    func requestFinished(endpoint: String, duration: TimeInterval, data: Data?) {
        if ProcessInfo.processInfo.isEnabled(.shouldShowJSONResponse) {
            let jsonResult = getJsonDataAsDictionary(data: data)
            logger.debug("Request finished: \(endpoint) json: \(jsonResult )")
        } else {
            logger.debug("Request finished: \(endpoint)")
        }
    }

    private func getJsonDataAsDictionary(data: Data?) -> NSDictionary {
        guard let data = data else { return [:] }
        guard
            let jsonResult = try? JSONSerialization.jsonObject(with: data, options: [])
                as? NSDictionary
        else {
            return [:]
        }
        return jsonResult
    }
}

final public class APIManager {

    public let network: AthleticRestNetwork.Network

    static let decoder: JSONDecoder = {
        let decoder = JSONDecoder()
        decoder.keyDecodingStrategy = .convertFromSnakeCase
        decoder.dateDecodingStrategy = .formatted(Date.wordpressDateFormatter)
        return decoder
    }()

    static let encoder: JSONEncoder = {
        let encoder = JSONEncoder()
        encoder.keyEncodingStrategy = .convertToSnakeCase
        encoder.dateEncodingStrategy = .formatted(Date.wordpressDateFormatter)
        return encoder
    }()

    // MARK: Network Reachability
    private var monitor: NWPathMonitor?
    private var isMonitoring: Bool {
        return monitor != nil
    }

    public var isReachable: Bool {
        guard let monitor = monitor else { return false }
        return monitor.currentPath.status == .satisfied
    }

    public var availableInterfacesTypes: [NWInterface.InterfaceType]? {
        guard let monitor = monitor else { return nil }
        return monitor.currentPath.availableInterfaces.map { $0.type }
    }

    private let newNetworkListener = NewNetworkRequestListener()

    @objc
    func userUpdated() {
        onMain {
            /// This method is called from multiple threads, update the network on a consistent thread for thread-sefaty
            self.network.updateAccessToken(ATHKeychain.main.accessToken)
        }
    }

    public init(clientName: String, environment: NetworkConfiguration.Environment) {

        let headersConfig: NetworkConfiguration.HeadersConfig = .init(
            platform: "ios",
            client: clientName,
            version: String.getAppVersionSemver,
            language: Locale.current.formatted,
            userAgent: String.userAgent
        )

        let accessToken = ATHKeychain.main.accessToken

        let configuration = NetworkConfiguration(
            environment: environment,
            accessToken: accessToken,
            headersConfig: headersConfig,
            listeners: [newNetworkListener]
        )

        ATHLogger(category: .network).info("Network intialized", .network)
        network = Network(configuration: .withConfiguration(configuration))

        startNetworkListening()

        addNotificationObserverWithObject(
            self,
            selector: #selector(userUpdated),
            name: Notifications.UserCredentialsChanged
        )
    }

    public func startNetworkListening() {
        guard !isMonitoring else {
            return
        }

        monitor = NWPathMonitor()
        monitor?.pathUpdateHandler = { _ in
            postNotification(Notifications.ReachabilityUpdated)  // DO NOT DELETE
        }
        monitor?.start(queue: .main)
    }

    public func pauseNetworkListening() {
        monitor?.cancel()
        monitor = nil
    }

}
