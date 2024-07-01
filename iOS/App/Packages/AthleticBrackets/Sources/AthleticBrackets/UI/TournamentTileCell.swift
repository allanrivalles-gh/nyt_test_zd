//
//  TournamentTileCell.swift
//
//
//  Created by Leonardo da Silva on 28/10/22.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

public struct TournamentTileCell: View {
    private let tile: TournamentTile
    private let tbdString: String
    private let liveString: String
    private let suggestedWidth: CGFloat?

    public init(
        tile: TournamentTile,
        tbdString: String,
        liveString: String,
        suggestedWidth: CGFloat? = nil
    ) {
        self.tile = tile
        self.tbdString = tbdString
        self.liveString = liveString
        self.suggestedWidth = suggestedWidth
    }

    public var body: some View {
        let horizontalPadding: CGFloat = 16
        let minWidth: CGFloat = 100 + horizontalPadding * 2
        let width = suggestedWidth.map { max(minWidth, $0) } ?? minWidth
        VStack(alignment: .leading, spacing: 16) {
            title
            let game = tile.asSingleGame
            teamInfo(team: tile.homeTeam, gameScore: game?.homeTeam?.scores)
            teamInfo(team: tile.awayTeam, gameScore: game?.awayTeam?.scores)
        }
        .padding(.horizontal, horizontalPadding)
        .padding(.vertical, 8)
        .background(Color.chalk.dark200)
        .border(tile.isHighlighted ? Color.chalk.dark500 : Color.clear, width: 1)
        .frame(width: width)
    }

    @ViewBuilder
    private var title: some View {
        /// we fallback to an empty string because we need to render the `Text` view, since it contributes to the layout
        let title = tile.title ?? ""
        Group {
            switch tile.liveText(liveString: liveString) {
            case .prefix(let liveText):
                HStack(spacing: 0) {
                    Text(liveText)
                        .foregroundColor(.chalk.red)
                        .fontStyle(.calibreUtility.xs.medium)
                    /// leading white space for seamless spacing
                    Text(" \(title)")
                        .fontStyle(.calibreUtility.xs.regular)
                }
            case .suffix(let liveText):
                Text("\(title), \(Text(liveText).foregroundColor(.chalk.red))")
                    .fontStyle(.calibreUtility.xs.medium)
            case nil:
                Text(title)
                    .fontStyle(.calibreUtility.xs.regular)
            }
        }
        .foregroundColor(.chalk.dark700)
        .lineLimit(1)
    }

    private func teamInfo(
        team: TournamentTile.Team?,
        gameScore: TournamentTile.TeamScore?
    ) -> some View {
        var logos: [ATHImageResource]?
        var alias: String?
        var score: Int?
        var penaltyScore: Int?
        var lost: Bool?
        var seed: Int?
        var record: String?
        var seriesLosses: [Bool?]?
        var teamData: TournamentTile.TeamData?
        var redCardsCount: Int?
        switch team {
        case .confirmed(let data):
            logos = data.logos
            alias = data.alias
            seed = data.seed
            teamData = data
        case .placeholder(let name):
            alias = name
        default: break
        }

        switch tile.data {
        case .game(let game):
            lost = teamData?.lost(game: game)

            if game.shouldRenderScore {
                score = gameScore?.score
                penaltyScore = gameScore?.penaltyScore
                redCardsCount = gameScore?.redCardsCount
            } else {
                record = teamData?.record
            }
        case .series(let series):
            let result = series.getResult(team: teamData)
            lost = result.lost

            if let game = series.games.first, !game.shouldRenderScore {
                record = teamData?.record
            } else {
                seriesLosses = result.losses
            }
        }

        return HStack(spacing: 0) {
            TeamLogoLazyImage(
                size: 20,
                resources: logos ?? []
            )

            if let seed {
                Text(String(seed))
                    .foregroundColor(.chalk.dark500)
                    .fontStyle(.calibreUtility.xs.regular)
                    .frame(width: 12)
                    .padding(.leading, 4)
            }

            Text(alias ?? tbdString)
                .padding(.leading, seed == nil ? 8 : 6)

            if let redCardsCount {
                let _ = DuplicateIDLogger.logDuplicates(in: Array(0..<redCardsCount), id: \.self)
                ForEach(0..<redCardsCount, id: \.self) { _ in
                    RedCardView(height: 8)
                        .padding(.leading, 4)
                }
            }

            Spacer(minLength: 0)

            Group {
                if let record {
                    Text(record)
                        .foregroundColor(.chalk.dark500)
                        .fontStyle(.calibreUtility.xs.regular)
                }

                if let score {
                    HStack(spacing: 2) {
                        Text(String(score))
                        if let penaltyScore {
                            Text("(\(String(penaltyScore)))")
                        }
                    }
                }

                if let seriesLosses {
                    HStack(spacing: 4) {
                        let _ = DuplicateIDLogger.logDuplicates(
                            in: Array(seriesLosses.enumerated()),
                            id: \.0
                        )
                        ForEach(Array(seriesLosses.enumerated()), id: \.0) { (_, lost) in
                            SeriesVictoryIndicator(lost: lost)
                        }
                    }
                }
            }
            .padding(.leading, 8)
        }
        .fontStyle(.calibreUtility.xl.medium)
        .foregroundColor(lost ?? true ? .chalk.dark500 : .chalk.dark700)
    }
}

private struct RedCardView: View {
    let height: CGFloat

    var body: some View {
        RoundedRectangle(cornerRadius: relativeSize(for: 2))
            .fill(Color.chalk.red)
            .frame(width: relativeSize(for: 10), height: relativeSize(for: 14))
    }

    private func relativeSize(for absoluteSize: CGFloat) -> CGFloat {
        /// the base card (`icn_soccer_red_card`) has height of 14
        /// we transform the absolute values into relative values using this function
        return absoluteSize / 14 * height
    }
}

private struct SeriesVictoryIndicator: View {
    let lost: Bool?

    var body: some View {
        Circle()
            .strokeBorder(strokeColor, lineWidth: 1)
            .background(Circle().fill(fillColor))
            .frame(width: 8, height: 8)
    }

    private var strokeColor: Color {
        lost == true ? .chalk.dark500 : .clear
    }

    private var fillColor: Color {
        guard let lost else { return .chalk.dark300 }
        return lost ? .clear : .chalk.green
    }
}

extension TournamentTile {
    fileprivate enum LiveText {
        case prefix(String)
        case suffix(String)
    }

    fileprivate func liveText(liveString: String) -> LiveText? {
        switch data {
        case .series(let series):
            guard series.isLive else { return nil }
            return .suffix(liveString)
        case .game(let game):
            guard game.status == .inProgress, let matchTimeDisplay = game.matchTimeDisplay else {
                return nil
            }
            return .prefix(matchTimeDisplay)
        }
    }

    fileprivate var asSingleGame: TournamentTile.Game? {
        if case .game(let game) = data {
            return game
        }
        return nil
    }
}

extension TournamentTile.Game {
    fileprivate var shouldRenderScore: Bool {
        phase != .preGame
    }
}

struct TournamentTileCell_Previews: PreviewProvider {
    private static func logos(teamId: String) -> [ATHImageResource] {
        let cdn = "https://cdn-team-logos.theathletic.com"
        return [ATHImageResource(url: URL(string: "\(cdn)/team-logo-\(teamId)-72x72.png")!)]
    }

    private struct SingleGame {
        struct NCAAB {
            private static func gonz(omitRecord: Bool = false) -> TournamentTile.TeamData {
                TournamentTile.TeamData(
                    id: "1049",
                    legacyId: nil,
                    logos: logos(teamId: "1049"),
                    alias: "GONZ",
                    accentColor: Color(hex: "848484"),
                    seed: 7,
                    record: omitRecord ? nil : "11-6"
                )
            }

            private static func gsu(omitRecord: Bool = false) -> TournamentTile.TeamData {
                TournamentTile.TeamData(
                    id: "825",
                    legacyId: nil,
                    logos: logos(teamId: "825"),
                    alias: "GSU",
                    accentColor: Color(hex: "848484"),
                    seed: 10,
                    record: omitRecord ? nil : "11-6"
                )
            }

            static let recent = TournamentTile(
                title: "Final, Mar 17, Alumni Stadium",
                data: .game(
                    TournamentTile.Game(
                        id: "1",
                        phase: .postGame,
                        status: .final,
                        sport: nil,
                        ticketViewModel: nil,
                        homeTeam: TournamentTile.GameTeam(
                            details: .confirmed(gonz()),
                            scores: TournamentTile.TeamScore(
                                score: 141
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: .confirmed(gsu()),
                            scores: TournamentTile.TeamScore(
                                score: 101
                            )
                        )
                    )
                ),
                isHighlighted: false
            )

            static let live = TournamentTile(
                title: "Alumni Stadium",
                data: .game(
                    TournamentTile.Game(
                        id: "2",
                        phase: .inGame,
                        status: .inProgress,
                        sport: nil,
                        ticketViewModel: nil,
                        matchTimeDisplay: "2nd - 15:00",
                        homeTeam: TournamentTile.GameTeam(
                            details: .confirmed(gonz()),
                            scores: TournamentTile.TeamScore(
                                score: 102
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: .confirmed(gsu()),
                            scores: TournamentTile.TeamScore(
                                score: 85
                            )
                        )
                    )
                ),
                isHighlighted: false
            )

            struct Upcoming {
                static let teamsTBD = TournamentTile(
                    title: "Sun, Mar 17, Alumni Stadium",
                    data: .game(
                        TournamentTile.Game(
                            id: "3",
                            phase: .preGame,
                            status: .scheduled,
                            sport: nil,
                            ticketViewModel: nil,
                            homeTeam: nil,
                            awayTeam: nil
                        )
                    ),
                    isHighlighted: false
                )

                static let knownTeams = TournamentTile(
                    title: "Sun, Mar 17, Alumni Stadium",
                    data: .game(
                        TournamentTile.Game(
                            id: "4",
                            phase: .preGame,
                            status: .scheduled,
                            sport: nil,
                            ticketViewModel: nil,
                            homeTeam: TournamentTile.GameTeam(
                                details: .confirmed(gonz(omitRecord: true))
                            ),
                            awayTeam: TournamentTile.GameTeam(
                                details: .confirmed(gsu(omitRecord: true))
                            )
                        )
                    ),
                    isHighlighted: false
                )

                static let seeds = TournamentTile(
                    title: "Sun, Mar 17, Alumni Stadium",
                    data: .game(
                        TournamentTile.Game(
                            id: "5",
                            phase: .preGame,
                            status: .scheduled,
                            sport: nil,
                            ticketViewModel: nil,
                            homeTeam: TournamentTile.GameTeam(
                                details: .confirmed(gonz())
                            ),
                            awayTeam: TournamentTile.GameTeam(
                                details: .confirmed(gsu())
                            )
                        )
                    ),
                    isHighlighted: false
                )
            }
        }

        struct NFL {
            private static func sf(omitRecord: Bool = false) -> TournamentTile.TeamData {
                TournamentTile.TeamData(
                    id: "58",
                    legacyId: nil,
                    logos: logos(teamId: "58"),
                    alias: "SF",
                    accentColor: Color(hex: "d30303"),
                    seed: 7,
                    record: omitRecord ? nil : "11-6"
                )
            }

            private static func cle(omitRecord: Bool = false) -> TournamentTile.TeamData {
                TournamentTile.TeamData(
                    id: "38",
                    legacyId: nil,
                    logos: logos(teamId: "38"),
                    alias: "CLE",
                    accentColor: Color(hex: "a14f0c"),
                    seed: 10,
                    record: omitRecord ? nil : "11-6"
                )
            }

            static let recent = TournamentTile(
                title: "Final, Mar 17, MetLife Stadium",
                data: .game(
                    TournamentTile.Game(
                        id: "1",
                        phase: .postGame,
                        status: .final,
                        sport: nil,
                        ticketViewModel: nil,
                        homeTeam: TournamentTile.GameTeam(
                            details: .confirmed(sf()),
                            scores: TournamentTile.TeamScore(
                                score: 58
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: .confirmed(cle()),
                            scores: TournamentTile.TeamScore(
                                score: 3
                            )
                        )
                    )
                ),
                isHighlighted: false
            )

            static let live = TournamentTile(
                title: "MetLife Stadium",
                data: .game(
                    TournamentTile.Game(
                        id: "2",
                        phase: .inGame,
                        status: .inProgress,
                        sport: nil,
                        ticketViewModel: nil,
                        matchTimeDisplay: "2nd - 15:00",
                        homeTeam: TournamentTile.GameTeam(
                            details: .confirmed(sf()),
                            scores: TournamentTile.TeamScore(
                                score: 55
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: .confirmed(cle()),
                            scores: TournamentTile.TeamScore(
                                score: 3
                            )
                        )
                    )
                ),
                isHighlighted: false
            )

            struct Upcoming {
                static let teamsTBD = TournamentTile(
                    title: "Sun, Mar 17, MetLife Stadium",
                    data: .game(
                        TournamentTile.Game(
                            id: "3",
                            phase: .preGame,
                            status: .scheduled,
                            sport: nil,
                            ticketViewModel: nil,
                            homeTeam: nil,
                            awayTeam: nil
                        )
                    ),
                    isHighlighted: false
                )

                static let knownTeams = TournamentTile(
                    title: "Sun, Mar 17, MetLife Stadium",
                    data: .game(
                        TournamentTile.Game(
                            id: "4",
                            phase: .preGame,
                            status: .scheduled,
                            sport: nil,
                            ticketViewModel: nil,
                            homeTeam: TournamentTile.GameTeam(
                                details: .confirmed(sf(omitRecord: true))
                            ),
                            awayTeam: TournamentTile.GameTeam(
                                details: .confirmed(cle(omitRecord: true))
                            )
                        )
                    ),
                    isHighlighted: false
                )

                static let seeds = TournamentTile(
                    title: "Sun, Mar 17, MetLife Stadium",
                    data: .game(
                        TournamentTile.Game(
                            id: "5",
                            phase: .preGame,
                            status: .scheduled,
                            sport: nil,
                            ticketViewModel: nil,
                            homeTeam: TournamentTile.GameTeam(
                                details: .confirmed(sf())
                            ),
                            awayTeam: TournamentTile.GameTeam(
                                details: .confirmed(cle())
                            )
                        )
                    ),
                    isHighlighted: false
                )
            }
        }

        struct MLS {
            private static func dcu(omitRecord: Bool = false) -> TournamentTile.TeamData {
                TournamentTile.TeamData(
                    id: "132",
                    legacyId: nil,
                    logos: logos(teamId: "132"),
                    alias: "DCU",
                    accentColor: Color(hex: "797979"),
                    seed: 7,
                    record: omitRecord ? nil : "11-6"
                )
            }

            private static func col(omitRecord: Bool = false) -> TournamentTile.TeamData {
                TournamentTile.TeamData(
                    id: "141",
                    legacyId: nil,
                    logos: logos(teamId: "141"),
                    alias: "COL",
                    accentColor: Color(hex: "85b7ea"),
                    seed: 10,
                    record: omitRecord ? nil : "11-6"
                )
            }

            static let recent = TournamentTile(
                title: "FT, Mar 17, Q2 Stadium",
                data: .game(
                    TournamentTile.Game(
                        id: "1",
                        phase: .postGame,
                        status: .final,
                        sport: nil,
                        ticketViewModel: nil,
                        homeTeam: TournamentTile.GameTeam(
                            details: .confirmed(dcu()),
                            scores: TournamentTile.TeamScore(
                                score: 2
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: .confirmed(col()),
                            scores: TournamentTile.TeamScore(
                                score: 4
                            )
                        )
                    )
                ),
                isHighlighted: false
            )

            static let live = TournamentTile(
                title: "Q2 Stadium",
                data: .game(
                    TournamentTile.Game(
                        id: "2",
                        phase: .inGame,
                        status: .inProgress,
                        sport: nil,
                        ticketViewModel: nil,
                        matchTimeDisplay: "46'",
                        homeTeam: TournamentTile.GameTeam(
                            details: .confirmed(dcu()),
                            scores: TournamentTile.TeamScore(
                                score: 2
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: .confirmed(col()),
                            scores: TournamentTile.TeamScore(
                                score: 3
                            )
                        )
                    )
                ),
                isHighlighted: false
            )

            struct Upcoming {
                static let teamsTBD = TournamentTile(
                    title: "Sun, Mar 17, Q2 Stadium",
                    data: .game(
                        TournamentTile.Game(
                            id: "3",
                            phase: .preGame,
                            status: .scheduled,
                            sport: nil,
                            ticketViewModel: nil,
                            homeTeam: nil,
                            awayTeam: nil
                        )
                    ),
                    isHighlighted: false
                )

                static let knownTeams = TournamentTile(
                    title: "Sun, Mar 17, Q2 Stadium",
                    data: .game(
                        TournamentTile.Game(
                            id: "4",
                            phase: .preGame,
                            status: .scheduled,
                            sport: nil,
                            ticketViewModel: nil,
                            homeTeam: TournamentTile.GameTeam(
                                details: .confirmed(dcu(omitRecord: true))
                            ),
                            awayTeam: TournamentTile.GameTeam(
                                details: .confirmed(col(omitRecord: true))
                            )
                        )
                    ),
                    isHighlighted: false
                )

                static let seeds = TournamentTile(
                    title: "Sun, Mar 17, Q2 Stadium",
                    data: .game(
                        TournamentTile.Game(
                            id: "5",
                            phase: .preGame,
                            status: .scheduled,
                            sport: nil,
                            ticketViewModel: nil,
                            homeTeam: TournamentTile.GameTeam(
                                details: .confirmed(dcu())
                            ),
                            awayTeam: TournamentTile.GameTeam(
                                details: .confirmed(col())
                            )
                        )
                    ),
                    isHighlighted: false
                )
            }

            static let variations = TournamentTile(
                title: "Mar 17, Q2 Stadium",
                data: .game(
                    TournamentTile.Game(
                        id: "6",
                        phase: .postGame,
                        status: .final,
                        sport: nil,
                        ticketViewModel: nil,
                        homeTeam: TournamentTile.GameTeam(
                            details: .confirmed(dcu()),
                            scores: TournamentTile.TeamScore(
                                score: 2,
                                penaltyScore: 3,
                                redCardsCount: 1
                            )
                        ),
                        awayTeam: TournamentTile.GameTeam(
                            details: .confirmed(col()),
                            scores: TournamentTile.TeamScore(
                                score: 2,
                                penaltyScore: 4
                            )
                        )
                    )
                ),
                isHighlighted: false
            )
        }
    }

    private struct Series {
        private enum WinnerTeam {
            case home
            case away
        }

        private static func seriesGames(
            homeTeam: TournamentTile.TeamData,
            awayTeam: TournamentTile.TeamData,
            winners: [WinnerTeam]
        ) -> [TournamentTile.Game] {
            winners.enumerated().map { index, winner in
                TournamentTile.Game(
                    id: "\(index)",
                    phase: .postGame,
                    status: .final,
                    sport: nil,
                    ticketViewModel: nil,
                    homeTeam: TournamentTile.GameTeam(
                        details: .confirmed(homeTeam),
                        scores: TournamentTile.TeamScore(
                            score: winner == .home ? 1 : 0
                        )
                    ),
                    awayTeam: TournamentTile.GameTeam(
                        details: .confirmed(awayTeam),
                        scores: TournamentTile.TeamScore(
                            score: winner == .away ? 1 : 0
                        )
                    )
                )
            }
        }

        struct MLB {
            private static let nyy = TournamentTile.TeamData(
                id: "111",
                legacyId: nil,
                logos: logos(teamId: "111"),
                alias: "NYY",
                accentColor: Color(hex: "104e90"),
                seed: 7,
                record: "11-6"
            )

            private static let hou = TournamentTile.TeamData(
                id: "103",
                legacyId: nil,
                logos: logos(teamId: "103"),
                alias: "HOU",
                accentColor: Color(hex: "104e90"),
                seed: 10,
                record: "11-6"
            )

            static let recent = TournamentTile(
                title: "HOU Wins Series 3-1, Final",
                data: .series(
                    TournamentTile.Series(
                        id: "1",
                        bestOf: 5,
                        isLive: false,
                        homeTeam: .confirmed(nyy),
                        awayTeam: .confirmed(hou),
                        games: seriesGames(
                            homeTeam: nyy,
                            awayTeam: hou,
                            winners: [.home, .away, .away, .away]
                        )
                    )
                ),
                isHighlighted: false
            )

            static let live = TournamentTile(
                title: "Game 2",
                data: .series(
                    TournamentTile.Series(
                        id: "2",
                        bestOf: 5,
                        isLive: true,
                        homeTeam: .confirmed(nyy),
                        awayTeam: .confirmed(hou),
                        games: seriesGames(
                            homeTeam: nyy,
                            awayTeam: hou,
                            winners: [.away]
                        )
                    )
                ),
                isHighlighted: false
            )

            struct MidSeries {
                static let teamLeading = TournamentTile(
                    title: "HOU Leads Series 2-1",
                    data: .series(
                        TournamentTile.Series(
                            id: "3",
                            bestOf: 5,
                            isLive: false,
                            homeTeam: .confirmed(nyy),
                            awayTeam: .confirmed(hou),
                            games: seriesGames(
                                homeTeam: nyy,
                                awayTeam: hou,
                                winners: [.away, .home, .away]
                            )
                        )
                    ),
                    isHighlighted: false
                )

                static let tied = TournamentTile(
                    title: "Series Tied 1-1",
                    data: .series(
                        TournamentTile.Series(
                            id: "4",
                            bestOf: 5,
                            isLive: false,
                            homeTeam: .confirmed(nyy),
                            awayTeam: .confirmed(hou),
                            games: seriesGames(
                                homeTeam: nyy,
                                awayTeam: hou,
                                winners: [.away, .home]
                            )
                        )
                    ),
                    isHighlighted: false
                )
            }

            struct Upcoming {
                static let teamsTBD = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "5",
                            bestOf: 5,
                            isLive: false,
                            homeTeam: nil,
                            awayTeam: nil,
                            games: []
                        )
                    ),
                    isHighlighted: false
                )

                static let knownTeams = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "6",
                            bestOf: 5,
                            isLive: false,
                            homeTeam: .confirmed(nyy),
                            awayTeam: .confirmed(hou),
                            games: []
                        )
                    ),
                    isHighlighted: false
                )

                static let seeds = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "7",
                            bestOf: 5,
                            isLive: false,
                            homeTeam: .confirmed(nyy),
                            awayTeam: .confirmed(hou),
                            games: [
                                TournamentTile.Game(
                                    id: "1",
                                    phase: .preGame,
                                    status: .scheduled,
                                    sport: nil,
                                    ticketViewModel: nil,
                                    homeTeam: TournamentTile.GameTeam(
                                        details: .confirmed(nyy)
                                    ),
                                    awayTeam: TournamentTile.GameTeam(
                                        details: .confirmed(hou)
                                    )
                                )
                            ]
                        )
                    ),
                    isHighlighted: false
                )
            }
        }

        struct NBA {
            private static let chi = TournamentTile.TeamData(
                id: "73",
                legacyId: nil,
                logos: logos(teamId: "73"),
                alias: "CHI",
                accentColor: nil,
                seed: 7,
                record: "11-6"
            )

            private static let mil = TournamentTile.TeamData(
                id: "77",
                legacyId: nil,
                logos: logos(teamId: "77"),
                alias: "MIL",
                accentColor: nil,
                seed: 10,
                record: "11-6"
            )

            static let recent = TournamentTile(
                title: "MIL Wins Series 4-2, Final",
                data: .series(
                    TournamentTile.Series(
                        id: "1",
                        bestOf: 7,
                        isLive: false,
                        homeTeam: .confirmed(chi),
                        awayTeam: .confirmed(mil),
                        games: seriesGames(
                            homeTeam: chi,
                            awayTeam: mil,
                            winners: [.home, .home, .away, .away, .away, .away]
                        )
                    )
                ),
                isHighlighted: false
            )

            static let live = TournamentTile(
                title: "Game 2",
                data: .series(
                    TournamentTile.Series(
                        id: "2",
                        bestOf: 7,
                        isLive: true,
                        homeTeam: .confirmed(chi),
                        awayTeam: .confirmed(mil),
                        games: seriesGames(
                            homeTeam: chi,
                            awayTeam: mil,
                            winners: [.away]
                        )
                    )
                ),
                isHighlighted: false
            )

            struct MidSeries {
                static let teamLeading = TournamentTile(
                    title: "MIL Leads Series 2-1",
                    data: .series(
                        TournamentTile.Series(
                            id: "3",
                            bestOf: 7,
                            isLive: false,
                            homeTeam: .confirmed(chi),
                            awayTeam: .confirmed(mil),
                            games: seriesGames(
                                homeTeam: chi,
                                awayTeam: mil,
                                winners: [.away, .home, .away]
                            )
                        )
                    ),
                    isHighlighted: false
                )

                static let tied = TournamentTile(
                    title: "Series Tied 1-1",
                    data: .series(
                        TournamentTile.Series(
                            id: "4",
                            bestOf: 7,
                            isLive: false,
                            homeTeam: .confirmed(chi),
                            awayTeam: .confirmed(mil),
                            games: seriesGames(
                                homeTeam: chi,
                                awayTeam: mil,
                                winners: [.away, .home]
                            )
                        )
                    ),
                    isHighlighted: false
                )
            }

            struct Upcoming {
                static let teamsTBD = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "5",
                            bestOf: 7,
                            isLive: false,
                            homeTeam: nil,
                            awayTeam: nil,
                            games: []
                        )
                    ),
                    isHighlighted: false
                )

                static let knownTeams = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "6",
                            bestOf: 7,
                            isLive: false,
                            homeTeam: .confirmed(chi),
                            awayTeam: .confirmed(mil),
                            games: []
                        )
                    ),
                    isHighlighted: false
                )

                static let seeds = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "7",
                            bestOf: 7,
                            isLive: false,
                            homeTeam: .confirmed(chi),
                            awayTeam: .confirmed(mil),
                            games: [
                                TournamentTile.Game(
                                    id: "1",
                                    phase: .preGame,
                                    status: .scheduled,
                                    sport: nil,
                                    ticketViewModel: nil,
                                    homeTeam: TournamentTile.GameTeam(
                                        details: .confirmed(chi)
                                    ),
                                    awayTeam: TournamentTile.GameTeam(
                                        details: .confirmed(mil)
                                    )
                                )
                            ]
                        )
                    ),
                    isHighlighted: false
                )
            }
        }

        struct NHL {
            private static let bos = TournamentTile.TeamData(
                id: "1",
                legacyId: nil,
                logos: logos(teamId: "1"),
                alias: "BOS",
                accentColor: nil,
                seed: 7,
                record: "11-6"
            )

            private static let sea = TournamentTile.TeamData(
                id: "756",
                legacyId: nil,
                logos: logos(teamId: "756"),
                alias: "SEA",
                accentColor: nil,
                seed: 10,
                record: "11-6"
            )

            static let recent = TournamentTile(
                title: "SEA Wins Series 4-2, Final",
                data: .series(
                    TournamentTile.Series(
                        id: "1",
                        bestOf: 7,
                        isLive: false,
                        homeTeam: .confirmed(bos),
                        awayTeam: .confirmed(sea),
                        games: seriesGames(
                            homeTeam: bos,
                            awayTeam: sea,
                            winners: [.home, .home, .away, .away, .away, .away]
                        )
                    )
                ),
                isHighlighted: false
            )

            static let live = TournamentTile(
                title: "Game 2",
                data: .series(
                    TournamentTile.Series(
                        id: "2",
                        bestOf: 7,
                        isLive: true,
                        homeTeam: .confirmed(bos),
                        awayTeam: .confirmed(sea),
                        games: seriesGames(
                            homeTeam: bos,
                            awayTeam: sea,
                            winners: [.away]
                        )
                    )
                ),
                isHighlighted: false
            )

            struct MidSeries {
                static let teamLeading = TournamentTile(
                    title: "SEA Leads Series 2-1",
                    data: .series(
                        TournamentTile.Series(
                            id: "3",
                            bestOf: 7,
                            isLive: false,
                            homeTeam: .confirmed(bos),
                            awayTeam: .confirmed(sea),
                            games: seriesGames(
                                homeTeam: bos,
                                awayTeam: sea,
                                winners: [.away, .home, .away]
                            )
                        )
                    ),
                    isHighlighted: false
                )

                static let tied = TournamentTile(
                    title: "Series Tied 1-1",
                    data: .series(
                        TournamentTile.Series(
                            id: "4",
                            bestOf: 7,
                            isLive: false,
                            homeTeam: .confirmed(bos),
                            awayTeam: .confirmed(sea),
                            games: seriesGames(
                                homeTeam: bos,
                                awayTeam: sea,
                                winners: [.away, .home]
                            )
                        )
                    ),
                    isHighlighted: false
                )
            }

            struct Upcoming {
                static let teamsTBD = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "5",
                            bestOf: 7,
                            isLive: false,
                            homeTeam: nil,
                            awayTeam: nil,
                            games: []
                        )
                    ),
                    isHighlighted: false
                )

                static let knownTeams = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "6",
                            bestOf: 7,
                            isLive: false,
                            homeTeam: .confirmed(bos),
                            awayTeam: .confirmed(sea),
                            games: []
                        )
                    ),
                    isHighlighted: false
                )

                static let seeds = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "7",
                            bestOf: 7,
                            isLive: false,
                            homeTeam: .confirmed(bos),
                            awayTeam: .confirmed(sea),
                            games: [
                                TournamentTile.Game(
                                    id: "1",
                                    phase: .preGame,
                                    status: .scheduled,
                                    sport: nil,
                                    ticketViewModel: nil,
                                    homeTeam: TournamentTile.GameTeam(
                                        details: .confirmed(bos)
                                    ),
                                    awayTeam: TournamentTile.GameTeam(
                                        details: .confirmed(sea)
                                    )
                                )
                            ]
                        )
                    ),
                    isHighlighted: false
                )
            }
        }

        struct MLS {
            private static let dcu = TournamentTile.TeamData(
                id: "132",
                legacyId: nil,
                logos: logos(teamId: "132"),
                alias: "DCU",
                accentColor: nil,
                seed: 7,
                record: "11-6"
            )

            private static let col = TournamentTile.TeamData(
                id: "19",
                legacyId: nil,
                logos: logos(teamId: "19"),
                alias: "COL",
                accentColor: nil,
                seed: 10,
                record: "11-6"
            )

            static let recent = TournamentTile(
                title: "COL Wins Series 2-0, Final",
                data: .series(
                    TournamentTile.Series(
                        id: "1",
                        bestOf: 3,
                        isLive: false,
                        homeTeam: .confirmed(dcu),
                        awayTeam: .confirmed(col),
                        games: seriesGames(
                            homeTeam: dcu,
                            awayTeam: col,
                            winners: [.away, .away]
                        )
                    )
                ),
                isHighlighted: false
            )

            static let live = TournamentTile(
                title: "Game 2",
                data: .series(
                    TournamentTile.Series(
                        id: "2",
                        bestOf: 3,
                        isLive: true,
                        homeTeam: .confirmed(dcu),
                        awayTeam: .confirmed(col),
                        games: seriesGames(
                            homeTeam: dcu,
                            awayTeam: col,
                            winners: [.away]
                        )
                    )
                ),
                isHighlighted: false
            )

            struct MidSeries {
                static let teamLeading = TournamentTile(
                    title: "COL Leads Series 1-0",
                    data: .series(
                        TournamentTile.Series(
                            id: "3",
                            bestOf: 3,
                            isLive: false,
                            homeTeam: .confirmed(dcu),
                            awayTeam: .confirmed(col),
                            games: seriesGames(
                                homeTeam: dcu,
                                awayTeam: col,
                                winners: [.away]
                            )
                        )
                    ),
                    isHighlighted: false
                )

                static let tied = TournamentTile(
                    title: "Series Tied 1-1",
                    data: .series(
                        TournamentTile.Series(
                            id: "4",
                            bestOf: 3,
                            isLive: false,
                            homeTeam: .confirmed(dcu),
                            awayTeam: .confirmed(col),
                            games: seriesGames(
                                homeTeam: dcu,
                                awayTeam: col,
                                winners: [.away, .home]
                            )
                        )
                    ),
                    isHighlighted: false
                )
            }

            struct Upcoming {
                static let teamsTBD = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "5",
                            bestOf: 3,
                            isLive: false,
                            homeTeam: nil,
                            awayTeam: nil,
                            games: []
                        )
                    ),
                    isHighlighted: false
                )

                static let knownTeams = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "6",
                            bestOf: 3,
                            isLive: false,
                            homeTeam: .confirmed(dcu),
                            awayTeam: .confirmed(col),
                            games: []
                        )
                    ),
                    isHighlighted: false
                )

                static let seeds = TournamentTile(
                    title: "Wed Oct 21",
                    data: .series(
                        TournamentTile.Series(
                            id: "7",
                            bestOf: 3,
                            isLive: false,
                            homeTeam: .confirmed(dcu),
                            awayTeam: .confirmed(col),
                            games: [
                                TournamentTile.Game(
                                    id: "1",
                                    phase: .preGame,
                                    status: .scheduled,
                                    sport: nil,
                                    ticketViewModel: nil,
                                    homeTeam: TournamentTile.GameTeam(
                                        details: .confirmed(dcu)
                                    ),
                                    awayTeam: TournamentTile.GameTeam(
                                        details: .confirmed(col)
                                    )
                                )
                            ]
                        )
                    ),
                    isHighlighted: false
                )
            }
        }
    }

    static var previews: some View {
        AllPreviews()
            .loadCustomFonts()
    }

    private struct AllPreviews: View {
        var body: some View {
            Preview(
                tiles: [
                    SingleGame.NCAAB.recent,
                    SingleGame.NCAAB.live,
                    SingleGame.NCAAB.Upcoming.teamsTBD,
                    SingleGame.NCAAB.Upcoming.knownTeams,
                    SingleGame.NCAAB.Upcoming.seeds,
                ]
            )
            .previewDisplayName("NCAAB")
            Preview(
                tiles: [
                    SingleGame.NFL.recent,
                    SingleGame.NFL.live,
                    SingleGame.NFL.Upcoming.teamsTBD,
                    SingleGame.NFL.Upcoming.knownTeams,
                    SingleGame.NFL.Upcoming.seeds,
                ]
            )
            .previewDisplayName("NFL")
            Preview(
                tiles: [
                    SingleGame.MLS.recent,
                    SingleGame.MLS.live,
                    SingleGame.MLS.Upcoming.teamsTBD,
                    SingleGame.MLS.Upcoming.knownTeams,
                    SingleGame.MLS.Upcoming.seeds,
                    SingleGame.MLS.variations,
                ]
            )
            .previewDisplayName("MLS")
            Preview(
                tiles: [
                    Series.MLB.recent,
                    Series.MLB.live,
                    Series.MLB.MidSeries.teamLeading,
                    Series.MLB.MidSeries.tied,
                    Series.MLB.Upcoming.teamsTBD,
                    Series.MLB.Upcoming.knownTeams,
                    Series.MLB.Upcoming.seeds,
                ]
            )
            .previewDisplayName("MLB")
            Preview(
                tiles: [
                    Series.NBA.recent,
                    Series.NBA.live,
                    Series.NBA.MidSeries.teamLeading,
                    Series.NBA.MidSeries.tied,
                    Series.NBA.Upcoming.teamsTBD,
                    Series.NBA.Upcoming.knownTeams,
                    Series.NBA.Upcoming.seeds,
                ]
            )
            .previewDisplayName("NBA")
            Preview(
                tiles: [
                    Series.NHL.recent,
                    Series.NHL.live,
                    Series.NHL.MidSeries.teamLeading,
                    Series.NHL.MidSeries.tied,
                    Series.NHL.Upcoming.teamsTBD,
                    Series.NHL.Upcoming.knownTeams,
                    Series.NHL.Upcoming.seeds,
                ]
            )
            .previewDisplayName("NHL")
            Preview(
                tiles: [
                    Series.MLS.recent,
                    Series.MLS.live,
                    Series.MLS.MidSeries.teamLeading,
                    Series.MLS.MidSeries.tied,
                    Series.MLS.Upcoming.teamsTBD,
                    Series.MLS.Upcoming.knownTeams,
                    Series.MLS.Upcoming.seeds,
                ]
            )
            .previewDisplayName("MLS (Series)")
        }
    }

    private struct Preview: View {
        let tiles: [TournamentTile]

        var body: some View {
            GeometryReader { geometry in
                ScrollView {
                    let suggestedWidth = geometry.size.width * 0.6
                    HStack {
                        Spacer()
                        VStack(spacing: 18) {
                            let _ = DuplicateIDLogger.logDuplicates(in: tiles)
                            ForEach(tiles) { tile in
                                TournamentTileCell(
                                    tile: tile,
                                    tbdString: "TBD",
                                    liveString: "Live",
                                    suggestedWidth: suggestedWidth
                                )
                            }
                        }
                        Spacer()
                    }
                }
            }
            .background(Color.chalk.dark100)
        }
    }
}
