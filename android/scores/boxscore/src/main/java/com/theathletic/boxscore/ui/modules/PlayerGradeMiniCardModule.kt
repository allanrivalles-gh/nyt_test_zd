package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.playergrades.PlayerGradeMiniCard
import com.theathletic.boxscore.ui.playergrades.PlayerGradeMiniCardModel
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.themes.AthTheme

data class PlayerGradeMiniCardModule(
    val id: String,
    val playerGradeMiniCard: PlayerGradeMiniCardModel
) : FeedModuleV2 {

    override val moduleId: String = "PlayerGradeMiniCardModule:$id"

    @Composable
    override fun Render() {
        Box(modifier = Modifier.background(color = AthTheme.colors.dark200)) {
            PlayerGradeMiniCard(playerGradeMiniCard = playerGradeMiniCard)
            Divider(
                color = AthTheme.colors.dark300,
                thickness = 1.dp,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}