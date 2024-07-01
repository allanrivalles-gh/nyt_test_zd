package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.playergrades.BoxScorePlayerGrades
import com.theathletic.boxscore.ui.playergrades.PlayerGrades
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2

data class PlayerGradeModule(
    val id: String,
    val playerGrades: BoxScorePlayerGrades.PlayerGrades,
    val showFirstTeam: Boolean
) : FeedModuleV2 {

    override val moduleId: String = "PlayerGradeModule:$id"

    interface Interaction {
        data class OnPlayerGradesClick(val isLocked: Boolean) : FeedInteraction
        data class OnTeamToggled(val isFirstTeamSelected: Boolean) : FeedInteraction
    }

    @Composable
    override fun Render() {
        PlayerGrades(
            firstTeamName = playerGrades.teams.firstTeamName,
            secondTeamName = playerGrades.teams.secondTeamName,
            firstTeamPlayerGrades = playerGrades.firstTeamPlayerGrades,
            secondTeamPlayerGrades = playerGrades.secondTeamPlayerGrades,
            isLocked = playerGrades.isLocked,
            showFirstTeam = showFirstTeam
        )
    }
}