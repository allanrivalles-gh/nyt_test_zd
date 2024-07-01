package com.theathletic.scores.data.local

import com.theathletic.data.LocalModel
import com.theathletic.data.SizedImages
import com.theathletic.entity.main.League
import com.theathletic.entity.main.Sport

data class TeamDetailsLocalModel(
    val id: String,
    val legacyId: Long,
    val league: League,
    val isPrimaryLeague: Boolean,
    val logoUrls: SizedImages,
    val sport: Sport,
    val currentStanding: String?,
    val type: TeamType
) : LocalModel {

    enum class TeamType {
        ALL_STAR,
        CLUB,
        NATIONAL,
        UNKNOWN
    }
}