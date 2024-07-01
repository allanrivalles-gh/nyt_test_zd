package com.theathletic.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SignalWifiBad
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R

@Composable
fun OfflineAlert(
    onAlertClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthColor.OrangeUser)
            .clickable { onAlertClicked() },
    ) {
        Icon(
            imageVector = Icons.Default.SignalWifiBad,
            tint = AthTheme.colors.dark800,
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .size(size = 32.dp)
                .align(Alignment.CenterVertically),
        )
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = stringResource(id = R.string.feed_offline_header),
                color = AthTheme.colors.dark800,
                overflow = TextOverflow.Ellipsis,
                style = AthTextStyle.Calibre.Utility.Medium.Small
            )
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.feed_offline_text),
                color = AthTheme.colors.dark800,
                overflow = TextOverflow.Ellipsis,
                style = AthTextStyle.Calibre.Utility.Regular.Small
            )
        }
        Icon(
            imageVector = Icons.Default.Close,
            tint = AthTheme.colors.dark800,
            contentDescription = null,
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Preview
@Composable
private fun OfflineAlert_DarkPreview() {
    OfflineAlert {}
}

@Preview
@Composable
private fun OfflineAlert_LightPreview() {
    AthleticTheme(lightMode = true) {
        OfflineAlert {}
    }
}