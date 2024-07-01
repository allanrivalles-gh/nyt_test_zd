package com.theathletic.ui.widgets.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme

private val CORNER_SIZE = RoundedCornerShape(4.dp)

@Composable
fun <T : Any> ToggleButtonGroup(
    buttons: List<T>,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    buttonLabels: (Int) -> String = { index -> buttons[index].toString() },
    onButtonSelected: (Int, T) -> Unit = { _, _ -> },
) {
    val state = remember { ToggleButtonGroupControlState() }
    var selectedButton by remember { mutableStateOf(buttons[selectedIndex]) }

    state.buttonsCount = buttons.size
    state.selectedButton = buttons.indexOf(selectedButton)
    state.onButtonSelected = { index ->
        selectedButton = buttons[index]
        onButtonSelected(index, selectedButton)
    }

    Row(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = innerModifier
                .wrapContentHeight()
                .fillMaxWidth()
                .clip(CORNER_SIZE)
                .background(
                    color = AthTheme.colors.dark300,
                    shape = CORNER_SIZE
                )
        ) {
            buttons.forEachIndexed { i, button ->
                val isSelected = i == state.selectedButton
                ToggleButton(
                    label = buttonLabels(i),
                    isSelected = isSelected,
                    modifier = innerModifier
                        .weight(1f),
                    onSelected = { state.onButtonSelected(i) }
                )
            }
        }
    }
}

@Composable
private fun ToggleButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier,
    onSelected: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) AthTheme.colors.dark800 else AthTheme.colors.dark300,
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium)
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) AthTheme.colors.dark300 else AthTheme.colors.dark700,
        animationSpec = tween(250)
    )
    Box(
        modifier = modifier
            .height(40.dp)
            .padding(2.dp)
            .clip(CORNER_SIZE)
            .background(backgroundColor)
            .clickable { onSelected() },
    ) {
        Text(
            text = label,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            textAlign = TextAlign.Center,
            color = contentColor,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

class ToggleButtonGroupControlState {
    var buttonsCount by mutableStateOf(0)
    var selectedButton by mutableStateOf(0)
    var onButtonSelected: (Int) -> Unit by mutableStateOf({})
}

@Preview
@Composable
fun ToggleButtonGroup_LightPreview() {
    AthleticTheme(lightMode = true) {
        val buttons = listOf("All", "Teams", "Leagues", "Authors")
        ToggleButtonGroup(buttons = buttons)
    }
}

@Preview
@Composable
fun ToggleButtonGroup_DarkPreview() {
    AthleticTheme(lightMode = false) {
        val buttons = listOf("All", "Teams", "Leagues", "Authors")
        ToggleButtonGroup(buttons = buttons)
    }
}