package com.theathletic.feed.compose.ui.reusables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

@Composable
fun SmallLiveTag(modifier: Modifier = Modifier) {
    LiveTag(6.dp, textStyle = AthTextStyle.Calibre.Utility.Medium.Small, modifier)
}

@Composable
fun ExtraSmallLiveTag(modifier: Modifier = Modifier) {
    LiveTag(4.dp, textStyle = AthTextStyle.Calibre.Utility.Medium.ExtraSmall, modifier)
}

@Composable
private fun LiveTag(
    size: Dp,
    textStyle: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(color = AthTheme.colors.red, shape = CircleShape)
        ) {}
        Spacer(modifier = Modifier.width(size))

        Text(
            color = AthTheme.colors.red,
            style = textStyle,
            text = stringResource(id = R.string.feed_live)
        )
    }
}