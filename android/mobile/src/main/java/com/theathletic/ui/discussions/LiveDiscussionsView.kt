package com.theathletic.ui.discussions

import com.theathletic.ui.BaseView

interface LiveDiscussionsView : BaseView, LiveDiscussionsBaseItemView {
    fun onSendCommentClick()
    fun onCancelClick()
    fun onNewPostClick()
}