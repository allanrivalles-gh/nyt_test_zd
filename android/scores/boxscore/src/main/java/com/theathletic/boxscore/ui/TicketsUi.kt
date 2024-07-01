package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.VendorLogo

data class TicketsUiModel(
    val vendorImageLight: SizedImages,
    val vendorImageDark: SizedImages,
    val title: ResourceString,
    val ticketUrlLink: String
)

@Composable
fun TicketsUi(
    vendorImageLight: SizedImages,
    vendorImageDark: SizedImages,
    title: ResourceString,
    ticketUrlLink: String,
    onLinkClick: (String) -> Unit
) {
    val vendorImage = if (MaterialTheme.colors.isLight) vendorImageLight else vendorImageDark
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
            .clickable(
                onClick = { onLinkClick(ticketUrlLink) }
            )
            .padding(
                vertical = 16.dp,
                horizontal = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {

        VendorLogo(
            imageUrls = vendorImage,
            preferredSize = 48.dp,
            modifier = Modifier.size(
                width = 60.dp,
                height = 12.dp
            )
        )

        Text(
            text = title.asString(),
            color = AthTheme.colors.dark600,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            modifier = Modifier.padding(start = 8.dp)
        )

        ResourceIcon(
            resourceId = R.drawable.ic_chevron_right,
            tint = AthTheme.colors.dark700,
            modifier = Modifier
                .padding(start = 4.dp)
        )
    }
}

@Preview
@Composable
private fun Ticket_Preview() {
    BoxScorePreviewData.ticketsModule.Render()
}

@Preview
@Composable
private fun Ticket_PreviewLight() {
    AthleticTheme(lightMode = true) {
        BoxScorePreviewData.ticketsModule.Render()
    }
}