//
//  GoogleUser.swift
//  theathletic-ios
//
//  Created by Charles Huang on 4/6/20.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import Foundation

struct GoogleUser: Codable {
    let user: GoogleUserInfo
    let sub: String
    let idToken: String
}

struct GoogleUserInfo: Codable {
    let name: GoogleUserName
    let email: String
}

struct GoogleUserName: Codable {
    let firstName: String
    let lastName: String?
}
