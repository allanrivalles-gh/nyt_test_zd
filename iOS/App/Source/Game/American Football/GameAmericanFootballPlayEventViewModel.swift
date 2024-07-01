//
//  GameAmericanFootballPlayEventViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 29/6/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

struct GameAmericanFootballPlayEventViewModel: Identifiable {
    let id: String
    let clock: String?
    let title: String?
    let titleSuffix: String?
    let subtitle: String
    let playId: String
    let occurredAtString: String
}

extension Array where Element == GQL.AmericanFootballSubPlay {
    func makeViewModels(sectionId: String) -> [GameAmericanFootballPlayEventViewModel] {
        map { play in
            let possession = play.possession?.fragments.americanFootballPossession
            return GameAmericanFootballPlayEventViewModel(
                id: "\(sectionId)-\(play.id)",
                clock: play.clock,
                title: play.header,
                titleSuffix: possession?.summary,
                subtitle: play.description,
                playId: play.id,
                occurredAtString: play.occurredAtStr
            )
        }
    }
}
