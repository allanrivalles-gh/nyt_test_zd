//
//  BracketSeriesView.swift
//
//
//  Created by Jason Xu on 3/31/23.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticScoresFoundation
import AthleticUI
import SwiftUI

public struct BracketSeries: View {
    /// we need to identify the tile by index to be able to get the most up to date info from the view model
    /// if we just copied the value it wouldn't change when a new subscription update arrives
    public struct TileIndex {
        public let round: Int
        public let tile: Int

        public init(round: Int, tile: Int) {
            self.round = round
            self.tile = tile
        }
    }

    @ObservedObject private var viewModel: LeagueBracketViewModel
    private let tileIndex: TileIndex
    private let onTapGame: (TournamentTile.Game) -> Void
    private let onTapTeam: (TournamentTile.TeamData) -> Void
    private let tbdString: String

    public init(
        viewModel: LeagueBracketViewModel,
        tileIndex: TileIndex,
        onTapGame: @escaping (TournamentTile.Game) -> Void,
        onTapTeam: @escaping (TournamentTile.TeamData) -> Void,
        tbdString: String
    ) {
        self.viewModel = viewModel
        self.tileIndex = tileIndex
        self.onTapGame = onTapGame
        self.onTapTeam = onTapTeam
        self.tbdString = tbdString
    }

    public var body: some View {
        if let round = viewModel.rounds?[tileIndex.round] {
            let roundTile = round.tiles[tileIndex.tile]
            if case .series(let series) = roundTile.tile.data {
                BracketSeriesView(
                    teams: Teams(
                        homeTeam: roundTile.tile.homeTeam,
                        awayTeam: roundTile.tile.awayTeam
                    ),
                    roundTab: round.tab,
                    series: series,
                    onTapGame: onTapGame,
                    onTapTeam: onTapTeam,
                    tbdString: tbdString
                )
            }
        }
    }
}

private struct BracketSeriesView: View {
    @Environment(\.dismiss) var dismiss

    let teams: Teams
    let roundTab: BracketTab
    let series: TournamentTile.Series
    let onTapGame: (TournamentTile.Game) -> Void
    let onTapTeam: (TournamentTile.TeamData) -> Void
    let tbdString: String

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 10) {
                BracketSeriesHeader(
                    teams: teams,
                    roundTab: roundTab,
                    series: series,
                    onTapTeam: onTapTeam,
                    tbdString: tbdString
                )
                VStack(alignment: .leading, spacing: 12) {
                    Text(Strings.seriesScheduleTitle.localized)
                        .fontName(.regularSlabBold, size: 14)
                        .foregroundColor(.chalk.dark800)
                    ForEach(indexed: series.games) { index, game in
                        if index != 0 { DividerView() }
                        BracketSeriesGameRow(
                            index: index,
                            game: game,
                            tbdString: tbdString
                        )
                        /// better hitbox
                        .contentShape(Rectangle())
                        .onTapGesture { onTapGame(game) }

                        if let ticketViewModel = game.ticketViewModel {
                            GameTicketPurchaseButton(
                                viewModel: ticketViewModel,
                                shouldShowLogo: false,
                                fontStyle: .calibreUtility.s.medium,
                                fontForegroundColor: .chalk.dark700,
                                chevronWidth: 5,
                                chevronHeight: 9,
                                chevronForegroundColor: .chalk.dark800,
                                verticalPadding: 0
                            )
                        }
                    }
                }
                .padding(.horizontal, 14)
                Spacer()
            }
            .overlay(alignment: .topTrailing) {
                if UIDevice.current.userInterfaceIdiom == .pad {
                    Button(action: { dismiss() }) {
                        Image(systemName: "xmark")
                            .padding(8)
                    }
                    .foregroundColor(.chalk.dark800)
                }
            }
        }
        .background(Color.chalk.dark200)
    }
}

private struct BracketSeriesHeader: View {
    let teams: Teams
    let roundTab: BracketTab
    let series: TournamentTile.Series
    let onTapTeam: (TournamentTile.TeamData) -> Void
    let tbdString: String

    var body: some View {
        ZStack {
            GeometryReader { proxy in
                FeedGameBackgroundView(
                    firstTeamColor: teams.firstTeam?.accentColor ?? .chalk.dark400,
                    secondTeamColor: teams.secondTeam?.accentColor ?? .chalk.dark400,
                    containerWidth: proxy.size.width
                )
            }
            HStack(alignment: .center, spacing: 10) {
                Group {
                    teamLogo(resources: teams.firstTeam?.logos)
                    Text(teams.firstTeam?.alias ?? tbdString)
                        .fontStyle(.calibreHeadline.s.medium)
                }
                .onTapGesture {
                    if case .confirmed(let data) = teams.firstTeam {
                        onTapTeam(data)
                    }
                }

                Spacer()

                VStack(alignment: .center, spacing: 0) {
                    Spacer()
                    Text(String(format: Strings.bestOfCount.localized, String(series.bestOfCount)))
                        .fontStyle(.calibreUtility.l.regular)
                        .foregroundColor(.chalk.dark700)
                    Text(victoriesCountText())
                        .fontStyle(.calibreUtility.xl.medium)
                        .foregroundColor(.chalk.dark700)
                    Spacer()
                }

                Spacer()

                Group {
                    Text(teams.secondTeam?.alias ?? tbdString)
                        .fontStyle(.calibreHeadline.s.medium)
                    teamLogo(resources: teams.secondTeam?.logos)
                }
                .onTapGesture {
                    if case .confirmed(let data) = teams.secondTeam {
                        onTapTeam(data)
                    }
                }
            }
            .foregroundColor(Color.chalk.dark700)
            .padding(.horizontal, 16)

            VStack(alignment: .center) {
                Text(roundTab.title)
                    .fontStyle(.calibreUtility.s.medium)
                    .foregroundColor(.chalk.dark500)
                    .padding(.top, 16)
                Spacer()
            }
        }
        .frame(height: 120)
        .padding(.top, 30)
    }

    private func victoriesCountText() -> String {
        let firstTeamVictoriesCount = teams.firstTeam?.victoriesCount(series: series) ?? 0
        let secondTeamVictoriesCount = teams.secondTeam?.victoriesCount(series: series) ?? 0
        return "\(firstTeamVictoriesCount) - \(secondTeamVictoriesCount)"
    }

    private func teamLogo(resources: [ATHImageResource]?) -> some View {
        TeamLogoLazyImage(
            size: 48,
            resources: resources ?? []
        )
    }
}

private struct BracketSeriesGameRow: View {
    let index: Int
    let game: TournamentTile.Game
    let tbdString: String

    var body: some View {
        GeometryReader { proxy in
            HStack(spacing: 0) {
                VStack(spacing: 6) {
                    teamScoreRowView(team: game.firstTeam)
                    teamScoreRowView(team: game.secondTeam)
                }
                .frame(width: proxy.size.width * 0.6, height: 24)
                DividerView(axis: .vertical)
                VStack(alignment: .leading, spacing: 2) {
                    Text(game.title(index: index))
                        .foregroundColor(game.isLive ? .chalk.red : .chalk.dark700)
                        .fontStyle(.calibreUtility.s.medium)
                    if let dateString = game.formattedDateString(tbdString: tbdString) {
                        Text(dateString)
                            .foregroundColor(.chalk.dark500)
                            .fontStyle(.calibreUtility.s.regular)
                    }
                }
                .frame(width: proxy.size.width * 0.4, height: 24, alignment: .leading)
                .padding(.leading, 15)
                Spacer()
            }
        }
        .frame(height: 54)
    }

    @ViewBuilder
    private func teamScoreRowView(
        team: TournamentTile.GameTeam?
    ) -> some View {
        let lost = team?.details?.lost(game: game)
        let foregroundColor: Color = lost == true ? .chalk.dark500 : .chalk.dark700
        let showScoreArrow = team?.details?.won(game: game) ?? false

        HStack(spacing: 8) {
            TeamLogoLazyImage(
                size: 24,
                resources: team?.details?.logos ?? []
            )
            Text(team?.details?.alias ?? tbdString)
                .fontStyle(.calibreUtility.xl.medium)
            Spacer()
            if game.phase != .preGame, let score = team?.scores?.score {
                Text(String(score))
                    .fontStyle(.calibreHeadline.s.medium)
                Image(systemName: "arrowtriangle.left.fill")
                    .resizable()
                    .frame(width: 9, height: 12)
                    .opacity(showScoreArrow ? 1 : 0)
            }
        }
        .foregroundColor(foregroundColor)
    }
}

private protocol TeamsProtocol {
    associatedtype Team

    var homeTeam: Team? { get }
    var awayTeam: Team? { get }
}

extension TeamsProtocol {
    /// this logic works for all the tournaments that we support at the moment
    var firstTeam: Team? { awayTeam }
    var secondTeam: Team? { homeTeam }
}

private struct Teams: TeamsProtocol {
    let homeTeam: TournamentTile.Team?
    let awayTeam: TournamentTile.Team?
}

extension TournamentTile.Game: TeamsProtocol {}

extension TournamentTile.Team {
    fileprivate func lost(game: TournamentTile.Game) -> Bool? {
        if case .confirmed(let data) = self {
            return data.lost(game: game)
        }
        return nil
    }

    fileprivate func won(game: TournamentTile.Game) -> Bool? {
        if case .confirmed(let data) = self {
            return data.won(game: game)
        }
        return nil
    }

    fileprivate func victoriesCount(series: TournamentTile.Series) -> Int? {
        if case .confirmed(let data) = self {
            return data.victoriesCount(series: series)
        }
        return nil
    }

    fileprivate var alias: String {
        switch self {
        case .confirmed(let data):
            return data.alias
        case .placeholder(let name):
            return name
        }
    }

    fileprivate var logos: [ATHImageResource]? {
        if case .confirmed(let data) = self {
            return data.logos
        }
        return nil
    }

    fileprivate var accentColor: Color? {
        if case .confirmed(let data) = self {
            return data.accentColor
        }
        return nil
    }
}

extension TournamentTile.Game {
    fileprivate var isLive: Bool {
        phase == .inGame
    }

    fileprivate func formattedDateString(tbdString: String) -> String? {
        guard let scheduledAt else { return tbdString }
        if phase == .preGame {
            let day = Date.shortMonthDayFormatter.string(from: scheduledAt)

            if isStartTimeToBeDecided {
                return [day, tbdString].joined(separator: ", ")
            } else {
                let time = Date.timeWithSpaceFormatter.string(from: scheduledAt)
                return [day, time].joined(separator: ", ")
            }
        }
        return Date.shortMonthDayFormatter.string(from: scheduledAt)
    }
}

struct SwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        let teams = Teams(
            homeTeam: .confirmed(
                TournamentTile.TeamData(
                    id: "103",
                    legacyId: nil,
                    logos: [
                        ATHImageResource(
                            url: URL(
                                string:
                                    "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-103-50x50.png"
                            )!
                        )
                    ],
                    alias: "HOU",
                    accentColor: Color(hex: "104e90")
                )
            ),
            awayTeam: .confirmed(
                TournamentTile.TeamData(
                    id: "111",
                    legacyId: nil,
                    logos: [
                        ATHImageResource(
                            url: URL(
                                string:
                                    "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-111-50x50.png"
                            )!
                        )
                    ],
                    alias: "NYY",
                    accentColor: Color(hex: "104e90")
                )
            )
        )
        BracketSeriesView(
            teams: teams,
            roundTab: BracketTab(id: "1", title: "DIVISIONAL SERIES"),
            series: TournamentTile.Series(
                id: "1",
                bestOf: 5,
                isLive: false,
                homeTeam: teams.homeTeam,
                awayTeam: teams.awayTeam,
                games: [
                    TournamentTile.Game(
                        id: "1",
                        phase: .postGame,
                        status: .final,
                        sport: .baseball,
                        ticketViewModel: nil,
                        inningHalf: .over,
                        inning: 8,
                        matchTimeDisplay: nil,
                        startedAt: nil,
                        scheduledAt: Date(timeIntervalSince1970: 1_665_122_400),
                        homeTeam: TournamentTile.GameTeam(
                            details: teams.homeTeam!,
                            scores: TournamentTile.TeamScore(
                                score: 2,
                                penaltyScore: nil
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: teams.awayTeam!,
                            scores: TournamentTile.TeamScore(
                                score: 1,
                                penaltyScore: nil
                            )
                        )
                    ),
                    TournamentTile.Game(
                        id: "2",
                        phase: .inGame,
                        status: .inProgress,
                        sport: .baseball,
                        ticketViewModel: nil,
                        inningHalf: .bottom,
                        inning: 8,
                        matchTimeDisplay: nil,
                        startedAt: nil,
                        scheduledAt: Date(timeIntervalSince1970: 1_665_151_200),
                        homeTeam: TournamentTile.GameTeam(
                            details: teams.homeTeam!,
                            scores: TournamentTile.TeamScore(
                                score: 0,
                                penaltyScore: nil
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: teams.awayTeam!,
                            scores: TournamentTile.TeamScore(
                                score: 2,
                                penaltyScore: nil
                            )
                        )
                    ),
                    TournamentTile.Game(
                        id: "3",
                        phase: .preGame,
                        status: .scheduled,
                        sport: .baseball,
                        ticketViewModel: GameTicketPurchaseButtonViewModel(
                            title: "Tickets from $55",
                            url: URL(string: "https://mock")!,
                            provider: "mockProvider",
                            lightModeLogos: [],
                            darkModeLogos: [],
                            analyticsDefaults: PreviewAnalyticDefaults()
                        ),
                        inningHalf: nil,
                        inning: nil,
                        matchTimeDisplay: nil,
                        startedAt: nil,
                        scheduledAt: Date(timeIntervalSince1970: 1_665_151_200),
                        homeTeam: TournamentTile.GameTeam(
                            details: teams.homeTeam!,
                            scores: TournamentTile.TeamScore(
                                score: 0,
                                penaltyScore: nil
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: teams.awayTeam!,
                            scores: TournamentTile.TeamScore(
                                score: 0,
                                penaltyScore: nil
                            )
                        )
                    ),
                    TournamentTile.Game(
                        id: "4",
                        phase: .preGame,
                        status: .scheduled,
                        sport: .baseball,
                        ticketViewModel: nil,
                        inningHalf: nil,
                        inning: nil,
                        matchTimeDisplay: nil,
                        startedAt: nil,
                        scheduledAt: Date(timeIntervalSince1970: 1_665_151_200),
                        homeTeam: TournamentTile.GameTeam(
                            details: teams.homeTeam!,
                            scores: TournamentTile.TeamScore(
                                score: 0,
                                penaltyScore: nil
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: teams.awayTeam!,
                            scores: TournamentTile.TeamScore(
                                score: 0,
                                penaltyScore: nil
                            )
                        )
                    ),
                    TournamentTile.Game(
                        id: "5",
                        phase: .preGame,
                        status: .scheduled,
                        sport: .baseball,
                        ticketViewModel: nil,
                        inningHalf: nil,
                        inning: nil,
                        matchTimeDisplay: nil,
                        startedAt: nil,
                        scheduledAt: Date(timeIntervalSince1970: 1_665_151_200),
                        homeTeam: TournamentTile.GameTeam(
                            details: teams.homeTeam!,
                            scores: TournamentTile.TeamScore(
                                score: 0,
                                penaltyScore: nil
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: teams.awayTeam!,
                            scores: TournamentTile.TeamScore(
                                score: 0,
                                penaltyScore: nil
                            )
                        )
                    ),
                ]
            ),
            onTapGame: { _ in },
            onTapTeam: { _ in },
            tbdString: "TBD"
        )
        .darkScheme()
        .loadCustomFonts()
    }
}
