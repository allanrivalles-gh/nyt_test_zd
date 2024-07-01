import Apollo
import ApolloSQLite
import ApolloWebSocket
import AthleticFoundation
import Foundation

public class AthleticApolloNetwork {

    public struct Constants {
        static let platformHeaderKey = "X-Ath-Platform"
        static let versionHeaderKey = "X-Ath-Version"
        static let authHeaderKey = "x-ath-auth"
        static let timeZoneHeaderKey = "x-ath-timezone"
        static let oldVersionHeaderKey = "X-App-Version"
        static let acceptLanguageKey = "Accept-Language"
        static let userAgentKey = "User-Agent"
        static let deviceTokenKey = "X-ATH-Device-Token"
        static let userFollowingKey = "UserFollowing"
        static let followableItemsKey = "FollowableItems"

        // MARK: - Web Socket reconnect settings

        /// Whether to use Apollo's built in reconnect functionality (causes exponential attempts, not recommended)
        static let webSocketFrameworkShouldAutoReconnect = false

        /// Maximum number of times to attempt to reconnect.
        /// We don't want to drain device resource or backend resource if the device connection is
        /// down or the backend is having problems.
        /// In combination with the delay, this would be approximately 4 minutes worth of attempts, and it resets
        /// if the user backgrounds the app so it should cover typical usage.
        static let webSocketReconnectionMaxAttempts = 10

        /// The length of time to wait before timing out when connecting to the web socket
        static let webSocketConnectionTimeout: TimeInterval = 30
    }

    public enum WebSocketState {
        case initial
        case connected
        case disconnected
        case paused
        case resuming
    }

    public struct WebSocketKeyEvent: Hashable {
        public let date: Date
        public let fileName: String
        public let description: String
    }

    public enum EndpointBase: Equatable, CaseIterable, Codable, Hashable, CustomStringConvertible {
        case stage
        case production
        case betaiac
        case testParty
        case custom(String)

        public var value: String {
            switch self {
            case .stage:
                return "staging2.theathletic.com"
            case .production:
                return "graphql.theathletic.com"
            case .betaiac:
                return "beta-iac.theathletic.com"
            case .testParty:
                return "testparty.theathletic.com"
            case .custom(let value):
                return value
            }
        }

        public var description: String {
            switch self {
            case .stage:
                return "Staging"
            case .production:
                return "Production"
            case .betaiac:
                return "Beta IAC"
            case .testParty:
                return "Test Party"
            case .custom(let value):
                return value
            }
        }

        public static var allCases: [AthleticApolloNetwork.EndpointBase] = [
            .production,
            .betaiac,
            .stage,
            .testParty,
        ]

        public var isCustom: Bool {
            switch self {
            case .custom:
                return true
            default:
                return false
            }
        }
    }

    public enum Endpoint {
        case normal(EndpointBase)
        case websocket(EndpointBase)

        var path: String {
            switch self {
            case .normal(let base):
                return "https://\(base.value)/graphql"
            case .websocket(let base):
                return "wss://\(base.value)/gqlsubscriptions"
            }
        }
    }

    public var isWebSocketEnabled: Bool {
        let hasPendingConnectionAttempt = webSocketReconnectTimer != nil
        let hasAttemptsRemaining =
            webSocketReconnectionCount < Constants.webSocketReconnectionMaxAttempts
        return hasAttemptsRemaining || hasPendingConnectionAttempt
    }

    public var isWebSocketConnected: Bool {
        return webSocketTransport.isConnected()
    }

    public var isWebSocketReconnectScheduled: Bool {
        webSocketReconnectTimer?.isValid == true
    }

    public var client: ApolloClientProtocol!

    private var endpoint: Endpoint
    private var websocketEndpoint: Endpoint

    public var webSocketReconnectionCount: Int = 0
    private var webSocketReconnectTimer: Timer?
    private var webSocketConnectionTimeoutTimer: Timer?

    private var webSocketNextReconnectInterval: TimeInterval {
        // Stagger the reconnect intervals using Fibonacci sequence spacing, with element at n+1
        return TimeInterval(Int.fibonacci(webSocketReconnectionCount + 1))
    }

    public var webSocketState: WebSocketState = .initial {
        didSet {
            /// Log the state change for debugging purposes
            logger.trace(
                "WebSocket state: \(String(describing: webSocketState))",
                .websocket
            )

            // Only process if the state changed
            guard webSocketState != oldValue else {
                return
            }

            onMain { [threadName = Thread.currentName] in
                self.logWebSocketKeyEvent(state: self.webSocketState, threadName: threadName)

                /// Cancel the timeout timer if a connection attempt finished
                let connectingStates: [WebSocketState] = [.initial, .resuming]
                if connectingStates.contains(oldValue) {
                    self.logWebSocketKeyEvent("Invalidating socket timeout timer")
                    self.webSocketConnectionTimeoutTimer?.invalidate()
                    self.webSocketConnectionTimeoutTimer = nil
                }
            }
        }
    }

    public var webSocketKeyEvents: [WebSocketKeyEvent] = []
    private lazy var lifeCycle = AppLifeCycleObservation(self)
    private lazy var uploadTransportQueue: DispatchQueue = {
        DispatchQueue(label: "apollo-client", qos: .background)
    }()
    private lazy var logger = ATHLogger(category: .apollo)

    // MARK: - Initialization

    public init(apolloClient: ApolloClientProtocol, environment: EndpointBase) {
        client = apolloClient
        self.endpoint = .normal(environment)
        self.websocketEndpoint = .websocket(environment)

        onMain { [threadName = Thread.currentName] in
            self.logWebSocketKeyEvent("init Apollo client on \(threadName)")
        }

        lifeCycle.register(for: .appDidBackground, .appWillForeground, .userCredentialsUpdated)
    }

    public init(environment: EndpointBase) {
        self.endpoint = .normal(environment)
        self.websocketEndpoint = .websocket(environment)

        setupNewClient()
        onMain { [threadName = Thread.currentName] in
            self.logWebSocketKeyEvent("init Apollo client on \(threadName)")
        }

        lifeCycle.register(for: .appDidBackground, .appWillForeground, .userCredentialsUpdated)
    }

    // MARK: - Configuration

    private func setupNewClient() {
        let store: ApolloStore
        let documentsPath = NSSearchPathForDirectoriesInDomains(
            .documentDirectory,
            .userDomainMask,
            true
        ).first!
        let documentsURL = URL(fileURLWithPath: documentsPath)
        let sqliteFileURL = documentsURL.appendingPathComponent("athletic_graph_db.sqlite")
        let sqLiteConnection = try? SQLiteNormalizedCache(
            fileURL: sqliteFileURL,
            shouldVacuumOnClear: true
        )
        if let sqliteCache = sqLiteConnection {
            store = ApolloStore(cache: sqliteCache)
        } else {
            store = ApolloStore(cache: InMemoryNormalizedCache())
        }

        let networkTransport = SplitNetworkTransport(
            uploadingNetworkTransport: createUploadTransport(with: store),
            webSocketNetworkTransport: webSocketTransport
        )
        onMain { [threadName = Thread.currentName] in
            self.logWebSocketKeyEvent(state: .initial, threadName: threadName)
            self.startWebSocketConnectionTimeoutTimer()
        }

        client = ApolloClient(networkTransport: networkTransport, store: store)
        client.cacheKeyForObject = { [logger] dict in
            guard let typename = dict["__typename"] as? String else {
                logger.warning("Graph cache key not set. Are you requesting __typename?")
                return nil
            }
            /// Some feed consumables id's do not include their type.
            if ["feedconsumable", "userfollowoutput"].contains(typename.lowercased()),
                let id = dict["id"],
                let type = dict["type"]
            {
                return "\(typename)-\(id)-\(type)"
            }

            if let id = dict["id"] {
                return "\(typename)-\(id)"
            }

            if let consumableKey = dict["consumable"] {
                return "\(typename)-\(consumableKey)"
            }

            if let appText = dict["app_text"] {
                return "\(typename)-\(appText)"
            }

            // Certain caches should use the distinct typename key so they are always updated
            switch typename {
            case Constants.userFollowingKey,
                Constants.followableItemsKey:
                return typename
            default:
                logger.trace(
                    "Graph could not generate safe cache key for: \(typename). Graph cache key not set. Are you requesting an ID?"
                )
                return nil
            }
        }
    }

    private func createUploadTransport(
        with store: ApolloStore
    ) -> RequestChainNetworkTransport {

        let operationQueue = OperationQueue()
        operationQueue.underlyingQueue = uploadTransportQueue

        /// Disable URL level caching since this interferes with our graph cache policies
        let urlSessionConfiguration = URLSessionConfiguration.default
        urlSessionConfiguration.urlCache = nil

        let intercepterProvider = NetworkInterceptorProvider(
            store: store,
            client: URLSessionClient(
                sessionConfiguration: urlSessionConfiguration,
                callbackQueue: operationQueue
            )
        )

        /// Headers that dont need to be computed
        let additionalHeaders: [String: String] = [
            Constants.platformHeaderKey: "ios",
            Constants.acceptLanguageKey: Locale.current.formatted,
            Constants.versionHeaderKey: String.getAppVersionSemver,
            Constants.oldVersionHeaderKey: String.getAppVersionSemver,
            Constants.userAgentKey: String.userAgent,
            Constants.deviceTokenKey: "",  // TODO: FIx this
        ]

        let transport = RequestChainNetworkTransport(
            interceptorProvider: intercepterProvider,
            endpointURL: endpoint.path.url!,
            additionalHeaders: additionalHeaders,
            autoPersistQueries: true,
            useGETForQueries: true
        )
        transport.clientName = "ios"

        return transport
    }

    private lazy var webSocketTransport: WebSocketTransport = {
        var request = URLRequest(url: websocketEndpoint.path.url!)
        request.setValue(
            "permessage-deflate; client_max_window_bits",
            forHTTPHeaderField: "Sec-WebSocket-Extensions"
        )
        request.setValue("gzip, deflate, br", forHTTPHeaderField: "Accept-Encoding")
        request.setValue("en,zh-CN;q=0.9,zh;q=0.8", forHTTPHeaderField: "Accept-Language")
        request.cachePolicy = .reloadIgnoringLocalAndRemoteCacheData

        let payload = webSocketConnectionPayload
        logWebSocketKeyEvent("Initializing socket with payload \(String(describing: payload))")
        let webSocket = WebSocket(request: request, protocol: .graphql_ws)
        webSocket.onText = { text in
            ResponseLoggingInterceptor.receivedSocketText(text)
        }
        let transport = WebSocketTransport(
            websocket: webSocket,
            reconnect: Constants.webSocketFrameworkShouldAutoReconnect,
            connectingPayload: payload
        )
        transport.delegate = self

        return transport
    }()

    private lazy var webSocketConnectionPayload: [String: String]? = {
        Self.makeWebSocketConnectionPayload()
    }()

    private static func makeWebSocketConnectionPayload() -> [String: String]? {
        [
            Constants.authHeaderKey: ATHKeychain.main.accessToken,
            Constants.timeZoneHeaderKey: SystemTimeSettings().timeZone.identifier,
        ].compactMapValues { $0 }
    }
}

// MARK: - WebSocketTransportDelegate
extension AthleticApolloNetwork: WebSocketTransportDelegate {
    public func webSocketTransportDidConnect(_ webSocketTransport: WebSocketTransport) {
        onMain { [logger, weak self] in
            logger.trace("Apollo websocket connected", .websocket)
            self?.webSocketDidConnect()
        }
    }

    public func webSocketTransportDidReconnect(_ webSocketTransport: WebSocketTransport) {
        onMain { [logger, weak self] in
            logger.trace("Apollo websocket reconnected", .websocket)
            self?.webSocketDidConnect()
        }
    }

    public func webSocketTransport(
        _ webSocketTransport: WebSocketTransport,
        didDisconnectWithError error: Error?
    ) {
        onMain { [logger, weak self] in
            logger.info(
                """
                Web socket didDisconnectWithError:
                \(String(describing: error))
                \(String(describing: error.underlying))
                """,
                .websocket
            )
            self?.webSocketDidDisconnect()
        }
    }

    func webSocketTransport(_ webSocketTransport: WebSocketTransport, didReceivePingData: Data?) {
        onMain { [threadName = Thread.currentName] in
            self.logWebSocketKeyEvent("ping on \(threadName)")
        }
    }
    func webSocketTransport(_ webSocketTransport: WebSocketTransport, didReceivePongData: Data?) {
        onMain { [threadName = Thread.currentName] in
            self.logWebSocketKeyEvent("pong on \(threadName)")
        }
    }

    private func webSocketDidConnect() {
        webSocketState = .connected
        webSocketReconnectionCount = 0
        cancelWebSocketReconnectTimer()
    }

    private func webSocketDidDisconnect() {
        assert(
            Thread.isMainThread,
            "webSocketDidDisconnect: Should only be called from the main thread"
        )
        webSocketState = .disconnected

        if isWebSocketEnabled {
            scheduleWebSocketReconnect()

        } else if webSocketReconnectionCount >= Constants.webSocketReconnectionMaxAttempts {
            logger.info(
                "Web socket reconnect attempts threshold crossed. Pausing web socket.",
                .websocket
            )
            pauseWebSocketConnection()
        } else {
            logger.info(
                "Received residual web socket error after pausing. There shouldn't be many of these, if there are something is wrong.",
                .websocket
            )
        }
    }

    private func scheduleWebSocketReconnect() {
        assert(
            Thread.isMainThread,
            "scheduleWebSocketReconnect: Should only be called from the main thread"
        )

        guard self.webSocketReconnectTimer == nil else {
            logger.trace(
                "scheduleWebSocketReconnect: Already a reconnect scheduled, skipping",
                .websocket
            )
            return
        }

        webSocketReconnectionCount += 1

        let nextAttemptDelay = webSocketNextReconnectInterval
        logger.trace(
            "scheduleWebSocketReconnect: Scheduling a reconnect #\(webSocketReconnectionCount) in \(nextAttemptDelay) seconds",
            .websocket
        )
        logWebSocketKeyEvent("Schedule reconnect in \(nextAttemptDelay) seconds")

        webSocketReconnectTimer = Timer.scheduledTimer(
            withTimeInterval: nextAttemptDelay,
            repeats: false,
            block: { [logger, weak self] timer in
                logger.trace(
                    "scheduleWebSocketReconnect: Reconnect timer fired, attempting to resume web socket connection",
                    .websocket
                )
                self?.logWebSocketKeyEvent("Timer fired (\(nextAttemptDelay) seconds)")
                timer.invalidate()
                self?.resumeWebSocketConnection()
            }
        )
    }

    private func pauseWebSocketConnection() {
        assert(
            Thread.isMainThread,
            "pauseWebSocketConnection: Should only be called from the main thread"
        )

        webSocketState = .paused
        webSocketTransport.pauseWebSocketConnection()
    }

    private func resumeWebSocketConnection() {
        assert(
            Thread.isMainThread,
            "resumeWebSocketConnection: Should only be called from the main thread"
        )

        cancelWebSocketReconnectTimer()

        guard webSocketState != .resuming else {
            logger.trace(
                "resumeWebSocketConnection: Web Socket is already resuming",
                .websocket
            )
            return
        }

        /// NB: There's a chance the socket is already connected, but there's no way of knowing
        /// whether it is still paused (in which case it will likely disconnect soon),
        /// so try to resume it either way; calling `resumeWebSocketConnection` when it has already
        /// resumed shouldn't have any negative consequences.
        logger.info(
            "resumeWebSocketConnection: Attempting to resume web socket connection",
            .websocket
        )
        webSocketState = .resuming
        startWebSocketConnectionTimeoutTimer()
        webSocketTransport.resumeWebSocketConnection(
            autoReconnect: Constants.webSocketFrameworkShouldAutoReconnect
        )
    }

    private func cancelWebSocketReconnectTimer() {
        assert(
            Thread.isMainThread,
            "cancelWebSocketReconnectTimer: Should only be called from the main thread"
        )

        guard let timer = webSocketReconnectTimer else {
            return
        }

        if timer.isValid {
            let remainingTime = timer.fireDate.timeIntervalSince1970 - Date().timeIntervalSince1970
            logger.trace(
                "cancelWebSocketReconnectTimer: Invalidating timer due to fire in \(remainingTime) seconds",
                .websocket
            )
            timer.invalidate()
        } else {
            logger.trace(
                "cancelWebSocketReconnectTimer: Timer finished, going to remove",
                .websocket
            )
        }

        webSocketReconnectTimer = nil
    }

    private func startWebSocketConnectionTimeoutTimer() {
        assert(
            Thread.isMainThread,
            "startWebSocketConnectionTimeoutTimer: Should only be called from the main thread"
        )

        logWebSocketKeyEvent("startWebSocketConnectionTimeoutTimer")
        webSocketConnectionTimeoutTimer = Timer.scheduledTimer(
            withTimeInterval: Constants.webSocketConnectionTimeout,
            repeats: false,
            block: { [weak self] _ in
                self?.didTimeoutConnectingToWebSocket()
            }
        )
    }

    private func didTimeoutConnectingToWebSocket() {
        assert(
            Thread.isMainThread,
            "didTimeoutConnectingToWebSocket: Should only be called from the main thread"
        )

        logger.trace(
            "Web socket didTimeoutConnectingToWebSocket",
            .websocket
        )
        logWebSocketKeyEvent("didTimeoutConnectingToWebSocket, pausing")
        pauseWebSocketConnection()
        if isWebSocketEnabled {
            scheduleWebSocketReconnect()
        } else {
            logWebSocketKeyEvent("didTimeoutConnectingToWebSocket, socket disabled")
        }
    }

    private func logWebSocketKeyEvent(
        state: WebSocketState,
        threadName: String,
        fileName: String = #file
    ) {
        webSocketKeyEvents.append(
            .init(
                date: Date(),
                fileName: (fileName as NSString).lastPathComponent,
                description: "State: \(String(describing: state)), \(threadName)"
            )
        )
    }

    public func logWebSocketKeyEvent(_ string: String, fileName: String = #file) {
        webSocketKeyEvents.append(
            .init(
                date: Date(),
                fileName: (fileName as NSString).lastPathComponent,
                description: string
            )
        )
    }
}

// MARK: - AppLifeCycleProtocol
extension AthleticApolloNetwork: AppLifeCycleProtocol {
    public func appWillForeground() {
        assert(
            Thread.isMainThread,
            "appWillForeground: Is expected to be called on the main thread"
        )
        logger.trace("appWillForeground: resetting reconnection count", .websocket)
        logWebSocketKeyEvent("appWillForeground")
        webSocketReconnectionCount = 0

        if [.paused, .disconnected].contains(webSocketState) {
            logWebSocketKeyEvent("appWillForeground - attempt resume")
            logger.trace(
                "appWillForeground: resuming connection because status was \(webSocketState)",
                .websocket
            )
            resumeWebSocketConnection()
        }
    }

    public func appDidBackground() {
        assert(Thread.isMainThread, "appDidBackground: Is expected to be called on the main thread")
        logger.trace("appDidBackground: pausing connection", .websocket)
        logWebSocketKeyEvent("appDidBackground")
        pauseWebSocketConnection()
    }

    public func userCredentialsUpdated() {
        updateSocketConnectionPayloadIfNeeded()
    }

    private func updateSocketConnectionPayloadIfNeeded() {
        let existingPayload = webSocketConnectionPayload
        let newPayload = Self.makeWebSocketConnectionPayload()

        guard existingPayload != newPayload else {
            /// Payload remains the same, do nothing
            return
        }

        logWebSocketKeyEvent("Updating socket with payload \(String(describing: newPayload))")
        webSocketConnectionPayload = newPayload
        webSocketTransport.updateConnectingPayload(
            newPayload ?? [:],
            reconnectIfConnected: false
        )

        /// Manually implement the reconnect because Apollo doesn't appear to reliably reconnect
        pauseWebSocketConnection()
        scheduleWebSocketReconnect()
    }
}
