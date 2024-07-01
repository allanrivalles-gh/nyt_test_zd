package com.theathletic.brackets.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.theathletic.brackets.ui.BracketsUi
import com.theathletic.brackets.ui.BracketsUi.Companion.ANIMATION_OFFSET
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import kotlin.math.absoluteValue

private val roundPadding = 16.dp

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RoundsPage(
    pagerState: PagerState,
    round: BracketsUi.Round,
    currentIndex: Int,
    matchHalfHeight: Dp,
    labelHalfHeight: Dp,
    label: @Composable (String) -> Unit,
    matchLayout: @Composable (BracketsUi.Match) -> Unit,
) {

    Column(modifier = Modifier.fillMaxSize()) {
        when (round) {
            is BracketsUi.Round.Pre ->
                InitialRoundPage(
                    groups = round.groups,
                    label = label,
                    matchLayout = matchLayout
                )
            is BracketsUi.Round.Initial ->
                InitialRoundPage(
                    groups = round.groups,
                    label = label,
                    matchLayout = matchLayout
                )
            is BracketsUi.Round.SemiFinal ->
                SemiFinalRoundPage(
                    pagerState = pagerState,
                    groups = round.groups,
                    currentIndex = currentIndex,
                    spacerHeight = matchHalfHeight + labelHalfHeight,
                    labelSpacerHeight = labelHalfHeight * 2,
                    matchLayout = matchLayout
                )
            is BracketsUi.Round.Final ->
                StandardRoundPage(
                    pagerState = pagerState,
                    groups = round.groups,
                    currentIndex = currentIndex,
                    spacerHeight = matchHalfHeight,
                    labelHalfHeight = labelHalfHeight,
                    label = { text -> LabelText(text = text, modifier = Modifier.alpha(0f)) },
                    matchLayout = matchLayout
                )
            else ->
                StandardRoundPage(
                    pagerState = pagerState,
                    groups = round.groups,
                    currentIndex = currentIndex,
                    spacerHeight = matchHalfHeight,
                    labelHalfHeight = labelHalfHeight,
                    label = label,
                    matchLayout = matchLayout
                )
        }
    }
}

@Composable
private fun InitialRoundPage(
    groups: List<BracketsUi.Group>,
    label: @Composable (String) -> Unit,
    matchLayout: @Composable (BracketsUi.Match) -> Unit,
) {
    groups.forEach { group ->
        Column(modifier = Modifier.fillMaxWidth()) {
            group.matches.forEachIndexed { index, match ->
                if (index == 0) {
                    label(group.label)
                }
                matchLayout(match)
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun StandardRoundPage(
    pagerState: PagerState,
    groups: List<BracketsUi.Group>,
    currentIndex: Int,
    spacerHeight: Dp,
    labelHalfHeight: Dp,
    label: @Composable (String) -> Unit,
    matchLayout: @Composable (BracketsUi.Match) -> Unit,
) {
    val spacerHeightAnim: Dp by spacerHeightAnim(
        pagerState = pagerState,
        currentIndex = currentIndex,
        height = spacerHeight
    )
    val offsetHeight = spacerHeight + (labelHalfHeight * 2)
    val initialOffset = offsetHeight + roundPadding + 6.dp

    groups.forEach { group ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawConnectors(
                    verticalConnectorHeight = spacerHeight * 2,
                    initialOffset = initialOffset,
                    paddingOffset = roundPadding,
                    numberOfConnectors = group.matches.size,
                    shouldShow = pagerState.shouldShowConnector(currentIndex),
                    connectorColor = BracketsUi.connectorColor
                )
                .animateContentSize()
        ) {
            group.matches.forEachIndexed { index, match ->

                Spacer(modifier = Modifier.height(spacerHeightAnim))

                if (index == 0) {
                    label(group.label)
                }

                matchLayout(match)

                Spacer(modifier = Modifier.height(spacerHeightAnim))
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SemiFinalRoundPage(
    pagerState: PagerState,
    groups: List<BracketsUi.Group>,
    currentIndex: Int,
    spacerHeight: Dp,
    labelSpacerHeight: Dp,
    matchLayout: @Composable (BracketsUi.Match) -> Unit,
) {
    val spacerHeightAnim: Dp by spacerHeightAnim(
        pagerState = pagerState,
        currentIndex = currentIndex,
        height = spacerHeight
    )
    val labelSpacerHeightAnim: Dp by spacerHeightAnim(
        pagerState = pagerState,
        currentIndex = currentIndex,
        height = labelSpacerHeight
    )
    val verticalConnectorHeight = (spacerHeight) + (labelSpacerHeight * 2) + roundPadding

    groups.forEach { group ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawConnectors(
                    verticalConnectorHeight = verticalConnectorHeight,
                    initialOffset = spacerHeight + labelSpacerHeight,
                    paddingOffset = roundPadding,
                    numberOfConnectors = group.matches.size,
                    shouldShow = pagerState.shouldShowConnector(currentIndex),
                    connectorColor = BracketsUi.connectorColor
                )
                .animateContentSize()
        ) {
            group.matches.forEachIndexed { matchIndex, match ->

                Spacer(modifier = Modifier.height(spacerHeightAnim))

                if (matchIndex == 0) {
                    Spacer(modifier = Modifier.height(labelSpacerHeight))
                } else {
                    Spacer(modifier = Modifier.height(labelSpacerHeightAnim))
                }

                matchLayout(match)

                Spacer(modifier = Modifier.height(spacerHeightAnim))
            }
        }
    }
}

@Composable
fun LabelText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = AthTextStyle.Slab.Bold.Small.copy(color = AthTheme.colors.dark800),
        modifier = modifier.padding(start = 16.dp, top = 16.dp)
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun spacerHeightAnim(pagerState: PagerState, currentIndex: Int, height: Dp) = animateDpAsState(
    targetValue = when {
        pagerState.currentPage > currentIndex -> 0.dp
        pagerState.currentPage == currentIndex -> {
            if (pagerState.currentPageOffset.absoluteValue > ANIMATION_OFFSET) {
                height
            } else {
                0.dp
            }
        }
        pagerState.targetPage == currentIndex -> {
            if (pagerState.currentPageOffset > ANIMATION_OFFSET) {
                0.dp
            } else {
                height
            }
        }
        else -> height
    },
    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
)