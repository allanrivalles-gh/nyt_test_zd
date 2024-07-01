//
//  StringsTests.swift
//
//  Created by Mark Corbyn on 8/9/2022.
//

import XCTest

@testable import AthleticFoundation
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
