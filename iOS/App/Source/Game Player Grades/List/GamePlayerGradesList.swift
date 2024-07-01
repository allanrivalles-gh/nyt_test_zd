//
//  GamePlayerGradesList.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 25/11/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

struct GamePlayerGradesList: View {
    @ObservedObject var viewModel: GamePlayerGradesViewModel
    let didPullToRefresh: VoidClosure

    @State private var presentedPlayer: GamePlayerID?

    private var hasLoaded: Bool {
        !viewModel.items.isEmpty
    }

    var body: some View {
        content
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.chalk.dark100.ignoresSafeArea(.all, edges: .bottom))
            .overlay(
                Group {
                    if viewModel.items.isEmpty {
                        EmptyContent(state: viewModel.state) {
                            Task {
                                await viewModel.loadData(isInitialLoad: true)
                            }
                        }
                    }
                }
            )
            .task {
                guard !hasLoaded else {
                    return
                }

                await viewModel.loadData(isInitialLoad: true)
            }
            .onForeground {
                guard hasLoaded else { return }
                viewModel.appWillForeground()
            }
            .onBackground {
                guard hasLoaded else { return }
                viewModel.appDidBackground()
            }
            .task {
                await viewModel.trackView()
            }
    }

    @ViewBuilder
    private var content: some View {
        RefreshableScrollView {
            didPullToRefresh()
            await viewModel.loadData()
        } content: {
            VStack(spacing: 0) {
                let _ = DuplicateIDLogger.logDuplicates(in: viewModel.items)
                ForEach(viewModel.items) {
                    item(for: $0)
                        .background(Color.chalk.dark200)
                        .withGlobalSideMargins()
                        .background(Color.chalk.dark100)
                }
            }
            .padding(.top, BoxScoreUIConstant.topContentInset)
            .padding(.bottom, 40)
        }
    }

    @ViewBuilder
    private func item(for viewModel: AnyIdentifiable) -> some View {
        switch viewModel.base {
        case let viewModel as GamePlayerGradesHeaderSectionViewModel:
            SegmentedPicker(viewModel: viewModel.segmentedControl.picker)
                .padding(.top, 8)
                .padding(.horizontal, 16)

        case let viewModel as GamePlayerGradesNoContentSectionViewModel:
            NoContentRow(viewModel: viewModel.content)
                .padding(.horizontal, 16)

        case let viewModel as GamePlayerGradesTeamViewModel:
            LazyVStack(spacing: 0) {
                let _ = DuplicateIDLogger.logDuplicates(in: viewModel.players)
                ForEach(viewModel.players) { playerViewModel in
                    GamePlayerGradesPlayerRow(
                        viewModel: playerViewModel,
                        presentedPlayer: $presentedPlayer,
                        teamLogos: viewModel.teamLogos,
                        iconColor: viewModel.iconColor,
                        isGradingLocked: viewModel.isGradingLocked,
                        analyticsSourceView: .gradePlayersGradesTab
                    )
                    // Note: Workaround fix until Apple fixes the implementation of .sheet().
                    // Button disabled until presentedPlayer is set to nil by SwiftUI.
                    // Also refer to this PR: https://github.com/TheAthletic/iOS/pull/3764
                    .disabled(presentedPlayer != nil)
                    DividerView()
                }
            }
            .padding(.horizontal, 16)
            .deeplinkListeningSheet(item: $presentedPlayer) {
                PlayerGradeDetailPagingView(
                    viewModel: viewModel.makePagingDetailViewModel(
                        selectedGamePlayerId: $0,
                        gameId: self.viewModel.gameId
                    )
                )
            }
        default:
            fatalError("Encountered unsupported item \(viewModel.base)")
        }
    }
}

extension GamePlayerID: Identifiable {
    public var id: AnyHashable {
        self
    }
}
