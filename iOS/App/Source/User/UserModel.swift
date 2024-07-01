//
//  UserModel.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 9/15/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import AthleticAnalytics
import AthleticApolloNetworking
import AthleticApolloTypes
import AthleticFoundation
import AthleticRestNetwork
import AuthenticationServices
import Combine
import Foundation
import IterableSDK
import KeychainSwift

typealias UserModelNetwork =
    NetworkModelProtocol
    & UserNetworking
    & UserPreferencesNetworking
    & OnboardingNetworking
    & PodcastsNetworking
    & LiveChatNetworking
    & LiveRoomsNetworking
    & ArticlesNetworking

final class UserModel: ObservableObject, UserModeling {

    struct Constants {
        static let userCredentialsCacheKey = "UserCredentials"
        static let userCredentialTokenType = "bearer"
        static let fbToken: String = "fb_token"
        static let fbFragment: String = "#_=_"
        static let googleFragment: String = "#"
    }

    /// Emits user credential value changes on the main thread.
    var userCredentialsPublisher: AnyPublisher<GQL.UserCredentials?, Never> {
        $userCredentialsUpdated
            .map { [weak self] _ in self?.userCredentials }
            .receive(on: RunLoop.main)
            .eraseToAnyPublisher()
    }

    @Published private(set) var userCredentialsState: LoadingState?

    private lazy var networkLogger = ATHLogger(category: .network)

    let network: UserModelNetwork
    let keychain: ATHKeychainProtocol
    private let logger = ATHLogger(category: .user)
    var cancellables = Cancellables()

    var isLoggedIn: Bool {
        userCredentials != nil
    }

    var current: GQL.CustomerDetail? {
        userCredentials?.user.fragments.customerDetail
    }

    var isAnonymous: Bool {
        current?.isAnonymous == 1
    }

    var isStaff: Bool {
        current?.isStaff ?? false
    }

    /// A thread-safe interface for the Published property, preventing concurrent reads during a mutation, or concurrent mutations.
    ///
    /// After setting, the `@Published userCredentialsUpdated` property is updated to trigger a notification to the
    /// observers of the ObservableObject. This is for thread safety to prevent a publisher update in the middle of the barrier lock setter.
    var userCredentials: GQL.UserCredentials? {
        get {
            userCredentialsQueue.sync { _userCredentials }
        }
        set {
            userCredentialsQueue.sync(flags: .barrier) {
                _userCredentials = newValue
            }

            /// We need to trigger the published property update *after* the setter has finished, otherwise the
            /// `objectWillChange` event will file while the setter is run inside the barrier. In response to the notification the
            /// receiving code can try to access the getter, but the queue barrier is still in place, resuling in a deadlock.
            DispatchQueue.main.async {
                self.userCredentialsUpdated = Date()
            }
        }
    }

    /// Queue for reading and writing the user credentials property in a thread-safe manner
    private let userCredentialsQueue = DispatchQueue(
        label: "UserCredentialsQueue",
        attributes: .concurrent
    )

    /// Private backing var for the computed `userCredentials` var.
    /// Updated and read from the `userCredentialsQueue`.
    private var _userCredentials: GQL.UserCredentials?

    private var userNetworkActor: UserNetworkActor

    /// Private property to trigger a notification to observers of the ObservableObject whenever the credentials changes
    @Published private var userCredentialsUpdated: Date?

    // MARK: - Initialization

    init(
        network: UserModelNetwork,
        keychain: ATHKeychainProtocol = ATHKeychain.main
    ) {
        self.keychain = keychain
        self.network = network
        self.userNetworkActor = UserNetworkActor(network: network, keychain: keychain)

        subscribeToUserCredentialUpdates()
        subscribeToReachabilityUpdates()
    }

    func updateUserInfo(
        firstName: String?,
        lastName: String?,
        emailAddress: String?
    ) {
        assert(Thread.isMainThread, "Call on `main` to ensure thread safety on `cancellables`")
        guard let currentUser = current else {
            return
        }

        network.updateUserInfo(
            userId: currentUser.id,
            firstName: firstName ?? currentUser.firstName,
            lastName: lastName ?? currentUser.lastName,
            emailAddress: emailAddress ?? currentUser.email
        )
        .receive(on: DispatchQueue.main)
        .sink { result in
            switch result {
            case .success:
                Task {
                    try await self.flushRemoteUserCache()
                    try await self.loadUser(forceIgnoreCache: true)
                }
            case .failure:
                break
            }
        }
        .store(in: &cancellables)
    }

    func fetchUserDynamicDataIfNeeded(completion: CompletionResult<Void>? = nil) {
        assert(Thread.isMainThread, "Call on `main` to ensure thread safety on `cancellables`")
        guard let userId = current?.id else {
            completion?(.failure(AthError.userIdMissing))
            return
        }

        guard abs(UserDynamicData.lastUpdated.timeIntervalSinceNow) >= 15.minutes else {
            Task { [weak self] in
                await self?.processArticleReadStateRecords()
            }

            completion?(.success(()))
            return
        }

        let payload = SimpleUserPayload(userId: userId.intValue)
        network.fetchUserDynamicData(payload: payload)
            .sink { result in
                switch result {
                case .success(let data):
                    data.articlesRated.forEach {
                        UserDynamicData.article.rate(for: $0, value: true)
                    }
                    data.articlesRead.forEach {
                        UserDynamicData.article.read(for: $0, value: true)
                    }
                    data.articlesSaved.forEach {
                        UserDynamicData.article.save(for: $0, value: true)
                    }
                    data.commentsLiked.forEach {
                        UserDynamicData.comment.like(for: $0, value: true)
                    }
                    data.commentsFlagged.forEach {
                        UserDynamicData.comment.flag(for: $0, value: true)
                    }

                    Task { [weak self] in
                        await self?.processArticleReadStateRecords()
                    }

                    completion?(.success(()))

                case .failure(let error):
                    completion?(.failure(error))
                }
            }
            .store(in: &cancellables)
    }

    private func subscribeToUserCredentialUpdates() {
        $userCredentialsUpdated
            .dropFirst()
            .map { _ in self.userCredentials }
            .receive(on: RunLoop.main)
            .sink { credentials in
                if let credentials = credentials {
                    LegacyAnalyticsManager.shared.identifyUser(
                        credentials.user.fragments.customerDetail
                    )
                    let userIdentifier = credentials.user.fragments.customerDetail.id.intValue
                    UserDefaults.lastKnownUserIdentifier = userIdentifier
                } else {
                    UserDefaults.lastKnownUserIdentifier = 0
                }

                postNotification(Notifications.UserCredentialsChanged)
            }
            .store(in: &cancellables)
    }

    private func subscribeToReachabilityUpdates() {
        NotificationCenter.default.publisher(for: Notifications.ReachabilityUpdated)
            .dropFirst()
            .throttle(
                for: .seconds(2),
                scheduler: RunLoop.main,
                latest: true
            )
            .sink { [weak self] _ in
                guard let self, self.network.isReachable else {
                    return
                }

                Task {
                    await self.processArticleReadStateRecords()
                }

            }
            .store(in: &cancellables)
    }

    private func processArticleReadStateRecords() async {
        await ArticleReadStateRecord.allStorageItems.concurrentForEach { [network] record in
            await network.updateArticleReadState(
                id: record.articleId,
                isRead: record.isRead,
                percentRead: record.percentRead
            )
        }
    }

    // Not a big fan of this, but took it from UserService. Can we update the cache instead?
    func updateCurrentFollowing(with followingObject: GQL.CustomerDetail.Following) {
        guard var existingCredentials = userCredentials else {
            return
        }

        existingCredentials.user.fragments.customerDetail.following = followingObject
        userCredentials = existingCredentials
    }

    func fetchUser(id: Int, completion: @escaping CompletionResult<GQL.UserDetail>) {
        assert(Thread.isMainThread, "Call on `main` to ensure thread safety on `cancellables`")
        let hashAlgorithm = Hashids(
            salt: "\(Global.hashUserSalt)\(Global.hashSecret)\(Global.hashUserSalt)",
            minHashLength: Global.hashMininumLength
        )

        guard let hashId = hashAlgorithm.encode(id) else {
            completion(.failure(AthError.missingData))
            return
        }

        network.fetchUser(hash: hashId).sink { result in
            switch result {
            case .success(let user):
                completion(.success(user))
            case .failure(let error):
                completion(.failure(error))
            }
        }
        .store(in: &cancellables)
    }

    func flushRemoteUserCache() async throws {
        guard
            let userId = userCredentials?.user.fragments.customerDetail.id.intValue,
            userId > 0
        else {
            return
        }

        try await network.flushCustomerCache(id: userId)
    }

    // MARK: - Authentication

    func login(type: LoginType, completion: @escaping CompletionResult<GQL.UserCredentials>) {
        assert(Thread.isMainThread, "Call on `main` to ensure thread safety on `cancellables`")
        network.login(type: type).receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                switch result {
                case .success(let credentials):
                    self?.finishLogin(credentials: credentials)
                    completion(.success(credentials))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
            .store(in: &cancellables)
    }

    func logout() {
        networkLogger.warning("logging out", .user)
        Analytics.track(.logout)
        IterableAPI.disableDeviceForAllUsers()
        BackgroundFetchManager.shared.cancellAllLoading()
        MinimizedAudioPlayer.shared.hide(animated: false)

        UserDefaults.standard.removeObject(forKey: Global.UserDefaults.presentedFBLink)
        UserDefaults.standard.removeObject(
            forKey: Global.UserDefaults.kochavaDefferedDeeplinkUsed
        )
        UserDefaults.standard.set(false, forKey: Global.UserDefaults.isKochavaIndentityLinkSent)
        ArticleReadingState.remove()
        ArticleReadStateRecord.removeAll()
        UserDefaults.followableItemsFetch = .distantPast
        AppEnvironment.shared.resetForLogout()
        Task {
            await userNetworkActor.clearMemoryCachedUser()
        }
        PublisherProvidedID.reset()
        onMain { [unowned self] in
            self.userCredentials = nil
            self.network.logout()
            self.keychain.deleteAccessToken()
        }
    }

    func finishLogin(credentials: GQL.UserCredentials) {
        keychain.updateAccessToken(credentials.accessToken, isInit: false)
        LoggingAppDelegate.addDatadogAttribute(
            forKey: Global.accessTokenSourceKey,
            value: keychain.accessTokenSource
        )

        userCredentials = credentials
        userCredentialsState = .loaded

        postNotification(Notifications.UserProfileUpdated)

        if let currentUser = current {
            LegacyAnalyticsManager.shared.identifyUser(currentUser)
        }

        /// fetch user data
        Task { @MainActor in
            try await loadUser(forceIgnoreCache: true)
            fetchUserDynamicDataIfNeeded(completion: nil)
            await AppEnvironment.shared.listen.loadInitialData()
        }

        if current?.attributionSurveyEligible == true {
            UserDefaults.showAttributionSurvey = true
        }

        // check notification status for experiment exposure
        RemoteNotificationHandler.shared.checkNotificationAuthorization()

        /// I'm not sure if this is the perfect place for this, but we need to fetch the followable
        /// items each time the user logs in via any method
        AppEnvironment.shared.following.updateFollowableItemsIfNeeded(
            cachePolicy: .fetchIgnoringCacheData
        )

        PublisherProvidedID.reset()
    }

    func appleSignUp(user: AppleUser) async throws -> GQL.UserCredentials {
        let credentials = try await appleLogin(user: user)
        let userId = credentials.user.fragments.customerDetail.id.intValue

        /// Flush the customer cache when we sign up with a social provider. This works around a backend issue where a social
        /// sign up incorrectly populates the cache with anonymous details, resulting in the `customer` query returning anonymous
        /// details.
        do {
            try await network.flushCustomerCache(id: userId)
        } catch {
            networkLogger.warning(
                "Failed to flush the user cache for id \(userId) with error \(error)"
            )
        }

        return credentials
    }

    func appleLogin(user: AppleUser) async throws -> GQL.UserCredentials {
        try await network.appleLogin(user: user)
    }

    @MainActor
    func socialSignUp(
        type: LoginFlowType,
        contextProvider: ASWebAuthenticationPresentationContextProviding
    ) async throws -> GQL.UserCredentials {
        let credentials = try await socialLogin(type: type, contextProvider: contextProvider)
        let userId = credentials.user.fragments.customerDetail.id.intValue

        keychain.updateAccessToken(credentials.accessToken, isInit: false)
        userCredentials = credentials

        /// Flush the customer cache when we sign up with a social provider. This works around a backend issue where a social
        /// sign up incorrectly populates the cache with anonymous details, resulting in the `customer` query returning anonymous
        /// details.
        do {
            try await network.flushCustomerCache(id: userId)
        } catch {
            networkLogger.warning(
                "Failed to flush the user cache for id \(userId) with error \(error)"
            )
        }

        return credentials
    }

    func socialLogin(
        type: LoginFlowType,
        contextProvider: ASWebAuthenticationPresentationContextProviding
    ) async throws -> GQL.UserCredentials {
        enum LoginType {
            case facebook, google, newYorkTimes
        }

        let loginType: LoginType

        switch type {
        case .fbLogin:
            loginType = .facebook
        case .googleLogin:
            loginType = .google
        case .newYorkTimesLogin:
            loginType = .newYorkTimes
        default:
            networkLogger.error("Social login called with unexpected type \(type)")
            throw AthError.generalNetworkRequestError
        }

        let url: URL
        let restHost: String

        switch Preferences.standard.adminRestEnvironment {
        case .custom(let host):
            restHost = "https://\(host)"
        case .production:
            restHost = "https://theathletic.com"
        case .betaiac:
            restHost = "https://beta-iac.theathletic.com"

        case .stage:
            restHost = "https://staging2.theathletic.com"
        case .testParty:
            restHost = "https://testparty.theathletic.com"
        }

        switch loginType {
        case .facebook:
            url = URL(string: "\(restHost)/fb-client-login")!
        case .google:
            url = URL(string: "\(restHost)/google-client-login")!
        case .newYorkTimes:
            url = URL(string: "\(restHost)/nyt-client-login")!
        }

        let callbackUrl: URL = try await withCheckedThrowingContinuation { continuation in
            let authSession = ASWebAuthenticationSession(
                url: url,
                callbackURLScheme: "theathletic"
            ) { callbackUrl, error in
                guard error == nil, let callbackUrl = callbackUrl else {
                    continuation.resume(throwing: error ?? AthError.webAuthenticationSessionError)
                    return
                }
                continuation.resume(returning: callbackUrl)
            }
            authSession.prefersEphemeralWebBrowserSession = true
            authSession.presentationContextProvider = contextProvider

            onMain {
                authSession.start()
            }
        }

        let payload: UserSocialAuthPayload
        let gqlGrantType: GQL.LoginGrantType

        switch loginType {
        case .facebook:
            guard let token = self.cleanFacebookCallback(callbackUrl) else {
                throw AthError.webAuthenticationSessionError
            }

            payload = UserSocialAuthPayload(
                withDeviceId: UserSettings.deviceToken,
                tokenCode: token,
                grantType: .facebook
            )
            gqlGrantType = .facebook

        case .google:
            guard let googlePayload = self.cleanGoogleCallback(callbackUrl) else {
                throw AthError.missingData
            }
            payload = googlePayload
            gqlGrantType = .google

        case .newYorkTimes:
            guard let nytPayload = self.cleanNewYorkTimesCallback(callbackUrl) else {
                throw AthError.webAuthenticationSessionError
            }

            payload = nytPayload
            gqlGrantType = .nyt
        }

        let gqlPayload = GQL.SocialLoginInput(
            deviceId: UserSettings.deviceToken,
            tokenCode: payload.tokenCode,
            grantType: gqlGrantType,
            firstName: payload.firstName,
            lastName: payload.lastName,
            email: payload.email,
            sub: payload.sub,
            accessViaNyt: payload.accessViaNyt
        )

        return try await network.socialLogin(payload: gqlPayload)
    }

    // MARK: - Registration

    func createAccount(
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ) async throws -> GQL.UserCredentials {
        let credentials = try await network.createAccount(
            email: email,
            firstName: firstName,
            lastName: lastName,
            password: password
        )

        keychain.updateAccessToken(credentials.accessToken, isInit: false)
        userCredentials = credentials

        return credentials
    }

    func updateUserInterests(teamIds: [String], leagueIds: [String]) async throws {
        try await network.updateUserInterests(teamIds: teamIds, leagueIds: leagueIds)
    }

    // MARK: - Referrals

    func generateReferralUrl() async throws -> URL {
        try await withCheckedThrowingContinuation { continuation in
            guard let userId = current?.id else {
                continuation.resume(throwing: AthError.userIsNotLoggedIn)
                return
            }

            network.generateReferralUrl(userId: userId).sink { result in
                continuation.resume(with: result)
            }
            .store(in: &cancellables)
        }
    }

    // MARK: - Code of Conduct & GDPR

    func acceptCodeOfConduct(year: Int?) async throws {
        try await network.acceptCodeOfConduct(year: year)
    }

    func updateUserPolicies(refreshProfile: Bool = true) {
        assert(Thread.isMainThread, "Call on `main` to ensure thread safety on `cancellables`")
        let policy = UserPolicy(privacyPolicy: true, termsAndConditions: true)

        network.updateUserPolicies(policy).sink { [weak self] _ in
            guard let self else { return }
            Task {
                try await self.flushRemoteUserCache()
                try await self.loadUser()
            }
        }
        .store(in: &cancellables)
    }

    // MARK: - Region / Content Edition

    func fetchUserContentEdition() async throws -> GQL.UserContentEdition {
        try await network.fetchUserContentEditionPreference()
    }

    func updateUserContentEdition(_ newValue: GQL.UserContentEdition) async throws {
        try await network.updateUserContentEditionPreference(newValue)
    }

    func updateLocalUserContentEdition(_ newValue: GQL.UserContentEdition) {
        guard var entity = userCredentials else {
            return
        }

        entity.user.fragments.customerDetail.contentEdition = newValue
        userCredentials = entity
    }

    // MARK: - Customer

    func fetchPHPAccess(userId: Int, accessToken: String) -> NetworkPublisher<UserClass> {
        let payload = SimpleUserPayload(userId: userId)
        network.restNetwork.network.updateAccessToken(accessToken)
        return network.fetchUncachedUserProfile(payload: payload)
    }

    /// Loads the user object either from memory, from apollo cache, or remote if none exists.
    /// If the users subscription is expired, this function will assume that all local caches are out of date.
    /// Additionally it will fill those caches should a remote fetch be done.
    /// - Parameter forceIgnoreCache: If `true`, Ignore the local memory, apollo caches, and fetch remotely
    /// - Returns: A User Object
    @MainActor @discardableResult
    func loadUser(forceIgnoreCache: Bool = false) async throws -> GQL.UserCredentials {
        logger.debug("Starting loadUser ignore cache: \(forceIgnoreCache)")
        do {
            let user = try await userNetworkActor.loadUser(forceIgnoreCache: forceIgnoreCache)
            userCredentials = user
            userCredentialsState = .loaded
            return user
        } catch {
            userCredentialsState = .failed
            if let error = error as? AthError, error != AthError.userIsNotLoggedIn {
                logger.debug("fetching user failed: \(error)")
            }
            /// Throw the error so the callee can respond
            throw error
        }
    }

    // MARK: - Social Login Helpers

    private func cleanFacebookCallback(_ url: URL) -> String? {
        guard
            let fbArray = url.absoluteString.removingPercentEncoding?.components(separatedBy: "?"),
            fbArray.count > 1
        else {
            ATHLogger(category: .user).error(
                "Failed to parse Facebook sign in payload, not enough components in url \(url.absoluteString)."
            )
            return nil
        }

        let sanitizedCallback = fbArray[1].replacingOccurrences(of: Constants.fbFragment, with: "")

        do {
            guard let data = sanitizedCallback.data(using: .utf8) else {
                ATHLogger(category: .user).error(
                    "Failed to parse Facebook sign in payload, unable to convert data."
                )
                return nil
            }

            let dictionary = try JSONDecoder().decode([String: String].self, from: data)
            return dictionary[Constants.fbToken]
        } catch {
            ATHLogger(category: .user).error(
                "Failed to parse Facebook sign in payload with error \(error)."
            )
            return nil
        }
    }

    private func cleanGoogleCallback(_ url: URL) -> UserSocialAuthPayload? {
        guard
            let googleArray = url.absoluteString.removingPercentEncoding?.components(
                separatedBy: "?"
            ),
            googleArray.count > 1
        else {
            ATHLogger(category: .user).error(
                "Failed to parse Google sign in payload, not enough components in url \(url.absoluteString)."
            )
            return nil
        }

        let sanitizedCallback = googleArray[1].replacingOccurrences(
            of: Constants.googleFragment,
            with: ""
        )

        let gDecoder = JSONDecoder()
        gDecoder.keyDecodingStrategy = .convertFromSnakeCase

        do {
            guard let data = sanitizedCallback.data(using: .utf8) else {
                ATHLogger(category: .user).error(
                    "Failed to parse Google sign in payload, unable to convert data."
                )
                return nil
            }

            let gUser = try gDecoder.decode(GoogleUser.self, from: data)
            return UserSocialAuthPayload(
                withDeviceId: UserSettings.deviceToken,
                tokenCode: gUser.idToken,
                grantType: .google,
                firstName: gUser.user.name.firstName,
                lastName: gUser.user.name.lastName,
                email: gUser.user.email,
                sub: gUser.sub
            )
        } catch {
            ATHLogger(category: .user).error(
                "Failed to parse Google sign in payload with error \(error)."
            )
            return nil
        }
    }

    private func cleanNewYorkTimesCallback(_ url: URL) -> UserSocialAuthPayload? {
        guard
            let tokenString = URLComponents(
                url: url,
                resolvingAgainstBaseURL: false
            )?.queryItems?.first?.name
        else {
            ATHLogger(category: .user).error(
                "Failed to parse New York Times sign in payload, not enough components in url \(url.absoluteString)."
            )
            return nil
        }

        do {
            guard let data = tokenString.data(using: .utf8) else {
                ATHLogger(category: .user).error(
                    "Failed to parse New York Times sign in payload, unable to convert data."
                )
                return nil
            }

            let decoder = JSONDecoder()
            decoder.keyDecodingStrategy = .convertFromSnakeCase
            let nytUser = try decoder.decode(NytUser.self, from: data)

            return UserSocialAuthPayload(
                withDeviceId: UserSettings.deviceToken,
                tokenCode: nytUser.idToken,
                grantType: .nyt,
                email: nytUser.email,
                sub: String(nytUser.sub),
                accessViaNyt: nytUser.accessViaNyt
            )
        } catch {
            ATHLogger(category: .user).error(
                "Failed to parse New York Times sign in payload with error \(error)."
            )
            return nil
        }
    }
}
