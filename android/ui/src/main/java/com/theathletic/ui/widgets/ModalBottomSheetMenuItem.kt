package com.theathletic.ui.widgets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.preview.DayNightPreview

@Composable
fun ModalBottomSheetMenuItem(
    @DrawableRes icon: Int,
    @StringRes label: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(AthTheme.colors.dark300)
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ResourceIcon(
            resourceId = icon,
            tint = AthTheme.colors.dark800,
            modifier = Modifier
                .padding(start = 14.dp)
                .size(24.dp)
        )

        Text(
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            text = stringResource(id = label),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@DayNightPreview
@Composable
private fun ModalBottomSheetMenuItemPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        ModalBottomSheetMenuItem(icon = R.drawable.ic_share, label = R.string.feed_article_action_share)
    }
}