package com.theathletic.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(@DimenRes private val spaceSize: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager

        if (layoutManager is LinearLayoutManager) {
            val orientation = layoutManager.orientation
            val isReversed = layoutManager.reverseLayout
            val isLastItem = if (!isReversed) {
                parent.getChildAdapterPosition(view) == (parent.adapter?.itemCount ?: 1) - 1
            } else {
                parent.getChildAdapterPosition(view) == 0
            }
            val spaceSizeInPx = parent.context.resources.getDimension(spaceSize).toInt()

            if (!isLastItem) {
                if (orientation == LinearLayoutManager.HORIZONTAL) {
                    outRect.right = spaceSizeInPx
                } else {
                    outRect.bottom = spaceSizeInPx
                }
            }
        }
    }
}