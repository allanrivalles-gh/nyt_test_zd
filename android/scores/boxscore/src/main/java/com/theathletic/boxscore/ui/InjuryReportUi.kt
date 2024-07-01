package com.theathletic.boxscore.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.boxscore.ui.InjuryReportUi.Companion.NUM_OF_INJURIES_ON_SUMMARY
import com.theathletic.boxscore.ui.modules.InjuryReportSummaryModule
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.Headshot
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.buttons.TwoItemToggleButton

class InjuryReportUi {
    data class PlayerInjury(
        val name: String,
        val position: String,
        val headshots: SizedImages,
        val type: InjuryType,
        val injury: ResourceString
    )

    data class TeamDetails(
        val name: String,
        val logoUrls: SizedImages,
        val teamColor: Color
    )

    enum class InjuryType(
        @StringRes val labelRes: Int,
    ) {
        D7(R.string.box_score_injury_type_d7),
        D10(R.string.box_score_injury_type_d10),
        D15(R.string.box_score_injury_type_d15),
        D60(R.string.box_score_injury_type_d60),
        DAY(R.string.box_score_injury_type_day),
        DAY_TO_DAY(R.string.box_score_injury_type_day_to_day),
        DOUBTFUL(R.string.box_score_injury_type_doubtful),
        OUT(R.string.box_score_injury_type_out),
        OUT_FOR_SEASON(R.string.box_score_injury_type_out_for_season),
        OUT_INDEFINITELY(R.string.box_score_injury_type_out_indefinitely),
        QUESTIONABLE(R.string.box_score_injury_type_questionable),
        UNKNOWN(R.string.box_score_injury_type_unknown);

        val color: Color
            @Composable
            @ReadOnlyComposable
            get() = when (this) {
                D7 -> AthTheme.colors.red
                D10 -> AthTheme.colors.red
                D15 -> AthTheme.colors.red
                D60 -> AthTheme.colors.red
                DAY -> AthTheme.colors.yellow
                DAY_TO_DAY -> AthTheme.colors.yellow
                DOUBTFUL -> AthTheme.colors.red
                OUT -> AthTheme.colors.red
                OUT_FOR_SEASON -> AthTheme.colors.red
                OUT_INDEFINITELY -> AthTheme.colors.red
                QUESTIONABLE -> AthTheme.colors.yellow
                UNKNOWN -> AthTheme.colors.yellow
            }
    }

    companion object {
        const val NUM_OF_INJURIES_ON_SUMMARY = 2
    }

    interface Interactor {
        fun onBackButtonClicked()
        fun onTeamSelected(firstTeamSelected: Boolean)
    }

    interface SummaryInteractor {
        fun onInjuryReportFullReportClick(gameId: String, isFirstTeamSelected: Boolean)
    }
}

@Composable
fun InjuryReportScreen(
    gameDetails: ResourceString,
    firstTeam: InjuryReportUi.TeamDetails,
    secondTeam: InjuryReportUi.TeamDetails,
    firstTeamInjuries: List<InjuryReportUi.PlayerInjury>,
    secondTeamInjuries: List<InjuryReportUi.PlayerInjury>,
    isFirstTeamSelected: Boolean,
    interactor: InjuryReportUi.Interactor
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AthTheme.colors.dark100)
    ) {
        Toolbar(
            gameDetails = gameDetails.asString(),
            onBackClicked = {
                interactor.onBackButtonClicked()
            }
        )
        InjuryList(
            firstTeam = firstTeam,
            secondTeam = secondTeam,
            isFirstTeamSelected = isFirstTeamSelected,
            injuries = if (isFirstTeamSelected) {
                firstTeamInjuries
            } else {
                secondTeamInjuries
            },
            interactor = interactor
        )
    }
}

@Composable
fun InjuryReportSummary(
    gameId: String,
    firstTeam: InjuryReportUi.TeamDetails,
    secondTeam: InjuryReportUi.TeamDetails,
    firstTeamInjuries: List<InjuryReportUi.PlayerInjury>,
    secondTeamInjuries: List<InjuryReportUi.PlayerInjury>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        BoxScoreHeaderTitle(R.string.box_score_injury_report_title)
        if (firstTeamInjuries.isNotEmpty()) {
            InjuryReportTeamSummary(
                gameId = gameId,
                teamDetails = firstTeam,
                injuries = firstTeamInjuries,
                isFirstTeam = true,
            )
        }
        if (secondTeamInjuries.isNotEmpty()) {
            InjuryReportTeamSummary(
                gameId = gameId,
                teamDetails = secondTeam,
                injuries = secondTeamInjuries,
                isFirstTeam = false,
            )
        }
        if (firstTeamInjuries.isEmpty() && secondTeamInjuries.isEmpty()) {
            InjuryReportSummaryEmpty()
        }
    }
}

// todo (Mark): Delete when cleaning up and removing the InjuryReportSummary for old feed
@Deprecated("Use InjuryReportSummary for the compose Feed")
@Composable
fun InjuryReportSummaryForRVFeed(
    gameId: String,
    firstTeam: InjuryReportUi.TeamDetails,
    secondTeam: InjuryReportUi.TeamDetails,
    firstTeamInjuries: List<InjuryReportUi.PlayerInjury>,
    secondTeamInjuries: List<InjuryReportUi.PlayerInjury>,
    interactor: InjuryReportUi.SummaryInteractor
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        BoxScoreHeaderDivider()
        BoxScoreHeaderTitle(R.string.box_score_injury_report_title)
        if (firstTeamInjuries.isNotEmpty()) {
            InjuryReportTeamSummary(
                gameId = gameId,
                teamDetails = firstTeam,
                injuries = firstTeamInjuries,
                isFirstTeam = true,
                interactor = interactor
            )
        }
        if (secondTeamInjuries.isNotEmpty()) {
            InjuryReportTeamSummary(
                gameId = gameId,
                teamDetails = secondTeam,
                injuries = secondTeamInjuries,
                isFirstTeam = false,
                interactor = interactor
            )
        }
        if (firstTeamInjuries.isEmpty() && secondTeamInjuries.isEmpty()) {
            InjuryReportSummaryEmpty()
        }
    }
}

@Composable
private fun Toolbar(
    gameDetails: String,
    onBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        Row(Modifier.align(Alignment.CenterStart)) {
            IconButton(onClick = onBackClicked) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = AthTheme.colors.dark800
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(
                text = stringResource(id = R.string.box_score_injury_report_title),
                style = AthTextStyle.Slab.Bold.Small,
                fontSize = 20.sp,
                maxLines = 1,
                color = AthTheme.colors.dark800,
            )
            Text(
                text = gameDetails,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500
            )
        }
    }
}

@Composable
private fun InjuryList(
    firstTeam: InjuryReportUi.TeamDetails,
    secondTeam: InjuryReportUi.TeamDetails,
    isFirstTeamSelected: Boolean,
    injuries: List<InjuryReportUi.PlayerInjury>,
    interactor: InjuryReportUi.Interactor
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AthTheme.colors.dark200)
    ) {
        item {
            TwoItemToggleButton(
                modifier = Modifier
                    .padding(
                        top = 24.dp,
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                itemOneLabel = firstTeam.name.asResourceString(),
                itemTwoLabel = secondTeam.name.asResourceString(),
                isFirstItemSelected = isFirstTeamSelected,
                onTwoItemToggleSelected = interactor::onTeamSelected
            )
        }
        if (injuries.isNotEmpty()) {
            itemsIndexed(injuries) { index, injury ->
                InjuredPlayer(
                    injury = injury,
                    team = if (isFirstTeamSelected) {
                        firstTeam
                    } else {
                        secondTeam
                    },
                    showDivider = injuries.lastIndex != index
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = stringResource(id = R.string.box_score_no_injuries),
                        style = AthTextStyle.Calibre.Utility.Medium.Small,
                        color = AthTheme.colors.dark500,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 150.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InjuredPlayer(
    injury: InjuryReportUi.PlayerInjury,
    team: InjuryReportUi.TeamDetails,
    showDivider: Boolean
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Headshot(
                headshotsUrls = injury.headshots,
                teamUrls = team.logoUrls,
                teamColor = team.teamColor,
                preferredSize = 40.dp,
                modifier = Modifier
                    .height(40.dp)
                    .width(40.dp)
            )
            InjuryReportText(injury)
        }
        if (showDivider) {
            Divider(
                color = AthTheme.colors.dark300,
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun InjuryReportText(injury: InjuryReportUi.PlayerInjury) {
    Column(
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Row {
            Text(
                text = injury.name,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark700
            )
            Text(
                text = injury.position,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                color = AthTheme.colors.dark500,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
        Text(
            text = stringResource(id = injury.type.labelRes),
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = injury.type.color,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = injury.injury.asString(),
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = AthTheme.colors.dark500,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun InjuryReportTeamSummary(
    gameId: String,
    teamDetails: InjuryReportUi.TeamDetails,
    injuries: List<InjuryReportUi.PlayerInjury>,
    isFirstTeam: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        Text(
            text = teamDetails.name,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark500,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        injuries.take(NUM_OF_INJURIES_ON_SUMMARY)
            .forEachIndexed { index, injury ->
                InjuredPlayer(
                    injury = injury,
                    team = teamDetails,
                    showDivider = injuries.size > index + 1
                )
            }
        val remainingInjuriesToShow = injuries.size - NUM_OF_INJURIES_ON_SUMMARY
        if (remainingInjuriesToShow > 0) {
            InjuryReportSummaryToFullReport(
                gameId = gameId,
                numOfRemainingInjuries = remainingInjuriesToShow,
                isFirstTeamSelected = isFirstTeam,
            )
        }
    }
}

// todo (Mark): Delete when cleaning up and removing the InjuryReportSummary for old feed
@Composable
private fun InjuryReportTeamSummary(
    gameId: String,
    teamDetails: InjuryReportUi.TeamDetails,
    injuries: List<InjuryReportUi.PlayerInjury>,
    isFirstTeam: Boolean,
    interactor: InjuryReportUi.SummaryInteractor
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        Text(
            text = teamDetails.name,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark500,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        injuries.take(NUM_OF_INJURIES_ON_SUMMARY)
            .forEachIndexed { index, injury ->
                InjuredPlayer(
                    injury = injury,
                    team = teamDetails,
                    showDivider = injuries.size > index + 1
                )
            }
        val remainingInjuriesToShow = injuries.size - NUM_OF_INJURIES_ON_SUMMARY
        if (remainingInjuriesToShow > 0) {
            InjuryReportSummaryToFullReport(
                gameId = gameId,
                numOfRemainingInjuries = remainingInjuriesToShow,
                isFirstTeamSelected = isFirstTeam,
                interactor = interactor
            )
        }
    }
}

@Composable
fun InjuryReportSummaryToFullReport(
    gameId: String,
    numOfRemainingInjuries: Int,
    isFirstTeamSelected: Boolean,
) {
    val interactor = LocalFeedInteractor.current

    Box(
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp
            )
            .fillMaxWidth()
            .clickable {
                interactor.send(
                    InjuryReportSummaryModule.Interaction.OnShowFullInjuryReportClick(
                        gameId,
                        isFirstTeamSelected
                    )
                )
            }
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = when (numOfRemainingInjuries) {
                    1 -> StringWithParams(R.string.box_score_one_more_injury).asString()
                    else -> StringWithParams(R.string.box_score_more_injuries, numOfRemainingInjuries).asString()
                },
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark500
            )
            ResourceIcon(
                resourceId = R.drawable.ic_arrow_right,
                tint = AthTheme.colors.dark500,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// todo (Mark): Delete when cleaning up and removing the InjuryReportSummary for old feed
@Composable
fun InjuryReportSummaryToFullReport(
    gameId: String,
    numOfRemainingInjuries: Int,
    isFirstTeamSelected: Boolean,
    interactor: InjuryReportUi.SummaryInteractor
) {
    Box(
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp
            )
            .fillMaxWidth()
            .clickable { interactor.onInjuryReportFullReportClick(gameId, isFirstTeamSelected) }
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = when (numOfRemainingInjuries) {
                    1 -> StringWithParams(R.string.box_score_one_more_injury).asString()
                    else -> StringWithParams(R.string.box_score_more_injuries, numOfRemainingInjuries).asString()
                },
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark500
            )
            ResourceIcon(
                resourceId = R.drawable.ic_arrow_right,
                tint = AthTheme.colors.dark500,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun InjuryReportSummaryEmpty() {
    Box(
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 24.dp
            )
            .fillMaxWidth()
            .background(AthTheme.colors.dark300)
    ) {
        Text(
            text = stringResource(id = R.string.box_score_no_injuries),
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark800,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 16.dp)
        )
    }
}

@Composable
@Preview
private fun InjuryReportScreen_Preview() {
    InjuryReportScreen(
        gameDetails = InjuryReportPreviewData.gameDetail,
        firstTeam = InjuryReportPreviewData.firstTeam,
        secondTeam = InjuryReportPreviewData.secondTeam,
        firstTeamInjuries = InjuryReportPreviewData.injuriesMoreThan2,
        secondTeamInjuries = InjuryReportPreviewData.injuriesMoreThan2,
        isFirstTeamSelected = true,
        interactor = InjuryReportPreviewData.interactor
    )
}

@Composable
@Preview
private fun InjuryReportScreen_LightPreview() {
    AthleticTheme(lightMode = true) {
        InjuryReportScreen(
            gameDetails = InjuryReportPreviewData.gameDetail,
            firstTeam = InjuryReportPreviewData.firstTeam,
            secondTeam = InjuryReportPreviewData.secondTeam,
            firstTeamInjuries = InjuryReportPreviewData.injuriesMoreThan2,
            secondTeamInjuries = InjuryReportPreviewData.injuriesMoreThan2,
            isFirstTeamSelected = false,
            interactor = InjuryReportPreviewData.interactor
        )
    }
}

@Composable
@Preview
private fun InjuryReportScreen_EmptyPreview() {
    InjuryReportScreen(
        gameDetails = InjuryReportPreviewData.gameDetail,
        firstTeam = InjuryReportPreviewData.firstTeam,
        secondTeam = InjuryReportPreviewData.secondTeam,
        firstTeamInjuries = InjuryReportPreviewData.injuriesMoreThan2,
        secondTeamInjuries = emptyList(),
        isFirstTeamSelected = false,
        interactor = InjuryReportPreviewData.interactor
    )
}

@Composable
@Preview
private fun InjuryReportSummary_Preview() {
    InjuryReportSummary(
        gameId = InjuryReportPreviewData.gameId,
        firstTeam = InjuryReportPreviewData.firstTeam,
        secondTeam = InjuryReportPreviewData.secondTeam,
        firstTeamInjuries = InjuryReportPreviewData.injuriesMoreThan2,
        secondTeamInjuries = InjuryReportPreviewData.injuriesMoreThan2,
    )
}

@Composable
@Preview
private fun InjuryReportSummary_NoInjuryPreview() {
    InjuryReportSummary(
        gameId = InjuryReportPreviewData.gameId,
        firstTeam = InjuryReportPreviewData.firstTeam,
        secondTeam = InjuryReportPreviewData.secondTeam,
        firstTeamInjuries = emptyList(),
        secondTeamInjuries = emptyList(),
    )
}