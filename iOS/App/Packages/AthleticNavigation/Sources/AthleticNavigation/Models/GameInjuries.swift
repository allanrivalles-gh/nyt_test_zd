//
//  GameInjuries.swift
//
//  Created by Mark Corbyn on 5/6/2023.
//

import AthleticAnalytics
import AthleticUI
import Foundation
import SwiftUI

public struct GameInjuries: Codable, Hashable {
    public struct Team: Identifiable, Codable, Hashable {
        public struct PlayerInjury: Identifiable, Codable, Hashable {
            public enum Severity: Codable {
                case major
                case minor
                case unknown
            }

            public let id: String
            public let headshots: [ATHImageResource]
            public let headshotColor: Color?
            public let playerName: String
            public let playerPosition: String?
            public let availabilityStatus: String?
            public let injuryDescription: String
            public let severity: Severity
            public let clickAnalyticsView: AnalyticsEvent.View

            public init(
                id: String,
                headshots: [ATHImageResource],
                headshotColor: Color? = nil,
                playerName: String,
                playerPosition: String? = nil,
                availabilityStatus: String? = nil,
                injuryDescription: String,
                severity: GameInjuries.Team.PlayerInjury.Severity,
                clickAnalyticsView: AnalyticsEvent.View
            ) {
                self.id = id
                self.headshots = headshots
                self.headshotColor = headshotColor
                self.playerName = playerName
                self.playerPosition = playerPosition
                self.availabilityStatus = availabilityStatus
                self.injuryDescription = injuryDescription
                self.severity = severity
                self.clickAnalyticsView = clickAnalyticsView
            }
        }

        public let id: String
        public let title: String
        public let players: [PlayerInjury]

        public init(id: String, title: String, players: [GameInjuries.Team.PlayerInjury]) {
            self.id = id
            self.title = title
            self.players = players
        }
    }

    public let teams: [Team]
    public let selectedTeamId: String?

    public init(teams: [GameInjuries.Team], selectedTeamId: String? = nil) {
        self.teams = teams
        self.selectedTeamId = selectedTeamId
    }
}
