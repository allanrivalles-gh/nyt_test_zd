package com.theathletic.impressions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

internal val LocalScreenVisibility = compositionLocalOf { mutableStateOf(true) }

@Composable
fun rememberIsScreenVisible(): MutableState<Boolean> {
    val isScreenVisible = LocalScreenVisibility.current
    return remember { isScreenVisible }
}

@Composable
fun ImpressionContainer(content: @Composable () -> Unit) {
    val screenVisibility = remember { mutableStateOf(false) }
    CompositionLocalProvider(LocalScreenVisibility provides screenVisibility) {

        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    screenVisibility.value = true
                } else if (event == Lifecycle.Event.ON_PAUSE) {
                    screenVisibility.value = false
                }
            }

            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }
        content()
    }
}