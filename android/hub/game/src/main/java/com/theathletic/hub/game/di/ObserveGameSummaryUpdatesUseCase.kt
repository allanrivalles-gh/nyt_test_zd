package com.theathletic.hub.game.di

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.hub.game.data.GameHubRepository
import kotlinx.coroutines.flow.flow

class ObserveGameSummaryUpdatesUseCase @AutoKoin constructor(
    private val repository: GameHubRepository
) {
    operator fun invoke(gameId: String) = flow {
        repository.getGameSummary(gameId).collect { emit(it) }
    }
}