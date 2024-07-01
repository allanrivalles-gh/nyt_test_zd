package com.theathletic.rooms.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTheme
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

@Composable
fun SpeakingIndicator(
    currentVolume: Float,
    size: Dp = 100.dp,
) {
    val animator = remember { Animatable(0.0f) }
    val latestValue = animator.value

    LaunchedEffect(currentVolume) {
        // Amplify volume so we see more of the speaking indicator per volume level
        val amplified = min(currentVolume * 1.5f, 1f)

        animator.apply {
            snapTo(max(latestValue, amplified))
            animateTo(
                0.0f,
                animationSpec = tween(durationMillis = 500, delayMillis = 100)
            )
        }
    }

    val strokeColor = AthTheme.colors.dark300
    val fillColor = AthTheme.colors.dark200

    Canvas(modifier = Modifier.size(size)) {
        val innerRadius = size.toPx() * .47f
        val outerRadius = size.toPx() / 2
        val maxIndicatorSize = size.toPx() * .08f

        drawCircle(
            color = strokeColor,
            radius = outerRadius,
        )

        val currentValue = animator.value
        if (currentValue > 0f) {
            drawCircle(
                brush = LiveRoomBubbleGradient,
                radius = innerRadius + (maxIndicatorSize * currentValue),
            )
        }

        drawCircle(
            color = fillColor,
            radius = innerRadius,
        )
    }
}

@Composable
@Preview
fun SpeakingIndicator_Preview() {
    var volume by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(400)
            volume = Random.nextFloat()
        }
    }
    SpeakingIndicator(volume)
}