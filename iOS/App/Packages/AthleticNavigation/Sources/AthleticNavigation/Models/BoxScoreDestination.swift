//
//  BoxScoreDestination.swift
//
//
//  Created by Mark Corbyn on 5/6/2023.
//

import Foundation

public struct BoxScoreDestination: Codable, Hashable {
    public enum Tab: Codable, Hashable {
        /// Box score is the default, but this allows for supplying a focusable section to scroll to
        case boxScore(section: BoxScoreFocusableSection?)
        case comments(String?)
        case playerGrades
        case liveBlog
        case stats
        case plays
    }

    public let gameId: String
    public let initialSelectionOverride: Tab?

    public init(gameId: String, initialSelectionOverride: BoxScoreDestination.Tab? = nil) {
        self.gameId = gameId
        self.initialSelectionOverride = initialSelectionOverride
    }
}
