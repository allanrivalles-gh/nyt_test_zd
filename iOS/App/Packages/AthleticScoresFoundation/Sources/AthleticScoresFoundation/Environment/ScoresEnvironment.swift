//
//  ScoresEnvironment.swift
//
//
//  Created by Mark Corbyn on 19/5/2023.
//

import AthleticAnalytics
import AthleticApolloTypes
import Foundation

public final class ScoresEnvironment {

    public static var shared: ScoresEnvironment!

    public let config: ConfigResolver
    public let currentUser: () -> GQL.CustomerDetail?
    public let network: ScoresNetworking
    public let makeAnalyticsDefaults: () -> AnalyticsRequiredValues

    public init(
        config: ConfigResolver,
        currentUser: @escaping () -> GQL.CustomerDetail?,
        network: ScoresNetworking,
        makeAnalyticsDefaults: @escaping () -> AnalyticsRequiredValues
    ) {
        self.config = config
        self.currentUser = currentUser
        self.network = network
        self.makeAnalyticsDefaults = makeAnalyticsDefaults
    }
}
