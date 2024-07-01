package com.theathletic.gamedetail.playergrades.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.PlayerGradeMiniCardModule
import com.theathletic.boxscore.ui.modules.PlayerGradeNoRatingsAvailableModule
import com.theathletic.boxscore.ui.playergrades.PlayerGradeMiniCardModel
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.FeedUiV2
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreCommonRenderers
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GradeStatus
import com.theathletic.gamedetail.data.local.PlayerGradesLocalModel
import com.theathletic.ui.Transformer
import com.theathletic.ui.asResourceString
import com.theathletic.ui.modules.SpacingModuleV2
import com.theathletic.ui.widgets.buttons.TwoItemToggleButtonModule
import com.theathletic.utility.orShortDash

class PlayerGradesTabTransformer @AutoKoin constructor(
    private val commonRenderers: BoxScoreCommonRenderers,
) :
    Transformer<PlayerGradesTabState, PlayerGradesTabContract.ViewState> {
    override fun transform(data: PlayerGradesTabState): PlayerGradesTabContract.ViewState {
        return PlayerGradesTabContract.ViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            feed = FeedUiV2(modules = createFeedModules(data))
        )
    }

    private fun createFeedModules(data: PlayerGradesTabState): List<FeedModuleV2> {
        if (data.playerGrades == null) return emptyList()

        return mutableListOf<FeedModuleV2>().apply {
            add(
                TwoItemToggleButtonModule(
                    id = data.playerGrades.gameId,
                    itemOneLabel = data.playerGrades.getFirstTeam(data.sport)?.name.orShortDash().asResourceString(),
                    itemTwoLabel = data.playerGrades.getSecondTeam(data.sport)?.name.orShortDash().asResourceString(),
                    isFirstItemSelected = data.isFirstTeamSelected
                )
            )
            val team = if (data.isFirstTeamSelected) {
                data.playerGrades.getFirstTeam(data.sport)
            } else {
                data.playerGrades.getSecondTeam(data.sport)
            }
            team?.players?.let { players ->
                val playerModules = players.map { player ->
                    PlayerGradeMiniCardModule(
                        id = player.playerId,
                        playerGradeMiniCard = player.toUi(
                            teamLogos = team.logos,
                            teamColor = team.backgroundColor,
                            playerCurrentlySubmittingGrade = data.playersCurrentlySubmittingGrade.contains(player.playerId),
                            isLocked = data.playerGrades.gradeStatus == GradeStatus.LOCKED
                        )
                    )
                }
                if (playerModules.isNotEmpty()) {
                    addAll(playerModules)
                    add(
                        SpacingModuleV2(
                            id = data.playerGrades.gameId,
                            color = SpacingModuleV2.Background.StandardForegroundColor,
                            height = SpacingModuleV2.Height.Large
                        )
                    )
                } else {
                    add(PlayerGradeNoRatingsAvailableModule(id = data.gameId))
                }
            } ?: add(PlayerGradeNoRatingsAvailableModule(id = data.gameId))
        }
    }

    private fun PlayerGradesLocalModel.Player.toUi(
        teamLogos: SizedImages,
        teamColor: String?,
        playerCurrentlySubmittingGrade: Boolean,
        isLocked: Boolean = false
    ): PlayerGradeMiniCardModel {
        val grade = grading?.grade ?: 0
        return PlayerGradeMiniCardModel(
            id = playerId,
            playerName = displayName,
            playerHeadshot = headshots,
            teamLogos = teamLogos,
            teamColor = teamColor,
            playerStats = summaryStatistics.toSummaryLabel() ?: position.alias,
            awardedGrade = grade,
            averageGrade = grading?.averageGradeDisplay.orShortDash(),
            totalGrades = grading?.totalGrades ?: 0,
            isLocked = isLocked,
            isGraded = grade > 0 && playerCurrentlySubmittingGrade.not()
        )
    }

    private fun List<GameDetailLocalModel.Statistic>.toSummaryLabel() =
        if (isNotEmpty()) commonRenderers.formatStatisticValue(this[0]).orEmpty() else null
}