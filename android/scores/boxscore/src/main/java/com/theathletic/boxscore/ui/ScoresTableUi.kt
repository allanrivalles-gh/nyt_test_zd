package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.BoxScorePreviewData.scoresTable3QColumns
import com.theathletic.boxscore.ui.BoxScorePreviewData.scoresTable3QTotalColumns
import com.theathletic.boxscore.ui.BoxScorePreviewData.scoresTable4QColumns
import com.theathletic.boxscore.ui.BoxScorePreviewData.scoresTable4QTotalColumns
import com.theathletic.boxscore.ui.BoxScorePreviewData.scoresTableBaseballColumns
import com.theathletic.boxscore.ui.BoxScorePreviewData.scoresTableBaseballTotalColumns
import com.theathletic.boxscore.ui.BoxScorePreviewData.teamLogo
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.UiModel
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.SimpleShrinkToFitText
import com.theathletic.ui.widgets.TeamLogo

data class BoxScoresScoreTableUiModel(
    val id: String,
    val firstTeamName: String,
    val secondTeamName: String,
    val firstTeamLogoUrlList: SizedImages,
    val secondTeamLogoUrlList: SizedImages,
    val columns: List<ScoreTableColumn>,
    val totalsColumns: List<ScoreTableColumn>,
    val currentPeriodColumnIndex: Int,
    val showFooterDivider: Boolean,
    val scrollToInningIndex: Int = 0
) : UiModel {
    override val stableId = "BoxScoresScoreTable:$id"

    data class ScoreTableColumn(
        val title: ResourceString,
        val firstTeamValue: String,
        val secondTeamValue: String
    )
}

@Composable
fun ScoreTable(
    firstTeamName: String,
    secondTeamName: String,
    firstTeamLogoUrlList: SizedImages,
    secondTeamLogoUrlList: SizedImages,
    columns: List<BoxScoresScoreTableUiModel.ScoreTableColumn>,
    totalsColumns: List<BoxScoresScoreTableUiModel.ScoreTableColumn>,
    currentPeriodColumnIndex: Int,
    showFooterDivider: Boolean,
    scrollToInningIndex: Int
) {
    val isExtendedScoreTable = totalsColumns.size > 1
    Column {
        Row(
            modifier = Modifier
                .background(color = AthTheme.colors.dark200)
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth()
        ) {

            if (isExtendedScoreTable) {
                RenderTeams(
                    firstTeamName = firstTeamName,
                    secondTeamName = secondTeamName,
                    firstTeamLogoUrlList = firstTeamLogoUrlList,
                    secondTeamLogoUrlList = secondTeamLogoUrlList,
                    weightValue = 0.3f
                )
                RenderExtendedScoreColumns(
                    columns = columns,
                    totalsColumns = totalsColumns,
                    currentPeriodColumnIndex = currentPeriodColumnIndex,
                    scrollToInningIndex = scrollToInningIndex
                )
            } else {
                RenderTeams(
                    firstTeamName = firstTeamName,
                    secondTeamName = secondTeamName,
                    firstTeamLogoUrlList = firstTeamLogoUrlList,
                    secondTeamLogoUrlList = secondTeamLogoUrlList,
                    weightValue = 0.4f
                )
                RenderScoreColumns(
                    columns = columns,
                    totalsColumns = totalsColumns,
                    currentPeriodColumnIndex = currentPeriodColumnIndex
                )
            }
        }
        BoxScoreFooterDivider(showFooterDivider)
    }
}

@Composable
private fun RowScope.RenderTeams(
    firstTeamName: String,
    secondTeamName: String,
    firstTeamLogoUrlList: SizedImages,
    secondTeamLogoUrlList: SizedImages,
    weightValue: Float
) {
    var teamNameFontSize by remember { mutableStateOf(AthTextStyle.Calibre.Utility.Medium.Small.fontSize) }

    Row(
        modifier = Modifier.weight(weightValue),
        horizontalArrangement = Arrangement.Start
    ) {
        ScoreColumnSlot(
            rowOne = {
                Spacer(modifier = Modifier.height(10.dp))
            },
            rowTwo = {
                TeamNameLogo(
                    teamName = firstTeamName,
                    teamLogos = firstTeamLogoUrlList,
                    fontSize = teamNameFontSize,
                    onFontSizeChanged = { newFontSize -> teamNameFontSize = newFontSize },
                )
            },
            rowThree = {
                TeamNameLogo(
                    teamName = secondTeamName,
                    teamLogos = secondTeamLogoUrlList,
                    fontSize = teamNameFontSize,
                    onFontSizeChanged = { newFontSize -> teamNameFontSize = newFontSize },
                )
            }
        )
    }
}

@Composable
private fun RowScope.RenderScoreColumns(
    columns: List<BoxScoresScoreTableUiModel.ScoreTableColumn>,
    totalsColumns: List<BoxScoresScoreTableUiModel.ScoreTableColumn>,
    currentPeriodColumnIndex: Int
) {
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        modifier = Modifier.weight(0.6f),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        itemsIndexed(columns) { index, scoreTableColumn ->
            ScoreColumn(
                scoreTableColumn.title,
                firstTeamValue = scoreTableColumn.firstTeamValue,
                secondTeamValue = scoreTableColumn.secondTeamValue,
                highlightText = index == currentPeriodColumnIndex,
                false
            )
        }

        items(totalsColumns) { total ->
            ScoreColumn(
                title = total.title,
                firstTeamValue = total.firstTeamValue,
                secondTeamValue = total.secondTeamValue,
                highlightText = true,
                false
            )
        }
    }
}

@Composable
private fun RowScope.RenderExtendedScoreColumns(
    columns: List<BoxScoresScoreTableUiModel.ScoreTableColumn>,
    totalsColumns: List<BoxScoresScoreTableUiModel.ScoreTableColumn>,
    currentPeriodColumnIndex: Int,
    scrollToInningIndex: Int
) {
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        modifier = Modifier
            .weight(0.8f),
        horizontalArrangement = Arrangement.End
    ) {
        itemsIndexed(columns) { index, scoreTableColumn ->
            ScoreColumn(
                scoreTableColumn.title,
                firstTeamValue = scoreTableColumn.firstTeamValue,
                secondTeamValue = scoreTableColumn.secondTeamValue,
                highlightText = index == currentPeriodColumnIndex,
                true
            )
        }
    }
    Row(
        modifier = Modifier.weight(0.3f),
        horizontalArrangement = Arrangement.Start
    ) {
        totalsColumns.forEach { total ->
            ScoreColumn(
                title = total.title,
                firstTeamValue = total.firstTeamValue,
                secondTeamValue = total.secondTeamValue,
                true,
                true
            )
        }
    }
    ScrollScoreTable(
        scrollToInningIndex = scrollToInningIndex.plus(1),
        listState = listState
    )
}

@Composable
private fun ScrollScoreTable(
    scrollToInningIndex: Int,
    listState: LazyListState
) {
    val shouldScroll = remember {
        derivedStateOf {
            scrollToInningIndex in 0..listState.layoutInfo.totalItemsCount &&
                listState.layoutInfo.visibleItemsInfo.map { it.index }
                    .contains(scrollToInningIndex).not()
        }
    }
    if (shouldScroll.value) {
        LaunchedEffect(scrollToInningIndex) {
            listState.animateScrollToItem(scrollToInningIndex)
        }
    }
}

@Composable
private fun ScoreColumnSlot(
    rowOne: @Composable BoxScope.() -> Unit,
    rowTwo: @Composable BoxScope.() -> Unit,
    rowThree: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = Modifier.wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(30.dp),
            contentAlignment = Alignment.Center
        ) { rowOne() }
        Box(
            modifier = Modifier
                .height(30.dp),
            contentAlignment = Alignment.Center
        ) { rowTwo() }
        Box(
            modifier = Modifier
                .height(30.dp),
            contentAlignment = Alignment.Center
        ) { rowThree() }
    }
}

@Composable
private fun TeamNameLogo(
    teamName: String,
    teamLogos: SizedImages,
    fontSize: TextUnit,
    onFontSizeChanged: (newFontSize: TextUnit) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        TeamLogo(
            teamUrls = teamLogos,
            preferredSize = 23.dp,
            modifier = Modifier
                .size(23.dp)
                .align(alignment = Alignment.CenterVertically)
        )

        key(fontSize) {
            SimpleShrinkToFitText(
                text = teamName,
                style = AthTextStyle.Calibre.Utility.Medium.Small.copy(
                    color = AthTheme.colors.dark800,
                    fontSize = fontSize
                ),
                maxLines = 1,
                modifier = Modifier.padding(start = 13.dp),
                textAlign = TextAlign.Left,
                onFontSizeChanged = onFontSizeChanged
            )
        }
    }
}

@Composable
private fun ScoreColumn(
    title: ResourceString,
    firstTeamValue: String,
    secondTeamValue: String,
    highlightText: Boolean,
    isExtendedScoreTable: Boolean
) {
    val horizontalPaddings = if (isExtendedScoreTable) 4.dp else 8.dp
    val textColor = if (highlightText) AthTheme.colors.dark800 else AthTheme.colors.dark500

    ScoreColumnSlot(
        rowOne = {
            Text(
                text = title.asString(),
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                color = AthTheme.colors.dark500,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = horizontalPaddings)
                    .defaultMinSize(minWidth = 16.dp)
            )
        },
        rowTwo = {
            Text(
                text = firstTeamValue,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = horizontalPaddings)
                    .defaultMinSize(minWidth = 16.dp)
            )
        },
        rowThree = {
            Text(
                text = secondTeamValue,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = horizontalPaddings)
                    .defaultMinSize(minWidth = 16.dp)
            )
        }
    )
}

@Preview
@Composable
private fun ScoreTable4Q_Preview() {
    ScoreTable(
        firstTeamName = "PHI",
        secondTeamName = "FDS",
        firstTeamLogoUrlList = teamLogo,
        secondTeamLogoUrlList = teamLogo,
        columns = scoresTable4QColumns,
        totalsColumns = scoresTable4QTotalColumns,
        currentPeriodColumnIndex = 1,
        showFooterDivider = false,
        scrollToInningIndex = 0
    )
}

@Preview
@Composable
private fun ScoreTable3Q_Preview() {
    ScoreTable(
        firstTeamName = "PHI",
        secondTeamName = "FDS",
        firstTeamLogoUrlList = teamLogo,
        secondTeamLogoUrlList = teamLogo,
        columns = scoresTable3QColumns,
        totalsColumns = scoresTable3QTotalColumns,
        currentPeriodColumnIndex = 1,
        showFooterDivider = true,
        scrollToInningIndex = 0
    )
}

@Preview
@Composable
private fun ScoreTableBaseball_Preview() {
    ScoreTable(
        firstTeamName = "PHI",
        secondTeamName = "FDS",
        firstTeamLogoUrlList = teamLogo,
        secondTeamLogoUrlList = teamLogo,
        columns = scoresTableBaseballColumns,
        totalsColumns = scoresTableBaseballTotalColumns,
        currentPeriodColumnIndex = 1,
        showFooterDivider = false,
        scrollToInningIndex = 0
    )
}

@Preview
@Composable
private fun ScoreTable4Q_PreviewLight() {
    AthleticTheme(lightMode = true) {
        ScoreTable(
            firstTeamName = "PHI",
            secondTeamName = "FDS",
            firstTeamLogoUrlList = teamLogo,
            secondTeamLogoUrlList = teamLogo,
            columns = scoresTable4QColumns,
            totalsColumns = scoresTable4QTotalColumns,
            currentPeriodColumnIndex = 1,
            showFooterDivider = false,
            scrollToInningIndex = 0
        )
    }
}