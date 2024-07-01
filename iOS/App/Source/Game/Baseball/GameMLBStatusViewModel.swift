//
//  GameMLBStatusViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 5/11/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticScoresFoundation
import Foundation

struct GameMLBStatusViewModel {
    struct GameInfo: Equatable {
        let inningText: String?
        let basesHighlighting: BaseballBasesDiamond.Highlighting
        let bottomText: String?
    }

    let isDelayed: Bool
    let gameInfo: GameInfo
}

extension GameMLBStatusViewModel {
    static var empty: Self {
        .init(
            isDelayed: false,
            gameInfo: GameInfo(
                inningText: nil,
                basesHighlighting: .none,
                bottomText: nil
            )
        )
    }
}
