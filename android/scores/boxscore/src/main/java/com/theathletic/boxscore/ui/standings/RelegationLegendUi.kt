package com.theathletic.boxscore.ui.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString

data class RelegationItem(
    val label: ResourceString,
    val color: Color
)

@Composable
fun RelegationLegend(relegationItems: List<RelegationItem>) {

    Column(
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .padding(vertical = 4.dp)
    ) {
        relegationItems.forEach { item ->
            RelegationItem(item = item)
        }
    }
}

@Composable
private fun RelegationItem(item: RelegationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 18.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .drawBehind {
                    drawCircle(
                        color = item.color,
                        radius = 5.dp.toPx()
                    )
                }
                .align(Alignment.CenterVertically)
        )
        Text(
            text = item.label.asString(),
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = AthTheme.colors.dark800,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
        )
    }
}

@Preview
@Composable
private fun RelegationLegendPreview() {
    RelegationLegend(
        relegationItems = listOf(
            RelegationItem(
                label = "Champions League".asResourceString(),
                color = AthColor.BlueUser,
            ),
            RelegationItem(
                label = "Europa League".asResourceString(),
                color = AthColor.YellowUser,
            ),
            RelegationItem(
                label = "Relegation".asResourceString(),
                color = AthColor.RedUser,
            ),
        )
    )
}