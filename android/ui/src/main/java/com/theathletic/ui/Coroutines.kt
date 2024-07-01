package com.theathletic.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@SuppressLint("ComposableNaming")
@Composable
inline fun <reified T> Flow<T>.collectWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit
) {
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(lifecycleState) {
            collect { action(it) }
        }
    }
}

/**
 * Uses compareAndSet in a loop to ensure that the modified fields of a data object are updated
 * even if a concurrent edit preempts this one, without overwriting changes to other fields
 * made by the concurrent edit.
 */
fun <T> MutableStateFlow<T>.updateState(action: T.() -> T) {
    update { action(value) }
}