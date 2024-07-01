package com.theathletic.comments.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.comments.ui.CommentsUiModel
import com.theathletic.comments.ui.ThreadsUiState
import com.theathletic.comments.ui.preview.CommentBannerColorStringProvider
import com.theathletic.comments.ui.preview.CommentsPreviewData
import com.theathletic.components.HtmlText
import com.theathletic.entity.user.SortType
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.utility.asHexColor
import com.theathletic.ui.utility.getContrastColor
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.VariableSizeBadge

@Composable
fun HeaderSection(
    header: CommentsUi.HeaderModel?,
    teamThreadBanner: CommentsUi.TeamThreadBanner?,
    sortedBy: SortType,
    commentsCount: Int,
    interactor: CommentsUi.Interactor,
) {
    val sortOptions = remember { SortType.values().asList() }
    header?.let {
        when (header) {
            is CommentsUi.HeaderModel.SimpleHeader ->
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = header.text,
                    style = AthTextStyle.TiemposHeadline.Regular.Medium.copy(color = AthTheme.colors.dark700)
                )
            is CommentsUi.HeaderModel.Header ->
                Header(
                    uiModel = header,
                    onLinkClick = { interactor.onLinkClick(it) }
                )
        }
    }
    teamThreadBanner?.let {
        CommentsTeamBanner(
            name = teamThreadBanner.teamName,
            backgroundColor = teamThreadBanner.teamColor,
            logoUrl = teamThreadBanner.teamLogo,
            showChangeTeamThread = teamThreadBanner.showChangeTeamThread,
            onClickTeamBannerChange = interactor::onClickTeamBannerChange
        )
    }
    CommentsFilterCountBar(
        sortOptions = sortOptions,
        selectedOption = sortedBy,
        commentsCount = commentsCount,
        onSortOptionSelected = interactor::onSortOptionSelected
    )
    Spacer(modifier = Modifier.padding(top = 6.dp))
}

@Composable
private fun CommentsTeamBanner(
    name: String,
    backgroundColor: Color,
    logoUrl: String,
    showChangeTeamThread: Boolean = false,
    onClickTeamBannerChange: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(backgroundColor)
            .padding(start = 15.dp)
            .height(36.dp)
            .fillMaxWidth()
    ) {
        RemoteImageAsync(
            url = logoUrl,
            placeholder = R.drawable.ic_team_logo_placeholder,
            fallbackImage = R.drawable.ic_team_logo_placeholder,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "$name ${stringResource(id = R.string.comments_team_banner_followers)}",
            style = AthTextStyle.Calibre.Utility.Medium.Small.copy(
                color = backgroundColor.getContrastColor(),
                letterSpacing = 0.21.sp
            ),
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        if (showChangeTeamThread) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 5.dp)
                    .clickable { onClickTeamBannerChange() }
            ) {
                Text(
                    text = stringResource(id = R.string.comments_team_banner_change),
                    style = AthTextStyle.Calibre.Utility.Regular.Small.copy(
                        color = backgroundColor.getContrastColor(),
                        textAlign = TextAlign.End,
                        letterSpacing = 0.14.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun Header(
    uiModel: CommentsUi.HeaderModel.Header,
    onLinkClick: (String) -> Unit,
) {
    val backgroundHexColor = uiModel.backgroundColor.asHexColor
    val textColor = if (backgroundHexColor == null) {
        AthTheme.colors.dark700
    } else {
        AthColor.Gray700
    }
    Column(
        modifier = Modifier
            .background(color = backgroundHexColor ?: AthTheme.colors.dark200)
            .padding(16.dp)
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            uiModel.badgeUrl?.let { badgeUrl ->
                RemoteImageAsync(url = badgeUrl, modifier = Modifier.size(30.dp))
                Divider(
                    modifier = Modifier
                        .height(22.dp)
                        .width(1.dp),
                    color = textColor,
                    thickness = 1.dp
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = uiModel.labelRes),
                style = AthTextStyle.Calibre.Headline.Medium.Small.copy(color = textColor)
            )
            uiModel.liveTag?.let { liveTag ->
                VariableSizeBadge(
                    text = stringResource(id = liveTag.labelRes).uppercase(),
                    backgroundShape = RoundedCornerShape(2.dp),
                    fontSize = 14.sp,
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = uiModel.title,
            style = AthTextStyle.Calibre.Headline.SemiBold.Medium.copy(color = textColor)
        )
        Spacer(modifier = Modifier.height(24.dp))

        HtmlText(
            text = uiModel.excerpt,
            style = AthTextStyle.TiemposBody.Regular.Medium.copy(color = textColor),
            onLinkClick = onLinkClick
        )

        Spacer(modifier = Modifier.height(54.dp))

        PublicationInfo(uiModel.authorName, uiModel.timeStamp, textColor)
    }
}

@Composable
private fun PublicationInfo(
    authorName: String,
    timestamp: String,
    textColor: Color,
) {
    Text(
        text = authorName,
        style = AthTextStyle.Calibre.Utility.Medium.Small.copy(color = textColor)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = timestamp,
        style = AthTextStyle.Calibre.Utility.Regular.Small.copy(color = textColor)
    )
}

@Preview
@Composable
private fun CommentsScreenHeaderPreview() {
    CommentsUi(
        viewState = CommentsPreviewData.viewState,
        interactor = CommentsPreviewData.CommentsUiPreviewInteractor,
        itemInteractor = CommentsPreviewData.CommentsItemPreviewInteractor,
    )
}

@Preview
@Composable
private fun CommentsScreenHeaderPreview_Light() {
    AthleticTheme(lightMode = true) {
        CommentsUi(
            viewState = CommentsPreviewData.viewState,
            interactor = CommentsPreviewData.CommentsUiPreviewInteractor,
            itemInteractor = CommentsPreviewData.CommentsItemPreviewInteractor,
        )
    }
}

@Preview
@Composable
private fun CommentsScreenHeaderNoBackgroundColorPreview() {
    CommentsUi(
        viewState = CommentsPreviewData.viewState.copy(
            commentsUiModel = CommentsUiModel(
                header = CommentsPreviewData.header.copy(
                    backgroundColor = "",
                    liveTag = null
                )
            )
        ),
        interactor = CommentsPreviewData.CommentsUiPreviewInteractor,
        itemInteractor = CommentsPreviewData.CommentsItemPreviewInteractor,
    )
}

@Preview
@Composable
private fun CommentsScreenHeaderNoBackgroundColorPreview_Light() {
    AthleticTheme(lightMode = true) {
        CommentsUi(
            viewState = CommentsPreviewData.viewState.copy(
                commentsUiModel = CommentsUiModel(
                    header = CommentsPreviewData.header.copy(
                        backgroundColor = "",
                        liveTag = null
                    )
                )
            ),
            interactor = CommentsPreviewData.CommentsUiPreviewInteractor,
            itemInteractor = CommentsPreviewData.CommentsItemPreviewInteractor,
        )
    }
}

@Preview
@Composable
private fun CommentsScreenBannerContrastColorPreview(
    @PreviewParameter(CommentBannerColorStringProvider::class)
    teamColor: String,
) {
    CommentsUi(
        viewState = CommentsPreviewData.viewState.copy(
            commentsUiModel = CommentsUiModel(header = CommentsPreviewData.simpleHeader),
            threadsUiState = ThreadsUiState(
                teamThreadBanner = CommentsPreviewData.teamBanner.copy(teamColor = teamColor.parseHexColor())
            )
        ),
        interactor = CommentsPreviewData.CommentsUiPreviewInteractor,
        itemInteractor = CommentsPreviewData.CommentsItemPreviewInteractor,
    )
}