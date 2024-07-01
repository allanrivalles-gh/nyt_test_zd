//
//  TournamentTileGame+Title.swift
//
//
//  Created by Leonardo da Silva on 4/6/23.
//

import AthleticApolloTypes
import Foundation

extension TournamentTile.Game {
    func title(index: Int) -> String {
        let prefix = status == .ifNecessary ? "*" : ""
        return ["\(prefix)Game \(index + 1)", titleSuffix]
            .compactMap { $0 }
            .joined(separator: ", ")
    }

    private var titleSuffix: String? {
        switch status {
        case .final:
            return "Final"
        case .inProgress:
            if sport == .baseball {
                return inningHalfShortTile
            }
            return matchTimeDisplay
        default: return nil
        }
    }
}

extension TournamentTile.Game {
    fileprivate var inningHalfShortTile: String? {
        guard let inning, let inningHalf else { return nil }
        return inningHalf.shortTitle(forInning: inning)
    }
}
