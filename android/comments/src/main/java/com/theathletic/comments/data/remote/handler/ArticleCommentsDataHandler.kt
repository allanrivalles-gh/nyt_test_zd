package com.theathletic.comments.data.remote.handler

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.data.CommentsRepository
import com.theathletic.comments.v2.data.local.CommentsSourceType

class ArticleCommentsDataHandler @AutoKoin constructor(
    commentsRepository: CommentsRepository
) : BaseCommentsDataHandler(commentsRepository) {

    override fun getCommentsSourceType() = CommentsSourceType.ARTICLE
}