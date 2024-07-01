package com.theathletic.comments

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.game.Team

class AnalyticsCommentTeamIdUseCase @AutoKoin constructor() {
    operator fun invoke(
        isTeamSpecificComment: Boolean,
        team: Team? = null,
        useLegacy: Boolean = false
    ): String? {
        return if (isTeamSpecificComment && team != null) {
            if (useLegacy) team.legacyId else team.id
        } else {
            null
        }
    }
}