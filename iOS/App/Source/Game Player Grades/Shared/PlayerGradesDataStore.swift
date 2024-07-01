//
//  PlayerGradesDataStore.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 19/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Combine
import Foundation

final class PlayerGradesDataStore {

    static let shared = PlayerGradesDataStore()

    private var subject = CurrentValueSubject<[GamePlayerID: PlayerGrade], Never>([:])
    private let queue = DispatchQueue(label: "PlayerGradesDataStore", qos: .userInitiated)

}

extension PlayerGradesDataStore {

    // MARK: - Exposed (thread-safe)

    func grade(for id: GamePlayerID) -> PlayerGrade? {
        queue.sync { subject.value[id] }
    }

    func publisherForGamePlayerId(id: GamePlayerID) -> AnyPublisher<PlayerGrade?, Never> {
        queue.sync {
            subject.map { $0[id] }
                .removeDuplicates()
                .receive(on: RunLoop.main)
                .eraseToAnyPublisher()
        }
    }

    func publisherForHasGradedAllPlayers(
        for playerIds: [GamePlayerID]
    ) -> AnyPublisher<Bool, Never> {
        queue.sync {
            subject
                .map {
                    $0.filter { playerIds.contains($0.key) }
                        .allSatisfy { $0.value.userGrade != nil }
                }
                .removeDuplicates()
                .receive(on: RunLoop.main)
                .eraseToAnyPublisher()
        }
    }

    func storeGrades(from gameGrades: GQL.GamePlayerGrades) {
        queue.sync(flags: .barrier) {
            gameGrades.firstTeam.map { _storeGrades(from: $0) }
            gameGrades.secondTeam.map { _storeGrades(from: $0) }
        }

    }

    func storeGrades(from team: GQL.GamePlayerGradesTeam) {
        queue.sync(flags: .barrier) {
            _storeGrades(from: team)
        }
    }

    func storeGrade(gamePlayerId: GamePlayerID, entity: GQL.GamePlayerGrade) {
        let newGrade = PlayerGrade(
            average: entity.average,
            averageString: entity.averageString,
            total: entity.total,
            userGrade: entity.grade.flatMap { $0 > 0 ? $0 : nil },
            updatedAt: entity.updatedAt
        )

        storeGrade(gamePlayerId: gamePlayerId, newGrade: newGrade, ignoreOutdated: true)
    }

    func storeGrade(gamePlayerId: GamePlayerID, newGrade: PlayerGrade, ignoreOutdated: Bool) {
        queue.sync(flags: .barrier) {
            guard
                shouldAcceptUpdate(
                    gamePlayerId: gamePlayerId,
                    grade: newGrade,
                    ignoreOutdated: ignoreOutdated
                )
            else {
                return
            }

            subject.value[gamePlayerId] = newGrade
        }
    }

    // MARK: - Private (not-thread safe)

    private func _storeGrades(from team: GQL.GamePlayerGradesTeam) {
        team.lineUp?.players.forEach { gamePlayer in
            if let gqlGrade = gamePlayer.grade?.fragments.gamePlayerGrade {
                let newGrade = PlayerGrade(
                    average: gqlGrade.average,
                    averageString: gqlGrade.averageString,
                    total: gqlGrade.total,
                    userGrade: gqlGrade.grade,
                    updatedAt: gqlGrade.updatedAt
                )

                guard
                    shouldAcceptUpdate(
                        gamePlayerId: gamePlayer.id,
                        grade: newGrade,
                        ignoreOutdated: true
                    )
                else {
                    return
                }

                subject.value[gamePlayer.id] = newGrade
            } else {
                if subject.value.keys.contains(gamePlayer.id) {
                    subject.value.removeValue(forKey: gamePlayer.id)
                }
            }
        }
    }

    private func shouldAcceptUpdate(
        gamePlayerId: GamePlayerID,
        grade: PlayerGrade,
        ignoreOutdated: Bool
    ) -> Bool {
        if let existing = subject.value[gamePlayerId] {
            if existing == grade {
                /// Do nothing if it's the same as existing
                return false

            } else if ignoreOutdated && grade.updatedAt <= existing.updatedAt {
                /// Ignore the given one if it's older than the one in the store
                return false
            }
        }

        return true
    }

}
