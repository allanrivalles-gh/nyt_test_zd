package com.theathletic.scores.ui.gamecells

import com.theathletic.analytics.impressions.ImpressionPayload

object GameCellPreviewData {
    private val firstTeam = GameCellModel.Team(
        logo = emptyList(),
        name = "Brighton & Hove Albion ",
        isDimmed = false,
        ranking = "",
        teamDetails = GameCellModel.TeamDetails.PreGame(pregameLabel = "41-46")
    )

    private val secondTeam = GameCellModel.Team(
        logo = emptyList(),
        name = "Packers",
        isDimmed = false,
        ranking = "",
        teamDetails = GameCellModel.TeamDetails.PreGame(pregameLabel = "9-11")
    )

    val preGameCell = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam,
        secondTeam = secondTeam,
        title = "",
        showTitle = false,
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.DateTimeStatus("Fri, 2/4\n8:00pm"),
                GameCellModel.GameInfo.Default("TNT"),
                GameCellModel.GameInfo.Default("BOS -10.5")
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val preGameCellWithRanking = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(ranking = "4"),
        secondTeam = secondTeam.copy(ranking = "13"),
        title = "",
        showTitle = false,
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.DateTimeStatus("Fri, 2/4\n8:00pm"),
                GameCellModel.GameInfo.Default("TNT"),
                GameCellModel.GameInfo.Default("BOS -10.5")
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val inGameCell = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "199",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        secondTeam = secondTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "1",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        title = "",
        showTitle = false,
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.Live("Q3 3:47"),
                GameCellModel.GameInfo.Default("TNT"),
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val inGameCellRedCard = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "4",
                icon = GameCellModel.EventIcon.RED_CARD,
                penaltyGoals = null,
                isWinner = false,
            )
        ),
        secondTeam = secondTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "1",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        title = "",
        showTitle = false,
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.Live("Q3 3:47"),
                GameCellModel.GameInfo.Default("TNT"),
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val baseballInGameCell = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "199",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        secondTeam = secondTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "1",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        title = "",
        showTitle = false,
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.BaseballWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.Live("BOT 2"),
                GameCellModel.GameInfo.Situation("1 Out"),
                GameCellModel.GameInfo.Default("ESPN Network"),
            ),
            occupiedBases = listOf(2, 3)
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val inGameCellWithRanking = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(
            ranking = "45",
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "199",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        secondTeam = secondTeam.copy(
            ranking = "63",
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "1",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        title = "",
        showTitle = false,
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.Live("Q3 3:47"),
                GameCellModel.GameInfo.Default("TNT"),
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val inGameCellWithPossession = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "199",
                penaltyGoals = null,
                icon = GameCellModel.EventIcon.POSSESSION,
                isWinner = false,
            )
        ),
        secondTeam = secondTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "1",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        title = "",
        showTitle = false,
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.Live("Q2 11:12"),
                GameCellModel.GameInfo.Situation("1st & 10 at TEN 12"),
                GameCellModel.GameInfo.Default("ESPN")
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val postGameCell = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(
            isDimmed = true,
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "9",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        secondTeam = secondTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "199",
                penaltyGoals = null,
                icon = null,
                isWinner = true
            )
        ),
        showTitle = true,
        title = "UEFA Champions League, Group Stage Matchday 2 of 6",
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.DateTimeStatus("Final"),
                GameCellModel.GameInfo.Default("Mon, 10/24"),
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val postGameCellWithRanking = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(
            isDimmed = true,
            ranking = "23",
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "9",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        secondTeam = secondTeam.copy(
            ranking = "42",
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "199",
                penaltyGoals = null,
                icon = null,
                isWinner = true
            )
        ),
        showTitle = true,
        title = "UEFA Champions League, Group Stage Matchday 2 of 6",
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.DateTimeStatus("Final"),
                GameCellModel.GameInfo.Default("Mon, 10/24"),
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val tbdGameCell = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(
            teamDetails = GameCellModel.TeamDetails.PreGame("00-00"),
            name = "Winner of Group B",
            isDimmed = true
        ),
        secondTeam = secondTeam.copy(
            name = "TBD",
            isDimmed = true,
            teamDetails = GameCellModel.TeamDetails.PreGame("00-00")
        ),
        showTitle = true,
        title = "WInner of BLANK & BLANK",
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.DateTimeStatus("Mon, 3/24\n2:00 PM"),
                GameCellModel.GameInfo.Default("ESPN")
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val cancelledGameCell = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(isDimmed = true, teamDetails = GameCellModel.TeamDetails.PreGame(pregameLabel = "000")),
        secondTeam = secondTeam.copy(isDimmed = true, teamDetails = GameCellModel.TeamDetails.PreGame(pregameLabel = "000")),
        title = "",
        showTitle = false,
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.DateTimeStatus("Canceled")
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val ctaGameCell = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "11-24",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        secondTeam = secondTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "19-17",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        ),
        title = "",
        showTitle = false,
        discussionLinkText = "Join The Discussion",
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.DateTimeStatus("12:00 PM"),
                GameCellModel.GameInfo.Default("ESPN"),
                GameCellModel.GameInfo.Default("TEN -10.5")
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    val penaltyGoalsGameCell = GameCellModel(
        gameId = "gameId",
        firstTeam = firstTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "3",
                penaltyGoals = "(10)",
                icon = null,
                isWinner = true,
            )
        ),
        secondTeam = secondTeam.copy(
            teamDetails = GameCellModel.TeamDetails.InAndPostGame(
                score = "3",
                penaltyGoals = "(2)",
                icon = null,
                isWinner = false
            ),
            isDimmed = true
        ),
        title = "",
        showTitle = false,
        discussionLinkText = null,
        infoWidget = GameCellModel.InfoWidget.LabelWidget(
            infos = mutableListOf(
                GameCellModel.GameInfo.DateTimeStatus("Final"),
                GameCellModel.GameInfo.Default("Mon, 10/24"),
            )
        ),
        impressionPayload = impressionPayload,
        showTeamRanking = false
    )

    private val impressionPayload: ImpressionPayload
        get() = ImpressionPayload(
            objectType = "",
            objectId = "",
            element = "",
            pageOrder = 0
        )
}