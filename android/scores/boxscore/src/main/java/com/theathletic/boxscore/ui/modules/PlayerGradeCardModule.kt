package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.playergrades.PlayerGradeCard
import com.theathletic.boxscore.ui.playergrades.PlayerGradeModel
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor

data class PlayerGradeCardModule(
    val id: String,
    val playerGradeCard: PlayerGradeModel.PlayerGradeCard
) : FeedModuleV2 {

    override val moduleId: String = "PlayerGradeCardModule:$id"

    interface Interaction {
        data class PlayerGradeDetailsClick(val playerId: String) : FeedInteraction
    }

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        PlayerGradeCard(
            playerId = playerGradeCard.player.id,
            name = playerGradeCard.player.name,
            position = playerGradeCard.player.position,
            teamLogos = playerGradeCard.player.teamLogos,
            teamColor = playerGradeCard.player.teamColor,
            headshots = playerGradeCard.player.headshots,
            averageGrade = playerGradeCard.player.averageGrade,
            awardedGrade = playerGradeCard.awardedGrade,
            isGraded = playerGradeCard.isGraded,
            stats = playerGradeCard.player.stats,
            totalGrades = playerGradeCard.player.totalGrades,
            interactor = interactor
        )
    }
}