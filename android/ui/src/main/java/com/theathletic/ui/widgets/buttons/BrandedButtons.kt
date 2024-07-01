package com.theathletic.ui.widgets.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun BrandedButtonLarge(
    text: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = defaultButtonColors(),
) {
    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(2.dp),
        colors = colors,
        enabled = isEnabled,
        modifier = Modifier
            .widthIn(min = 0.dp, max = 480.dp)
            .defaultMinSize(minHeight = 64.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = text,
                style = AthTextStyle.Slab.Bold.Medium
            )
            ResourceIcon(
                resourceId = R.drawable.ic_onboarding_icon_arrow,
                tint = colors.contentColor(enabled = isEnabled).value,
                modifier = Modifier
                    .size(25.dp)
                    .padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun BrandedButtonSmall(
    text: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = defaultButtonColors(),
) {
    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(2.dp),
        colors = colors,
        enabled = isEnabled,
        modifier = modifier
            .widthIn(min = 0.dp, max = 480.dp)
            .defaultMinSize(minHeight = 50.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = text,
                style = AthTextStyle.Slab.Bold.Small
            )
            ResourceIcon(
                resourceId = R.drawable.ic_onboarding_icon_arrow,
                tint = colors.contentColor(enabled = isEnabled).value,
                modifier = Modifier
                    .size(18.dp)
                    .padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun defaultButtonColors() = ButtonDefaults.buttonColors(
    backgroundColor = AthTheme.colors.dark200,
    contentColor = AthTheme.colors.dark800,
    disabledBackgroundColor = AthTheme.colors.dark500,
    disabledContentColor = AthTheme.colors.dark800
)