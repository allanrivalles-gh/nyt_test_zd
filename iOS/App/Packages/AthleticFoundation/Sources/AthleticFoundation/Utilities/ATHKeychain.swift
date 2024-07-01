//
//  ATHKeychain.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 9/20/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticStorage
import Foundation
import KeychainSwift

public protocol ATHKeychainProtocol {
    var accessToken: String? { get }
    var accessTokenSource: String? { get }

    func updateAccessToken(_ value: String, isInit: Bool)
    func deleteAccessToken()
}

/// A helper for accessing the keychain
public final class ATHKeychain: ATHKeychainProtocol {
    /// A singleton for the keychain so only one does exist.
    public static let main = ATHKeychain()

    /// This file system key is intentionally abbreviated to add some, albeit very minor, obfuscation.
    private static let fileSystemAccessTokenKey = "fsa"
    private static let accessTokenKey = "user_access_token_new"

    private let keychain: KeychainSwift
    private let lock = NSLock()
    private let logger = ATHLogger(category: .application)

    /// A file system storage of the access token.
    @Storable(keyName: ATHKeychain.fileSystemAccessTokenKey, defaultValue: nil)
    private var fsAccessToken: String?

    /// A memory backed access token set on init from keychain.
    private var _memoryAccessToken: String?
    private var memoryAccessToken: String? {
        get {
            lock.lock()
            defer { lock.unlock() }

            return _memoryAccessToken
        }
        set {
            lock.lock()
            defer { lock.unlock() }

            _memoryAccessToken = newValue
        }
    }

    public var accessToken: String? {
        /// Look in memory first, then keychain, then filesystem
        if let memoryAccessToken = memoryAccessToken {
            return memoryAccessToken
        } else if let keychainAccessToken = keychainAccessToken {
            return keychainAccessToken
        } else if let fsAccessToken = fsAccessToken {
            return fsAccessToken
        } else {
            return nil
        }
    }

    public var accessTokenSource: String? {
        /// Look in memory first, then keychain, then filesystem
        if memoryAccessToken != nil {
            return "memory"
        } else if keychainAccessToken != nil {
            return "keychain"
        } else if fsAccessToken != nil {
            return "filesystem"
        } else {
            return nil
        }
    }

    /// KeychainSwift backed access token.
    private var keychainAccessToken: String? {
        let value = keychain.get(ATHKeychain.accessTokenKey)

        if keychain.lastResultCode != noErr && keychain.lastResultCode != errSecItemNotFound {
            logger.warning("Keychain access received error status of \(keychain.lastResultCode)")
        }

        return value
    }

    private init() {
        let keychain = KeychainSwift()
        /// Some reports say adding the accessGroup and synchronizable resolve getting nil.
        keychain.accessGroup = "X5U5MPSSEX.com.theathletic.news"
        keychain.synchronizable = true
        self.keychain = keychain
        /// if we have a KeychainSwift backed token, lets update them all.
        if let value = keychainAccessToken {
            updateAccessToken(value, isInit: true)
        }
    }

    public func updateAccessToken(
        _ value: String,
        isInit: Bool = false
    ) {
        /// Because the filesystem access is a fallback, we do not want to set it to the initial
        /// value from keychain swift, on the off chance it's corrupt,
        /// however, we do want to migrate the user to this value on startup of this new app
        /// so that we may be able to gracefully fall back to the filesystem
        /// hence the check below.
        if !isInit || fsAccessToken == nil {
            fsAccessToken = value
        }

        memoryAccessToken = value
        keychain.set(
            value,
            forKey: ATHKeychain.accessTokenKey,
            withAccess: .accessibleAfterFirstUnlock
        )
        if keychain.lastResultCode != noErr && keychain.lastResultCode != errSecItemNotFound {
            logger.warning("Keychain set received error status of \(keychain.lastResultCode)")
        }
    }

    public func deleteAccessToken() {
        logger.info("Deleting access token")
        memoryAccessToken = nil
        fsAccessToken = nil
        keychain.delete(ATHKeychain.accessTokenKey)

        if keychain.lastResultCode != noErr && keychain.lastResultCode != errSecItemNotFound {
            logger.warning("Keychain delete received error status of \(keychain.lastResultCode)")
        }
    }
}
