package com.theathletic.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import com.theathletic.ui.BuildConfig
import com.theathletic.ui.R

object AppConfig {
    val isTablet: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalContext.current.resources.getBoolean(R.bool.tablet)

    val isDebug: Boolean
        @Composable
        @ReadOnlyComposable
        get() = BuildConfig.DEBUG
}