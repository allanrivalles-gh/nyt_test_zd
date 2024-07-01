package com.theathletic.widget.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

// The wrap_content for recycler won't work without this!
class NestedLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun canScrollVertically(): Boolean {
        return false
    }

    override fun canScrollHorizontally(): Boolean {
        return false
    }
}