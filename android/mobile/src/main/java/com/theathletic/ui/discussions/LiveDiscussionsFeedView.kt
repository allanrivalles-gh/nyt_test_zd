package com.theathletic.ui.discussions

import com.theathletic.entity.main.DiscussionPresentationModel
import com.theathletic.entity.main.LiveDiscussionPresentationModel

interface LiveDiscussionsFeedView {
    fun liveDiscussionClick(item: LiveDiscussionPresentationModel)

    fun toggleLiveDiscussionNotification(
        item: LiveDiscussionPresentationModel,
        shouldNotify: Boolean
    )
}

interface DiscussionsFeedView {
    fun discussionClick(item: DiscussionPresentationModel)
}