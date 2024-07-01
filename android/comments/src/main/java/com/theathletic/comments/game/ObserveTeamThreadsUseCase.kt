package com.theathletic.comments.game

import com.theathletic.annotation.autokoin.AutoKoin
import kotlinx.coroutines.flow.catch
import timber.log.Timber

class ObserveTeamThreadsUseCase @AutoKoin constructor(
    private val teamThreadsRepository: TeamThreadsRepository
) {

    operator fun invoke(gameId: String) = teamThreadsRepository.observeTeamThreads(gameId).catch { Timber.e(it) }
}