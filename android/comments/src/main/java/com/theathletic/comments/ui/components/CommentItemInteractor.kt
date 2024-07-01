package com.theathletic.comments.ui.components

interface CommentItemInteractor {
    fun onLikeClick(commentId: String, index: Int) {}
    fun onReplyClick(parentId: String, commentId: String) {}
    fun onEditClick(commentId: String, text: String) {}
    fun onDeleteClick(commentId: String) {}
    fun onFlagClick(commentId: String, index: Int) {}
    fun onShareClick(permalink: String) {}
    fun onTweetClick(tweetUrl: String) {}
    fun onCommentClick(commentId: String, index: Int) {}
    fun onVisibilityChanged(commentId: String, commentIndex: Int, visibility: Float) {}
}