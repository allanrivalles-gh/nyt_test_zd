package com.theathletic.scores.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.asString
import com.theathletic.ui.utility.MeasureUnconstrainedViewWidth

@Composable
fun ScoresDayTabBar(
    tabItems: List<ScoresFeedUI.DayTabItem>,
    selectedTabIndex: Int,
    onTabClicked: (Int, String) -> Unit,
) {
    val localDensity = LocalDensity.current
    val scrollState = rememberScrollState()
    var initialScrollDone by remember { mutableStateOf(false) }
    val tabWidths = remember { mutableStateListOf<Int>() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(state = scrollState)
                .align(Alignment.CenterStart)
        ) {
            val doCollectMeasuredWidths = tabWidths.isEmpty()
            tabItems.forEachIndexed { index, tab ->
                val selected = index == selectedTabIndex
                MeasureUnconstrainedViewWidth(
                    viewToMeasure = { ScheduleTab(tab = tab, selected = false) }
                ) { measuredWidth ->
                    if (doCollectMeasuredWidths) tabWidths.add(index, measuredWidth)
                    Box(
                        modifier = Modifier
                            .background(AthTheme.colors.dark200)
                            .clickable { onTabClicked(index, tab.id) }
                    ) {
                        ScheduleTab(tab = tab, selected = selected)
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedTabIndex) {
        // Center the tab in the center of the screen when possible
        if (tabWidths.isNotEmpty()) {
            val offset = calculateTabOffset(
                density = localDensity,
                selectedIndex = selectedTabIndex,
                tabWidths = tabWidths,
                scrollStateMax = scrollState.maxValue
            )
            if (scrollState.value != offset) {
                if (initialScrollDone) {
                    scrollState.animateScrollTo(offset, ScrollableTabRowScrollSpec)
                } else {
                    initialScrollDone = true
                    scrollState.scrollTo(offset)
                }
            }
        }
    }
}

@Composable
private fun ScheduleTab(
    tab: ScoresFeedUI.DayTabItem,
    selected: Boolean,
) {
    Column(
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .defaultMinSize(minWidth = 44.dp, minHeight = 32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = tab.labelTop.asString().uppercase(),
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = if (selected) AthTheme.colors.dark800 else AthTheme.colors.dark400,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        val bottomLabel = tab.labelBottom.asString()
        if (bottomLabel.isNotEmpty()) {
            Text(
                text = bottomLabel.uppercase(),
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                color = if (selected) AthTheme.colors.dark800 else AthTheme.colors.dark400,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

private fun calculateTabOffset(
    density: Density,
    selectedIndex: Int,
    tabWidths: List<Int>,
    scrollStateMax: Int
): Int = with(density) {
    val totalTabRowWidth = tabWidths.sumOf { it }
    val visibleWidth = totalTabRowWidth - scrollStateMax
    val tabOffset = tabWidths.take(selectedIndex).sumOf { it }
    val scrollerCenter = visibleWidth / 2
    val centeredTabOffset = tabOffset - (scrollerCenter - tabWidths[selectedIndex] / 2)
    val availableSpace = (totalTabRowWidth - visibleWidth).coerceAtLeast(0)
    return centeredTabOffset.coerceIn(0, availableSpace)
}

private val ScrollableTabRowScrollSpec: AnimationSpec<Float> = tween(
    durationMillis = 250,
    easing = FastOutSlowInEasing
)