package com.theathletic.boxscore.ui

import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentManager

interface TabModule {
    @Composable
    fun Render(
        isActive: Boolean,
        fragmentManager: () -> FragmentManager
    )
}