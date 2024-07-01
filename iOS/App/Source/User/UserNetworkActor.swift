//
//  UserNetworkActor.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 1/19/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloNetworking
import AthleticApolloTypes
import AthleticFoundation
import Foundation

actor UserNetworkActor {
    private var activeTask: Task<GQL.UserCredentials, Error>?
    private let network: UserModelNetwork
    private let keychain: ATHKeychainProtocol
    private let logger = ATHLogger(category: .user)
    private var memoryCachedUser: GQL.UserCredentials?

    init(
        network: UserModelNetwork,
        keychain: ATHKeychainProtocol = ATHKeychain.main
    ) {
        self.network = network
        self.keychain = keychain
    }

    private func isUserSubscriptionOutdated(date: Date?) -> Bool {
        !Entitlement.isDateValidForSubscription(date)
    }

    @discardableResult
    func loadUser(forceIgnoreCache: Bool = false) async throws -> GQL.UserCredentials {

        if forceIgnoreCache {
            logger.debug("force ignore cache enabled - fetching", .user)
            return try await userTaskValue(forceIgnoreCache: forceIgnoreCache)
        } else if let cachedValue = memoryCachedUser {

            if isUserSubscriptionOutdated(date: cachedValue.user.fragments.customerDetail.endDate) {
                logger.debug("existing memory cache stale - fetching", .user)
                return try await userTaskValue(forceIgnoreCache: forceIgnoreCache)
            }

            logger.debug("returning in memory cached data", .user)
            return cachedValue

        } else if let apolloCache = try? await apolloUserCache() {

            if isUserSubscriptionOutdated(date: apolloCache.user.fragments.customerDetail.endDate) {
                logger.debug("existing apollo cache stale - fetching", .user)
                return try await userTaskValue(forceIgnoreCache: forceIgnoreCache)
            }

            logger.debug("returning in apollo cached data", .user)
            memoryCachedUser = apolloCache
            return apolloCache

        } else {
            logger.debug("checking for active task, or starting new one", .user)
            return try await userTaskValue(forceIgnoreCache: forceIgnoreCache)
        }
    }

    func clearMemoryCachedUser() {
        memoryCachedUser = nil
    }

    private func apolloUserCache() async throws -> GQL.UserCredentials {
        try await withCheckedThrowingContinuation { continuation in
            network.apolloStore.withinReadTransaction { transaction in
                /// Try to grab the credentials from GraphQL cache
                do {
                    let data = try transaction.readObject(
                        ofType: GQL.UserCredentials.self,
                        withKey: UserModel.Constants.userCredentialsCacheKey
                    )
                    continuation.resume(returning: data)
                } catch {
                    continuation.resume(throwing: error)
                }
            }
        }
    }

    private func userTaskValue(forceIgnoreCache: Bool) async throws -> GQL.UserCredentials {

        if let existingTask = activeTask {
            logger.debug("found active task, using it", .user)
            return try await existingTask.value
        } else {
            let task = Task<GQL.UserCredentials, Error> {
                defer {
                    activeTask = nil
                }

                guard Task.isCancelled == false else {
                    logger.debug("task cancelled", .user)
                    throw AthError.taskCancelled
                }

                guard let accessToken = keychain.accessToken else {
                    throw AthError.userIsNotLoggedIn
                }

                let userInfo = try await network.fetchUserInfo(
                    accessToken: accessToken,
                    tokenType: UserModel.Constants.userCredentialTokenType,
                    cacheKey: UserModel.Constants.userCredentialsCacheKey,
                    cachePolicy: forceIgnoreCache
                        ? .fetchIgnoringCacheData : .returnCacheDataElseFetch
                )

                memoryCachedUser = userInfo
                logger.debug("task completed", .user)
                return userInfo
            }
            activeTask = task
            logger.debug("executing new task fetching", .user)
            return try await task.value
        }
    }
}
