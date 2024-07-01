//
//  LogHandlers.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 3/27/20.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import Datadog
import UIKit
import os

public enum ATHLogCategory: String {
    case ads
    case agora
    case analytics
    case apollo
    case application
    case appModel
    case article
    case audio
    case boxScore
    case cloudKit
    case codeOfConduct
    case comments
    case compass
    case deeplink
    case entitlement
    case feed
    case feedNetwork
    case firebase
    case gamePlayerGrades
    case gameStats
    case gamePlayByPlay
    case headlines
    case hub
    case initialLoading
    case liveActivity
    case liveBlog
    case liveDiscussion
    case liveRooms
    case login
    case location
    case network
    case onboarding
    case paywall
    case podcast
    case profile
    case refer
    case sceneDelegate
    case scores
    case store
    case survey
    case ui
    case user
    case webView
    case tournaments
    case twitterWebView

    var prettyText: String {
        let uglyText = self.rawValue

        return uglyText.replacingOccurrences(
            of: "([A-Z])",
            with: " $1",
            options: .regularExpression,
            range: uglyText.range(of: uglyText)
        ).trimmingCharacters(in: .whitespacesAndNewlines).capitalized
    }
}

/// Add subcategories as needed for more context in logs
public enum ATHLogSubCategory: String {
    case application = "📱"
    case audio = "🔊"
    case startupData = "⚡️"
    case feed = "📰"
    case subscriptions = "💰"
    case locationServices = "📍"
    case network = "🌐"
    case notifications = "🔔"
    case observing = "👀"
    case user = "🙋"
    case websocket = "🔌"
}

public protocol LogListener {
    var logLevel: OSLogType { get set }
    func logMessage(_ logDetails: LogDetails, logger: ATHLogger)
}

public struct MetadataKey {
    static let accessTokenLogs = "accessTokenLogs"
}

public struct LogDetails {
    public let level: OSLogType
    public let message: String
    let extra: [String: Encodable]?
    let metadata: Metadata?
    let prettyMetadata: String?
    public var thread: String
    public let file: String
    public let function: String
    public let line: UInt
    let date: Date
    let logger: os.Logger
}

public class ATHLogger {
    public static var listeners: [LogListener] = []
    private let logger: os.Logger
    public let forceDebug: Bool

    private var threadNumberAndName: String {
        let fullDescription = Thread.current.description
        let splitOnBracket = fullDescription.components(separatedBy: "{")
        let splitOnSecondBracket = splitOnBracket[1].components(separatedBy: "}")
        var threadNumberAndName = String(splitOnSecondBracket[0])
        /// If the thread name is null, lets just show the number to cleanup logs.
        if threadNumberAndName.contains("null") {
            threadNumberAndName = threadNumberAndName.components(separatedBy: ",")[0]
        }
        return threadNumberAndName
    }

    public init(category: ATHLogCategory, forceDebug: Bool = false) {
        self.logger = Logger(subsystem: Bundle.main.bundleIdentifier!, category: category.rawValue)
        self.forceDebug = forceDebug
    }

    public func trace(
        _ message: String,
        _ subCategory: ATHLogSubCategory = .application,
        extra: [String: Encodable]? = nil,
        metadata: Metadata? = nil,
        file: String = #fileID,
        function: String = #function,
        line: UInt = #line
    ) {
        /// This uses debug because trace is our equivelent of Apples debug
        log(
            level: .debug,
            message: message,
            subCategory: subCategory,
            extra: extra,
            metadata: metadata,
            file: file,
            function: function,
            line: line
        )
    }

    public func debug(
        _ message: String,
        _ subCategory: ATHLogSubCategory = .application,
        extra: [String: Encodable]? = nil,
        metadata: Metadata? = nil,
        file: String = #fileID,
        function: String = #function,
        line: UInt = #line
    ) {
        /// This uses info because debug is our equivelent of Apples info
        info(
            message,
            subCategory,
            extra: extra,
            metadata: metadata,
            file: file,
            function: function,
            line: line
        )
    }

    public func info(
        _ message: String,
        _ subCategory: ATHLogSubCategory = .application,
        extra: [String: Encodable]? = nil,
        metadata: Metadata? = nil,
        file: String = #fileID,
        function: String = #function,
        line: UInt = #line
    ) {
        log(
            level: .info,
            message: message,
            subCategory: subCategory,
            extra: extra,
            metadata: metadata,
            file: file,
            function: function,
            line: line
        )
    }

    public func notice(
        _ message: String,
        _ subCategory: ATHLogSubCategory = .application,
        extra: [String: Encodable]? = nil,
        metadata: Metadata? = nil,
        file: String = #fileID,
        function: String = #function,
        line: UInt = #line
    ) {
        log(
            level: .default,
            message: message,
            subCategory: subCategory,
            extra: extra,
            metadata: metadata,
            file: file,
            function: function,
            line: line
        )
    }

    public func warning(
        _ message: String,
        _ subCategory: ATHLogSubCategory = .application,
        extra: [String: Encodable]? = nil,
        metadata: Metadata? = nil,
        file: String = #fileID,
        function: String = #function,
        line: UInt = #line
    ) {
        log(
            level: .error,
            message: message,
            subCategory: subCategory,
            extra: extra,
            metadata: metadata,
            file: file,
            function: function,
            line: line
        )
    }

    public func error(
        _ message: String,
        _ subCategory: ATHLogSubCategory = .application,
        extra: [String: Encodable]? = nil,
        metadata: Metadata? = nil,
        file: String = #fileID,
        function: String = #function,
        line: UInt = #line
    ) {
        log(
            level: .error,
            message: message,
            subCategory: subCategory,
            extra: extra,
            metadata: metadata,
            file: file,
            function: function,
            line: line
        )
    }

    public func critical(
        _ message: String,
        _ subCategory: ATHLogSubCategory = .application,
        extra: [String: Encodable]? = nil,
        metadata: Metadata? = nil,
        file: String = #fileID,
        function: String = #function,
        line: UInt = #line
    ) {
        log(
            level: .fault,
            message: message,
            subCategory: subCategory,
            extra: extra,
            metadata: metadata,
            file: file,
            function: function,
            line: line
        )
    }

    private func log(
        level: OSLogType,
        message: String,
        subCategory: ATHLogSubCategory,
        extra: [String: Encodable]?,
        metadata: Metadata?,
        file: String,
        function: String,
        line: UInt
    ) {
        let date = Date()
        let filePath = self.conciseSourcePath(file)

        let prettyMetadata =
            metadata?.isEmpty ?? true
            ? self.prettyMetadata
            : self.prettify(
                self.metadata.merging(metadata!, uniquingKeysWith: { _, new in new })
            )

        let logDetails = LogDetails(
            level: level,
            message: "\(subCategory.rawValue) \(message)",
            extra: extra,
            metadata: metadata,
            prettyMetadata: prettyMetadata,
            thread: threadNumberAndName,
            file: filePath,
            function: function,
            line: line,
            date: date,
            logger: logger
        )

        ATHLogger.listeners.forEach { listener in
            listener.logMessage(logDetails, logger: self)
        }
    }

    /// splits a path on the /'s
    ///
    private func conciseSourcePath(_ path: String) -> String {
        return (path as NSString).lastPathComponent
    }

    subscript(metadataKey metadataKey: String) -> Metadata.Value? {
        get {
            return metadata[metadataKey]
        }
        set {
            metadata[metadataKey] = newValue
        }
    }

    public var metadata = Metadata() {
        didSet {
            prettyMetadata = prettify(metadata)
        }
    }

    private var prettyMetadata: String?

    private func prettify(_ metadata: Metadata) -> String? {
        return !metadata.isEmpty ? metadata.map { "\($0)=\($1)" }.joined(separator: " ") : nil
    }
}

public class ATHLogListener: LogListener {
    public var logLevel: OSLogType

    public init(logLevel: OSLogType) {
        self.logLevel = logLevel
    }

    public func logMessage(_ logDetails: LogDetails, logger: ATHLogger) {
        if logLevel.rank <= logDetails.level.rank || logger.forceDebug {
            /// There is a bug in the simulator where using any of the other log singatures, they dont show up
            #if targetEnvironment(simulator)
                logDetails.logger.log("\(logDetails.message)")
            #else
                switch logDetails.level {
                case .debug:
                    logDetails.logger.debug("\(logDetails.message)")
                case .info:
                    logDetails.logger.info("\(logDetails.message)")
                case .error:
                    logDetails.logger.error("\(logDetails.message)")
                case .fault:
                    logDetails.logger.fault("\(logDetails.message)")
                default:
                    logDetails.logger.log("\(logDetails.message)")
                }
            #endif

        }
    }
}

public class DatadogListener: LogListener {
    public var ddLogger: DDLogger
    public var logLevel: OSLogType

    public init(logLevel: OSLogType, environment: String) {
        self.logLevel = logLevel

        let appID = "fc4a3f87-5e82-4b6c-ac42-b91712e96d64"
        let clientToken = "pubddd23394db799716941b9009018cd0ff"

        Datadog.initialize(
            appContext: .init(),
            trackingConsent: .granted,
            configuration: Datadog.Configuration
                .builderUsing(
                    rumApplicationID: appID,
                    clientToken: clientToken,
                    environment: environment
                )
                .set(endpoint: .us1)
                .set(serviceName: Bundle.main.bundleIdentifier!)
                .set(uploadFrequency: .frequent)
                .set(rumSessionsSamplingRate: 1.0)
                .trackRUMLongTasks()
                .trackUIKitRUMViews()
                .trackUIKitRUMActions()
                .build()
        )

        DDGlobal.rum = RUMMonitor.initialize()

        ddLogger = DDLogger.builder
            .sendNetworkInfo(true)
            .set(datadogReportingThreshold: .debug)
            .printLogsToConsole(false, usingFormat: .short)
            .build()
        ddLogger.addTag(withKey: "build", value: String.getBuildVersion)
        ddLogger.addAttribute(forKey: "device-model", value: UIDevice.current.model)
        ddLogger.addAttribute(forKey: "device-name", value: UIDevice.current.name)
    }

    public func logMessage(_ logDetails: LogDetails, logger: ATHLogger) {
        if logLevel.rank <= logDetails.level.rank || logger.forceDebug {
            let message = "\(logDetails.message)"
            let attributes: [String: Encodable] = [
                "file": logDetails.file,
                "line": logDetails.line,
                "func": logDetails.function,
                "thread": logDetails.thread,
            ].merged(with: logDetails.extra ?? [:])

            switch logDetails.level {
            case .debug:
                ddLogger.debug(message, attributes: attributes)
            case .info:
                ddLogger.info(message, attributes: attributes)
            case .error:
                ddLogger.error(message, attributes: attributes)
            case .fault:
                ddLogger.critical(message, attributes: attributes)
            default:
                ddLogger.notice(message, attributes: attributes)
            }
        }
    }
}

/// From swift-log (https://github.com/apple/swift-log)
public typealias Metadata = [String: MetadataValue]

public enum MetadataValue {
    /// A metadata value which is a `String`.
    ///
    /// Because `MetadataValue` implements `ExpressibleByStringInterpolation`, and `ExpressibleByStringLiteral`,
    /// you don't need to type `.string(someType.description)` you can use the string interpolation `"\(someType)"`.
    case string(String)

    /// A metadata value which is some `CustomStringConvertible`.
    case stringConvertible(CustomStringConvertible)

    /// A metadata value which is a dictionary from `String` to `Logger.MetadataValue`.
    ///
    /// Because `MetadataValue` implements `ExpressibleByDictionaryLiteral`, you don't need to type
    /// `.dictionary(["foo": .string("bar \(buz)")])`, you can just use the more natural `["foo": "bar \(buz)"]`.
    case dictionary(Metadata)

    /// A metadata value which is an array of `Logger.MetadataValue`s.
    ///
    /// Because `MetadataValue` implements `ExpressibleByArrayLiteral`, you don't need to type
    /// `.array([.string("foo"), .string("bar \(buz)")])`, you can just use the more natural `["foo", "bar \(buz)"]`.
    case array([Metadata.Value])
}

extension OSLogType {
    public var rank: Int {
        switch self {
        case .debug:
            return 0
        case .info:
            return 1
        case .error:
            return 3
        case .fault:
            return 4
        default:
            return 2
        }
    }
}
