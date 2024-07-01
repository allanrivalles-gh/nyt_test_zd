package com.theathletic.ui.widgets.buttons

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun SocialButton(
    @StringRes textRes: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
    @DrawableRes iconRes: Int? = null,
    fillWidth: Boolean = true,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AthColor.Gray800,
            contentColor = AthColor.Gray100,
            disabledBackgroundColor = AthColor.Gray500,
            disabledContentColor = AthColor.Gray700
        ),
        modifier = modifier
            .widthIn(min = 0.dp, max = 480.dp)
            .defaultMinSize(minHeight = 48.dp),
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(2.dp),
        border = if (enabled) {
            BorderStroke(width = 1.dp, color = AthColor.Gray600)
        } else {
            null
        }
    ) {
        Box(modifier = if (fillWidth) Modifier.fillMaxWidth() else Modifier) {
            if (icon != null) {
                Box(modifier = Modifier.align(Alignment.CenterStart)) {
                    icon()
                }
            } else if (iconRes != null) {
                ResourceIcon(
                    resourceId = iconRes,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            Text(
                text = stringResource(textRes),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 35.dp)
            )
        }
    }
}

@Composable
@Preview(
    name = "Social Button",
    group = "Light"
)
private fun SocialButton_Preview_Light() {
    AthleticTheme(lightMode = true) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SocialButton(
                textRes = R.string.auth_options_continue_google,
                iconRes = R.drawable.ic_auth_google
            )
            SocialButton(
                textRes = R.string.auth_options_continue_google,
                iconRes = R.drawable.ic_auth_google,
                enabled = false
            )
        }
    }
}