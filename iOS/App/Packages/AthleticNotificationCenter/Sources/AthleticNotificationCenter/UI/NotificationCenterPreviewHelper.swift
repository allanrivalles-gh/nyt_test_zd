//
//  NotificationCenterPreviewHelper.swift
//
//
//  Created by Jason Leyrer on 9/13/23.
//

import AthleticApolloNetworking
import AthleticApolloTypes
import Foundation

struct NotificationCenterPreviewHelper {
    static let network = NetworkModel(
        graphNetwork: .init(environment: .stage),
        restNetwork: .init(clientName: "ios", environment: .stage)
    )
}
