package com.theathletic.feed.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.analytics.impressions.ImpressionPayload

/**
 * This represents a single module that can be rendered in our compose feed. Each module is
 * responsible for its own rendering via the [Render] function. Make sure that any class that
 * implements this interface is marked as @Stable (data classes are automatically stable).
 */
interface FeedModuleV2 {
    @Composable
    fun Render()

    val moduleId: String
    val impressionPayload: ImpressionPayload? get() = null
}

data class FeedUiV2(
    val modules: List<FeedModuleV2> = emptyList(),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedV2(
    uiModel: FeedUiV2,
    listState: LazyListState,
    isVisible: Boolean,
    onViewVisibilityChanged: (ImpressionPayload, Float) -> Unit,
    verticalSpacing: Dp = 6.dp,
    bottomContentPadding: Dp = 78.dp,
    showLeadingDivider: Boolean = false
) {
    LazyColumn(
        state = listState,
        verticalArrangement = spacedBy(verticalSpacing),
        contentPadding = PaddingValues(bottom = bottomContentPadding),
        modifier = Modifier
            .fillMaxSize()
    ) {
        // When showing the leading/top divider, adding a zero height spacer
        // tricks the spaceBy implementation to add in the leading divider
        item { if (showLeadingDivider) Spacer(modifier = Modifier.height(0.dp)) }
        items(uiModel.modules, key = { it.moduleId }) { module ->
            Box(modifier = Modifier.animateItemPlacement(animationSpec = tween(600))) {
                module.Render()
            }
        }
    }
    if (isVisible) {
        ImpressionTrackerV2(
            feedModules = uiModel.modules,
            listState = listState,
            onViewVisibilityChanged = onViewVisibilityChanged,
        )
    }
}