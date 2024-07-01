package com.theathletic.boxscore.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.modules.TopCommentsModule
import com.theathletic.comments.ui.LikeActionUiState
import com.theathletic.comments.ui.components.Comment
import com.theathletic.comments.ui.components.CommentItemInteractor
import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.comments.ui.preview.CommentsPreviewData
import com.theathletic.comments.ui.preview.CommentsPreviewData.CommentsItemPreviewInteractor
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.widgets.ResourceIcon

data class TopCommentsUiModel constructor(
    val comments: List<CommentsUi.Comments>,
    val showJoinDiscussionIndicator: Boolean
)

@Composable
fun TopComments(
    uiModel: TopCommentsUiModel,
    commentInteractor: CommentItemInteractor,
    likeActionUiState: LikeActionUiState,
) {
    Column(Modifier.background(AthTheme.colors.dark200)) {
        BoxScoreHeaderTitle(R.string.box_score_top_comments_title)

        Column {
            for (comment in uiModel.comments) {
                Comment(
                    comment = comment,
                    interactor = commentInteractor,
                    // TODO: Set the index when implementing analytics
                    index = -1,
                    isLikeEnabled = likeActionUiState.isEnabled(comment.commentId),
                )
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 16.dp)
                .background(AthTheme.colors.dark300)
        )

        Footer(uiModel)
    }
}

@Composable
fun Footer(uiModel: TopCommentsUiModel) {
    val interactor = LocalFeedInteractor.current
    val circleColor = AthTheme.colors.red
    Row(
        modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(1f))
        if (uiModel.showJoinDiscussionIndicator) {
            Canvas(
                modifier = Modifier.size(6.dp),
                onDraw = {
                    drawCircle(circleColor)
                }
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            modifier = Modifier
                .clickable {
                    interactor.send(TopCommentsModule.Interaction.OnJoinDiscussionClick)
                },
            text = stringResource(R.string.box_score_top_comments_join_the_discussion),
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
        ResourceIcon(
            modifier = Modifier
                .padding(start = 6.dp)
                .size(10.dp),
            resourceId = R.drawable.ic_chalk_chevron_right,
            tint = AthTheme.colors.dark500,
        )
        Spacer(Modifier.weight(1f))
    }
}

@DayNightPreview
@Composable
private fun TopCommentsPreview(
    @PreviewParameter(TopCommentsPreviewProvider::class) uiModel: TopCommentsUiModel
) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        TopComments(uiModel, CommentsItemPreviewInteractor, LikeActionUiState())
    }
}

private class TopCommentsPreviewProvider : PreviewParameterProvider<TopCommentsUiModel> {
    override val values: Sequence<TopCommentsUiModel> = sequenceOf(
        getUiModel()
    )

    private fun getUiModel() = TopCommentsUiModel(
        comments = listOf(CommentsPreviewData.userCommentBase, CommentsPreviewData.staffCommentBase),
        showJoinDiscussionIndicator = true
    )
}