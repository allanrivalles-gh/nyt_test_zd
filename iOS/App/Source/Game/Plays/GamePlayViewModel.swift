//
//  GamePlayViewModel.swift
//  theathletic-ios
//
//  Created by Eric Yang on 30/7/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticUI
import Foundation

struct GamePlayViewModel: Identifiable {

    enum Image {
        case team([ATHImageResource])
        case player(images: [ATHImageResource], colorHex: String?)
    }

    enum TrailingInfo {
        case scores(GamePlayScoreDetailViewModel)
        case result(ScoringResult)
        case icon(String)
    }

    struct Team {
        let id: String
        let alias: String
    }

    enum ScoringResult {
        case goal, save
    }

    let id: String
    let curtainColor: String?
    let titlePrefix: String?
    let titleSuffix: String?
    let info: String?
    let leadingText: String?
    let image: Image?
    let trailingInfo: TrailingInfo?
    let occurredAtString: String
    var needsPaddedTopDivider: Bool = false

    init(
        id: String,
        curtainColor: String? = nil,
        image: Image? = nil,
        titlePrefix: String? = nil,
        titleSuffix: String? = nil,
        info: String? = nil,
        leadingText: String? = nil,
        occurredAtString: String,
        trailingInfo: TrailingInfo? = nil
    ) {
        self.id = id
        self.curtainColor = curtainColor
        self.image = image
        self.titlePrefix = titlePrefix
        self.titleSuffix = titleSuffix
        self.info = info
        self.leadingText = leadingText
        self.occurredAtString = occurredAtString
        self.trailingInfo = trailingInfo
    }

    init(
        play: GQL.AmericanFootballScoringPlay,
        firstTeam: Team,
        secondTeam: Team,
        allowScoringPlayCurtain: Bool
    ) {
        id = play.id
        curtainColor = allowScoringPlayCurtain ? play.teamV2.colorAccent : nil
        image = .team(play.teamV2.teamLogos)
        titlePrefix = play.header
        titleSuffix = play.possession?.fragments.americanFootballPossession.summary
        info = play.description
        leadingText = play.clock
        occurredAtString = play.occurredAtStr
        trailingInfo = .scores(
            GamePlayScoreDetailViewModel(
                firstScore: GamePlayScoreViewModel(
                    title: firstTeam.alias,
                    value: play.firstScore
                ),
                secondScore: GamePlayScoreViewModel(
                    title: secondTeam.alias,
                    value: play.secondScore
                )
            )
        )
    }

    init(
        play: GQL.AmericanFootballPlay,
        firstTeam: Team,
        secondTeam: Team,
        allowScoringPlayCurtain: Bool
    ) {
        id = play.id
        curtainColor = allowScoringPlayCurtain && play.isScoringPlay ? play.team?.colorAccent : nil
        image = play.team.map {
            .team($0.logos.map { $0.fragments.teamLogo }.map(ATHImageResource.init))
        }
        titlePrefix = play.header
        titleSuffix = play.possession?.fragments.americanFootballPossession.summary
        info = play.description
        leadingText = play.clock
        occurredAtString = play.occurredAtStr
        if play.isScoringPlay {
            trailingInfo = .scores(
                GamePlayScoreDetailViewModel(
                    firstScore: GamePlayScoreViewModel(
                        title: firstTeam.alias,
                        value: play.firstScore
                    ),
                    secondScore: GamePlayScoreViewModel(
                        title: secondTeam.alias,
                        value: play.secondScore
                    )
                )
            )
        } else {
            trailingInfo = nil
        }
    }

    init(play: GQL.HockeyPlay) {
        id = play.id
        curtainColor = nil
        image = nil
        titlePrefix = play.header
        titleSuffix = play.gameTime
        info = play.description
        trailingInfo = nil
        leadingText = nil
        occurredAtString = play.occurredAtStr
    }

    init(
        play: GQL.HockeyTeamPlay,
        firstTeam: Team,
        secondTeam: Team,
        allowScoringPlayCurtain: Bool
    ) {
        id = play.id
        curtainColor =
            allowScoringPlayCurtain && play.type.isScoringPlay
            ? play.team.colorAccent
            : nil
        image = .team(
            play.team.logos
                .map { ATHImageResource(entity: $0.fragments.teamLogo) }
        )
        leadingText = nil
        titlePrefix = play.header
        titleSuffix = play.gameTime
        info = play.description
        occurredAtString = play.occurredAtStr
        if play.type.isScoringPlay {
            trailingInfo = .scores(
                GamePlayScoreDetailViewModel(
                    firstScore: GamePlayScoreViewModel(
                        title: firstTeam.alias,
                        value: play.firstScore
                    ),
                    secondScore: GamePlayScoreViewModel(
                        title: secondTeam.alias,
                        value: play.secondScore
                    )
                )
            )
        } else {
            trailingInfo = nil
        }
    }

    init(play: GQL.HockeyShootoutPlay) {
        id = play.id
        curtainColor = nil
        image = .player(
            images: play.shooter?.headshots
                .map { ATHImageResource(entity: $0.fragments.playerHeadshot) }
                ?? [],
            colorHex: play.team.colorPrimary
        )
        leadingText = nil
        titlePrefix = play.shooter?.displayName ?? play.header
        titleSuffix = play.team.alias
        info = play.description
        occurredAtString = play.occurredAtStr

        switch play.type {
        case .shootoutgoal:
            trailingInfo = .result(.goal)

        case .shootoutshotmissed, .shootoutshotsaved:
            trailingInfo = .result(.save)

        default:
            assertionFailure("Unexpected shootout play type: \(play.type)")
            trailingInfo = .result(.save)
        }
    }

    init(
        play: GQL.BaseballScoringPlay,
        firstTeam: Team,
        secondTeam: Team,
        allowScoringPlayCurtain: Bool
    ) {
        id = play.id
        curtainColor = allowScoringPlayCurtain ? play.team.colorAccent : nil
        image = .team(
            play.team.logos
                .map { ATHImageResource(entity: $0.fragments.teamLogo) }
        )
        leadingText = nil
        titlePrefix = play.header
        titleSuffix = nil
        info = play.description
        occurredAtString = play.occurredAtStr
        trailingInfo = .scores(
            GamePlayScoreDetailViewModel(
                firstScore: GamePlayScoreViewModel(
                    title: firstTeam.alias,
                    value: play.firstScore
                ),
                secondScore: GamePlayScoreViewModel(
                    title: secondTeam.alias,
                    value: play.secondScore
                )
            )
        )
    }

    init(
        play: GQL.BasketballPlay,
        firstTeam: Team,
        secondTeam: Team,
        allowScoringPlayCurtain: Bool
    ) {
        id = play.id
        curtainColor = allowScoringPlayCurtain && play.isScoringPlay ? play.team?.colorAccent : nil
        image = play.team.map {
            .team($0.logos.map { ATHImageResource(entity: $0.fragments.teamLogo) })
        }
        leadingText = nil
        titlePrefix = play.header
        titleSuffix = play.team != nil ? play.clock : nil
        info = play.description
        occurredAtString = play.occurredAtStr

        if play.isScoringPlay {
            trailingInfo = .scores(
                GamePlayScoreDetailViewModel(
                    firstScore: GamePlayScoreViewModel(
                        title: firstTeam.alias,
                        value: play.firstScore
                    ),
                    secondScore: GamePlayScoreViewModel(
                        title: secondTeam.alias,
                        value: play.secondScore
                    )
                )
            )
        } else {
            trailingInfo = nil
        }
    }

    init(
        play: GQL.BaseballTeamPlay,
        firstAlias: String,
        secondAlias: String,
        allowScoringPlayCurtain: Bool
    ) {
        id = play.id
        curtainColor = allowScoringPlayCurtain ? play.team.colorAccent : nil
        image = .team(play.team.logos.map { ATHImageResource(entity: $0.fragments.teamLogo) })
        leadingText = nil
        titlePrefix = play.header
        titleSuffix = nil
        info = play.description
        occurredAtString = play.occurredAtStr
        trailingInfo = .scores(
            GamePlayScoreDetailViewModel(
                firstScore: GamePlayScoreViewModel(
                    title: firstAlias,
                    value: play.firstScore
                ),
                secondScore: GamePlayScoreViewModel(
                    title: secondAlias,
                    value: play.secondScore
                )
            )
        )
    }

    init(
        soccerPlay: GQL.SoccerStandardPlay,
        firstAlias: String?,
        secondAlias: String?,
        allowScoringPlayCurtain: Bool,
        needsTopDivider: Bool
    ) {
        id = soccerPlay.id
        self.needsPaddedTopDivider = needsTopDivider

        curtainColor =
            allowScoringPlayCurtain && soccerPlay.isScoringPlay
            ? soccerPlay.soccerPlayByPlayTeam?.colorAccent
            : nil

        if let teamLogos = soccerPlay.soccerPlayByPlayTeam?.logos {
            image = .team(teamLogos.map { ATHImageResource(entity: $0.fragments.teamLogo) })
        } else {
            image = nil
        }

        leadingText = nil
        titlePrefix = soccerPlay.header
        titleSuffix = soccerPlay.gameTime
        info = soccerPlay.description
        trailingInfo = soccerPlay.trailingInfo(firstAlias: firstAlias, secondAlias: secondAlias)
        occurredAtString = soccerPlay.occurredAtStr
    }
}

extension GQL.AmericanFootballPlay {
    fileprivate var firstScore: String {
        awayScore.string
    }

    fileprivate var secondScore: String {
        homeScore.string
    }
}

extension GQL.BasketballPlay {
    fileprivate var isScoringPlay: Bool {
        [
            .freethrowmade,
            .threepointmade,
            .threepointmadeandfoul,
            .twopointmade,
            .twopointmadeandfoul,
        ].contains(self.type)
    }
}

extension GQL.BaseballScoringPlay {
    fileprivate var firstScore: String {
        awayScore.string
    }

    fileprivate var secondScore: String {
        homeScore.string
    }
}

extension GQL.HockeyTeamPlay {
    fileprivate var firstScore: String {
        awayScore.string
    }

    fileprivate var secondScore: String {
        homeScore.string
    }
}

extension GQL.AmericanFootballScoringPlay {
    fileprivate var firstScore: String {
        awayScore.string
    }

    fileprivate var secondScore: String {
        homeScore.string
    }
}

extension GQL.BasketballPlay {
    fileprivate var firstScore: String {
        awayScore.string
    }

    fileprivate var secondScore: String {
        homeScore.string
    }
}

extension GQL.SoccerStandardPlay {
    fileprivate var firstScore: String {
        homeScore.string
    }

    fileprivate var secondScore: String {
        awayScore.string
    }
}

extension GQL.SoccerStandardPlay {

    fileprivate var isScoringPlay: Bool {
        switch type {
        case .goal, .ownGoal, .penaltyGoal:
            return true
        default:
            return false
        }
    }

    fileprivate func trailingInfo(
        firstAlias: String?,
        secondAlias: String?
    ) -> GamePlayViewModel.TrailingInfo? {
        switch type {
        case .yellowCard:
            return .icon("icn_soccer_yellow_card")

        case .secondYellowCard:
            return .icon("icn_soccer_yellow_red_card_centered")

        case .redCard:
            return .icon("icn_soccer_red_card")

        case .substitution:
            return .icon("icn_soccer_substitute_on_off")

        case .goal, .ownGoal, .penaltyGoal:
            return .scores(
                GamePlayScoreDetailViewModel(
                    firstScore: GamePlayScoreViewModel(
                        title: firstAlias ?? .gameStatPlaceholder,
                        value: firstScore.string
                    ),
                    secondScore: GamePlayScoreViewModel(
                        title: secondAlias ?? .gameStatPlaceholder,
                        value: secondScore.string
                    )
                )
            )
        default:
            return nil
        }
    }

}
