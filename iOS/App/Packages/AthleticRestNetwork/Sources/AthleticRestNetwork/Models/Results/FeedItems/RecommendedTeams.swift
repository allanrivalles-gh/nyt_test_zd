//
//  RecommendedTeams.swift
//
//
//  Created by Eric Yang on 13/2/20.
//

import Foundation

// MARK: - RecommendedTeamsDecoded
/// - tag: RecommendedTeamsDecoded
public struct RecommendedTeamsDecoded: Codable {
    public let groups: [RecommendedTeamsGroup]
}

// MARK: - Group
public struct RecommendedTeamsGroup: Codable {
    public let index: Int
    public let title: String
    public let teams: [RecommendedTeam]
}

// MARK: - Team
public struct RecommendedTeam: Codable {
    public let id: String
    public let cityId, leagueId, name: String
    public let shortname, searchText: String
    public let cityname: String?
    public let status: String?
}
