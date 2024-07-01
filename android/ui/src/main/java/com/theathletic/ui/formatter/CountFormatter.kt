package com.theathletic.ui.formatter

import com.theathletic.annotation.autokoin.AutoKoin

class CountFormatter @AutoKoin constructor() {

    /**
     * This comments count formatter is for V2 comments functionality
     * as there is some slight differences on how the count is displayed
     */
    fun formatCommentCount(count: Int): String = when {
        count <= 0 -> ""
        count < 1000 -> count.toString()
        else -> {
            String.format("%.1fk", count.div(1000.toFloat()))
        }
    }

    fun formatLikesCount(count: Int): String = when {
        count <= 0 -> ""
        count < 1000 -> count.toString()
        else -> {
            String.format("%.1fk", count.div(1000.toFloat()))
        }
    }
}