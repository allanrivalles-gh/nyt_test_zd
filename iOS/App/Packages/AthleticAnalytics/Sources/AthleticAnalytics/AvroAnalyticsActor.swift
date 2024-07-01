//
//  AvroAnalyticsActor.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 6/02/20.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticStorage
import UIKit

public typealias AnalyticImpressionManager = AvroAnalyticsActor<AnalyticsImpressionRecord>
public typealias AnalyticEventManager = AvroAnalyticsActor<AnalyticsEventRecord>

public protocol TimestampIndependentEquatable {
    func isSameDisregardingTimestamp(record: Self) -> Bool
}

public actor AvroAnalyticsActor<Record: Codable & Hashable & TimestampIndependentEquatable> {

    enum AvroAnalyticNetworkError: Error {
        case failedDowncastError
        case serverSideError(Int)
    }

    private struct RecordsStore: StorageObject {
        let storageIdentifier: String
        let records: Set<Record>

        /// Store in a folder that wont be cleared on cache clear
        static var storageDirectory: Storage.Directory {
            .documents
        }
    }

    let uuid = UUID().uuidString.prefix(5)

    private var configuration: AnalyticsConfiguration

    public private(set) var records = Set<Record>()
    private var inFlightRecords = Set<Record>()
    private var currentTask: Task<Void, Error>?

    private let encoder: JSONEncoder
    private let decoder: JSONDecoder

    private let urlSession: URLSession

    private lazy var logger = ATHLogger(category: .analytics)
    private lazy var lifeCycle = AppLifeCycleObservation(self)

    private var loggingKey: String {
        [uuid.description, configuration.topic].joined(separator: "-")
    }

    private var isTesting: Bool {
        [.testingEvents, .testingImpressions].contains(configuration.identifier)
    }

    public init(
        withConfiguration configuration: AnalyticsConfiguration,
        encoder: JSONEncoder = JSONEncoder(),
        decoder: JSONDecoder = JSONDecoder()
    ) {
        self.configuration = configuration
        self.encoder = encoder
        self.decoder = decoder
        let config = URLSessionConfiguration.default
        config.waitsForConnectivity = false
        self.urlSession = URLSession(configuration: config)

        Task {
            await lifeCycle.register(for: .appDidBackground)
        }
    }

    // MARK: - Listener Ops

    public func stopListener() async {
        logger.trace(
            "stopping analytics actor for: \(loggingKey)"
        )

        if let task = currentTask {
            logger.trace(
                "stopListener: \(loggingKey) canceling pending task"
            )
            task.cancel()
            currentTask = nil
        }
    }

    public func startListener() async {
        guard !isTesting else {
            assertionFailure(
                "The .testingEvents and .testingImpressions identifiers are for testing and records inspection only."
            )
            return
        }

        guard currentTask == nil else {
            logger.trace(
                "startListener: \(loggingKey) called but the listener is already running, ignoring request"
            )
            return
        }

        logger.trace(
            "startListener: \(loggingKey) processing current records then scheduling the next task"
        )

        await processRecords()
    }

    // MARK: - Record lifecycle

    public func track(record: Record) {
        logger.trace("track for: \(loggingKey): \(record)")
        records.insert(record)
    }

    private func processRecords() async {
        defer {
            logger.trace(
                "processRecords: \(loggingKey) scheduling the next task"
            )
            makeNewSleepingTask()
        }

        logger.trace(
            "processRecords for: \(loggingKey) flushing \(configuration.flushRecordsTimeInterval)"
        )

        do {
            /// Queue up any unsent, persisted records and clear storage
            if let existingRecords = RecordsStore.retrieveFromStorage(with: configuration.topic)?
                .records
            {
                records = records.union(existingRecords)
                RecordsStore.remove(with: configuration.topic)
            }

            guard !records.isEmpty else {
                logger.trace(
                    "processRecords for: \(loggingKey) record was empty, nothing to do"
                )
                return
            }

            logger.trace(
                "processRecords for: \(loggingKey) attempting to send \(records.count) record(s)"
            )

            try await sendRecords()

        } catch let error {
            /// If something goes wrong, persist in-flight records. They'll be retrieved from
            /// storage on subsequent send attempts, until send is successful.
            appendRecordsToStorage(inFlightRecords)
            clearInFlightRecords()

            logger.warning(
                "sendRecords for: \(loggingKey), API call failed \(error.localizedDescription)"
            )
        }
    }

    private func makeNewSleepingTask() {
        if let currentTask = currentTask {
            logger.trace(
                "makeNewSleepingTask for: \(loggingKey) canceling the current task first"
            )
            currentTask.cancel()
        }

        logger.trace(
            "makeNewSleepingTask for: \(loggingKey) creating the next sleeping task"
        )
        currentTask = Task {
            let uuidInner = UUID().uuidString.prefix(5)
            let countdown = configuration.flushRecordsTimeInterval

            logger.trace(
                "sleeping task for: \(loggingKey), task ID \(uuidInner) is about to start sleeping for \(countdown) seconds"
            )
            try await Task.sleep(seconds: countdown)

            logger.trace(
                "sleeping task for: \(loggingKey), task ID \(uuidInner) finished sleeping, checking whether we were canceled in the meantime"
            )

            try Task.checkCancellation()

            logger.trace(
                "sleeping task for: \(loggingKey), task ID \(uuidInner) processing records"
            )

            await processRecords()

            logger.trace(
                "sleeping task for: \(loggingKey), task ID \(uuidInner) finished processing records"
            )
        }
    }

    private func sendRecords() async throws {
        inFlightRecords = records
        clearRecords()

        logger.trace("sendRecords for: \(loggingKey), preparing encoded data")
        let recordCount = inFlightRecords.count
        let schema = AvroSchema(records: Array(inFlightRecords), configuration: configuration)
        let body = try encoder.encode(schema)

        logger.trace("sendRecords for: \(loggingKey), constructing request, \(configuration.url)")
        var urlRequest = URLRequest(url: configuration.url)
        urlRequest.httpMethod = "POST"
        urlRequest.allHTTPHeaderFields = configuration.headers
        urlRequest.httpBody = body

        logger.trace("sendRecords for: \(loggingKey), making API call")
        let (_, response) = try await urlSession.data(for: urlRequest)

        guard let httpResponse = response as? HTTPURLResponse else {
            throw AvroAnalyticNetworkError.failedDowncastError
        }

        guard (200...299).contains(httpResponse.statusCode) else {
            throw AvroAnalyticNetworkError.serverSideError(httpResponse.statusCode)
        }

        logger.trace(
            "sendRecords for: \(loggingKey) sent \(recordCount) records successfully"
        )

        clearInFlightRecords()

        logger.trace(
            "sendRecords for: \(loggingKey) cleared out sent records"
        )
    }

    // MARK: - Helpers
    public func has(record: Record) -> Bool {
        /// Preconfigured events will have different time stamps than newly created records for tracking
        records.contains { $0.isSameDisregardingTimestamp(record: record) }
    }

    public func has(records: [Record]) -> Bool {
        for record in records {
            if !has(record: record) { return false }
        }

        return true
    }

    public func debugClearRecords() {
        guard isTesting else { return }

        clearRecords()
    }

    private func clearRecords() {
        records.removeAll()
    }

    private func clearInFlightRecords() {
        inFlightRecords.removeAll()
    }

    private func appendRecordsToStorage(_ records: Set<Record>) {
        let existingRecords =
            RecordsStore.retrieveFromStorage(with: configuration.topic)?.records ?? []

        RecordsStore(
            storageIdentifier: configuration.topic,
            records: existingRecords.union(records)
        ).createOrUpdate()
    }
}

// MARK: - AppLifeCycleProtocol
extension AvroAnalyticsActor: AppLifeCycleProtocol {
    nonisolated public func appDidBackground() {
        Task {
            /// Persist any unsent records so they aren't lost if the app is killed
            await appendRecordsToStorage(records)
            await clearRecords()
        }
    }
}
