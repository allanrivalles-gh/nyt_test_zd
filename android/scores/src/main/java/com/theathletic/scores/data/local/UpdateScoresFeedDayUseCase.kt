package com.theathletic.scores.data.local

import com.theathletic.annotation.autokoin.AutoKoin

class UpdateScoresFeedDayUseCase @AutoKoin constructor() {
    operator fun invoke(day: String, dayGroups: List<ScoresFeedGroup>, feed: ScoresFeedLocalModel): ScoresFeedLocalModel {
        val updatedDays = feed.days.map { item ->
            if (item.day == day) item.copy(groups = dayGroups) else item
        }
        return feed.copy(days = updatedDays)
    }
}