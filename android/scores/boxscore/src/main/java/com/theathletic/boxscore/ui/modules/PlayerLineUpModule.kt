package com.theathletic.boxscore.ui.modules

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.playbyplay.PlayerLineUp
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.ui.ResourceString

data class PlayerLineUpModule(
    val id: String,
    val firstTeamLabel: ResourceString,
    val secondTeamLabel: ResourceString,
    val firstTeamFormationUrl: String?,
    val secondTeamFormationUrl: String?,
    val firstTeamLineup: Map<ResourceString, List<PlayerLineUp>>,
    val secondTeamLineup: Map<ResourceString, List<PlayerLineUp>>
) : FeedModuleV2 {
    override val moduleId: String = "PlayerLineUpModule:$id"

    sealed class PlayerLineUp {

        data class Player(
            val id: String,
            val jerseyNumber: String,
            val name: String,
            val position: String,
            val isPreGame: Boolean,
            val showExpandIcon: Boolean,
            val eventIcons: List<EventIconType>,
            val playerStats: List<Stats>,
            val substitution: PlayerSubstitution,
            val substitutionTime: String,

            val isExpanded: Boolean
        ) : PlayerLineUp()

        data class Manager(
            val name: String,
        ) : PlayerLineUp()

        data class Stats(
            val label: String,
            val value: String,
        )

        interface EventIconType

        data class SingleIcon(@DrawableRes val icon: Int) : EventIconType
        data class BubbleIcon(@DrawableRes val icon: Int, val count: Int) : EventIconType

        enum class PlayerSubstitution {
            IN, OUT, IN_OUT, NONE
        }
    }

    interface Interaction {
        data class OnLineUpExpandClick(
            val playerId: String
        ) : FeedInteraction
    }

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        PlayerLineUp(
            firstTeamLabel = firstTeamLabel,
            secondTeamLabel = secondTeamLabel,
            firstTeamFormationUrl = firstTeamFormationUrl,
            secondTeamFormationUrl = secondTeamFormationUrl,
            firstTeamLineup = firstTeamLineup,
            secondTeamLineup = secondTeamLineup,
            interactor = interactor
        )
    }
}