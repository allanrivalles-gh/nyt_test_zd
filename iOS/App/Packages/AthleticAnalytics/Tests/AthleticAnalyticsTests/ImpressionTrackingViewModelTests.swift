//
//  ImpressionTrackingViewModelTests.swift
//  theathletic-iosTests
//
//  Created by kevin fremgen on 11/14/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import XCTest

@testable import AthleticAnalytics

class ImpressionTrackingViewModelTests: XCTestCase {

    private var subject: ImpressionTrackingViewModel!
    private let eventManager = AnalyticImpressionManager.testing
    private let record = AnalyticsImpressionRecord(
        verb: .impress,
        view: .home,
        element: .fourContent,
        objectType: .articleId,
        objectIdentifier: "5056498",
        requiredValues: PreviewAnalyticDefaults()
    )

    override func setUp() async throws {
        try await super.setUp()

        subject = ImpressionTrackingViewModel(manager: eventManager) { [weak self] in
            self?.record
        }

        await eventManager.debugClearRecords()
    }

    override func tearDown() async throws {
        await eventManager.debugClearRecords()
    }

    func testHandle_isImpressingTrue_shouldUpdateStartTime() {
        let previousStartTime = subject.startTime
        subject.handle(isImpressing: true)
        XCTAssertNotEqual(previousStartTime, subject.startTime)
        XCTAssertTrue(subject.canImpress)
    }

    func testHandle_isImpressingFalse_shouldUpdateCanImpress() async throws {
        let previousCanImpress = subject.canImpress
        subject.handle(isImpressing: false)
        XCTAssertNotEqual(previousCanImpress, subject.canImpress)
    }

    func testSendImpression_under500Milliseconds_shouldNotSend() async throws {
        let startDate = Date()
        let endDate = Date()

        var updatedRecord = record
        updatedRecord.impressStartTime = startDate.millisecondsSince1970
        updatedRecord.impressEndTime = endDate.millisecondsSince1970

        await subject.sendImpression(impressStartTime: startDate, impressionEndTime: endDate)
        let records = await eventManager.records
        XCTAssertTrue(records.isEmpty)
    }

    func testSendImpression_over500Milliseconds_shouldSend() async throws {
        let startDate = Date()
        let endDate = Date(timeInterval: 1, since: startDate)

        var updatedRecord = record
        updatedRecord.impressStartTime = startDate.millisecondsSince1970
        updatedRecord.impressEndTime = endDate.millisecondsSince1970

        await subject.sendImpression(impressStartTime: startDate, impressionEndTime: endDate)
        let hasRecord = await eventManager.has(record: updatedRecord)
        XCTAssertTrue(hasRecord)
    }
}

extension AnalyticImpressionManager {
    static var testing: Self {
        .init(
            withConfiguration: .impressionTesting,
            encoder: AnalyticsManagers.eventEncoder,
            decoder: AnalyticsManagers.eventDecoder
        )
    }
}

extension AnalyticsConfiguration {
    static var impressionTesting: Self {
        .init(
            schemaID: 43,
            identifier: .testingImpressions,
            environment: .staging,
            flushRecordsTimeInterval: 30
        )
    }
}
