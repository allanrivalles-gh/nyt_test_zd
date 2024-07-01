//
//  ScoresScheduleGameViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 27/2/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticScoresFoundation
import AthleticUI
import Foundation

public final class ScheduleGameViewModel: Identifiable, ObservableObject, Analytical {
    public struct Team: Equatable {
        public struct ScoreInfo: Equatable {
            public let score: String?
            public let penaltyScore: String?
            public let isPointerShowing: Bool

            public init(score: String?, penaltyScore: String?, isPointerShowing: Bool) {
                self.score = score
                self.penaltyScore = penaltyScore
                self.isPointerShowing = isPointerShowing
            }
        }

        public enum TrailingInfo: Equatable {
            case plainText(String)
            case score(ScoreInfo)
        }

        public enum Icon {
            case americanFootballPossession
            case redCard
        }

        public enum TextVisibility {
            case full
            case lightlyDimmed
            case veryDimmed
        }

        public let logos: [ATHImageResource]
        public let name: String
        public let icons: [Icon]
        public let isLogoDimmed: Bool
        public let textVisibility: TextVisibility
        public let ranking: String?
        public let trailingInfo: TrailingInfo?
        public let isToBeDetermined: Bool

        public init(
            logos: [ATHImageResource],
            name: String,
            icons: [Icon],
            isLogoDimmed: Bool,
            textVisibility: TextVisibility,
            ranking: String?,
            trailingInfo: TrailingInfo?,
            isToBeDetermined: Bool
        ) {
            self.logos = logos
            self.name = name
            self.icons = icons
            self.isLogoDimmed = isLogoDimmed
            self.textVisibility = textVisibility
            self.ranking = ranking
            self.trailingInfo = trailingInfo
            self.isToBeDetermined = isToBeDetermined
        }
    }

    public struct GameInfo: Equatable {
        public struct Text: Equatable {
            public enum Style {
                case `default`
                case live
                case whiteBold
                case whiteNormal
            }

            public let string: String
            public let style: Style

            public init(string: String, style: Style) {
                self.string = string
                self.style = style
            }
        }

        public enum Widget: Equatable {
            case baseballBases(highlighted: BaseballBasesDiamond.Highlighting)
        }

        public let text: [Text]
        public let widget: Widget?

        public init(text: [Text], widget: Widget?) {
            self.text = text
            self.widget = widget
        }
    }

    /// A helper struct to share the processing logic between the main `init` and `update(with:)` for things that change between subscription updates
    private struct Processed {
        let contextMenuActions: GameScoreContextMenuActionsProvider

        let header: String?
        let footer: String?

        let topTeam: Team
        let bottomTeam: Team

        let gameInfo: GameInfo
        let discussionLinkText: String?
        let needsUpdates: Bool

        let clickAnalyticsElement: AnalyticsEvent.Element?

        init(model: GQL.ScoresFeedBlock) {
            header = model.header
            footer = model.footer

            let gameBlock = model.gameBlock.fragments.scoresFeedGameBlock
            let team1 = gameBlock.team1.fragments.scoresFeedTeamBlock
            let team2 = gameBlock.team2.fragments.scoresFeedTeamBlock
            topTeam = Team(model: team1, otherTeamModel: team2)
            bottomTeam = Team(model: team2, otherTeamModel: team1)
            gameInfo = GameInfo(model: model.infoBlock.fragments.scoresFeedInfoBlock)
            discussionLinkText =
                model.widget?.asScoresFeedDiscussionWidgetBlock?.fragments
                .scoresFeedDiscussionWidgetBlock.text
            needsUpdates = model.willUpdate
            contextMenuActions = GameScoreContextMenuActionsProvider(
                gameId: model.gameId,
                status: GamePhase(
                    gameState: model.gameBlock.fragments.scoresFeedGameBlock.gameState
                )
            )

            clickAnalyticsElement = {
                switch gameBlock.gameState {
                case .pre:
                    return .preGameBoxScoreGame
                case .live:
                    return .inGameBoxScoreGame
                case .post:
                    return .postGameBoxScoreGame
                case .__unknown(let rawValue):
                    assertionFailure("Unhandled game state: \(rawValue)")
                    return nil
                }
            }()
        }
    }

    public let id: AnyHashable
    public let blockId: String
    public let gameId: String

    let impressionManager: AnalyticImpressionManager
    let impressionRecord: AnalyticsImpressionRecord?
    public var clickAnalyticsElement: AnalyticsEvent.Element?
    public var clickAnalyticsIndexV: Int?

    var contextMenuActions: GameScoreContextMenuActionsProvider

    @Published private(set) var header: String?
    @Published private(set) var footer: String?

    @Published private(set) var topTeam: Team
    @Published private(set) var bottomTeam: Team

    @Published private(set) var gameInfo: GameInfo
    @Published private(set) var discussionLinkText: String?

    public var needsUpdates: Bool

    public var isTappable: Bool {
        !topTeam.isToBeDetermined && !bottomTeam.isToBeDetermined
    }

    public var analyticData: AnalyticData {
        AnalyticData(impress: impressionRecord)
    }

    public init(
        id: AnyHashable,
        blockId: String,
        gameId: String,
        impressionManager: AnalyticImpressionManager,
        impressionRecord: AnalyticsImpressionRecord?,
        clickAnalyticsElement: AnalyticsEvent.Element?,
        clickAnalyticsIndexV: Int?,
        header: String?,
        footer: String?,
        topTeam: Team,
        bottomTeam: Team,
        gameInfo: GameInfo,
        discussionLinkText: String?,
        needsUpdates: Bool,
        contextMenuActions: GameScoreContextMenuActionsProvider
    ) {
        self.id = id
        self.blockId = blockId
        self.gameId = gameId
        self.impressionManager = impressionManager
        self.impressionRecord = impressionRecord
        self.clickAnalyticsElement = clickAnalyticsElement
        self.clickAnalyticsIndexV = clickAnalyticsIndexV
        self.header = header
        self.footer = footer
        self.topTeam = topTeam
        self.bottomTeam = bottomTeam
        self.gameInfo = gameInfo
        self.discussionLinkText = discussionLinkText
        self.needsUpdates = needsUpdates
        self.contextMenuActions = contextMenuActions
    }

    public convenience init(
        model: GQL.ScoresFeedBlock,
        impressionManager: AnalyticImpressionManager,
        impressionRecord: AnalyticsImpressionRecord?,
        clickAnalyticsIndexV: Int?
    ) {
        let processed = Processed(model: model)
        self.init(
            id: model.id,
            blockId: model.id,
            gameId: model.gameId,
            impressionManager: impressionManager,
            impressionRecord: impressionRecord,
            clickAnalyticsElement: processed.clickAnalyticsElement,
            clickAnalyticsIndexV: clickAnalyticsIndexV,
            header: processed.header,
            footer: processed.footer,
            topTeam: processed.topTeam,
            bottomTeam: processed.bottomTeam,
            gameInfo: processed.gameInfo,
            discussionLinkText: processed.discussionLinkText,
            needsUpdates: processed.needsUpdates,
            contextMenuActions: processed.contextMenuActions
        )
    }

    public func update(with model: GQL.ScoresFeedBlock) {
        let processed = Processed(model: model)
        header = processed.header
        footer = processed.footer
        topTeam = processed.topTeam
        bottomTeam = processed.bottomTeam
        gameInfo = processed.gameInfo
        discussionLinkText = processed.discussionLinkText
        needsUpdates = processed.needsUpdates
        contextMenuActions = processed.contextMenuActions
        clickAnalyticsElement = processed.clickAnalyticsElement
    }

    public func trackClickDiscussionCta() {
        Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .scores,
                element: .boxScoreDiscuss,
                objectType: .gameId,
                objectIdentifier: gameId,
                requiredValues: ScoresEnvironment.shared.makeAnalyticsDefaults()
            )
        )
    }
}

extension ScheduleGameViewModel.Team {

    public init(model: GQL.ScoresFeedTeamBlock, otherTeamModel: GQL.ScoresFeedTeamBlock) {
        logos = model.logos.map { ATHImageResource(entity: $0.fragments.teamLogo) }
        name = model.name

        if let info = model.teamInfo {
            if let info = info.asScoresFeedTeamPregameInfoBlock {
                trailingInfo = .plainText(info.text)

            } else if let info = info.asScoresFeedTeamGameInfoBlock {
                trailingInfo = .score(
                    ScoreInfo(
                        score: info.score,
                        penaltyScore: info.penaltyScore,
                        isPointerShowing: info.isWinner == true
                    )
                )
            } else {
                trailingInfo = nil
            }
        } else {
            trailingInfo = nil
        }

        let otherIsWinner = otherTeamModel.teamInfo?.asScoresFeedTeamGameInfoBlock?.isWinner == true
        isLogoDimmed = otherIsWinner && !model.isToBeDetermined
        textVisibility = {
            if model.isToBeDetermined {
                return .lightlyDimmed
            } else if otherIsWinner {
                return .veryDimmed
            } else {
                return .full
            }
        }()
        ranking = model.ranking?.string

        icons = model.icons.compactMap { icon in
            switch icon {
            case .soccerRedcard: return .redCard
            case .americanFootballPossession: return .americanFootballPossession
            case .__unknown: return nil
            }
        }
        isToBeDetermined = model.isToBeDetermined
    }

    public init(
        team: ScheduledGame.Team?,
        sport: SportType,
        gamePhase: GamePhase?,
        outcome: ScheduledGame.GameOutcome?
    ) {
        logos = team?.logoSmall.map { [ATHImageResource(url: $0)] } ?? []
        name =
            (team?.displayName?.isEmpty == false
                ? team?.displayName
                : team?.shortName) ?? AthleticScoresFoundation.Strings.tbd.localized

        switch gamePhase {
        case .preGame, .nonStarter, nil:
            if sport == .soccer {
                trailingInfo = team?.currentStanding.map { .plainText($0) }
            } else {
                trailingInfo = team?.currentRecord.map { .plainText($0) }
            }

        case .inGame, .postGame:
            trailingInfo = .score(
                ScoreInfo(
                    score: team?.score,
                    penaltyScore: team?.penaltyScore.map { "(\($0))" },
                    isPointerShowing: outcome == .win
                )
            )
        }

        isLogoDimmed = outcome == .loss && team != nil
        textVisibility = {
            if team != nil {
                if outcome == .loss {
                    return .veryDimmed
                } else {
                    return .full
                }
            } else {
                return .lightlyDimmed
            }
        }()
        ranking = team?.ranking?.string

        if team?.hasAmericanFootballPossession == true {
            icons = [.americanFootballPossession]
        } else {
            icons = []
        }
        isToBeDetermined = team == nil
    }
}

extension ScheduleGameViewModel.GameInfo {

    public init(model: GQL.ScoresFeedInfoBlock, timeSettings: TimeSettings = SystemTimeSettings()) {

        text = model.text.compactMap {
            Text(model: $0)
        }

        if let baseballWidget = model.widget?.asScoresFeedBaseballWidgetBlock {
            let fragment = baseballWidget.fragments.scoresFeedBaseballWidgetBlock
            widget = .baseballBases(
                highlighted: Self.highlightedBases(loaded: fragment.loadedBases)
            )
        } else {
            widget = nil
        }
    }

    private static func highlightedBases(loaded: [Int]) -> BaseballBasesDiamond.Highlighting {
        var highlighted: BaseballBasesDiamond.Highlighting = []
        for base in loaded {
            switch base {
            case 1:
                highlighted.insert(.right)
            case 2:
                highlighted.insert(.middle)
            case 3:
                highlighted.insert(.left)
            default:
                break
            }
        }
        return highlighted
    }
}

extension ScheduleGameViewModel.GameInfo.Text {

    public init?(
        model: GQL.ScoresFeedInfoBlock.Text,
        timeSettings: TimeSettings = SystemTimeSettings()
    ) {
        if let standardText = model.asScoresFeedStandardTextBlock?.fragments
            .scoresFeedStandardTextBlock
        {
            string = standardText.text
            style = standardText.type.style

        } else if let text = model.asScoresFeedDateTimeTextBlock?.fragments
            .scoresFeedDateTimeTextBlock
        {
            switch text.format {
            case .date:
                string = text.timestamp.dateString(format: .date)
            case .datetime, .__unknown:
                string = Self.dateTimeString(
                    from: text.timestamp,
                    isTimeToBeDetermined: text.isTimeToBeDetermined,
                    timeSettings: timeSettings
                )
            case .time:
                string = text.timestamp.dateString(
                    format: .timeWithMeridiem,
                    isTimeToBeDetermined: text.isTimeToBeDetermined
                )
            }
            style = text.type.style

        } else if let oddsText = model.asScoresFeedOddsTextBlock?.fragments
            .scoresFeedOddsTextBlock
        {
            let oddsString: String
            if timeSettings.calendar.locale?.isNorthAmerica == false {
                oddsString = oddsText.odds.fractionOdds
            } else {
                oddsString = oddsText.odds.usOdds
            }

            string = oddsString
            style = oddsText.type.style
        } else {
            assertionFailure(
                "Encountered an unhandled text type, this won't be shown until implemented \(model)"
            )
            return nil
        }
    }

    private static func dateTimeString(
        from date: Date,
        isTimeToBeDetermined: Bool,
        timeSettings: TimeSettings
    ) -> String {
        /// Same day, just show the time
        if date.isSame(timeSettings.now(), granularity: .day) {
            return date.dateString(
                format: .timeWithMeridiem,
                isTimeToBeDetermined: isTimeToBeDetermined
            )

            /// Time TBD, just show the date
        } else if isTimeToBeDetermined {
            return date.dateString(format: .date)

            /// Show date and time
        } else {
            let dateString = date.dateString(format: .date)
            let timeString = date.dateString(
                format: .timeWithMeridiem,
                isTimeToBeDetermined: isTimeToBeDetermined
            )
            return dateString + "\n" + timeString
        }
    }
}

extension GQL.ScoresFeedTextType {

    var style: ScheduleGameViewModel.GameInfo.Text.Style {
        switch self {
        case .status, .datetime:
            return .whiteBold
        case .situation:
            return .whiteNormal
        case .live:
            return .live
        case .default, .__unknown:
            return .default
        }
    }

}

extension Date {

    fileprivate enum DateFormat {
        case date
        case timeWithMeridiem
        case time
    }

    fileprivate func dateString(
        format: DateFormat,
        isTimeToBeDetermined: Bool? = nil
    ) -> String {
        switch format {
        case .date:
            return dayOfWeekDayMonthFormatted

        case .timeWithMeridiem where isTimeToBeDetermined == true:
            return AthleticScoresFoundation.Strings.tbd.localized

        case .time where isTimeToBeDetermined == true:
            return AthleticScoresFoundation.Strings.tbd.localized

        case .timeWithMeridiem:
            return Date.timeWithSpaceFormatter.string(from: self)

        case .time:
            return Date.hourDateFormatter.string(from: self)
        }
    }
}
