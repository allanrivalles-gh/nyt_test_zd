package com.theathletic.utility

import androidx.recyclerview.widget.RecyclerView

@Suppress("unused")
enum class RecyclerLayout(
    val recyclerLayoutType: RecyclerLayoutType,
    val recyclerLayoutOrientation: Int = RecyclerView.VERTICAL,
    val isReversed: Boolean = false,
    val gridRows: Int = 0
) {
    LINEAR_VERTICAL(RecyclerLayoutType.LINEAR),
    LINEAR_HORIZONTAL(RecyclerLayoutType.LINEAR, RecyclerView.HORIZONTAL),
    LINEAR_VERTICAL_REVERSED(RecyclerLayoutType.LINEAR, RecyclerView.VERTICAL, true),
    LINEAR_HORIZONTAL_REVERSED(RecyclerLayoutType.LINEAR, RecyclerView.HORIZONTAL, true),
    NESTED_LINEAR_VERTICAL(RecyclerLayoutType.NESTED_LINEAR),
    NESTED_LINEAR_HORIZONTAL(RecyclerLayoutType.NESTED_LINEAR, RecyclerView.HORIZONTAL),
    GRID_VERTICAL(RecyclerLayoutType.GRID, RecyclerView.VERTICAL, gridRows = 3),
    STAGGERED_GRID_HORIZONTAL(RecyclerLayoutType.STAGGERED_GRID, RecyclerView.HORIZONTAL, gridRows = 3),
    FOUR_ROW_CAROUSEL(RecyclerLayoutType.GRID, RecyclerView.HORIZONTAL, gridRows = 4),
    SIDE_BY_SIDE_GRID(RecyclerLayoutType.GRID, RecyclerView.VERTICAL, gridRows = 2),
    THREE_ROW_CAROUSEL(RecyclerLayoutType.GRID, RecyclerView.HORIZONTAL, gridRows = 3),
    TWO_ROW_CAROUSEL(RecyclerLayoutType.GRID, RecyclerView.HORIZONTAL, gridRows = 2),
    THREE_CONTENT_CAROUSEL(RecyclerLayoutType.GRID, RecyclerView.VERTICAL, gridRows = 3),
    FOUR_CONTENT_CAROUSEL(RecyclerLayoutType.GRID, RecyclerView.VERTICAL, gridRows = 4),
    RECOMMENDED_PODCASTS_TABLET_GIRD(RecyclerLayoutType.GRID, RecyclerView.VERTICAL, gridRows = 6)
    ;

    enum class RecyclerLayoutType {
        NESTED_LINEAR,
        LINEAR,
        GRID,
        STAGGERED_GRID
    }
}