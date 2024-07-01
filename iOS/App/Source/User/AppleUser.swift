//
//  AppleUser.swift
//  theathletic-ios
//
//  Created by Charles Huang on 3/25/20.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import AthleticRestNetwork
import AuthenticationServices
import Foundation

struct AppleUser {
    let loginType: LoginType
    var grantType: GrantType
    let token: String
    let sub: String
    let email: String?
    let firstName: String?
    let lastName: String?

    init(token: String, sub: String, email: String?, firstName: String?, lastName: String?) {
        self.token = token
        self.sub = sub
        self.email = email
        self.firstName = firstName
        self.lastName = lastName
        self.loginType = .apple(token: token)
        self.grantType = .apple
    }
}

extension AppleUser {
    init?(credential: ASAuthorizationAppleIDCredential) {
        guard
            let tokenData = credential.identityToken,
            let token = String(bytes: tokenData, encoding: .utf8),
            let subData = credential.authorizationCode,
            let sub = String(bytes: subData, encoding: .utf8)
        else {
            return nil
        }

        self.init(
            token: token,
            sub: sub,
            email: credential.email,
            firstName: credential.fullName?.givenName,
            lastName: credential.fullName?.familyName
        )
    }
}
