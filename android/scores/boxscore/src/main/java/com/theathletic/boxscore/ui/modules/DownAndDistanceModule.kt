package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.DownAndDistance
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.ui.ResourceString

data class DownAndDistanceModule(
    val id: String,
    val teamLogos: SizedImages,
    val title: ResourceString,
    val subTitle: ResourceString
) : FeedModuleV2 {

    override val moduleId: String = "DownAndDistanceModule:$id"

    @Composable
    override fun Render() {
        DownAndDistance(
            teamLogos = teamLogos,
            title = title,
            subtitle = subTitle
        )
    }
}