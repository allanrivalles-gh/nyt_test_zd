package com.theathletic.ui.widgets.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

data class ScrollableTabsModule(
    val id: String,
    val tabLabels: List<String>,
    val selectedTabIndex: Int,
    val bottomPadding: Dp = 0.dp
) : FeedModuleV2 {

    override val moduleId: String = "ScrollableTabsModule:$id"

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp,
                backgroundColor = AthTheme.colors.dark200,
                contentColor = AthTheme.colors.dark800,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = AthTheme.colors.dark800
                    )
                },
            ) {
                tabLabels.forEachIndexed { index, label ->
                    Tab(
                        selected = false,
                        onClick = {
                            interactor.send(Interaction.OnScrollableTabsClick(index, label))
                        },
                        text = {
                            Text(
                                text = label,
                                style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
                                color = AthTheme.colors.dark800,
                                modifier = Modifier
                                    .alpha(if (index == selectedTabIndex) 1f else 0.5f),
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(bottomPadding))
        }
    }

    interface Interaction {
        data class OnScrollableTabsClick(
            val tabClicked: Int,
            val title: String
        ) : FeedInteraction
    }
}