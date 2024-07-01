package com.theathletic.ui.widgets.buttons

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

// TODO(Todd): add icon support to spec (see SocialButtons, use that and make it scale better)
@Composable
fun PrimaryButtonLarge(
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) = ButtonLarge(text, modifier, isEnabled, primaryButtonColors(), onClick)

@Composable
fun SecondaryButtonLarge(
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) = ButtonLarge(text, modifier, isEnabled, secondaryButtonColors(), onClick)

@Composable
private fun ButtonLarge(
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    colors: ButtonColors = primaryButtonColors(),
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(2.dp),
        colors = colors,
        enabled = isEnabled,
        modifier = modifier
            .widthIn(min = 0.dp, max = 540.dp)
            .defaultMinSize(minHeight = 48.dp),
    ) {
        Text(
            text = text,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge
        )
    }
}

@Composable
fun PrimaryButtonSmall(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) = ButtonSmall(text, isEnabled, onClick, modifier, primaryButtonColors())

@Composable
fun SecondaryButtonSmall(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) = ButtonSmall(text, isEnabled, onClick, modifier, secondaryButtonColors())

@Composable
private fun ButtonSmall(
    text: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = primaryButtonColors(),
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(2.dp),
        colors = colors,
        enabled = isEnabled,
        modifier = modifier
            .widthIn(min = 0.dp, max = 540.dp)
            .defaultMinSize(minHeight = 32.dp),
    ) {
        Text(
            text = text,
            style = AthTextStyle.Calibre.Utility.Medium.Small
        )
    }
}

@Composable
private fun primaryButtonColors() = ButtonDefaults.buttonColors(
    backgroundColor = AthTheme.colors.dark800,
    contentColor = AthTheme.colors.dark200,
    disabledBackgroundColor = AthTheme.colors.dark800.copy(alpha = 0.5f),
    disabledContentColor = AthTheme.colors.dark200.copy(alpha = 0.5f)
)

@Composable
private fun secondaryButtonColors() = ButtonDefaults.buttonColors(
    backgroundColor = AthTheme.colors.dark300,
    contentColor = AthTheme.colors.dark700,
    disabledBackgroundColor = AthTheme.colors.dark300.copy(alpha = 0.5f),
    disabledContentColor = AthTheme.colors.dark700.copy(alpha = 0.5f)
)