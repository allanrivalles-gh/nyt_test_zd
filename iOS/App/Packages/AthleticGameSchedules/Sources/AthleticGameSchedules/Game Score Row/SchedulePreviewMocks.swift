//
//  SchedulePreviewMocks.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 27/2/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import Apollo
import AthleticApolloTypes
import Foundation

enum SchedulePreviewMocks {
    static let preGameNBA: GQL.ScoresFeedBlock = {
        GQL.ScoresFeedBlock(
            id: "\(#function)",
            gameId: "abc123",
            gameBlock: makeGameBlock(
                gameState: .pre,
                team1: try! .init(
                    jsonObject: makeTeam(
                        id: "team1",
                        name: "Suns",
                        logoUrl:
                            "https://cdn-team-logos.theathletic.com/team-logo-91-96x96.png?1659417060",
                        teamInfo: .makeScoresFeedTeamPregameInfoBlock(
                            id: "preGame",
                            text: "41-16"
                        )
                    ).jsonObject
                ),
                team2: try! .init(
                    jsonObject: makeTeam(
                        id: "team2",
                        name: "Bucks",
                        logoUrl: "https://cdn-team-logos.theathletic.com/team-logo-77-96x96.png",
                        teamInfo: .makeScoresFeedTeamPregameInfoBlock(
                            id: "preGame",
                            text: "33-29"
                        )
                    ).jsonObject
                )
            ),
            infoBlock: makeInfoBlock(
                text: [
                    makeDateTimeText(date: Date(timeIntervalSince1970: 0), isHighlighted: true),
                    makeStandardText(text: "TNT", type: .default),
                    makeOddsText(
                        usOdds: "SUN +125",
                        fractionalOdds: "SUN 5/4",
                        decimalOdds: "SUN 2.25",
                        teamAlias: "PHX",
                        type: .default
                    ),
                ]
            ),
            willUpdate: true
        )
    }()

    static let soccerPreGame: GQL.ScoresFeedBlock = {
        GQL.ScoresFeedBlock(
            id: "\(#function)",
            gameId: "abc123",
            header: "Premier League, Matchweek 7",
            gameBlock: makeGameBlock(
                gameState: .pre,
                team1: try! .init(
                    jsonObject: makeTeam(
                        id: "team1",
                        name: "Arsenal",
                        logoUrl:
                            "https://cdn-team-logos.theathletic.com/team-logo-651-96x96.png?1659417052",
                        teamInfo: .makeScoresFeedTeamPregameInfoBlock(
                            id: "preGame",
                            text: "1st in EPL"
                        )
                    ).jsonObject
                ),
                team2: try! .init(
                    jsonObject: makeTeam(
                        id: "team2",
                        name: "Everton",
                        logoUrl: "https://cdn-team-logos.theathletic.com/team-logo-677-96x96.png",
                        teamInfo: .makeScoresFeedTeamPregameInfoBlock(
                            id: "preGame",
                            text: "18th in EPL"
                        )
                    ).jsonObject
                )
            ),
            infoBlock: makeInfoBlock(
                text: [
                    makeDateTimeText(date: Date(timeIntervalSince1970: 0), isHighlighted: true),
                    makeStandardText(text: "Sky Sports", type: .default),
                ]
            ),
            willUpdate: true
        )
    }()

    static let soccerKnockoutPreGame: GQL.ScoresFeedBlock = {
        GQL.ScoresFeedBlock(
            id: "\(#function)",
            gameId: "abc123",
            header: "Round of 16, Game 3",
            footer: "Only takes place if some conditions are met",
            gameBlock: makeGameBlock(
                gameState: .pre,
                team1: try! .init(
                    jsonObject: makeTeam(
                        id: "team1",
                        name: "Winner of Group B",
                        logoUrl: nil,
                        isToBeDetermined: true
                    ).jsonObject
                ),
                team2: try! .init(
                    jsonObject: makeTeam(
                        id: "team2",
                        name: "Runner-up of Group A",
                        logoUrl: nil,
                        isToBeDetermined: true
                    ).jsonObject
                )
            ),
            infoBlock: makeInfoBlock(
                text: [
                    makeDateTimeText(date: Date(timeIntervalSince1970: 0), isHighlighted: true),
                    makeStandardText(text: "Sky Sports", type: .default),
                ]
            ),
            willUpdate: true
        )
    }()

    static let soccerShootout: GQL.ScoresFeedBlock = {
        GQL.ScoresFeedBlock(
            id: "\(#function)",
            gameId: "abc123",
            gameBlock: makeGameBlock(
                gameState: .post,
                team1: try! .init(
                    jsonObject: makeTeam(
                        id: "team1",
                        name: "Argentina",
                        logoUrl:
                            "https://cdn-team-logos.theathletic.com/team-logo-1180-96x96.png",
                        icons: [.soccerRedcard],
                        teamInfo: .makeScoresFeedTeamGameInfoBlock(
                            id: "teamInfo",
                            score: "3",
                            penaltyScore: "(4)",
                            isWinner: true
                        )
                    ).jsonObject
                ),
                team2: try! .init(
                    jsonObject: makeTeam(
                        id: "team2",
                        name: "France",
                        logoUrl: "https://cdn-team-logos.theathletic.com/team-logo-779-96x96.png",
                        teamInfo: .makeScoresFeedTeamGameInfoBlock(
                            id: "teamInfo",
                            score: "3",
                            penaltyScore: "(2)",
                            isWinner: false
                        )
                    ).jsonObject
                )
            ),
            infoBlock: makeInfoBlock(
                text: [
                    makeStandardText(text: "FT", type: .status),
                    makeDateText(date: Date(timeIntervalSince1970: 0), isHighlighted: false),
                ]
            ),
            willUpdate: true
        )
    }()

    static let ncaaInGame: GQL.ScoresFeedBlock = {
        GQL.ScoresFeedBlock(
            id: "\(#function)",
            gameId: "abc123",
            gameBlock: makeGameBlock(
                gameState: .live,
                team1: try! .init(
                    jsonObject: makeTeam(
                        id: "team1",
                        name: "Penn State",
                        logoUrl:
                            "https://cdn-team-logos.theathletic.com/team-logo-193-96x96.png",
                        ranking: 11,
                        icons: [.americanFootballPossession],
                        teamInfo: .makeScoresFeedTeamGameInfoBlock(
                            id: "teamInfo",
                            score: "35"
                        )
                    ).jsonObject
                ),
                team2: try! .init(
                    jsonObject: makeTeam(
                        id: "team2",
                        name: "Utah",
                        logoUrl:
                            "https://cdn-team-logos.theathletic.com/team-logo-254-96x96.png?1659417048",
                        ranking: 8,
                        teamInfo: .makeScoresFeedTeamGameInfoBlock(
                            id: "teamInfo",
                            score: "21"
                        )
                    ).jsonObject
                )
            ),
            infoBlock: makeInfoBlock(
                text: [
                    makeLiveText(text: "Q2 11:12"),
                    makeStandardText(text: "1st & 10 at TEN 12", type: .status),
                    makeStandardText(text: "ESPN", type: .default),
                ]
            ),
            willUpdate: true
        )
    }()

    static var baseballInGame: GQL.ScoresFeedBlock = {
        GQL.ScoresFeedBlock(
            id: "\(#function)",
            gameId: "abc123",
            gameBlock: makeGameBlock(
                gameState: .live,
                team1: try! .init(
                    jsonObject: makeTeam(
                        id: "team1",
                        name: "Mets",
                        logoUrl:
                            "https://cdn-team-logos.theathletic.com/team-logo-110-96x96.png?1659417061",
                        teamInfo: .makeScoresFeedTeamGameInfoBlock(
                            id: "teamInfo",
                            score: "7"
                        )
                    ).jsonObject
                ),
                team2: try! .init(
                    jsonObject: makeTeam(
                        id: "team2",
                        name: "Cardinals",
                        logoUrl:
                            "https://cdn-team-logos.theathletic.com/team-logo-118-96x96.png?1659417062",
                        teamInfo: .makeScoresFeedTeamGameInfoBlock(
                            id: "teamInfo",
                            score: "12"
                        )
                    ).jsonObject
                )
            ),
            infoBlock: makeInfoBlock(
                text: [
                    makeLiveText(text: "BOT 2"),
                    makeStandardText(text: "1 Out", type: .status),
                    makeStandardText(text: "ESPN", type: .default),
                ],
                widget: try! .init(
                    jsonObject: GQL.ScoresFeedBaseballWidgetBlock(
                        id: "bases",
                        loadedBases: [1]
                    ).jsonObject
                )
            ),
            willUpdate: true
        )
    }()
}

extension SchedulePreviewMocks {

    // MARK: Feed Block Helpers

    fileprivate static func makeGameBlock(
        gameState: GQL.GameState,
        team1: GQL.ScoresFeedGameBlock.Team1,
        team2: GQL.ScoresFeedGameBlock.Team2
    ) -> GQL.ScoresFeedBlock.GameBlock {
        try! .init(
            jsonObject: GQL.ScoresFeedGameBlock(
                id: "",
                gameState: gameState,
                team1: team1,
                team2: team2
            ).jsonObject
        )
    }

    fileprivate static func makeInfoBlock(
        text: [GQL.ScoresFeedInfoBlock.Text],
        widget: GQL.ScoresFeedInfoBlock.Widget? = nil
    ) -> GQL.ScoresFeedBlock.InfoBlock {
        try! .init(
            jsonObject: GQL.ScoresFeedInfoBlock(
                id: "",
                text: text,
                widget: widget
            ).jsonObject
        )
    }

    // MARK: Team Helpers

    fileprivate static func makeTeam(
        id: String,
        name: String,
        logoUrl: String? = nil,
        ranking: Int? = nil,
        icons: [GQL.ScoresFeedTeamIcon] = [],
        teamInfo: GQL.ScoresFeedTeamBlock.TeamInfo? = nil,
        isToBeDetermined: Bool = false
    ) -> GQL.ScoresFeedTeamBlock {
        GQL.ScoresFeedTeamBlock(
            id: id,
            isToBeDetermined: isToBeDetermined,
            name: name,
            icons: icons,
            teamInfo: teamInfo,
            ranking: ranking,
            logos: logoUrl.map { url in
                [
                    try! .init(
                        jsonObject: GQL.TeamLogo(
                            id: "",
                            uri: url,
                            width: 96,
                            height: 96
                        ).jsonObject
                    )
                ]
            } ?? []
        )
    }

    // MARK: - Info Text Helpers

    fileprivate static func makeLiveText(text: String) -> GQL.ScoresFeedInfoBlock.Text {
        try! GQL.ScoresFeedInfoBlock.Text(
            jsonObject: GQL.ScoresFeedStandardTextBlock(
                id: "live",
                type: .live,
                text: text
            ).jsonObject
        )
    }

    fileprivate static func makeDateTimeText(
        date: Date = Date(timeIntervalSince1970: 0),
        isHighlighted: Bool
    ) -> GQL.ScoresFeedInfoBlock.Text {
        try! GQL.ScoresFeedInfoBlock.Text(
            jsonObject: GQL.ScoresFeedDateTimeTextBlock(
                id: "datetime",
                type: isHighlighted ? .status : .default,
                isTimeToBeDetermined: false,
                timestamp: date,
                format: .datetime
            ).jsonObject
        )
    }

    fileprivate static func makeDateText(
        date: Date = Date(timeIntervalSince1970: 0),
        isHighlighted: Bool
    ) -> GQL.ScoresFeedInfoBlock.Text {
        try! GQL.ScoresFeedInfoBlock.Text(
            jsonObject: GQL.ScoresFeedDateTimeTextBlock(
                id: "date",
                type: isHighlighted ? .status : .default,
                isTimeToBeDetermined: false,
                timestamp: date,
                format: .date
            ).jsonObject
        )
    }

    fileprivate static func makeStandardText(
        text: String,
        type: GQL.ScoresFeedTextType
    ) -> GQL.ScoresFeedInfoBlock.Text {
        try! GQL.ScoresFeedInfoBlock.Text(
            jsonObject: GQL.ScoresFeedStandardTextBlock(
                id: text,
                type: type,
                text: text
            ).jsonObject
        )
    }

    fileprivate static func makeOddsText(
        usOdds: String,
        fractionalOdds: String,
        decimalOdds: String,
        teamAlias: String,
        type: GQL.ScoresFeedTextType
    ) -> GQL.ScoresFeedInfoBlock.Text {
        try! GQL.ScoresFeedInfoBlock.Text(
            jsonObject: GQL.ScoresFeedOddsTextBlock(
                id: "",
                type: type,
                odds: .init(usOdds: usOdds, decimalOdds: decimalOdds, fractionOdds: fractionalOdds)
            ).jsonObject
        )
    }

}
