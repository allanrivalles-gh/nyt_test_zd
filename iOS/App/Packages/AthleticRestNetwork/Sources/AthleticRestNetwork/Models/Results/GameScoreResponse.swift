//
//  GameScoreResponse.swift
//
//
//  Created by Eric Yang on 19/2/20.
//

import Foundation

public enum SeasonType: Codable, RawRepresentable, Equatable {
    public typealias RawValue = Int

    case regularSeason
    case preseason
    case postseason
    case offseason
    case allStar
    case other(Int)

    public init?(rawValue: RawValue) {
        switch rawValue {
        case 1:
            self = .regularSeason
        case 2:
            self = .preseason
        case 3:
            self = .postseason
        case 4:
            self = .offseason
        case 5:
            self = .allStar
        default:
            self = .other(rawValue)
        }
    }

    public var rawValue: Int {
        switch self {
        case .regularSeason:
            return 1
        case .preseason:
            return 2
        case .postseason:
            return 3
        case .offseason:
            return 4
        case .allStar:
            return 5
        case .other(let type):
            return type
        }
    }

    public init(from decoder: Decoder) throws {
        guard let typeString = try? decoder.singleValueContainer().decode(String.self),
            let typeInt = Int(typeString),
            let typeEnum = SeasonType(rawValue: typeInt)
        else {
            self = .other(Int.min)  //Use Int.min to indicate null or non-int value
            return
        }
        self = typeEnum
    }
}

public enum GameStatus: Codable, RawRepresentable, Equatable {
    public typealias RawValue = String

    case scheduled
    case inProgress
    case final
    case FOT
    case FINALOT
    case FSO
    case suspended
    case postponed
    case canceled
    case `break`
    case awarded
    case FT
    case other(String)

    public init?(rawValue: RawValue) {
        switch rawValue {
        case "Scheduled":
            self = .scheduled
        case "InProgress":
            self = .inProgress
        case "Final":
            self = .final
        case "F/OT":
            self = .FOT
        case "FINAL/OT":
            self = .FINALOT
        case "F/SO":
            self = .FSO
        case "Suspended":
            self = .suspended
        case "Postponed":
            self = .postponed
        case "Canceled":
            self = .canceled
        case "Break":
            self = .break
        case "Awarded":
            self = .awarded
        case "FT":
            self = .FT
        default:
            self = .other(rawValue)
        }
    }

    public var rawValue: String {
        switch self {
        case .scheduled:
            return "Scheduled"
        case .inProgress:
            return "InProgress"
        case .final:
            return "Final"
        case .FOT:
            return "F/OT"
        case .FINALOT:
            return "FINAL/OT"
        case .FSO:
            return "F/SO"
        case .suspended:
            return "Suspended"
        case .postponed:
            return "Postponed"
        case .canceled:
            return "Canceled"
        case .break:
            return "Break"
        case .awarded:
            return "Awarded"
        case .FT:
            return "FT"
        case .other(let status):
            return status
        }
    }

    public init(from decoder: Decoder) throws {
        guard let status = try? decoder.singleValueContainer().decode(String.self),
            let statusEnum = GameStatus(rawValue: status)
        else {
            self = .other("")  // Use "" to indicat null
            return
        }
        self = statusEnum
    }
}

// MARK: GameScoreResponse
/// - tag: GameScoreResponse
public struct GameScoreResponse: Codable {
    public let schedule, scores: [GameScoreSchedule]?
    public let standings: GameScoreStandings?
    public let navigation: [GameScoreNavigation]
}
// MARK: - Navigation
public struct GameScoreNavigation: Codable {
    public let index: Int
    public let style: String
    public let entities: [GameScoreEntity]
}

// MARK: - GameScoreEntity
public struct GameScoreEntity: Codable {
    public let title: String?
    public let deeplinkUrl: String?
    public let entityType: String
    public let index: Int
    public let deeplinkUrlBase, formatType: String?
}

// MARK: - GameScoreSchedule
public struct GameScoreSchedule: Codable {
    @CodableValueNullable
    public var underscoreId: String?
    @CodableValueNullable
    public var gameId: String?
    public let homeTeamDetails: String?
    public let gameTime: Date?
    public let awayTeamShortname, season: String?
    public let homeScore: String?
    public let displaySeason: String?
    public let homeTeamLongname, homeTeamShortname: String?
    public let channel: String?
    public let status: GameStatus
    public let awayTeamLongname: String?
    @CodableValueNullable
    public var week: Int?
    @CodableValueNullable
    public var origWeek: Int?
    public let awayTeamDetails: String?
    public let seasonType: SeasonType
    public let homeTeamName, homeTeamUrl: String?
    public let awayTeamUrl, awayTeamName: String?
    public let awayScore: String?
    public let homeTeamId: String?
    public let timeTbd: Bool?
    @CodableValueNullable
    public var leagueId: String?
    @CodableValueNullable
    public var everyPlayerId: String?
    public var awayTeamId: String?
    public let awayTeamRecord: String?
    public let homeTeamRecord: String?
    @CodableValueNullable
    public var finalInning: Int?
    public let homeTeamConference, awayTeamConference: String?
    @CodableValueNullable
    public var round: Int?
    public let bracket: String?
}

// MARK: - GameScoreStandings
public struct GameScoreStandings: Codable {
    public let groupings: [StandingGrouping]?
    public let teamDetails: ArrayCodable<StandingTeamDetails>?
    @CodableValueNullable
    public var leagueId: Int?
    public let season: String?
}

// MARK: - StandingGrouping
public struct StandingGrouping: Codable {
    public let sections: [StandingSection]?
    public let label: String?
}

// MARK: - StandingSection
public struct StandingSection: Codable {
    public let headersTeam: [String]?
    public let label: String?
    public let headers: [String]?
    public let rows: [StandingRow]?

    enum CodingKeys: String, CodingKey {
        case headersTeam = "headers-team"
        case label, headers, rows
    }
}

// MARK: - StandingRow
public struct StandingRow: Codable {
    public let teamUrl: String?
    public let sequence: Int?
    @CodableValueArray
    public var columns: [String]?
    public let columnsTeam: [String]?
    public let teamFullname: String?
    @CodableValueNullable
    public var teamId: Int?
    public let teamLongname, teamName: String?

    enum CodingKeys: String, CodingKey {
        case columnsTeam = "columns-team"
        case teamUrl, sequence, columns, teamFullname, teamId, teamLongname, teamName
    }
}

// MARK: - StandingTeamDetails
public struct StandingTeamDetails: Codable {
    let teamId, name, cityname, stat1Label: String?
    let stat1Value, stat2Label, stat2Value, stat3Label: String?
    let stat3Value: String?
    let rank, group, groupDescription, recordString: String?
}
