//
//  BracketsPreviewHelper.swift
//
//
//  Created by Jason Xu on 11/7/22.
//

import AthleticAnalytics
import AthleticApolloNetworking
import Foundation

struct BracketsPreviewHelper {
    static let network = NetworkModel(
        graphNetwork: .init(environment: .stage),
        restNetwork: .init(clientName: "ios", environment: .stage)
    )
}
