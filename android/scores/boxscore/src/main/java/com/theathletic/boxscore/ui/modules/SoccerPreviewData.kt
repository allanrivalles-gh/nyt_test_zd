package com.theathletic.boxscore.ui.modules

import com.theathletic.boxscore.ui.SoccerMomentsUi
import com.theathletic.boxscore.ui.SoccerRecentFormHeaderModel
import com.theathletic.boxscore.ui.TimelineSummaryModel
import com.theathletic.boxscore.ui.playbyplay.SoccerPenaltyShootoutUI
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper

object SoccerPreviewData {

    val mockTimelineSummaryModule = TimelineSummaryModule(
        id = "001",
        TimelineSummaryModel.ExpectedGoals(
            showExpectedGoals = true,
            secondTeamValue = "0.32",
            firstTeamValue = "1.56"
        ),
        timelineSummary = listOf(
            TimelineSummaryModel.SummaryItem(
                icon = R.drawable.ic_soccer_goal,
                firstTeam = listOf(
                    TimelineSummaryModel.DisplayStrings(listOf(StringWrapper("E.Mendy 32'")))

                ),
                secondTeam = listOf(
                    TimelineSummaryModel.DisplayStrings(listOf(StringWrapper("K. Walker 30'"))),
                    TimelineSummaryModel.DisplayStrings(listOf(StringWrapper("J. Caneclo 68'"))),
                    TimelineSummaryModel.DisplayStrings(listOf(StringWrapper("E. Haaland 74'")))
                )
            ),
            TimelineSummaryModel.SummaryItem(
                icon = R.drawable.ic_soccer_card_red,
                firstTeam = listOf(
                    TimelineSummaryModel.DisplayStrings(listOf(StringWrapper("E.Mendy 42'")))

                ),
                secondTeam = listOf(
                    TimelineSummaryModel.DisplayStrings(listOf(StringWrapper("K. Walker 30'"))),
                    TimelineSummaryModel.DisplayStrings(listOf(StringWrapper("J. Caneclo 68'"))),
                )
            ),
        )
    )

    val mockKeyMoments = listOf(
        SoccerMomentsUi.EventSoccerMoment(
            id = "003",
            headerLabel = "Yellow Card",
            clock = "46'",
            description = "Adria Pedrosa is shown the yellow card for a bad foul.",
            teamLogos = emptyList(),
            iconRes = R.drawable.ic_soccer_substitute_on_off
        ),
        SoccerMomentsUi.ScoringSoccerMoment(
            teamLogos = emptyList(),
            description = "Karim Benzema (Real Madrid) header from the centre of the box to the top left corner. Assisted by Vinícius Júnior with a cross.",
            homeTeamScore = "1",
            awayTeamScore = "0",
            clock = "11'",
            headerLabel = "Goal",
            id = "001",
            awayTeamName = "CHE",
            homeTeamName = "MNC",
            teamColor = "6DC1FF"
        ),
        SoccerMomentsUi.ScoringSoccerMoment(
            teamLogos = emptyList(),
            description = "Karim Benzema (Real Madrid) header from the centre of the box to the top left corner. Assisted by Vinícius Júnior with a cross.",
            homeTeamScore = "1",
            awayTeamScore = "1",
            clock = "13'",
            headerLabel = "Goal",
            id = "001",
            awayTeamName = "CHE",
            homeTeamName = "MNC",
            teamColor = "6DC1FF"
        ),
        SoccerMomentsUi.StandardSoccerMoment(
            teamLogos = emptyList(),
            description = "Karim Benzema (Real Madrid) header from the centre of the box to the top left corner. Assisted by Vinícius Júnior with a cross.",
            clock = "90' + 3'",
            headerLabel = "Timeout",
            id = "001",
        )
    )

    val soccerRecentFormHeaderModel = SoccerRecentFormHeaderModel(
        expectedGoals = SoccerRecentFormHeaderModel.ExpectedGoals(
            firstTeamValue = "1.56",
            secondTeamValue = "0.32",
            showExpectedGoals = true
        ),
        firstTeamRecentForms = listOf(
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.LOSS,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.DRAW,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.LOSS
        ),
        secondTeamRecentForms = listOf(
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.LOSS,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.DRAW,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.NONE,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.NONE
        )
    )

    val soccerRecentFormHeaderAllMatchesModel = SoccerRecentFormHeaderModel(
        expectedGoals = SoccerRecentFormHeaderModel.ExpectedGoals(
            firstTeamValue = "1.56",
            secondTeamValue = "0.32",
            showExpectedGoals = true
        ),
        firstTeamRecentForms = listOf(
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.LOSS,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.LOSS
        ),
        secondTeamRecentForms = listOf(
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.LOSS,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.LOSS,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN
        )
    )

    val penaltyShootout = SoccerPenaltyShootoutModule(
        id = "001",
        firstTeamName = "CHE",
        firstTeamLogos = emptyList(),
        secondTeamName = "MNC",
        secondTeamLogos = emptyList(),
        penaltyShots = listOf(
            SoccerPenaltyShootoutUI.PenaltyShot(
                firstTeamPlayerName = "R. Lukaku",
                firstPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.MISSED,
                penaltyTitle = StringWithParams(R.string.box_score_soccer_penalty_play_title, "1"),
                secondTeamPlayerName = "R. Sterling",
                secondPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.SCORED
            ),
            SoccerPenaltyShootoutUI.PenaltyShot(
                firstTeamPlayerName = "C. Pulisic",
                firstPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.SCORED,
                penaltyTitle = StringWithParams(R.string.box_score_soccer_penalty_play_title, "2"),
                secondTeamPlayerName = "G. Jesus",
                secondPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.MISSED
            ),
            SoccerPenaltyShootoutUI.PenaltyShot(
                firstTeamPlayerName = "T. Werner",
                firstPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.MISSED,
                penaltyTitle = StringWithParams(R.string.box_score_soccer_penalty_play_title, "3"),
                secondTeamPlayerName = "R. Mahrez",
                secondPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.MISSED
            ),
            SoccerPenaltyShootoutUI.PenaltyShot(
                firstTeamPlayerName = "H. Ziyech",
                firstPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.SCORED,
                penaltyTitle = StringWithParams(R.string.box_score_soccer_penalty_play_title, "4"),
                secondTeamPlayerName = "J. Alvarez",
                secondPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.SCORED
            ),
            SoccerPenaltyShootoutUI.PenaltyShot(
                firstTeamPlayerName = "K. Havertz",
                firstPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.SCORED,
                penaltyTitle = StringWithParams(R.string.box_score_soccer_penalty_play_title, "5"),
                secondTeamPlayerName = "\u2014",
                secondPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.PENDING
            )
        )
    )
}