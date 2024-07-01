package com.theathletic.scores.di

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.scores.data.ScheduleRepository
import com.theathletic.scores.data.local.Schedule
import timber.log.Timber

class FetchScheduleFeedGroupUseCase @AutoKoin constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        entityId: String,
        isLeague: Boolean,
        groupId: String,
        filterId: String?,
    ): Result<Schedule.Group?> {
        val key = if (isLeague) entityId.uppercase() else entityId
        val feed = repository.getScheduleFeedGroup(key = key, groupId = groupId, filterId = filterId)
        return if (feed == null || feed.sections.isEmpty()) {
            try {
                repository.fetchScheduleFeedGroup(key, groupId, filterId)
                Result.success(null)
            } catch (error: Throwable) {
                Timber.e(error)
                Result.failure(error)
            }
        } else {
            Result.success(feed)
        }
    }
}