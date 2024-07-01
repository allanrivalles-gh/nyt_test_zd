//
//  GamePlayRowType.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 21/6/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import Foundation

enum GamePlayRowType: Identifiable, Equatable {
    case header(PlayByPlayHeaderViewModel)
    case basicHeader(PlayByPlayBasicHeaderViewModel)
    case teamHeader(PlayByPlayTeamHeaderViewModel)

    case general(GamePlayViewModel)
    case soccerShootoutHeader(PlayByPlaySoccerShootoutHeaderViewModel)
    case soccerShootout(PlayByPlaySoccerShootoutRowViewModel)
    case americanFootballPlay(GameAmericanFootballPlayEventViewModel)
    case baseballPlay(PlayByPlayBaseballPlayViewModel)
    case baseballEvent(GameBaseballPlayEventViewModel)
    case noSectionContent(PlayByPlayNoSectionContentViewModel)
    case noRowContent(PlayByPlayNoContentViewModel)

    var id: AnyHashable {
        switch self {
        case .header(let viewModel):
            return viewModel.id
        case .basicHeader(let viewModel):
            return viewModel.id
        case .teamHeader(let viewModel):
            return viewModel.id
        case .general(let viewModel):
            return viewModel.id
        case .soccerShootoutHeader(let viewModel):
            return viewModel.id
        case .soccerShootout(let viewModel):
            return viewModel.id
        case .americanFootballPlay(let viewModel):
            return viewModel.id
        case .baseballPlay(let viewModel):
            return viewModel.id
        case .baseballEvent(let viewModel):
            return viewModel.id
        case .noSectionContent(let viewModel):
            return viewModel.id
        case .noRowContent(let viewModel):
            return viewModel.id
        }
    }

    static func == (lhs: GamePlayRowType, rhs: GamePlayRowType) -> Bool {
        lhs.id == rhs.id
    }
}
