package com.theathletic.ui.discussions

import com.theathletic.entity.settings.UserTopicsBaseItem
import com.theathletic.ui.BaseView
import org.alfonz.adapter.AdapterView

interface CreateDiscussionView : BaseView, AdapterView {
    fun onPostClick()
    fun onTagClick(data: UserTopicsBaseItem)
}