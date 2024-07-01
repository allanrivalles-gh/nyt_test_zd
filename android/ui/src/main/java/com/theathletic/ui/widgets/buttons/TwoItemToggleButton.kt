package com.theathletic.ui.widgets.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString

private val CORNER_SIZE = RoundedCornerShape(4.dp)

@Composable
fun TwoItemToggleButton(
    modifier: Modifier = Modifier,
    itemOneLabel: ResourceString,
    itemTwoLabel: ResourceString,
    isFirstItemSelected: Boolean,
    onTwoItemToggleSelected: (itemOneSelected: Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .clip(CORNER_SIZE)
            .background(color = AthTheme.colors.dark300),
    ) {
        ItemToggleButton(
            label = itemOneLabel,
            isSelected = isFirstItemSelected,
            onItemToggleSelected = {
                onTwoItemToggleSelected(true)
            },
            modifier = Modifier
                .weight(1f)
        )
        ItemToggleButton(
            label = itemTwoLabel,
            isSelected = !isFirstItemSelected,
            onItemToggleSelected = {
                onTwoItemToggleSelected(false)
            },
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
private fun ItemToggleButton(
    label: ResourceString,
    isSelected: Boolean,
    modifier: Modifier,
    onItemToggleSelected: () -> Unit
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
            .clickable { onItemToggleSelected() },
    ) {
        Text(
            text = label.asString(),
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            textAlign = TextAlign.Center,
            color = contentColor,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}