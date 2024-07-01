package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

data class StandingsGroupTitleModule(
    val id: String,
    val title: String
) : FeedModuleV2 {
    override val moduleId: String = "StandingsGroupTitleModule:$title-$id"

    @Composable
    override fun Render() {
        Text(
            text = title.uppercase(),
            style = AthTextStyle.Calibre.Headline.SemiBold.Small,
            color = AthTheme.colors.dark700,
            modifier = Modifier
                .background(color = AthTheme.colors.dark200)
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}