package com.theathletic.gamedetail.boxscore.ui.soccer

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.SoccerMomentsUi
import com.theathletic.boxscore.ui.modules.EventSoccerMomentModule
import com.theathletic.boxscore.ui.modules.NoKeyMomentModule
import com.theathletic.boxscore.ui.modules.PlaysPeriodHeaderModule
import com.theathletic.boxscore.ui.modules.RecentMomentsModule
import com.theathletic.boxscore.ui.modules.ScoringSoccerMomentModule
import com.theathletic.boxscore.ui.modules.SoccerPenaltyShootoutModule
import com.theathletic.boxscore.ui.modules.StandardSoccerMomentModule
import com.theathletic.boxscore.ui.modules.TwoItemToggleButtonModule
import com.theathletic.boxscore.ui.playbyplay.SoccerPenaltyShootoutUI
import com.theathletic.feed.ui.FeedModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.boxscore.ui.common.toPeriodLabel
import com.theathletic.gamedetail.boxscore.ui.playbyplay.BoxScorePlayByPlayState
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.gamedetail.data.local.PlayByPlayLocalModel
import com.theathletic.gamedetail.data.local.SoccerPlayType
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.utility.orLongDash

class SoccerPlayByPlayRenderers @AutoKoin constructor() {

    fun renderModules(data: BoxScorePlayByPlayState): List<FeedModule> {
        return data.gamePlays?.let { gamePlays ->
            val modules = mutableListOf<FeedModule>()
            modules.add(
                TwoItemToggleButtonModule(
                    id = data.gamePlays.id,
                    itemOneLabel = StringWithParams(R.string.plays_soccer_plays_option_title_all_moments),
                    itemTwoLabel = StringWithParams(R.string.box_score_key_moments_title),
                    isFirstItemSelected = data.isFirstViewItemSelected
                )
            )

            if (data.isFirstViewItemSelected) {
                modules.addAll(renderMoments(data = data, isKeyMoment = false))
            } else {
                modules.addAll(renderMoments(data = data, isKeyMoment = true))
            }

            modules
        } ?: emptyList()
    }

    private fun renderMoments(
        data: BoxScorePlayByPlayState,
        isKeyMoment: Boolean
    ): List<FeedModule> {
        val modules = mutableListOf<FeedModule>()
        val moments = data.gamePlays?.plays ?: return emptyList()
        val allHalfPlays =
            moments.filterIsInstance<GameDetailLocalModel.SoccerKeyPlay>()
                .groupBy { it.period }
        val shootOutPlay = moments.filterIsInstance<GameDetailLocalModel.SoccerShootoutPlay>()

        allHalfPlays.entries.forEach { groupPlays ->
            renderPlayModules(
                modules = modules,
                groupPlays = groupPlays.value,
                shootOutPlay = shootOutPlay,
                data = data,
                isKeyMoment = isKeyMoment
            )
        }

        if (isKeyMoment && modules.isEmpty()) {
            modules.add(
                NoKeyMomentModule(
                    id = data.gamePlays.id
                )
            )
        }

        return modules
    }

    private fun renderPlayModules(
        modules: MutableList<FeedModule>,
        groupPlays: List<GameDetailLocalModel.SoccerKeyPlay>,
        shootOutPlay: List<GameDetailLocalModel.SoccerShootoutPlay>,
        data: BoxScorePlayByPlayState,
        isKeyMoment: Boolean
    ) {
        val headerPlay = if (groupPlays.first().occurredAt > groupPlays.last().occurredAt) {
            groupPlays.first()
        } else {
            groupPlays.last()
        }

        modules.add(headerPlay.toPeriodHeader(data))
        if (data.currentExpandedPeriod == headerPlay.period) {
            if (headerPlay.period == Period.PENALTY_SHOOTOUT) {
                data.gamePlays?.id?.let { id ->
                    modules.add(
                        shootOutPlay.toPenaltyShootout(
                            id,
                            data.gamePlays.homeTeam,
                            data.gamePlays.awayTeam
                        )
                    )
                }
                val finalPlay = groupPlays.find { it.playType == SoccerPlayType.END_OF_GAME }
                finalPlay?.let {
                    modules.add(createStandardSoccerMomentModule(finalPlay, true))
                }
            } else {
                val plays = if (isKeyMoment) groupPlays.filter { it.keyPlay } else groupPlays
                if (isKeyMoment && plays.isEmpty()) {
                    modules.add(
                        NoKeyMomentModule(
                            id = headerPlay.id
                        )
                    )
                } else {
                    modules.addAll(
                        plays.toSubMoments(data.gamePlays)
                    )
                }
            }
        }
    }

    fun createSoccerRecentMomentsModule(
        game: GameDetailLocalModel,
        recentMoments: List<GameDetailLocalModel.SoccerPlay>
    ): FeedModuleV2 {
        return RecentMomentsModule(
            id = game.id,
            recentMoments = recentMoments.toRecentMoments(game),
            soccerPenaltyShootoutModule = recentMoments.toSoccerPenaltyShootoutModule(
                game.id,
                game.firstTeam?.team,
                game.secondTeam?.team
            )
        )
    }

    private fun List<GameDetailLocalModel.SoccerPlay>.toRecentMoments(game: GameDetailLocalModel): List<SoccerMomentsUi> {
        val recentMoments = mutableListOf<SoccerMomentsUi>()
        this.forEach { moment ->
            when {
                moment.playType.isGoalPlay -> {
                    recentMoments.add(scoringSoccerMomentBuilder(moment, game))
                }
                moment.playType.isCardEvent -> {
                    val eventIcon = moment.playType.toEventIcon()
                    eventIcon?.let {
                        recentMoments.add(
                            eventSoccerMomentBuilder(moment, eventIcon)
                        )
                    }
                }
                else -> {
                    recentMoments.add(
                        standardSoccerMomentBuilder(moment)
                    )
                }
            }
        }

        return recentMoments
    }

    private val SoccerPlayType.isCardEvent
        get() = when (this) {
            SoccerPlayType.RED_CARD,
            SoccerPlayType.SECOND_YELLOW_CARD,
            SoccerPlayType.SUBSTITUTION,
            SoccerPlayType.INJURY_SUBSTITUTION,
            SoccerPlayType.YELLOW_CARD -> true
            else -> false
        }

    private val SoccerPlayType.isGoalPlay
        get() = when (this) {
            SoccerPlayType.GOAL,
            SoccerPlayType.OWN_GOAL,
            SoccerPlayType.PENALTY_GOAL -> true
            else -> false
        }

    private fun List<GameDetailLocalModel.SoccerPlay>.toSubMoments(gamePlays: PlayByPlayLocalModel?): List<FeedModule> {
        val subMoments = mutableListOf<FeedModule>()
        this.forEachIndexed { index, moment ->
            when {
                moment.playType.isGoalPlay -> {
                    subMoments.add(
                        createScoringSoccerMomentModule(
                            moment,
                            gamePlays,
                            index != subMoments.lastIndex
                        )
                    )
                }
                moment.playType.isCardEvent -> {
                    val eventIcon = moment.playType.toEventIcon()
                    eventIcon?.let { icon ->
                        subMoments.add(
                            createEventSoccerMomentModule(
                                moment,
                                icon,
                                index != subMoments.lastIndex
                            )
                        )
                    }
                }
                else -> {
                    subMoments.add(
                        createStandardSoccerMomentModule(
                            moment,
                            index != subMoments.lastIndex
                        )
                    )
                }
            }
        }

        return subMoments
    }

    private fun createScoringSoccerMomentModule(
        moment: GameDetailLocalModel.SoccerPlay,
        gamePlays: PlayByPlayLocalModel?,
        showDivider: Boolean,
    ) = ScoringSoccerMomentModule(
        homeTeamScore = moment.homeTeamScore.toString(),
        awayTeamScore = moment.awayTeamScore.toString(),
        clock = moment.gameTime.orEmpty(),
        description = moment.description,
        headerLabel = moment.headerLabel.orEmpty(),
        teamLogos = moment.team?.logos ?: emptyList(),
        teamColor = moment.team?.accentColor,
        awayTeamName = gamePlays?.awayTeam?.alias.orEmpty(),
        homeTeamName = gamePlays?.homeTeam?.alias.orEmpty(),
        showDivider = showDivider,
        id = moment.id,
    )

    private fun createEventSoccerMomentModule(
        moment: GameDetailLocalModel.SoccerPlay,
        icon: Int,
        showDivider: Boolean,
    ) = EventSoccerMomentModule(
        id = moment.id,
        headerLabel = moment.headerLabel.orEmpty(),
        clock = moment.gameTime.orEmpty(),
        description = moment.description,
        teamLogos = moment.team?.logos ?: emptyList(),
        iconRes = icon,
        showDivider = showDivider
    )

    private fun createStandardSoccerMomentModule(
        moment: GameDetailLocalModel.SoccerPlay,
        showDivider: Boolean,
    ) = StandardSoccerMomentModule(
        id = moment.id,
        teamLogos = moment.team?.logos ?: emptyList(),
        description = moment.description.orEmpty(),
        clock = moment.gameTime.orEmpty(),
        headerLabel = moment.headerLabel.orEmpty(),
        showDivider = showDivider
    )

    private fun GameDetailLocalModel.SoccerKeyPlay.toPeriodHeader(
        data: BoxScorePlayByPlayState
    ): PlaysPeriodHeaderModule {
        return PlaysPeriodHeaderModule(
            title = period.toPeriodLabel,
            firstTeamAlias = data.gamePlays?.homeTeam?.alias.orEmpty(),
            secondTeamAlias = data.gamePlays?.awayTeam?.alias.orEmpty(),
            firstTeamScore = this.homeTeamScore.toString(),
            secondTeamScore = this.awayTeamScore.toString(),
            id = this.period.toString(),
            expanded = data.currentExpandedPeriod == this.period,
            periodData = this.toPeriodData(data.gamePlays),
            showSubtitle = this.toPeriodData(data.gamePlays).parameters.firstOrNull().toString().isNotEmpty()
        )
    }

    private fun GameDetailLocalModel.SoccerKeyPlay.toPeriodData(gamePlays: PlayByPlayLocalModel?): StringWithParams {
        return when (this.period) {
            Period.FIRST_HALF,
            Period.SECOND_HALF,
            Period.EXTRA_TIME_FIRST_HALF,
            Period.EXTRA_TIME_SECOND_HALF -> StringWithParams(
                R.string.plays_soccer_plays_chances_title,
                gamePlays?.homeTeam?.alias.orEmpty(),
                this.homeChancesCreated,
                gamePlays?.awayTeam?.alias.orEmpty(),
                this.awayChancesCreated
            )
            else -> StringWithParams(R.string.empty_string)
        }
    }
}

fun createPenaltyShots(
    soccerShootoutPlays: List<GameDetailLocalModel.SoccerShootoutPlay>,
    firstTeamId: String
): List<SoccerPenaltyShootoutUI.PenaltyShot> {
    val shootOuts = soccerShootoutPlays.chunked(2)
    val penaltyShots = mutableListOf<SoccerPenaltyShootoutUI.PenaltyShot>()

    shootOuts.forEachIndexed { index, shots ->
        val (firstTeamIndex, secondTeamIndex) = getTeamIndex(shots, firstTeamId)
        val firstTeam = shots.getOrNull(firstTeamIndex)
        val secondTeam = shots.getOrNull(secondTeamIndex)
        penaltyShots.add(
            SoccerPenaltyShootoutUI.PenaltyShot(
                penaltyTitle = StringWithParams(R.string.box_score_soccer_penalty_play_title, index.plus(1)),
                firstPenaltyState = firstTeam?.playType.toPenaltyState(),
                secondPenaltyState = secondTeam?.playType.toPenaltyState(),
                firstTeamPlayerName = firstTeam?.shooter?.displayName.orLongDash(),
                secondTeamPlayerName = secondTeam?.shooter?.displayName.orLongDash(),
            )
        )
    }

    if (penaltyShots.size < 5) {
        val penaltyShotsPending = 5 - penaltyShots.size
        penaltyShots.addAll(
            MutableList(penaltyShotsPending) {
                SoccerPenaltyShootoutUI.PenaltyShot(
                    penaltyTitle = StringWithParams(
                        R.string.box_score_soccer_penalty_play_title,
                        penaltyShots.size.plus(it + 1)
                    ),
                    firstPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.PENDING,
                    secondPenaltyState = SoccerPenaltyShootoutUI.PenaltyState.PENDING,
                    firstTeamPlayerName = "\u2014",
                    secondTeamPlayerName = "\u2014",
                )
            }
        )
    }

    return penaltyShots
}

// This method is used to identify which team has first went for the penalty shot
// As the decision for the first team to go is based on coin flip
// Using this method we identify the index for the first team to show correct player name and score
fun getTeamIndex(shots: List<GameDetailLocalModel.SoccerShootoutPlay>, firstTeamId: String): Pair<Int, Int> {
    return if (shots.first().team?.id == firstTeamId)
        Pair(0, 1)
    else
        Pair(1, 0)
}

private fun SoccerPlayType?.toPenaltyState(): SoccerPenaltyShootoutUI.PenaltyState {
    return when (this) {
        SoccerPlayType.PENALTY_SHOT_SAVED,
        SoccerPlayType.PENALTY_SHOT_MISSED -> SoccerPenaltyShootoutUI.PenaltyState.MISSED
        SoccerPlayType.PENALTY_GOAL -> SoccerPenaltyShootoutUI.PenaltyState.SCORED
        else -> SoccerPenaltyShootoutUI.PenaltyState.PENDING
    }
}

fun standardSoccerMomentBuilder(moment: GameDetailLocalModel.SoccerPlay) =
    SoccerMomentsUi.StandardSoccerMoment(
        id = moment.id,
        teamLogos = moment.team?.logos ?: emptyList(),
        description = moment.description,
        clock = moment.gameTime.orEmpty(),
        headerLabel = moment.headerLabel.orEmpty()
    )

fun eventSoccerMomentBuilder(moment: GameDetailLocalModel.SoccerPlay, eventIcon: Int) =
    SoccerMomentsUi.EventSoccerMoment(
        id = moment.id,
        headerLabel = moment.headerLabel.orEmpty(),
        clock = moment.gameTime.orEmpty(),
        description = moment.description,
        teamLogos = moment.team?.logos ?: emptyList(),
        iconRes = eventIcon
    )

fun scoringSoccerMomentBuilder(
    moment: GameDetailLocalModel.SoccerPlay,
    game: GameDetailLocalModel
) =
    SoccerMomentsUi.ScoringSoccerMoment(
        id = moment.id,
        teamLogos = moment.team?.logos ?: emptyList(),
        teamColor = moment.team?.accentColor,
        description = moment.description,
        clock = moment.gameTime.orEmpty(),
        headerLabel = moment.headerLabel.orEmpty(),
        awayTeamName = game.awayTeam?.team?.alias.orEmpty(),
        homeTeamName = game.homeTeam?.team?.alias.orEmpty(),
        awayTeamScore = moment.awayTeamScore.toString(),
        homeTeamScore = moment.homeTeamScore.toString()
    )

fun SoccerPlayType.toEventIcon(): Int? {
    return when (this) {
        SoccerPlayType.RED_CARD -> R.drawable.ic_soccer_card_red
        SoccerPlayType.SECOND_YELLOW_CARD -> R.drawable.ic_soccer_card_yellow_red
        SoccerPlayType.SUBSTITUTION, SoccerPlayType.INJURY_SUBSTITUTION -> R.drawable.ic_soccer_substitute_on_off
        SoccerPlayType.YELLOW_CARD -> R.drawable.ic_soccer_card_yellow
        else -> null
    }
}

fun List<GameDetailLocalModel.SoccerPlay>.toSoccerPenaltyShootoutModule(
    id: String,
    firstTeam: GameDetailLocalModel.Team?,
    secondTeam: GameDetailLocalModel.Team?
): FeedModule? {
    val shootoutPlays = this.filterIsInstance<GameDetailLocalModel.SoccerShootoutPlay>().sortedBy { it.occurredAt }
    if (shootoutPlays.isEmpty()) return null
    return shootoutPlays.toPenaltyShootout(
        id,
        firstTeam,
        secondTeam
    )
}

fun List<GameDetailLocalModel.SoccerShootoutPlay>.toPenaltyShootout(
    id: String,
    firstTeam: GameDetailLocalModel.Team?,
    secondTeam: GameDetailLocalModel.Team?
): FeedModule {
    return SoccerPenaltyShootoutModule(
        id = id,
        firstTeamName = firstTeam?.alias.orEmpty(),
        firstTeamLogos = firstTeam?.logos ?: emptyList(),
        secondTeamName = secondTeam?.alias.orEmpty(),
        secondTeamLogos = secondTeam?.logos ?: emptyList(),
        penaltyShots = createPenaltyShots(this, firstTeam?.id.orEmpty()),
    )
}