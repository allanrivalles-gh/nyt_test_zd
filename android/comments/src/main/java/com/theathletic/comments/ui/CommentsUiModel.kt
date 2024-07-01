package com.theathletic.comments.ui

import androidx.compose.ui.graphics.Color
import com.theathletic.comments.ui.components.CommentsUi

data class CommentsUiModel(
    val header: CommentsUi.HeaderModel? = null,
    val comments: List<CommentsUi.Comments> = listOf(),
    val commentsCount: Int = 0,
    val highlightedCommentIndex: Int? = null,
    val backgroundColor: Color = Color.Unspecified,
)