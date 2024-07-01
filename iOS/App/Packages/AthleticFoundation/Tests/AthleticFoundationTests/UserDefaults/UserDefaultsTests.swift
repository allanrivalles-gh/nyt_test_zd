//
//  UserDefaultsTests.swift
//  theathletic-iosTests
//
//  Created by Kyle Browning on 3/9/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import XCTest

@testable import AthleticFoundation

class UserDefaultsTests: XCTestCase {

    enum Key: String {
        case nullableString
        case nonNullableString
    }

    @UserDefault(key: Key.nullableString.rawValue, defaultValue: nil)
    static var nullableString: String?

    func testNullableString() throws {
        let valueToSet = "aNullableValue"
        UserDefaultsTests.nullableString = valueToSet
        XCTAssert(UserDefaultsTests.nullableString == valueToSet)
        if let value = UserDefaults.standard.object(forKey: Key.nullableString.rawValue) as? String
        {
            XCTAssert(value == valueToSet)
        } else {
            XCTFail("value didn't exist")
        }
        UserDefaultsTests.nullableString = nil
        XCTAssertNil(UserDefaultsTests.nullableString)
        XCTAssertNil(UserDefaults.standard.object(forKey: Key.nullableString.rawValue))
    }

    @UserDefault(key: Key.nonNullableString.rawValue, defaultValue: "aNonNullableValue")
    static var nonNullableString: String!

    func testNonNullableString() throws {
        let valueToSet = "aNonNullableValue"
        UserDefaultsTests.nonNullableString = valueToSet
        XCTAssert(UserDefaultsTests.nonNullableString == valueToSet)
        if let value = UserDefaults.standard.object(forKey: Key.nonNullableString.rawValue)
            as? String
        {
            XCTAssert(value == valueToSet)
        }
        UserDefaultsTests.nonNullableString = nil
        // Property wrapper responds with the default value
        XCTAssertNotNil(UserDefaultsTests.nonNullableString)
        // but under the hood it is gone
        XCTAssertNil(UserDefaults.standard.object(forKey: Key.nonNullableString.rawValue))
    }
}
