package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.TopComments
import com.theathletic.boxscore.ui.TopCommentsUiModel
import com.theathletic.comments.ui.LikeActionUiState
import com.theathletic.comments.ui.components.CommentItemInteractor
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor

data class TopCommentsModule(
    val id: String,
    private val uiModel: TopCommentsUiModel,
    private val likeActionUiState: LikeActionUiState
) : FeedModuleV2 {

    override val moduleId: String = "TopCommentsModule:$id"

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        TopComments(
            uiModel = uiModel,
            commentInteractor = object : CommentItemInteractor {
                override fun onCommentClick(commentId: String, index: Int) {
                    interactor.send(Interaction.OnCommentClick(commentId))
                }

                override fun onLikeClick(commentId: String, index: Int) {
                    interactor.send(Interaction.OnLikeClick(commentId))
                }

                override fun onReplyClick(parentId: String, commentId: String) {
                    interactor.send(
                        Interaction.OnReplyClick(commentId = commentId, parentId = parentId)
                    )
                }

                override fun onFlagClick(commentId: String, index: Int) {
                    interactor.send(
                        Interaction.OnFlagClick(commentId)
                    )
                }

                override fun onShareClick(permalink: String) {
                    interactor.send(
                        Interaction.OnShareClick(permalink)
                    )
                }
            },
            likeActionUiState = likeActionUiState
        )
    }

    interface Interaction {
        data class OnCommentClick(
            val commentId: String,
        ) : FeedInteraction

        data class OnLikeClick(
            val commentId: String,
        ) : FeedInteraction

        data class OnReplyClick(
            val commentId: String,
            val parentId: String
        ) : FeedInteraction

        data class OnFlagClick(
            val commentId: String,
        ) : FeedInteraction

        data class OnShareClick(
            val permalink: String,
        ) : FeedInteraction

        object OnJoinDiscussionClick : FeedInteraction
    }
}