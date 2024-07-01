//
//  HubDetail.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 27/7/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloNetworking
import AthleticApolloTypes
import AthleticBrackets
import AthleticFoundation
import AthleticGameSchedules
import AthleticNavigation
import AthleticScoresFoundation
import AthleticUI
import Combine
import SwiftUI

struct HubDetail: View {
    @EnvironmentObject private var network: NetworkModel
    @EnvironmentObject private var compass: Compass
    @EnvironmentObject private var following: FollowingModel
    let entity: FollowingEntity
    let selectedTabType: HubTabType?

    init(entity: FollowingEntity, selectedTabType: HubTabType? = nil) {
        self.entity = entity
        self.selectedTabType = selectedTabType
    }

    var body: some View {
        HubDetailView(
            viewModel: .init(
                entity: entity,
                network: network,
                compass: compass,
                following: following,
                selectedTabType: selectedTabType
            )
        )
        .navigationTitle("")
    }
}

private struct HubDetailView: View {

    struct NavigatedSeries: Identifiable {
        let id: String
        let viewModel: LeagueBracketViewModel
        let tileIndex: BracketSeries.TileIndex
    }

    @StateObject private var viewModel: HubViewModel
    @StateObject private var collapsibleHeaderState = HubCollapsibleHeaderState()

    @EnvironmentObject private var network: NetworkModel
    @EnvironmentObject private var following: FollowingModel
    @EnvironmentObject private var navigation: NavigationModel

    init(viewModel: @escaping @autoclosure () -> HubViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel())
    }

    @State private var navigatedSeries: NavigatedSeries?
    @State private var hasStandingContainerAutoScrolled: Bool = false

    var body: some View {
        VStack(spacing: 0) {
            HubHeader(
                viewModel: viewModel.header,
                collapsibleState: collapsibleHeaderState,
                loadingState: viewModel.dataFetchState,
                foregroundColor: viewModel.colors.foreground,
                backgroundColor: viewModel.colors.background
            )

            PagingTabView(
                tabs: viewModel.tabs,
                selectedTab: $viewModel.selectedTab,
                onSelectTab: {
                    viewModel.trackTabSelection(for: $1, oldTab: $0)
                }
            ) { tab in
                tabContent(for: tab)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.chalk.dark200)
                    .pagingTabBarBackgroundColor(nil)
                    .pagingTabNormalForegroundColor(nil)
                    .pagingTabSelectedForegroundColor(nil)
                    .pagingTabBarVisibility(nil)
            }
            .pagingTabBarBackgroundColor(viewModel.colors.background)
            .pagingTabNormalForegroundColor(viewModel.colors.foreground.opacity(0.7))
            .pagingTabSelectedForegroundColor(viewModel.colors.selectedTabForeground)
            .pagingTabSizing(.equalSpacing)
            .pagingTabUnderscoreStyle(
                viewModel.entity.entityType == .team
                    ? .hugText
                    : .fillWidth
            )
            .pagingTabBarVisibility(.ifMultipleTabs(hiddenHeight: 10))

            TransparentNavigationBarStylingView(
                foregroundColor: UIColor(viewModel.colors.foreground),
                shouldRestoreOnDisappear: false
            )
            .fixedSize()
        }
        .navigationBarTitleDisplayMode(.inline)
        .followMenuNavigationItem(
            viewModel: viewModel.header.followButton,
            foregroundColor: viewModel.colors.foreground
        )
        .task {
            await viewModel.fetchHeaderInfoIfNeeded()
        }
        .sheet(item: $navigatedSeries) { presentingSeries in
            BracketSeries(
                viewModel: presentingSeries.viewModel,
                tileIndex: presentingSeries.tileIndex,
                onTapGame: { game in
                    guard !game.isPlaceholder else { return }

                    navigatedSeries = nil

                    navigation.addScreenToSelectedTab(
                        .scores(.boxScore(BoxScoreDestination(gameId: game.id)))
                    )
                },
                onTapTeam: { team in
                    let entity = team.legacyId.flatMap { legacyId in
                        following.entity(forLegacyId: legacyId, entityType: .team)
                    }
                    guard let entity else { return }

                    navigatedSeries = nil

                    navigation.addScreenToSelectedTab(
                        .hubDetails(entity: entity, preferredTab: nil)
                    )
                },
                tbdString: AthleticScoresFoundation.Strings.tbd.localized
            )
            .presentationDetents([.medium, .large])
        }
        .environmentObject(viewModel)
    }

    @ViewBuilder
    private func tabContent(for tab: HubTab) -> some View {
        switch tab.tabType {
        case .feed:
            FeedV2List(
                viewModel: FeedV2ViewModel(
                    followingEntity: viewModel.entity,
                    navigationModel: navigation
                )
            ) { offset in
                collapsibleHeaderState.handleOffsetUpdate(
                    offset: offset,
                    forTab: tab,
                    isSelectedTab: tab == viewModel.selectedTab
                )
            }
        case .podcasts:
            if let teamId = viewModel.teamLegacyId {
                FeedV2List(
                    viewModel: FeedV2ViewModel(
                        id: "\(teamId)-podcast",
                        followingEntity: viewModel.entity,
                        network: network,
                        requestConfiguration: .makePodcast(teamId: teamId),
                        navigationModel: navigation,
                        analyticsSourceView: .podcasts
                    ),
                    onScrollOffsetChanged: { offset in
                        collapsibleHeaderState.handleOffsetUpdate(
                            offset: offset,
                            forTab: tab,
                            isSelectedTab: tab == viewModel.selectedTab
                        )
                    }
                )
                .onAppear {
                    viewModel.trackPodcastTabView()
                }
            } else {
                let _ = assertionFailure(
                    "Unexpectedly asked show a podcast tab for an invalid entity"
                )
                EmptyView()
            }
        case .threads:
            TeamThreadsDetail(
                onScrollOffsetChanged: { offset in
                    collapsibleHeaderState.handleOffsetUpdate(
                        offset: offset,
                        forTab: tab,
                        isSelectedTab: tab == viewModel.selectedTab
                    )
                }
            )

        case .schedule:
            if viewModel.isTeamOrLeagueEntity {
                ScheduleList(
                    viewModel: ScheduleListViewModel(entity: viewModel.entity),
                    onScrollOffsetChanged: { offset in
                        collapsibleHeaderState.handleOffsetUpdate(
                            offset: offset,
                            forTab: tab,
                            isSelectedTab: tab == viewModel.selectedTab
                        )
                    }
                )
            } else {
                let _ = assertionFailure(
                    "Unexpectedly asked show a schedule tab for an invalid entity"
                )
                EmptyView()
            }
        case .standings:
            StandingsContainer(
                viewModel: StandingsContainerViewModel(
                    entity: viewModel.entity,
                    network: network
                ),
                onScrollOffsetChanged: { offset in
                    collapsibleHeaderState.handleOffsetUpdate(
                        offset: offset,
                        forTab: tab,
                        isSelectedTab: tab == viewModel.selectedTab
                    )
                }
            )
            .hasStandingContainerAutoScrolled($hasStandingContainerAutoScrolled)

        case .stats:
            if let teamId = viewModel.teamId {
                TeamStatsDetail(
                    viewModel: TeamStatsViewModel(teamId: teamId),
                    onScrollOffsetChanged: { offset in
                        collapsibleHeaderState.handleOffsetUpdate(
                            offset: offset,
                            forTab: tab,
                            isSelectedTab: tab == viewModel.selectedTab
                        )
                    }
                )
            } else {
                let _ = assertionFailure(
                    "Unexpectedly asked show a stats tab for an invalid entity"
                )
                EmptyView()
            }

        case .roster:
            if let teamId = viewModel.teamId {
                TeamRosterDetail(
                    viewModel: TeamRosterViewModel(teamId: teamId),
                    onScrollOffsetChanged: { offset in
                        collapsibleHeaderState.handleOffsetUpdate(
                            offset: offset,
                            forTab: tab,
                            isSelectedTab: tab == viewModel.selectedTab
                        )
                    }
                )
            } else {
                let _ = assertionFailure(
                    "Unexpectedly asked show a roster tab for an invalid entity"
                )
                EmptyView()
            }

        case .bracket:
            if let bracketEntityDetails {
                LeagueBracketDetail(
                    viewModel: LeagueBracketViewModel(
                        network: network,
                        leagueId: bracketEntityDetails.leagueId,
                        leagueCode: bracketEntityDetails.leagueCode,
                        seasonId: viewModel.leagueSeasonId,
                        teamId: viewModel.teamId,
                        analyticsDefaults: AnalyticDefaults(),
                        tbdString: AthleticScoresFoundation.Strings.tbd.localized,
                        getGamePhase: { game in
                            GamePhase(
                                statusCode: game.status,
                                startedAt: game.startedAt
                            )?.asTournamentGamePhase
                        }
                    ),
                    liveString: AthleticScoresFoundation.Strings.live.localized,
                    onTournamentCellTap: { viewModel, tile in
                        if case .confirmed = tile.homeTeam, case .confirmed = tile.awayTeam {
                            switch tile.data {
                            case .series(let series):
                                let tileIndex = viewModel.rounds?.findIndexForTile(
                                    withId: tile.id
                                )
                                guard let tileIndex else { return }

                                navigatedSeries = NavigatedSeries(
                                    id: series.id,
                                    viewModel: viewModel,
                                    tileIndex: tileIndex
                                )
                            case .game(let game):
                                self.viewModel.trackTournamentGameTap(game: game)
                                navigation.addScreenToSelectedTab(
                                    .scores(.boxScore(BoxScoreDestination(gameId: game.id)))
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    private var bracketEntityDetails:
        (leagueCode: GQL.LeagueCode, leagueId: String, teamId: String?)?
    {

        let leagueCode: GQL.LeagueCode?
        let leagueId: String?
        let teamId: String?

        let entity = viewModel.entity
        switch entity.entityType {
        case .league:
            leagueCode = entity.leagueCode
            leagueId = entity.legacyId
            teamId = nil
        case .team:
            leagueCode = entity.associatedLeagueGqlId.flatMap({ .init(rawValue: $0) })
            leagueId = entity.associatedLeagueLegacyId
            teamId = entity.gqlId
        default:
            return nil
        }

        guard let leagueCode, let leagueId else {
            return nil
        }

        return (
            leagueCode: leagueCode,
            leagueId: leagueId,
            teamId: teamId
        )
    }
}

extension Array where Element == TournamentRound {
    fileprivate func findIndexForTile(withId id: String) -> BracketSeries.TileIndex? {
        for (roundIndex, round) in self.enumerated() {
            for (tileIndex, roundTile) in round.tiles.enumerated() {
                if roundTile.tile.id == id {
                    return BracketSeries.TileIndex(
                        round: roundIndex,
                        tile: tileIndex
                    )
                }
            }
        }
        return nil
    }
}

extension GamePhase {
    fileprivate var asTournamentGamePhase: TournamentTile.Phase {
        switch self {
        case .nonStarter, .preGame:
            return .preGame
        case .inGame:
            return .inGame
        case .postGame:
            return .postGame
        }
    }
}

struct HubDetail_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            HubDetail(
                entity: .init(
                    legacyId: "110",
                    name: "Mets",
                    shortName: "Mets",
                    shortDisplayName: "Mets",
                    longDisplayName: "New York Mets",
                    color: "012f6c",
                    iconColor: "053d88",
                    type: .team,
                    imageUrl: "https://cdn-team-logos.theathletic.com/team-logo-110-96x96.png"
                        .url
                )
            )
        }
        .environmentObject(AppEnvironment.shared.user)
    }
}

private struct HasStandingContainerAutoScrolledKey: EnvironmentKey {
    static var defaultValue: Binding<Bool> = .constant(false)
}

extension EnvironmentValues {
    var hasStandingContainerAutoScrolled: Binding<Bool> {
        get { self[HasStandingContainerAutoScrolledKey.self] }
        set { self[HasStandingContainerAutoScrolledKey.self] = newValue }
    }
}

extension View {
    fileprivate func hasStandingContainerAutoScrolled(_ hasAutoScrolled: Binding<Bool>) -> some View
    {
        environment(\.hasStandingContainerAutoScrolled, hasAutoScrolled)
    }
}
