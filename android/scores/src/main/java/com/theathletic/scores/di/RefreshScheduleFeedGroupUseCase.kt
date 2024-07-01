package com.theathletic.scores.di

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.scores.data.ScheduleRepository
import com.theathletic.scores.data.local.Schedule
import timber.log.Timber

class RefreshScheduleFeedGroupUseCase @AutoKoin constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        entityId: String,
        isLeague: Boolean,
        index: Int,
        filterId: String?
    ): Result<Schedule.Group?> {
        val key = if (isLeague) entityId.uppercase() else entityId
        return repository.getGroupIdForIndex(key = key, index = index)?.let { groupId ->
            try {
                repository.fetchScheduleFeedGroup(key, groupId, filterId)
                Result.success(null)
            } catch (error: Throwable) {
                Timber.e(error)
                Result.failure(error)
            }
        } ?: Result.failure(
            ScheduleRepository.ScheduleException("Error refreshing Schedule Group for entity: $entityId, index: $index")
        )
    }
}