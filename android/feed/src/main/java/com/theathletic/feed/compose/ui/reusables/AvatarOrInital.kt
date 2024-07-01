package com.theathletic.feed.compose.ui.reusables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.ui.widgets.RemoteImageAsync

/**
 * This composable will display the circular avatar using the url but if this null then
 * generates a circular colored place holder with the user's first initial displayed in it
 */

@Composable
fun AvatarOrInitial(
    url: String?,
    name: String,
    modifier: Modifier = Modifier
) {
    var showFallback by remember { mutableStateOf(false) }
    if (url == null) showFallback = true

    if (showFallback.not()) {
        RemoteImageAsync(
            url = url,
            circular = true,
            onError = { showFallback = true },
            modifier = modifier
        )
    } else {
        val initial = name.getOrNull(0)?.toString() ?: ""
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .background(color = AthColor.BlueUser, shape = CircleShape)
        ) {
            Text(
                text = initial.uppercase(),
                style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
                color = AthColor.Gray700,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}