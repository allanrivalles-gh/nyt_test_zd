//
//  GamePlayerGradesSportViewModel.swift
//  theathletic-ios
//
//  Created by Duncan Lau on 29/11/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticScoresFoundation
import Foundation

protocol GamePlayerGradesSportViewModel: Actor {
    var state: LoadingState { get }
    var stateUpdates: Published<LoadingState>.Publisher { get }
    var items: [AnyIdentifiable] { get }
    var itemsUpdates: Published<[AnyIdentifiable]>.Publisher { get }

    func prepareBindings()
    func loadData(isInitialLoad: Bool) async
    func stopLiveUpdates() async

    func select(teamId: String)
    func trackView()
}

struct PlayerGradesBasicTeamInfo {
    let id: String?
    let displayName: String?
}

extension GamePlayerGradesSportViewModel {

    func items(
        from viewModel: GamePlayerGradesTeamViewModel,
        firstTeamInfo: PlayerGradesBasicTeamInfo,
        secondTeamInfo: PlayerGradesBasicTeamInfo,
        selectedTeamId: String
    ) -> [AnyIdentifiable] {
        guard let firstTeamId = firstTeamInfo.id, let secondTeamId = secondTeamInfo.id else {
            return []
        }

        let pickerViewModel = SegmentedPickerViewModel(
            options: [
                SegmentedPickerViewModel.Option(
                    id: firstTeamId,
                    title: firstTeamInfo.displayName
                        ?? AthleticScoresFoundation.Strings.tbd.localized
                ),
                SegmentedPickerViewModel.Option(
                    id: secondTeamId,
                    title: secondTeamInfo.displayName
                        ?? AthleticScoresFoundation.Strings.tbd.localized
                ),
            ],
            selectedId: selectedTeamId
        )
        let headerSection = GamePlayerGradesHeaderSectionViewModel(
            id: "grades-segmented-control",
            segmentedControl: BoxScoreSegmentedControlViewModel(
                picker: pickerViewModel,
                selectId: { [weak self] id in
                    guard let self = self else { return }
                    Task {
                        await self.select(teamId: id)
                    }
                }
            )
        )

        if viewModel.players.isEmpty {
            let contentSection =
                GamePlayerGradesNoContentSectionViewModel(
                    id: "grades-no-content",
                    content: NoContentViewModel(title: Strings.gradesNotAvailable.localized)
                )

            return [
                AnyIdentifiable(headerSection),
                AnyIdentifiable(contentSection),
            ]
        } else {
            return [
                AnyIdentifiable(headerSection),
                AnyIdentifiable(viewModel),
            ]
        }
    }

    static func makePlayerGradesTeamViewModel(
        from gameTeam: GQL.GamePlayerGradesTeam,
        gameId: String,
        leagueCode: GQL.LeagueCode,
        analytics: PlayerGradesAnalyticsTracker,
        gradeStatus: GradeStatus?,
        gradesStore: PlayerGradesDataStore
    ) -> GamePlayerGradesTeamViewModel? {
        return GamePlayerGradesTeamViewModel(
            gameTeam: gameTeam,
            gameId: gameId,
            leagueCode: leagueCode,
            analytics: analytics,
            gradeStatus: gradeStatus,
            gradesStore: gradesStore
        )
    }
}
