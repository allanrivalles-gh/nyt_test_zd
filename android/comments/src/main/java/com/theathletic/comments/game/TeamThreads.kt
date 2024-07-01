package com.theathletic.comments.game

data class TeamThreads(
    val contentId: String,
    val contentType: String,
    val current: TeamThread,
    val teamThreads: List<TeamThread>
) {
    val teamId: String = current.team.id
    private val secondThread = if (teamThreads.size > 1) {
        teamThreads.filterNot { it.teamId == current.team.id }.firstOrNull()
    } else {
        null
    }

    fun getTeamThreadContextSwitch(): TeamThreadContextSwitch {
        val firstThread = current

        return TeamThreadContextSwitch(firstThread, secondThread)
    }
}

data class TeamThread(
    val label: String,
    val team: Team
) {
    val teamId: String = team.id
}

data class TeamThreadContextSwitch(
    val currentTeamThread: TeamThread,
    val secondTeamThread: TeamThread?
)

data class Team(
    val id: String,
    val legacyId: String?,
    val name: String,
    val color: String,
    val logo: String
)