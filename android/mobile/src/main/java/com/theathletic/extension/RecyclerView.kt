package com.theathletic.extension

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.extSetOnScrolledListener(
    listener: (RecyclerView, Int, RecyclerView.OnScrollListener) -> Unit
): RecyclerView.OnScrollListener {
    val callback = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            listener.invoke(recyclerView, dx, this)
            super.onScrolled(recyclerView, dx, dy)
        }
    }
    clearOnScrollListeners()
    addOnScrollListener(callback)
    return callback
}

/**
 * All credit to Carl Rice: https://carlrice.io/blog/better-smoothscrollto
 */
fun RecyclerView.betterSmoothScrollToPosition(targetItem: Int) {
    layoutManager?.apply {
        val maxScroll = 10
        when (this) {
            is LinearLayoutManager -> {
                val topItem = findFirstVisibleItemPosition()
                val distance = topItem - targetItem
                val anchorItem = when {
                    distance > maxScroll -> targetItem + maxScroll
                    distance < -maxScroll -> targetItem - maxScroll
                    else -> topItem
                }
                if (anchorItem != topItem) scrollToPosition(anchorItem)
                post {
                    smoothScrollToPosition(targetItem)
                }
            }
            else -> smoothScrollToPosition(targetItem)
        }
    }
}