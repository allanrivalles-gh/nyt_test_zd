package com.theathletic.boxscore.ui.playbyplay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.components.AnimateBaseballBase
import com.theathletic.themes.AthleticTheme

@Composable
internal fun BaseballOccupiedBases(
    occupiedBases: List<Int>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(
            width = 26.dp,
            height = 20.dp
        )
    ) {
        AnimateBaseballBase(
            occupied = occupiedBases.isNotEmpty() && occupiedBases.contains(3),
            size = 12.dp,
            modifier = Modifier.align(Alignment.BottomStart)
        )
        AnimateBaseballBase(
            occupied = occupiedBases.isNotEmpty() && occupiedBases.contains(2),
            size = 12.dp,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        AnimateBaseballBase(
            occupied = occupiedBases.isNotEmpty() && occupiedBases.contains(1),
            size = 12.dp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Preview
@Composable
fun BaseballOccupiedBasesPreview() {
    BaseballOccupiedBases(
        occupiedBases = listOf(2, 3)
    )
}

@Preview
@Composable
fun BaseballOccupiedBasesPreview_Light() {
    AthleticTheme(lightMode = true) {
        BaseballOccupiedBases(
            occupiedBases = listOf(1, 3)
        )
    }
}

@Preview
@Composable
fun BaseballOccupiedBasesPreview_Empty() {
    BaseballOccupiedBases(
        occupiedBases = emptyList()
    )
}