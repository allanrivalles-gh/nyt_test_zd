package com.theathletic.boxscore.ui.playbyplay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString

@Composable
internal fun AmericanFootballPlayContent(
    description: String?,
    title: String,
    possession: ResourceString?,
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark700,
                modifier = Modifier.padding(end = 6.dp)
            )
            if (possession != null) {
                Text(
                    text = possession.asString(),
                    style = AthTextStyle.Calibre.Utility.Regular.Small,
                    color = AthTheme.colors.dark500,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        description?.let {
            Text(
                text = description,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500,
                modifier = Modifier.padding(
                    top = 2.dp,
                    bottom = 6.dp
                )
            )
        }
    }
}