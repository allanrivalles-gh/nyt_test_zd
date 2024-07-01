package com.theathletic.main.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.theathletic.followable.Followable
import com.theathletic.main.ui.NavigationItem.Companion.NO_PLACEHOLDER
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.utility.asHexColor

interface NavigationItem {
    val id: Followable.Id
    val title: String
    val imageUrl: String
    val placeholder: Int
    val color: String
    val background: NavItemBackground

    companion object {
        internal const val NO_PLACEHOLDER = -1
    }
}

data class SimpleNavItem(
    override val id: Followable.Id = Followable.Id("", Followable.Type.TEAM),
    override val title: String = "",
    override val imageUrl: String = "",
    override val placeholder: Int = NO_PLACEHOLDER,
    override val color: String = "",
    override val background: NavItemBackground = NavItemBackground.Empty
) : NavigationItem

sealed class NavItemBackground(internal val isCircular: Boolean = false) {
    internal open val color: Color @Composable get() = AthTheme.colors.dark300

    object Default : NavItemBackground(isCircular = false)
    object Empty : NavItemBackground(isCircular = true)
    data class Colored(val hexColor: String) : NavItemBackground(isCircular = false) {
        override val color: Color @Composable get() = hexColor.asHexColor ?: super.color
    }
}

class TeamNavItem(delegate: NavigationItem) : NavigationItem by delegate {
    override val background: NavItemBackground = NavItemBackground.Colored(color)
}

class LeagueNavItem(delegate: NavigationItem) : NavigationItem by delegate {
    override val background: NavItemBackground = NavItemBackground.Default
}

class AuthorNavItem(delegate: NavigationItem) : NavigationItem by delegate {
    @DrawableRes override val placeholder: Int = R.drawable.ic_headshot_placeholder
}

class ScoresNavItem(delegate: NavigationItem) : NavigationItem by delegate {
    override val background: NavItemBackground = NavItemBackground.Default
}