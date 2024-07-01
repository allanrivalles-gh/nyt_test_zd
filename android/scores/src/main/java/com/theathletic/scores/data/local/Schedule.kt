package com.theathletic.scores.data.local

import com.theathletic.data.SizedImages
import com.theathletic.datetime.Datetime
import com.theathletic.entity.main.League
import com.theathletic.utility.safeLet

data class Schedule(
    val id: String,
    val groups: List<Group>,
    val filters: List<Filter>?
) {

    data class Group(
        val navItem: NavItem,
        val sections: List<Section>
    )

    data class NavItem(
        val id: String,
        val isDefault: Boolean,
        val day: String?,
        val primaryLabel: String?,
        val secondaryLabel: String?,
        var filterSelected: String?
    )

    data class Filter(
        val id: String,
        val values: List<FilterValue>
    )

    data class FilterValue(
        val id: String,
        val isDefault: Boolean,
        val label: String
    )

    data class Section(
        val id: String,
        val title: String?,
        val subTitle: String?,
        val games: List<Game>,
        val widget: Widget?,
        val league: League? = null
    )

    data class Game(
        val gameId: String,
        val state: GameState,
        val team1: Team,
        val team2: Team,
        val info: GameInfo,
        val widget: Widget?,
        val header: String?,
        val footer: String?,
        val willUpdate: Boolean
    ) {
        fun isGameADraw(): Boolean {
            val team1Info = if (team1.info is LivePostGame) team1.info else null
            val team2Info = if (team2.info is LivePostGame) team2.info else null

            return safeLet(team1Info, team2Info) { safeTeam1Info, safeTeam2Info ->
                safeTeam1Info.isWinner.not() && safeTeam2Info.isWinner.not()
            } ?: false
        }
    }

    data class Team(
        val name: String,
        val logos: SizedImages,
        val ranking: Int?,
        val isTbd: Boolean,
        val icons: List<TeamIcon>,
        val info: TeamInfo?
    ) {
        fun isTeamTheWinner() = info is LivePostGame && info.isWinner

        fun dimTeamContent(
            gameState: GameState,
            isGameADraw: Boolean
        ): Boolean {
            return when {
                isTbd -> true
                gameState == GameState.POST_GAME && isTeamTheWinner().not() && isGameADraw.not() -> true
                else -> false
            }
        }
    }

    interface TeamInfo

    data class PreGame(
        val text: String
    ) : TeamInfo

    data class LivePostGame(
        val score: String?,
        val penaltyScore: String?,
        val isWinner: Boolean
    ) : TeamInfo

    data class GameInfo(
        val textContent: List<TextContent>,
        val widget: Widget?
    )

    sealed class TextContent {
        data class DateTime(
            val type: TextType,
            val format: DateType,
            val dateTime: Datetime,
            val isTimeTbd: Boolean,
        ) : TextContent()

        data class Odds(
            val type: TextType,
            val decimalOdds: String,
            val fractionOdds: String,
            val usOdds: String
        ) : TextContent()

        data class Standard(
            val type: TextType,
            val text: String
        ) : TextContent()

        enum class TextType {
            DEFAULT,
            DATE_TIME,
            LIVE,
            SITUATION,
            STATUS
        }

        enum class DateType {
            DATE,
            DATE_AND_TIME,
            TIME,
            UNKNOWN
        }
    }

    sealed class Widget {
        data class BaseballBases(
            val loadedBases: List<Int>
        ) : Widget()

        data class AllGamesLink(
            val linkText: String
        ) : Widget()

        data class DiscussionLink(
            val text: String
        ) : Widget()

        data class GameTickets(
            val text: String,
            val provider: String,
            val logosDark: SizedImages,
            val logosLight: SizedImages,
            val uri: String
        ) : Widget()
    }

    enum class GameState {
        PREGAME,
        LIVE_GAME,
        POST_GAME,
        UNKNOWN
    }

    enum class TeamIcon {
        AMERICAN_FOOTBALL_POSSESSION,
        SOCCER_RED_CARD,
        UNKNOWN
    }
}