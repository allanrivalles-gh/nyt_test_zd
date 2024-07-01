//
//  TeamRosterDetail.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 29/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloNetworking
import AthleticUI
import Foundation
import SwiftUI

struct TeamRosterDetail: View {
    @StateObject var viewModel: TeamRosterViewModel
    let onScrollOffsetChanged: (CGFloat) -> Void

    @EnvironmentObject private var network: NetworkModel

    var body: some View {
        VStack {
            if let table = viewModel.table {
                Content(
                    viewModel: table,
                    onScrollOffsetChanged: onScrollOffsetChanged,
                    reloadData: {
                        await viewModel.fetchDataIfNecessary(network: network)
                    }
                )
            } else if viewModel.state == .loaded {
                NoContent(
                    title: Strings.teamRosterNoDataTitle.localized,
                    subtitle: Strings.teamRosterNoDataBody.localized
                )
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.chalk.dark100.ignoresSafeArea(.all, edges: .bottom))
        .overlay(
            EmptyContent(state: viewModel.state) {
                Task {
                    await viewModel.fetchDataIfNecessary(network: network)
                }
            }
        )
        .onAppear {
            Task {
                await viewModel.fetchDataIfNecessary(network: network)
            }

            viewModel.trackPageView()
        }
    }
}

private struct Content: View {
    let viewModel: PlayerStatsTableViewModel<TeamRosterStat>
    let onScrollOffsetChanged: (CGFloat) -> Void
    let reloadData: () async -> Void

    private let scrollViewNamespace = "team-roster-scroll-view"

    var body: some View {
        RefreshableScrollView {
            await reloadData()
        } content: {
            PlayerStatsTable(viewModel: viewModel)
                .background(Color.chalk.dark200)
                .padding(.bottom, 40)
                .getVerticalOffset(
                    in: .named(scrollViewNamespace),
                    perform: onScrollOffsetChanged
                )
        }
        .coordinateSpace(name: scrollViewNamespace)
    }
}
