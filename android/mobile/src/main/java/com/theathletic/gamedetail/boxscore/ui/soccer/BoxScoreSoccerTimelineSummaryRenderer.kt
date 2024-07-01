package com.theathletic.gamedetail.boxscore.ui.soccer

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.TimelineSummaryModel
import com.theathletic.boxscore.ui.modules.TimelineSummaryModule
import com.theathletic.datetime.Datetime
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreCommonRenderers
import com.theathletic.gamedetail.data.local.CardType
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GoalType
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.utility.orShortDash
import java.util.concurrent.atomic.AtomicInteger

class BoxScoreSoccerTimelineSummaryRenderer @AutoKoin constructor(
    private val commonRenderers: BoxScoreCommonRenderers
) {

    fun createTimelineSummaryModule(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        if (game.isGameInProgressOrCompleted.not()) return null
        pageOrder.getAndIncrement()
        val goalSummary = game.toGoalsSummary()
        val cardSummary = game.toCardsSummary()
        val expectedGoals = game.toExpectedGoals(game)
        if (goalSummary == null && cardSummary == null && expectedGoals.showExpectedGoals.not()) return null

        return TimelineSummaryModule(
            id = game.id,
            expectedGoals = expectedGoals,
            timelineSummary = listOfNotNull(
                goalSummary,
                cardSummary
            )
        )
    }

    private fun GameDetailLocalModel.toExpectedGoals(game: GameDetailLocalModel): TimelineSummaryModel.ExpectedGoals {
        if (!this.isGameInProgressOrCompleted || containsAPenaltyScore(this)) return TimelineSummaryModel.ExpectedGoals()
        val firstTeamExpectedGoals = (this.homeTeam as? GameDetailLocalModel.SoccerGameTeam)?.expectedGoals
        val secondTeamExpectedGoals = (this.awayTeam as? GameDetailLocalModel.SoccerGameTeam)?.expectedGoals
        if (firstTeamExpectedGoals == null || secondTeamExpectedGoals == null) return TimelineSummaryModel.ExpectedGoals()
        return TimelineSummaryModel.ExpectedGoals(
            firstTeamValue = commonRenderers.formatStatisticValue(firstTeamExpectedGoals).orShortDash(),
            secondTeamValue = commonRenderers.formatStatisticValue(secondTeamExpectedGoals).orShortDash(),
            showExpectedGoals = true
        )
    }

    private fun containsAPenaltyScore(game: GameDetailLocalModel) =
        (game.firstTeam as? GameDetailLocalModel.SoccerGameTeam)?.penaltyScore != null ||
            (game.secondTeam as? GameDetailLocalModel.SoccerGameTeam)?.penaltyScore != null
}

private fun GameDetailLocalModel.toGoalsSummary(): TimelineSummaryModel.SummaryItem? {
    val firstTeamGoals = this.firstTeam?.team?.let { team ->
        this.events.filterIsInstance<GameDetailLocalModel.GoalEvent>()
            .filter { it.team == team }
            .map { goal -> goal.toEventSummary() }
    } ?: emptyList()

    val secondTeamGoals = this.secondTeam?.team?.let { team ->
        this.events.filterIsInstance<GameDetailLocalModel.GoalEvent>()
            .filter { it.team == team }
            .map { goal -> goal.toEventSummary() }
    } ?: emptyList()

    val firstTeamOwnGoals = firstTeamGoals.filter { it.ownGoal }
    var firstTeamGoalSummary = firstTeamGoals.filter { !it.ownGoal }.toMutableList()
    val secondTeamOwnGoals = secondTeamGoals.filter { it.ownGoal }
    var secondTeamGoalSummary = secondTeamGoals.filter { !it.ownGoal }.toMutableList()

    firstTeamGoalSummary = (firstTeamGoalSummary + secondTeamOwnGoals)
        .toCondenseList()
        .sortedBy { it.occurredAt }
        .toMutableList()

    secondTeamGoalSummary = (secondTeamGoalSummary + firstTeamOwnGoals)
        .toCondenseList()
        .sortedBy { it.occurredAt }
        .toMutableList()

    if (firstTeamGoalSummary.isNotEmpty() || secondTeamGoalSummary.isNotEmpty()) {
        return TimelineSummaryModel.SummaryItem(
            R.drawable.ic_soccer_goal,
            firstTeamGoalSummary.toSummaryList(),
            secondTeamGoalSummary.toSummaryList()
        )
    }

    return null
}

private fun GameDetailLocalModel.toCardsSummary(): TimelineSummaryModel.SummaryItem? {
    // Only show red and Y2C card events
    val firstTeamCards = this.firstTeam?.team?.let { team ->
        this.events.filterIsInstance<GameDetailLocalModel.CardEvent>()
            .filter { filterCardEvents(it, team) }
            .map { card -> card.toEventSummary() }
            .sortedBy { it.occurredAt }
    } ?: emptyList()

    val secondTeamCards = this.secondTeam?.team?.let { team ->
        this.events.filterIsInstance<GameDetailLocalModel.CardEvent>()
            .filter { filterCardEvents(it, team) }
            .map { card -> card.toEventSummary() }
            .sortedBy { it.occurredAt }
    } ?: emptyList()

    if (firstTeamCards.isNotEmpty() || secondTeamCards.isNotEmpty()) {
        return TimelineSummaryModel.SummaryItem(
            R.drawable.ic_soccer_card_red,
            firstTeamCards.toSummaryList(),
            secondTeamCards.toSummaryList()
        )
    }
    return null
}

private fun filterCardEvents(
    it: GameDetailLocalModel.CardEvent,
    team: GameDetailLocalModel.Team
) = it.team == team &&
    (it.cardType == CardType.RED || it.cardType == CardType.YELLOW_2ND)

private fun GameDetailLocalModel.CardEvent.toEventSummary(): EventSummary {
    return EventSummary(
        playerId = this.id,
        occurredAt = this.occurredAt,
        matchTime = StringWrapper(this.matchTimeDisplay),
        playerDisplayName = this.cardedPlayer.displayName.orEmpty(),
        eventDisplayString = mutableListOf(StringWrapper(this.matchTimeDisplay))
    )
}

private fun List<EventSummary>.toSummaryList(): List<TimelineSummaryModel.DisplayStrings> {
    return this.map { it.toDisplayStrings() }
}

private fun EventSummary.toDisplayStrings(): TimelineSummaryModel.DisplayStrings {
    val displayStrings = mutableListOf<ResourceString>()
    displayStrings.add(StringWrapper(this.playerDisplayName))
    displayStrings.addAll(this.eventDisplayString)
    return TimelineSummaryModel.DisplayStrings(
        displayStrings
    )
}

private fun GameDetailLocalModel.GoalEvent.toEventSummary(): EventSummary {
    return EventSummary(
        playerId = this.scorer.id,
        occurredAt = this.occurredAt,
        matchTime = when (this.goalType) {
            GoalType.PENALTY_GOAL -> StringWithParams(
                R.string.box_score_soccer_timeline_goal_penalty,
                this.matchTimeDisplay
            )
            GoalType.OWN_GOAL -> StringWithParams(
                R.string.box_score_soccer_timeline_goal_own,
                this.matchTimeDisplay
            )
            else -> StringWrapper(this.matchTimeDisplay)
        },
        playerDisplayName = this.scorer.displayName.orEmpty(),
        ownGoal = goalType == GoalType.OWN_GOAL,
        eventDisplayString = emptyList()
    )
}

private fun List<EventSummary>.toCondenseList(): List<EventSummary> {
    val condensedList = this.groupBy { it.playerId }
    return condensedList.map { entry ->
        EventSummary(
            eventDisplayString = entry.value.toParameterizedString(),
            matchTime = StringWrapper(""),
            ownGoal = false,
            playerDisplayName = entry.value.first().playerDisplayName,
            occurredAt = entry.value.first().occurredAt,
            playerId = entry.key
        )
    }
}

private fun List<EventSummary>.toParameterizedString(): List<ResourceString> {
    return this.map { it.matchTime }
}

data class EventSummary(
    val playerId: String,
    val playerDisplayName: String,
    val occurredAt: Datetime,
    val matchTime: ResourceString,
    val eventDisplayString: List<ResourceString>,
    val ownGoal: Boolean = false
)