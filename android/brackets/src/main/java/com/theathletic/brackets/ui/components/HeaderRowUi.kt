package com.theathletic.brackets.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.brackets.ui.BracketsPreviewData
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

interface HeaderRowUi {
    data class BracketTab(
        val label: String,
        val isCurrentRound: Boolean
    )
}

@Composable
fun BracketsTabRow(
    currentTabIndex: Int,
    tabs: List<HeaderRowUi.BracketTab>,
    onTabSelectedClick: (index: Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = currentTabIndex,
        backgroundColor = AthTheme.colors.dark200,
        edgePadding = 0.dp,
        indicator = {}
    ) {
        BracketTabs(
            tabs = tabs,
            currentTabIndex = currentTabIndex,
            onTabSelectedClick = onTabSelectedClick
        )
    }
}

@Composable
private fun BracketTabs(
    tabs: List<HeaderRowUi.BracketTab>,
    currentTabIndex: Int,
    onTabSelectedClick: (index: Int) -> Unit
) {
    tabs.forEachIndexed { index, tab ->
        Tab(
            selected = false,
            onClick = { onTabSelectedClick(index) },
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (tab.isCurrentRound) {
                    CurrentRoundIndicator()
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(
                    text = tab.label.uppercase(),
                    style = AthTextStyle.Calibre.Utility.Medium.Large,
                    color = if (index == currentTabIndex) AthTheme.colors.dark800 else AthTheme.colors.dark500,
                )
            }
        }
    }
}

@Composable
private fun CurrentRoundIndicator() {
    val circleColor = AthTheme.colors.red
    Box(
        modifier = Modifier
            .size(6.dp)
            .drawBehind {
                drawCircle(
                    color = circleColor,
                    radius = 3.dp.toPx()
                )
            }
    )
}

@Preview
@Composable
private fun HeaderRowPreview() {
    BracketsTabRow(0, BracketsPreviewData.tabs) {}
}