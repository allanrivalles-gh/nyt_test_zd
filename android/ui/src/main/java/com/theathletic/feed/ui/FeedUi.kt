package com.theathletic.feed.ui

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.analytics.impressions.ImpressionPayload

/**
 * This represents a single module that can be rendered in our compose feed. Each module is
 * responsible for its own rendering via the [Render] function. Make sure that any class that
 * implements this interface is marked as @Stable (data classes are automatically stable).
 */
@Deprecated("Use FeedModuleV2 which implements a stable id pattern for our Feed")
interface FeedModule {
    @Composable
    fun Render()

    val impressionPayload: ImpressionPayload? get() = null
}

data class FeedUi(
    val modules: List<FeedModule> = emptyList(),
)

@Deprecated("Use FeedV2 which implements a stable id pattern for our Feed")
@Composable
fun Feed(
    uiModel: FeedUi,
    listState: LazyListState,
    isVisible: Boolean,
    onViewVisibilityChanged: (ImpressionPayload, Float) -> Unit,
    verticalSpacing: Dp = 6.dp,
    showLeadingDivider: Boolean = false
) {
    LazyColumn(
        state = listState,
        verticalArrangement = spacedBy(verticalSpacing),
        contentPadding = PaddingValues(bottom = 78.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        // When showing the lead divider just need to add a zero height spacer
        // to trick the spaceBy implementation to add in the leading divider
        item { if (showLeadingDivider) Spacer(modifier = Modifier.height(0.dp)) }
        items(uiModel.modules) { module -> module.Render() }
    }

    if (isVisible) {
        ImpressionTracker(
            feedModules = uiModel.modules,
            listState = listState,
            onViewVisibilityChanged = onViewVisibilityChanged,
        )
    }
}

val LocalFeedInteractor = staticCompositionLocalOf { EmptyInteractor }