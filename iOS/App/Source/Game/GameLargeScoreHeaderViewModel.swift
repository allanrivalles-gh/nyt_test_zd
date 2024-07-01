//
//  GameLargeScoreHeaderViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 29/7/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticScoresFoundation
import AthleticUI
import Foundation
import SwiftUI

protocol GameLargeScoreHeaderViewModelDelegate: AnyObject {
    func viewModel(
        _ viewModel: GameLargeScoreHeaderViewModel,
        needsToTrackSelectedEntity entity: FollowingEntity
    )
}

final class GameLargeScoreHeaderViewModel: ObservableObject {
    struct Constants {
        static let defaultScoreColor = Color.chalk.dark700
        static let dimmedScoreColor = Color.chalk.dark500
    }

    enum LayoutMode {
        case largeIcon
        case smallIcon
    }

    enum CenterMode {
        case normal(GameStackedTitlesViewModel)
        case live(StatusDetails)
    }

    enum StatusDetails {
        case `default`(GameGeneralSportStatusViewModel)
        case mlb(GameMLBStatusViewModel)
        case soccer(GameSoccerStatusViewModel)
    }

    struct ExpectedGoals {
        init(firstTeam: String?, secondTeam: String?) {
            self.firstTeam = firstTeam ?? .gameStatPlaceholder
            self.secondTeam = secondTeam ?? .gameStatPlaceholder
        }

        let firstTeam: String
        let secondTeam: String
    }

    final class TeamViewModel: ObservableObject {
        enum ScoreFooter {
            case indicatorLine(IndicatorLineViewModel)
        }

        var id: String?
        var logoUrl: URL?
        var title: String?
        var displayTitle: String?
        @Published var isTitleIndicatorVisible = false
        @Published var subtitle: String?
        @Published var subtitleImage: UIImage?
        @Published var score: String?
        @Published var scoreFooter: ScoreFooter?
        @Published var scoreColor: Color = Constants.defaultScoreColor
        @Published var ranking: String?
        @Published var form: TeamFormChartViewModel?

        var followingEntity: FollowingEntity? {
            id.flatMap { id in
                AppEnvironment.shared.following.followable(types: [.team])
                    .first { $0.gqlId == id }
            }
        }
    }

    weak var delegate: GameLargeScoreHeaderViewModelDelegate?

    private(set) var navigationTitle: String?
    private(set) var title: String?

    let firstTeam: TeamViewModel
    let secondTeam: TeamViewModel
    var showsBottomRow: Bool {
        firstTeam.form != nil || secondTeam.form != nil || expectedGoals != nil
    }
    var layoutInfo: (hasIndicator: Bool, hasRanking: Bool) {
        (
            firstTeam.isTitleIndicatorVisible || secondTeam.isTitleIndicatorVisible,
            firstTeam.ranking != nil || secondTeam.ranking != nil
        )
    }

    @Published private(set) var layoutMode: LayoutMode = .largeIcon
    @Published private(set) var centerMode: CenterMode?
    @Published private(set) var gameDescription: String?
    @Published private(set) var expectedGoals: ExpectedGoals?

    init(
        navigationTitle: String? = nil,
        title: String? = nil,
        layoutMode: LayoutMode = .largeIcon,
        centerMode: CenterMode? = nil
    ) {
        self.navigationTitle = navigationTitle
        self.title = title
        self.firstTeam = TeamViewModel()
        self.secondTeam = TeamViewModel()
        self.layoutMode = layoutMode
        self.centerMode = centerMode
    }

    /// Light update function, used to populate the initial data before a full data object is retrieved.
    func update(with model: ScheduledGame) {
        onMain {
            self.update(with: model, entity: nil)
        }
    }

    /// Fully updates the view model attributes.
    func update(with entity: GQL.GameContainer) {
        onMain {
            let model = ScheduledGame(entity: entity.fragments.gameV2Lite)
            self.update(with: model, entity: entity)
        }
    }

    func trackTapEvent(for entity: FollowingEntity) {
        delegate?.viewModel(self, needsToTrackSelectedEntity: entity)
    }

    private func update(with model: ScheduledGame, entity: GQL.GameContainer?) {
        navigationTitle = [
            model.firstTeam?.shortName,
            model.secondTeam?.shortName,
        ]
        .compactMap { $0 }
        .joined(separator: model.gameIdentifier.isSoccer ? " v " : " @ ")

        updateGeneralTeamInfo(in: firstTeam, team: model.firstTeam)
        updateGeneralTeamInfo(in: secondTeam, team: model.secondTeam)
        updateTeamSubtitles(with: model, entity: entity)

        centerMode = .init(model: model, entity: entity)

        switch GamePhase(gameStatus: model.status, startedAt: model.startedAt) {
        case .preGame, .nonStarter, nil:
            layoutMode = .largeIcon
            firstTeam.score = nil
            secondTeam.score = nil

        case .inGame:
            layoutMode = .smallIcon
            firstTeam.score = model.firstTeam?.score
            secondTeam.score = model.secondTeam?.score

        case .postGame:
            layoutMode = .smallIcon
            firstTeam.score = model.firstTeam?.score
            secondTeam.score = model.secondTeam?.score

            let outcomes = model.teamOutcomes

            firstTeam.scoreColor =
                outcomes.first == .loss
                ? Constants.dimmedScoreColor
                : Constants.defaultScoreColor

            secondTeam.scoreColor =
                outcomes.second == .loss
                ? Constants.dimmedScoreColor
                : Constants.defaultScoreColor
        }

        guard let entity = entity else { return }

        updateTitle(entity: entity)

        if let game = entity.asAmericanFootballGame {
            update(with: entity.fragments.gameV2Lite, americanFootballGame: game)
        } else if let game = entity.asBasketballGame {
            update(with: entity.fragments.gameV2Lite, basketballGame: game)
        } else if let game = entity.asHockeyGame {
            update(with: entity.fragments.gameV2Lite, hockeyGame: game)
        } else if let game = entity.asSoccerGame {
            update(with: game)
        }
    }

    private func updateTitle(entity: GQL.GameContainer) {
        var titleParts: [String] = []
        if let prefix = entity.asSoccerGame?.league.displayName {
            titleParts.append(prefix)
        }

        if let gameTitle = entity.gameTitle {
            titleParts.append(gameTitle)
        }

        guard !titleParts.isEmpty else {
            return
        }

        title = titleParts.joined(separator: ", ")
    }

    /// Updates with American football attributes.
    private func update(
        with game: GQL.GameV2Lite,
        americanFootballGame: GQL.GameContainer.AsAmericanFootballGame
    ) {
        guard let status = game.status.map({ ScheduledGame.Status(status: $0) }) else {
            firstTeam.isTitleIndicatorVisible = false
            secondTeam.isTitleIndicatorVisible = false
            return
        }

        switch GamePhase(gameStatus: status, startedAt: game.startedAt) {
        case .inGame:
            let possession = americanFootballGame.possession?.fragments.americanFootballPossession

            if let possessingId = possession?.team?.fragments.teamV2.id {
                let isFirstTeamPossessing =
                    possessingId == game.firstTeam?.team?.fragments.teamV2Lite.id
                firstTeam.isTitleIndicatorVisible = isFirstTeamPossessing
                secondTeam.isTitleIndicatorVisible = !isFirstTeamPossessing
            } else {
                firstTeam.isTitleIndicatorVisible = false
                secondTeam.isTitleIndicatorVisible = false
            }

            let timeouts = americanFootballGame.fragments.americanFootballTimeouts

            firstTeam.scoreFooter =
                timeouts.awayTeam?.fragments.americanFootballTimeoutsTeam?
                .makeScoreFooter(gameId: game.id)
            secondTeam.scoreFooter =
                timeouts.homeTeam?.fragments.americanFootballTimeoutsTeam?
                .makeScoreFooter(gameId: game.id)

        case .preGame, .postGame, .nonStarter, nil:
            firstTeam.isTitleIndicatorVisible = false
            firstTeam.scoreFooter = nil
            secondTeam.isTitleIndicatorVisible = false
            secondTeam.scoreFooter = nil
        }
    }

    /// Updates with basketball attributes.
    private func update(
        with game: GQL.GameV2Lite,
        basketballGame: GQL.GameContainer.AsBasketballGame
    ) {
        switch GamePhase(statusCode: game.status, startedAt: game.startedAt) {
        case .inGame:
            let timeouts = basketballGame.fragments.basketballTimeouts

            firstTeam.scoreFooter =
                timeouts.awayTeam?.fragments.basketballTimeoutsTeam?
                .makeScoreFooter(gameId: game.id)
            secondTeam.scoreFooter =
                timeouts.homeTeam?.fragments.basketballTimeoutsTeam?
                .makeScoreFooter(gameId: game.id)

        case .preGame, .postGame, .nonStarter, nil:
            firstTeam.scoreFooter = nil
            secondTeam.scoreFooter = nil
        }
    }

    /// Updates with hockey attributes.
    private func update(
        with game: GQL.GameV2Lite,
        hockeyGame: GQL.GameContainer.AsHockeyGame
    ) {
        guard GamePhase(statusCode: game.status, startedAt: game.startedAt) == .inGame else {
            firstTeam.subtitleImage = nil
            secondTeam.subtitleImage = nil
            return
        }

        let powerPlay = hockeyGame.fragments.hockeyPowerPlay
        let homeTeam = powerPlay.homeTeam?.fragments.hockeyPowerPlayTeam
        let awayTeam = powerPlay.awayTeam?.fragments.hockeyPowerPlayTeam

        if homeTeam?.strength == .powerplay {
            firstTeam.subtitleImage = nil
            secondTeam.subtitleImage = #imageLiteral(resourceName: "power_play_indicator")
        } else if awayTeam?.strength == .powerplay {
            firstTeam.subtitleImage = #imageLiteral(resourceName: "power_play_indicator")
            secondTeam.subtitleImage = nil
        } else {
            firstTeam.subtitleImage = nil
            secondTeam.subtitleImage = nil
        }
    }

    private func update(with soccerGame: GQL.GameContainer.AsSoccerGame) {
        gameDescription = makeSoccerGameDescription(soccerGame: soccerGame)
        updateBottomRow(with: soccerGame)
    }

    private func updateGeneralTeamInfo(in teamViewModel: TeamViewModel, team: ScheduledGame.Team?) {
        teamViewModel.id = team?.id
        teamViewModel.logoUrl = team?.logo
        teamViewModel.title = team?.shortName ?? AthleticScoresFoundation.Strings.tbd.localized
        teamViewModel.displayTitle = team?.displayName
        teamViewModel.ranking = team?.ranking?.string
    }

    private func updateTeamSubtitles(with model: ScheduledGame, entity: GQL.GameContainer?) {
        guard let gameLite = entity?.fragments.gameV2Lite else {
            firstTeam.subtitle = nil
            secondTeam.subtitle = nil
            return
        }

        switch gameLite.sport {
        case .baseball, .americanFootball, .hockey, .basketball:
            firstTeam.subtitle = model.firstTeam?.currentRecord
            secondTeam.subtitle = model.secondTeam?.currentRecord
        case .soccer:
            guard
                let soccerGame = entity?.asSoccerGame,
                let status = soccerGame.status.map({ ScheduledGame.Status(status: $0) }),
                let gamePhase = GamePhase(gameStatus: status, startedAt: model.startedAt),
                gamePhase != .inGame,
                GQL.LeagueCode.regularSeasonSoccerLeagues.contains(soccerGame.league.id)
            else {
                firstTeam.subtitle = nil
                secondTeam.subtitle = nil
                return
            }

            firstTeam.subtitle = soccerGame.firstTeam?.currentStanding.flatMap({
                $0.isEmpty ? nil : $0
            })
            secondTeam.subtitle = soccerGame.secondTeam?.currentStanding.flatMap({
                $0.isEmpty ? nil : $0
            })
        default:
            firstTeam.subtitle = nil
            secondTeam.subtitle = nil
            return
        }
    }

    private func updateBottomRow(with soccerGame: GQL.GameContainer.AsSoccerGame) {
        guard
            GamePhase(statusCode: soccerGame.status, startedAt: soccerGame.startedAt) == .preGame
        else {
            firstTeam.form = nil
            secondTeam.form = nil
            expectedGoals = nil
            return
        }

        updateTeamForm(
            leagueCode: soccerGame.league.id,
            firstTeamForm: soccerGame.firstTeam?.lastSix,
            secondTeamForm: soccerGame.secondTeam?.lastSix
        )

        updateTeamExpectedGoals(
            firstTeamExpectedGoals: soccerGame.firstTeam?.expectedGoals?.stringValue,
            secondTeamExpectedGoals: soccerGame.secondTeam?.expectedGoals?.stringValue
        )
    }

    private func updateTeamForm(
        leagueCode: GQL.LeagueCode,
        firstTeamForm: String?,
        secondTeamForm: String?
    ) {
        guard GQL.LeagueCode.regularSeasonSoccerLeagues.contains(leagueCode) else {
            firstTeam.form = nil
            secondTeam.form = nil
            return
        }

        if firstTeamForm != nil || secondTeamForm != nil {
            firstTeam.form = TeamFormChartViewModel(form: firstTeamForm)
            secondTeam.form = TeamFormChartViewModel(form: secondTeamForm)
        } else {
            firstTeam.form = nil
            secondTeam.form = nil
        }
    }

    func updateTeamExpectedGoals(
        firstTeamExpectedGoals: String?,
        secondTeamExpectedGoals: String?
    ) {
        if firstTeamExpectedGoals != nil || secondTeamExpectedGoals != nil {
            expectedGoals = ExpectedGoals(
                firstTeam: firstTeamExpectedGoals,
                secondTeam: secondTeamExpectedGoals
            )
        } else {
            expectedGoals = nil
        }
    }

    private func makeSoccerGameDescription(soccerGame: GQL.GameContainer.AsSoccerGame) -> String? {
        guard
            GamePhase(statusCode: soccerGame.status, startedAt: soccerGame.startedAt) == .postGame
        else {
            return nil
        }

        if let aggregateWinner = soccerGame.aggregateWinner?.displayName {
            return String(format: Strings.teamWinsOnAggregateFormat.localized, aggregateWinner)

        } else if let firstTeam = soccerGame.firstTeam,
            let secondTeam = soccerGame.secondTeam,
            let firstPenaltyScore = firstTeam.penaltyScore,
            let secondPenaltyScore = secondTeam.penaltyScore,
            let firstTeamName = firstTeam.team?.displayName,
            let secondTeamName = secondTeam.team?.displayName
        {
            if firstPenaltyScore > secondPenaltyScore {
                return String(
                    format: Strings.teamWinsOnPenaltiesFormat.localized,
                    firstTeamName,
                    firstPenaltyScore,
                    secondPenaltyScore
                )
            } else if secondPenaltyScore > firstPenaltyScore {
                return String(
                    format: Strings.teamWinsOnPenaltiesFormat.localized,
                    secondTeamName,
                    secondPenaltyScore,
                    firstPenaltyScore
                )
            } else {
                assertionFailure("Post game state with equal penalty shootout score?")
                return nil
            }
        } else {
            return nil
        }
    }
}

extension GameLargeScoreHeaderViewModel.CenterMode {
    private struct Constants {
        static let preGameTitleFont: AthleticFont.Style = .calibreHeadline.s.medium
        static let preGameSubtitleFont: AthleticFont.Style = .calibreUtility.xs.regular

        static let normalTitleFont: AthleticFont.Style = .calibreUtility.l.medium
        static let soccerTitleFont: AthleticFont.Style = .calibreHeadline.s.medium
        static let normalSubtitleFont: AthleticFont.Style = .calibreUtility.s.regular
    }

    fileprivate init(model: ScheduledGame, entity: GQL.GameContainer?) {
        switch GamePhase(gameStatus: model.status, startedAt: model.startedAt) {
        case .preGame, .nonStarter, nil:
            let broadcastSubtitle = entity?.fragments.gameV2BroadcastNetwork.broadcastNetwork.map {
                GameStackedTitlesViewModel.Title(text: $0, style: Constants.preGameSubtitleFont)
            }

            let subtitle1: GameStackedTitlesViewModel.Title?
            let subtitle2: GameStackedTitlesViewModel.Title?

            if model.gameIdentifier.sportType == .soccer,
                let soccerSubtitle = Self.soccerSubtitle(entity: entity)
            {
                subtitle1 = .init(text: soccerSubtitle, style: Constants.normalSubtitleFont)
                subtitle2 = broadcastSubtitle
            } else {
                subtitle1 = broadcastSubtitle
                subtitle2 = nil
            }

            self = .normal(
                .init(
                    pretitle: model.scheduledAt.map {
                        GameStackedTitlesViewModel.Title(text: $0.feedFormattedDate)
                    },
                    title:
                        model.preGameStatusDisplayTitle(
                            dateFormat: .timeWithMeridiem
                        ).map { title in
                            GameStackedTitlesViewModel.Title(
                                text: title,
                                style: Constants.preGameTitleFont
                            )
                        },
                    subtitle1: subtitle1,
                    subtitle2: subtitle2
                )
            )

        case .inGame:
            let isDelayed = model.status == .delayed

            if model.gameIdentifier.sportType == .baseball {
                self = .live(
                    .mlb(
                        entity?.asBaseballGame.map { GameMLBStatusViewModel(entity: $0) } ?? .empty
                    )
                )
            } else if model.gameIdentifier.sportType == .soccer {
                self = .live(
                    .soccer(
                        GameSoccerStatusViewModel(
                            isDelayed: isDelayed,
                            gameClockText: entity?.gameStatus?.fragments.gameStatusDisplay.main,
                            bottomText: Self.soccerSubtitle(entity: entity)
                        )
                    )
                )
            } else {
                self = .live(
                    .default(
                        GameGeneralSportStatusViewModel(
                            isDelayed: isDelayed,
                            gameStatusDisplayMain: entity?.gameStatus?.fragments.gameStatusDisplay
                                .main,
                            gameClockText: entity?.gameStatus?.fragments.gameStatusDisplay.extra
                        )
                    )

                )
            }

        case .postGame:
            if model.gameIdentifier.sportType == .soccer {
                self = .normal(
                    .init(
                        pretitle: model.scheduledAt.map {
                            GameStackedTitlesViewModel.Title(text: $0.feedFormattedDate)
                        },
                        title: model.detail.map { status in
                            GameStackedTitlesViewModel.Title(
                                text: status.uppercased(),
                                style: Constants.soccerTitleFont
                            )
                        },
                        subtitle1: Self.soccerSubtitle(entity: entity).map {
                            GameStackedTitlesViewModel.Title(
                                text: $0,
                                style: Constants.normalSubtitleFont
                            )
                        },
                        subtitle2: nil
                    )
                )
            } else {
                self = .normal(
                    .init(
                        pretitle:
                            model.scheduledAt.map {
                                GameStackedTitlesViewModel.Title(text: $0.feedFormattedDate)
                            },
                        title: model.detail.map { status in
                            GameStackedTitlesViewModel.Title(
                                text: status.uppercased(),
                                style: Constants.normalTitleFont
                            )
                        },
                        subtitle1: nil,
                        subtitle2: nil
                    )
                )
            }
        }
    }

    fileprivate static func soccerSubtitle(entity: GQL.GameContainer?) -> String? {

        /// If there's an aggregate score, show it but only if the game is the Leg 2 game.
        /// The Leg 1 game also contains the same final aggregate score but we don't want that to appear on Leg 1.
        guard
            let soccerGame = entity?.asSoccerGame,
            let overallAggregateScore = soccerGame.aggregateScore,
            let thisGameDate = soccerGame.scheduledAt,
            let relatedGameDate = soccerGame.relatedGame?.scheduledAt
        else {
            return nil
        }

        if thisGameDate > relatedGameDate {
            return overallAggregateScore
        } else {
            return nil
        }
    }
}

extension GameMLBStatusViewModel {
    fileprivate init(entity: GQL.GameContainer.AsBaseballGame) {
        guard let outcome = entity.outcome else {
            self = .empty
            return
        }

        let inningText: String?
        let bottomText: String?
        let basesHighlighting: BaseballBasesDiamond.Highlighting

        if let inningHalf = entity.inningHalf, let inning = entity.inning {
            inningText = inningHalf.shortTitle(forInning: inning)?.uppercased()
        } else {
            inningText = nil
        }

        switch entity.inningHalf {
        case .top, .bottom:
            basesHighlighting = BaseballBasesDiamond.Highlighting(
                endingBases: outcome.runners.map { $0.endingBase }
            )

            let outsText = "\(outcome.outs ?? 0) \(Strings.mlbOutTitle.localized.uppercased())"
            let ballsStrikesText: String

            if outcome.balls == 4 {
                ballsStrikesText = Strings.mlbWalkAbbreviation.localized
            } else if outcome.strikes == 3 {
                ballsStrikesText = Strings.mlbStrikeoutAbbreviation.localized
            } else {
                ballsStrikesText = "\(outcome.balls ?? 0)-\(outcome.strikes ?? 0)"
            }

            bottomText = [ballsStrikesText, outsText].joined(separator: ", ")

        case .over:
            basesHighlighting = .none
            bottomText =
                "\(0)-\(0), \(outcome.outs ?? 0) \(Strings.mlbOutTitle.localized.uppercased())"
        case .middle, .__unknown, nil:
            basesHighlighting = .none
            bottomText = nil
        }

        self.init(
            isDelayed: entity.status == .delayed,
            gameInfo: GameInfo(
                inningText: inningText,
                basesHighlighting: basesHighlighting,
                bottomText: bottomText
            )
        )
    }
}

extension GQL.AmericanFootballTimeoutsTeam {
    fileprivate func makeScoreFooter(
        gameId: String
    ) -> GameLargeScoreHeaderViewModel.TeamViewModel.ScoreFooter? {
        IndicatorLineViewModel(gameId: gameId, team: self).map { .indicatorLine($0) }
    }
}

extension GQL.BasketballTimeoutsTeam {
    fileprivate func makeScoreFooter(
        gameId: String
    ) -> GameLargeScoreHeaderViewModel.TeamViewModel.ScoreFooter? {
        IndicatorLineViewModel(gameId: gameId, team: self).map { .indicatorLine($0) }
    }
}

extension GQL.GameContainer.AsSoccerGame {
    fileprivate var aggregateScore: String? {
        let aggregateScores = [
            homeTeam?.fragments.soccerGameContainerGameTeam?.aggregateScore,
            awayTeam?.fragments.soccerGameContainerGameTeam?.aggregateScore,
        ]
        .compactMap { $0 }

        if aggregateScores.count == 2 {
            return String(
                format: Strings.aggregateScoreAbbreviationFormat.localized,
                aggregateScores[0],
                aggregateScores[1]
            )
        } else {
            return nil
        }
    }
}

extension GQL.LeagueCode {

    /// Leagues that are regular season leagues with standings. e.g. not knockout based competitions.
    static var regularSeasonSoccerLeagues: Set<GQL.LeagueCode> {
        [
            .mls,
            .nws,
            .epl,
            .pre,
            .cha,
            .leo,
            .let,
            .prd,
        ]
    }

}
