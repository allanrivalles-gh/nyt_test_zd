package com.theathletic.comments.game

import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.ui.utility.parseHexColor

fun TeamThreads.toTeamThreadBannerUiModel() = CommentsUi.TeamThreadBanner(
    teamName = current.team.name,
    teamLogo = current.team.logo,
    teamColor = current.team.color.parseHexColor(),
    showChangeTeamThread = teamThreads.size > 1
)

fun TeamThreadContextSwitch.toTeamThreadSheetUiModel() = CommentsUi.TeamThreadsSheet(
    currentThread = currentTeamThread.toTeamThreadUiModel(),
    secondThread = secondTeamThread?.toTeamThreadUiModel()
)

fun TeamThread.toTeamThreadUiModel() = CommentsUi.TeamThread(
    label = label,
    team = team.toTeamUiModel()
)

fun Team.toTeamUiModel() = CommentsUi.Team(
    id = id,
    name = name,
    logo = logo,
    color = color.parseHexColor()
)