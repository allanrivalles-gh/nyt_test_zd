package com.theathletic.feed.compose.ui.items.scores

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.feed.R
import com.theathletic.feed.compose.SOURCE_FEED
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.links.deep.Deeplink
import com.theathletic.themes.AthFont
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.widgets.PulsingIcon
import com.theathletic.ui.widgets.RemoteImageAsync

data class ScoresCarouselItemUiModel(
    override val id: String,
    override val permalink: String?,
    override val analyticsData: AnalyticsData,
    val shouldHideScores: Boolean,
    val showCommentsButton: Boolean,
    val rows: Pair<Row, Row>,
    val scrollIndex: Int
) : LayoutUiModel.Item {

    override fun deepLink(): Deeplink = Deeplink.boxScore(id).addSource(SOURCE_FEED)

    data class Row(
        val logoUrl: String?,
        val identifier: ResourceString,
        val displayScore: String?,
        val isTextDimmed: Boolean,
        val isStatusTextHighlighted: Boolean,
        val statusString: ResourceString,
    )

    val discussDeeplink: String get() = "theathletic://boxscore/$id#comment_id=0"
}

private fun ScoresCarouselItemUiModel.rowsList(): List<ScoresCarouselItemUiModel.Row> {
    return rows.let { listOf(it.first, it.second) }
}

@Composable
internal fun ScoresCarouselItem(uiModel: ScoresCarouselItemUiModel, itemInteractor: ItemInteractor) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .interactive(uiModel, itemInteractor)
            .background(color = AthTheme.colors.dark200)
            .padding(
                start = 16.dp,
                top = 8.dp,
                end = if (uiModel.showCommentsButton) 4.dp else 16.dp,
                bottom = 8.dp,
            )
    ) {
        val verticalArrangement = Arrangement.spacedBy(6.dp)
        val rows = uiModel.rowsList()
        TeamsInfo(uiModel = uiModel, verticalArrangement = verticalArrangement)
        Column(
            verticalArrangement = verticalArrangement,
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = if (uiModel.shouldHideScores) 20.dp else 16.dp)
        ) {
            for (row in rows) {
                ItemText(row.statusString.asString(), isHighlighted = row.isStatusTextHighlighted)
            }
        }
        if (uiModel.showCommentsButton) {
            CommentsCtaButton(
                modifier = Modifier.padding(start = 4.dp),
                onClick = {
                    itemInteractor.onNavLinkClick(uiModel, uiModel.discussDeeplink, null)
                }
            )
        }
    }
}

@Composable
private fun TeamsInfo(
    uiModel: ScoresCarouselItemUiModel,
    verticalArrangement: Arrangement.Vertical,
) {
    val rows = uiModel.rowsList()
    Column(
        verticalArrangement = verticalArrangement,
    ) {
        for (row in rows) {
            RemoteImageAsync(
                url = row.logoUrl,
                fallbackImage = R.drawable.ic_team_logo_placeholder,
                modifier = Modifier.size(16.dp)
            )
        }
    }
    Column(
        verticalArrangement = verticalArrangement,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        for (row in rows) {
            ItemText(
                row.identifier.asString(),
                isDimmed = row.isTextDimmed,
                style = TextStyle(letterSpacing = 0.21.sp),
            )
        }
    }
    if (uiModel.shouldHideScores.not()) {
        Column(
            verticalArrangement = verticalArrangement,
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            for (row in rows) {
                ItemText(row.displayScore ?: "", isDimmed = row.isTextDimmed)
            }
        }
    }
}

@Composable
private fun ItemText(
    text: String,
    isHighlighted: Boolean = false,
    isDimmed: Boolean = true,
    style: TextStyle = TextStyle(),
) {
    Text(
        text,
        color = when {
            isHighlighted -> AthTheme.colors.red
            isDimmed -> AthTheme.colors.dark500
            else -> AthTheme.colors.dark700
        },
        style = style.copy(fontFamily = AthFont.Sohne, fontSize = 12.sp),
    )
}

@Composable
private fun CommentsCtaButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    PulsingIcon(
        layoutSize = 36.dp,
        iconId = R.drawable.ic_news_comment,
        iconSize = 12.dp,
        animationColor = AthTheme.colors.dark800,
        color = AthTheme.colors.dark700,
        circleSize = 36.dp,
        strokeWidth = 2.dp,
        modifier = modifier
            .size(36.dp)
            .clickable(onClick = onClick)
    )
}

@DayNightPreview
@Composable
private fun FeedScoresItemUiPreview(@PreviewParameter(FeedScoresItemParameterProvider::class) uiModel: ScoresCarouselItemUiModel) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        ScoresCarouselItem(uiModel, ItemInteractor())
    }
}

internal class FeedScoresItemParameterProvider : PreviewParameterProvider<ScoresCarouselItemUiModel> {
    override val values = sequenceOf(
        ScoresCarouselItemUiModel(
            id = "1",
            analyticsData = analyticsPreviewData(),
            permalink = null,
            shouldHideScores = true,
            showCommentsButton = false,
            rows = Pair(
                ScoresCarouselItemUiModel.Row(
                    logoUrl = null,
                    identifier = ResourceString.StringWrapper("SCH"),
                    displayScore = null,
                    isTextDimmed = false,
                    isStatusTextHighlighted = false,
                    statusString = ResourceString.StringWrapper("Sat"),
                ),
                ScoresCarouselItemUiModel.Row(
                    logoUrl = "https://cdn-team-logos.theathletic.com/team-logo-153-72x72.png",
                    identifier = ResourceString.StringWrapper("UCONN"),
                    displayScore = null,
                    isTextDimmed = false,
                    isStatusTextHighlighted = false,
                    statusString = ResourceString.StringWrapper("14:00"),
                ),
            ),
            scrollIndex = 0,
        ),
        ScoresCarouselItemUiModel(
            id = "2",
            analyticsData = analyticsPreviewData(),
            permalink = null,
            shouldHideScores = false,
            showCommentsButton = false,
            rows = Pair(
                ScoresCarouselItemUiModel.Row(
                    logoUrl = "https://cdn-team-logos.theathletic.com/team-logo-1185-72x72.png",
                    identifier = ResourceString.StringWrapper("AUS"),
                    displayScore = "0 (7)",
                    isTextDimmed = false,
                    isStatusTextHighlighted = false,
                    statusString = ResourceString.StringWrapper("Aug 12"),
                ),
                ScoresCarouselItemUiModel.Row(
                    logoUrl = "https://cdn-team-logos.theathletic.com/team-logo-779-72x72.png",
                    identifier = ResourceString.StringWrapper("FRA"),
                    displayScore = "0 (6)",
                    isTextDimmed = true,
                    isStatusTextHighlighted = false,
                    statusString = ResourceString.StringWrapper("FT"),
                ),
            ),
            scrollIndex = 1
        ),
        ScoresCarouselItemUiModel(
            id = "3",
            analyticsData = analyticsPreviewData(),
            permalink = null,
            shouldHideScores = false,
            showCommentsButton = true,
            rows = Pair(
                ScoresCarouselItemUiModel.Row(
                    logoUrl = "https://cdn-team-logos.theathletic.com/team-logo-35-72x72.png",
                    identifier = ResourceString.StringWrapper("CAR"),
                    displayScore = "10",
                    isTextDimmed = false,
                    isStatusTextHighlighted = true,
                    statusString = ResourceString.StringWrapper("Q3"),
                ),
                ScoresCarouselItemUiModel.Row(
                    logoUrl = "https://cdn-team-logos.theathletic.com/team-logo-36-72x72.png",
                    identifier = ResourceString.StringWrapper("CHI"),
                    displayScore = "9",
                    isTextDimmed = false,
                    isStatusTextHighlighted = false,
                    statusString = ResourceString.StringWrapper("14:03"),
                ),
            ),
            scrollIndex = 2
        ),
    )
}