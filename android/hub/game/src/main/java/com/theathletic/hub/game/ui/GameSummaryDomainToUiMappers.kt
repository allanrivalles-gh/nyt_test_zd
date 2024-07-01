package com.theathletic.hub.game.ui

import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.boxscore.ui.formatters.BoxScoreBaseballInningFormatter
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.entity.main.League
import com.theathletic.entity.main.Sport
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.hub.game.R
import com.theathletic.hub.game.data.local.GameSummary
import com.theathletic.scores.GameUtil
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.orShortDash
import com.theathletic.utility.orShortDash
import com.theathletic.utility.safeLet

fun GameSummary.mapToToolbarLabelUiModel(): ResourceString {
    return GameUtil.buildGameTitle(
        firstTeamDisplayString = firstTeam?.alias.orShortDash(),
        secondTeamDisplayString = secondTeam?.alias.orShortDash(),
        firstTeamTbd = isFirstTeamTbd,
        secondTeamTbd = isSecondTeamTbd,
        isSoccer = sport == Sport.SOCCER
    )
}

fun GameSummary.mapToTeamUiModel(
    isFirstTeam: Boolean,
    isLoaded: Boolean,
    isFollowable: Boolean,
    supportedLeagues: SupportedLeagues
): GameDetailUi.TeamSummary {
    val currentTeam = if (isFirstTeam) firstTeam else secondTeam
    return currentTeam?.let { team ->
        val currentRanking = team.toCurrentRanking()
        GameDetailUi.TeamSummary(
            teamId = team.id,
            legacyId = team.legacyId,
            name = team.alias.asResourceString(),
            logoUrls = team.logos,
            score = if (isGameInProgressOrCompleted) currentTeam.score else null,
            currentRecord = team.toCurrentRecord(sport),
            isWinner = isTeamWin(isFirstTeam),
            isFollowable = isFollowable,
            currentRanking = currentRanking,
            showCollegeCurrentRanking = supportedLeagues.isCollegeLeague(league.legacyLeague),
            showCurrentRanking = showTeamCurrentRanking(currentRanking)
        )
    } ?: emptyTeamUiModel(isLoaded)
}

fun GameSummary.mapToGameInfoUiModel(
    supportedLeagues: SupportedLeagues,
    isUnitedStatesOrCanada: Boolean
): GameDetailUi.GameInfo? {
    return when (status) {
        GameStatus.FINAL -> if (sport == Sport.SOCCER) mapToSoccerPostGameWinnerTitle() else null

        GameStatus.IN_PROGRESS -> null

        else -> if (sport == Sport.SOCCER && supportedLeagues.isRecentFormSupportingLeague(league.legacyLeague)) {
            mapToRecentFormGameInfo(isUnitedStatesOrCanada)
        } else {
            null
        }
    }
}

fun GameSummary.Team?.mapToTeamStatus(
    isGameInProgress: Boolean,
    sport: Sport
): List<GameDetailUi.TeamStatus> {
    if (this == null || isGameInProgress.not()) return emptyList()
    return when (sport) {
        Sport.HOCKEY -> mapToPowerPlay()
        Sport.BASKETBALL -> mapToBasketballTimeouts()
        Sport.FOOTBALL -> mapToAmericanFootballTimeouts()
        else -> emptyList()
    }
}

fun GameSummary.mapToGameStatus(
    dateUtility: DateUtility,
    inningFormatter: BoxScoreBaseballInningFormatter
): GameDetailUi.GameStatus {
    return when (status) {
        GameStatus.FINAL -> {
            when (sport) {
                Sport.SOCCER -> mapToSoccerPostGameStatus(dateUtility)
                else -> mapToPostGameStatus(dateUtility)
            }
        }
        GameStatus.IN_PROGRESS -> {
            when (sport) {
                Sport.BASEBALL -> mapToBaseballInGameStatus(inningFormatter)
                Sport.SOCCER -> mapToSoccerInGameStatus()
                else -> mapToInGameStatus()
            }
        }
        else -> mapToPregameStatus(dateUtility)
    }
}

fun GameSummary.mapToGameTitleUiModel(): ResourceString? {
    return when {
        sport == Sport.SOCCER && gameTitle == null -> ResourceString.StringWrapper(league.displayName)
        sport == Sport.SOCCER -> ResourceString.StringWithParams(
            R.string.game_detail_header_soccer_game_title,
            league.displayName,
            gameTitle.orEmpty()
        )
        else -> gameTitle?.asResourceString()
    }
}

private fun emptyTeamUiModel(isLoaded: Boolean) = GameDetailUi.TeamSummary(
    teamId = "",
    legacyId = 0L,
    name = if (isLoaded) ResourceString.StringWithParams(R.string.global_tbc) else "-".asResourceString(),
    logoUrls = emptyList(),
    score = null,
    isWinner = false,
    currentRecord = null,
    currentRanking = null,
    showCurrentRanking = false,
    isFollowable = true
)

private fun GameSummary.isTeamWin(isFirstTeam: Boolean): Boolean {
    return safeLet(firstTeam?.score, secondTeam?.score) { firstScore, secondScore ->
        when {
            status != GameStatus.FINAL -> true
            isFirstTeam && secondScore > firstScore -> false
            !isFirstTeam && firstScore > secondScore -> false
            else -> true
        }
    } ?: true
}

private fun GameSummary.Team.toCurrentRanking() =
    when (this) {
        is GameSummary.SoccerTeam -> currentRanking
        is GameSummary.AmericanFootballTeam -> currentRanking
        is GameSummary.BasketballTeam -> currentRanking
        else -> null
    }

private fun GameSummary.Team.toCurrentRecord(sport: Sport) =
    if (sport != Sport.SOCCER) currentRecord else null

private fun GameSummary.showTeamCurrentRanking(currentRanking: String?) =
    if (currentRanking == null) false else league.legacyLeague.isCurrentRankingSupportedLeague()

private fun League.isCurrentRankingSupportedLeague() =
    when (this) {
        League.EPL,
        League.EFL,
        League.LEAGUE_ONE,
        League.LEAGUE_TWO,
        League.SCOTTISH_PREMIERE,
        League.MLS,
        League.NWSL,
        League.LA_LIGA -> true
        else -> false
    }

fun GameSummary.formattedStartDate(dateUtility: DateUtility): String {
    return dateUtility.formatGMTDate(
        scheduleAt,
        DisplayFormat.WEEKDAY_MONTH_DATE_SHORT
    )
}

private fun GameSummary.formattedStartTime(dateUtility: DateUtility): ResourceString {
    return when {
        status == GameStatus.CANCELED ->
            ResourceString.StringWithParams(R.string.game_detail_pre_game_canceled_label)
        status == GameStatus.POSTPONED ->
            ResourceString.StringWithParams(R.string.game_detail_pre_game_postponed_label)
        status == GameStatus.SUSPENDED ->
            ResourceString.StringWithParams(R.string.game_detail_pre_game_suspended_label)
        status == GameStatus.IF_NECESSARY ->
            ResourceString.StringWithParams(R.string.game_detail_pre_game_if_necessary_label)
        isScheduledTimeTbd ->
            ResourceString.StringWithParams(R.string.global_tbd)
        else ->
            ResourceString.StringWrapper(dateUtility.formatGMTDate(scheduleAt, DisplayFormat.HOURS_MINUTES))
    }
}

private fun GameSummary.mapToPostGameStatus(dateUtility: DateUtility): GameDetailUi.GameStatus.PostGameStatus {
    return GameDetailUi.GameStatus.PostGameStatus(
        gamePeriod = period.toHeaderPeriodLabel(sport, status).orShortDash(),
        scheduledDate = formattedStartDate(dateUtility)
    )
}

private fun GameSummary.mapToInGameStatus(): GameDetailUi.GameStatus.InGameStatus {
    return GameDetailUi.GameStatus.InGameStatus(
        isGameDelayed = status == GameStatus.DELAYED,
        gameStatePrimary = this.gameStatePrimary,
        gameStateSecondary = this.gameStateSecondary
    )
}

private fun GameSummary.mapToPregameStatus(dateUtility: DateUtility): GameDetailUi.GameStatus.PregameStatus {
    return GameDetailUi.GameStatus.PregameStatus(
        scheduledDate = formattedStartDate(dateUtility),
        scheduledTime = formattedStartTime(dateUtility)
    )
}

@SuppressWarnings("LongMethod")
fun Period.toHeaderPeriodLabel(sport: Sport, status: GameStatus): ResourceString? {
    if ((status == GameStatus.IN_PROGRESS || status == GameStatus.FINAL).not()) return null
    return when (this) {
        Period.FIRST_QUARTER,
        Period.FIRST_HALF,
        Period.FIRST_PERIOD -> ResourceString.StringWithParams(R.string.game_detail_post_game_1st_quarter_label)
        Period.SECOND_QUARTER,
        Period.SECOND_HALF,
        Period.SECOND_PERIOD -> ResourceString.StringWithParams(R.string.game_detail_post_game_2nd_quarter_label)
        Period.THIRD_QUARTER,
        Period.THIRD_PERIOD -> ResourceString.StringWithParams(R.string.game_detail_post_game_3rd_quarter_label)
        Period.FOURTH_QUARTER -> ResourceString.StringWithParams(R.string.game_detail_post_game_4th_quarter_label)
        Period.HALF_TIME -> ResourceString.StringWithParams(R.string.game_detail_post_game_halftime_label)
        Period.OVER_TIME -> ResourceString.StringWithParams(R.string.game_detail_post_game_overtime_label)
        Period.OVER_TIME_2 -> ResourceString.StringWithParams(R.string.game_detail_post_game_overtime_formatter, 2)
        Period.OVER_TIME_3 -> ResourceString.StringWithParams(R.string.game_detail_post_game_overtime_formatter, 3)
        Period.OVER_TIME_4 -> ResourceString.StringWithParams(R.string.game_detail_post_game_overtime_formatter, 4)
        Period.OVER_TIME_5 -> ResourceString.StringWithParams(R.string.game_detail_post_game_overtime_formatter, 5)
        Period.OVER_TIME_6 -> ResourceString.StringWithParams(R.string.game_detail_post_game_overtime_formatter, 6)
        Period.OVER_TIME_7 -> ResourceString.StringWithParams(R.string.game_detail_post_game_overtime_formatter, 7)
        Period.OVER_TIME_8 -> ResourceString.StringWithParams(R.string.game_detail_post_game_overtime_formatter, 8)
        Period.OVER_TIME_9 -> ResourceString.StringWithParams(R.string.game_detail_post_game_overtime_formatter, 9)
        Period.OVER_TIME_10 -> ResourceString.StringWithParams(R.string.game_detail_post_game_overtime_formatter, 10)
        Period.FULL_TIME -> if (sport == Sport.SOCCER) {
            ResourceString.StringWithParams(R.string.box_score_soccer_timeline_full)
        } else {
            ResourceString.StringWithParams(R.string.game_detail_post_game_final_label)
        }
        Period.FULL_TIME_OT -> ResourceString.StringWithParams(R.string.game_detail_post_game_final_overtime_label)
        Period.FULL_TIME_OT_2 -> ResourceString.StringWithParams(
            R.string.game_detail_post_game_final_overtime_formatter,
            2
        )
        Period.FULL_TIME_OT_3 -> ResourceString.StringWithParams(
            R.string.game_detail_post_game_final_overtime_formatter,
            3
        )
        Period.FULL_TIME_OT_4 -> ResourceString.StringWithParams(
            R.string.game_detail_post_game_final_overtime_formatter,
            4
        )
        Period.FULL_TIME_OT_5 -> ResourceString.StringWithParams(
            R.string.game_detail_post_game_final_overtime_formatter,
            5
        )
        Period.FULL_TIME_OT_6 -> ResourceString.StringWithParams(
            R.string.game_detail_post_game_final_overtime_formatter,
            6
        )
        Period.FULL_TIME_OT_7 -> ResourceString.StringWithParams(
            R.string.game_detail_post_game_final_overtime_formatter,
            7
        )
        Period.FULL_TIME_OT_8 -> ResourceString.StringWithParams(
            R.string.game_detail_post_game_final_overtime_formatter,
            8
        )
        Period.FULL_TIME_OT_9 -> ResourceString.StringWithParams(
            R.string.game_detail_post_game_final_overtime_formatter,
            9
        )
        Period.FULL_TIME_OT_10 -> ResourceString.StringWithParams(
            R.string.game_detail_post_game_final_overtime_formatter,
            10
        )
        Period.SHOOTOUT -> ResourceString.StringWithParams(R.string.game_detail_post_game_shootout_label)
        Period.FULL_TIME_SO -> ResourceString.StringWithParams(R.string.game_detail_post_game_final_shootout_label)
        else -> null
    }
}