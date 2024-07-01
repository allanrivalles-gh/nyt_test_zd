package com.theathletic.comments.game

import com.theathletic.TeamSpecificThreadsQuery

private typealias RemoteTeamThreads = com.theathletic.fragment.TeamThread
private typealias RemoteTeam = com.theathletic.fragment.TeamThread.Team

fun TeamSpecificThreadsQuery.TeamSpecificThreads.toDomain() = TeamThreads(
    contentId = content_id,
    contentType = content_type,
    current = current_thread.fragments.teamThread.toDomain(),
    teamThreads = threads.map { it.fragments.teamThread.toDomain() }
)

fun RemoteTeamThreads.toDomain(): TeamThread {
    val team = this.team?.toDomain() ?: throw Exception("Unable to load teams for the specific thread")
    return TeamThread(label = label, team = team)
}

fun RemoteTeam.toDomain() = Team(
    id = id,
    legacyId = legacy_team?.id,
    name = name ?: "",
    color = color_contrast ?: "",
    logo = logos.firstOrNull()?.uri ?: ""
)