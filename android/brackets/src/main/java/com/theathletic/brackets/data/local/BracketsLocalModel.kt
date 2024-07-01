package com.theathletic.brackets.data.local

import com.theathletic.brackets.data.PlaceholderTeams
import com.theathletic.data.LocalModel
import com.theathletic.data.SizedImages
import com.theathletic.type.GameStatusCode

data class BracketsLocalModel(
    val rounds: List<TournamentRound>,
) : LocalModel

data class TournamentRoundGame(
    val id: String,
    val placeholderTeams: PlaceholderTeams?,
    val conferenceName: String?,
    val venueName: String?,
    val matchTimeDisplay: String?,
    val timeTbd: Boolean,
    val scheduledAt: Long?,
    val homeTeam: Team?,
    val awayTeam: Team?,
    val phase: Phase?,
    val status: GameStatusCode?,
    val startedAt: Long?,
    val isPlaceholder: Boolean,
) {
    enum class Phase {
        PreGame,
        InGame,
        PostGame,
    }

    sealed class Team {
        data class Confirmed(
            val data: TeamData,
        ) : Team()

        data class Placeholder(
            val name: String,
        ) : Team()
    }

    data class TeamData(
        val id: String,
        val logos: SizedImages,
        val alias: String,
        val score: Int?,
        val penaltyScore: Int?,
        val seed: Int?,
        val record: String?,
    )
}

data class TournamentRound(
    val id: String,
    val title: String,
    val isLive: Boolean,
    val type: String,
    val bracketRound: BracketRound?,
    val groups: List<TournamentRoundGroup>,
    val connected: Boolean,
) {
    enum class BracketRound(val rawValue: Int) {
        Round1(1),
        Round2(2),
        Round3(3),
        Round4(4),
        Round5(5),
        Round6(6),
    }
}

data class TournamentRoundGroup(
    val name: String?,
    val games: List<TournamentRoundGame>,
)