package com.theathletic.comments.ui.preview

import androidx.compose.ui.graphics.Color
import com.theathletic.comments.R
import com.theathletic.comments.ui.CommentsUiModel
import com.theathletic.comments.ui.CommentsViewState
import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.comments.ui.components.InputHeaderData
import com.theathletic.data.ContentDescriptor
import com.theathletic.entity.user.SortType
import com.theathletic.themes.AthColor
import com.theathletic.ui.ResourceString
import com.theathletic.ui.utility.parseHexColor
import kotlin.random.Random

object CommentsPreviewData {
    val sourceDescriptor = ContentDescriptor(1, "Source Title")
    val titleRes = R.string.comments_header_article
    val liveTag = CommentsUi.LiveTag(
        labelRes = R.string.community_topic_tag_live
    )
    val simpleHeader = CommentsUi.HeaderModel.SimpleHeader(
        text = "‘Vintage Mark Stone’ — Vegas’ captain taking control of series with huge goal, Selke-level defense"
    )
    val header = CommentsUi.HeaderModel.Header(
        badgeUrl = "https://cdn-team-logos.theathletic.com/team-logo-96-50x50.png",
        labelRes = R.string.comments_header_discussion,
        title = "West Ham fans: Discuss the Burnley game with fellow Hammers here!",
        excerpt = "Pose your questions now and The Athletic’s West Ham reporter Roshane Thomas will join a couple of hours before kickoff to answer questions.",
        authorName = "Roshane Thomas",
        timeStamp = "Sat, Apr 16",
        backgroundColor = "#702C3A",
        liveTag = liveTag
    )

    val teamBanner = CommentsUi.TeamThreadBanner(
        teamName = "West Ham",
        teamLogo = "",
        teamColor = "CB3939".parseHexColor(),
        showChangeTeamThread = true
    )

    val authorFlair = CommentsUi.Comments.CommentFlair(
        title = "WHU",
        contrastColor = AthColor.RedUser
    )

    val userCommentBase = CommentsUi.Comments.UserComment(
        authorId = Random.nextInt(11).toString(),
        authorName = "Thomas B.",
        commentText = "Wow I never heard that story before. This is why I love The Athletic.",
        commentLink = "",
        commentedAt = ResourceString.StringWithParams(R.string.plural_time_now),
        commentId = Random.nextInt(99).toString(),
        parentId = Random.nextInt(99).toString(),
        isPinned = false,
        likesCount = Random.nextInt(35),
        isHighlighted = false,
        replyCount = Random.nextInt(5),
        hasUserLiked = false,
        isAuthor = false,
        commentMetadata = "Postgame",
        isDeletable = false,
        authorFlairs = listOf()
    )

    val staffCommentBase = CommentsUi.Comments.StaffComment(
        commentInfo = userCommentBase.copy(authorId = Random.nextInt(11).toString()),
        backgroundColor = Color.Unspecified,
        colorSet = null
    )

    val viewState = CommentsViewState(
        sourceDescriptor = sourceDescriptor,
        title = titleRes,
        commentsUiModel = CommentsUiModel(comments = listOf(userCommentBase.copy(authorFlairs = listOf(authorFlair)))),
        sortedBy = SortType.MOST_LIKED
    )

    internal object CommentsUiPreviewInteractor : CommentsUi.Interactor {
        override fun onBackButtonPressed() {}
        override fun onSortOptionSelected(selectedOption: SortType) {}
        override fun onPullToRefresh() {}
        override fun onFinishedScrollingToComment() {}
        override fun onLinkClick(url: String) {}
        override fun onClickTeamBannerChange() {}
        override fun onDismissTeamThreadsSheet() {}
        override fun onSwitchedTeamThread(teamId: String) {}
    }

    object CommentsItemPreviewInteractor : CommentsUi.Comments.Interactor {
        override fun onCommentInputClick() {}
        override fun onLikeClick(commentId: String, index: Int) {}
        override fun onTextChanged(newText: String) {}
        override fun onSendClick(onFinished: () -> Unit) {}
        override fun onDeleteClick(commentId: String) {}
        override fun onEditClick(commentId: String, text: String) {}
        override fun onShareClick(permalink: String) {}
        override fun onReplyClick(parentId: String, commentId: String) {}
        override fun onCancelInput(inputHeaderData: InputHeaderData) {}
        override fun onFlagClick(commentId: String, index: Int) {}
        override fun onClickTweet(tweetUrl: String) {}
    }
}