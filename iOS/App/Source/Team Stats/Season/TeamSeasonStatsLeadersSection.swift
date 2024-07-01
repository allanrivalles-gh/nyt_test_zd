//
//  TeamSeasonStatsLeadersSection.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 22/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import Foundation
import SwiftUI

struct TeamSeasonStatsLeadersSection: View {
    let viewModel: TeamSeasonStatsLeadersViewModel

    var body: some View {
        VStack(spacing: 0) {
            BoxScoreSectionHeaderRow(title: viewModel.title)
            let _ = DuplicateIDLogger.logDuplicates(in: viewModel.sections)
            ForEach(viewModel.sections) { section in
                Section(viewModel: section)
            }
        }
    }
}

private struct Section: View {
    let viewModel: TeamSeasonStatsLeadersViewModel.Section

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            if let title = viewModel.title {
                Text(title)
                    .fontStyle(.calibreUtility.xs.medium)
                    .foregroundColor(.chalk.dark500)
                    .padding(.top, 8)
            }
            let _ = DuplicateIDLogger.logDuplicates(in: viewModel.items)
            ForEach(viewModel.items) { item in
                if item.id != viewModel.items.first?.id {
                    DividerView()
                }
                Row(viewModel: item, hasHeadshots: viewModel.hasHeadshots)
            }
        }
    }
}

private struct Row: View {
    @EnvironmentObject private var compass: Compass
    @EnvironmentObject private var hubViewModel: HubViewModel

    let viewModel: TeamSeasonStatsLeadersViewModel.Item
    let hasHeadshots: Bool

    var body: some View {
        HStack(spacing: 0) {
            HStack(spacing: 8) {
                if compass.config.flags.isPlayerHubsEnabled {
                    NavigationLink(
                        screen: .scores(
                            .playerHub(
                                player: .id(
                                    playerId: viewModel.playerId,
                                    leagueString: hubViewModel.entity.associatedLeagueGqlId
                                ),
                                colors: hubViewModel.colors
                            )
                        )
                    ) {
                        playerInfo
                    }
                    .trackClick(viewModel: viewModel)
                } else {
                    playerInfo
                }
            }
            Spacer(minLength: 8)
            VStack(spacing: 0) {
                Text(viewModel.value.text)
                    .fontStyle(.calibreUtility.l.medium)
                    .foregroundColor(.chalk.dark700)
                if let subtext = viewModel.value.subtext {
                    Text(subtext)
                        .fontStyle(.calibreUtility.l.regular)
                        .foregroundColor(.chalk.dark500)
                }
            }
            .frame(width: 72)
        }
        .padding(.vertical, 8)
    }

    @ViewBuilder
    private var playerInfo: some View {
        if hasHeadshots {
            HeadshotLazyImage(
                size: 32,
                resources: viewModel.iconResources,
                contentMode: .fill,
                backgroundColor: viewModel.iconHex
                    .map { Color(hex: $0) }
                    ?? .chalk.dark300
            )
        }
        Text(viewModel.title.text)
            .fontStyle(.calibreUtility.l.regular)
            .foregroundColor(.chalk.dark800)
        if let subtext = viewModel.title.subtext {
            Text(subtext)
                .fontStyle(.calibreUtility.l.regular)
                .foregroundColor(.chalk.dark500)
        }
    }
}
