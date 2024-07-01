package com.theathletic.comments.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.comments.ui.preview.CommentsPreviewData
import com.theathletic.comments.ui.preview.CommentsPreviewData.authorFlair
import com.theathletic.comments.ui.preview.CommentsPreviewData.staffCommentBase
import com.theathletic.comments.ui.preview.CommentsPreviewData.userCommentBase
import com.theathletic.extension.toStringOrEmpty
import com.theathletic.rooms.ui.chatAvatarColor
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.asString
import com.theathletic.ui.utility.conditional
import com.theathletic.ui.utility.getContrastColor
import com.theathletic.ui.widgets.ExpandableMenu
import com.theathletic.ui.widgets.ExpandableMenuDefaultIcon
import com.theathletic.ui.widgets.ExpandableMenuDefaultItemLayout
import com.theathletic.ui.widgets.ExpandableMenuItem
import com.theathletic.ui.widgets.MarkdownText
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.TextAvatar
import kotlin.random.Random

@Composable
fun Comment(
    comment: CommentsUi.Comments,
    interactor: CommentItemInteractor,
    index: Int = -1,
    isLikeEnabled: Boolean = false,
) = when (comment) {
    is CommentsUi.Comments.UserComment -> {
        UserComment(
            comment = comment,
            interactor = interactor,
            index = index,
            isLikeEnabled = isLikeEnabled,
            modifier = Modifier.commentModifier(isHighlighted = comment.isHighlighted)
        )
    }

    is CommentsUi.Comments.UserCommentReply -> {
        UserComment(
            comment = comment,
            interactor = interactor,
            index = index,
            isLikeEnabled = isLikeEnabled,
            modifier = Modifier.commentModifier(isReply = true, isHighlighted = comment.isHighlighted)
        )
    }

    is CommentsUi.Comments.StaffComment -> {
        StaffComment(
            comment = comment,
            colors = comment.colorSet ?: CommentsUi.ColorSet.defaultColorSet,
            actionListener = interactor,
            index = index,
            isLikeEnabled = isLikeEnabled,
            modifier = Modifier.commentModifier(
                isHighlighted = comment.isHighlighted,
                isReply = false,
                backgroundColor = comment.backgroundColor,
            )
        )
    }

    is CommentsUi.Comments.StaffCommentReply -> {
        StaffComment(
            comment = comment,
            colors = comment.colorSet ?: CommentsUi.ColorSet.defaultColorSet,
            actionListener = interactor,
            index = index,
            isLikeEnabled = isLikeEnabled,
            modifier = Modifier.commentModifier(
                isHighlighted = comment.isHighlighted,
                isReply = true,
                backgroundColor = comment.backgroundColor,
            )
        )
    }
}

@Composable
private fun UserComment(
    comment: CommentsUi.Comments,
    interactor: CommentItemInteractor,
    index: Int,
    isLikeEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    CommentItem(
        commentText = comment.commentText,
        commentTextColor = AthTheme.colors.dark700,
        modifier = modifier,
        header = {
            SingleCommentHeader(
                authorId = comment.authorId,
                authorName = comment.authorName,
                commentedAt = comment.commentedAt.asString(),
                metadata = comment.commentMetadata,
                authorFlairs = comment.authorFlairs
            )
        },
        onClick = { interactor.onCommentClick(comment.commentId, index) },
        actionRow = {
            CommentActionRow(
                likesCount = comment.likesCount,
                replyCount = comment.replyCount,
                hasUserLiked = comment.hasUserLiked,
                isAuthor = comment.isAuthor,
                isDeletable = comment.isDeletable,
                iconsColor = AthTheme.colors.dark500,
                iconsTextColor = AthTheme.colors.dark700,
                isLikeEnabled = isLikeEnabled
            ) { action ->
                when (action) {
                    CommentAction.LIKE -> interactor.onLikeClick(comment.commentId, index)
                    CommentAction.REPLY -> interactor.onReplyClick(comment.parentId, comment.commentId)
                    CommentAction.DELETE -> interactor.onDeleteClick(comment.commentId)
                    CommentAction.EDIT -> interactor.onEditClick(comment.commentId, comment.commentText)
                    CommentAction.FLAG -> interactor.onFlagClick(comment.commentId, index)
                    CommentAction.SHARE -> interactor.onShareClick(comment.commentLink)
                }
            }
        }
    )
}

@Composable
private fun StaffComment(
    modifier: Modifier = Modifier,
    comment: CommentsUi.Comments,
    colors: CommentsUi.ColorSet,
    actionListener: CommentItemInteractor,
    index: Int,
    isLikeEnabled: Boolean,
) {
    CommentItem(
        commentText = comment.commentText,
        commentTextColor = colors.commentTextColor,
        modifier = modifier,
        header = {
            SingleCommentHeaderStaff(
                authorId = comment.authorId,
                authorName = comment.authorName,
                avatarUrl = (comment as? CommentsUi.Comments.StaffComments)?.avatarUrl,
                commentedAt = comment.commentedAt.asString(),
                isPinned = comment.isPinned,
                colors = colors,
                metadata = comment.commentMetadata
            )
        },
        onClick = { actionListener.onCommentClick(comment.commentId, index) },
        actionRow = {
            CommentActionRow(
                likesCount = comment.likesCount,
                replyCount = comment.replyCount,
                hasUserLiked = comment.hasUserLiked,
                isAuthor = comment.isAuthor,
                isDeletable = comment.isDeletable,
                iconsColor = colors.iconsColor,
                iconsTextColor = colors.iconsTextColor,
                isLikeEnabled = isLikeEnabled
            ) { action ->
                when (action) {
                    CommentAction.LIKE -> actionListener.onLikeClick(comment.commentId, index)
                    CommentAction.REPLY -> actionListener.onReplyClick(comment.parentId, comment.commentId)
                    CommentAction.DELETE -> actionListener.onDeleteClick(comment.commentId)
                    CommentAction.EDIT -> actionListener.onEditClick(comment.commentId, comment.commentText)
                    CommentAction.FLAG -> actionListener.onFlagClick(comment.commentId, index)
                    CommentAction.SHARE -> actionListener.onShareClick(comment.commentLink)
                }
            }
        }
    )
}

@Composable
private fun CommentItem(
    modifier: Modifier = Modifier,
    commentText: String,
    commentTextColor: Color,
    header: @Composable () -> Unit,
    actionRow: @Composable () -> Unit,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        header()
        MarkdownText(
            markdownText = commentText,
            color = commentTextColor,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            modifier = Modifier
                .clickable(
                    onClick = onClick,
                    interactionSource = MutableInteractionSource(),
                    indication = null
                )
                .fillMaxWidth()
                .padding(vertical = 10.dp),
        )
        actionRow()
    }
}

@Composable
private fun SingleCommentHeader(
    authorId: String,
    authorName: String,
    commentedAt: String,
    metadata: String?,
    authorFlairs: List<CommentsUi.Comments.CommentFlair>?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextAvatar(
            text = authorName.firstOrNull().toStringOrEmpty(),
            textColor = AthColor.Gray800,
            backgroundColor = chatAvatarColor(authorId),
        )
        Text(
            text = authorName,
            color = AthTheme.colors.dark600,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            modifier = Modifier.padding(start = 8.dp)
        )
        Text(
            text = "• $commentedAt",
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            modifier = Modifier.padding(start = 4.dp)
        )
        if (authorFlairs != null) {
            Row {
                authorFlairs.forEach { flair ->
                    Spacer(modifier = Modifier.size(8.dp))
                    CommentFlair(flair)
                }
            }
        }
        if (metadata != null) {
            Spacer(modifier = Modifier.weight(1.0f))
            MetadataText(metadata)
        }
    }
}

@Composable
private fun SingleCommentHeaderStaff(
    authorId: String,
    authorName: String,
    avatarUrl: String? = null,
    commentedAt: String,
    isPinned: Boolean,
    colors: CommentsUi.ColorSet,
    metadata: String?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isPinned) {
            Image(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = R.drawable.ic_chalk_push_pin),
                colorFilter = ColorFilter.tint(AthTheme.colors.dark800),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        StaffHeaderAvatar(
            authorId = authorId,
            avatarUrl = avatarUrl,
            authorName = authorName
        )
        Text(
            text = authorName,
            color = colors.authorTextColor,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            modifier = Modifier.padding(start = 8.dp)
        )
        Text(
            text = "• $commentedAt",
            color = colors.dateTextColor,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 36.dp, height = 14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.tagBackgroundColor)
        ) {
            Text(
                text = stringResource(id = R.string.comments_item_staff).uppercase(),
                color = colors.tagTextColor,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall.copy(
                    fontSize = 10.sp,
                    lineHeight = 10.sp
                ),
                textAlign = TextAlign.Center
            )
        }
        if (metadata != null) {
            Spacer(modifier = Modifier.weight(1.0f))
            MetadataText(metadata, color = colors.dateTextColor)
        }
    }
}

@Composable
private fun StaffHeaderAvatar(
    authorId: String,
    avatarUrl: String?,
    authorName: String,
) {
    if (avatarUrl != null) {
        RemoteImageAsync(
            url = avatarUrl,
            circular = true,
            modifier = Modifier.size(16.dp)
        )
    } else {
        TextAvatar(
            text = authorName.firstOrNull().toStringOrEmpty(),
            textColor = AthColor.Gray800,
            backgroundColor = chatAvatarColor(authorId),
        )
    }
}

@Composable
private fun CommentActionRow(
    likesCount: Int,
    replyCount: Int,
    hasUserLiked: Boolean,
    isAuthor: Boolean,
    isDeletable: Boolean,
    iconsColor: Color,
    iconsTextColor: Color,
    isLikeEnabled: Boolean,
    onActionSelected: (CommentAction) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        LikesCountButton(
            onLikeClick = { onActionSelected(CommentAction.LIKE) },
            hasUserLiked = hasUserLiked,
            iconsColor = iconsColor,
            likesCount = likesCount,
            enabled = isLikeEnabled,
            iconsTextColor = iconsTextColor
        )
        Spacer(Modifier.padding(end = 16.dp))
        RepliesCountButton(
            iconsColor = iconsColor,
            replyCount = replyCount,
            iconsTextColor = iconsTextColor,
            onClick = { onActionSelected(CommentAction.REPLY) }
        )
        Spacer(Modifier.padding(end = 16.dp))
        MoreButton(
            iconsColor = iconsColor,
            isAuthor = isAuthor,
            isDeletable = isDeletable,
            onActionSelected = { onActionSelected(it) }
        )
    }
}

@Composable
private fun RepliesCountButton(
    iconsColor: Color,
    replyCount: Int,
    iconsTextColor: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(30.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_reply_outline),
            contentDescription = null,
            tint = iconsColor
        )
    }
    if (replyCount > 0) {
        Text(
            text = replyCount.toString(),
            color = iconsTextColor,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun LikesCountButton(
    hasUserLiked: Boolean,
    iconsColor: Color,
    likesCount: Int,
    enabled: Boolean,
    iconsTextColor: Color,
    onLikeClick: () -> Unit
) {
    IconButton(
        onClick = { if (enabled) onLikeClick() },
        modifier = Modifier.size(30.dp)
    ) {
        val likeIconResourceId = if (hasUserLiked) R.drawable.ic_like_filled else R.drawable.ic_like_outline

        Icon(
            painter = painterResource(id = likeIconResourceId),
            contentDescription = null,
            tint = iconsColor
        )
    }
    Text(
        modifier = Modifier.padding(start = 4.dp),
        text = likesCount.toString(),
        color = iconsTextColor,
        style = AthTextStyle.Calibre.Utility.Regular.Small,
    )
}

@Composable
private fun MoreButton(
    iconsColor: Color,
    isAuthor: Boolean,
    isDeletable: Boolean,
    onActionSelected: (CommentAction) -> Unit
) {
    val options = listOfNotNull(
        (R.string.comments_settings_edit to null).takeIf { isAuthor },
        (R.string.comments_settings_delete to Icons.Default.Close).takeIf { isAuthor || isDeletable },
        (R.string.comments_settings_flag to Icons.Outlined.Flag).takeIf { isAuthor.not() },
        (R.string.comments_settings_share to Icons.Outlined.Share)
    ).toMap()

    ExpandableMenu(
        backgroundColor = AthTheme.colors.dark300,
        backgroundShape = RoundedCornerShape(8.dp),
        dropdownModifier = Modifier
            .wrapContentHeight()
            .width(200.dp),
        options = options.keys.toList(),
        showDivider = true,
        onItemClick = { label, _ ->
            when (label) {
                R.string.comments_settings_delete -> onActionSelected(CommentAction.DELETE)
                R.string.comments_settings_edit -> onActionSelected(CommentAction.EDIT)
                R.string.comments_settings_flag -> onActionSelected(CommentAction.FLAG)
                R.string.comments_settings_share -> onActionSelected(CommentAction.SHARE)
            }
        },
        menuItem = { option, _ ->
            val icon = options[option]
            ExpandableMenuDefaultItemLayout(
                menuItem = ExpandableMenuItem(
                    text = stringResource(id = option),
                    textColor = AthTheme.colors.dark700,
                    textSize = 16.sp,
                    icon = {
                        if (icon != null) {
                            ExpandableMenuDefaultIcon(
                                icon = icon,
                                iconColor = AthTheme.colors.dark700,
                                iconSize = 22.dp
                            )
                        }
                    }
                ),
            )
        }
    ) {
        IconButton(
            onClick = {},
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = null,
                tint = iconsColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun MetadataText(text: String, color: Color = AthTheme.colors.dark500) {
    Text(
        text,
        color = color,
        style = AthTextStyle.Calibre.Utility.Regular.Small
    )
}

@Composable
private fun CommentFlair(flair: CommentsUi.Comments.CommentFlair) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 36.dp, height = 14.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(color = flair.contrastColor)
    ) {
        Text(
            text = flair.title.uppercase(),
            color = flair.contrastColor.getContrastColor(),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall.copy(
                fontSize = 10.sp,
                lineHeight = 10.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

private fun Modifier.styleReplyComment(lineColor: Color, hasBackground: Boolean = false) = drawWithCache {
    onDrawWithContent {
        val lineSize = if (hasBackground) size.height else size.height - 28.dp.roundToPx()
        drawLine(
            color = lineColor,
            start = Offset.Zero,
            end = Offset(0f, lineSize),
            strokeWidth = 1 * density
        )
        drawContent()
    }
}.then(padding(start = 16.dp))

private fun Modifier.styleBackgroundComment(backgroundColor: Color) = this
    .conditional(backgroundColor != Color.Unspecified) {
        background(backgroundColor, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    }

private fun Modifier.highlightComment(isHighlighted: Boolean, color: Color) = this
    .conditional(isHighlighted) {
        drawBehind {
            drawRect(
                color,
                alpha = 0.05f,
            )
        }
    }

private fun Modifier.commentModifier(
    backgroundColor: Color = Color.Unspecified,
    isHighlighted: Boolean = false,
    isReply: Boolean = false,
    hasBackground: Boolean = false,
) = composed {
    val highlightColor = AthTheme.colors.dark700
    val lineColor = AthTheme.colors.dark400
    this
        .highlightComment(isHighlighted = isHighlighted, color = highlightColor)
        .padding(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp,
            top = if (isReply) 4.dp else 16.dp
        )
        .then(
            if (isReply) {
                Modifier
                    .styleReplyComment(
                        lineColor = lineColor,
                        hasBackground = hasBackground
                    )
                    .styleBackgroundComment(backgroundColor)
            } else {
                Modifier.styleBackgroundComment(backgroundColor)
            }
        )
}

private enum class CommentAction {
    REPLY,
    DELETE,
    EDIT,
    FLAG,
    SHARE,
    LIKE
}

@Preview
@Composable
private fun UserComment_LightPreview() {
    AthleticTheme(lightMode = true) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(
                userCommentBase.copy(hasUserLiked = false),
                interactor = CommentsPreviewData.CommentsItemPreviewInteractor
            )
        }
    }
}

@Preview
@Composable
private fun UserComment_DarkPreview() {
    AthleticTheme(lightMode = false) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(userCommentBase, interactor = CommentsPreviewData.CommentsItemPreviewInteractor)
        }
    }
}

@Preview
@Composable
private fun UserCommentReply_LightPreview() {
    AthleticTheme(lightMode = true) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(
                CommentsUi.Comments.UserCommentReply(
                    userCommentBase.copy(
                        commentText = userCommentBase.commentText.repeat(3),
                        hasUserLiked = true
                    )
                ),
                interactor = CommentsPreviewData.CommentsItemPreviewInteractor
            )
        }
    }
}

@Preview
@Composable
private fun UserCommentReply_DarkPreview() {
    AthleticTheme(lightMode = false) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(
                CommentsUi.Comments.UserCommentReply(
                    userCommentBase.copy(
                        commentText = userCommentBase.commentText.repeat(3),
                        hasUserLiked = true
                    )
                ),
                interactor = CommentsPreviewData.CommentsItemPreviewInteractor
            )
        }
    }
}

@Preview
@Composable
private fun UserCommentWithFlair_Preview() {
    AthleticTheme(lightMode = false) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(
                CommentsUi.Comments.UserCommentReply(
                    userCommentBase.copy(
                        commentText = userCommentBase.commentText.repeat(3),
                        hasUserLiked = true,
                        authorFlairs = listOf(authorFlair)
                    )
                ),
                interactor = CommentsPreviewData.CommentsItemPreviewInteractor
            )
        }
    }
}

@Preview
@Composable
private fun StaffComment_LightPreview() {
    AthleticTheme(lightMode = true) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(
                staffCommentBase,
                interactor = CommentsPreviewData.CommentsItemPreviewInteractor
            )
        }
    }
}

@Preview
@Composable
private fun StaffComment_DarkPreview() {
    AthleticTheme(lightMode = false) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(
                staffCommentBase,
                interactor = CommentsPreviewData.CommentsItemPreviewInteractor
            )
        }
    }
}

@Preview
@Composable
private fun StaffCommentWithBg_LightPreview() {
    AthleticTheme(lightMode = true) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(
                CommentsUi.Comments.StaffComment(
                    commentInfo = userCommentBase.copy(
                        authorId = Random.nextInt(11).toString(),
                        authorName = "Tomas B. Yellow.",
                        commentedAt = userCommentBase.commentedAt,
                    ),
                    backgroundColor = AthTheme.colors.yellow,
                    colorSet = CommentsUi.ColorSet.darkColorSet
                ),
                interactor = CommentsPreviewData.CommentsItemPreviewInteractor
            )
        }
    }
}

@Preview
@Composable
private fun StaffCommentWithBg_DarkPreview() {
    AthleticTheme(lightMode = false) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(
                CommentsUi.Comments.StaffComment(
                    commentInfo = userCommentBase.copy(
                        authorId = Random.nextInt(11).toString(),
                        authorName = "Tomas B. Navy",
                        commentedAt = userCommentBase.commentedAt
                    ),
                    backgroundColor = AthTheme.colors.blue,
                    colorSet = CommentsUi.ColorSet.lightColorSet
                ),
                interactor = CommentsPreviewData.CommentsItemPreviewInteractor
            )
        }
    }
}

@Preview
@Composable
private fun StaffCommentReplyNoBg_LightPreview() {
    AthleticTheme(lightMode = true) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(
                CommentsUi.Comments.StaffCommentReply(
                    staffInfo = staffCommentBase.copy(
                        colorSet = null,
                        commentInfo = userCommentBase.copy(authorName = "Roshane Thomas")
                    )
                ),
                interactor = CommentsPreviewData.CommentsItemPreviewInteractor
            )
        }
    }
}

@Preview
@Composable
private fun StaffCommentReplyWithBg_LightPreview() {
    AthleticTheme(lightMode = true) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(
                CommentsUi.Comments.StaffCommentReply(
                    staffInfo = staffCommentBase.copy(
                        colorSet = CommentsUi.ColorSet.lightColorSet,
                        backgroundColor = AthTheme.colors.red,
                        commentInfo = userCommentBase.copy(authorName = "Roshane B. Maroon")
                    )
                ),
                interactor = CommentsPreviewData.CommentsItemPreviewInteractor
            )
        }
    }
}

@Preview
@Composable
private fun StaffCommentReplyWithBg_DarkPreview() {
    AthleticTheme(lightMode = false) {
        Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
            Comment(
                CommentsUi.Comments.StaffCommentReply(
                    staffInfo = staffCommentBase.copy(
                        backgroundColor = AthTheme.colors.green,
                        colorSet = CommentsUi.ColorSet.darkColorSet,
                        commentInfo = userCommentBase.copy(authorName = "Roshane B. Green")
                    )
                ),
                interactor = CommentsPreviewData.CommentsItemPreviewInteractor
            )
        }
    }
}