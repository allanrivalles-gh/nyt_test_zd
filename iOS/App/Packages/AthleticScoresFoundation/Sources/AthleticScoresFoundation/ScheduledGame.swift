//
//  ScheduledGame.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 21/5/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import UIKit

public struct ScheduledGame: Hashable {
    public enum ExtraContext: Hashable {
        case baseballLoadedBases(BaseballBasesDiamond.Highlighting)
    }

    public let gameIdentifier: GameIdentifier

    public var gameId: String {
        gameIdentifier.gameId
    }

    public let status: Status
    public let gradeStatus: GradeStatus?
    public let scheduledAt: Date?
    public let isScheduledTimeTbd: Bool?
    public let startedAt: Date?
    public let leagueDisplayName: String
    public let periodTitle: String?

    public let detailMain: String?
    public let detailExtra: String?
    public let extraContext: ExtraContext?

    public let groupName: String?
    public let firstTeam: Team?
    public let secondTeam: Team?
    public let availableData: AvailableData
    public let permalink: String?

    public init(entity: GQL.GameV2Lite) {
        let gameIdentifier = GameIdentifier(leagueCode: entity.league.id, gameId: entity.id)
        self.gameIdentifier = gameIdentifier
        status = entity.status.flatMap { Status(status: $0) } ?? .other("Unknown")
        gradeStatus = entity.gradeStatus
        scheduledAt = entity.scheduledAt
        isScheduledTimeTbd = entity.isScheduledTimeTbd
        startedAt = entity.startedAt
        leagueDisplayName = entity.league.displayName
        periodTitle = entity.periodTitle
        groupName = entity.group
        detailMain = entity.gameStatus?.fragments.gameStatusDisplay.main
        detailExtra = entity.gameStatus?.fragments.gameStatusDisplay.extra
        if let baseballGame = entity.asBaseballGame, let outcome = baseballGame.outcome {
            switch baseballGame.inningHalf {
            case .top, .bottom:
                let highlightedBases = BaseballBasesDiamond.Highlighting(
                    endingBases: outcome.runners.map { $0.endingBase }
                )
                extraContext = .baseballLoadedBases(highlightedBases)
            default:
                extraContext = nil
            }
        } else {
            extraContext = nil
        }
        let americanFootballPossessingTeamId = entity.asAmericanFootballGame?.possession?.team?.id
        firstTeam = entity.firstTeam.map { gameTeam in
            ScheduledGame.Team(
                gameTeam: gameTeam,
                hasAmericanFootballPossession: americanFootballPossessingTeamId.map {
                    $0 == gameTeam.teamLite?.id
                }
            )
        }
        secondTeam = entity.secondTeam.map { gameTeam in
            ScheduledGame.Team(
                gameTeam: gameTeam,
                hasAmericanFootballPossession: americanFootballPossessingTeamId.map {
                    $0 == gameTeam.teamLite?.id
                }
            )
        }

        /// Old games have no available data value so we assume all.
        availableData = AvailableData.makeData(types: entity.coverage?.availableData ?? [.all])
        permalink = entity.permalink
    }

    public init(entity: GQL.GameV2) {
        self.init(entity: GQL.GameV2Lite(unsafeResultMap: entity.resultMap))
    }

    public init(entity: GQL.ScoresBannerGame) {
        let gameIdentifier = GameIdentifier(leagueCode: entity.league.id, gameId: entity.id)
        self.gameIdentifier = gameIdentifier
        status = entity.status.flatMap { Status(status: $0) } ?? .other("Unknown")
        gradeStatus = nil
        scheduledAt = entity.scheduledAt
        isScheduledTimeTbd = entity.isScheduledTimeTbd
        startedAt = entity.startedAt
        leagueDisplayName = entity.league.displayName
        periodTitle = entity.periodId?.title(for: gameIdentifier.sportType)
        groupName = entity.group
        detailMain = entity.gameStatus?.fragments.gameStatusDisplay.main
        detailExtra = entity.gameStatus?.fragments.gameStatusDisplay.extra
        extraContext = nil
        firstTeam = entity.firstTeam.map { ScheduledGame.Team(gameTeam: $0) }
        secondTeam = entity.secondTeam.map { ScheduledGame.Team(gameTeam: $0) }

        /// Old games have no available data value so we assume all.
        availableData = AvailableData.makeData(types: entity.coverage?.availableData ?? [.all])
        permalink = entity.permalinkString
    }

    public init(entity: GQL.FeaturedGameV2) {
        let gameIdentifier = GameIdentifier(leagueCode: entity.league.id, gameId: entity.id)
        self.gameIdentifier = gameIdentifier
        status = entity.status.flatMap { Status(status: $0) } ?? .other("Unknown")
        gradeStatus = nil
        scheduledAt = entity.scheduledAt
        isScheduledTimeTbd = entity.isScheduledTimeTbd
        startedAt = entity.startedAt
        leagueDisplayName = entity.league.displayName
        periodTitle = entity.periodId?.title(for: gameIdentifier.sportType)
        groupName = entity.group
        detailMain = entity.gameStatus?.fragments.gameStatusDisplay.main
        detailExtra = entity.gameStatus?.fragments.gameStatusDisplay.extra
        extraContext = nil
        firstTeam = entity.firstTeam.map { ScheduledGame.Team(featuredGameTeam: $0) }
        secondTeam = entity.secondTeam.map { ScheduledGame.Team(featuredGameTeam: $0) }
        availableData = AvailableData.makeData(types: entity.coverage?.availableData ?? [.all])
        permalink = entity.permalink
    }
}

extension ScheduledGame {
    public var detail: String? {
        let parts = [detailMain, detailExtra].compactMap { $0 }
        guard !parts.isEmpty else {
            return nil
        }

        return parts.joined(separator: " ")
    }
}

extension ScheduledGame {
    public enum Status: Hashable {
        case scheduled
        case ifNecessary
        case unnecessary
        case suspended
        case postponed
        case delayed
        case canceled
        case inProgress
        case final
        case other(String)

        public func title(preferShort: Bool = false) -> String {
            switch self {
            case .scheduled:
                return Strings.gameStatusScheduledTitle.localized
            case .inProgress:
                return Strings.gameStatusInProgressTitle.localized
            case .final:
                return Strings.gameStatusFinalTitle.localized
            case .suspended:
                return Strings.gameStatusSuspendedTitle.localized
            case .postponed:
                return Strings.gameStatusPostponedTitle.localized
            case .canceled:
                return Strings.gameStatusCancelledTitle.localized
            case .delayed:
                return Strings.gameStatusDelayedTitle.localized
            case .ifNecessary:
                if preferShort {
                    return Strings.gameStatusIfNecessaryShortTitle.localized
                } else {
                    return Strings.gameStatusIfNecessaryTitle.localized
                }
            case .unnecessary:
                return Strings.gameStatusUnnecessaryTitle.localized
            case .other(let status):
                return status
            }
        }

        public static var preGameExceptionCases: [Self] {
            [
                .postponed,
                .suspended,
                .canceled,
                .ifNecessary,
                .unnecessary,
                .delayed,
            ]
        }
    }

    public struct AvailableData: OptionSet, Hashable {
        public static let lineUp = AvailableData(rawValue: 1 << 0)
        public static let playerStats = AvailableData(rawValue: 1 << 1)
        public static let plays = AvailableData(rawValue: 1 << 2)
        public static let scores = AvailableData(rawValue: 1 << 3)
        public static let teamStats = AvailableData(rawValue: 1 << 4)
        public static let liveBlog = AvailableData(rawValue: 1 << 5)
        public static let commentsNavigation = AvailableData(rawValue: 1 << 6)
        public static let discoverableComments = AvailableData(rawValue: 1 << 7)
        public static let teamSpecificComments = AvailableData(rawValue: 1 << 8)

        public static let all: AvailableData = [
            .lineUp,
            .playerStats,
            .plays,
            .scores,
            .teamStats,
            .liveBlog,
            .commentsNavigation,
            .discoverableComments,
            .teamSpecificComments,
        ]

        public static func makeData(types: [GQL.GameCoverageDataType]) -> AvailableData {
            types.reduce(into: []) { result, availableData in
                switch availableData {
                case .all:
                    result.insert(.all)
                case .lineUp:
                    result.insert(.lineUp)
                case .playerStats:
                    result.insert(.playerStats)
                case .plays:
                    result.insert(.plays)
                case .scores:
                    result.insert(.scores)
                case .teamStats:
                    result.insert(.teamStats)
                case .liveBlog:
                    result.insert(.liveBlog)
                case .commentsNavigation:
                    result.insert(.commentsNavigation)
                case .discoverableComments:
                    result.insert(.discoverableComments)
                case .teamSpecificComments:
                    result.insert(.teamSpecificComments)
                case .comments:
                    /// Used as old method to see if comments are enabled
                    /// New method uses .commentsNavigation
                    break
                case .__unknown:
                    assertionFailure("Unhandled data type \(availableData)")
                    break
                }

            }
        }

        public let rawValue: Int

        public init(rawValue: Int) {
            self.rawValue = rawValue
        }
    }

    public struct Team: Hashable {
        public let id: String?
        public let logo: URL?
        public let logoSmall: URL?
        public let name: String?
        public let shortName: String?
        public let displayName: String?
        public let score: String?
        public let penaltyScore: String?
        public let details: String?
        public let ranking: Int?
        public let currentRecord: String?
        public let currentStanding: String?
        public let accentColorHex: String?
        public let hasAmericanFootballPossession: Bool?
    }
}

extension ScheduledGame.Team {
    public init(gameTeam: GQL.GameV2LiteTeam, hasAmericanFootballPossession: Bool? = nil) {
        id = gameTeam.teamLite?.id
        logo = gameTeam.teamLite?.teamLogos?.bestUrl(forSquareSize: 56)
        logoSmall = gameTeam.teamLite?.teamLogos?.bestUrl(forSquareSize: 32)
        name = gameTeam.teamLite?.name
        shortName = gameTeam.teamLite?.alias
        displayName = gameTeam.teamLite?.displayName
        score = gameTeam.score?.string
        penaltyScore = gameTeam.penaltyScore?.string
        details = nil
        ranking = gameTeam.ranking
        currentRecord = gameTeam.currentRecord
        currentStanding = gameTeam.currentStanding
        accentColorHex = gameTeam.teamLite?.colorAccent
        self.hasAmericanFootballPossession = hasAmericanFootballPossession
    }

    public init(featuredGameTeam: GQL.FeaturedGameTeam) {
        id = featuredGameTeam.team?.fragments.teamV2.id
        logo = featuredGameTeam.team?.fragments.teamV2.teamLogos.bestUrl(forSquareSize: 56)
        logoSmall = featuredGameTeam.team?.fragments.teamV2.teamLogos.bestUrl(forSquareSize: 32)
        name = featuredGameTeam.team?.fragments.teamV2.name
        shortName = featuredGameTeam.team?.fragments.teamV2.alias
        displayName = featuredGameTeam.team?.fragments.teamV2.displayName
        score = featuredGameTeam.score?.string
        penaltyScore = featuredGameTeam.penaltyScore?.string
        details = nil
        ranking = nil
        currentRecord = featuredGameTeam.currentRecord
        currentStanding = nil
        accentColorHex = featuredGameTeam.team?.fragments.teamV2.colorAccent
        hasAmericanFootballPossession = nil
    }
}

extension GamePhase {
    public init?(gameStatus: ScheduledGame.Status, startedAt: Date?) {
        self.init(gameStatus: gameStatus, hasStarted: startedAt != nil)
    }

    public init?(gameStatus: ScheduledGame.Status, hasStarted: Bool) {
        switch gameStatus {
        case .scheduled, .ifNecessary:
            self = .preGame

        case .unnecessary, .postponed, .canceled:
            self = .nonStarter

        case .delayed where hasStarted == true:
            self = .inGame

        case .delayed:
            self = .preGame

        case .inProgress, .suspended:
            self = .inGame

        case .final:
            self = .postGame

        case .other:
            return nil
        }
    }

    public init?(statusCode: GQL.GameStatusCode?, startedAt: Date?) {
        guard let statusCode = statusCode else {
            return nil
        }

        self.init(
            gameStatus: ScheduledGame.Status(status: statusCode),
            startedAt: startedAt
        )
    }

    public init?(gameState: GQL.GameState) {
        switch gameState {
        case .pre:
            self = .preGame
        case .live:
            self = .inGame
        case .post:
            self = .postGame
        case .__unknown(let rawValue):
            assertionFailure("Received unexpected game state: \(rawValue)")
            self = .nonStarter
        }
    }
}

extension ScheduledGame {
    public var phase: GamePhase? {
        GamePhase(gameStatus: status, startedAt: startedAt)
    }
}

extension ScheduledGame.Status {
    public init(status: GQL.GameStatusCode) {
        switch status {
        case .cancelled:
            self = .canceled
        case .final:
            self = .final
        case .inProgress:
            self = .inProgress
        case .postponed:
            self = .postponed
        case .scheduled:
            self = .scheduled
        case .suspended:
            self = .suspended
        case .delayed:
            self = .delayed
        case .ifNecessary:
            self = .ifNecessary
        case .unnecessary:
            self = .unnecessary
        case let .__unknown(other):
            self = .other(other)
        }
    }
}

extension ScheduledGame {
    public var needsUpdates: Bool {
        guard let phase = phase else {
            return false
        }

        return Self.needsUpdates(for: phase, scheduledAt: scheduledAt)
    }

    public var hasTeams: Bool {
        firstTeam?.id != nil && secondTeam?.id != nil
    }

    public var areCommentsEnabled: Bool {
        availableData.contains(.commentsNavigation)
    }

    public var areCommentsDiscoverable: Bool {
        availableData.contains(.discoverableComments)
    }

    public var areTeamSpecificCommentsEnabled: Bool {
        availableData.contains(.teamSpecificComments)
    }

    public var title: String {
        /// Make sure first and second team are not nil
        guard let firstTeamName = firstTeam?.shortName, let secondTeamName = secondTeam?.shortName
        else { return "" }
        return "\(firstTeamName) \(gameIdentifier.isSoccer ? "v" : "@") \(secondTeamName)"
    }

    public func subscribedTeamIds() -> [String] {
        let teamIds = [firstTeam?.id, secondTeam?.id].compactMap { $0 }

        guard let user = ScoresEnvironment.shared.currentUser() else { return teamIds }

        let followingTeamIds = user.following.fragments.userFollowing.teams
            .flatMap {
                [$0.fragments.teamDetail.id, $0.fragments.teamDetail.teamv2?.id]
                    .compactMap { $0 }
            }
        let filteredTeamIds = Array(Set(teamIds).intersection(followingTeamIds))

        return filteredTeamIds.isEmpty ? teamIds : filteredTeamIds
    }

    public static func needsUpdates(
        for phase: GamePhase,
        scheduledAt: Date?
    ) -> Bool {
        guard !ScoresEnvironment.shared.config.shouldAllGamesSubscribeForUpdates() else {
            return true
        }

        let isDueToStart: Bool

        if let gameTime = scheduledAt {
            let isStartTimeInNext30 = gameTime < Date().addingTimeInterval(30.minutes)
            let isPastStartTime = Date() > gameTime
            isDueToStart = phase == .preGame && (isStartTimeInNext30 || isPastStartTime)
        } else {
            isDueToStart = false
        }

        return phase == .inGame || isDueToStart
    }
}

extension GQL.GameV2Lite {
    fileprivate var periodTitle: String? {
        let gameIdentifier = GameIdentifier(leagueCode: league.id, gameId: id)

        guard let baseballGame = asBaseballGame else {
            return periodId?.title(for: gameIdentifier.sportType)
        }

        guard
            let inning = baseballGame.inning,
            let inningHalf = baseballGame.inningHalf
        else {
            return nil
        }

        return inningHalf.title(forInning: inning)
    }
}

extension GQL.GameV2LiteTeam {
    fileprivate var ranking: Int? {
        if let team = asAmericanFootballGameTeam {
            return team.currentRanking
        } else if let team = asBasketballGameTeam {
            return team.currentRanking
        } else {
            return nil
        }
    }
}
