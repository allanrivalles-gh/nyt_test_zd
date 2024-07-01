package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.playbyplay.BaseballPitchPlay
import com.theathletic.feed.ui.FeedModule
import com.theathletic.themes.AthTheme

data class BaseballPitchPlayModule(
    val id: String,
    val title: String,
    val description: String,
    val pitchNumber: Int,
    val pitchOutcomeColor: Color,
    val occupiedBases: List<Int>,
    val hitZone: Int?,
    val pitchZone: Int
) : FeedModule {

    @Composable
    override fun Render() {
        IndentedBaseballPitchPlay(
            title = title,
            description = description,
            pitchNumber = pitchNumber,
            pitchOutcomeColor = pitchOutcomeColor,
            occupiedBases = occupiedBases,
            hitZone = hitZone,
            pitchZone = pitchZone
        )
    }
}

@Composable
private fun IndentedBaseballPitchPlay(
    title: String,
    description: String,
    pitchNumber: Int,
    pitchOutcomeColor: Color,
    occupiedBases: List<Int>,
    hitZone: Int?,
    pitchZone: Int
) {
    Row(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.size(width = 40.dp, height = 10.dp))
        BaseballPitchPlay(
            title = title,
            description = description,
            pitchNumber = pitchNumber,
            pitchOutcomeColor = pitchOutcomeColor,
            occupiedBases = occupiedBases,
            hitZone = hitZone,
            pitchZone = pitchZone
        )
    }
}

@Preview
@Composable
fun BaseballPitchPlayModulePreview() {
    BaseballPitchPlayModule(
        id = "uniqueId",
        title = "Strike Looking",
        description = "92mph slider",
        pitchNumber = 3,
        pitchOutcomeColor = AthTheme.colors.green,
        occupiedBases = listOf(2),
        hitZone = 16,
        pitchZone = 5
    ).Render()
}