package com.theathletic.gamedetail.boxscore.ui.soccer

import androidx.core.text.isDigitsOnly
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.PlayerLineUpModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.boxscore.ui.BoxScoreState
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreCommonRenderers
import com.theathletic.gamedetail.data.local.CardType
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GoalType
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.orEmpty
import java.util.concurrent.atomic.AtomicInteger

class BoxScoreSoccerLineUpRenderers @AutoKoin constructor(
    private val commonRenderers: BoxScoreCommonRenderers
) {

    private val iconSortOrder = hashMapOf(
        R.drawable.ic_soccer_captain to 0,
        R.drawable.ic_soccer_goal to 1,
        R.drawable.ic_goal_error to 2,
        R.drawable.ic_soccer_card_yellow to 3,
        com.theathletic.ui.R.drawable.ic_soccer_card_yellow_red to 4,
        R.drawable.ic_soccer_card_red to 5
    )

    private val comparator = Comparator { o1: Int, o2: Int ->
        return@Comparator (iconSortOrder[o1] ?: 0) - (iconSortOrder[o2] ?: 0)
    }

    fun createPlayerLineModule(data: BoxScoreState, pageOrder: AtomicInteger): FeedModuleV2? {
        if (data.game == null ||
            (data.game.firstTeam?.noLineup() != false || data.game.secondTeam?.noLineup() != false)
        ) return null
        pageOrder.getAndIncrement()
        return PlayerLineUpModule(
            id = data.game.id,
            firstTeamLabel = data.game.firstTeam?.toTeamName().orEmpty(),
            secondTeamLabel = data.game.secondTeam?.toTeamName().orEmpty(),
            firstTeamFormationUrl = data.game.firstTeam?.lineUp?.formationImage,
            secondTeamFormationUrl = data.game.secondTeam?.lineUp?.formationImage,
            firstTeamLineup = getPlayerLineUp(
                data.game,
                data.game.firstTeam,
                data.expandedLineUpPlayers
            ),
            secondTeamLineup = getPlayerLineUp(
                data.game,
                data.game.secondTeam,
                data.expandedLineUpPlayers
            )
        )
    }

    private fun GameDetailLocalModel.GameTeam.toTeamName() =
        StringWithParams(
            R.string.box_score_line_up_team_switcher,
            this.team?.name.orEmpty(),
            this.lineUp?.formation?.asFormattedFormation.orEmpty()
        )

    private val String.asFormattedFormation: String
        get() = if (this.isDigitsOnly()) {
            this.chunked(1).joinToString(separator = "-")
        } else {
            this
        }

    private fun getPlayerLineUp(
        game: GameDetailLocalModel,
        team: GameDetailLocalModel.GameTeam?,
        expandedLineUpPlayers: List<String>,
    ): Map<ResourceString, List<PlayerLineUpModule.PlayerLineUp>> {

        val data = LinkedHashMap<ResourceString, List<PlayerLineUpModule.PlayerLineUp>>()

        data[StringWithParams(R.string.box_score_line_up_title_starting_line_up)] =
            getLineUpPlayers(game, team, expandedLineUpPlayers)
        data[StringWithParams(R.string.box_score_line_up_title_substitutions)] =
            getLineupSubstitutes(game, team, expandedLineUpPlayers)
        data[StringWithParams(R.string.box_score_line_up_title_manager)] = getManager(team)
        return data
    }

    private fun getManager(team: GameDetailLocalModel.GameTeam?): List<PlayerLineUpModule.PlayerLineUp> {
        return listOf(
            PlayerLineUpModule.PlayerLineUp.Manager(
                name = team?.lineUp?.manager.orEmpty()
            )
        )
    }

    private fun getLineUpPlayers(
        game: GameDetailLocalModel,
        team: GameDetailLocalModel.GameTeam?,
        expandedPlayers: List<String>
    ): List<PlayerLineUpModule.PlayerLineUp.Player> {
        val lineUpList = mutableListOf<PlayerLineUpModule.PlayerLineUp.Player>()
        team?.lineUp?.players
            ?.filter { it.position != PlayerPosition.SUBSTITUTE }
            ?.sortedBy { it.position.order }
            ?.forEach { player ->
                val isExpanded = expandedPlayers.contains(player.id)
                lineUpList.add(
                    populatePlayer(
                        player = player,
                        isPreGame = !game.isGameInProgressOrCompleted,
                        playerEvents = getEventsForPlayer(
                            player.id,
                            game.events
                        ),
                        isExpanded = isExpanded
                    )
                )
            }
        return lineUpList
    }

    private fun getLineupSubstitutes(
        game: GameDetailLocalModel,
        team: GameDetailLocalModel.GameTeam?,
        expandedPlayers: List<String>
    ): List<PlayerLineUpModule.PlayerLineUp.Player> {
        val lineUpList = mutableListOf<PlayerLineUpModule.PlayerLineUp.Player>()
        team?.lineUp?.players
            ?.filter { it.position == PlayerPosition.SUBSTITUTE }
            ?.sortedBy { it.regularPosition.order }
            ?.forEach { player ->
                val isExpanded = expandedPlayers.contains(player.id)
                lineUpList.add(
                    populatePlayer(
                        player = player,
                        isPreGame = !game.isGameInProgressOrCompleted,
                        playerEvents = getEventsForPlayer(
                            player.id,
                            game.events
                        ),
                        isExpanded = isExpanded,
                        isSubstitute = true
                    )
                )
            }
        return lineUpList
    }

    private fun populatePlayer(
        player: GameDetailLocalModel.Player,
        isPreGame: Boolean,
        playerEvents: List<GameDetailLocalModel.GameEvent>,
        isExpanded: Boolean,
        isSubstitute: Boolean = false
    ): PlayerLineUpModule.PlayerLineUp.Player {
        val lastSubstitutionTime = getLastSubstitutionTimeV2(playerEvents)
        return PlayerLineUpModule.PlayerLineUp.Player(
            id = player.id,
            jerseyNumber = player.jerseyNumber.orEmpty(),
            name = player.displayName.orEmpty(),
            position = getPlayerPosition(isSubstitute, player),
            isPreGame = isPreGame,
            isExpanded = isExpanded,
            showExpandIcon = player.statistics.isNotEmpty(),
            eventIcons = getPlayerEventIcons(playerEvents, player.captain),
            playerStats = if (isExpanded) {
                player.statistics.mapIndexed { index, gameStat ->
                    PlayerLineUpModule.PlayerLineUp.Stats(
                        label = gameStat.label,
                        value = commonRenderers.formatStatisticValue(gameStat) ?: "0.0"
                    )
                }
            } else {
                emptyList()
            },
            substitutionTime = lastSubstitutionTime,
            substitution = getSubstitutionIcon(playerEvents, player.id)
        )
    }

    private fun getPlayerPosition(
        isSubstitute: Boolean,
        player: GameDetailLocalModel.Player
    ) = when {
        (isSubstitute && player.regularPosition == PlayerPosition.UNKNOWN) -> ""
        (isSubstitute) -> player.regularPosition.alias
        else -> player.position.alias
    }

    private fun getPlayerEventIcons(
        events: List<GameDetailLocalModel.GameEvent>,
        captain: Boolean
    ): List<PlayerLineUpModule.PlayerLineUp.EventIconType> {
        val icons = mutableListOf<Int>()
        if (captain) {
            icons.add(R.drawable.ic_soccer_captain)
        }
        icons.addAll(
            events.filter { it !is GameDetailLocalModel.SubstitutionEvent }.mapNotNull { event ->
                getIconForEvent(event)
            }
        )

        // order the event icons
        icons.sortWith(comparator)

        val eventIcons = mutableListOf<PlayerLineUpModule.PlayerLineUp.EventIconType>()
        if (icons.size > 5) {
            val groupedIcons = icons.groupingBy { it }.eachCount()
            groupedIcons.entries.forEach {
                if (it.value > 1) {
                    eventIcons.add(PlayerLineUpModule.PlayerLineUp.BubbleIcon(icon = it.key, count = it.value))
                } else {
                    eventIcons.add(PlayerLineUpModule.PlayerLineUp.SingleIcon(it.key))
                }
            }
        } else {
            eventIcons.addAll(icons.map { PlayerLineUpModule.PlayerLineUp.SingleIcon(it) })
        }
        return eventIcons
    }

    private fun getSubstitutionIcon(
        events: List<GameDetailLocalModel.GameEvent>,
        playerId: String
    ): PlayerLineUpModule.PlayerLineUp.PlayerSubstitution {
        val substitutionEvents = events.filterIsInstance<GameDetailLocalModel.SubstitutionEvent>()
        return when {
            substitutionEvents.isEmpty() -> {
                PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.NONE
            }
            substitutionEvents.size > 1 -> {
                return PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.IN_OUT
            }
            playerId.contains("-${substitutionEvents.first().playerOn.id}") -> {
                return PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.IN
            }
            playerId.contains("-${substitutionEvents.first().playerOff.id}") -> {
                return PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.OUT
            }
            else -> {
                PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.NONE
            }
        }
    }

    private fun getIconForEvent(event: GameDetailLocalModel.GameEvent?): Int? {
        return when (event) {
            is GameDetailLocalModel.CardEvent -> {
                when (event.cardType) {
                    CardType.YELLOW -> R.drawable.ic_soccer_card_yellow
                    CardType.YELLOW_2ND -> com.theathletic.ui.R.drawable.ic_soccer_card_yellow_red
                    CardType.RED -> R.drawable.ic_soccer_card_red
                    else -> null
                }
            }
            is GameDetailLocalModel.GoalEvent -> if (event.goalType == GoalType.OWN_GOAL) {
                R.drawable.ic_goal_error
            } else {
                R.drawable.ic_soccer_goal
            }
            is GameDetailLocalModel.SubstitutionEvent -> R.drawable.ic_substitution_new
            else -> null
        }
    }

    private fun getEventsForPlayer(
        playerId: String,
        events: List<GameDetailLocalModel.GameEvent>
    ) = events.filter {
        when (it) {
            // playerId from line up has the game.id prepended to player's id
            is GameDetailLocalModel.CardEvent -> {
                playerId.contains("-${it.cardedPlayer.id}")
            }
            is GameDetailLocalModel.GoalEvent -> {
                playerId.contains("-${it.scorer.id}")
            }
            is GameDetailLocalModel.SubstitutionEvent -> {
                playerId.contains("-${it.playerOn.id}") ||
                    playerId.contains("-${it.playerOff.id}")
            }
            else -> false
        }
    }

    private fun getLastSubstitutionTimeV2(events: List<GameDetailLocalModel.GameEvent>): String {
        val times = events.filterIsInstance<GameDetailLocalModel.SubstitutionEvent>().map { it.matchTimeDisplay }
        return times.joinToString(separator = " ") { it }
    }

    private fun GameDetailLocalModel.GameTeam.noLineup() =
        lineUp?.formationImage == null && lineUp?.players.isNullOrEmpty()
}