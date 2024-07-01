package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.PlayerGradeCardModule
import com.theathletic.boxscore.ui.modules.PlayerGradeMiniCardModule
import com.theathletic.boxscore.ui.modules.PlayerGradeModule
import com.theathletic.boxscore.ui.playergrades.BoxScorePlayerGrades
import com.theathletic.boxscore.ui.playergrades.PlayerGradeMiniCardModel
import com.theathletic.boxscore.ui.playergrades.PlayerGradeModel
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GradeStatus
import com.theathletic.gamedetail.data.local.PlayerGradesLocalModel
import com.theathletic.gamedetail.playergrades.ui.FilterPlayerGradesUseCase
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.utility.orShortDash
import java.util.concurrent.atomic.AtomicInteger

class BoxScorePlayerGradeRenderers @AutoKoin constructor(
    private val commonRenderers: BoxScoreCommonRenderers,
    private val filterPlayerGradesUseCase: FilterPlayerGradesUseCase,
) {
    fun createPlayerGradeCarousel(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger,
        showFirstTeam: Boolean
    ): FeedModuleV2? {
        if ((game.gradeStatus.gradingIsActive() && game.arePlayersValid).not()) return null
        pageOrder.getAndIncrement()
        return PlayerGradeModule(
            id = game.id,
            playerGrades = BoxScorePlayerGrades.PlayerGrades(
                teams = BoxScorePlayerGrades.Teams(
                    firstTeamName = game.firstTeam?.team?.displayName.orEmpty(),
                    secondTeamName = game.secondTeam?.team?.displayName.orEmpty()
                ),
                isLocked = game.gradeStatus.isLocked(),
                firstTeamPlayerGrades = game.firstTeam?.let { team ->
                    playerGradeCards(
                        team,
                        game.gradeStatus.isLocked()
                    )
                } ?: emptyList(),
                secondTeamPlayerGrades = game.secondTeam?.let { team ->
                    playerGradeCards(
                        team,
                        game.gradeStatus.isLocked()
                    )
                } ?: emptyList(),
            ),
            showFirstTeam = showFirstTeam
        )
    }

    private fun playerGradeCards(team: GameDetailLocalModel.GameTeam, isLocked: Boolean) =
        filterPlayerGradesUseCase(team.players).take(3).map {
            if (isLocked) {
                it.toLockedCard(team.team)
            } else {
                it.toUnlockedCard(team.team)
            }
        }

    private fun PlayerGradesLocalModel.Player.toLockedCard(
        team: GameDetailLocalModel.Team?
    ): PlayerGradeMiniCardModule {
        val player = PlayerGradeMiniCardModel(
            id = playerId,
            playerName = displayName,
            totalGrades = grading?.totalGrades ?: 0,
            averageGrade = grading?.averageGradeDisplay ?: "0.0",
            playerHeadshot = headshots,
            playerStats = summaryStatistics.toSummaryLabel() ?: position.alias,
            isGraded = grading?.grade != null,
            isLocked = true,
            awardedGrade = grading?.grade ?: 0,
            teamLogos = team?.logos ?: emptyList(),
            teamColor = team?.primaryColor
        )

        return PlayerGradeMiniCardModule(
            id = playerId,
            playerGradeMiniCard = player
        )
    }

    private fun PlayerGradesLocalModel.Player.toUnlockedCard(team: GameDetailLocalModel.Team?): PlayerGradeCardModule {
        return PlayerGradeCardModule(
            id = playerId,
            playerGradeCard = PlayerGradeModel.PlayerGradeCard(
                isGraded = grading?.grade != null,
                awardedGrade = grading?.grade ?: 0,
                player = PlayerGradeModel.Player(
                    id = playerId,
                    averageGrade = grading?.averageGradeDisplay ?: "0.0",
                    totalGrades = grading?.totalGrades ?: 0,
                    position = position.alias,
                    name = displayName,
                    stats = defaultStatistics.take(4).map {
                        PlayerGradeModel.Stat(
                            title = StringWrapper(it.longHeaderLabel ?: it.label),
                            value = commonRenderers.formatStatisticValue(it).orShortDash()
                        )
                    },
                    headshots = headshots,
                    teamLogos = team?.logos ?: emptyList(),
                    teamColor = team?.primaryColor
                )
            )
        )
    }

    private fun GradeStatus?.isLocked(): Boolean {
        return this == GradeStatus.LOCKED
    }

    private fun List<GameDetailLocalModel.Statistic>.toSummaryLabel() =
        if (isNotEmpty()) commonRenderers.formatStatisticValue(this[0]).orEmpty() else null
}

private val GameDetailLocalModel.arePlayersValid
    get() = awayTeam?.players.orEmpty().isNotEmpty() || homeTeam?.players.orEmpty().isNotEmpty()

fun GradeStatus?.gradingIsActive(): Boolean {
    return this == GradeStatus.ENABLED || this == GradeStatus.LOCKED
}