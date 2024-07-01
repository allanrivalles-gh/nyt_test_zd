package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.modules.SoccerPreviewData
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.asString

data class SoccerRecentFormHeaderModel(
    val expectedGoals: ExpectedGoals,
    val firstTeamRecentForms: List<SoccerRecentFormIcons>,
    val secondTeamRecentForms: List<SoccerRecentFormIcons>
) {
    data class ExpectedGoals(
        val firstTeamValue: String = "",
        val secondTeamValue: String = "",
        val showExpectedGoals: Boolean = false
    )

    enum class SoccerRecentFormIcons {
        WIN, LOSS, DRAW, NONE;

        val color: Color
            @Composable
            @ReadOnlyComposable
            get() = when (this) {
                LOSS -> AthTheme.colors.red
                WIN -> AthTheme.colors.green
                DRAW -> AthTheme.colors.dark400
                NONE -> AthTheme.colors.dark300
            }

        val text: ResourceString
            get() = when (this) {
                LOSS -> StringWithParams(R.string.box_score_soccer_form_guide_letter_lose)
                WIN -> StringWithParams(R.string.box_score_soccer_form_guide_letter_win)
                DRAW -> StringWithParams(R.string.box_score_soccer_form_guide_letter_draw)
                NONE -> StringWithParams(R.string.empty_string)
            }
    }
}

@Composable
fun SoccerRecentFormHeader(
    expectedGoals: SoccerRecentFormHeaderModel.ExpectedGoals,
    firstTeamRecentForms: List<SoccerRecentFormHeaderModel.SoccerRecentFormIcons>,
    secondTeamRecentForms: List<SoccerRecentFormHeaderModel.SoccerRecentFormIcons>,
    isReverse: Boolean,
    showRecentForm: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(
                vertical = 5.dp,
                horizontal = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (showRecentForm) {
            RecentForms(
                teamRecentForms = firstTeamRecentForms,
                modifier = Modifier.weight(0.35f),
                horizontalArrangement = Arrangement.Start,
                isReverse = isReverse
            )
        }

        if (expectedGoals.showExpectedGoals) {
            ExpectedGoalsRow(
                firstTeamGoals = expectedGoals.firstTeamValue,
                secondTeamGoals = expectedGoals.secondTeamValue,
                modifier = Modifier.weight(0.30f)
            )
        } else {
            Spacer(modifier = Modifier.weight(0.30f))
        }

        if (showRecentForm) {
            RecentForms(
                teamRecentForms = secondTeamRecentForms,
                modifier = Modifier.weight(0.35f),
                horizontalArrangement = Arrangement.End,
                isReverse = isReverse
            )
        }
    }
}

@Composable
private fun RowScope.ExpectedGoalsRow(
    firstTeamGoals: String,
    secondTeamGoals: String,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.then(modifier)
    ) {

        Text(
            text = firstTeamGoals,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier.padding(end = 12.dp)
        )

        Text(
            text = stringResource(id = R.string.box_score_expected_goals_label),
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(color = AthTheme.colors.dark300)
                .padding(vertical = 2.dp, horizontal = 6.dp)
        )

        Text(
            text = secondTeamGoals,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
fun RecentForms(
    teamRecentForms: List<SoccerRecentFormHeaderModel.SoccerRecentFormIcons>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal,
    isReverse: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
        modifier = Modifier.then(modifier)

    ) {

        teamRecentForms.forEachIndexed { index, soccerRecentFormIcons ->
            RecentFormIcon(formIcon = soccerRecentFormIcons, alphaIndex = index, isReverse = isReverse)
        }
    }
}

@Composable
private fun RecentFormIcon(
    formIcon: SoccerRecentFormHeaderModel.SoccerRecentFormIcons,
    alphaIndex: Int,
    isReverse: Boolean
) {
    val alpha = getAlpha(index = alphaIndex, isReverse = isReverse)
    val circleColor =
        if (formIcon == SoccerRecentFormHeaderModel.SoccerRecentFormIcons.NONE) {
            formIcon.color
        } else {
            formIcon.color.copy(alpha)
        }
    Box(
        modifier = Modifier
            .padding(horizontal = 1.dp)
            .clip(CircleShape)
            .size(18.dp)
            .background(circleColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = formIcon.text.asString(),
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            color = AthColor.Gray800,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(alpha)
        )
    }
}

private fun getAlpha(index: Int, isReverse: Boolean): Float {
    val alphaMarker = listOf(1.0f, 0.85f, 0.6f, 0.45f, 0.3f)
    return if (isReverse) {
        alphaMarker.reversed().elementAtOrElse(index) { 1.0f }
    } else {
        alphaMarker.elementAtOrElse(index) { 1.0f }
    }
}

@Preview
@Composable
private fun SoccerRecentFormHeader_Preview() {
    SoccerRecentFormHeader(
        expectedGoals = SoccerPreviewData.soccerRecentFormHeaderModel.expectedGoals,
        firstTeamRecentForms = SoccerPreviewData.soccerRecentFormHeaderModel.firstTeamRecentForms,
        secondTeamRecentForms = SoccerPreviewData.soccerRecentFormHeaderModel.secondTeamRecentForms,
        isReverse = false,
        showRecentForm = true
    )
}

@Preview
@Composable
private fun SoccerRecentFormHeader_PreviewLight() {
    AthleticTheme(lightMode = true) {
        SoccerRecentFormHeader(
            expectedGoals = SoccerPreviewData.soccerRecentFormHeaderModel.expectedGoals,
            firstTeamRecentForms = SoccerPreviewData.soccerRecentFormHeaderModel.firstTeamRecentForms,
            secondTeamRecentForms = SoccerPreviewData.soccerRecentFormHeaderModel.secondTeamRecentForms,
            isReverse = true,
            showRecentForm = false
        )
    }
}

@Preview
@Composable
private fun SoccerRecentFormHeaderAllMatches_Preview() {
    SoccerRecentFormHeader(
        expectedGoals = SoccerPreviewData.soccerRecentFormHeaderAllMatchesModel.expectedGoals,
        firstTeamRecentForms = SoccerPreviewData.soccerRecentFormHeaderAllMatchesModel.firstTeamRecentForms,
        secondTeamRecentForms = SoccerPreviewData.soccerRecentFormHeaderAllMatchesModel.secondTeamRecentForms,
        isReverse = true,
        showRecentForm = true
    )
}

@Preview
@Composable
private fun SoccerRecentFormHeaderNoExpectedGoals_Preview() {
    SoccerRecentFormHeader(
        expectedGoals = SoccerPreviewData.soccerRecentFormHeaderModel.expectedGoals.copy(showExpectedGoals = false),
        firstTeamRecentForms = SoccerPreviewData.soccerRecentFormHeaderModel.firstTeamRecentForms,
        secondTeamRecentForms = SoccerPreviewData.soccerRecentFormHeaderModel.secondTeamRecentForms,
        isReverse = false,
        showRecentForm = true
    )
}