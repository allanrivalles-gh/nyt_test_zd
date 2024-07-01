//
//  TeamStatsDetail.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 19/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloNetworking
import AthleticFoundation
import AthleticUI
import Foundation
import SwiftUI

struct TeamStatsDetail: View {
    @StateObject var viewModel: TeamStatsViewModel
    let onScrollOffsetChanged: (CGFloat) -> Void

    @EnvironmentObject private var network: NetworkModel

    var body: some View {
        VStack {
            if let sections = viewModel.sections, !sections.isEmpty {
                Content(
                    picker: viewModel.picker,
                    sections: sections,
                    onScrollOffsetChanged: onScrollOffsetChanged,
                    reloadData: {
                        await viewModel.fetchDataIfNecessary(network: network)
                    }
                )
            } else if viewModel.state == .loaded {
                NoContent(
                    title: Strings.playerStatsNoDataTitle.localized,
                    subtitle: Strings.playerStatsNoDataBody.localized
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
        .onChange(of: viewModel.selectedPage) { _ in
            viewModel.trackPageView()
        }
    }
}

private struct Content: View {
    let picker: SegmentedPickerViewModel<TeamStatsViewModel.PageOption>?
    let sections: [TeamStatsViewModel.Section]
    let onScrollOffsetChanged: (CGFloat) -> Void
    let reloadData: () async -> Void

    private let scrollViewNamespace = "team-stats-scroll-view"

    var body: some View {
        RefreshableScrollView {
            await reloadData()
        } content: {
            VStack(spacing: 0) {
                if let picker = picker {
                    SegmentedPicker(viewModel: picker)
                        .padding([.horizontal, .top], 16)
                        .background(Color.chalk.dark200)
                }
                VStack(spacing: 6) {
                    let _ = DuplicateIDLogger.logDuplicates(in: sections)
                    ForEach(sections) { section in
                        switch section {
                        case let .leaders(viewModel):
                            TeamSeasonStatsLeadersSection(viewModel: viewModel)
                                .padding(.horizontal, 16)

                        case let .seasonStats(viewModel):
                            TeamSeasonStatsSection(viewModel: viewModel)
                                .padding(.horizontal, 16)

                        case let .playerStats(viewModel):
                            PlayerStatsTable(viewModel: viewModel.table)
                        }
                    }
                    .background(Color.chalk.dark200)
                }
            }
            .padding(.bottom, 40)
            .getVerticalOffset(
                in: .named(scrollViewNamespace),
                perform: onScrollOffsetChanged
            )
        }
        .coordinateSpace(name: scrollViewNamespace)
    }
}
