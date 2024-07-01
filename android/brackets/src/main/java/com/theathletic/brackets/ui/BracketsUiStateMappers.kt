package com.theathletic.brackets.ui

import com.theathletic.brackets.data.local.TournamentRound
import com.theathletic.brackets.data.local.TournamentRoundGame
import com.theathletic.brackets.data.local.TournamentRoundGameTitleFormatter
import com.theathletic.brackets.data.local.TournamentRoundGroup
import com.theathletic.brackets.data.local.winnerTeamId
import com.theathletic.brackets.ui.components.HeaderRowUi
import com.theathletic.extension.toStringOrEmpty

fun List<TournamentRound>.toRoundUiModels(
    titleFormatter: TournamentRoundGameTitleFormatter
) = mapNotNull { round -> round.toUiModel(titleFormatter) }

fun List<TournamentRound>.toTabUiModels() = mapNotNull { round ->
    HeaderRowUi.BracketTab(
        label = round.title,
        isCurrentRound = round.isLive
    )
}

fun List<TournamentRound>.indexOfLiveRound() = withIndex().firstOrNull { it.value.isLive }?.index ?: 0

private fun TournamentRound.toUiModel(titleFormatter: TournamentRoundGameTitleFormatter) = when (bracketRound) {
    TournamentRound.BracketRound.Round1 ->
        BracketsUi.Round.Initial(groups = groups.toGroupUiModels(titleFormatter, showMatchConnector = false))
    TournamentRound.BracketRound.Round2 ->
        BracketsUi.Round.Initial(groups = groups.toGroupUiModels(titleFormatter, showMatchConnector = false))
    TournamentRound.BracketRound.Round6 -> BracketsUi.Round.SemiFinal(groups = groups.toGroupUiModels(titleFormatter))
    else -> BracketsUi.Round.Standard(groups = groups.toGroupUiModels(titleFormatter))
}

private fun List<TournamentRoundGroup>.toGroupUiModels(
    titleFormatter: TournamentRoundGameTitleFormatter,
    showMatchConnector: Boolean = true
) =
    map { group -> group.toUiModel(titleFormatter, showMatchConnector) }

private fun TournamentRoundGroup.toUiModel(
    titleFormatter: TournamentRoundGameTitleFormatter,
    showMatchConnector: Boolean
) = BracketsUi.Group(
    label = name.orEmpty(),
    matches = games.toMatchUiModels(titleFormatter, showMatchConnector)
)

private fun List<TournamentRoundGame>.toMatchUiModels(
    titleFormatter: TournamentRoundGameTitleFormatter,
    showMatchConnector: Boolean
) =
    mapNotNull { game -> game.toUiModel(titleFormatter, showMatchConnector) }

private fun TournamentRoundGame.toUiModel(
    titleFormatter: TournamentRoundGameTitleFormatter,
    showMatchConnector: Boolean
): BracketsUi.Match {
    val winningTeamId = winnerTeamId()
    val homeTeam = homeTeam.toUiModel(phase, winningTeamId)
    val awayTeam = awayTeam.toUiModel(phase, winningTeamId)
    return BracketsUi.Match(
        id = id,
        dateAndTimeText = titleFormatter.format(this),
        firstTeam = homeTeam,
        secondTeam = awayTeam,
        hasBoxScore = homeTeam.isValidTeam() && awayTeam.isValidTeam(),
        phase = phase,
        showConnector = showMatchConnector
    )
}

private fun TournamentRoundGame.Team?.toUiModel(
    phase: TournamentRoundGame.Phase?,
    winningTeamId: String?
): BracketsUi.Team {
    if (this == null) return BracketsUi.Team.PlaceholderTeam()
    return when (this) {
        is TournamentRoundGame.Team.Confirmed -> toUiModel(phase, winningTeamId)
        is TournamentRoundGame.Team.Placeholder -> toUiModel()
    }
}

private fun TournamentRoundGame.Team.Placeholder.toUiModel() = BracketsUi.Team.PlaceholderTeam(name = name)
private fun TournamentRoundGame.Team.Confirmed.toUiModel(
    phase: TournamentRoundGame.Phase?,
    winningTeamId: String?
) = data.toUiModel(phase, winningTeamId)

private fun TournamentRoundGame.TeamData.toUiModel(
    phase: TournamentRoundGame.Phase?,
    winningTeamId: String?
) = when (phase) {
    TournamentRoundGame.Phase.PreGame -> BracketsUi.Team.PreGameTeam(
        name = alias,
        logos = logos,
        seed = seed.toStringOrEmpty(),
        record = record.orEmpty()
    )
    else -> BracketsUi.Team.PostGameTeam(
        name = alias,
        logos = logos,
        score = if (penaltyScore == null) score.toStringOrEmpty() else "$score ($penaltyScore)",
        isWinner = winningTeamId?.let { id == winningTeamId }
    )
}