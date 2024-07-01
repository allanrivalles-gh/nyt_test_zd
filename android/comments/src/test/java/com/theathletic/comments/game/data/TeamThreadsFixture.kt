package com.theathletic.comments.game.data

import com.theathletic.comments.game.Team
import com.theathletic.comments.game.TeamThread
import com.theathletic.comments.game.TeamThreads
import com.theathletic.fragment.TeamThread as RemoteTeamThread

fun teamFixture(
    id: String = "teamId",
    legacyId: String = "1",
    name: String = "teamName",
    color: String = "teamColor",
    logo: String = "teamLogo"
) = Team(id, legacyId, name, color, logo)

fun teamThreadsFixture(
    contentId: String = "contentId",
    contentType: String = "contentType",
    currentThread: TeamThread = threadFixture(),
    threads: List<TeamThread> = emptyList()
) = TeamThreads(contentId, contentType, currentThread, threads)

fun threadFixture(
    label: String = "Team Thread",
    teamId: String = "teamId",
    teamName: String = "Team Name",
    color: String = "",
    teamLogo: String = ""
) = TeamThread(
    label,
    Team(teamId, teamId, teamName, color, teamLogo)
)

fun remoteTeamThreadFixture(
    label: String = "Team Thread",
    teamId: String = "teamId",
    teamName: String = "Team Name"
) = RemoteTeamThread(
    label = label,
    team = RemoteTeamThread.Team(
        id = teamId,
        legacy_team = RemoteTeamThread.Legacy_team(
            id = teamId
        ),
        name = teamName,
        color_contrast = "",
        logos = emptyList()
    )
)