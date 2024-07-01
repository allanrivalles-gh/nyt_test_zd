package com.theathletic.scores.ui.gamecells

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.scores.R
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun SectionFooter(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(AthTheme.colors.dark200)
            .clickable { onClick() }
    ) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 16.dp)
                .background(AthTheme.colors.dark300)
                .align(Alignment.TopCenter)
        )
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                modifier = Modifier.padding(end = 8.dp)
            )
            ResourceIcon(
                resourceId = R.drawable.ic_chalk_chevron_right,
                tint = AthTheme.colors.dark500,
                modifier = Modifier.height(12.dp)
            )
        }
    }
}