package com.theathletic.scores.di

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.League
import com.theathletic.scores.data.ScheduleRepository
import timber.log.Timber

class FetchScheduleUseCase @AutoKoin constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        entityId: String,
        isLeague: Boolean
    ): Result<Unit> {
        return try {
            if (isLeague) {
                repository.fetchLeagueSchedule(League.valueOf(entityId.uppercase()))
            } else {
                repository.fetchTeamSchedule(entityId)
            }
            Result.success(Unit)
        } catch (error: Throwable) {
            Timber.e(error)
            Result.failure(error)
        }
    }
}