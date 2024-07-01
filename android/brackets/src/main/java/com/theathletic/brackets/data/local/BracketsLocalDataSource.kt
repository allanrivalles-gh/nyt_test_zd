package com.theathletic.brackets.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource
import com.theathletic.type.LeagueCode

class BracketsLocalDataSource @AutoKoin(Scope.SINGLE) constructor() : InMemoryLocalDataSource<LeagueCode, BracketsLocalModel>() {
    fun updateGame(leagueCode: LeagueCode, game: TournamentRoundGame) {
        val existing = getStateFlow(leagueCode).value
        if (existing != null) {
            val updated = existing.replaceGame(game)
            update(leagueCode, updated)
        }
    }
}

private fun BracketsLocalModel.replaceGame(game: TournamentRoundGame): BracketsLocalModel {
    return copy(
        rounds = rounds.map { round ->
            round.copy(
                groups = round.groups.map { group ->
                    group.copy(
                        games = group.games.map { existingGame ->
                            if (game.id == existingGame.id) {
                                game
                            } else {
                                existingGame
                            }
                        }
                    )
                }
            )
        }
    )
}