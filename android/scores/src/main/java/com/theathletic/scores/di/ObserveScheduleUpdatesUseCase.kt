package com.theathletic.scores.di

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.scores.data.ScheduleRepository
import kotlinx.coroutines.flow.flow

class ObserveScheduleUpdatesUseCase @AutoKoin constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke(key: String) = flow {
        repository.getSchedule(key).collect { emit(it) }
    }
}