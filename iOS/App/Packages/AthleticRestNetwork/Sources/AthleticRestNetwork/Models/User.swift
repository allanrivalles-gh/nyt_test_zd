//
//  File.swift
//
//
//  Created by Kyle Browning on 12/24/19.
//

import Foundation

// MARK: - User
public struct User: Codable {
    public let accessToken: String
    public let expiresIn: Int
    public let tokenType: String
    public let user: UserClass
}

public enum GrantType: String, Encodable {
    case facebook
    case password
    case apple
    case google
    case nyt
    case existing
}

// MARK: - UserClass
public struct UserClass: Codable {
    public let isAmbassador: Int
    public let avatarURL: String?
    public let cityId, commentsActivated: Int
    public let email: String
    public let endDate: Date?
    public let fbID: String?
    public let fname: String
    public let isInGracePeriod, hasInvalidEmail: Bool
    public let id, isAppleSubscriber, isFbLinked: Int
    public let lname: String
    public let notifComments: Int
    public let showOnboarding: Bool
    public let socialName: String?
    public let stripeCustomerID, timezone: String?
    public let userLevel: Int
    public let isAnonymous, hasCommentTab: Bool
    public let remoteConfig: RemoteConfig
    public let privacyPolicy, termsAndConditions: Bool
    public let referralsRedeemed: Int
    public let referralsTotal: Int
    public let attributionSurveyEligible: Bool
}

// MARK: - RemoteConfig
public struct RemoteConfig: Codable {
    public let isDiscussionTabEnabled: Bool
}
// MARK: User Password Payload
/// - Tag: UserPasswordPayload
public struct UserPasswordPayload: Encodable {
    public init(
        withUsername username: String,
        andPassword password: String,
        andDeviceId deviceId: String
    ) {
        self.username = username
        self.password = password
        self.deviceId = deviceId
    }
    public let username: String
    public let password: String
    public let deviceId: String
    public let grantType: GrantType = .password
}

public struct UserSocialAuthPayload: Encodable {
    public init(
        withDeviceId deviceId: String,
        tokenCode: String,
        grantType: GrantType,
        firstName: String? = nil,
        lastName: String? = nil,
        email: String? = nil,
        sub: String? = nil,
        accessViaNyt: Bool? = nil
    ) {
        self.deviceId = deviceId
        self.tokenCode = tokenCode
        self.grantType = grantType
        self.firstName = firstName
        self.lastName = lastName
        self.email = email
        self.sub = sub
        self.accessViaNyt = accessViaNyt
    }
    public let tokenCode: String
    public let deviceId: String
    public let grantType: GrantType
    public let firstName: String?
    public let lastName: String?
    public let email: String?
    public let sub: String?
    public let accessViaNyt: Bool?
}
