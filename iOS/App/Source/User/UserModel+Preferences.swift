//
//  UserModel+Preferences.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 9/16/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import AthleticApolloTypes
import AthleticFoundation
import AthleticRestNetwork
import Foundation

extension UserModel {

    func fetchEmailSettings(
        completion: @escaping CompletionResult<AthleticRestNetwork.EmailSettings>
    ) {
        assert(Thread.isMainThread, "Call on `main` to ensure thread safety on `cancellables`")

        network.fetchEmailSettings()
            .receive(on: RunLoop.main)
            .sink { result in
                switch result {
                case .success(let settings):
                    completion(.success(settings))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
            .store(in: &cancellables)
    }

    func updateEmailSetting(
        _ setting: EmailSetting,
        isOn: Bool,
        completion: @escaping CompletionResult<EmailSetting>
    ) {
        assert(Thread.isMainThread, "Call on `main` to ensure thread safety on `cancellables`")

        guard let userId = current?.id else {
            completion(.failure(AthError.userIsNotLoggedIn))
            return
        }

        network.updateEmailSetting(setting, userId: userId, isOn: isOn)
            .receive(on: RunLoop.main)
            .sink { result in
                switch result {
                case .success(let setting):
                    completion(.success(setting))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
            .store(in: &cancellables)
    }

    func updateNotificationSetting(
        type: PushNotificationSettingsType,
        entityType: NotificationEntityType,
        entityId: String,
        isOn: Bool,
        completion: @escaping VoidCompletion
    ) {
        assert(Thread.isMainThread, "Call on `main` to ensure thread safety on `cancellables`")

        guard let apiNotificationName = type.notificationApiName,
            let userId = current?.id.intValue,
            let itemIdInt = Int(entityId)
        else {
            completion(.failure(AthError.userIsNotLoggedIn))
            return
        }

        let payload = UserNotificationPayload(
            withNotificationId: itemIdInt,
            andType: entityType.rawValue,
            andName: apiNotificationName,
            andUserId: userId,
            isSubscribed: isOn
        )

        network.updateNotificationSetting(payload: payload).sink { result in
            switch result {
            case .success:
                Task { @MainActor in
                    try? await self.flushRemoteUserCache()

                    switch entityType {
                    case .team:
                        self.updateTeamNotificationsCache(
                            teamId: entityId,
                            topic: type,
                            isOn: isOn
                        )
                    case .league:
                        self.updateLeagueNotificationsCache(
                            leagueId: entityId,
                            isOn: isOn
                        )
                    case .author:
                        self.updateAuthorNotificationsCache(
                            authorId: entityId,
                            isOn: isOn
                        )
                    case .comments:
                        self.updateUserCache { $0.notifyComments = isOn }
                    default:
                        break
                    }
                    completion(.success)
                }
            case .failure(let error):
                completion(.failure(error))
            }
        }
        .store(in: &cancellables)
    }

    @MainActor
    func updateNotificationSettingForCommentsReplies(enabled: Bool) async throws {
        try await withCheckedThrowingContinuation { continuation in
            /// Entity ID is hardcoded to 1 here because thats what the backend expects
            updateNotificationSetting(
                type: .comments,
                entityType: .comments,
                entityId: "1",
                isOn: enabled
            ) { result in
                switch result {
                case .success:
                    continuation.resume()
                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }
        }
    }

    @MainActor
    func updateNotificationSettingForTopSportsNews(enabled: Bool) async throws {
        try await network.updateNotificationSettingForTopSportsNews(enabled: enabled)
        /// This does some refreshing of the data on the backend:
        /// https://github.com/TheAthletic/theathletic-apollo-express/blob/f284bccf0387cfd18e78cc2f9106cb892c0d03d4/src/datasources/athleticRestDataSource.ts#L1614
        try? await flushRemoteUserCache()

        updateUserCache { $0.notifyTopSportsNews = enabled }
    }

    // MARK: - Notifications Settings Helpers

    private func updateUserCache(applyChanges: @escaping (inout GQL.CustomerDetail) -> Void) {
        /// This is to make sure the value is correct after leaving and opening the Notifications settings screen again.
        if var userCredentials {
            applyChanges(&userCredentials.user.fragments.customerDetail)
            self.userCredentials = userCredentials
        }

        /// This is to make sure the value is correct on the Notifications settings screen after launching the app again.
        network.apolloStore.withinReadWriteTransaction { transaction in
            try? transaction.update(query: GQL.MeQuery()) { data in
                if var customer = data.customer.asCustomer {
                    applyChanges(&customer.fragments.customerDetail)
                    data.customer.asCustomer = customer
                }
            }
        }
    }

    private func updateTeamNotificationsCache(
        teamId: String,
        topic: PushNotificationSettingsType,
        isOn: Bool
    ) {
        network.apolloStore.withinReadWriteTransaction { [weak self] transaction in
            do {
                try transaction.update(query: GQL.MeQuery()) { (data: inout GQL.MeQuery.Data) in
                    guard
                        let teamIndex = data.customer.asCustomer?.fragments.customerDetail
                            .following.fragments.userFollowing.teams.firstIndex(
                                where: { $0.fragments.teamDetail.id == teamId }
                            )
                    else {
                        return
                    }

                    switch topic {
                    case .games:
                        data.customer.asCustomer?.fragments.customerDetail.following.fragments
                            .userFollowing.teams[teamIndex].fragments.teamDetail.notifGames = isOn
                    case .stories:
                        data.customer.asCustomer?.fragments.customerDetail.following.fragments
                            .userFollowing.teams[teamIndex].fragments.teamDetail.notifStories = isOn
                    case .gameStart:
                        data.customer.asCustomer?.fragments.customerDetail.following.fragments
                            .userFollowing.teams[teamIndex].fragments.teamDetail.notifGamesStart =
                            isOn
                    default:
                        break
                    }

                    guard let self else { return }
                    Task {
                        try await self.loadUser(forceIgnoreCache: true)
                    }

                }
            }
        }
    }

    private func updateLeagueNotificationsCache(leagueId: String, isOn: Bool) {
        network.apolloStore.withinReadWriteTransaction { [weak self] transaction in
            do {
                try transaction.update(query: GQL.MeQuery()) { (data: inout GQL.MeQuery.Data) in

                    guard
                        let leagueIndex = data.customer.asCustomer?.fragments.customerDetail
                            .following.fragments.userFollowing.leagues.firstIndex(
                                where: { $0.fragments.leagueDetail.id == leagueId }
                            )
                    else {
                        return
                    }

                    data.customer.asCustomer?.fragments.customerDetail.following.fragments
                        .userFollowing.leagues[leagueIndex].fragments.leagueDetail
                        .notifStories = isOn

                    guard let self else { return }
                    Task {
                        try await self.loadUser(forceIgnoreCache: true)
                    }
                }
            }
        }
    }

    private func updateAuthorNotificationsCache(authorId: String, isOn: Bool) {
        network.apolloStore.withinReadWriteTransaction { [weak self] transaction in
            do {
                try transaction.update(query: GQL.MeQuery()) { (data: inout GQL.MeQuery.Data) in

                    guard
                        let authorIndex = data.customer.asCustomer?.fragments.customerDetail
                            .following.fragments.userFollowing.authors.firstIndex(
                                where: { $0.fragments.authorDetail.id == authorId }
                            )
                    else {
                        return
                    }

                    data.customer.asCustomer?.fragments.customerDetail.following.fragments
                        .userFollowing.authors[authorIndex].fragments.authorDetail
                        .notifStories = isOn

                    guard let self else { return }
                    Task {
                        try await self.loadUser(forceIgnoreCache: true)
                    }
                }
            }
        }
    }
}
