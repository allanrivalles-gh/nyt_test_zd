import XCTest

@testable import AthleticNotificationCenter
@testable import AthleticTestUtils

final class StringsTests: XCTestCase {

    func testLocalizableIntegrity() {
        let tester = LocalizableStringsIntegrityTester(
            localizableBundle: Strings.allCases.first!.bundle,
            baseFilename: Strings.allCases.first!.baseFilename,
            regionalFilenames: [],
            stringsKeys: Set(Strings.allCases.map { $0.rawValue })
        )

        tester.testIntegrity()
    }
}
