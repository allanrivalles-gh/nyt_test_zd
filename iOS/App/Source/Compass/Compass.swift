//
//  CompassRemoteConfig.swift
//  theathletic-ios
//
//  Created by Jan Remes on 13/01/2020.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticStorage
import CoreTelephony
import Foundation
import UIKit

enum CompassError: Error {
    case requestFailure
}

final class Compass: ObservableObject {

    struct ConfigRaw: Codable {
        let experiments: [ExperimentRaw]
        let timestamp: Date
        let flags: [DataPacket]

        init(date: Date) {
            self.timestamp = date
            self.experiments = []
            self.flags = []
        }
    }

    private let session: URLSession
    private let user: UserModeling

    @Storable(
        keyName: Constants.configCacheKey,
        defaultValue: ConfigRaw(date: Date.distantPast)
    )

    private var rawConfig: ConfigRaw {
        didSet {
            Task {
                await mainActorSet(\.config, convert(config: rawConfig))
            }
        }
    }

    private let registeredExperiments: [CompassExperiment.Type]

    private var baseURL: URL {
        return UserDefaults.adminEnableCompassStaging ? Constants.stageURL : Constants.prodURL
    }

    private lazy var logger = ATHLogger(category: .compass)

    @Published
    public var config: CompassConfig

    // add storable for override experiments
    @Storable(
        keyName: StorageLocation.overrideExperiments.rawValue,
        defaultValue: [:],
        lifetime: -1
    )

    var overrideExperiments: [String: String] {
        didSet {
            Task {
                try? await fetchDB(isInteractive: true)
            }
        }
    }

    @UserDefault(key: "Compass.overrideFlags", defaultValue: [:])
    private(set) var overrideFlags: [String: Any]! {
        didSet {
            Task {
                try? await fetchDB(isInteractive: true)
            }
        }
    }

    func updateOverridenExperiments(id: String, value: CompassExperimentVariant?) {
        guard let value = value else {
            overrideExperiments.removeValue(forKey: id)
            config = convert(config: rawConfig)
            return
        }
        overrideExperiments[id] = value.rawValue
        config = convert(config: rawConfig)
    }

    func updateOverridenFlag(key: String, value: Any?) {
        if let value {
            overrideFlags[key] = value
        } else {
            overrideFlags.removeValue(forKey: key)
        }
        config = convert(config: rawConfig)
    }

    func clearOverridenFlags() {
        overrideFlags.removeAll()
        config = convert(config: rawConfig)
    }

    private func convert(config raw: ConfigRaw) -> CompassConfig {
        let experiments = normalizeExperiments(config: raw)
        let flags = normalizeFlags(flags: raw.flags)

        return CompassConfig(
            timestamp: Date.distantPast,
            experiments: experiments,
            flags: flags
        )
    }

    private func normalizeExperiments(config raw: ConfigRaw) -> [CompassExperiment] {

        var experiments: [CompassExperiment] = []
        for ourRegisteredExperiments in registeredExperiments {
            /// based on the constraints here, there should only ever been one experiment with this id
            let firstFoundExp = raw.experiments.lazy.first { $0.id == ourRegisteredExperiments.id }
            /// Check to see if the experiement even exists
            guard let foundExperiment = firstFoundExp else { continue }
            /// Normalize ourselves to a data structure
            if var normalized = ourRegisteredExperiments.initialize(with: foundExperiment) {
                if let foundVariant = overrideExperiments[normalized.id],
                    let newVariant = CompassExperimentVariant(rawValue: foundVariant)
                {
                    normalized.variant = newVariant
                }
                experiments.append(normalized)
            }
        }
        return experiments
    }

    private func normalizeFlags(flags: [DataPacket]) -> FeatureFlags {

        var dictionary: [String: Any] = [:]
        for packet in flags {
            switch packet.type {
            case .bool:
                dictionary[packet.key] = packet.value.bool
            case .int:
                dictionary[packet.key] = Int(packet.value)
            case .double:
                dictionary[packet.key] = Double(packet.value)
            case .string:
                dictionary[packet.key] = packet.value
            }
        }

        return FeatureFlags(remoteValues: dictionary, overridingValues: overrideFlags)
    }

    struct Constants {
        static let configCacheKey = "CompassConfig"
        static let stageURL: URL = URL(string: "https://api-staging.theathletic.com/compass")!
        static let prodURL: URL = URL(string: "https://api.theathletic.com/compass")!

        static var urlSession: URLSession = {
            let configuration = URLSessionConfiguration.default
            configuration.requestCachePolicy = .reloadIgnoringLocalCacheData
            configuration.shouldUseExtendedBackgroundIdleMode = true

            let versionString = String.getAppVersionString()
            let userAgentString = String.userAgent

            configuration.httpAdditionalHeaders = [
                "Accept-Language": Locale.current.formatted,
                "User-Agent": userAgentString,
                "X-App-Version": versionString,
                "Content-Type": "application/json",
            ]

            configuration.timeoutIntervalForRequest = 15.0
            return URLSession(configuration: configuration)
        }()

        static let decoder: JSONDecoder = {
            let decoder = JSONDecoder()
            decoder.keyDecodingStrategy = .convertFromSnakeCase
            decoder.dateDecodingStrategy = .iso8601
            return decoder
        }()
    }

    private var lastCompassConfigFetchDate: Date {
        get {
            return UserDefaults.lastCompassConfigFetchDate
        }
        set(value) {
            UserDefaults.lastCompassConfigFetchDate = value
        }
    }

    init(
        urlSession: URLSession = Constants.urlSession,
        user: UserModeling,
        featureFlags: FeatureFlags = FeatureFlags()
    ) {
        self.session = urlSession
        self.user = user

        /// If an experiment needs to exist on all platforms add here
        var allRegisteredExperiments: [CompassExperiment.Type] = [
            PushNotificationsPromptExperiment.self,
            ArticleBoxScoreExperiment.self,
        ]

        /// Use these for platform specific experiments
        let iPhoneOnlyExperiments: [CompassExperiment.Type] = []

        let iPadOnlyExperiments: [CompassExperiment.Type] = []

        if isIpad() {
            allRegisteredExperiments += iPadOnlyExperiments
        } else {
            allRegisteredExperiments += iPhoneOnlyExperiments
        }

        self.registeredExperiments = allRegisteredExperiments

        /// Default empty configs
        self.config = CompassConfig(
            timestamp: Date.distantPast,
            experiments: [],
            flags: featureFlags
        )
    }

    @MainActor
    func loadFromDiskIfAvailable() async {
        config = convert(config: rawConfig)
    }

    // MARK: Request Builder

    private func buildGetRequest(with path: String, parameters: [String: Any] = [:]) -> URLRequest {
        var url = baseURL.appendingPathComponent(path)
        var request = URLRequest(url: url)
        var comps = URLComponents(url: url, resolvingAgainstBaseURL: false)!
        comps.queryItems = parameters.map { URLQueryItem(name: $0.key, value: "\($0.value)") }
        url = comps.url!
        request.url = url
        request.httpMethod = "GET"
        return request
    }

    private func buildPostRequest<Post: Encodable>(
        with path: String,
        parameters: [String: Any] = [:],
        post: Post
    ) -> URLRequest {

        var url = baseURL.appendingPathComponent(path)
        var request = URLRequest(url: url)
        var comps = URLComponents(url: url, resolvingAgainstBaseURL: false)!
        comps.queryItems = parameters.map { URLQueryItem(name: $0.key, value: "\($0.value)") }
        url = comps.url!
        request.url = url
        request.httpMethod = "POST"
        let encoder = JSONEncoder()
        encoder.keyEncodingStrategy = .convertToSnakeCase
        request.httpBody = try? encoder.encode(post)
        return request
    }

    // MARK: Clear

    func removeAllData() {
        lastCompassConfigFetchDate = Date.distantPast
        rawConfig = .init(date: Date.distantPast)
    }

    private func generateParams() -> [String: Any] {
        /// Do actual refresh request
        let lastChangeDate = Date.iso8601Formatter.string(from: config.timestamp)

        var params: [String: Any] = [
            "explorer_type": "ios",
            "bundle_identifier": Bundle.main.infoDictionary?["CFBundleName"] as? String ?? "",
            "identifier": UserSettings.deviceToken,
            "type": isIpad() ? "tablet" : "mobile",
            "locale": Locale.current.formatted,
            "os_version": UIDevice.current.systemVersion,
            "app_version": String.getAppVersionSemver,
            "model": String.platform,
            "brand": "Apple",
            "last_change_date": lastChangeDate,
            "device_push_token": UserDefaults.devicePushToken,
        ]

        overrideExperiments.enumerated().forEach { index, experiment in
            params["forced_experiments[\(index)]"] = "\(experiment.key),\(experiment.value)"
        }

        if let userId = user.current?.id, let userIdInt = Int(userId) {
            params["user_identifier"] = userIdInt
        }

        return params
    }

    // MARK: Fetch

    @MainActor
    func fetchDB(isInteractive: Bool = false) async throws {
        let storedFlags = normalizeFlags(flags: rawConfig.flags)
        let timeInterval = storedFlags.compassFetchTTL

        /// Check Last Refresh Date
        guard
            isInteractive
                || abs(lastCompassConfigFetchDate.timeIntervalSinceNow)
                    > Double(timeInterval)
        else {
            logger.debug("Compass config fetched recently, returning disk storage")
            await loadFromDiskIfAvailable()
            return
        }

        let params = generateParams()
        logger.debug("Fetching compass config file, params: \(params)")

        let getRequest = buildGetRequest(
            with: "/v4/config",
            parameters: params
        )

        let (data, response) = try await session.data(for: getRequest)
        guard
            let response = response as? HTTPURLResponse,
            let stringData = String(bytes: data, encoding: .utf8)
        else {
            self.logger.error("Fetching Compass failed, wrong response")
            throw CompassError.requestFailure
        }

        if response.statusCode == 200 {
            let config = try Constants.decoder.decode(ConfigRaw.self, from: data)
            self.logger.trace("Fetched Compass: \(config)")
            self.rawConfig = config
            self.lastCompassConfigFetchDate = Date()
        } else if response.statusCode == 304 {
            self.lastCompassConfigFetchDate = Date()
            self.logger.trace("Fetched Compass response: \(stringData)")
            await loadFromDiskIfAvailable()
        } else {
            self.logger.error("Fetching Compass failed, response: \(response)")
            throw CompassError.requestFailure
        }
    }

    // MARK: Expose
    func expose(to experiment: CompassExperiment) async throws {
        logger.debug("exposing on \(experiment.id) with \(experiment.variant.rawValue)")
        try await self.expose(
            experimentId: experiment.id,
            variantId: experiment.variant.rawValue
        )
    }

    @MainActor
    private func expose(
        experimentId: String,
        variantId: String
    ) async throws {
        struct ExposePayload: Encodable {
            let identity: UserIdentity
            let experimentId: String
            let variantId: String
        }
        struct UserIdentity: Encodable {
            let deviceId: String
            var userId: Int?
        }

        var userIdentity = UserIdentity(deviceId: UserSettings.deviceToken, userId: nil)
        if let userId = self.user.current?.id, let intId = Int(userId) {
            userIdentity.userId = intId
        }

        let exposedPayload = ExposePayload(
            identity: userIdentity,
            experimentId: experimentId,
            variantId: variantId
        )
        logger.trace("Exposing to compass experiment, payload: \(exposedPayload)")

        let postRequest = buildPostRequest(with: "/v1/exposed", post: exposedPayload)

        let (_, response) = try await session.data(for: postRequest)

        guard let response = response as? HTTPURLResponse else {
            logger.error("exposing Compass failed, wrong response")
            throw CompassError.requestFailure
        }

        if response.statusCode != 200 {
            logger.error("exposed failed \(experimentId) - \(variantId) \(response)")
            throw CompassError.requestFailure
        }
    }

}
