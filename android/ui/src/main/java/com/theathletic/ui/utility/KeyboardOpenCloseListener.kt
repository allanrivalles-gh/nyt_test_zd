package com.theathletic.ui.utility

import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * This detects when the keyboard is open and uses callback to update the caller.
 *
 * Note: As of October 2023, this was confirmed to work with both on-screen keyboard and hardware keyboard.
 * With a hardware keyboard, tabbing into a text field triggers the keyboard-open event, even though
 * no on-screen keyboard slides into view.
 */
@Composable
fun KeyboardOpenCloseListener(onKeyboardOpenChanged: (Boolean) -> Unit) {
    val view = LocalView.current
    val viewTreeObserver = view.viewTreeObserver
    DisposableEffect(viewTreeObserver) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
            onKeyboardOpenChanged(isKeyboardOpen)
        }

        viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
}