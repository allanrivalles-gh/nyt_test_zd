package com.theathletic.boxscore.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.BoxScorePreviewData.gameOddsMock
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.UiModel
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.TeamLogo
import java.util.Locale

private val roundedCornerRadius = 4.dp

sealed class GameOddsUi {
    data class TeamOdds(
        val label: String,
        val logoUrlList: SizedImages,
        val spreadLine: ResourceString,
        val spreadUsOdds: String,
        val totalDirection: ResourceString?,
        val totalUsOdds: String,
        val moneyUsOdds: String,
    )
}

@Deprecated("Use GameOddsModule")
data class BoxScoreGameOddsUiModel(
    val id: String,
    val firstTeam: GameOddsUi.TeamOdds,
    val secondTeam: GameOddsUi.TeamOdds,
    val showHeaderDivider: Boolean
) : UiModel {
    override val stableId = "BoxScoreGameOdds:$id"
}

@Composable
fun GameOdds(
    firstTeamOdds: GameOddsUi.TeamOdds,
    secondTeamOdds: GameOddsUi.TeamOdds,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {

            GameOddsHeader(Modifier.fillMaxWidth())
            OddsValueHeaders(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 12.dp,
                        top = 24.dp
                    )
            )
            TeamOddsRow(
                Modifier.fillMaxWidth(),
                RoundedCornerShape(topStart = roundedCornerRadius),
                RoundedCornerShape(topEnd = roundedCornerRadius),
                label = firstTeamOdds.label,
                logoUrlList = firstTeamOdds.logoUrlList,
                moneyUsOdds = firstTeamOdds.moneyUsOdds,
                spreadLine = firstTeamOdds.spreadLine,
                spreadUsOdds = firstTeamOdds.spreadUsOdds,
                totalDirection = firstTeamOdds.totalDirection,
                totalUsOdds = firstTeamOdds.totalUsOdds
            )
            TeamOddsRow(
                Modifier.fillMaxWidth(),
                RoundedCornerShape(bottomStart = roundedCornerRadius),
                RoundedCornerShape(bottomEnd = roundedCornerRadius),
                label = secondTeamOdds.label,
                logoUrlList = secondTeamOdds.logoUrlList,
                moneyUsOdds = secondTeamOdds.moneyUsOdds,
                spreadLine = secondTeamOdds.spreadLine,
                spreadUsOdds = secondTeamOdds.spreadUsOdds,
                totalDirection = secondTeamOdds.totalDirection,
                totalUsOdds = secondTeamOdds.totalUsOdds
            )
            BoxScoreFooterDivider(false)
        }
    }
}

@Deprecated("Not used for FeedModule for only UiModel implementation")
@Composable
fun GameOdds(
    firstTeamLabel: String,
    firstTeamLogoUrlList: SizedImages,
    firstTeamSpreadLine: ResourceString,
    firstTeamSpreadUsOdds: String,
    firstTeamTotalDirection: ResourceString?,
    firstTeamTotalUsOdds: String,
    firstTeamMoneyUsOdds: String,
    secondTeamLabel: String,
    secondTeamLogoUrlList: SizedImages,
    secondTeamSpreadLine: ResourceString,
    secondTeamSpreadUsOdds: String,
    secondTeamTotalDirection: ResourceString?,
    secondTeamTotalUsOdds: String,
    secondTeamMoneyUsOdds: String,
    showHeaderDivider: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        if (showHeaderDivider) {
            BoxScoreHeaderDivider()
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {

            GameOddsHeader(Modifier.fillMaxWidth())
            OddsValueHeaders(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 12.dp,
                        top = 24.dp
                    )
            )
            TeamOddsRow(
                Modifier.fillMaxWidth(),
                RoundedCornerShape(topStart = roundedCornerRadius),
                RoundedCornerShape(topEnd = roundedCornerRadius),
                firstTeamLabel,
                firstTeamLogoUrlList,
                moneyUsOdds = firstTeamMoneyUsOdds,
                spreadLine = firstTeamSpreadLine,
                spreadUsOdds = firstTeamSpreadUsOdds,
                totalDirection = firstTeamTotalDirection,
                totalUsOdds = firstTeamTotalUsOdds
            )
            TeamOddsRow(
                Modifier.fillMaxWidth(),
                RoundedCornerShape(bottomStart = roundedCornerRadius),
                RoundedCornerShape(bottomEnd = roundedCornerRadius),
                secondTeamLabel,
                secondTeamLogoUrlList,
                moneyUsOdds = secondTeamMoneyUsOdds,
                spreadLine = secondTeamSpreadLine,
                spreadUsOdds = secondTeamSpreadUsOdds,
                totalDirection = secondTeamTotalDirection,
                totalUsOdds = secondTeamTotalUsOdds
            )

            BoxScoreFooterDivider(false)
        }
    }
}

@Composable
private fun TeamOddsRow(
    modifier: Modifier,
    cellShapeLeft: RoundedCornerShape,
    cellShapeRight: RoundedCornerShape,
    label: String,
    logoUrlList: SizedImages,
    spreadLine: ResourceString,
    spreadUsOdds: String,
    totalDirection: ResourceString?,
    totalUsOdds: String,
    moneyUsOdds: String,
) {
    OddsRowSlot(
        first =
        {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamLogo(
                    teamUrls = logoUrlList,
                    preferredSize = 28.dp,
                    modifier = Modifier
                        .size(28.dp)
                        .align(alignment = Alignment.CenterVertically)
                )

                Text(
                    modifier = Modifier.padding(8.dp),
                    text = label,
                    style = AthTextStyle.Calibre.Utility.Medium.Large,
                    color = AthTheme.colors.dark700,
                    textAlign = TextAlign.Center
                )
            }
        },
        second = {
            OddsValueCell(
                Modifier.fillMaxWidth(),
                cellShapeLeft,
                spreadLine,
                spreadUsOdds
            )
        },
        third = {
            OddsValueCell(
                Modifier.fillMaxWidth(),
                RoundedCornerShape(size = 0.dp),
                totalDirection,
                totalUsOdds
            )
        },
        fourth = {
            OddsValueCell(
                Modifier.fillMaxWidth(),
                cellShapeRight,
                null,
                moneyUsOdds
            )
        }
    )
}

@Composable
private fun OddsValueCell(
    modifier: Modifier,
    cellShape: RoundedCornerShape,
    lineOrDirection: ResourceString?,
    usOdds: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(1.dp)
            .background(
                color = AthTheme.colors.dark300,
                shape = cellShape
            )
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            lineOrDirection?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = lineOrDirection.asString(),
                    color = AthTheme.colors.dark700,
                    style = AthTextStyle.Calibre.Utility.Medium.Small,
                    textAlign = TextAlign.Center
                )
            }

            val extraPaddingForSingleValue = if (lineOrDirection == null) 8.dp else 0.dp
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = extraPaddingForSingleValue),
                text = usOdds,
                color = AthTheme.colors.green,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun OddsValueHeaders(modifier: Modifier) {
    OddsRowSlot(
        first = {},
        second = {
            Text(
                modifier = modifier,
                text = stringResource(id = R.string.box_score_game_odds_spread_label).uppercase(Locale.getDefault()),
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                textAlign = TextAlign.Center
            )
        },
        third = {
            Text(
                modifier = modifier,
                text = stringResource(id = R.string.box_score_game_odds_total_label).uppercase(Locale.getDefault()),
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                textAlign = TextAlign.Center
            )
        },
        fourth = {
            Text(
                modifier = modifier,
                text = stringResource(id = R.string.box_score_game_odds_money_label).uppercase(Locale.getDefault()),
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                textAlign = TextAlign.Center
            )
        }
    )
}

@Composable
private fun OddsRowSlot(
    first: @Composable BoxScope.() -> Unit,
    second: @Composable BoxScope.() -> Unit,
    third: @Composable BoxScope.() -> Unit,
    fourth: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Box(modifier = Modifier.weight(1.5f)) { first() }
            Box(modifier = Modifier.weight(1f)) { second() }
            Box(modifier = Modifier.weight(1f)) { third() }
            Box(modifier = Modifier.weight(1f)) { fourth() }
        }
    }
}

@Composable
private fun GameOddsHeader(modifier: Modifier) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(R.string.box_score_scoring_game_odds_title),
            color = AthTheme.colors.dark700,
            modifier = Modifier
                .background(color = AthTheme.colors.dark200),
            style = AthTextStyle.Slab.Bold.Small
        )

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.box_score_game_odds_powered_by),
                color = AthTheme.colors.dark700,
                modifier = Modifier
                    .background(color = AthTheme.colors.dark200)
                    .padding(end = 6.dp),
                style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall
            )

            Image(
                painter = painterResource(id = R.drawable.logo_bet_mgm_lion),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 0.dp)
                    .size(14.dp, 14.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.logo_bet_mgm_text),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 0.dp)
                    .size(38.dp, 14.dp)
            )
        }
    }
}

@Preview
@Composable
private fun GameOdds_Preview() {
    GameOdds(
        firstTeamOdds = gameOddsMock.firstTeamOdds,
        secondTeamOdds = gameOddsMock.firstTeamOdds,
    )
}

@Preview(device = Devices.PIXEL)
@Composable
private fun GameOdds_PreviewSmallDevice() {
    GameOdds(
        firstTeamOdds = gameOddsMock.firstTeamOdds,
        secondTeamOdds = gameOddsMock.firstTeamOdds,
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun GameOdds_PreviewLargeDevice() {
    GameOdds(
        firstTeamOdds = gameOddsMock.firstTeamOdds,
        secondTeamOdds = gameOddsMock.firstTeamOdds,
    )
}

@Preview
@Composable
private fun GameOdds_PreviewLight() {
    AthleticTheme(lightMode = true) {
        GameOdds(
            firstTeamOdds = gameOddsMock.firstTeamOdds,
            secondTeamOdds = gameOddsMock.firstTeamOdds,
        )
    }
}