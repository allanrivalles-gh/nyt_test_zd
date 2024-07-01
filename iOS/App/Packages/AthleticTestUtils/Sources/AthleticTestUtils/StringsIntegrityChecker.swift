//
//  LocalizableStringsIntegrityTester.swift
//
//
//  Created by Mark Corbyn on 9/9/2022.
//

import Foundation
import XCTest

public struct LocalizableStringsIntegrityTester {

    let localizableBundle: Bundle
    let baseFilename: String
    let regionalFilenames: Set<String>
    let stringsKeys: Set<String>

    public func testIntegrity(
        file: StaticString = #file,
        line: UInt = #line
    ) {
        testAllStringsAreInBaseLocalizableFile(file: file, line: line)
        testNoUnlocalizedDuplicates(file: file, line: line)
        testLocalizedStrings_localeSpecificKeysMustAlsoHaveBaseKey(file: file, line: line)
        testNoUnusedLocalizedStrings()
    }

    public func testLocalizedStrings_localeSpecificKeysMustAlsoHaveBaseKey(
        file: StaticString = #file,
        line: UInt = #line
    ) {
        let baseStrings = localizableStrings(forLocalization: baseFilename)
        let baseKeys = Set(baseStrings.keys)

        for filename in regionalFilenames {
            let regionalStrings = localizableStrings(forLocalization: filename)
            let regionalKeys = Set(regionalStrings.keys)
            let regionalKeysNotInBase = regionalKeys.subtracting(baseKeys)

            XCTAssertTrue(
                regionalKeysNotInBase.isEmpty,
                "\(regionalKeysNotInBase) keys are in the \(filename) file but not in the base file",
                file: file,
                line: line
            )
        }
    }

    public func testAllStringsAreInBaseLocalizableFile(
        file: StaticString = #file,
        line: UInt = #line
    ) {
        let baseStrings = localizableStrings(forLocalization: baseFilename)
        stringsKeys.forEach {
            XCTAssertNotNil(
                baseStrings[$0],
                "Key '\($0)' not found in base localizable.strings file",
                file: file,
                line: line
            )
        }
    }

    public func testNoUnusedLocalizedStrings(
        file: StaticString = #file,
        line: UInt = #line
    ) {
        let filesToCheck = [baseFilename] + regionalFilenames
        for filename in filesToCheck {
            let baseStrings = localizableStrings(forLocalization: filename)
            baseStrings.keys.forEach { key in
                XCTAssert(
                    stringsKeys.contains(key),
                    "Key '\(key)' found in \(filename).strings file but not in list of Strings cases",
                    file: file,
                    line: line
                )
            }
        }
    }

    public func testNoUnlocalizedDuplicates(
        file: StaticString = #file,
        line: UInt = #line
    ) {
        let baseStrings = localizableStrings(forLocalization: baseFilename)
        for filename in regionalFilenames {
            let regionalStrings = localizableStrings(forLocalization: filename)
            for key in regionalStrings.keys {
                XCTAssertNotEqual(
                    baseStrings[key],
                    regionalStrings[key],
                    "\(filename) string for the key '\(key)' is the same as base - only include the key if it is localized",
                    file: file,
                    line: line
                )
            }
        }
    }
}

extension LocalizableStringsIntegrityTester {

    fileprivate func localizableStrings(forLocalization localization: String) -> [String: String] {
        guard
            let url = localizableBundle.url(
                forResource: "Localizable",
                withExtension: "strings",
                subdirectory: nil,
                localization: localization
            )
        else {
            XCTFail("Could not find localizable file for localisation '\(localization)'")
            fatalError()
        }

        guard let dict = NSDictionary(contentsOf: url) as? [String: String] else {
            XCTFail("Could not parse localizable file for localization '\(localization)'")
            fatalError()
        }

        return dict
    }

}
