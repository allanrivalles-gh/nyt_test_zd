package com.theathletic.boxscore.ui

import com.theathletic.boxscore.ui.modules.PlayerLineUpModule
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper

object PlayerLineUpPreviewData {

    val playerLineUpMock = PlayerLineUpModule(
        id = "001",
        firstTeamLabel = StringWrapper("Chelsea\n4-3-2-1"),
        secondTeamLabel = StringWrapper("Manchester City\n4-3-2-1"),
        "",
        "",
        firstTeamLineup = mapOf(
            StringWithParams(R.string.box_score_line_up_title_starting_line_up) to listOf(
                PlayerLineUpModule.PlayerLineUp.Player(
                    id = "001",
                    name = "First & Last",
                    eventIcons = listOf(
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_goal),
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_card_red)
                    ),
                    isExpanded = false,
                    isPreGame = false,
                    jerseyNumber = "10",
                    position = "GK",
                    showExpandIcon = true,
                    substitution = PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.IN_OUT,
                    substitutionTime = "72'",
                    playerStats = emptyList()
                ),
                PlayerLineUpModule.PlayerLineUp.Player(
                    id = "001",
                    name = "Second & Last",
                    eventIcons = listOf(
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_captain),
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_goal),
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_goal),
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_card_red)
                    ),
                    isExpanded = false,
                    isPreGame = false,
                    jerseyNumber = "10",
                    position = "GK",
                    showExpandIcon = true,
                    substitution = PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.NONE,
                    substitutionTime = "72'",
                    playerStats = List(5) { PlayerLineUpModule.PlayerLineUp.Stats("Total Touches", "32.00") }
                )
            ),
            StringWithParams(R.string.box_score_line_up_title_substitutions) to listOf(
                PlayerLineUpModule.PlayerLineUp.Player(
                    id = "001",
                    name = "Second & Last",
                    eventIcons = listOf(
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_captain),
                        PlayerLineUpModule.PlayerLineUp.BubbleIcon(R.drawable.ic_soccer_goal, 2),
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_card_yellow),
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_card_red),
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_card_yellow_red)
                    ),
                    isExpanded = false,
                    isPreGame = false,
                    jerseyNumber = "10",
                    position = "GK",
                    showExpandIcon = true,
                    substitution = PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.IN_OUT,
                    substitutionTime = "72' 82'",
                    playerStats = emptyList()
                ),
                PlayerLineUpModule.PlayerLineUp.Player(
                    id = "001",
                    name = "Second & Last",
                    eventIcons = emptyList(),
                    isExpanded = false,
                    isPreGame = false,
                    jerseyNumber = "10",
                    position = "GK",
                    showExpandIcon = true,
                    substitution = PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.NONE,
                    substitutionTime = "",
                    playerStats = List(5) { PlayerLineUpModule.PlayerLineUp.Stats("Total Touches", "32.00") }
                )
            ),
            StringWithParams(R.string.box_score_line_up_title_manager) to listOf(
                PlayerLineUpModule.PlayerLineUp.Manager(
                    name = "Mr.Manager",
                )
            )
        ),
        secondTeamLineup = mapOf(
            StringWithParams(R.string.box_score_line_up_title_starting_line_up) to listOf(
                PlayerLineUpModule.PlayerLineUp.Player(
                    id = "001",
                    name = "Second & Last",
                    eventIcons = listOf(
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_goal),
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_card_red)
                    ),
                    isExpanded = false,
                    isPreGame = false,
                    jerseyNumber = "10",
                    position = "GK",
                    showExpandIcon = true,
                    substitution = PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.IN_OUT,
                    substitutionTime = "72'",
                    playerStats = emptyList()
                ),
                PlayerLineUpModule.PlayerLineUp.Player(
                    id = "001",
                    name = "Second & Last",
                    eventIcons = listOf(
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_goal),
                        PlayerLineUpModule.PlayerLineUp.SingleIcon(R.drawable.ic_soccer_card_red)
                    ),
                    isExpanded = false,
                    isPreGame = false,
                    jerseyNumber = "10",
                    position = "GK",
                    showExpandIcon = true,
                    substitution = PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.IN_OUT,
                    substitutionTime = "72'",
                    playerStats = List(5) { PlayerLineUpModule.PlayerLineUp.Stats("Total Touches", "32.00") }
                )
            ),
            StringWithParams(R.string.box_score_line_up_title_substitutions) to listOf(
                PlayerLineUpModule.PlayerLineUp.Player(
                    id = "001",
                    name = "Second & Last",
                    eventIcons = emptyList(),
                    isExpanded = false,
                    isPreGame = false,
                    jerseyNumber = "10",
                    position = "GK",
                    showExpandIcon = true,
                    substitution = PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.NONE,
                    substitutionTime = "",
                    playerStats = emptyList()
                ),
                PlayerLineUpModule.PlayerLineUp.Player(
                    id = "001",
                    name = "Second & Last",
                    eventIcons = emptyList(),
                    isExpanded = false,
                    isPreGame = false,
                    jerseyNumber = "10",
                    position = "GK",
                    showExpandIcon = true,
                    substitution = PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.NONE,
                    substitutionTime = "",
                    playerStats = List(5) { PlayerLineUpModule.PlayerLineUp.Stats("Total Touches", "32.00") }
                )
            ),
            StringWithParams(R.string.box_score_line_up_title_manager) to listOf(
                PlayerLineUpModule.PlayerLineUp.Manager(
                    name = "Mr.Manager",
                )
            )
        ),
    )
}