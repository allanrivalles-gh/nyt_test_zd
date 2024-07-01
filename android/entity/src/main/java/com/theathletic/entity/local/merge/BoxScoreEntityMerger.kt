package com.theathletic.entity.local.merge

import com.theathletic.scores.data.local.BoxScoreEntity

object BoxScoreEntityMerger : EntityMerger<BoxScoreEntity>() {
    override fun merge(old: BoxScoreEntity, new: BoxScoreEntity): BoxScoreEntity {
        return new.run {
            copy(
                scoreStatusText = newerString(old) { scoreStatusText },

                homeTeam = homeTeam.copy(
                    name = newerString(old) { homeTeam.name }.orEmpty(),
                    shortName = newerString(old) { homeTeam.shortName }.orEmpty(),
                    record = newerString(old) { homeTeam.record },
                    details = newerString(old) { homeTeam.details },
                    logo = newerString(old) { homeTeam.logo },
                ),

                awayTeam = awayTeam.copy(
                    name = newerString(old) { awayTeam.name }.orEmpty(),
                    shortName = newerString(old) { awayTeam.shortName }.orEmpty(),
                    record = newerString(old) { awayTeam.record },
                    details = newerString(old) { awayTeam.details },
                    logo = newerString(old) { awayTeam.logo },
                )
            )
        }
    }
}