package com.theathletic.brackets.data.local

fun TournamentRoundGame.winnerTeamId(): String? {
    // there can't be a winner if the game hasn't finished yet
    if (phase != TournamentRoundGame.Phase.PostGame) return null

    val homeTeam = this.homeTeam?.data() ?: return null
    val awayTeam = this.awayTeam?.data() ?: return null
    val homeDecidingScore = homeTeam.decidingScore(awayTeam) ?: return null
    val awayDecidingScore = awayTeam.decidingScore(homeTeam) ?: return null

    return if (homeDecidingScore > awayDecidingScore) {
        homeTeam.id
    } else {
        awayTeam.id
    }
}

private fun TournamentRoundGame.Team.data(): TournamentRoundGame.TeamData? {
    if (this is TournamentRoundGame.Team.Confirmed) return data
    return null
}

// If there was a penalty shootout (soccer) then use the penalty score
private fun TournamentRoundGame.TeamData.decidingScore(other: TournamentRoundGame.TeamData): Int? {
    // sanity check to make sure teams are in sync
    if (other.penaltyScore != null) return penaltyScore
    return score
}