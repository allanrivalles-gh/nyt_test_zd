package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedModule
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString

data class PlaysSimplePeriodHeaderModule(
    val id: String,
    val title: ResourceString
) : FeedModule {

    @Composable
    override fun Render() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AthTheme.colors.dark200)
                .padding(top = 22.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title.asString(),
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark500
            )
        }
    }
}

@Preview
@Composable
fun PlaysSimplePeriodHeaderPreview() {
    PlaysSimplePeriodHeaderModule(
        id = "uniqueId",
        title = "Period 2".asResourceString()
    ).Render()
}

@Preview
@Composable
fun PlaysSimplePeriodHeaderPreview_Light() {
    AthleticTheme(lightMode = true) {
        PlaysSimplePeriodHeaderModule(
            id = "uniqueId",
            title = "Period 2".asResourceString()
        ).Render()
    }
}