package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R

data class PlayerGradeNoRatingsAvailableModule(
    val id: String
) : FeedModuleV2 {
    override val moduleId: String = "PlayerGradeNoRatingsAvailableModule:$id"

    @Composable
    override fun Render() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(AthTheme.colors.dark200)
        ) {
            Text(
                text = stringResource(id = R.string.player_grades_no_grades_available),
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark800,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}