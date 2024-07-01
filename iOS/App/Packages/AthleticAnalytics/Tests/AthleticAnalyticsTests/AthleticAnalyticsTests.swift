import XCTest

@testable import AthleticAnalytics

final class AthleticAnalyticsTests: XCTestCase {
    func testExample() async throws {

        let config = AnalyticsConfiguration(
            schemaID: 26,
            identifier: .events,
            environment: .staging,
            flushRecordsTimeInterval: 3
        )
        let manager = AnalyticEventManager(withConfiguration: config)
        await manager.track(
            record: .init(
                verb: .add,
                view: .appIconSetting,
                requiredValues: PreviewAnalyticDefaults()
            )
        )
        try await Task.sleep(seconds: 5)
    }
}
