package com.theathletic.ui.utility

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints

@Composable
fun MeasureUnconstrainedViewWidth(
    viewToMeasure: @Composable () -> Unit,
    content: @Composable (measuredWidth: Int) -> Unit,
) {
    SubcomposeLayout { constraints ->
        val measuredWidth = subcompose(
            slotId = "viewToMeasure",
            content = viewToMeasure
        )[0].measure(Constraints()).width

        val contentPlaceable = subcompose(slotId = "content") { content(measuredWidth) }[0].measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) { contentPlaceable.place(0, 0) }
    }
}