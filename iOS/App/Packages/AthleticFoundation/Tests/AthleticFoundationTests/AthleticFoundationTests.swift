import KeychainSwift
import XCTest

@testable import AthleticFoundation

final class AthleticFoundationTests: XCTestCase {

    func testKeyChain() {
        let firstValue = "1234"
        let keychain = ATHKeychain.main
        keychain.updateAccessToken(firstValue)
        XCTAssertEqual(keychain.accessToken, firstValue)
        keychain.deleteAccessToken()
        XCTAssertNil(keychain.accessToken)
    }
}
