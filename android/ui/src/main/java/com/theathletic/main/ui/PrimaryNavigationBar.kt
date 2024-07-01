package com.theathletic.main.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.BoxWithBadge
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.VariableSizeBadge

data class PrimaryNavItem(
    val tab: BottomTabItem,
    val badgeText: String? = null
)

@Composable
fun PrimaryNavigationBar(
    allTabs: List<PrimaryNavItem>,
    currentTab: BottomTabItem,
    onTabClicked: (BottomTabItem) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(AthTheme.colors.dark200)
            .clipToBounds()
    ) {
        allTabs.forEach { item ->
            PrimaryNavigationItem(
                selected = currentTab == item.tab,
                icon = item.tab.iconId,
                title = item.tab.titleId,
                onClick = { onTabClicked(item.tab) },
                badgeText = item.badgeText,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
    }
}

@get:DrawableRes private val BottomTabItem.iconId: Int
    get() = when (this) {
        BottomTabItem.FEED -> R.drawable.ic_tab_for_you
        BottomTabItem.SCORES -> R.drawable.ic_tab_scores
        BottomTabItem.FRONTPAGE -> R.drawable.ic_tab_front_page
        BottomTabItem.DISCOVER -> R.drawable.ic_tab_discover
        BottomTabItem.LISTEN -> R.drawable.ic_listen
        BottomTabItem.ACCOUNT -> R.drawable.ic_account
    }

@Composable
private fun PrimaryNavigationItem(
    selected: Boolean,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null
) {
    val tintColor = if (selected) AthTheme.colors.dark800 else AthTheme.colors.dark400

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, color = AthTheme.colors.dark800),
            )
            .padding(top = 10.dp),
    ) {
        BoxWithBadge(
            badgeContent = {
                badgeText?.let {
                    VariableSizeBadge(
                        text = it,
                        fontSize = 9.sp,
                    )
                }
            }
        ) {
            ResourceIcon(
                resourceId = icon,
                tint = tintColor,
                modifier = Modifier
                    .size(22.dp),
            )
        }
        Text(
            text = stringResource(id = title),
            color = tintColor,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Preview
@Composable
fun PrimaryNavigationBar_Preview() {
    var currentTab by remember { mutableStateOf(BottomTabItem.FEED) }

    AthleticTheme(lightMode = false) {
        Column(modifier = Modifier.height(56.dp)) {
            PrimaryNavigationBar(
                allTabs = listOf(
                    PrimaryNavItem(BottomTabItem.FEED),
                    PrimaryNavItem(BottomTabItem.FRONTPAGE),
                    PrimaryNavItem(BottomTabItem.LISTEN, "LIVE"),
                    PrimaryNavItem(BottomTabItem.SCORES),
                ),
                currentTab = currentTab,
                onTabClicked = { tab -> currentTab = tab }
            )
        }
    }
}

@Preview
@Composable
fun PrimaryNavigationBar_LightPreview() {
    var currentTab by remember { mutableStateOf(BottomTabItem.FEED) }

    AthleticTheme(lightMode = true) {
        Column(modifier = Modifier.height(56.dp)) {
            PrimaryNavigationBar(
                allTabs = listOf(
                    PrimaryNavItem(BottomTabItem.FEED),
                    PrimaryNavItem(BottomTabItem.FRONTPAGE),
                    PrimaryNavItem(BottomTabItem.LISTEN, "LIVE"),
                    PrimaryNavItem(BottomTabItem.SCORES),
                ),
                currentTab = currentTab,
                onTabClicked = { tab -> currentTab = tab }
            )
        }
    }
}