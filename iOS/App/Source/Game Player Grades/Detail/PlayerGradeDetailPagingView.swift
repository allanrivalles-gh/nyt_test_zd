//
//  PlayerGradeDetailPagingView.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 13/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

struct PlayerGradeDetailPagingView: View {

    private enum Constants {
        static let arrowsTopOffsetRatio: CGFloat = 230 / 768
    }

    @Environment(\.dismiss) private var dismiss
    @Environment(\.gameSelectTab) private var gameSelectTab

    @StateObject var viewModel: PlayerGradeDetailPagingViewModel

    @State private var closeButtonHeight: CGFloat = 0
    @State private var pageStatsExpansions: [PlayerGradeDetailPagingViewModel.Page: Bool] = [:]
    @State private var paginationOpacity: CGFloat = 1
    @State private var showUngradedToast: Bool = false
    @State private var shouldShowUngradedToast: Bool = false

    var body: some View {
        TabView(selection: $viewModel.selectedPage) {
            let _ = DuplicateIDLogger.logDuplicates(in: viewModel.pages)
            ForEach(viewModel.pages) {
                item(for: $0)
                    .tag($0)
            }

        }
        .tabViewStyle(.page(indexDisplayMode: .always))
        .background(Color.chalk.dark200.edgesIgnoringSafeArea(.all))
        .environmentObject(viewModel.gameInfo)
        .overlay(
            Button(action: { dismiss() }) {
                CloseIcon()
            }
            .getSize { size in
                closeButtonHeight = size.height
            },
            alignment: .topLeading
        )
        .overlay(
            GeometryReader { geometry in
                VStack {
                    let preferredOffset = geometry.size.height * Constants.arrowsTopOffsetRatio
                    paginationArrows
                        .padding(.top, min(max(closeButtonHeight, preferredOffset), 230))
                        .opacity(paginationOpacity)

                    Spacer()
                }
            }
        )
        .onChange(of: pageStatsExpansions) { newValue in
            updatePaginationOpacity(expansions: newValue, selectedPage: viewModel.selectedPage)
        }
        .onChange(of: viewModel.selectedPage) { newValue in
            shouldShowUngradedToast = false
            updatePaginationOpacity(expansions: pageStatsExpansions, selectedPage: newValue)
        }
        .toast(
            Strings.gradeDeleted.localized,
            systemImage: "minus.circle.fill",
            isPresented: $showUngradedToast
        )
        .teamColor(viewModel.teamColor)
        .teamForegroundColor(viewModel.teamForegroundColor)
        .teamInvertedForegroundColor(viewModel.teamInvertedForegroundColor)
    }

    @ViewBuilder
    private func item(for page: PlayerGradeDetailPagingViewModel.Page) -> some View {
        switch page {
        case let .playerGradeDetail(viewModel):
            PlayerGradeDetailView(
                viewModel: viewModel,
                showGradesTab: {
                    self.viewModel.trackSeeAllGradesClick()

                    dismiss()
                    gameSelectTab(.playerGrades(.placeholder))
                },
                pageStatsExpansions: $pageStatsExpansions,
                shouldShowUngradedToast: $shouldShowUngradedToast
            )

        case let .allGraded(viewModel):
            PlayerGradeAllGradedView(viewModel: viewModel)
        }
    }

    @ViewBuilder
    private var paginationArrows: some View {
        HStack {
            let isFirstPage = viewModel.selectedPage == viewModel.pages.first
            let isLastPage = viewModel.selectedPage == viewModel.pages.last

            Button(action: { viewModel.goToPreviousPage() }) {
                DirectionArrow(direction: .left)
            }
            .opacity(isFirstPage ? 0.3 : 1)
            .disabled(isFirstPage)

            Spacer()

            Button(action: { viewModel.goToNextPage() }) {
                DirectionArrow(direction: .right)
            }
            .opacity(isLastPage ? 0.3 : 1)
            .disabled(isLastPage)
        }
        .padding(.horizontal, 16)
    }

    private func updatePaginationOpacity(
        expansions: [PlayerGradeDetailPagingViewModel.Page: Bool],
        selectedPage: PlayerGradeDetailPagingViewModel.Page
    ) {
        withAnimation {
            paginationOpacity = expansions[selectedPage] != true ? 1 : 0
        }
    }
}

private struct CloseIcon: View {

    @Environment(\.teamForegroundColor) private var teamForegroundColor

    var body: some View {
        Image(systemName: "xmark")
            .font(Font.title3.weight(.medium))
            .foregroundColor(teamForegroundColor)
            .padding(24)
    }
}

private struct DirectionArrow: View {
    enum Direction {
        case left
        case right
    }

    let direction: Direction

    @Environment(\.teamForegroundColor) private var teamForegroundColor
    @Environment(\.teamInvertedForegroundColor) private var teamInvertedForegroundColor

    var body: some View {
        Circle()
            .fill(teamForegroundColor)
            .frame(width: 30, height: 30)
            .overlay(
                Chevron(
                    foregroundColor: teamInvertedForegroundColor,
                    width: 10,
                    height: 14,
                    direction: chevronDirection
                )
            )
    }

    private var chevronDirection: Chevron.Direction {
        switch direction {
        case .left: return .left
        case .right: return .right
        }
    }
}

struct PlayerGradeDetailPagingView_Previews: PreviewProvider {

    static var previews: some View {
        let gradesStore = PlayerGradesDataStore()
        let page1 = PlayerGradeDetailView_Previews.mockViewModel(
            playerId: "1",
            isLocked: false,
            userGrade: nil
        )
        let page2 = PlayerGradeDetailView_Previews.mockViewModel(
            playerId: "2",
            isLocked: false,
            userGrade: 2
        )
        let page3 = PlayerGradeDetailView_Previews.mockViewModel(
            playerId: "3",
            isLocked: true,
            userGrade: 2
        )

        Color.black
            .sheet(isPresented: .constant(true)) {
                PlayerGradeDetailPagingView(
                    viewModel: .init(
                        playerPages: [page1, page2, page3],
                        selectedPage: page2,
                        gameId: "abc123",
                        leagueCode: .nfl,
                        teamColor: .chalk.red,
                        isGradingLocked: false,
                        entryPoint: .gradesTab,
                        analytics: PlayerGradesAnalyticsTracker(gameId: "abc123", leagueCode: .nfl),
                        gradesStore: gradesStore
                    )
                )
                .preferredColorScheme(.dark)
            }
    }
}

private struct TeamColorKey: EnvironmentKey {
    static var defaultValue: Color = .chalk.dark300
}

private struct TeamForegroundColorKey: EnvironmentKey {
    static var defaultValue: Color = .chalk.constant.gray800
}

private struct TeamInvertedForegroundColorKey: EnvironmentKey {
    static var defaultValue: Color = .chalk.constant.gray100
}

extension EnvironmentValues {
    var teamColor: Color {
        get { self[TeamColorKey.self] }
        set { self[TeamColorKey.self] = newValue }
    }

    var teamForegroundColor: Color {
        get { self[TeamForegroundColorKey.self] }
        set { self[TeamForegroundColorKey.self] = newValue }
    }

    var teamInvertedForegroundColor: Color {
        get { self[TeamInvertedForegroundColorKey.self] }
        set { self[TeamInvertedForegroundColorKey.self] = newValue }
    }
}

extension View {
    public func teamColor(_ color: Color) -> some View {
        environment(\.teamColor, color)
    }

    public func teamForegroundColor(_ color: Color) -> some View {
        environment(\.teamForegroundColor, color)
    }

    public func teamInvertedForegroundColor(_ color: Color) -> some View {
        environment(\.teamInvertedForegroundColor, color)
    }
}
