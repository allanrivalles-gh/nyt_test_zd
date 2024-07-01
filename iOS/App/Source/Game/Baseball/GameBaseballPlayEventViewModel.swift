//
//  GameBaseballPlayEventViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 8/6/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticScoresFoundation
import Foundation
import SwiftUI

struct GameBaseballPlayEventViewModel: Identifiable, Equatable {
    struct Icons: Equatable {
        let basesHighlighting: BaseballBasesDiamond.Highlighting
        let pitchPosition: CGPoint?
        let hitPosition: CGPoint?
    }

    let id: String
    let color: Color?
    let number: String?
    let title: String
    let subtitle: String?
    let icons: Icons?
}

extension GameBaseballPlayEventViewModel {
    init(id: String, entity: GQL.BaseballPitchSubPlay) {
        self.init(
            id: id,
            color: entity.pitchOutcome?.color,
            number: entity.number.string,
            title: entity.description,
            subtitle: entity.pitchDescription,
            icons: Icons(
                basesHighlighting:
                    entity.bases
                    .reduce(into: .none) {
                        $0.insert(BaseballBasesDiamond.Highlighting(rawValue: 1 << $1))
                    },
                pitchPosition: entity.pitchZone.map { BaseballPitchZone(zone: $0).position },
                hitPosition: entity.hitZone.map { BaseballHitZone(zone: $0).position }
            )
        )
    }

    init(id: String, entity: GQL.BaseballSubPlay) {
        self.init(
            id: id,
            color: nil,
            number: nil,
            title: entity.description,
            subtitle: nil,
            icons: nil
        )
    }
}

extension Array where Element == GQL.BaseballGenericSubPlay {
    func makeViewModels(sectionId: String) -> [GameBaseballPlayEventViewModel] {
        compactMap { play in
            if let play = play.fragments.baseballPitchSubPlay {
                return GameBaseballPlayEventViewModel(
                    id: "\(sectionId)-\(play.id)",
                    entity: play
                )
            } else if let play = play.fragments.baseballSubPlay {
                return GameBaseballPlayEventViewModel(
                    id: "\(sectionId)-\(play.id)",
                    entity: play
                )
            } else {
                return nil
            }
        }
    }
}

extension GQL.BaseballPitchOutcome {
    fileprivate var color: Color {
        switch self {
        case .ball:
            return .chalk.green
        case .deadBall:
            return .chalk.dark500
        case .hit:
            return .chalk.blue
        case .strike:
            return .chalk.red
        case .__unknown:
            return .clear
        }
    }
}
