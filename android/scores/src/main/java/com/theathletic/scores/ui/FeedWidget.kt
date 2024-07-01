package com.theathletic.scores.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.data.SizedImage
import com.theathletic.data.SizedImages
import com.theathletic.scores.R
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun FeedWidget(
    widget: ScoresFeedUI.FeedWidget,
    onClick: (String, String) -> Unit
) {
    when (widget) {
        is ScoresFeedUI.GameTicketsWidget -> GameTicketsWidget(
            widget = widget,
            onClick = onClick
        )
        else -> { /* Not Rendered */ }
    }
}

@Composable
private fun GameTicketsWidget(
    widget: ScoresFeedUI.GameTicketsWidget,
    onClick: (String, String) -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(widget.uri, widget.provider) }
                .background(color = AthTheme.colors.dark200)
                .padding(vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TicketsProviderLogo(
                    logosDark = widget.logosDark,
                    logosLight = widget.logosLight,
                    preferredHeight = 12.dp,
                    modifier = Modifier
                        .height(12.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = widget.text,
                    style = AthTextStyle.Calibre.Utility.Regular.Small,
                    color = AthTheme.colors.dark600,
                )
                ResourceIcon(
                    resourceId = R.drawable.ic_arrow_right,
                    tint = AthTheme.colors.dark700,
                )
            }
        }
        Spacer(
            modifier = Modifier
                .height(4.dp)
                .background(AthTheme.colors.dark100)
        )
    }
}

@Composable
private fun TicketsProviderLogo(
    logosDark: SizedImages,
    logosLight: SizedImages,
    preferredHeight: Dp,
    modifier: Modifier = Modifier
) {
    val logos = if (MaterialTheme.colors.isLight) logosLight else logosDark
    val teamUrl = logos.preferredProviderLogoHeight(preferredHeight)?.uri.orEmpty()
    RemoteImageAsync(
        url = teamUrl,
        modifier = modifier,
        error = com.theathletic.ui.R.drawable.ic_team_logo_placeholder,
    )
}

@Composable
internal fun SizedImages.preferredProviderLogoHeight(preferredHeight: Dp): SizedImage? {
    val preferredPxSize = with(LocalDensity.current) { preferredHeight.toPx() }.toInt()
    return sortedBy { it.height }.find { it.height >= preferredPxSize } ?: lastOrNull()
}

@Preview
@Composable
private fun GameTicketsWidget_Preview() {
    GameTicketsWidget(
        widget = ScoresFeedUI.GameTicketsWidget(
            logosDark = emptyList(),
            logosLight = emptyList(),
            text = "Buy Tickets from $55",
            uri = "",
            provider = ""
        ),
        onClick = { _, _ -> }
    )
}