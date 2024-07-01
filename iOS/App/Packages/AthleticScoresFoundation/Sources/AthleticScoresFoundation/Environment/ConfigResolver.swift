//
//  EnvironmentConfigResolver.swift
//
//  Resolves config related information from the main app environment.
//
//  Created by Mark Corbyn on 19/5/2023.
//

import AthleticApolloTypes
import Foundation

public struct ConfigResolver {

    public let shouldAllGamesSubscribeForUpdates: () -> Bool

    public let isSoccerPlayerGradesEnabled: () -> Bool
    public let isBasketballPlayerGradesEnabled: () -> Bool
    public let isBaseballPlayerGradesEnabled: () -> Bool
    public let isHockeyPlayerGradesEnabled: () -> Bool

    public init(
        shouldAllGamesSubscribeForUpdates: @autoclosure @escaping () -> Bool,
        isSoccerPlayerGradesEnabled: @autoclosure @escaping () -> Bool,
        isBasketballPlayerGradesEnabled: @autoclosure @escaping () -> Bool,
        isBaseballPlayerGradesEnabled: @autoclosure @escaping () -> Bool,
        isHockeyPlayerGradesEnabled: @autoclosure @escaping () -> Bool
    ) {
        self.shouldAllGamesSubscribeForUpdates = shouldAllGamesSubscribeForUpdates
        self.isSoccerPlayerGradesEnabled = isSoccerPlayerGradesEnabled
        self.isBasketballPlayerGradesEnabled = isBasketballPlayerGradesEnabled
        self.isBaseballPlayerGradesEnabled = isBaseballPlayerGradesEnabled
        self.isHockeyPlayerGradesEnabled = isHockeyPlayerGradesEnabled
    }
}
