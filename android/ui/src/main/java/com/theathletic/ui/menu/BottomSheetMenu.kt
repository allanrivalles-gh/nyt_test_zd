package com.theathletic.ui.menu

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R

@Composable
fun BottomSheetMenu(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight()
            .background(AthTheme.colors.dark200),
        content = content,
    )
}

@Composable
fun BottomSheetMenuItem(
    @DrawableRes icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AthTheme.colors.dark800),
            modifier = Modifier.padding(horizontal = 20.dp).size(20.dp),
        )

        Text(
            text = text,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            maxLines = 1,
            color = AthTheme.colors.dark800,
        )
    }
}

@Composable
@Preview
fun BottomSheetMenu_Preview() {
    BottomSheetMenu {
        BottomSheetMenuItem(
            icon = R.drawable.ic_live_audio_mic_on,
            text = "Mute",
            onClick = {},
        )
        BottomSheetMenuItem(
            icon = R.drawable.ic_live_audio_mic_off,
            text = "Unmute",
            onClick = {},
        )
    }
}

@Composable
@Preview
fun BottomSheetMenu_LightPreview() {
    AthleticTheme(lightMode = true) {
        BottomSheetMenu_Preview()
    }
}