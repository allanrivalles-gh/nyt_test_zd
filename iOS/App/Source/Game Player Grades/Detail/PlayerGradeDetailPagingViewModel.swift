//
//  PlayerGradeDetailPagingViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 13/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticUI
import Combine
import Foundation
import SwiftUI

final class PlayerGradeDetailPagingViewModel: ObservableObject {

    enum Page: Identifiable, Hashable {
        case playerGradeDetail(PlayerGradeDetailViewModel)
        case allGraded(PlayerGradeAllGradedViewModel)

        var id: AnyHashable {
            switch self {
            case .allGraded(let model):
                return model.id
            case .playerGradeDetail(let model):
                return model.id
            }
        }
    }

    let gameInfo: GradesDetailGameViewModel

    var pages: [Page] {
        hasGradedAllPlayers && !isGradingLocked
            ? playerPages + [allGradedPage]
            : playerPages
    }

    @Published var selectedPage: Page
    @Published private var hasGradedAllPlayers = false

    private let playerPages: [Page]
    private let gameId: String
    private let leagueCode: GQL.LeagueCode
    private let isGradingLocked: Bool
    private let analytics: PlayerGradesAnalyticsTracker
    private let gradesStore: PlayerGradesDataStore
    private var isPagingButtonActive: Bool = false
    private var hasGradedAllPlayersCancellable: AnyCancellable?
    private var pageTrackingCancellable: AnyCancellable?
    private var pageViewCancellable: AnyCancellable?

    private let entryPoint: PlayerGradesEntryPoint

    private lazy var allGradedPage: Page = .allGraded(
        PlayerGradeAllGradedViewModel(id: "all-graded")
    )

    let teamColor: Color
    let teamForegroundColor: Color
    let teamInvertedForegroundColor: Color

    init(
        playerPages: [PlayerGradeDetailViewModel],
        selectedPage: PlayerGradeDetailViewModel,
        gameId: String,
        leagueCode: GQL.LeagueCode,
        teamColor: Color?,
        isGradingLocked: Bool,
        entryPoint: PlayerGradesEntryPoint,
        analytics: PlayerGradesAnalyticsTracker,
        gradesStore: PlayerGradesDataStore,
        network: GamePlayerGradesNetworking & LiveUpdatesNetworking = AppEnvironment.shared.network
    ) {
        gameInfo = GradesDetailGameViewModel(gameId: gameId, network: network)
        self.playerPages = playerPages.map { .playerGradeDetail($0) }
        self.gameId = gameId
        self.leagueCode = leagueCode
        self.isGradingLocked = isGradingLocked
        self.entryPoint = entryPoint
        self.analytics = analytics
        self.gradesStore = gradesStore
        _selectedPage = Published(wrappedValue: .playerGradeDetail(selectedPage))

        let teamColor = teamColor ?? .chalk.dark200

        let foregroundColor = Color.highContrastAppearance(
            of: .chalk.dark800,
            forBackgroundColor: teamColor
        )

        let invertedForegroundColor = Color.highContrastAppearance(
            of: .chalk.dark800,
            forBackgroundColor: foregroundColor
        )

        self.teamColor = teamColor
        self.teamForegroundColor = foregroundColor
        self.teamInvertedForegroundColor = invertedForegroundColor

        hasGradedAllPlayersCancellable = gradesStore.publisherForHasGradedAllPlayers(
            for: playerPages.map { $0.playerId }
        )
        .sink { [weak self] hasGradedAllPlayers in
            self?.hasGradedAllPlayers = hasGradedAllPlayers
        }

        pageTrackingCancellable =
            $selectedPage
            .dropFirst()
            .removeDuplicates()
            .sink { [weak self] page in
                self?.trackPageChange(
                    destination: page,
                    gameId: gameId,
                    leagueCode: leagueCode
                )
            }

        pageViewCancellable = $selectedPage.removeDuplicates().sink { page in
            switch page {
            case .playerGradeDetail(let viewModel):
                viewModel.trackView()
            case .allGraded:
                return
            }
        }
    }

    func goToNextPage() {
        guard let currentIndex = pages.firstIndex(of: selectedPage) else {
            return
        }

        let nextIndex = pages.index(after: currentIndex)
        guard nextIndex < pages.endIndex else {
            return
        }

        isPagingButtonActive = true
        withAnimation {
            selectedPage = pages[nextIndex]
        }
        isPagingButtonActive = false
    }

    func goToPreviousPage() {
        guard let currentIndex = pages.firstIndex(of: selectedPage) else {
            return
        }

        let previousIndex = pages.index(before: currentIndex)
        guard previousIndex >= pages.startIndex else {
            return
        }

        isPagingButtonActive = true
        withAnimation {
            selectedPage = pages[previousIndex]
        }
        isPagingButtonActive = false
    }

    func trackSeeAllGradesClick() {
        Task {
            switch entryPoint {
            case .gameTab:
                await analytics.clickSeeAllPlayerGradesButtonInGameTabFlow()
            case .gradesTab:
                await analytics.clickSeeAllPlayerGradesOnGradesTabFlowInUnlockedState()
            }
        }
    }

    private func trackPageChange(
        destination: Page,
        gameId: String,
        leagueCode: GQL.LeagueCode
    ) {
        guard
            let oldIndex = pages.firstIndex(of: selectedPage),
            let newIndex = pages.firstIndex(of: destination),
            oldIndex != newIndex
        else {
            return
        }

        /// Capture the current bool value before proceeding with async work
        let isPagingButtonActive = isPagingButtonActive

        Task {
            switch entryPoint {
            case .gameTab:
                if oldIndex < newIndex {
                    if isPagingButtonActive {
                        await analytics.clickNextOnGradePlayerGameTabFlow()
                    } else {
                        await analytics.swipeNextOnGradePlayerGameTabFlow()
                    }
                } else {
                    if isPagingButtonActive {
                        await analytics.clickPreviousOnGradePlayerGameTabFlow()
                    } else {
                        await analytics.swipePreviousOnGradePlayerGameTabFlow()
                    }
                }

            case .gradesTab:
                if oldIndex < newIndex {
                    if isPagingButtonActive {
                        await analytics.clickNextOnGradePlayerGradesTabFlow()
                    } else {
                        await analytics.swipeNextOnGradePlayerGradesTabFlow()
                    }
                } else {
                    if isPagingButtonActive {
                        await analytics.clickPreviousOnGradePlayerGradesTabFlow()
                    } else {
                        await analytics.swipePreviousOnGradePlayerGradesTabFlow()
                    }
                }
            }
        }
    }
}

extension PlayerGradeDetailPagingViewModel: Identifiable {
    var id: AnyHashable {
        pages.map { $0.id }
    }
}
