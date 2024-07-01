//
//  LeagueBracketDetail.swift
//  theathletic-ios
//
//  Created by Jason Xu on 10/29/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import SwiftUI

public struct LeagueBracketDetail: View {

    public typealias TournamentCellTap = ((LeagueBracketViewModel, TournamentTile) -> Void)

    @StateObject private var viewModel: LeagueBracketViewModel
    @State private var dataFetched = false

    private let liveString: String
    private let onTournamentCellTap: TournamentCellTap?

    public init(
        viewModel: @escaping @autoclosure () -> LeagueBracketViewModel,
        liveString: String,
        onTournamentCellTap: TournamentCellTap? = nil
    ) {
        self._viewModel = StateObject(wrappedValue: viewModel())
        self.liveString = liveString
        self.onTournamentCellTap = onTournamentCellTap
    }

    public var body: some View {
        VStack(spacing: 0) {
            switch viewModel.loadingState {
            case .loading:
                ProgressView()
                    .progressViewStyle(.athletic)
            case .failed:
                Button(Strings.reload.localized) {
                    Task {
                        await viewModel.load()
                    }
                }
                .buttonStyle(.core(size: .small, level: .secondary))
            case let .loaded(tabs, rounds):
                BracketRoundTabBarView(
                    viewModel: viewModel,
                    tabs: tabs,
                    selectedTabIndex: $viewModel.selectedTabOffset
                )
                TournamentGamesBrackets(
                    offset: $viewModel.selectedTabOffset,
                    stages: rounds.map { stage in
                        TournamentStage(
                            id: stage.tab.id,
                            groups: stage.tiles.splitIntoGroups(leagueCode: viewModel.leagueCode),
                            connected: stage.connected
                        )
                    },
                    tbdString: viewModel.tbdString,
                    liveString: liveString,
                    refresh: { await viewModel.load(isRefresh: true) },
                    onCellTap: onTournamentCellTap.map { onTournamentCellTap in
                        { (tile: TournamentTile) in onTournamentCellTap(viewModel, tile) }
                    }
                )
            default:
                EmptyView()
            }
        }
        .task {
            await viewModel.load(ignoreIfLoaded: true)
        }
        .onAppear {
            Analytics.track(
                event: .init(
                    verb: .view,
                    view: .brackets,
                    objectType: .leagueId,
                    objectIdentifier: viewModel.leagueId,
                    requiredValues: viewModel.analyticsDefaults
                )
            )
        }
    }
}

extension Array where Element == TournamentRoundTile {
    private static let logger = ATHLogger(category: .tournaments)

    /// this method creates a new group when the last game group is not the same as the current game group
    fileprivate func splitIntoGroups(leagueCode: GQL.LeagueCode) -> [TournamentStageGroup] {
        var lastGroupName: String?
        var tilesInGroup = [TournamentTile]()
        var groups = [TournamentStageGroup]()

        func moveTilesToGroupIfAny() {
            if !tilesInGroup.isEmpty {
                groups.append(TournamentStageGroup(title: lastGroupName, tiles: tilesInGroup))
            }
        }

        for roundTile in self {
            if roundTile.conferenceName != lastGroupName {
                moveTilesToGroupIfAny()
                tilesInGroup = [roundTile.tile]
            } else {
                tilesInGroup.append(roundTile.tile)
            }
            lastGroupName = roundTile.conferenceName
        }

        moveTilesToGroupIfAny()

        return groups
    }
}

struct LeagueBracketDetail_Previews: PreviewProvider {
    static var previews: some View {
        LeagueBracketDetail(
            viewModel: LeagueBracketViewModel(
                network: BracketsPreviewHelper.network,
                leagueId: LeagueId.WOMENS_EUROS.rawValue,
                leagueCode: GQL.LeagueCode.uwc,
                seasonId: nil,
                teamId: nil,
                analyticsDefaults: PreviewAnalyticDefaults(),
                tbdString: "TBD",
                getGamePhase: { _ in nil }
            ),
            liveString: "Live"
        )
        .loadCustomFonts()
    }
}
