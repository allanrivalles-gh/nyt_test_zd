package com.theathletic.ui.discussions

import com.theathletic.entity.discussions.LiveDiscussionTextBaseItem
import com.theathletic.ui.BaseView
import org.alfonz.adapter.AdapterView

interface LiveDiscussionsBaseItemView : BaseView, AdapterView {
    fun onItemLikeClick(entity: LiveDiscussionTextBaseItem)
    fun onItemReplyClick(entity: LiveDiscussionTextBaseItem)
    fun onItemOptionsClick(entity: LiveDiscussionTextBaseItem)
}