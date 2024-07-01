//
//  GameScoreSortable.swift
//  theathletic-ios
//
//  Created by Jan Remes on 24/04/2019.
//  Copyright © 2019 The Athletic. All rights reserved.
//

import AthleticScoresFoundation
import Foundation

public protocol GameScoreSortable {
    var sortingDate: Date? { get }
    var gameId: String { get }
}

extension ScheduledGame: GameScoreSortable {

    public var sortingDate: Date? {
        return scheduledAt
    }
}

extension Array where Element: GameScoreSortable {

    /// Sort score by optional date and if missing use gameId
    ///
    /// - Parameter ascending: Ascending means that earlier values precede later ones e.g. 1/1/2000 will sort ahead of 1/1/2001.
    public mutating func sortScore(ascending: Bool) {
        self.sort(by: sortingAlgorithm(ascending: ascending))
    }

    /// Returns a new array containing the sorted scores
    /// - Parameter ascending: Whether to sort in ascending order, first by date, then by gameId on duplicate dates
    /// - Returns: New array
    public func sorted(ascending: Bool) -> [Element] {
        return self.sorted(by: sortingAlgorithm(ascending: ascending))
    }

    private func sortingAlgorithm(ascending: Bool) -> ((Element, Element) -> Bool) {
        return {
            if let d1 = $0.sortingDate, let d2 = $1.sortingDate {
                // If they both have a date, sort by date then by gameId
                let lhs = (d1, $0.gameId)
                let rhs = (d2, $1.gameId)
                return ascending ? lhs < rhs : lhs > rhs
            } else {
                return ascending ? $0.gameId < $1.gameId : $0.gameId > $1.gameId
            }
        }
    }

}
