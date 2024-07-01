package com.theathletic.boxscore.ui.playergrades

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.asString

@Composable
fun AverageAndTotalGradeIndicator(
    averageGrade: String,
    hasGrades: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .then(modifier),
        horizontalAlignment = Alignment.End
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                Icons.Default.Grade,
                contentDescription = null,
                tint = AthTheme.colors.yellow,
                modifier = Modifier.size(16.dp)
            )

            val (averageDisplay, averageColor) = averageGrade.formatAverageGrade(hasGrades)
            Text(
                text = averageDisplay,
                style = AthTextStyle.Calibre.Headline.SemiBold.Medium,
                color = averageColor,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}

@Composable
fun String.formatAverageGrade(hasGrades: Boolean) =
    if (hasGrades.not()) {
        Pair(stringResource(id = R.string.player_grade_not_applicable), AthTheme.colors.dark500)
    } else {
        Pair(this, AthTheme.colors.dark800)
    }

@Composable
fun GradeIndicator(
    awardedGrade: Int,
    isGraded: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isGraded) {
                StringWithParams(R.string.player_grade_your_grades, awardedGrade)
            } else {
                StringWithParams(R.string.player_grade_not_graded)
            }.asString(),
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            modifier = Modifier.padding(end = 6.dp)
        )
        if (isGraded) {
            GradeBar(grading = awardedGrade)
        }
    }
}

@Composable
private fun GradeBar(grading: Int) {
    Row {
        repeat(grading) {
            Icon(
                Icons.Default.Grade,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = AthTheme.colors.dark800
            )
        }
        repeat(5 - grading) {
            Icon(
                Icons.Outlined.Grade,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = AthTheme.colors.dark800
            )
        }
    }
}

@Preview
@Composable
private fun AverageAndTotalGradeIndicator_Preview() {
    AverageAndTotalGradeIndicator("5.0", true)
}

@Preview
@Composable
private fun AverageAndTotalGradeIndicatorNotLocked_Preview() {
    AverageAndTotalGradeIndicator("0.0", false)
}

@Preview
@Composable
private fun GradeIndicator_Preview() {
    GradeIndicator(
        3,
        true
    )
}