//
//  GQLCustomerDetail+AccessLevel.swift
//
//
//  Created by Mark Corbyn on 19/5/2023.
//

import Foundation

extension GQL.CustomerDetail {
    public var isStaff: Bool {
        return userLevel > 0
    }

    public var isAdmin: Bool {
        return userLevel >= 10
    }
}
