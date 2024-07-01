package com.theathletic.feed.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.feed.compose.SOURCE_FEED
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.feed.compose.ui.reusables.AvatarOrInitial
import com.theathletic.feed.compose.ui.reusables.Badge
import com.theathletic.links.deep.Deeplink
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.preview.DevicePreviewSmallAndLarge
import com.theathletic.ui.utility.getContrastColor
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.ui.widgets.ResourceIcon

data class TopCommentUiModel(
    override val id: String,
    val avatarUrl: String?,
    val author: String,
    val commentedAt: ResourceString,
    val flairName: String?,
    val flairColor: String?,
    val isStaff: Boolean,
    val comment: String,
    override val permalink: String?,
    override val analyticsData: AnalyticsData
) : LayoutUiModel.Item {

    override fun deepLink(): Deeplink = Deeplink.discussion(id).addSource(SOURCE_FEED)
}

@Composable
fun TopComment(
    uiModel: TopCommentUiModel,
    itemInteractor: ItemInteractor
) {
    Column(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .padding(16.dp)
            .interactive(uiModel, itemInteractor)
    ) {
        BylineAndTitle(uiModel)
        Spacer(modifier = Modifier.height(4.dp))
        Comment(uiModel.comment)
    }
}

@Composable
private fun BylineAndTitle(uiModel: TopCommentUiModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Byline(
            uiModel = uiModel,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        TopCommentTitle()
    }
}

@Composable
private fun Byline(
    uiModel: TopCommentUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarOrInitial(
            url = uiModel.avatarUrl,
            name = uiModel.author,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = uiModel.author,
            color = AthTheme.colors.dark600,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(id = R.string.global_bulleted_string, uiModel.commentedAt.asString()),
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Regular.Small
        )
        Spacer(modifier = Modifier.width(8.dp))
        AuthorBadge(uiModel = uiModel)
    }
}

@Composable
private fun TopCommentTitle() {
    Row {
        ResourceIcon(
            resourceId = R.drawable.ic_feed_news_comment,
            tint = AthTheme.colors.dark500
        )
        Text(
            text = "Top Comment".uppercase(),
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall
        )
    }
}

@Composable
private fun AuthorBadge(uiModel: TopCommentUiModel) {
    if (uiModel.isStaff) {
        Badge(label = stringResource(id = R.string.comments_item_staff).uppercase())
    } else {
        if (uiModel.flairName != null && uiModel.flairColor != null) {
            val background = uiModel.flairColor.parseHexColor(defaultColor = AthTheme.colors.dark700)
            Badge(
                label = uiModel.flairName.uppercase(),
                background = background,
                foreground = background.getContrastColor()
            )
        }
    }
}

@Composable
private fun Comment(comment: String) {
    Text(
        text = comment,
        color = AthTheme.colors.dark700,
        style = AthTextStyle.Calibre.Utility.Regular.Large,
        overflow = TextOverflow.Ellipsis,
        maxLines = 3,
        modifier = Modifier.defaultMinSize(minHeight = 32.dp)
    )
}

@DayNightPreview
@DevicePreviewSmallAndLarge
@Composable
private fun TopCommentPreview(
    @PreviewParameter(TopCommentItemPreviewProvider::class) uiModel: TopCommentUiModel
) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        TopComment(uiModel, ItemInteractor())
    }
}

internal class TopCommentItemPreviewProvider : PreviewParameterProvider<TopCommentUiModel> {
    override val values: Sequence<TopCommentUiModel> = sequenceOf(
        topCommentUserPreviewData(),
        topCommentStaffPreviewData(),
        topCommentShortCommentPreviewData()
    )

    private fun topCommentUserPreviewData() = TopCommentUiModel(
        id = "commentId",
        avatarUrl = null,
        author = "Elijah M.",
        commentedAt = "12m".asResourceString(),
        flairName = "NYJ",
        flairColor = "0E823C",
        isStaff = false,
        comment = "I think it will be close at times but the Jets should win. We’ve got players coming back and Cowboys are missing OL and CB. Hopefully Zach comes out and saves the day.",
        permalink = "permalink",
        analyticsData = analyticsPreviewData()
    )

    private fun topCommentStaffPreviewData() = TopCommentUiModel(
        id = "commentId",
        avatarUrl = null,
        author = "James L. Edwards III",
        commentedAt = "8m".asResourceString(),
        flairName = null,
        flairColor = null,
        isStaff = true,
        comment = "I think it will be close at times but the Jets should win. We’ve got players coming back and Cowboys are missing OL and CB. Hopefully Zach comes out and saves the day.",
        permalink = "permalink",
        analyticsData = analyticsPreviewData()
    )

    private fun topCommentShortCommentPreviewData() = TopCommentUiModel(
        id = "commentId",
        avatarUrl = null,
        author = "Elijah M.",
        commentedAt = "12m".asResourceString(),
        flairName = "NYJ",
        flairColor = "0E823C",
        isStaff = false,
        comment = "I think it will be close at times, but we got this.",
        permalink = "permalink",
        analyticsData = analyticsPreviewData()
    )
}