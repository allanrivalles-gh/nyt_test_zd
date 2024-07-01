package com.theathletic.boxscore.ui.modules

import com.theathletic.ui.asResourceString

object PlayerStatsTableModulePreviewData {
    val players = listOf<PlayerStatsTableModule.PlayerColumnItem>(
        PlayerStatsTableModule.Category(
            label = "Hitters".asResourceString()
        ),
        PlayerStatsTableModule.Player(
            playerName = "D.LeMahieu",
            position = "3B -1B"
        ),
        PlayerStatsTableModule.Player(
            playerName = "A.Judge",
            position = "RF"
        ),
        PlayerStatsTableModule.Player(
            playerName = "A.Smithensonnesonolopas",
            position = "1B"
        )
    )

    val statistics = listOf<List<PlayerStatsTableModule.StatisticColumnItem>>(
        listOf<PlayerStatsTableModule.StatisticColumnItem>(
            PlayerStatsTableModule.StatisticLabel(
                label = "AB"
            ),
            PlayerStatsTableModule.StatisticValue(
                value = "5"
            ),
            PlayerStatsTableModule.StatisticValue(
                value = "3"
            ),
            PlayerStatsTableModule.StatisticValue(
                value = "2"
            )
        ),
        listOf<PlayerStatsTableModule.StatisticColumnItem>(
            PlayerStatsTableModule.StatisticLabel(
                label = "RBIXX"
            ),
            PlayerStatsTableModule.StatisticValue(
                value = "1"
            ),
            PlayerStatsTableModule.StatisticValue(
                value = "0"
            ),
            PlayerStatsTableModule.StatisticValue(
                value = "2"
            )
        ),
        listOf<PlayerStatsTableModule.StatisticColumnItem>(
            PlayerStatsTableModule.StatisticLabel(
                label = "AVG"
            ),
            PlayerStatsTableModule.StatisticValue(
                value = "0.26887"
            ),
            PlayerStatsTableModule.StatisticValue(
                value = "0.30567"
            ),
            PlayerStatsTableModule.StatisticValue(
                value = "0.45"
            )
        )
    )
}