package com.theathletic.main.ui.listen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun TrackScreenViewAnalytics(
    pagerState: PagerState,
    trackTabView: (Int) -> Unit
) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, pagerState.currentPage) {
        val observer = LifecycleEventObserver { _, receivedEvent ->
            if (receivedEvent == Lifecycle.Event.ON_RESUME) {
                trackTabView(pagerState.currentPage)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}