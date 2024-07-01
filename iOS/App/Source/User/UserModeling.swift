//
//  UserModeling.swift
//  theathletic-iosTests
//
//  Created by Tim Korotky on 22/10/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Combine
import Foundation

protocol UserModeling {
    var current: GQL.CustomerDetail? { get }
    var isLoggedIn: Bool { get }
    var userCredentialsPublisher: AnyPublisher<GQL.UserCredentials?, Never> { get }

    func updateCurrentFollowing(with followingObject: GQL.CustomerDetail.Following)

    func updateNotificationSettingForCommentsReplies(enabled: Bool) async throws

    func updateNotificationSettingForTopSportsNews(enabled: Bool) async throws
}
