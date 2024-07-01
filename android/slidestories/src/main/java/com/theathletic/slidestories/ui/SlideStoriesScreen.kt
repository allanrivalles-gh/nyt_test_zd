package com.theathletic.slidestories.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.slidestories.R
import com.theathletic.slidestories.ui.slidecomponents.ImageSlide
import com.theathletic.slidestories.ui.slidecomponents.QuoteSlide
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.ui.utility.conditional
import com.theathletic.ui.widgets.ResourceIcon
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlideStoriesScreen(
    slides: List<SlideStoriesUiModel.Slide>,
    slideProgress: List<Float>,
    currentSlideIndex: Int,
    onClose: () -> Unit,
    onGesture: (RawGestureEvent) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })

    LaunchedEffect(currentSlideIndex) { pagerState.animateScrollToPage(currentSlideIndex) }

    Box {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
        ) { page ->
            when (val slide = slides[page]) {
                // todo: Added support for the 3 current defined Slides types and remove the TestSlide
                is SlideStoriesUiModel.TestSlide -> TestSlide(slide = slide)
                is SlideStoriesUiModel.QuoteSlide -> QuoteSlide(
                    quote = slide.quote,
                    attributor = slide.attributor,
                    attributorRole = slide.attributorRole,
                    slideCardBlocks = slide.slideCardBlocks
                )
                is SlideStoriesUiModel.ImageSlide -> ImageSlide(
                    urlImage = slide.imageUrl,
                    credit = slide.credit,
                    slideCardBlocks = slide.slideCardBlocks
                )
                else -> Timber.e("Slide $slide not supported")
            }
        }
        GestureController(onGesture = onGesture)
        SlideStoriesTopper(
            modifier = Modifier.align(Alignment.TopStart),
            slideProgress = slideProgress,
            onClose = onClose
        )
    }
}

@Composable
private fun TestSlide(slide: SlideStoriesUiModel.TestSlide) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AthColor.Gray700)
    ) {
        Text(
            text = slide.title,
            color = AthColor.Gray200,
            style = AthTextStyle.Calibre.Headline.Medium.Small,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun GestureController(
    onGesture: (RawGestureEvent) -> Unit,
    showZones: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.2f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { onGesture(RawGestureEvent.RAW_PRESS) },
                        onTap = { onGesture(RawGestureEvent.RAW_TAP_LEFT) }
                    )
                }
                .conditional(condition = showZones) { background(Color(0x2000FFFF)) }
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.6f)
                // todo (Mark): In future PR add gesture handling for press to pause and down swipe to close
                .conditional(condition = showZones) { background(Color(0x20FF0000)) }
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.2f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { onGesture(RawGestureEvent.RAW_PRESS) },
                        onTap = { onGesture(RawGestureEvent.RAW_TAP_RIGHT) }
                    )
                }
                .conditional(condition = showZones) { background(Color(0x2000FFFF)) }
        )
    }
}

@Composable
private fun SlideStoriesTopper(
    modifier: Modifier = Modifier,
    slideProgress: List<Float>,
    onClose: () -> Unit,
    isQuotesTopper: Boolean = false
) {
    val topperAlpha = if (isQuotesTopper) 0.3f else 1.0f
    Box(
        modifier = modifier
            .height(134.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(AthColor.Gray200.copy(alpha = topperAlpha), Color.Transparent),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY),
                    tileMode = TileMode.Clamp
                )
            )
    ) {
        CloseStoriesButton(
            modifier = Modifier
                .padding(top = 54.dp, end = 8.dp)
                .align(Alignment.TopEnd),
            onClose = onClose
        )
        ProgressBar(
            slideProgress = slideProgress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 16.dp, top = 40.dp)
                .align(Alignment.TopStart)
        )
    }
}

@Composable
private fun CloseStoriesButton(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    ResourceIcon(
        resourceId = R.drawable.ic_close,
        tint = AthColor.Gray800,
        modifier = modifier
            .size(34.dp)
            .clip(CircleShape)
            .clickable { onClose() }
            .padding(4.dp)
    )
}

@Composable
private fun ProgressBar(
    slideProgress: List<Float>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        slideProgress.forEach { progress ->
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(durationMillis = 100, easing = LinearEasing), label = "progress"
            )
            LinearProgressIndicator(
                progress = animatedProgress,
                color = AthColor.Gray800,
                backgroundColor = AthColor.Gray800.copy(alpha = 0.35f),
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .height(3.dp)
                    .weight(1f)
            )
        }
    }
}

@Composable
@Preview
private fun ProgressBar_Preview() {
    ProgressBar(
        slideProgress = listOf(1f, 1f, 0.8f, 0f, 0f)
    )
}