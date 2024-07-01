//
//  NytUser.swift
//  theathletic-ios
//
//  Created by Jason Xu on 8/22/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation

struct NytUser: Codable {
    let email: String
    let sub: String
    let accessToken: String
    let accessViaNyt: Bool
    let idToken: String
}
