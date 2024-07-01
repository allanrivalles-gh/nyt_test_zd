//
//  StringsTests.swift
//
//  Created by Leonardo da Silva on 8/27/22.
//

import XCTest

@testable import AthleticBrackets
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
