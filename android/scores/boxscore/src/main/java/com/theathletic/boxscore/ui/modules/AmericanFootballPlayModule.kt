package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.playbyplay.AmericanFootballPlayContent
import com.theathletic.feed.ui.FeedModule
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString

data class AmericanFootballPlayModule(
    val id: String,
    val title: String,
    val description: String,
    val possession: ResourceString?,
    val clock: String,
    val showDivider: Boolean
) : FeedModule {

    @Composable
    override fun Render() {
        AmericanFootballPlay(
            title = title,
            description = description,
            possession = possession,
            clock = clock,
            showDivider = showDivider
        )
    }
}

@Composable
private fun AmericanFootballPlay(
    title: String,
    description: String,
    possession: ResourceString?,
    clock: String,
    showDivider: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 6.dp
            )
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = clock,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500,
                modifier = Modifier
                    .width(44.dp)
                    .padding(top = 2.dp, start = 6.dp)
            )
            AmericanFootballPlayContent(
                description, title, possession, Modifier.fillMaxWidth()
            )
        }
        if (showDivider) {
            Divider(
                color = AthTheme.colors.dark300,
                thickness = 1.dp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Preview
@Composable
fun AmericanFootballPlayModulePreview() {
    AmericanFootballPlayModule(
        id = "uniqueId",
        title = "Incomplete Pass",
        description = "(Shotgun) J. Brissett pass incomplete deep right to R.Grant (M. Jenkins).",
        possession = "1st & 10 at IND 46".asResourceString(),
        clock = "12:40",
        showDivider = true
    ).Render()
}