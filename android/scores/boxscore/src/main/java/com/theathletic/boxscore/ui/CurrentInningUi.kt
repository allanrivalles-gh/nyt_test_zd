package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.BoxScorePreviewData.baseballCurrentInningInteractor
import com.theathletic.boxscore.ui.BoxScorePreviewData.baseballCurrentInningMultiPlayMock
import com.theathletic.boxscore.ui.BoxScorePreviewData.baseballCurrentInningPlayMock
import com.theathletic.boxscore.ui.modules.BaseballPlayModule
import com.theathletic.boxscore.ui.modules.CurrentInningPlay
import com.theathletic.boxscore.ui.modules.FullPlayByPlays
import com.theathletic.boxscore.ui.modules.IndicatorType
import com.theathletic.boxscore.ui.modules.InningStatusIndicator
import com.theathletic.boxscore.ui.modules.PitcherBatterUi
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.UiModel

sealed class CurrentInningUi {
    data class Play(
        val title: String,
        val plays: List<BaseballPlayModule.PitchPlay>
    )

    data class PlayerSummary(
        val headshotList: SizedImages,
        val name: String,
        val playInfo: ResourceString,
        val stats: ResourceString,
        val lastPlay: ResourceString,
        val title: ResourceString,
        val teamColor: Color
    )

    data class CurrentPlayStatus(
        val type: IndicatorType,
        val count: Int
    )
}

data class BaseballCurrentInningPlayUiModel(
    val id: String,
    val batter: CurrentInningUi.PlayerSummary?,
    val pitcher: CurrentInningUi.PlayerSummary?,
    val currentInning: List<CurrentInningUi.Play>,
    val playStatus: List<CurrentInningUi.CurrentPlayStatus>

) : UiModel {
    override val stableId = "BaseballCurrentInningPlayUiModel:$id"

    interface Interactor {
        fun onFullPlayByPlayClick()
    }
}

@Composable
fun CurrentInning(
    batter: CurrentInningUi.PlayerSummary?,
    pitcher: CurrentInningUi.PlayerSummary?,
    currentInning: List<CurrentInningUi.Play>,
    playStatus: List<CurrentInningUi.CurrentPlayStatus>,
    interactor: FeedInteractor
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {

        BoxScoreHeaderTitle(R.string.box_score_scoring_current_inning)
        PitcherBatterUi(pitcher, batter)
        if (playStatus.isNotEmpty()) {
            InningStatusIndicator(playStatus)
        }
        if (currentInning.isNotEmpty()) {
            CurrentInningPlay(currentInning)
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp)
                    .height(1.dp),
                color = AthTheme.colors.dark300
            )
        }
        FullPlayByPlays(interactor)
        BoxScoreFooterDivider(includeBottomBar = false)
    }
}

@Deprecated("Use FeedModule version above")
@Composable
fun CurrentInning(
    baseballCurrentInningPlayUiModel: BaseballCurrentInningPlayUiModel,
    interactor: BaseballCurrentInningPlayUiModel.Interactor
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {

        BoxScoreHeaderTitle(R.string.box_score_scoring_current_inning)

        PitcherBatterUi(
            baseballCurrentInningPlayUiModel.pitcher,
            baseballCurrentInningPlayUiModel.batter
        )

        if (baseballCurrentInningPlayUiModel.playStatus.isNotEmpty()) {
            InningStatusIndicator(baseballCurrentInningPlayUiModel.playStatus)
        }

        if (baseballCurrentInningPlayUiModel.currentInning.isNotEmpty()) {
            CurrentInningPlay(baseballCurrentInningPlayUiModel.currentInning)
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp)
                    .height(1.dp),
                color = AthTheme.colors.dark300
            )
        }

        FullPlayByPlays(interactor)

        BoxScoreFooterDivider(includeBottomBar = false)
    }
}

@Preview
@Composable
private fun CurrentInning_Preview() {
    CurrentInning(
        baseballCurrentInningPlayMock,
        baseballCurrentInningInteractor
    )
}

@Preview
@Composable
private fun CurrentInning_PreviewLight() {
    AthleticTheme(lightMode = true) {
        CurrentInning(
            baseballCurrentInningPlayMock,
            baseballCurrentInningInteractor
        )
    }
}

@Preview
@Composable
private fun CurrentInningMultiPlay_Preview() {
    CurrentInning(
        baseballCurrentInningMultiPlayMock,
        baseballCurrentInningInteractor
    )
}

@Preview
@Composable
private fun CurrentInningMultiPlay_PreviewLight() {
    AthleticTheme(lightMode = true) {
        CurrentInning(
            baseballCurrentInningMultiPlayMock,
            baseballCurrentInningInteractor
        )
    }
}

@Preview
@Composable
private fun CurrentInningNullBatter_Preview() {
    CurrentInning(
        baseballCurrentInningMultiPlayMock.copy(batter = null),
        baseballCurrentInningInteractor
    )
}

@Preview
@Composable
private fun CurrentInningNullPitcher_Preview() {
    CurrentInning(
        baseballCurrentInningMultiPlayMock.copy(pitcher = null),
        baseballCurrentInningInteractor
    )
}

@Preview
@Composable
private fun CurrentInningNullBatterPitcher_Preview() {
    CurrentInning(
        baseballCurrentInningMultiPlayMock.copy(pitcher = null, batter = null),
        baseballCurrentInningInteractor
    )
}

@Preview
@Composable
private fun CurrentInningNullPlays_Preview() {
    CurrentInning(
        baseballCurrentInningMultiPlayMock.copy(currentInning = emptyList()),
        baseballCurrentInningInteractor
    )
}

@Preview
@Composable
private fun CurrentInningEmptyPlays_Preview() {
    CurrentInning(
        baseballCurrentInningMultiPlayMock.copy(currentInning = emptyList()),
        baseballCurrentInningInteractor
    )
}

@Preview
@Composable
private fun CurrentInningOnePlays_Preview() {
    CurrentInning(
        baseballCurrentInningMultiPlayMock.copy(
            currentInning = listOfNotNull(
                baseballCurrentInningMultiPlayMock.currentInning.firstOrNull()
            )
        ),
        baseballCurrentInningInteractor
    )
}