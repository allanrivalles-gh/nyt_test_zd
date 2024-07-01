package com.theathletic.comments.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.FormattedTextWithArgs
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun TempBanNotification(
    modifier: Modifier = Modifier,
    numberOfDays: Int,
    onCodeOfConductClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(modifier = modifier.background(Color.Transparent)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Transparent)
                .clickable(
                    onClick = onDismiss,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AthTheme.colors.dark300)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 4.dp)
        ) {
            ResourceIcon(
                modifier = Modifier.padding(top = 4.dp, end = 8.dp),
                resourceId = R.drawable.ic_alert_red
            )
            FormattedTextWithArgs(
                id = R.string.comments_temp_ban_notification_legacy,
                style = AthTextStyle.Calibre.Utility.Medium.Large.copy(color = AthTheme.colors.dark800),
                modifier = Modifier,
                clickHandler = { clickedUrl -> if (clickedUrl == "code_of_conduct") onCodeOfConductClick() },
                formatArgs = arrayOf(
                    pluralStringResource(
                        id = R.plurals.plural_days,
                        count = numberOfDays,
                        numberOfDays
                    )
                )
            )
        }
    }
}

@Preview
@Composable
fun TempBanNotification_Dark_Preview() {
    TempBanNotification(
        numberOfDays = 2,
        onCodeOfConductClick = {},
        onDismiss = {}
    )
}

@Preview
@Composable
fun TempBanNotification_Light_Preview() {
    AthleticTheme(lightMode = true) {
        TempBanNotification(
            numberOfDays = 2,
            onCodeOfConductClick = {},
            onDismiss = {}
        )
    }
}