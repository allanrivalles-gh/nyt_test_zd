//
//  StringsTests.swift
//
//  Created by Mark Corbyn on 5/11/23.
//

import XCTest

@testable import AthleticScoresFoundation
@testable import AthleticTestUtils

final class StringsTests: XCTestCase {

    func testLocalizableIntegrity() {
        let tester = LocalizableStringsIntegrityTester(
            localizableBundle: Strings.allCases.first!.bundle,
            baseFilename: Strings.allCases.first!.baseFilename,
            regionalFilenames: ["en-GB"],
            stringsKeys: Set(Strings.allCases.map { $0.rawValue })
        )

        tester.testIntegrity()
    }
}
