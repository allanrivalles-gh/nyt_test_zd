//
//  TeamRosterViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 29/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import Foundation

final class TeamRosterViewModel: ObservableObject {
    typealias Table = PlayerStatsTableViewModel<TeamRosterStat>
    typealias Group = PlayerStatsGroup<TeamRosterStat>

    @MainActor @Published private(set) var state: LoadingState = .initial
    @MainActor @Published private(set) var table: Table?

    private let teamId: String
    private let followingModel: FollowingModel

    init(teamId: String, followingModel: FollowingModel = AppEnvironment.shared.following) {
        self.teamId = teamId
        self.followingModel = followingModel
    }

    @MainActor
    func fetchDataIfNecessary(network: HubNetworking) async {
        guard !state.isLoading, table == nil else {
            return
        }

        state = .loading()

        await loadData(network: network)
    }

    private func loadData(network: HubNetworking) async {
        await mainActorSet(\.state, .loading())

        do {
            let entity = try await network.fetchTeamRoster(teamId: teamId)
            let newTable = await makeTable(from: entity)

            await MainActor.run {
                self.table = newTable
                self.state = .loaded
            }
        } catch {
            await mainActorSet(\.state, .failed)
        }
    }

    func trackPageView() {
        Task {
            Analytics.track(
                event: AnalyticsEventRecord(
                    verb: .view,
                    view: .roster,
                    element: .teamRoster,
                    objectType: .teamId,
                    objectIdentifier: teamId,
                    metaBlob: .init(
                        leagueId: followingModel.followableEntities.team(
                            withGqlId: teamId
                        )?.associatedLeagueLegacyId
                    )
                )
            )
        }
    }

    private func makeTable(from entity: GQL.TeamRosterTeam) async -> Table? {
        let existingTable = await table
        let gqlGroups = makeGroups(from: entity)

        guard !gqlGroups.isEmpty else { return nil }

        let rowGroups = gqlGroups.makeRowGroups(
            teamId: teamId,
            teamColor: entity.colorPrimary
        )

        if let existingTable = existingTable {
            return await Table(
                gqlGroups: gqlGroups,
                rowGroups: rowGroups,
                oldTable: existingTable
            )
        } else {
            return await Table(
                gqlGroups: gqlGroups,
                rowGroups: rowGroups,
                activeSorting: [:]
            )
        }
    }

    private func makeGroups(from entity: GQL.TeamRosterTeam) -> [Group] {
        Self.builder(for: entity.sport)
            .makeGroups(
                players: entity.members.map { $0.fragments.teamRosterPlayer }
            )
    }
}

extension Array where Element == PlayerStatsGroup<TeamRosterStat> {
    func makeRowGroups(
        teamId: String,
        teamColor: String?
    ) -> PlayerStatsTableViewModel<TeamRosterStat>.RowGroups {
        reduce(into: [:]) { result, group in
            let rowMap = group.players.reduce(into: [:]) { result, player in
                result[player.id] = PlayerStatsTableViewModel.Row(
                    model: player,
                    teamId: teamId,
                    teamColor: teamColor,
                    clickAnalyticsView: .roster
                )
            }
            result[group.id] = rowMap
        }
    }
}

// MARK: - Builder Construction

extension TeamRosterViewModel {

    fileprivate static func builder(for sport: GQL.Sport) -> TeamRosterGroupBuilder {
        switch sport {
        case .americanFootball:
            return makeAmericanFootballBuilder()
        case .baseball:
            return makeBaseballBuilder()
        case .hockey:
            return makeHockeyBuilder()
        case .soccer:
            return makeSoccerBuilder()
        default:
            return makeUngroupedBuilder()
        }
    }

    private static func makeAmericanFootballBuilder() -> TeamRosterGroupBuilder {
        TeamRosterPositionGroupBuilder(
            filters: [
                TeamRosterPositionGroupBuilder.Filter(
                    id: "offense",
                    title: Strings.teamRosterOffenseTitle.localized,
                    positions: [
                        .center, .quarterback, .runningBack, .offensiveGuard, .offensiveLineman,
                        .offensiveTackle, .wideReceiver, .tightEnd, .fullback,
                    ]
                ),
                TeamRosterPositionGroupBuilder.Filter(
                    id: "defense",
                    title: Strings.teamRosterDefenseTitle.localized,
                    positions: [
                        .defensiveBack, .defensiveEnd, .defensiveLineman, .defensiveTackle,
                        .cornerback, .safety, .insideLinebacker, .linebacker, .middleLinebacker,
                        .outsideLinebacker, .freeSafety, .strongSafety, .noseTackle,
                    ]
                ),
                TeamRosterPositionGroupBuilder.Filter(
                    id: "special-teams",
                    title: Strings.teamRosterSpecialTeamsTitle.localized,
                    positions: [.kicker, .punter, .longSnapper]
                ),
            ]
        )
    }

    private static func makeBaseballBuilder() -> TeamRosterGroupBuilder {
        TeamRosterPositionGroupBuilder(
            filters: [
                TeamRosterPositionGroupBuilder.Filter(
                    id: "pitchers",
                    title: Strings.teamRosterPitchersTitle.localized,
                    positions: [.pitcher, .reliefPitcher, .startingPitcher]
                ),
                TeamRosterPositionGroupBuilder.Filter(
                    id: "catchers",
                    title: Strings.teamRosterCatchersTitle.localized,
                    positions: [.catcher]
                ),
                TeamRosterPositionGroupBuilder.Filter(
                    id: "infielders",
                    title: Strings.teamRosterInfieldersTitle.localized,
                    positions: [.firstBase, .secondBase, .thirdBase, .shortstop]
                ),
                TeamRosterPositionGroupBuilder.Filter(
                    id: "outfielders",
                    title: Strings.teamRosterOutfieldersTitle.localized,
                    positions: [.centerField, .leftField, .rightField]
                ),
                TeamRosterPositionGroupBuilder.Filter(
                    id: "designated-hitters",
                    title: Strings.teamRosterDesignatedHittersTitle.localized,
                    positions: [.designatedHitter, .pinchHitter, .pinchRunner]
                ),
            ]
        )
    }

    private static func makeHockeyBuilder() -> TeamRosterGroupBuilder {
        TeamRosterPositionGroupBuilder(
            filters: [
                TeamRosterPositionGroupBuilder.Filter(
                    id: "forward",
                    title: Strings.teamRosterForwardTitle.localized,
                    positions: [.forward]
                ),
                TeamRosterPositionGroupBuilder.Filter(
                    id: "centers",
                    title: Strings.teamRosterCentersTitle.localized,
                    positions: [.center]
                ),
                TeamRosterPositionGroupBuilder.Filter(
                    id: "left-wings",
                    title: Strings.teamRosterLeftWingsTitle.localized,
                    positions: [.leftWing]
                ),
                TeamRosterPositionGroupBuilder.Filter(
                    id: "right-wings",
                    title: Strings.teamRosterRightWingsTitle.localized,
                    positions: [.rightWing]
                ),
                TeamRosterPositionGroupBuilder.Filter(
                    id: "defense",
                    title: Strings.teamRosterDefenseTitle.localized,
                    positions: [.defense]
                ),
                TeamRosterPositionGroupBuilder.Filter(
                    id: "goalies",
                    title: Strings.teamRosterGoaliesTitle.localized,
                    positions: [.goalie]
                ),
            ]
        )
    }

    private static func makeSoccerBuilder() -> TeamRosterGroupBuilder {
        TeamRosterSoccerGroupBuilder()
    }

    private static func makeUngroupedBuilder() -> TeamRosterGroupBuilder {
        TeamRosterUngroupedGroupBuilder()
    }
}
