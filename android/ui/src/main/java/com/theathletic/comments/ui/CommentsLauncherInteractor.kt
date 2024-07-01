package com.theathletic.comments.ui

import com.theathletic.comments.v2.data.local.CommentsSourceType

interface CommentsLauncherInteractor {
    fun onCommentsClick(id: String, type: CommentsSourceType, showEntry: Boolean, index: Int)
    fun onLikesClick(id: String, type: CommentsSourceType, index: Int, currentlyLiked: Boolean)
}