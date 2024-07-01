//
//  Array+TicketLogo.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 10/7/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticUI
import Foundation

extension Array where Element == GQL.TicketLogo {
    var resources: [ATHImageResource] {
        map { .init(id: $0.id, url: $0.uri.url, width: $0.width, height: $0.height) }
    }
}
