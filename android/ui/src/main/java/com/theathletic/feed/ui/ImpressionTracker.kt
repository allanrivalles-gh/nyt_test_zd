package com.theathletic.feed.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.theathletic.analytics.impressions.ImpressionPayload
import kotlin.math.min

/**
 * A utility composable that keeps track of the visibility of feed modules. Each module that
 * contains an [ImpressionPayload] will have its visibility tracked and reported via the
 * [onViewVisibilityChanged] callback. The visibility is reported back as a [Float] ranging from
 * 0.0f to 1.0f.
 *
 * Users have to make sure that [ImpressionTracker] is only in the composition when the list it is
 * monitoring is visible on screen and removed from the composition when it is no longer visible.
 */
@Composable
fun ImpressionTracker(
    feedModules: List<FeedModule>,
    listState: LazyListState,
    onViewVisibilityChanged: (ImpressionPayload, Float) -> Unit,
) {
    ListVisibilityTracker(
        feedModules = feedModules,
        listState = listState,
        onViewVisibilityChanged = onViewVisibilityChanged,
    )
    DisposeTracker(
        feedModules = feedModules,
        onViewVisibilityChanged = onViewVisibilityChanged,
    )
}

/**
 * A sub-composable of [ImpressionTracker] that is responsible for tracking the visibility of
 * list elements provided via the [listState] parameter. It does calculations to figure out the
 * visibility percentage of the first and last items on screen, and assumes all items between those
 * are 100% visible.
 */
@Composable
private fun ListVisibilityTracker(
    feedModules: List<FeedModule>,
    listState: LazyListState,
    onViewVisibilityChanged: (ImpressionPayload, Float) -> Unit,
) {
    val visibilityState = remember { mutableMapOf<Int, Float>() }

    listState.layoutInfo.visibleItemsInfo.forEachIndexed { localIndex, composable ->
        val impressionPayload = feedModules.getOrNull(composable.index)?.impressionPayload ?: return@forEachIndexed

        val visibility = when (localIndex) {
            0 -> (composable.size + min(composable.offset, 0)).toFloat() / composable.size

            listState.layoutInfo.visibleItemsInfo.indices.last -> {
                val lastVisiblePx = listState.layoutInfo.viewportEndOffset - composable.offset
                lastVisiblePx.toFloat() / composable.size
            }

            else -> 1.0f
        }.coerceIn(0f, 1f)

        if (visibilityState[composable.index] != visibility) {
            visibilityState[composable.index] = visibility
            onViewVisibilityChanged(impressionPayload, visibility)
        }
    }
}

/**
 * A sub-composable of [ImpressionTracker] that is responsible for firing the impression events for
 * onscreen items when the Feed is removed from screen (i.e. the [ImpressionTracker] composable gets
 * disposed).
 */
@Composable
private fun DisposeTracker(
    feedModules: List<FeedModule>,
    onViewVisibilityChanged: (ImpressionPayload, Float) -> Unit,
) {
    val modules by rememberUpdatedState(feedModules)

    DisposableEffect(Unit) {
        onDispose {
            modules.mapNotNull { it.impressionPayload }
                .forEach { payload -> onViewVisibilityChanged(payload, 0f) }
        }
    }
}