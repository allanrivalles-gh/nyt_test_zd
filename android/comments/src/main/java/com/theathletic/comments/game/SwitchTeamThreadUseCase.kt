package com.theathletic.comments.game

import com.theathletic.annotation.autokoin.AutoKoin

class SwitchTeamThreadUseCase @AutoKoin constructor(
    private val teamThreadsRepository: TeamThreadsRepository
) {
    suspend operator fun invoke(gameId: String, teamId: String): Result<Unit> {
        return try {
            teamThreadsRepository.switchTeamThread(gameId, teamId)
            Result.success(Unit)
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }
}