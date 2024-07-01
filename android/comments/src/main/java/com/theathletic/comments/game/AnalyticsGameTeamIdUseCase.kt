package com.theathletic.comments.game

import com.theathletic.annotation.autokoin.AutoKoin

class AnalyticsGameTeamIdUseCase @AutoKoin constructor(
    private val teamThreadsRepository: TeamThreadsRepository
) {
    suspend operator fun invoke(
        isTeamSpecificThreads: Boolean,
        gameId: String
    ): String? {
        if (isTeamSpecificThreads.not()) return null
        val threads = teamThreadsRepository.fetchTeamThreads(gameId)
        return threads?.teamId
    }
}