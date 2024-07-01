package com.theathletic.boxscore.ui.playergrades

import com.theathletic.data.SizedImage
import com.theathletic.ui.asResourceString

private const val GREEN = "0B6623"

object PlayerGradesDetailPreviewData {
    @Suppress("LongMethod")
    fun getPlayerGrades(state: PlayerGradesDetailUi.GradingState) = PlayerGradesDetailUi(
        teamBackground = GREEN,
        teamLogos = emptyList(),
        gameStatus = PlayerGradesDetailUi.GameStatus(
            firstTeamLogos = listOf(
                SizedImage(
                    width = 72,
                    height = 72,
                    uri = "https://cdn-team-logos.theathletic.com/team-logo-58-72x72.png"
                )
            ),
            firstTeamScore = 99,
            secondTeamLogos = listOf(
                SizedImage(
                    width = 72,
                    height = 72,
                    uri = "https://cdn-team-logos.theathletic.com/team-logo-31-72x72.png"
                )
            ),
            secondTeamScore = 77,
            gameStatePrimary = "4th",
            gameStateSecondary = "2:44",
            scheduledDate = "Feb 29th",
            showLiveGameDetails = true,
            showGameStatePrimary = true,
            showGameStateSecondary = true
        ),
        players = listOf(
            PlayerGradesDetailUi.Player(
                id = "uniqueId-1",
                name = "J. Brissett",
                details = "QB, CLE #7".asResourceString(),
                statisticsSummaryList = listOf(
                    PlayerGradesDetailUi.StatisticsSummary(
                        label = "Stat",
                        value = "9991"
                    ),
                    PlayerGradesDetailUi.StatisticsSummary(
                        label = "Stat Value",
                        value = "9992"
                    ),
                    PlayerGradesDetailUi.StatisticsSummary(
                        label = "Stat Very Long Value",
                        value = "9993"
                    ),
                    PlayerGradesDetailUi.StatisticsSummary(
                        label = "Stat",
                        value = "9994"
                    ),
                ),
                statisticsFullList = createFullPlayerStats(),
                grade = PlayerGradesDetailUi.PlayerGrade(
                    state = state,
                    grading = if (state == PlayerGradesDetailUi.GradingState.GRADED) 3 else 0,
                    averageGrade = "4.9",
                    totalGradings = 284
                ),
                headshots = emptyList(),
            )
        ),
        isLocked = false,
        initialPlayerIndex = 0
    )

    @Suppress("LongMethod")
    private fun createFullPlayerStats(): List<PlayerGradesDetailUi.StatisticsSummary> {
        return listOf(
            PlayerGradesDetailUi.StatisticsSummary(
                label = "QB Rating",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "Rushing Yards",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "Rushing Touchdowns",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "Fumbles Lost",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "On Target Throws (%)",
                value = "56.2"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "Poor Throws",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "Longest Pass",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "QB Rating again",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "QB Rating",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "Rushing Yards",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "Rushing Touchdowns",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "Fumbles Lost",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "On Target Throws (%)",
                value = "56.2"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "Poor Throws",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "Longest Pass",
                value = "000"
            ),
            PlayerGradesDetailUi.StatisticsSummary(
                label = "QB Rating again",
                value = "000"
            ),
        )
    }
}