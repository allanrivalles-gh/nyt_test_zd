//
//  TeamSeasonStatsSection.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 19/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import Foundation
import SwiftUI

struct TeamSeasonStatsSection: View {
    let viewModel: TeamSeasonStatsViewModel

    var body: some View {
        VStack(spacing: 0) {
            BoxScoreSectionHeaderRow(title: viewModel.title)
            Grid(
                alignment: .leading,
                horizontalSpacing: 8,
                verticalSpacing: 16
            ) {
                let _ = DuplicateIDLogger.logDuplicates(in: viewModel.items)
                ForEach(viewModel.items) { viewModel in
                    if viewModel.hasTopDivider {
                        DividerView()
                            .gridCellUnsizedAxes(.horizontal)
                    }

                    GridRow {
                        Text(viewModel.title)
                            .fontStyle(.calibreUtility.l.regular)
                            .foregroundColor(
                                viewModel.isTitleDimmed ? .chalk.dark500 : .chalk.dark700
                            )
                            .frame(maxWidth: .infinity, alignment: .leading)

                        TableValue(viewModel: viewModel)
                    }
                }
            }
        }
        .padding(.bottom, 24)
    }
}

private struct TableValue: View {
    let viewModel: TeamSeasonStatsViewModel.Item

    var body: some View {
        HStack(spacing: 0) {
            Text(viewModel.value)
                .fontStyle(.calibreUtility.l.medium)
                .foregroundColor(.chalk.dark700)
            if let suffix = viewModel.suffix {
                Text(suffix)
                    .fontStyle(.calibreUtility.xs.regular)
                    .foregroundColor(.chalk.dark500)
                    .padding(.leading, 4)
            }
        }
    }
}

struct TeamSeasonStatsSection_Previews: PreviewProvider {
    static var previews: some View {
        content
            .preferredColorScheme(.light)
        content
            .preferredColorScheme(.dark)
    }

    static var content: some View {
        GeometryReader { geometry in
            VStack(spacing: 0) {
                TeamSeasonStatsSection(viewModel: viewModel)
                Spacer()
            }
            .padding(16)
        }
    }

    static var viewModel: TeamSeasonStatsViewModel {
        TeamSeasonStatsViewModel(
            id: "season-stats",
            title: "season stats",
            items: [
                TeamSeasonStatsViewModel.Item(
                    id: "1",
                    title: "Points Per Game",
                    value: "1125.8",
                    suffix: "12th",
                    isTitleDimmed: false,
                    hasTopDivider: true
                ),
                TeamSeasonStatsViewModel.Item(
                    id: "2",
                    title: "Rebounds",
                    value: "22.2",
                    suffix: nil,
                    isTitleDimmed: false,
                    hasTopDivider: true
                ),
                TeamSeasonStatsViewModel.Item(
                    id: "3",
                    title: "Offensive Rebounds",
                    value: "7.8",
                    suffix: "32nd",
                    isTitleDimmed: true,
                    hasTopDivider: false
                ),
                TeamSeasonStatsViewModel.Item(
                    id: "4",
                    title: "Steals Per Game",
                    value: "4.8",
                    suffix: nil,
                    isTitleDimmed: false,
                    hasTopDivider: true
                ),
            ]
        )
    }
}
